package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.edu.monash.fit3077.R;

/***
 * Abstract class responsible for bid form activity
 */
public abstract class BidFormActivity extends AppCompatActivity {

    // fragment manager responsible for bid option fragment
    protected FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set common layout for all bid form type
        setContentView(R.layout.activity_bid_form);

        // change label from lesson end date to duration for bid form
        TextView endDateLbl = findViewById(R.id.mBidEndDateLbl);
        endDateLbl.setText(R.string.bid_duration);

        // set up callback for buttons in bid form
        setupButton();
    }

    /**
     * Set callback for cancel button in bid form to close the bid form when user click on cancel button
     */
    protected void setupButton() {
        Button cancelButton = (Button) findViewById(R.id.mCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Setup bid form options fragment container with the fragment provided.
     *
     * @param fragment fragment to be placed into bid form options fragment
     */
    protected void setupBidFormOptionsFragmentContainer(FormOptionsFragmentFactory fragment) {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mBidFormOptionsFragmentContainer, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

}
