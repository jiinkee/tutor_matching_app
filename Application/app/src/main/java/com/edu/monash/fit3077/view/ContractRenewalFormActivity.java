package com.edu.monash.fit3077.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.viewModel.ContractRenewalFormViewModel;
import com.google.android.material.snackbar.Snackbar;

public class ContractRenewalFormActivity extends AppCompatActivity {

    // view model responsible for data in bid form
    protected ContractRenewalFormViewModel contractRenewalFormViewModel;
    // fragment manager responsible for bid option fragment
    protected FragmentManager fragmentManager;

    // tags for intent
    final static String SELECTED_CONTRACT = "selected contract";
    final static String RENEWAL_STRATEGY = "renewal strategy";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set common layout for all bid form type
        setContentView(R.layout.activity_contract_renewal_form);
        // initiate view model
        contractRenewalFormViewModel = new ViewModelProvider(this).get(ContractRenewalFormViewModel.class);

        // get selected contract and renewal strategy object
        Intent intent = getIntent();
        Contract selectedContract = (Contract) intent.getSerializableExtra(SELECTED_CONTRACT);
        ContractRenewalStrategy renewalStrategy = (ContractRenewalStrategy) intent.getSerializableExtra(RENEWAL_STRATEGY);

        // keep track of selected contract in view model
        contractRenewalFormViewModel.setSelectedContract(selectedContract);

        // set up form sections
        setupButton();
        // setup first party and other section
        setupContractFirstPartySection();
        // setup fragment in form
        setupFragmentContainer(renewalStrategy);
    }

    // Setup renew and cancel buttons in contract renewal form
    protected void setupButton() {
        Activity activity = this;
        ConstraintLayout layout = findViewById(R.id.contractRenewalFormLayout);

        // setup cancel button
        Button cancelButton = (Button) findViewById(R.id.mCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        // setup renew button
        Button renewButton = (Button) findViewById(R.id.mRenewButton);
        renewButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                contractRenewalFormViewModel.renewContract().observe((LifecycleOwner) activity, response -> {
                    switch (response.status) {
                        case SUCCESS:
                            Snackbar.make(layout, "Renew contract successfully!", Snackbar.LENGTH_SHORT).show();
                            // redirect user back to contract list page
                            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    NavUtils.navigateUpFromSameTask(activity);
                                }
                            }, 2000);
                            break;
                        case ERROR:
                            Snackbar.make(layout, response.errorMsg, Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                    // reset the renew contract response status in view model
                    contractRenewalFormViewModel.resetRenewContractResponse();
                });
            }
        });
    }

    // setup and populate contract first party information into a fragment
    protected void setupContractFirstPartySection() {
        Student firstParty = contractRenewalFormViewModel.getFirstParty();
        TextView firstPartyNameFld = (TextView) findViewById(R.id.mFirstPartyNameFld);
        firstPartyNameFld.setText(firstParty.getFullName());
    }

    // setup tutor fragment and contract terms fragment in contract renewal form based on renewal strategy
    protected void setupFragmentContainer(ContractRenewalStrategy renewalStrategy) {
        // create tutor and terms fragment based on renewal strategy
        Fragment tutorFragment = renewalStrategy.renewContractTutorFragment();
        Fragment termsFragment = renewalStrategy.renewContractTermsFragment();

        // inflate tutor fragment
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contractTutorFragmentContainer, tutorFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        // inflate contract terms fragment
        fragmentManager.beginTransaction()
                .replace(R.id.contractTermsFragmentContainer, termsFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}
