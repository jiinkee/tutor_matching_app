package com.edu.monash.fit3077.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.viewModel.ContractListViewModel;
import com.google.android.material.tabs.TabLayout;

/**
 * This class manages the tab inside the contract list page, i.e. the PENDING tab, VALID tab and RENEWABLE tab
 * This class will swap out the contract list fragment according to the tab selected by the user.
 */
public class ContractListActivity extends AppCompatActivity {

    private FragmentManager fm;
    private TabLayout tabLayout;
    private int selectedTabPosition = 0;
    private ContractListViewModel contractListViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_list);

        fm = getSupportFragmentManager();
        contractListViewModel = new ViewModelProvider(this).get(ContractListViewModel.class);

        // set up contract type tab
        tabLayout = findViewById(R.id.contractTypeTabLayout);

        // hide Expired tab from logged in tutor
        if (contractListViewModel.getLoggedInUser().getRoleString().equals("TUTOR")) {
            tabLayout.removeTabAt(2);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = new ContractListFragment(tab.getText().toString());
                selectedTabPosition = tab.getPosition();
                displayContractList(fragment);
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
    }

    @Override
    protected void onPostResume() {
        tabLayout.selectTab(tabLayout.getTabAt(selectedTabPosition));
        super.onPostResume();
    }

    private void displayContractList(Fragment f) {
        fm.beginTransaction()
            .replace(R.id.contractListFragmentContainer, f)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit();
    }

}
