package com.edu.monash.fit3077.view;

import androidx.fragment.app.Fragment;

public class SameTutorModifyTermsStrategy implements ContractRenewalStrategy {
    @Override
    public Fragment renewContractTutorFragment() {
        return new ReadOnlyContractTutorFragment();
    }

    @Override
    public Fragment renewContractTermsFragment() {
        return new EditableContractTermsFragment();
    }
}
