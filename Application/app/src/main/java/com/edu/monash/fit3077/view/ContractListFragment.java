package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.viewAdapter.ContractListAdapter;
import com.edu.monash.fit3077.viewModel.ContractListViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * This class is the inline class for all types of contract list fragment.
 * It passes in a callback into the RecyclerView (contract list) adapter so that the program can be notified of the contract chosen by the user.
 * It also applies the observer pattern to always render the latest/updated contracts on UI.
 */
public class ContractListFragment extends Fragment implements RecyclerViewItemClickListener<Contract>{

    private String contractTab;
    private View fragment;
    private RecyclerView recyclerView;
    private ContractListViewModel contractListViewModel;
    private ContractListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public ContractListFragment(String selectedContractTab) {
        this.contractTab = selectedContractTab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment =  inflater.inflate(R.layout.fragment_contract_list, container, false);

        // setup swipe to refresh layout
        setupSwipeToRefreshLayout();

        // retrieve contract view model
        contractListViewModel = new ViewModelProvider(requireActivity()).get(ContractListViewModel.class);

        // initialize contracts list recycler view
        recyclerView = fragment.findViewById(R.id.recyclerViewContracts);
        adapter = new ContractListAdapter(requireActivity(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));

        // observe contracts obtained from web service
        contractListViewModel.getContractsResponse().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    adapter.setContracts(response.data);
                    break;
                case ERROR:
                    if (isAdded()) Snackbar.make(requireActivity().findViewById(android.R.id.content), response.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });

        return fragment;
    }

    @Override
    public void onResume() {
        // retrieve the latest contracts list whenever the user visit this page
        getContracts();
        super.onResume();
    }

    private void setupSwipeToRefreshLayout() {
        swipeRefreshLayout = fragment.findViewById(R.id.contractListSwipeRefreshLayout);

        // user can choose to refresh the contract list manually as well
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getContracts();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void getContracts() {
        if (contractTab.equals(getString(R.string.contract_renewable))) {
            contractListViewModel.getRenewableContracts();
        } else if (contractTab.equals(getString(R.string.contract_pending))) {
            contractListViewModel.getPendingContracts();
        } else if (contractTab.equals(getString(R.string.contract_valid))) {
            contractListViewModel.getValidContracts();
        }
    }

    @Override
    public void onItemClicked(int position, Contract contract) {
        Intent contractDetailsPageIntent = null;

        if (contractTab.equals(getString(R.string.contract_renewable))) {
            contractDetailsPageIntent = new Intent(requireActivity(), RenewableContractDetailsActivity.class);
        } else if (contractTab.equals(getString(R.string.contract_pending))) {
            contractDetailsPageIntent = new Intent(requireActivity(), PendingContractDetailsActivity.class);
        } else if (contractTab.equals(getString(R.string.contract_valid))) {
            contractDetailsPageIntent = new Intent(requireActivity(), ValidContractDetailsActivity.class);
        }

        contractDetailsPageIntent.putExtra(ContractDetailsActivity.SELECTED_CONTRACT_ID, contract.getId());
        startActivity(contractDetailsPageIntent);
    }

}