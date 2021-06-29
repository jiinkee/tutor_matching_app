package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.viewModel.ContractRenewalFormViewModel;

public class EditableContractTermsFragment extends FormOptionsFragmentFactory{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_editable_contract_terms, container, false);
        return fragment;
    }

    @Override
    protected void setupViewModel() {
        formViewModel = new ViewModelProvider(requireActivity()).get(ContractRenewalFormViewModel.class);
    }
}
