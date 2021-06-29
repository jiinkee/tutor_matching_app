package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.viewModel.ContractRenewalFormViewModel;

public abstract class ContractFormTutorFragmentFactory extends Fragment {

    protected ContractRenewalFormViewModel contractRenewalFormViewModel;
    protected FragmentActivity activity;

    @Nullable
    @Override
    abstract public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contractRenewalFormViewModel = new ViewModelProvider(requireActivity()).get(ContractRenewalFormViewModel.class);
        activity = getActivity();
    }
}
