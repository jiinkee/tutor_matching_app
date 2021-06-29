package com.edu.monash.fit3077.viewAdapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.view.BidOfferSelectWinnerClickListener;


public class SelectableBidOfferListAdapter extends BaseBidOfferListAdapter{

    private BidOfferSelectWinnerClickListener listener;

    public SelectableBidOfferListAdapter(BidOfferSelectWinnerClickListener listener) {
        super();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseBidOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = super.inflateBidOfferItemLayout(parent, viewType);
        return new SelectableBidOfferViewHolder(view);
    }

    public class SelectableBidOfferViewHolder extends BaseBidOfferViewHolder{
        private Button selectWinnerBtn;
        SelectableBidOfferViewHolder(@NonNull View itemView) {
            super(itemView);

            selectWinnerBtn = itemView.findViewById(R.id.btnSelectBidWinner);
            selectWinnerBtn.setVisibility(View.VISIBLE);

            selectWinnerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSelectWinnerBidButtonClicked(bidOffers.get(getAdapterPosition()));
                }
            });
        }
    }
}
