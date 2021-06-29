package com.edu.monash.fit3077.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PendingContract extends Contract {
    // constructor for contract retrieval from API
    // if it is a pending contract, then its sign date must be null, i.e. ha snot been signed by both parties
    public PendingContract(String id, Student firstParty, Tutor secondParty, Instant creationDate,
                           Instant expiryDate, ContractSignature contractSignature, LessonInformation lessonInfo, ContractPayment paymentInfo) {
        super(id, firstParty, secondParty, creationDate, expiryDate, contractSignature, lessonInfo, paymentInfo);
        this.status = ContractStatus.PENDING;
    }

    // constructor for new contract creation
    public PendingContract(Student firstParty, Tutor secondParty, LessonInformation lessonInfo) {
        super(firstParty, secondParty, lessonInfo);
        // contract expiry date is based on lesson end date
        this.expiryDate = lessonInfo.getLessonEndDate();
        this.paymentInfo = calculatePaymentAmount(lessonInfo);
        this.status = ContractStatus.PENDING;
        this.contractSignature = new ContractSignature();
    }

    // method for contract participants to sign on contract
    public PendingContract signOnContract(User contractParticipant) {
        if (contractParticipant.getRolePermissions().contains(UserRole.CONTRACT_FIRST_PARTY)) {
            this.contractSignature.setStudentSignDate(Instant.now());
        } else if (contractParticipant.getRolePermissions().contains(UserRole.CONTRACT_SECOND_PARTY)) {
            this.contractSignature.setTutorSignDate(Instant.now());
        }
        return this;
    }

    // auxiliary methods that determine the payment amount & expiry data of contract upon contract creation
    private ContractPayment calculatePaymentAmount(LessonInformation lessonInfo) {
        double amount = lessonInfo.getRatePerSession() * lessonInfo.getSessionNumPerWeek() * 0.2;
        return new ContractPayment(amount);
    }
}
