package com.edu.monash.fit3077.model;

import java.io.Serializable;

public class ContractPayment implements Serializable {
    private double amount;

    public ContractPayment(double amount) {
        this.amount = amount;
    }

    // GETTER method
    public String getAmountString() {
        return Double.toString(amount);
    }
}
