package com.edu.monash.fit3077.viewAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for RecyclerView.adapter responsible for bid day time list
 */
public abstract class BaseBidDayTimeListAdapter extends RecyclerView.Adapter<BaseBidDayTimeListAdapter.BaseBidDayTimeViewHolder> {

    protected Context context;
    protected ArrayList<String> days = new ArrayList<>();
    protected ArrayList<LocalTime[]> times = new ArrayList<>();

    public BaseBidDayTimeListAdapter(Context ctx, HashMap<String, ArrayList<LocalTime[]>> lessonDayTime) {
        context = ctx;
        setDayTimeOptions(lessonDayTime);
    }

    // get current lesson day time options value in the adapter
    public HashMap<String, ArrayList<LocalTime[]>> getSelectedDayTimes() {
        HashMap<String, ArrayList<LocalTime[]>> selectedDayTimes = new HashMap<>();
        for (int i = 0; i < getItemCount(); i++) {
            String day = days.get(i);
            LocalTime startTime = times.get(i)[0];
            LocalTime endTime = times.get(i)[1];
            LocalTime[] time = {startTime, endTime};

            if (selectedDayTimes.get(day) != null) {
                ArrayList<LocalTime[]> existingTimes = selectedDayTimes.get(day);
                existingTimes.add(time);
                selectedDayTimes.put(day, existingTimes);
            } else {
                selectedDayTimes.put(day, new ArrayList<LocalTime[]>(Arrays.<LocalTime[]>asList(time)));
            }
        }

        return selectedDayTimes;
    }

    // set the lesson day time option values in the adapter
    public void setDayTimeOptions(HashMap<String, ArrayList<LocalTime[]>> lessonDayTime) {
        days = new ArrayList<>();
        times = new ArrayList<>();
        for (Map.Entry<String, ArrayList<LocalTime[]>> entry : lessonDayTime.entrySet()) {

            String dayOption = entry.getKey();

            for (LocalTime[] timeOption: entry.getValue()) {
                days.add(dayOption);
                LocalTime[] time = new LocalTime[2];
                time[0] = timeOption[0];
                time[1] = timeOption[1];
                times.add(time);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    abstract public BaseBidDayTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull BaseBidDayTimeViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return days.size();
    }

    public abstract class BaseBidDayTimeViewHolder extends RecyclerView.ViewHolder{
        BaseBidDayTimeViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}


