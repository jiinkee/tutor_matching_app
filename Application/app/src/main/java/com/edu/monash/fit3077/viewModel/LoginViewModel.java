package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.UserRepository;

public class LoginViewModel extends ViewModel {
    private UserRepository userRepository;
    private MediatorLiveData<MyResponse<String>> loginStatus;

    public LoginViewModel() {
        userRepository = UserRepository.getInstance();
        loginStatus = new MediatorLiveData<>();
    }

    public void login(String userName, String password) {
        loginStatus.addSource(userRepository.login(userName, password), loginResponse -> {
            loginStatus.setValue(loginResponse);
        });
    }

    public LiveData<MyResponse<String>> getLoginStatus() {
        if (loginStatus == null) {
            loginStatus = new MediatorLiveData<>();
        }
        return loginStatus;
    }
}
