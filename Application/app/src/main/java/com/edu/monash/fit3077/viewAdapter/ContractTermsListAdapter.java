package com.edu.monash.fit3077.viewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.edu.monash.fit3077.R;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.view.ContractTermsSelectListener;
import java.util.ArrayList;

public class ContractTermsListAdapter extends RecyclerView.Adapter<ContractTermsListAdapter.ContractTermsViewHolder> {

    private ArrayList<Boolean> contractTermExpansion;
    private ArrayList<Contract> contracts;
    private ContractTermsSelectListener listener;
    private String selectedContractId;
    private Context context;

    public ContractTermsListAdapter(Context ctx, ContractTermsSelectListener listener) {
        context = ctx;
        contracts = new ArrayList<>();
        contractTermExpansion = new ArrayList<>();
        this.listener = listener;
    }

    // to refresh data in recycler view
    public void setContractTermsData(ArrayList<Contract> contractsData) {
        if (contractsData == null) {
            contractsData = new ArrayList<>();
        }
        contracts = contractsData;
        notifyDataSetChanged();
        for (int i = 0; i < contracts.size(); i++) {
            contractTermExpansion.add(false);
        }
    }

    // keep track of contract term selected to update select button background color
    public void setSelectedContractId(String selectedContractId) {
        this.selectedContractId = selectedContractId;
    }

    @NonNull
    @Override
    public ContractTermsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_contract_terms_item, parent, false);
        return new ContractTermsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContractTermsViewHolder holder, int position) {
        Contract contract = contracts.get(position);
        LessonInformation contractTerm = contract.getLessonInfo();

        // populate tutor bid offer data
        holder.contractName.setText(contract.getContractName());
        holder.contractCreationDate.setText(contract.getCreationDateString());
        holder.contractSubject.setText(contractTerm.getSubjectName());
        holder.contractTutorCompetencyLevel.setText(contractTerm.getPreferredTutorCompetencyLevelString());
        holder.contractDayTime.setText(contractTerm.getSessionDayTimeString());
        holder.contractSessionNum.setText(contractTerm.getSessionNumPerWeekString());
        holder.contractRate.setText(contractTerm.getRatePerSessionString());
        holder.contractFreeLesson.setText(contractTerm.hasFreeLessonString());

        // update select button background color if current contract term item is selected
        if (contract.getId().equals(selectedContractId)) {
            holder.selectContractTermsBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_700));
        } else {
            holder.selectContractTermsBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
        }

        // control the expansion of expandable section
        boolean isExpanded = contractTermExpansion.get(position);
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return contracts.size();
    }

    public class ContractTermsViewHolder extends RecyclerView.ViewHolder{
        private final TextView contractName, contractCreationDate, contractSubject,
                contractTutorCompetencyLevel, contractDayTime, contractSessionNum,
                contractRate, contractFreeLesson;
        private final Button selectContractTermsBtn;
        private final ConstraintLayout expandableLayout;

        ContractTermsViewHolder(@NonNull View itemView) {
            super(itemView);
            contractName = itemView.findViewById(R.id.txtContractName);
            contractCreationDate = itemView.findViewById(R.id.txtContractCreationDate);
            contractSubject = itemView.findViewById(R.id.txtSubject);
            contractTutorCompetencyLevel = itemView.findViewById(R.id.txtTutorCompetencyLvl);
            contractDayTime = itemView.findViewById(R.id.txtTutorialDayTime);
            contractSessionNum = itemView.findViewById(R.id.txtSessionNum);
            contractRate = itemView.findViewById(R.id.txtRate);
            contractFreeLesson = itemView.findViewById(R.id.txtFreeLesson);
            selectContractTermsBtn = itemView.findViewById(R.id.btnSelectContractTerms);

            selectContractTermsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Contract selectedContract = contracts.get(getAdapterPosition());
                    setSelectedContractId(selectedContract.getId());
                    listener.onSelectContractTermsButtonClicked(selectedContract.getLessonInfo());
                    notifyDataSetChanged();
                }
            });


            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            ConstraintLayout visibleLayout = itemView.findViewById(R.id.visibleLayout);

            visibleLayout.setOnClickListener(v -> {
                boolean initialExpansionState = contractTermExpansion.get(getAdapterPosition());
                contractTermExpansion.set(getAdapterPosition(), !initialExpansionState);
                notifyItemChanged(getAdapterPosition());
            });
        }

    }
}
