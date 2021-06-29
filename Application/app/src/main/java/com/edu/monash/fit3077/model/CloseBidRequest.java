package com.edu.monash.fit3077.model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CloseBidRequest extends BidRequest{

    // constructor used for close bid request data retrieval
    public CloseBidRequest(String id, Student initiator, Instant creationDate, Instant closedDownDate,
                           LessonInformation requiredLessonDetails, ArrayList<BidOffer> bidOffers, BidOffer winnerOffer) {
        super(id, initiator, creationDate, closedDownDate, requiredLessonDetails, bidOffers, winnerOffer);
        type = BidRequestType.CLOSE;
    }

    // constructor used for close bid request creation
    public CloseBidRequest(Student initiator, LessonInformation requiredLessonDetails) {
        super(initiator, requiredLessonDetails);
        type = BidRequestType.CLOSE;
    }

    // method that determines whether the close bid request has expired
    // a close bid request will only stay alive for 7 days (1 week)
    @Override
    public boolean isStillAlive() {
        return Duration.between(this.creationDate, Instant.now()).toDays() <= 7;
    }

    // method that calculates the expiry date time of the close bid request
    @Override
    public Instant getValidUntil() {
        LocalDateTime dateCreated = LocalDateTime.ofInstant(creationDate, ZoneOffset.ofHours(0));
        LocalDateTime validUntil = dateCreated.plusDays(7);
        return validUntil.toInstant(ZoneOffset.ofHours(0));
    }

}
