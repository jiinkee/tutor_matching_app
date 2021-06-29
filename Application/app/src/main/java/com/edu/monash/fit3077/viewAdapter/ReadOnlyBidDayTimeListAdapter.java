package com.edu.monash.fit3077.viewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.edu.monash.fit3077.R;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Subclass of BaseBidDayTimeListAdapter responsible for read only bid day time options
 */
public class ReadOnlyBidDayTimeListAdapter extends BaseBidDayTimeListAdapter {

    public ReadOnlyBidDayTimeListAdapter(Context ctx, HashMap<String, ArrayList<LocalTime[]>> lessonDayTime) {
        super(ctx, lessonDayTime);
    }

    @NonNull
    @Override
    public BaseBidDayTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_read_only_bid_day_time_item, parent, false);
        return new ReadOnlyBidDayTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBidDayTimeViewHolder holder, int position) {
        ReadOnlyBidDayTimeViewHolder readOnlyHolder = (ReadOnlyBidDayTimeViewHolder) holder;
        readOnlyHolder.bidDay.setText(days.get(position));
        readOnlyHolder.bidStartTime.setText(times.get(position)[0].toString());
        readOnlyHolder.bidEndTime.setText(times.get(position)[1].toString());
    }

    public class ReadOnlyBidDayTimeViewHolder extends BaseBidDayTimeListAdapter.BaseBidDayTimeViewHolder{
        protected TextView bidDay, bidStartTime, bidEndTime;

        ReadOnlyBidDayTimeViewHolder(@NonNull View itemView) {
            super(itemView);
            bidDay = itemView.findViewById(R.id.mBidDayTextView);
            bidStartTime = itemView.findViewById(R.id.mBidStartTimeTextView);
            bidEndTime = itemView.findViewById(R.id.mBidEndTimeTextView);
        }
    }
}