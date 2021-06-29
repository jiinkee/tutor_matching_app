package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.viewModel.BidOfferFormViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * Class responsible for bid offer create form activity
 */
public class BidOfferCreateFormActivity extends BidFormActivity{

    // view model responsible for data in bid form
    private BidOfferFormViewModel bidOfferFormViewModel;
    // tag for bid request information to be included in intent
    final static String BID_REQUEST_INFO = "bid_request_info";
    private BidRequest selectedBidRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup bid offer form view model
        bidOfferFormViewModel = new ViewModelProvider(this).get(BidOfferFormViewModel.class);

        // get selected bid request object and set predefined bid request in view model
        Intent intent = getIntent();
        selectedBidRequest = (BidRequest) intent.getSerializableExtra(BID_REQUEST_INFO);
        bidOfferFormViewModel.setPredefinedBidRequest(selectedBidRequest);

        // inflate bidOfferFormOptionsFragment into fragment container in form
        setupBidFormOptionsFragmentContainer(new BidOfferFormOptionsFragment());

    }

    /**
     * Override method to add callback for create button to create new bid offer
     */
    @Override
    protected void setupButton() {
        super.setupButton();
        Button createButton = (Button) findViewById(R.id.mCreateBidButton);
        createButton.setVisibility(View.VISIBLE);

        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createBidOffer();
            }
        });
    }

    /**
     * Call method in BidOfferFormViewModel to create bid offer
     */
    private void createBidOffer() {
        View bidFormLayout = findViewById(R.id.bidFormLayout);

        bidOfferFormViewModel.createBidOffer().observe(this, response -> {
            switch (response.status) {
                case SUCCESS:
                    Snackbar.make(bidFormLayout, "Bid offer has been successfully created", Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                    break;
                case ERROR:
                    Snackbar.make(bidFormLayout, response.errorMsg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}
