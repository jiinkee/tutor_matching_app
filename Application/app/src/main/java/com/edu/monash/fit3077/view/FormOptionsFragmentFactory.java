package com.edu.monash.fit3077.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.viewAdapter.EditableBidDayTimeListAdapter;
import com.edu.monash.fit3077.viewModel.FormViewModel;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Factory class to setup options for different type of forms
 */
public abstract class FormOptionsFragmentFactory extends Fragment {

    protected FragmentActivity activity;
    protected FormViewModel formViewModel;

    @Nullable
    @Override
    abstract public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    // setup view model and the form
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity = getActivity();
        setupViewModel();
        setupFormOptionSections();
    }

    // instantiate the view model to be used for the fragment
    protected abstract void setupViewModel();

    // default template for form with different types of form options
    // subclass can override different sub-methods used in this template method in order to create read only/ selectable form options
    protected void setupFormOptionSections() {
        setupBidTypeOptions();
        setupSubjectOptions();
        setupPreferredTutorCompetencyLvlOptions();
        setupLessonInfoOptions();
        setupStartDateOptions();
        setupDurationOptions();
    }

    // hook method to setup bid type spinner in form that needs this option
    protected void setupBidTypeOptions() {}

    // setup subject spinner
    protected void setupSubjectOptions() {
        ArrayList<Subject> bidSubjectsOptions = formViewModel.getSubjectOptions();
        MySpinner bidSubjectSpinner = new MySpinner<Subject>(activity, R.id.mSubjectFld, bidSubjectsOptions);
        bidSubjectSpinner.setListener(new SpinnerItemClickListener() {
            @Override
            public void onSpinnerItemClicked(AdapterView adapterView) {
                formViewModel.setSubject((Subject) adapterView.getSelectedItem());
            }
        });
    }

    // setup preferred tutor competency level spinner
    protected void setupPreferredTutorCompetencyLvlOptions() {
        ArrayList<Integer> tutorCompetencyLvlOptions = formViewModel.getPreferredTutorCompetencyLvlOptions();
        MySpinner preferredTutorCompetencyLvlSpinner = new MySpinner<Integer>(activity, R.id.mPreferredTutorCompetencyLvlFld, tutorCompetencyLvlOptions);
        preferredTutorCompetencyLvlSpinner.setListener(new SpinnerItemClickListener() {
            @Override
            public void onSpinnerItemClicked(AdapterView adapterView) {
                formViewModel.setPreferredTutorCompetencyLvl((Integer) adapterView.getSelectedItem());
            }
        });
    }

    // set up lesson information related option (number of session per week, lesson day time,
    // rate per session, has free lesson) in form
    protected void setupLessonInfoOptions() {
        // set up day time recycler view
        RecyclerView lessonDayTimeRecyclerView = activity.findViewById(R.id.mEditableDayTimeRecyclerView);
        HashMap<String, ArrayList<LocalTime[]>> lessonDayTimeOptions = new HashMap<>();
        EditableBidDayTimeListAdapter lessonDayTimeRecyclerViewAdapter = new EditableBidDayTimeListAdapter(getContext(), formViewModel.getLessonDayTimeOptions(), formViewModel.getLessonDayTime(), formViewModel.getLessonDayOptions(), new LessonDayTimeSetListener() {

            @Override
            public void onLessonDayTimeSet(HashMap<String, ArrayList<LocalTime[]>> selectedLessonDayTime) {
                formViewModel.setLessonDayTime(selectedLessonDayTime);
            }
        });
        lessonDayTimeRecyclerView.setAdapter(lessonDayTimeRecyclerViewAdapter);
        lessonDayTimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // set up number of session per week spinner
        ArrayList<Integer> numOfSessionPerWeekOptions = formViewModel.getNumSessionPerWeekOptions();
        MySpinner numOfSessionPerWeekSpinner = new MySpinner(activity, R.id.mNoOfSessionsPerWeekFld, numOfSessionPerWeekOptions);
        numOfSessionPerWeekSpinner.setSelection(formViewModel.getNumSessionPerWeek());
        numOfSessionPerWeekSpinner.setListener(new SpinnerItemClickListener() {
            @Override
            public void onSpinnerItemClicked(AdapterView adapterView) {
                // set selected number of session per week in view model
                formViewModel.setNumSessionPerWeek((Integer) adapterView.getSelectedItem());
                // update lesson day time options based on number of session per week selected
                lessonDayTimeRecyclerViewAdapter.setDayTimeOptions(formViewModel.getLessonDayTimeOptions());
            }
        });

        // set up rate per session option
        EditText ratePerSessionEditText = (EditText) activity.findViewById(R.id.mRateFld);
        ratePerSessionEditText.setText(formViewModel.getRatePerSessionString());
        ratePerSessionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // update user entered rate per session in view model
                formViewModel.setRatePerSession(ratePerSessionEditText.getText().toString());
            }
        });

        // set up has free lesson options
        CheckBox hasFreeLessonCheckBox = (CheckBox) activity.findViewById(R.id.mFreeLessonFld);
        hasFreeLessonCheckBox.setChecked(formViewModel.getHasFreeLesson());
        hasFreeLessonCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // update selected has free lesson option in view model
                formViewModel.setHasFreeLesson(b);
            }
        });
    }

    // setup start date text view and button
    protected void setupStartDateOptions() {
        TextView lessonStartDateTextView = (TextView) activity.findViewById(R.id.mStartDateFld);
        ImageButton lessonStartDateButton = (ImageButton) activity.findViewById(R.id.mSetStartDateBtn);
        lessonStartDateTextView.setText(formViewModel.getLessonStartDateString());
        // add listener to lesson start date set button
        lessonStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDatePickerDialog(lessonStartDateTextView, new LessonDateSetListener() {

                    @Override
                    public void onLessonDateSet(String date) {
                        // update selected lesson start date in view model
                        formViewModel.setLessonStartDate(date);
                    }
                });
            }
        });
    }

    // setup duration spinner
    protected void setupDurationOptions() {
        ArrayList<Integer> lessonDurationOptions = formViewModel.getDurationOptions();
        MySpinner lessonDurationSpinner = new MySpinner(activity, R.id.mLessonDurationFld, lessonDurationOptions);
        lessonDurationSpinner.setSelection(formViewModel.getLessonDuration());
        lessonDurationSpinner.setListener(new SpinnerItemClickListener() {
            @Override
            public void onSpinnerItemClicked(AdapterView adapterView) {
                // set selected lesson duration in view model
                formViewModel.setLessonDuration((Integer) adapterView.getSelectedItem());
            }
        });
    }

    // create a date picker dialog
    private void createDatePickerDialog(TextView v, LessonDateSetListener listener) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // date picker dialog

        DatePickerDialog datePickerDialog = new DatePickerDialog(activity,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String date = String.format("%02d/%02d/%02d", day, month+1, year);
                        v.setText(date);
                        listener.onLessonDateSet(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }
}
