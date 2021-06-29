package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.ContractStatus;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.ValidContract;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.ContractRepository;
import com.edu.monash.fit3077.service.repository.UserRepository;

import java.util.ArrayList;

public class HomePageViewModel extends BaseViewModel {
    private ContractRepository contractRepo;
    private MediatorLiveData<Integer> validContractNum;
    private MutableLiveData<Integer> almostExpireContractNum;

    public HomePageViewModel() {
        contractRepo = new ContractRepository();
        validContractNum = new MediatorLiveData<>();
        almostExpireContractNum = new MutableLiveData<>();
    }

    public LiveData<Integer> getLoggedInUserValidContractCount() {
        // count only the number of valid contracts
        validContractNum.addSource(contractRepo.getContracts(ContractStatus.VALID), contracts -> {
            if (contracts.status == MyResponse.ResponseStatus.SUCCESS) {
                validContractNum.setValue(contracts.data.size());

                int almostExpiredValidContracts =  (int) contracts.data.stream().filter(contract -> ((ValidContract) contract).isAlmostExpired()).count();
                almostExpireContractNum.setValue(almostExpiredValidContracts);
            }
        });

        return validContractNum;
    }

    public LiveData<Integer> getAlmostExpiredValidContractCount() {
        return almostExpireContractNum;
    }
}
