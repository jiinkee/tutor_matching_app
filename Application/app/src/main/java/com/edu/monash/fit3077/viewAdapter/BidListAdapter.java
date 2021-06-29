package com.edu.monash.fit3077.viewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.view.RecyclerViewItemClickListener;

import java.util.ArrayList;


public class BidListAdapter extends RecyclerView.Adapter<BidListAdapter.BidViewHolder> {

    private Context context;
    private RecyclerViewItemClickListener<BidRequest> bidRequestClickListener;
    private ArrayList<BidRequest> bidRequests = new ArrayList<>();

    public BidListAdapter(Context ctx, RecyclerViewItemClickListener<BidRequest> listener) {
        this.context = ctx;
        this.bidRequestClickListener = listener;
    }

    public void setBidListData(ArrayList<BidRequest> bidRequests) {
        this.bidRequests = bidRequests;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_bid_request_item, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        BidRequest bidRequest = bidRequests.get(position);

        // display bid request data
        holder.bidName.setText(bidRequest.getBidName());
        holder.bidCreationDate.setText(bidRequest.getCreationDateString());

        if (bidRequest.getStatus() == BidRequestStatus.CLOSED_DOWN) {
            holder.bidTypeTag.setText(BidRequestStatus.statusToString(BidRequestStatus.CLOSED_DOWN));
            holder.bidTypeTag.setBackground(ContextCompat.getDrawable(context, R.drawable.closed_down_bid_tag));
        } else if (bidRequest.getType() == BidRequestType.OPEN) {
            holder.bidTypeTag.setText(BidRequestType.bidTypeToString(BidRequestType.OPEN));
            holder.bidTypeTag.setBackground(ContextCompat.getDrawable(context, R.drawable.open_bid_tag));
        } else {
            holder.bidTypeTag.setText(BidRequestType.bidTypeToString(BidRequestType.CLOSE));
            holder.bidTypeTag.setBackground(ContextCompat.getDrawable(context, R.drawable.close_bid_tag));
        }

        // add onClickListener
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bidRequestClickListener.onItemClicked(position, bidRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bidRequests.size();
    }

    public class BidViewHolder extends RecyclerView.ViewHolder{
        private CardView card;
        private TextView bidName, bidCreationDate;
        private Button bidTypeTag;

        BidViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.bidRequestItemCard);
            bidName = itemView.findViewById(R.id.txtBidName);
            bidCreationDate = itemView.findViewById(R.id.txtBidCreationDate);
            bidTypeTag = itemView.findViewById(R.id.tagBidType);
        }

    }
}
