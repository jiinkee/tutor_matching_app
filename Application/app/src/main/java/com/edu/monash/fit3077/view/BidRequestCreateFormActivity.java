package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.viewModel.BidRequestFormViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * Class responsible for bid request form activity
 */
public class BidRequestCreateFormActivity extends BidFormActivity {

    // view model responsible for data in bid request create form
    private BidRequestFormViewModel bidRequestFormViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bidRequestFormViewModel = new ViewModelProvider(this).get(BidRequestFormViewModel.class);
        setupBidFormOptionsFragmentContainer(new BidRequestFormOptionsFragment());
    }

    /***
     * Override method to add callback for create button to create new bid request
     */
    @Override
    protected void setupButton() {
        super.setupButton();

        Button createButton = (Button) findViewById(R.id.mCreateBidButton);
        createButton.setVisibility(View.VISIBLE);

        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createBidRequest();
            }
        });
    }

    /**
     * Call method in BidRequestFormViewModel to create bid request
     */
    private void createBidRequest() {
        View bidFormLayout = findViewById(R.id.bidFormLayout);

        bidRequestFormViewModel.createBidRequest().observe(this, response -> {
            switch (response.status) {
                case SUCCESS:
                    Snackbar.make(bidFormLayout, "Bid request has been successfully created", Snackbar.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    Snackbar.make(bidFormLayout, response.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
