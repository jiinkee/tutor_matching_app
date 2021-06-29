package com.edu.monash.fit3077.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.viewModel.BidOfferFormViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * Class responsible for bid offer edit form activity
 */
public class BidOfferEditFormActivity extends BidFormActivity {

    // view model responsible for data in bid offer edit form
    private BidOfferFormViewModel bidOfferFormViewModel;
    // tag for bid request information to be included in intent
    final static String BID_REQUEST_INFO = "bid_request_info";
    // tag for bid offer information to be included in intent
    final static String BID_OFFER_INFO = "bid_offer_info";
    private BidRequest selectedBidRequest;
    private BidOffer selectedBidOffer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // setup bid offer form view model
        bidOfferFormViewModel = new ViewModelProvider(this).get(BidOfferFormViewModel.class);

        // get selected bid request object and set predefined bid request in view model
        Intent intent = getIntent();
        selectedBidRequest = (BidRequest) intent.getSerializableExtra(BID_REQUEST_INFO);
        bidOfferFormViewModel.setPredefinedBidRequest(selectedBidRequest);

        // get selected bid offer object and set predefined bid offer in view model
        selectedBidOffer = (BidOffer) intent.getSerializableExtra(BID_OFFER_INFO);
        bidOfferFormViewModel.setPredefinedBidOffer(selectedBidOffer);

        // inflate bidOfferFormOptionsFragment into fragment container in form
        setupBidFormOptionsFragmentContainer(new BidOfferFormOptionsFragment());

    }

    /**
     * Override method to add callback for edit button to edit bid offer
     */
    @Override
    protected void setupButton() {
        super.setupButton();
        Button createButton = (Button) findViewById(R.id.mEditBidButton);
        createButton.setVisibility(View.VISIBLE);

        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editBidOffer();
            }
        });
    }

    /**
     * Call method in BidOfferFormViewModel to edit bid offer
     */
    private void editBidOffer() {
        View bidFormLayout = findViewById(R.id.bidFormLayout);

        bidOfferFormViewModel.editBidOffer().observe(this, response -> {
            switch (response.status) {
                case SUCCESS:
                    Snackbar.make(bidFormLayout, "Bid offer has been successfully edited", Snackbar.LENGTH_SHORT).show();
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
