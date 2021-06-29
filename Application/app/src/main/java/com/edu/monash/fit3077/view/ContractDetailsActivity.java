package com.edu.monash.fit3077.view;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.viewModel.ContractDetailsViewModel;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

/**
 * This class represents the contract details page that will be shown on screen when user selects one of the
 * contract from the contract list page.
 * Observer pattern is applied in this class to display the latest contract details on the page.
 */
public abstract class ContractDetailsActivity extends AppCompatActivity {
    private String selectedContractId;

    protected ContractDetailsViewModel contractDetailsViewModel;
    protected Contract selectedContract;
    protected LessonInformation contractLessonInfo;

    final static String SELECTED_CONTRACT_ID = "selected contract ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_details);

        contractDetailsViewModel = new ViewModelProvider(this).get(ContractDetailsViewModel.class);

        // get the ID of the selected contract
        selectedContractId = getIntent().getStringExtra(SELECTED_CONTRACT_ID);

        // observe the selected contract object
        contractDetailsViewModel.getSelectedContract().observe(this, contract -> {
            if (contract != null) {
                selectedContract = contract;
                contractLessonInfo = contract.getLessonInfo();

                // after getting the latest contract details, update UI
                updateTextViews();
                setUpButton();
            }
        });
    }

    @Override
    protected void onPostResume() {
        // retrieve the latest details of the selected contract each time user visits this page
        contractDetailsViewModel.getContractDetails(selectedContractId);
        super.onPostResume();
    }

    private void updateTextViews() {
        // populate contract details data
        TextView contractStartDate = findViewById(R.id.mStartDateFld);
        contractStartDate.setText(contractLessonInfo.getLessonStartDateString());

        TextView contractEndDate = findViewById(R.id.mEndDateFld);
        contractEndDate.setText(selectedContract.getExpiryDateString());

        TextView contractServiceCharge = findViewById(R.id.mServiceChargeFld);
        contractServiceCharge.setText(selectedContract.getPaymentInfo().getAmountString());

        TextView fstPartyName = findViewById(R.id.mFirstPartyNameFld);
        fstPartyName.setText(selectedContract.getFirstParty().getFullName());

        TextView fstPartyCompetency = findViewById(R.id.mFirstPartyCompetencyLvlFld);
        fstPartyCompetency.setText(selectedContract.getPreferredTutorCompetencyLvlString());

        TextView sndPartyName = findViewById(R.id.mSecondPartyNameFld);
        sndPartyName.setText(selectedContract.getSecondParty().getFullName());

        TextView sndPartyQualifications = findViewById(R.id.mSecondPartyQualificationsFld);
        sndPartyQualifications.setText(selectedContract.getSecondParty().getQualificationsString());

        TextView sndPartyCompetency = findViewById(R.id.mSecondPartyCompetencyLvlFld);
        sndPartyCompetency.setText(selectedContract.getTutorCompetency().getLevelString());

        TextView contractSubject = findViewById(R.id.mSubjectFld);
        contractSubject.setText(selectedContract.getSubject().getName());

        TextView contractTopic = findViewById(R.id.mTopicFld2);
        contractTopic.setText(selectedContract.getSubject().getDescription());

        TextView contractDayTime = findViewById(R.id.mDayNTimeFld);
        contractDayTime.setText(contractLessonInfo.getSessionDayTimeString());

        TextView contractSessionNum = findViewById(R.id.mSessionNumPerWeekFld);
        contractSessionNum.setText(contractLessonInfo.getSessionNumPerWeekString());

        TextView contractRate = findViewById(R.id.mRateFld);
        contractRate.setText(contractLessonInfo.getRatePerSessionString());

        TextView contractFreeLesson = findViewById(R.id.mFreeLessonTxt);
        contractFreeLesson.setText(contractLessonInfo.hasFreeLessonString());

        TextView fstPartySignDate = findViewById(R.id.mFirstPartySignFld);
        fstPartySignDate.setText(selectedContract.getStudentSignDate() != null ? "Yes" : "No");

        TextView sndPartySignDate = findViewById(R.id.mSecondPartySignFld);
        sndPartySignDate.setText(selectedContract.getTutorSignDate() != null ? "Yes": "No");
    }

    protected abstract void setUpButton();
}