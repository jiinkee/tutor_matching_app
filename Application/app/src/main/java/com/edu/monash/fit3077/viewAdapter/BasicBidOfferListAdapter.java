package com.edu.monash.fit3077.viewAdapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

// the most normal Bid Offer view, no button, no nothing
public class BasicBidOfferListAdapter extends BaseBidOfferListAdapter{

    public BasicBidOfferListAdapter() {
        super();
    }

    @NonNull
    @Override
    public BaseBidOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = super.inflateBidOfferItemLayout(parent, viewType);
        return new BasicBidOfferViewHolder(view);
    }

    public class BasicBidOfferViewHolder extends BaseBidOfferListAdapter.BaseBidOfferViewHolder{

        BasicBidOfferViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
