package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.viewAdapter.BasicBidOfferListAdapter;

import java.util.ArrayList;

/**
 * This class represents the most basic version of bid offer list that will be displayed under the BIDDERS tab on the bid request details page.
 * In the basic bid offer list, the user can only see a list of bid offers.
 * This kind of bid offer list is being used for the bidders (tutors) and all the closed down bid requests.
 */
public class BasicBidOfferListFragment extends BidOfferListFragmentFactory {

    private BasicBidOfferListAdapter basicAdapter;

    public BasicBidOfferListFragment(BidRequest bidRequest, ArrayList<BidOffer> bidOffers) {
        super(bidRequest, bidOffers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = super.onCreateView(inflater, container, savedInstanceState);
        basicAdapter = new BasicBidOfferListAdapter();
        super.initializeBidOfferRecyclerView(basicAdapter);

        return fragment;
    }

    @Override
    protected void setBidOffersRecyclerViewData(ArrayList<BidOffer> offers, String bidSubjectId) {
        // set the updated/latest bid offers to the RecyclerView
        basicAdapter.setBidOffersData(offers, bidSubjectId);
    }
}
