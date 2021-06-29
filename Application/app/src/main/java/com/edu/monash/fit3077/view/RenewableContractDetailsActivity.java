package com.edu.monash.fit3077.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.viewModel.HomePageViewModel;

public class RenewableContractDetailsActivity extends ContractDetailsActivity{
    private HomePageViewModel homePageViewModel;
    private Context ctx;

    public RenewableContractDetailsActivity() {
        ctx = this;
    }

    @Override
    protected void setUpButton() {
        // show and set up renew button
        Button renewButton = findViewById(R.id.mRenewContractBtn);
        renewButton.setVisibility(View.VISIBLE);
        // disable renew button by default
        renewButton.setEnabled(false);

        // only show the renew button when logged in user has < 5 valid contracts at that time
        homePageViewModel = new ViewModelProvider(this).get(HomePageViewModel.class);
        homePageViewModel.getLoggedInUserValidContractCount().observe(this, validContractsCount -> {
            if (validContractsCount < 5) {
                renewButton.setEnabled(true);
                renewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent contractRenewalPageIntent = new Intent(ctx, ContractRenewalActivity.class);
                        contractRenewalPageIntent.putExtra(ContractRenewalActivity.SELECTED_CONTRACT, selectedContract);
                        startActivity(contractRenewalPageIntent);
                    }
                });
            }
        });

    }
}
