package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.viewModel.BidOfferFormViewModel;

/**
 * Class responsible for bid request form options fragment
 */
public class BidOfferFormOptionsFragment extends FormOptionsFragmentFactory {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_bid_offer_form_options, container, false);
        return fragment;
    }

    @Override
    protected void setupViewModel() {
        formViewModel = new ViewModelProvider(requireActivity()).get(BidOfferFormViewModel.class);
    }

    // override bid request options setup methods in parent class to change to read only options
    @Override
    protected void setupBidTypeOptions() {
        TextView bidTypeTextView = (TextView) activity.findViewById(R.id.mBidTypeFld);
        bidTypeTextView.setText(formViewModel.getBidRequestTypeString());
    }

    @Override
    protected void setupSubjectOptions() {
        TextView bidSubjectTextView = (TextView) activity.findViewById(R.id.mSubjectFld);
        bidSubjectTextView.setText(formViewModel.getSubjectString() == null? "": formViewModel.getSubjectString());
    }

    @Override
    protected void setupPreferredTutorCompetencyLvlOptions() {
        TextView preferredTutorCompetencyLvlTextView = (TextView) activity.findViewById(R.id.mPreferredTutorCompetencyLvlFld);
        preferredTutorCompetencyLvlTextView.setText(formViewModel.getPreferredTutorCompetencyLvlString());
    }
}
