package com.edu.monash.fit3077.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.lifecycle.ViewModelProvider;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.viewModel.BidRequestFormViewModel;

import java.util.ArrayList;

/**
 * Class responsible for bid request form options fragment
 */
public class BidRequestFormOptionsFragment extends FormOptionsFragmentFactory {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_bid_request_form_options, container, false);
        return fragment;
    }

    @Override
    protected void setupViewModel() {
        formViewModel = new ViewModelProvider(requireActivity()).get(BidRequestFormViewModel.class);
    }

    @Override
    protected void setupBidTypeOptions() {
        ArrayList<BidRequestType> bidRequestTypeOptions = formViewModel.getBidTypeOptions();
        MySpinner bidTypeSpinner = new MySpinner<BidRequestType>(activity, R.id.mBidTypeFld, bidRequestTypeOptions);
        bidTypeSpinner.setListener(new SpinnerItemClickListener() {
            @Override
            public void onSpinnerItemClicked(AdapterView adapterView) {
                formViewModel.setBidRequestType((BidRequestType) adapterView.getSelectedItem());
            }
        });
    }
}
