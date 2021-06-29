package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.viewModel.HomePageViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * This class represents the home page of the application.
 * Depending on the role of the logged in user, the application will display different buttons that lead to different functions.
 */
public class HomePageActivity extends AppCompatActivity {

    private View layout;
    private TextView userNameText, userRoleText, contractNumText;
    private Button createBidBtn, viewBidsBtn, viewContractsBtn;
    private HomePageViewModel homePageViewModel;
    private User loggedInUser;
    private final int VALID_CONTRACT_LIMIT = 5;
    private int almostExpiredContractCount = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        layout = findViewById(R.id.homePageLayout);
        homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        loggedInUser = homePageViewModel.getLoggedInUser();

        // initialize user profile
        userNameText = findViewById(R.id.txtName);
        userRoleText = findViewById(R.id.txtUserRole);
        contractNumText = findViewById(R.id.txtContractNum);
        userNameText.setText(loggedInUser.getFullName());
        userRoleText.setText(loggedInUser.getRoleString());

        // set listener of each button
        createBidBtn = findViewById(R.id.btnCreateNewBidRequest);
        viewBidsBtn = findViewById(R.id.btnViewMyBid);
        viewContractsBtn = findViewById(R.id.btnViewStudentContracts);

        createBidBtn.setOnClickListener(v -> {
            Intent myBidRequestCreateFormPageIntent = new Intent(this, BidRequestCreateFormActivity.class);
            startActivity(myBidRequestCreateFormPageIntent);
        });

        viewBidsBtn.setOnClickListener(v -> {
            Intent bidPageIntent = new Intent(this, BidListActivity.class);
            startActivity(bidPageIntent);
        });

        viewContractsBtn.setOnClickListener(v -> {
            Intent contractPageIntent = new Intent(this, ContractListActivity.class);
            startActivity(contractPageIntent);
        });
    }

    @Override
    protected void onPostResume() {

        // toggle the visibility of each button
        if (loggedInUser.getRolePermissions().contains(UserRole.BID_INITIATOR)) {
            createBidBtn.setVisibility(View.VISIBLE);
            viewBidsBtn.setText("View My Bids");
        } else if (loggedInUser.getRolePermissions().contains(UserRole.BIDDER)) {
            viewBidsBtn.setText("View All Bids");
        }

        // update the number of valid contracts every time user visit home page
        homePageViewModel.getLoggedInUserValidContractCount().observe(this, contractCount -> {
            contractNumText.setText(Integer.toString(contractCount));

            // disable create bid button if student's contract count exceeds the limit
            if (loggedInUser.getRolePermissions().contains(UserRole.BID_INITIATOR) && contractCount >= VALID_CONTRACT_LIMIT) {
                createBidBtn.setEnabled(false);
            } else {
                createBidBtn.setEnabled(true);
            }

            // has already retrieved the user's valid contract, so we can now determine the number of
            // valid contracts that almost expired
            homePageViewModel.getAlmostExpiredValidContractCount().observe(this, newAlmostExpiredContractCount -> {
                // notify user if there is any valid contracts which will expire in one month
                if (almostExpiredContractCount != newAlmostExpiredContractCount ) {
                    almostExpiredContractCount = newAlmostExpiredContractCount;
                    if (almostExpiredContractCount == 1) {
                        Snackbar.make(layout, "There is " + almostExpiredContractCount + " contract due in one month.", Snackbar.LENGTH_SHORT)
                                .show();
                    } else if (almostExpiredContractCount > 1) {
                        Snackbar.make(layout, "There are " + almostExpiredContractCount + " contracts due in one month.", Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            });

        });

        super.onPostResume();
    }

    @Override
    public void onBackPressed() {
        // quit app when user presses back button at Home Page
        finishAffinity();
        finish();
    }
}
