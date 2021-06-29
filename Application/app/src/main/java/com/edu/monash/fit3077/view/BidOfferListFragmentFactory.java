package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.viewAdapter.BaseBidOfferListAdapter;

import java.util.ArrayList;

/**
 * This class represents a factory class for different types of the bid offer list that user will see.
 * The bid offer list appears under the BIDDERS tab on the bid request details page.
 * Observer pattern is applied in this class to always display the most updated bid offers made to the selected bid request.
 */
public abstract class BidOfferListFragmentFactory extends Fragment {

    private View fragment;
    protected RecyclerView bidOffersRecyclerView;
    protected ArrayList<BidOffer> bidOffersToBeDisplayed;
    protected BidRequest selectedBidRequest;

    public BidOfferListFragmentFactory(BidRequest bidRequest, ArrayList<BidOffer> bidOffers) {
        selectedBidRequest = bidRequest;
        bidOffersToBeDisplayed = bidOffers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment =  inflater.inflate(R.layout.fragment_bid_offer_list, container, false);
        return fragment;
    }

    protected void initializeBidOfferRecyclerView(BaseBidOfferListAdapter adapter) {
        bidOffersRecyclerView = fragment.findViewById(R.id.recyclerViewBidResponse);
        bidOffersRecyclerView.setAdapter(adapter);
        bidOffersRecyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        setBidOffersRecyclerViewData(bidOffersToBeDisplayed, selectedBidRequest.getBidSubjectId());
    }

    protected abstract void setBidOffersRecyclerViewData(ArrayList<BidOffer> offers, String bidSubjectId);
}