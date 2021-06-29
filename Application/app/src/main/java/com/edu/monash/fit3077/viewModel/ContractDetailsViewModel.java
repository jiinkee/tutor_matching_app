package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.ContractStatus;
import com.edu.monash.fit3077.model.PendingContract;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.ContractRepository;
import com.edu.monash.fit3077.service.repository.UserRepository;

import java.time.Instant;

/**
 * View Model class which handles the retrieval and signing of a particular contract
 */
public class ContractDetailsViewModel extends BaseViewModel {
    private ContractRepository contractRepository;

    private MediatorLiveData<Contract> selectedContract; // observed by fragment to get the latest selected contract data
    private MediatorLiveData<MyResponse<Contract>> signContract; // observable live data which stores the status of contract signing

    public ContractDetailsViewModel() {
        contractRepository = new ContractRepository();
        selectedContract = new MediatorLiveData<>();
        signContract = new MediatorLiveData<>();
    }

    /**
     * FUNCTIONS THAT RETRIEVE THE DETAILS OF A SPECIFIC CONTRACT
     */
    public void getContractDetails(String contractId) {
        selectedContract.addSource(contractRepository.getContract(contractId), contractResponse -> {
            if (contractResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                selectedContract.setValue(contractResponse.data);
            }
        });
    }

    public LiveData<Contract> getSelectedContract() {
        return selectedContract;
    }

    /**
     * FUNCTIONS USED WHEN USER SIGN ON CONTRACT
     */
    public void signOnContract(Contract contract) {
        PendingContract pendingContract = (PendingContract) contract;
        User loggedInUser = super.getLoggedInUser();
        pendingContract.signOnContract(loggedInUser);
        signContract.addSource(contractRepository.updateContract(pendingContract), signContractResponse -> {
            switch (signContractResponse.status) {
                case SUCCESS:
                    Contract updatedContract = signContractResponse.data;
                    // logged in user sign on the contract successfully
                    // if the contract has already got signatures from both parties, system will automatically close the contract
                    if (updatedContract.getStudentSignDate() != null && updatedContract.getTutorSignDate() != null) {
                        closeContract(updatedContract);
                    } else {
                        signContract.setValue(signContractResponse);
                    }
                    break;
                case ERROR:
                    signContract.setValue(signContractResponse);
                    break;
            }
        });
    }

    // This function will be called when the contract has been signed by both parties and is ready to become a valid contract
    private void closeContract(Contract contract) {
        signContract.addSource(contractRepository.closeContract(contract, Instant.now()), closeContractResponse -> {
            signContract.setValue(closeContractResponse);
        });
    }

    public LiveData<MyResponse<Contract>> getSignContractResponse() {
        return signContract;
    }
}
