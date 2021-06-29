package com.edu.monash.fit3077.view;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.edu.monash.fit3077.viewAdapter.BaseArrayAdapter;

import java.util.ArrayList;

/**
 * Wrapper class for Android Spinner widget
 * @param <T> generic type for item in spinner options
 */
public class MySpinner<T> {

    private Spinner spinner;
    private BaseArrayAdapter adapter;
    private SpinnerItemClickListener listener;

    /**
     * Instantiate MySpinner with spinner id
     * @param context current context
     * @param spinnerId id for spinner to be wrapped in MySpinner
     * @param options list of option objected for spinner
     */
    public MySpinner(Context context, int spinnerId, ArrayList<T> options) {
        spinner = (Spinner) ((AppCompatActivity) context).findViewById(spinnerId);
        setupSpinner(context, options);
    }

    /**
     * Instantiate MySpinner with spinner object
     * @param context current context
     * @param spinner android spinner component to be wrapped in MySpinner
     * @param options list of option objects for spinner
     */
    public MySpinner(Context context, Spinner spinner, ArrayList<T> options) {
        this.spinner = spinner;
        setupSpinner(context, options);
    }

    // setup spinner listener and adapter
    private void setupSpinner(Context context, ArrayList<T> options) {
        adapter = new BaseArrayAdapter<T>(context, options);
        this.spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (listener != null) {
                    listener.onSpinnerItemClicked(adapterView);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Get the adapter of spinner
     * @return adapter of spinner
     */
    public BaseArrayAdapter getAdapter() {
        return adapter;
    }

    /**
     * Set current selection of spinner option
     * @param option object in spinner options to be selected
     */
    public void setSelection(T option) {
        int pos = adapter.getPosition(option);
        spinner.setSelection(pos);
    }

    /**
     * Set listener to spinner to be triggered when spinner item is selected
     * @param listener spinner item on click listener
     */
    public void setListener(SpinnerItemClickListener listener) {
        this.listener = listener;
    }

}
