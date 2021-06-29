package com.edu.monash.fit3077.viewAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.view.BidOfferChatButtonClickListener;
import com.edu.monash.fit3077.view.BidOfferSelectWinnerClickListener;

public class AdvancedBidOfferListAdapter extends BaseBidOfferListAdapter{

    private BidRequest selectedBidRequest;
    private BidOfferSelectWinnerClickListener selectWinnerClickListener;
    private BidOfferChatButtonClickListener chatButtonClickListener;

    public AdvancedBidOfferListAdapter(BidOfferSelectWinnerClickListener selectWinnerListener, BidOfferChatButtonClickListener chatListener) {
        super();
        this.selectWinnerClickListener = selectWinnerListener;
        this.chatButtonClickListener = chatListener;
    }

    public void setSelectedBidRequest(BidRequest bid) {
        selectedBidRequest = bid;
    }

    @NonNull
    @Override
    public AdvancedBidOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = super.inflateBidOfferItemLayout(parent, viewType);
        return new AdvancedBidOfferViewHolder(view);
    }

    public class AdvancedBidOfferViewHolder extends BaseBidOfferViewHolder{
        private Button selectWinnerBtn, chatBtn;
        AdvancedBidOfferViewHolder(@NonNull View itemView) {
            super(itemView);

            selectWinnerBtn = itemView.findViewById(R.id.btnSelectBidWinner);
            selectWinnerBtn.setVisibility(View.VISIBLE);

            chatBtn = itemView.findViewById(R.id.btnChatWithTutor);
            chatBtn.setVisibility(View.VISIBLE);

            selectWinnerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectWinnerClickListener.onSelectWinnerBidButtonClicked(bidOffers.get(getAdapterPosition()));
                }
            });

            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chatButtonClickListener.onChatButtonClicked(selectedBidRequest.getId(), selectedBidRequest.getInitiator(),
                            bidOffers.get(getAdapterPosition()).getBidder());
                }
            });
        }
    }
}
