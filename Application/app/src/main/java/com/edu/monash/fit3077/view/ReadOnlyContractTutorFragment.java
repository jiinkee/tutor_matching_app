package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Tutor;

public class ReadOnlyContractTutorFragment extends ContractFormTutorFragmentFactory{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_read_only_contract_tutor, container, false);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get second party information
        Tutor secondParty = contractRenewalFormViewModel.getSecondParty();

        // set second party name on view
        TextView secondPartyName = activity.findViewById(R.id.mSecondPartyNameFld);
        secondPartyName.setText(secondParty.getFullName());

        // set second party qualifications on view
        TextView secondPartyQualifications = activity.findViewById(R.id.mSecondPartyQualificationsFld);
        secondPartyQualifications.setText(secondParty.getQualificationsString());
    }
}
