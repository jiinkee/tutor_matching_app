package com.edu.monash.fit3077.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.viewAdapter.ReadOnlyBidDayTimeListAdapter;

/**
 * This class represents the  fragment to be shown when user selects DETAILS tab on the bid request bid details page
 * Observer pattern is applied in this class to display the latest bid request details on the page.
 */
public class BidRequestDetailsFragment extends Fragment {

    private BidRequest selectedBidRequest;
    private LessonInformation bidRequestLessonInfo;

    public BidRequestDetailsFragment(BidRequest bidRequest) {
        selectedBidRequest = bidRequest;
        bidRequestLessonInfo = bidRequest.getRequiredLessonDetails();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_bid_details, container, false);

        // display the bid request details
        TextView bidType = fragment.findViewById(R.id.mBidTypeTextView);
        bidType.setText(selectedBidRequest.getTypeString());

        TextView subject = fragment.findViewById(R.id.mBidSubjectTextView);
        subject.setText(selectedBidRequest.getSubject().toString());

        TextView tutorCompetencyLevel = fragment.findViewById(R.id.mBidTutorCompetencyTextView);
        tutorCompetencyLevel.setText(selectedBidRequest.getPreferredTutorCompetencyLevelString());

        TextView sessionNumPerWeek = fragment.findViewById(R.id.mBidNoOfSessionsPerWeekTextView);
        sessionNumPerWeek.setText(bidRequestLessonInfo.getSessionNumPerWeekString());

        TextView rate = fragment.findViewById(R.id.mBidRateTextView);
        rate.setText(bidRequestLessonInfo.getRatePerSessionString());

        TextView freeLesson = fragment.findViewById(R.id.mBidFreeLessonTextView2);
        freeLesson.setText(bidRequestLessonInfo.hasFreeLessonString());

        TextView startDate = fragment.findViewById(R.id.mBidStartDateTextView2);
        startDate.setText(bidRequestLessonInfo.getLessonStartDateString());

        TextView endDate = fragment.findViewById(R.id.mBidEndDateTextView);
        endDate.setText(bidRequestLessonInfo.getLessonEndDateString());

        // initialize bid request lesson day time recycler view
        RecyclerView recyclerView = fragment.findViewById(R.id.mBidDayTimeRecyclerView);
        ReadOnlyBidDayTimeListAdapter adapter = new ReadOnlyBidDayTimeListAdapter(fragment.getContext(),
                selectedBidRequest.getRequiredLessonDetails().getSessionDayTime());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(fragment.getContext()));

        return fragment;
    }
}