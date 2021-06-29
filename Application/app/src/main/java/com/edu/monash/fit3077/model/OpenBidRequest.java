
package com.edu.monash.fit3077.model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class OpenBidRequest extends BidRequest {

    // constructor used for open bid request data retrieval
    public OpenBidRequest(String id, Student initiator, Instant creationDate, Instant closedDownDate, LessonInformation requiredLessonDetails,
                          ArrayList<BidOffer> bidOffers, BidOffer winnerOffer) {
        super(id, initiator, creationDate, closedDownDate, requiredLessonDetails, bidOffers, winnerOffer);
        type = BidRequestType.OPEN;
    }

    // constructor used for open bid request creation
    public OpenBidRequest(Student initiator, LessonInformation requiredLessonDetails) {
        super(initiator, requiredLessonDetails);
        type = BidRequestType.OPEN;
    }

    // method that determines whether the open bid request has expired
    // an open bid request will only stay alive for 30 minutes
    @Override
    public boolean isStillAlive() {
        return Duration.between(this.creationDate, Instant.now()).toMinutes() <= 30;
    }

    // method that calculates the expiry date time of the open bid request
    public Instant getValidUntil() {
        LocalDateTime dateCreated = LocalDateTime.ofInstant(creationDate, ZoneOffset.ofHours(0));
        LocalDateTime validUntil = dateCreated.plusMinutes(30);
        return validUntil.toInstant(ZoneOffset.ofHours(0));
    }

}
