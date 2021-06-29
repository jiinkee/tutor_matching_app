package com.edu.monash.fit3077.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Contract;

/**
 * This class manages two groups of radio buttons and determine the chosen contract renewal strategy
 * based on the checked radio buttons.
 */
public class ContractRenewalActivity extends AppCompatActivity {
    private FragmentManager fm;
    private RadioGroup tutorStrategySelectionRadioGroup;
    private RadioGroup contractTermStrategySelectionRadioGroup;
    private Button proceedBtn;
    private ContractRenewalStrategy renewalStrategy;
    final static String SELECTED_CONTRACT = "selected contract";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_renewal);

        fm = getSupportFragmentManager();

        // get selected contract object to be renewed
        Intent intent = getIntent();
        Contract selectedContract = (Contract) intent.getSerializableExtra(SELECTED_CONTRACT);

        // get the radio button groups
        tutorStrategySelectionRadioGroup = findViewById(R.id.tutorChoiceRadioGroup);
        contractTermStrategySelectionRadioGroup = findViewById(R.id.contractTermRadioGroup);

        // set different types of strategies when different radio button is checked
        Context context = this;
        proceedBtn = findViewById(R.id.btnContractRenewalProceed);
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                determineContractRenewalStrategy();
                // pass selected contract to be renewed and correspond strategy object created based on
                //   student selected renew options to contract renewal form
                Intent contractRenewalFormPageIntent = new Intent(context, ContractRenewalFormActivity.class);
                contractRenewalFormPageIntent.putExtra(ContractRenewalFormActivity.SELECTED_CONTRACT, selectedContract);
                contractRenewalFormPageIntent.putExtra(ContractRenewalFormActivity.RENEWAL_STRATEGY, renewalStrategy);
                startActivity(contractRenewalFormPageIntent);
            }
        });

    }

    private void determineContractRenewalStrategy() {
        int selectedRenewTutorStrategy = tutorStrategySelectionRadioGroup.getCheckedRadioButtonId();
        int selectedRenewTermsStrategy = contractTermStrategySelectionRadioGroup.getCheckedRadioButtonId();

        if (selectedRenewTutorStrategy == R.id.radioBtnSameTutor && selectedRenewTermsStrategy == R.id.radioBtnReuseContractTerm) {
            renewalStrategy = new SameTutorReuseTermsStrategy();
        }
        else if (selectedRenewTutorStrategy == R.id.radioBtnSameTutor && selectedRenewTermsStrategy == R.id.radioBtnModifyContractTerm) {
            renewalStrategy = new SameTutorModifyTermsStrategy();
        }
        else if (selectedRenewTutorStrategy == R.id.radioBtnDifferentTutor && selectedRenewTermsStrategy == R.id.radioBtnReuseContractTerm) {
            renewalStrategy = new DifferentTutorReuseTermsStrategy();
        }
        else if (selectedRenewTutorStrategy == R.id.radioBtnDifferentTutor && selectedRenewTermsStrategy == R.id.radioBtnModifyContractTerm) {
            renewalStrategy = new DifferentTutorModifyTermsStrategy();
        }
    }

}
