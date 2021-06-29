package com.edu.monash.fit3077.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.viewModel.BidListViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

/**
 * This class manages the tab inside the bid request list page, i.e. the OPEN tab, CLOSE tab, CLOSED DOWN tab and SUBSCRIBED tab
 * This class will swap out the bid list fragment according to the tab selected by the user
 */
public class BidListActivity extends AppCompatActivity {

    private FragmentManager fm;
    private TabLayout tabLayout;
    private int selectedTabPosition = 0;
    private ConstraintLayout layout;
    private BidListViewModel bidListViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_request_list);

        fm = getSupportFragmentManager();
        tabLayout = findViewById(R.id.bidRequestLTypeTabLayout);
        layout = findViewById(R.id.bidRequestListPage);
        bidListViewModel = new ViewModelProvider(this).get(BidListViewModel.class);

        // set up the bid request type tab
        if (bidListViewModel.getLoggedInUser().getRolePermissions().contains(UserRole.BIDDER)) {
            tabLayout.getTabAt(2).setText(getString(R.string.bid_subscribed));
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment bidListFragment = new BidListFragment(tab.getText().toString());
                selectedTabPosition = tab.getPosition();
                displayBidList(bidListFragment);
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

        // observe is there is any bid request that has been closed down by the system automatically
        bidListViewModel.getCloseDownBidResponse().observe(this, bidCloseDown -> {
            switch(bidCloseDown.status) {
                case SUCCESS:
                    Snackbar.make(layout, "There are expired bid(s) which are closed down by system", Snackbar.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Snackbar.make(layout, bidCloseDown.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    protected void onPostResume() {
        // ensure that the tab selected remains the same when user navigate back to this bid list page from bid details page
        tabLayout.selectTab(tabLayout.getTabAt(selectedTabPosition));
        super.onPostResume();
    }


    private void displayBidList(Fragment bidListFragment) {
        fm.beginTransaction()
            .replace(R.id.bidRequestListFragmentContainer, bidListFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit();
    }
}
