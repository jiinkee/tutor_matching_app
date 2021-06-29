package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.edu.monash.fit3077.R;

public class EditableContractTutorFragment extends ContractFormTutorFragmentFactory {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_editable_contract_tutor, container, false);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initiate text view
        EditText secondPartyIdFld = (EditText) activity.findViewById(R.id.mSecondPartyIdFld);

        secondPartyIdFld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // update user entered rate per session in view model
                contractRenewalFormViewModel.setPreferredSecondPartyUserId(secondPartyIdFld.getText().toString());
            }
        });
    }

}
