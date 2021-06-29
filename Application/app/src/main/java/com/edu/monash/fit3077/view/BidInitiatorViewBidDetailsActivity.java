package com.edu.monash.fit3077.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.model.BidRequestType;
import com.google.android.material.tabs.TabLayout;

/**
 * This class represents the bid initiator's (student's) view of the bid details page.
 * It helps render different types of bid offer list when BIDDERS tab is selected according to the
 * status and type of the bid request.
 */
public class BidInitiatorViewBidDetailsActivity extends BidDetailsActivity {

    private int selectedTabPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initTabLayout() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = new BidRequestDetailsFragment(selectedBidRequest);
                switch (tab.getPosition()) {
                    case 0:
                        selectedTabPosition = 0;
                        fragment = new BidRequestDetailsFragment(selectedBidRequest);
                        break;
                    case 1:
                        selectedTabPosition = 1;
                        fragment = getBidOfferListFragment();
                        break;
                }
                BidInitiatorViewBidDetailsActivity.super.displayFragment(fragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (selectedTabPosition == 0) {
                    onTabSelected(tab);
                }
            }
        });

        tabLayout.selectTab(tabLayout.getTabAt(selectedTabPosition));
    }

    private BidOfferListFragmentFactory getBidOfferListFragment() {
        // depending on the bid request status & type, the system has different ways of showing the bid offers
        BidOfferListFragmentFactory bidOfferListFragment = null;

        if (selectedBidRequest.getStatus() == BidRequestStatus.CLOSED_DOWN) {
            // bid request has been closed down, hence bid offer is read-only
            bidOfferListFragment = new BasicBidOfferListFragment(super.selectedBidRequest, super.bidOffersToBeDisplayed);
        } else if (selectedBidRequest.getType() == BidRequestType.OPEN) {
            // Open type bid request, student can select the winner
            bidOfferListFragment = new SelectableBidOfferListFragment(super.selectedBidRequest, super.bidOffersToBeDisplayed);
        } else if (selectedBidRequest.getType() == BidRequestType.CLOSE) {
            // Close type bid request, student can chat with tutor privately & select the winner
            bidOfferListFragment = new AdvancedBidOfferListFragment(super.selectedBidRequest, super.bidOffersToBeDisplayed);
        }

        return bidOfferListFragment;
    }

    @Override
    protected void updateButtonState() {
        // do nothing
    }
}
