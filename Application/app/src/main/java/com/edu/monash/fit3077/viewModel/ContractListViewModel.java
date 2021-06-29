package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.ContractStatus;
import com.edu.monash.fit3077.model.PendingContract;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.ValidContract;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.ContractRepository;
import com.edu.monash.fit3077.service.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * View Model class which handles the retrieval of a list of contracts
 */
public class ContractListViewModel extends BaseViewModel {

    private ContractRepository contractRepository;
    private MediatorLiveData<MyResponse<ArrayList<Contract>>> contracts;

    public ContractListViewModel() {
        contractRepository = new ContractRepository();
        contracts = new MediatorLiveData<>();
    }

    /**
     * FUNCTIONS TO RETRIEVE A LIST OF CONTRACTS OF THE LOGGED IN USER
     */
    private void getContracts(ContractStatus status) {
        contracts.addSource(contractRepository.getContracts(status), contractResponse -> {
            switch (contractResponse.status) {
                case ERROR:
                    contracts.setValue(contractResponse);
                    break;
                case SUCCESS:
                    ArrayList<Contract> sortedContracts = sortContracts(contractResponse.data);
                    contracts.setValue(MyResponse.successResponse(sortedContracts));
                    break;
            }
        });
    }

    public void getValidContracts() {
        this.getContracts(ContractStatus.VALID);
    }

    public void getPendingContracts() {
        this.getContracts(ContractStatus.PENDING);
    }

    public void getRenewableContracts() {
        contracts.addSource(contractRepository.getLatestFiveSignedContracts(), latestFiveContracts -> {
            switch (latestFiveContracts.status) {
                case ERROR:
                    contracts.setValue(latestFiveContracts);
                    break;
                case SUCCESS:
                    ArrayList<Contract> sortedContracts = sortContracts(latestFiveContracts.data);
                    contracts.setValue(MyResponse.successResponse(sortedContracts));
                    break;
            }
        });
    }

    public LiveData<MyResponse<ArrayList<Contract>>> getContractsResponse() {
        return contracts;
    }

    /**
     * AUXILIARY METHOD
     */
    private ArrayList<Contract> sortContracts(ArrayList<Contract> contracts) {
        if (!contracts.isEmpty()) {
            // sort the retrieved contract by their creation date in descending order
            Collections.sort(contracts, new Comparator<Contract>() {
                @Override
                public int compare(Contract contract1, Contract contract2) {
                    return contract2.getCreationDate().compareTo(contract1.getCreationDate());
                }
            });
        }

        return contracts;
    }

}
