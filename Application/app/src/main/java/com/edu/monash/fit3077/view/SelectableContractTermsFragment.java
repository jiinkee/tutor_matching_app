package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.viewAdapter.ContractTermsListAdapter;
import com.edu.monash.fit3077.viewModel.ContractRenewalFormViewModel;
import com.google.android.material.snackbar.Snackbar;

public class SelectableContractTermsFragment extends FormOptionsFragmentFactory implements ContractTermsSelectListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_selectable_contract_terms, container, false);
        return fragment;
    }

    @Override
    protected void setupViewModel() {
        formViewModel = new ViewModelProvider(requireActivity()).get(ContractRenewalFormViewModel.class);
    }

    @Override
    public void onSelectContractTermsButtonClicked(LessonInformation selectedContractTerms) {
        ((ContractRenewalFormViewModel) formViewModel).setSelectedContractTerms(selectedContractTerms);
    }

    // override parent form class to remove bid type, subject and tutor competency lvl options
    @Override
    protected void setupSubjectOptions() {}

    @Override
    protected void setupPreferredTutorCompetencyLvlOptions() {}

    // override parent form class to replace multiple form options into a recycler view for contract terms selection
    //  and remain the start date and duration options
    @Override
    protected void setupLessonInfoOptions() {
        // setup recycler view
        RecyclerView contractTermsRecyclerView = activity.findViewById(R.id.recyclerViewContractTerms);

        ContractTermsListAdapter adapter = new ContractTermsListAdapter(getContext(), this);
        contractTermsRecyclerView.setAdapter(adapter);
        contractTermsRecyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // observe contracts obtained from web service
        ((ContractRenewalFormViewModel) formViewModel).getLatestFiveContract().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    adapter.setContractTermsData(response.data);
                    adapter.setSelectedContractId(((ContractRenewalFormViewModel) formViewModel).getSelectedContractId());
                    break;
                case ERROR:
                    if (isAdded()) Snackbar.make(requireActivity().findViewById(android.R.id.content), response.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

}
