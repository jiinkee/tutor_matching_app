package com.edu.monash.fit3077.view;

import androidx.fragment.app.Fragment;

public class SameTutorReuseTermsStrategy implements ContractRenewalStrategy {
    @Override
    public Fragment renewContractTutorFragment() {
        return new ReadOnlyContractTutorFragment();
    }

    @Override
    public Fragment renewContractTermsFragment() {
        return new SelectableContractTermsFragment();
    }
}
