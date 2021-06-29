package com.edu.monash.fit3077.view;

import androidx.fragment.app.Fragment;

import java.io.Serializable;

public interface ContractRenewalStrategy extends Serializable {
    Fragment renewContractTutorFragment();

    Fragment renewContractTermsFragment();
}
