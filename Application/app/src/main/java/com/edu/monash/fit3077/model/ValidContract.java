package com.edu.monash.fit3077.model;

import java.time.Duration;
import java.time.Instant;

public class ValidContract extends Contract{
    // constructor for contract data retrieval
    public ValidContract(String id, Student firstParty, Tutor secondParty, Instant creationDate, Instant expiryDate,
                         ContractSignature contractSignature, LessonInformation lessonInfo, ContractPayment paymentInfo) {
        super(id, firstParty, secondParty, creationDate, expiryDate, contractSignature,lessonInfo, paymentInfo);
        status = ContractStatus.VALID;
    }

    public boolean isAlmostExpired() {
        // contract is almost expired when it has less than 1 month (30 days) until its expiry date
        return Duration.between(Instant.now(), this.expiryDate).toDays() <= 30;
    }
}
