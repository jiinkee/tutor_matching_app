package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;

public abstract class Contract implements Serializable {
    protected String id;
    protected Student firstParty;
    protected Tutor secondParty;
    protected ContractSignature contractSignature;
    protected Instant creationDate;
    protected Instant expiryDate;
    protected ContractStatus status; // for internal usage, not stored in web service
    protected LessonInformation lessonInfo;
    protected ContractPayment paymentInfo;

    // constructor for contract retrieval
    public Contract(String id, Student firstParty, Tutor secondParty, Instant creationDate, Instant expiryDate,
                    ContractSignature contractSignature, LessonInformation lessonInfo, ContractPayment paymentInfo) {
        this.id = id;
        this.firstParty = firstParty;
        this.secondParty = secondParty;
        this.creationDate = creationDate;
        this.expiryDate = expiryDate;
        this.contractSignature = contractSignature;
        this.lessonInfo = lessonInfo;
        this.paymentInfo = paymentInfo;
    }

    // for new contract creation, when a contract is first created, it is not signed by any contract participant
    // contract creation date is set when contract object is created
    public Contract(Student firstParty, Tutor secondParty, LessonInformation lessonInfo) {
        this.firstParty = firstParty;
        this.secondParty = secondParty;
        this.creationDate = Instant.now();
        this.lessonInfo = lessonInfo;
    }

    // GETTER methods
    public String getContractName() {
        return "Contract - " + getSubject().getName();
    }

    public String getId() {
        return id;
    }

    public Student getFirstParty() {
        return firstParty;
    }

    public Tutor getSecondParty() {
        return secondParty;
    }

    public String getSecondPartyId() {
        return secondParty.getId();
    }

    public String getExpiryDateString() {
        return expiryDate.toString();
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return creationDate.toString();
    }

    public ContractStatus getStatus() {
        return status;
    }

    public LessonInformation getLessonInfo() {
        return lessonInfo;
    }

    public ContractPayment getPaymentInfo() {
        return paymentInfo;
    }

    // HIDE DELEGATE
    public Subject getSubject() {
        return lessonInfo.getSubject();
    }

    public String getPreferredTutorCompetencyLvlString(){
        return String.valueOf(this.lessonInfo.getPreferredTutorCompetencyLevel());
    }

    public Competency getTutorCompetency() {
        return secondParty.getCompetencyForSubject(getSubject().getId());
    }

    public ArrayList<Qualification> getTutorQualification() {
        return secondParty.getQualifications();
    }

    public Instant getStudentSignDate() {
        return this.contractSignature.getStudentSignDate();
    }

    public Instant getTutorSignDate() {
        return this.contractSignature.getTutorSignDate();
    }

    // SETTER
    public void setSignDate(Instant signDate) {
        this.contractSignature.setContractSignCompleteDate(signDate);
    }
}
