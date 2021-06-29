package com.edu.monash.fit3077.viewAdapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.view.LessonDayTimeSetListener;
import com.edu.monash.fit3077.view.MySpinner;
import com.edu.monash.fit3077.view.SpinnerItemClickListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Subclass of BaseBidDayTimeListAdapter responsible for editable bid day time options
 */
public class EditableBidDayTimeListAdapter extends BaseBidDayTimeListAdapter {

    private TimePickerDialog timePickerDialog;
    private ArrayList<String> lessonDayOptions;
    private LessonDayTimeSetListener lessonDayTimeSetListener;
    private HashMap<String,ArrayList<LocalTime[]>> presetDayTime;

    public EditableBidDayTimeListAdapter(Context ctx, HashMap<String,ArrayList<LocalTime[]>> lessonDayTimeOption, HashMap<String,ArrayList<LocalTime[]>> lessonDayTime,ArrayList<String> lessonDayOptions, LessonDayTimeSetListener lessonDayTimeSetListener) {
        super(ctx, lessonDayTimeOption);
        this.lessonDayOptions = lessonDayOptions;
        this.presetDayTime = lessonDayTime;
        this.lessonDayTimeSetListener = lessonDayTimeSetListener;
    }

    @Override
    public void setDayTimeOptions(HashMap<String, ArrayList<LocalTime[]>> lessonDayTime) {
        if (this.presetDayTime != null) {
            super.setDayTimeOptions(presetDayTime);
            this.presetDayTime = null;
        } else {
            super.setDayTimeOptions(lessonDayTime);
        }
    }

    @NonNull
    @Override
    public BaseBidDayTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_editable_bid_day_time_item, parent, false);
        return new EditableBidDayTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBidDayTimeViewHolder holder, int position) {
        EditableBidDayTimeViewHolder editableHolder = (EditableBidDayTimeViewHolder) holder;
        editableHolder.bidStartTimeTextView.setText(times.get(position)[0].toString());
        editableHolder.bidEndTimeTextView.setText(times.get(position)[1].toString());
        setupBidDaySpinner(editableHolder.bidDay, position);
        editableHolder.setBidStartTimeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createTimePickerDialog(editableHolder.bidStartTimeTextView, 0, position);
            }
        });
        editableHolder.setBidEndTimeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                createTimePickerDialog(editableHolder.bidEndTimeTextView, 1, position);
            }
        });
    }

    // setup lesson day spinner in each recycler view item
    private void setupBidDaySpinner(Spinner bidDaySpinner, int position) {
        MySpinner lessonDaySpinner = new MySpinner(context, bidDaySpinner, lessonDayOptions);
        lessonDaySpinner.setSelection(days.get(position));
        lessonDaySpinner.setListener(new SpinnerItemClickListener() {
            @Override
            public void onSpinnerItemClicked(AdapterView adapterView) {
                // update selected day in array list in adapter
                days.set(position, adapterView.getSelectedItem().toString());
                // update selected day time in view model
                lessonDayTimeSetListener.onLessonDayTimeSet(getSelectedDayTimes());
            }
        });
    }


    // create time picker dialog and update recycler view item when a time is selected
    private void createTimePickerDialog(TextView v, int index, int position) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // date picker dialog
        timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String AM_PM ;
                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        if(hourOfDay < 12) {
                            if (hourOfDay== 0) {
                                hourOfDay = 12;
                            }
                            AM_PM = context.getResources().getString(R.string.time_am);
                        } else {
                            if (hourOfDay!=12) {
                                hourOfDay -= 12;
                            }
                            AM_PM = context.getResources().getString(R.string.time_pm);
                        }
                        v.setText(String.format("%02d:%02d:%s", hourOfDay, minute, AM_PM));

                        final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                                .appendPattern("HH:mm")
                                .toFormatter();

                        // update selected time in view model
                        times.get(position)[index] = LocalTime.parse(selectedTime, timeFormatter);
                        // update selected day time in view model
                        lessonDayTimeSetListener.onLessonDayTimeSet(getSelectedDayTimes());
                    }}, hour, minute, DateFormat.is24HourFormat(context));
        timePickerDialog.show();
    }

    public class EditableBidDayTimeViewHolder extends BaseBidDayTimeListAdapter.BaseBidDayTimeViewHolder{
        protected Spinner bidDay;
        protected ImageButton setBidStartTimeBtn, setBidEndTimeBtn;
        protected TextView bidStartTimeTextView, bidEndTimeTextView;

        EditableBidDayTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            bidDay = itemView.findViewById(R.id.mBidDaySpinner);
            setBidStartTimeBtn = itemView.findViewById(R.id.mSetBidStartTimeBtn);
            setBidEndTimeBtn = itemView.findViewById(R.id.mSetBidEndTimeBtn);
            bidStartTimeTextView = itemView.findViewById(R.id.mBidStartTimeFld);
            bidEndTimeTextView = itemView.findViewById(R.id.mBidEndTimeFld);
        }
    }
}
