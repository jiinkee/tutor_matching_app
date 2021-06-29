package com.edu.monash.fit3077.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.service.MyResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Despite tab management as shown in its parent class, this class also manages the function buttons used by a bidder (tutor)
 * i.e. Buy Out, Make Offer, Edit Offer, Chat & Subscribe buttons on the bidder's (tutor's) view of bid details page
 */
public class BidderViewBidDetailsActivity extends BidDetailsActivity {
    Context ctx;
    Button chatBtn;
    Button createOfferBtn;
    Button editOfferBtn;
    Button buyOutBtn;
    ImageButton subscribeBtn;
    boolean isSubscribed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;

        FrameLayout buttonFrame = findViewById(R.id.bidDetailsButtonFragmentContainer);
        buttonFrame.setVisibility(View.VISIBLE);

        chatBtn = buttonFrame.findViewById(R.id.btnChatWithStudent);
        createOfferBtn = buttonFrame.findViewById(R.id.btnOffer);
        editOfferBtn = buttonFrame.findViewById(R.id.btnEditOffer);
        buyOutBtn = buttonFrame.findViewById(R.id.btnBuyOut);
        subscribeBtn = findViewById(R.id.btnSubscribe);

    }

    @Override
    protected void updateButtonState() {
        // show subscribe button if bid is OPEN bid
        if (super.selectedBidRequest.getType() == BidRequestType.OPEN) {
            subscribeBtn.setVisibility(View.VISIBLE);

            // initialize subscribe button state based on the logged in tutor's subscription
            isSubscribed = bidDetailsViewModel.hasSubscribedToBid(super.selectedBidRequest.getId());
            if (isSubscribed) {
                subscribeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
            }

            // add subscribe button click listener
            subscribeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSubscribed = !isSubscribed;
                    if (isSubscribed) {
                        subscribeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
                        subscribeToSelectedBidRequest();
                    } else {
                        subscribeBtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));
                        unsubscribeBidRequest();
                    }
                }
            });
        }

        // depending on the bid offer that the logged in bidder has created, render different buttons in button frame
        if (super.loggedInUserBidOffer == null) {
            // to make sure editOfferBtn is hidden when offerBtn is visible
            editOfferBtn.setVisibility(View.GONE);
            createOfferBtn.setVisibility(View.VISIBLE);
            createOfferBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makeBidOffer();
                }
            });
        } else {
            // to make sure offerBtn is hidden when editOfferBtn is visible
            createOfferBtn.setVisibility(View.GONE);
            editOfferBtn.setVisibility(View.VISIBLE);
            editOfferBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editBidOffer();
                }
            });
        }

        // Bidder will only see ALIVE bid requests (i.e. Open & Close bid requests)
        if (super.selectedBidRequest.getType() == BidRequestType.OPEN) {
            // Open type bid request, tutor can either buy out or make offer
            buyOutBtn.setVisibility(View.VISIBLE);
            buyOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buyOutBidRequest();
                }
            });

        } else if (super.selectedBidRequest.getType() == BidRequestType.CLOSE) {
            // Close type bid request, bidder can make offer and chat with the bid initiator
            chatBtn.setVisibility(View.VISIBLE);
            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<User> chatParticipants = new ArrayList<>();
                    chatParticipants.add(selectedBidRequest.getInitiator());
                    chatParticipants.add(bidDetailsViewModel.getLoggedInUser());
                    Intent chatRoomIntent = new Intent(ctx, ChatActivity.class);
                    chatRoomIntent.putExtra(ChatActivity.CHAT_BID_REQUEST, selectedBidRequest.getId());
                    chatRoomIntent.putExtra(ChatActivity.CHAT_PARTICIPANTS, chatParticipants);
                    startActivity(chatRoomIntent);
                }
            });

            // disable chat button if bidder has not made any bid offer
            if (super.loggedInUserBidOffer == null) {
                chatBtn.setEnabled(false);
            } else {
                chatBtn.setEnabled(true);
            }
        }

    }

    /**
     * When bidder (tutor) chooses to make a bid offer, system will check whether the tutor's competency level matches with student's requirement.
     * If requirements are satisfied, system will redirect user to a bid offer creation page
     */
    private void makeBidOffer() {
        if (bidDetailsViewModel.checkCanMakeBidOffer(super.selectedBidRequest)) {
            Intent mBidOfferCreateFormPageIntent = new Intent(this, BidOfferCreateFormActivity.class);
            mBidOfferCreateFormPageIntent.putExtra(BidOfferCreateFormActivity.BID_REQUEST_INFO, selectedBidRequest);
            startActivity(mBidOfferCreateFormPageIntent);
        } else {
            Snackbar.make(super.layout, "You are not eligible to make bid offer", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * When bidder (tutor) chooses to edit a bid offer, system will redirect user to a bid offer editing page
     */
    private void editBidOffer() {
        Intent mBidOfferEditFormPageIntent = new Intent(this, BidOfferEditFormActivity.class);
        mBidOfferEditFormPageIntent.putExtra(BidOfferEditFormActivity.BID_REQUEST_INFO, super.selectedBidRequest);
        mBidOfferEditFormPageIntent.putExtra(BidOfferEditFormActivity.BID_OFFER_INFO, super.loggedInUserBidOffer);
        startActivity(mBidOfferEditFormPageIntent);
    }

    /**
     * When bidder (tutor) chooses to buy out an open bid, system will check whether the tutor's competency level matches with student's requirement.
     * If requirements are satisfied, system will auto close down the bid and create a new contract based on the bid request details
     */
    private void buyOutBidRequest() {
        if (bidDetailsViewModel.checkCanMakeBidOffer(super.selectedBidRequest)) {
            bidDetailsViewModel.buyOutBidRequest(selectedBidRequest).observe(this, buyOutBidResponse -> {
                if (buyOutBidResponse.status == MyResponse.ResponseStatus.ERROR) {
                    Snackbar.make(super.layout, buyOutBidResponse.errorMsg, Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            Snackbar.make(super.layout, "You are not eligible to buy out bid", Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Functions that allow tutor to subscribe/unsubscribe to this bid request (if the selected bid is an open bid)
     */
    private void subscribeToSelectedBidRequest() {
        bidDetailsViewModel.subscribeToBidRequest(super.selectedBidRequest.getId()).observe(this, subscriptionResponse -> {
            switch (subscriptionResponse.status) {
                case SUCCESS:
                    Snackbar.make(super.layout, "Bid subscribed successfully!", Snackbar.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Snackbar.make(super.layout, subscriptionResponse.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void unsubscribeBidRequest() {
        bidDetailsViewModel.unsubscribeBidRequest(super.selectedBidRequest.getId()).observe(this, subscriptionResponse -> {
            switch (subscriptionResponse.status) {
                case SUCCESS:
                    Snackbar.make(super.layout, "Bid unsubscribed successfully!", Snackbar.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Snackbar.make(super.layout, subscriptionResponse.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }


}
