package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.time.Instant;

public class ContractSignature implements Serializable {
    // studentSignDate and tutorSignDate are used to keep track of whether each contract participant has signed on the contract
    // signDate is used to keep track of the overall sign status of the contract, i.e. a contract is signed iff both student & tutor has signed on it
    private Instant studentSignDate, tutorSignDate, contractSignCompleteDate;

    // constructor for contract signature creation
    public ContractSignature() {}

    // constructor for contract signature data retrieval
    public ContractSignature(Instant studentSignDate, Instant tutorSignDate, Instant contractSignCompleteDate){
        this.studentSignDate = studentSignDate;
        this.tutorSignDate = tutorSignDate;
        this.contractSignCompleteDate = contractSignCompleteDate;
    }

    public void setContractSignCompleteDate(Instant contractSignCompleteDate) {
        this.contractSignCompleteDate = contractSignCompleteDate;
    }

    public void setStudentSignDate(Instant studentSignDate) {
        this.studentSignDate = studentSignDate;
    }

    public void setTutorSignDate(Instant tutorSignDate) {
        this.tutorSignDate = tutorSignDate;
    }

    public Instant getStudentSignDate() {
        return studentSignDate;
    }

    public Instant getTutorSignDate() {
        return tutorSignDate;
    }

}
