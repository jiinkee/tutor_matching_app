package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.viewAdapter.BidListAdapter;
import com.edu.monash.fit3077.viewModel.BidListViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * This class is an inline class for all types of bid list fragment.
 * It passes in a callback into the RecyclerView (bid list) adapter so that the program can be notified of the bid request chosen by the user.
 * It also applies the observer pattern to always render the latest/updated bid requests on UI.
 */
public class BidListFragment extends Fragment implements RecyclerViewItemClickListener<BidRequest> {

    private String bidTab;
    private View fragment;
    private RecyclerView recyclerView;
    private BidListAdapter bidListAdapter;
    private BidListViewModel bidListViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    public BidListFragment(String selectedBidTab) {
        this.bidTab = selectedBidTab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment =  inflater.inflate(R.layout.fragment_bid_request_list, container, false);

        ConstraintLayout layout = fragment.findViewById(R.id.bidRequestListFragment);

        // setup swipe to refresh layout
        setupSwipeToRefreshLayout();

        // retrieve bid view model
        bidListViewModel = new ViewModelProvider(requireActivity()).get(BidListViewModel.class);

        // initialize bid request list adapter
        recyclerView = fragment.findViewById(R.id.mBidListRecyclerView);
        bidListAdapter = new BidListAdapter(getContext(), this);
        recyclerView.setAdapter(bidListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));

        // observe bid requests list data
        bidListViewModel.getBidRequestsResponse().observe(requireActivity(), response -> {
            if (response != null) {
                switch (response.status) {
                    case SUCCESS:
                        bidListAdapter.setBidListData(response.data);
                        break;
                    case ERROR:
                        if (isAdded()) Snackbar.make(getActivity().findViewById(android.R.id.content), response.errorMsg, Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return fragment;
    }

    @Override
    public void onResume() {
        // get/refresh the list of bid requests when user visits the bid list page
        getBids();
        super.onResume();
    }

    private void setupSwipeToRefreshLayout() {
        swipeRefreshLayout = fragment.findViewById(R.id.bidListSwipeRefreshLayout);

        // user can choose to refresh the bid request list manually as well
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getBids();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void getBids() {
        if (bidTab.equals(getString(R.string.bid_open))) {
            bidListViewModel.getOpenBidRequests();
        } else if (bidTab.equals(getString(R.string.bid_close))) {
            bidListViewModel.getCloseBidRequests();
        } else if (bidTab.equals(getString(R.string.bid_closed_down))) {
            bidListViewModel.getClosedDownBidRequests();
        } else if (bidTab.equals(getString(R.string.bid_subscribed))) {
            bidListViewModel.getSubscribedOpenBidRequests();
        }
    }

    @Override
    public void onItemClicked(int position, BidRequest item) {
        Intent bidDetailsPageIntent;

        if (bidListViewModel.getLoggedInUser().getRolePermissions().contains(UserRole.BIDDER)) {

            if (bidTab.equals(getString(R.string.bid_subscribed))) {
                bidDetailsPageIntent = new Intent(requireActivity(), BidderViewSubscribedBidDetailsActivity.class);
            } else {
                bidDetailsPageIntent = new Intent(requireActivity(), BidderViewBidDetailsActivity.class);
            }

        } else {
            bidDetailsPageIntent = new Intent(requireActivity(), BidInitiatorViewBidDetailsActivity.class);
        }
        bidDetailsPageIntent.putExtra(BidDetailsActivity.SELECTED_BID_REQUEST_ID, item.getId());
        startActivity(bidDetailsPageIntent);
    }

}