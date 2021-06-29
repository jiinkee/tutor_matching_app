package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.viewAdapter.AdvancedBidOfferListAdapter;
import com.edu.monash.fit3077.viewModel.BidDetailsViewModel;

import java.util.ArrayList;

/**
 * This class represents a bid offer list which contains a SELECT button and a CHAT button beside each of the bid offer item.
 * The bid offer list is displayed under the BIDDERS tab on the bid request details page.
 * This class passes in callbacks to the RecyclerView adapter so that actions can be triggered when the SELECT/CHAT button is clicked.
 * This type of bid offer list is used for the bid initiator (student) and when the selected bid request is a CLOSE bid.
 */
public class AdvancedBidOfferListFragment extends BidOfferListFragmentFactory implements BidOfferSelectWinnerClickListener, BidOfferChatButtonClickListener {

    private AdvancedBidOfferListAdapter advancedAdapter;
    private BidDetailsViewModel bidDetailsViewModel;

    public AdvancedBidOfferListFragment(BidRequest bidRequest, ArrayList<BidOffer> bidOffers) {
        super(bidRequest, bidOffers);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = super.onCreateView(inflater, container, savedInstanceState);
        advancedAdapter = new AdvancedBidOfferListAdapter( this, this);
        super.initializeBidOfferRecyclerView(advancedAdapter);

        bidDetailsViewModel = new ViewModelProvider(requireActivity()).get(BidDetailsViewModel.class);
        return fragment;
    }

    @Override
    protected void setBidOffersRecyclerViewData(ArrayList<BidOffer> offers, String bidSubjectId) {
        // set the updated/latest bid offers to the RecyclerView
        // the changes of bid offers is being observed in the parent class: BidOfferListFragmentFactory
        advancedAdapter.setBidOffersData(offers, bidSubjectId);
        advancedAdapter.setSelectedBidRequest(super.selectedBidRequest);
    }

    @Override
    public void onChatButtonClicked(String bidRequestID, Student student, Tutor tutor) {
        ArrayList<User> chatParticipants = new ArrayList<>();
        chatParticipants.add(student);
        chatParticipants.add(tutor);
        Intent chatRoomIntent = new Intent(requireActivity(), ChatActivity.class);
        chatRoomIntent.putExtra(ChatActivity.CHAT_BID_REQUEST, selectedBidRequest.getId());
        chatRoomIntent.putExtra(ChatActivity.CHAT_PARTICIPANTS, chatParticipants);
        startActivity(chatRoomIntent);
    }

    @Override
    public void onSelectWinnerBidButtonClicked(BidOffer winnerBidOffer) {
        // updates an observable live data in the bid view model so that the program can know that user has chosen a winner bid offer
        bidDetailsViewModel.userCloseDownBidRequest(selectedBidRequest, winnerBidOffer);
    }
}
