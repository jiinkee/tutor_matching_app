package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.service.repository.UserRepository;

/**
 * The base of all View Model classes.
 */
public abstract class BaseViewModel extends ViewModel {
    // set as private to prevent system from modifying the logged in user instance
    private User loggedInUser = UserRepository.getInstance().getLoggedInUser();

    /**
     * Function to retrieve the logged in user instance
     */
    public User getLoggedInUser() {
        return loggedInUser;
    }
}
