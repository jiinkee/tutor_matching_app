package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.viewModel.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * This class represents the login page of the application.
 */
public class LoginActivity extends AppCompatActivity {
    private LoginViewModel viewModel;
    private ConstraintLayout layout;
    private EditText userName, password;
    private Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        layout = findViewById(R.id.loginLayout);
        userName = findViewById(R.id.editUserName);
        password = findViewById(R.id.editPassword);
        loginBtn = findViewById(R.id.btnLogin);

        // login button listener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameInput = userName.getText().toString();
                String passwordInput = password.getText().toString();

                viewModel.login(userNameInput, passwordInput);

            }
        });

        // observe whether the login is successful or fail
        viewModel.getLoginStatus().observe(this, response -> {
            if (response != null) {
                switch (response.status) {
                    case SUCCESS:
                        navigateToHomePage();
                        break;
                    case ERROR:
                        Snackbar.make(layout, response.errorMsg, Snackbar.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void navigateToHomePage() {
        Intent homePageIntent = new Intent(this, HomePageActivity.class);
        startActivity(homePageIntent);
    }
}


