package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.viewAdapter.SelectableBidOfferListAdapter;
import com.edu.monash.fit3077.viewModel.BidDetailsViewModel;

import java.util.ArrayList;

/**
 * This class represents a bid offer list which contains a SELECT button beside each of the bid offer item.
 * The bid offer list is displayed under the BIDDERS tab on the bid request details page.
 * This class passes in a callback to the RecyclerView adapter so that actions can be triggered when the SELECT button is clicked.
 * This type of bid offer list is used for the bid initiator (student) and when the selected bid request is an OPEN bid.
 */
public class SelectableBidOfferListFragment extends BidOfferListFragmentFactory implements BidOfferSelectWinnerClickListener {
    private BidDetailsViewModel bidDetailsViewModel;
    private SelectableBidOfferListAdapter selectableAdapter;

    public SelectableBidOfferListFragment(BidRequest bidRequest, ArrayList<BidOffer> bidOffers) {
        super(bidRequest, bidOffers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragment = super.onCreateView(inflater, container, savedInstanceState);
        selectableAdapter = new SelectableBidOfferListAdapter(this);
        super.initializeBidOfferRecyclerView(selectableAdapter);

        bidDetailsViewModel = new ViewModelProvider(requireActivity()).get(BidDetailsViewModel.class);
        return fragment;
    }

    @Override
    public void onSelectWinnerBidButtonClicked(BidOffer winnerBidOffer) {
        // updates an observable live data in the bid view model so that the program can know that user has chosen a winner bid offer
        bidDetailsViewModel.userCloseDownBidRequest(selectedBidRequest, winnerBidOffer);
    }

    @Override
    protected void setBidOffersRecyclerViewData(ArrayList<BidOffer> offers, String bidSubjectId) {
        // set the updated/latest bid offers to the RecyclerView
        // the changes of bid offers is being observed in the parent class: BidOfferListFragmentFactory
        selectableAdapter.setBidOffersData(offers, bidSubjectId);
    }
}
