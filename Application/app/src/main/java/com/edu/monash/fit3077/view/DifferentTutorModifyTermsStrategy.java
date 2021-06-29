package com.edu.monash.fit3077.view;

import androidx.fragment.app.Fragment;

public class DifferentTutorModifyTermsStrategy implements ContractRenewalStrategy {
    @Override
    public Fragment renewContractTutorFragment() {
        return new EditableContractTutorFragment();
    }

    @Override
    public Fragment renewContractTermsFragment() {
        return new EditableContractTermsFragment();
    }
}
