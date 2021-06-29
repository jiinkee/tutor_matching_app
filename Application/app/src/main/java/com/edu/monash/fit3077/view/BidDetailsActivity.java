package com.edu.monash.fit3077.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.viewModel.BidDetailsViewModel;
import com.edu.monash.fit3077.viewModel.BidTimerViewModel;
import com.edu.monash.fit3077.viewModel.BidViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * This class manages the tab inside the bid request details page, i.e. the DETAILS tab and OFFERS tab
 * This class will swap out the fragment according to the tab selected by the user
 * This class also helps monitors a timer which count-down to the bid time-out
 */
public abstract class BidDetailsActivity extends AppCompatActivity {

    private BidTimerViewModel timerViewModel;
    private TabLayout tabLayout;
    private int selectedTabPosition = 0;
    private FragmentManager fm;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedBidRequestId;

    protected ConstraintLayout layout;
    protected BidDetailsViewModel bidDetailsViewModel;
    protected BidRequest selectedBidRequest;
    protected ArrayList<BidOffer> bidOffersToBeDisplayed;
    protected BidOffer loggedInUserBidOffer;

    final static String SELECTED_BID_REQUEST_ID = "selected_bid_request";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_request_details);

        // retrieve bid view model and timer view model
        bidDetailsViewModel = new ViewModelProvider(this).get(BidDetailsViewModel.class);
        timerViewModel = new ViewModelProvider(this).get(BidTimerViewModel.class);

        fm = getSupportFragmentManager();
        layout = findViewById(R.id.bidRequestDetailsPage);

        // get the ID of selected bid request from intent
        selectedBidRequestId = getIntent().getStringExtra(SELECTED_BID_REQUEST_ID);

        // set up swipe to refresh feature
        setupSwipeToRefreshLayout();

        // observe the selected bid request object
        bidDetailsViewModel.getSelectedBidRequest().observe(this, bidRequest -> {
            if (bidRequest != null) {
                selectedBidRequest = bidRequest;
            }

            // observe the bid offers list that should be displayed to logged in user
            bidDetailsViewModel.getDisplayBidOffers().observe(this, bidOffers -> {
                if (bidOffers != null) {
                    bidOffersToBeDisplayed = bidOffers;
                    if (selectedTabPosition == 1) tabLayout.selectTab(tabLayout.getTabAt(selectedTabPosition));
                }
            });

            // observe the bid offer that is made by the logged in user (if any)
            bidDetailsViewModel.getLoggedInUserBidOffer().observe(this, bidderBidOffer -> {
                loggedInUserBidOffer = bidderBidOffer;
                updateButtonState();
            });

            // after getting all latest data, update the UI
            setupBidTimer();
            populateBidRequestHeaderData();
            initTabLayout();
        });

        // close down the bid when it is overdue
        timerViewModel.getTimerEndStatus().observe(this, end -> {
            if (end) {
                // bid has become overdue, system auto close down the bid request
                bidDetailsViewModel.systemAutoCloseDownBidRequest(selectedBidRequest);
            }
        });

        // observe if there is any bid request that has been closed down due to user action (i.e. buy out, select bid winner)
        bidDetailsViewModel.getCloseDownBidResponse().observe(this, bidCloseDown -> {
            Context ctx = this;
            Activity activity = this;
            switch (bidCloseDown.status) {
                case SUCCESS:
                    if (bidDetailsViewModel.getBidCloseDownInitiator().equals(BidViewModel.SYSTEM_CLOSE_DOWN)) {
                        Snackbar.make(layout, "Bid has expired!", Snackbar.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                NavUtils.navigateUpFromSameTask(activity);
                            }
                        }, 2000);
                    } else {
                        Snackbar.make(layout, bidCloseDown.data, Snackbar.LENGTH_SHORT).show();
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent contractPageIntent = new Intent(ctx, ContractListActivity.class);
                                startActivity(contractPageIntent);
                                finish();
                            }
                        }, 2000);
                    }
                    break;
                case ERROR:
                    Snackbar.make(layout, bidCloseDown.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });

    }

    @Override
    protected void onPostResume() {
        // get/refresh the bid request details whenever user visits this page
        bidDetailsViewModel.getBidRequestDetails(selectedBidRequestId);
        super.onPostResume();
    }

    @Override
    public void onPause() {
        // stop current timer when user leaves the page
        timerViewModel.stopTimer();
        super.onPause();
    }

    private void setupSwipeToRefreshLayout() {
        // user can choose to refresh the bid request details page manually
        swipeRefreshLayout = findViewById(R.id.bidDetailsSwipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        bidDetailsViewModel.getBidRequestDetails(selectedBidRequestId);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void setupBidTimer() {
        // set up and start the bid request count-down timer
        TextView bidTimer = findViewById(R.id.txtBidTimer);
        timerViewModel.startTimer(selectedBidRequest);
        timerViewModel.getTimerCurrentValue().observe(this, timerVal -> {
            if (timerVal.status == MyResponse.ResponseStatus.SUCCESS) {
                bidTimer.setText(timerVal.data);
            }
        });
    }

    private void populateBidRequestHeaderData() {
        Button bidTypeTag = findViewById(R.id.tagBidTypeOnDetailsPage);
        TextView bidName = findViewById(R.id.txtBidNameOnDetailsPage);

        if (selectedBidRequest.getStatus() == BidRequestStatus.CLOSED_DOWN) {
            bidTypeTag.setText(BidRequestStatus.statusToString(BidRequestStatus.CLOSED_DOWN));
            bidTypeTag.setBackground(ContextCompat.getDrawable(this, R.drawable.closed_down_bid_tag));

        } else if (selectedBidRequest.getType() == BidRequestType.OPEN) {
            bidTypeTag.setText(BidRequestType.bidTypeToString(BidRequestType.OPEN));
            bidTypeTag.setBackground(ContextCompat.getDrawable(this, R.drawable.open_bid_tag));

        } else if (selectedBidRequest.getType() == BidRequestType.CLOSE) {
            bidTypeTag.setText(BidRequestType.bidTypeToString(BidRequestType.CLOSE));
            bidTypeTag.setBackground(ContextCompat.getDrawable(this, R.drawable.close_bid_tag));
        }

        bidName.setText(selectedBidRequest.getBidName());
    }

    protected void initTabLayout() {
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedTabPosition = 0;
                        fragment = new BidRequestDetailsFragment(selectedBidRequest);
                        break;
                    case 1:
                        selectedTabPosition = 1;
                        fragment = new BasicBidOfferListFragment(selectedBidRequest, bidOffersToBeDisplayed);
                        break;
                }
                displayFragment(fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tabLayout.getTabAt(selectedTabPosition));
            }
        });

        tabLayout.selectTab(tabLayout.getTabAt(selectedTabPosition));
    }

    protected void displayFragment(Fragment f) {
        fm.beginTransaction()
            .replace(R.id.bidDetailsFragmentContainer, f)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit();

    }

    protected abstract void updateButtonState();
}