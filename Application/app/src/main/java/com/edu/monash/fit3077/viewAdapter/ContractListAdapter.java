package com.edu.monash.fit3077.viewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.ContractStatus;
import com.edu.monash.fit3077.model.ValidContract;
import com.edu.monash.fit3077.view.RecyclerViewItemClickListener;

import java.util.ArrayList;

public class ContractListAdapter extends RecyclerView.Adapter<ContractListAdapter.ContractViewHolder> {
    private Context context;
    private ArrayList<Contract> contracts = new ArrayList<>();
    private RecyclerViewItemClickListener contractClickListener;

    public ContractListAdapter(Context ctx,  RecyclerViewItemClickListener listener) {
        this.context = ctx;
        this.contractClickListener = listener;
    }

    public void setContracts(ArrayList<Contract> contracts) {
        this.contracts = contracts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_contract_item, parent, false);
        return new ContractViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractViewHolder holder, int position) {
        Contract contract = contracts.get(position);

        // change background colour to red if the valid contract is almost expired
        if (contract.getStatus() == ContractStatus.VALID && ((ValidContract) contract).isAlmostExpired()) {
            holder.contractItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_red));
        }
        // change background colour to grey if the contract has expired
        else if (contract.getStatus() == ContractStatus.EXPIRED) {
            holder.contractItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
        }
        else {
            holder.contractItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_purple));
        }

        holder.contractName.setText(contract.getContractName());
        holder.contractCreationDate.setText(contract.getCreationDate().toString());

        holder.contractItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contractClickListener.onItemClicked(position, contract);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contracts.size();
    }


    public class ContractViewHolder extends RecyclerView.ViewHolder{
        private CardView contractItem;
        private TextView contractName, contractCreationDate;

        ContractViewHolder(@NonNull View itemView) {
            super(itemView);
            contractItem = itemView.findViewById(R.id.contractItemCard);
            contractName = itemView.findViewById(R.id.txtContractName);
            contractCreationDate = itemView.findViewById(R.id.txtContractCreationDate);
        }
    }
}
