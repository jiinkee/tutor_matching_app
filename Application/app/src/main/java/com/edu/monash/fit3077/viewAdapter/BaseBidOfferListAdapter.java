package com.edu.monash.fit3077.viewAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.LessonInformation;

import java.util.ArrayList;


public abstract class BaseBidOfferListAdapter extends RecyclerView.Adapter<BaseBidOfferListAdapter.BaseBidOfferViewHolder> {

    protected ArrayList<BidOffer> bidOffers;
    private ArrayList<Boolean> bidResponseExpansion;
    // need subject id to know which tutor competency to get from tutor's competencies list
    protected String bidSubjectId;

    public BaseBidOfferListAdapter() {
    }

    // to refresh data in recycler view
    public void setBidOffersData(ArrayList<BidOffer> bidOffersData, String bidSubjectId) {
        if (bidOffersData == null) {
            bidOffersData = new ArrayList<>();
        }
        bidOffers = bidOffersData;
        this.bidSubjectId = bidSubjectId;
        notifyDataSetChanged();
        bidResponseExpansion = new ArrayList<>();
        for (int i = 0; i < bidOffers.size(); i++) {
            bidResponseExpansion.add(false);
        }
    }

    @NonNull
    @Override
    public abstract BaseBidOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    protected View inflateBidOfferItemLayout(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_bid_offer_item, parent, false);
        return view;
    }

    @Override
    public int getItemCount() {
        return bidOffers.size();
    }

    @Override
    public void onBindViewHolder(@NonNull BaseBidOfferViewHolder holder, int position) {
        BidOffer tutorBidOffer = bidOffers.get(position);
        LessonInformation bidOfferLessonInfo = tutorBidOffer.getLessonInfo();
        // populate tutor bid offer data
        holder.tutorName.setText(tutorBidOffer.getBidder().getFullName());
        holder.bidOfferCreationTime.setText(tutorBidOffer.getCreationDateString());

        holder.bidOfferTutorCompetencyLevel.setText(tutorBidOffer.getTutorCompetency(bidSubjectId).getLevelString());
        holder.bidOfferDayTime.setText(bidOfferLessonInfo.getSessionDayTimeString());

        holder.bidOfferSessionNum.setText(bidOfferLessonInfo.getSessionNumPerWeekString());
        holder.bidOfferRate.setText(bidOfferLessonInfo.getRatePerSessionString());
        holder.bidOfferFreeLesson.setText(bidOfferLessonInfo.hasFreeLessonString());

        holder.bidOfferStartDate.setText(bidOfferLessonInfo.getLessonStartDateString());
        holder.bidOfferEndDate.setText(bidOfferLessonInfo.getLessonEndDateString());

        // control the expansion of expandable section
        boolean isExpanded = bidResponseExpansion.get(position);
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    public abstract class BaseBidOfferViewHolder extends RecyclerView.ViewHolder{
        private TextView tutorName, bidOfferCreationTime, bidOfferTutorCompetencyLevel, bidOfferDayTime, bidOfferSessionNum,
                         bidOfferRate, bidOfferFreeLesson, bidOfferStartDate, bidOfferEndDate;
        private ConstraintLayout expandableLayout, visibleLayout;

        BaseBidOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            tutorName = itemView.findViewById(R.id.txtBidOfferTutor);
            bidOfferCreationTime = itemView.findViewById(R.id.txtBidOfferTime);
            bidOfferTutorCompetencyLevel = itemView.findViewById(R.id.txtBidOfferTutorCompetencyLvl);
            bidOfferDayTime = itemView.findViewById(R.id.txtBidOfferTutorialDayTime);
            bidOfferSessionNum = itemView.findViewById(R.id.txtBidOfferSessionNum);
            bidOfferRate = itemView.findViewById(R.id.txtBidOfferRate);
            bidOfferFreeLesson = itemView.findViewById(R.id.txtBidOfferFreeLesson);
            bidOfferStartDate = itemView.findViewById(R.id.txtBidOfferStartDate);
            bidOfferEndDate = itemView.findViewById(R.id.txtBidOfferEndDate);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            visibleLayout = itemView.findViewById(R.id.visibleLayout);

            visibleLayout.setOnClickListener(v -> {
                boolean initialExpansionState = bidResponseExpansion.get(getAdapterPosition());
                bidResponseExpansion.set(getAdapterPosition(), !initialExpansionState);
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
