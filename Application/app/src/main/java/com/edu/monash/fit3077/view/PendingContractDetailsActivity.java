package com.edu.monash.fit3077.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.UserRole;
import com.google.android.material.snackbar.Snackbar;

/**
 * This class represents the contract details fragment that will be shown on the contract details page when the contract
 * is a pending contract.
 * It contains a SIGN button and when user clicks on it, it will notify the view model class to perform the required operations.
 */
public class PendingContractDetailsActivity extends ContractDetailsActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConstraintLayout layout = findViewById(R.id.contractDetailsPage);

        // observe when user sign on contract
        contractDetailsViewModel.getSignContractResponse().observe(this, contractSign -> {
            Activity activity = this;
            switch (contractSign.status) {
                case SUCCESS:
                    Snackbar.make(layout, "Sign on contract successfully!", Snackbar.LENGTH_SHORT).show();
                    // redirect user back to contract list page
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NavUtils.navigateUpFromSameTask(activity);
                        }
                    }, 2000);
                    break;
                case ERROR:
                    Snackbar.make(layout, contractSign.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    protected void setUpButton() {
        // show and set up sign button
        Button signButton = findViewById(R.id.mSignContractBtn);
        User user = contractDetailsViewModel.getLoggedInUser();

        if (selectedContract != null) {
            // only show the sign button if the user has not signed on the contract yet
            if ((user.getRolePermissions().contains(UserRole.CONTRACT_FIRST_PARTY) && selectedContract.getStudentSignDate()==null) ||
                    (user.getRolePermissions().contains(UserRole.CONTRACT_SECOND_PARTY) && selectedContract.getTutorSignDate()==null)) {
                signButton.setVisibility(View.VISIBLE);
                signButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contractDetailsViewModel.signOnContract(selectedContract);
                    }
                });
            }
        }
    }
}
