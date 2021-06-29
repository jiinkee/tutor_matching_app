package com.edu.monash.fit3077.model;

import java.time.Instant;

public class ExpiredContract extends Contract{
    public ExpiredContract(String id, Student firstParty, Tutor secondParty, Instant creationDate,
                           Instant expiryDate, ContractSignature contractSignature, LessonInformation lessonInfo,
                           ContractPayment paymentInfo) {
        super(id, firstParty, secondParty, creationDate, expiryDate, contractSignature, lessonInfo, paymentInfo);
        this.status = ContractStatus.EXPIRED;
    }

}
