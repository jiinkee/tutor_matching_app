package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;

public abstract class BidRequest implements Serializable {
    protected String id;
    protected Student initiator;
    protected Instant creationDate, closedDownDate;
    protected LessonInformation requiredLessonDetails;
    protected ArrayList<BidOffer> tutorBidOffers;
    protected BidOffer winnerBidOffer;
    protected BidRequestStatus status;
    protected BidRequestType type = null;
    public static int MIN_PREFERRED_TUTOR_COMPETENCY_LVL = 1;
    public static int MAX_PREFERRED_TUTOR_COMPETENCY_LVL = 6;

    // constructor used for bid request data retrieval
    public BidRequest(String id, Student initiator, Instant creationDate, Instant closedDownDate, LessonInformation lessonInfo, ArrayList<BidOffer> bidOffers, BidOffer winnerOffer) {
        this.id = id;
        this.initiator = initiator;
        this.creationDate = creationDate;
        this.closedDownDate = closedDownDate;
        this.requiredLessonDetails = lessonInfo;
        this.tutorBidOffers = bidOffers;
        this.winnerBidOffer = winnerOffer;

        if (this.closedDownDate == null) {
            this.status = BidRequestStatus.ALIVE;
        } else {
            this.status = BidRequestStatus.CLOSED_DOWN;
        }
    }

    // constructor used for bid request creation
    public BidRequest(Student initiator, LessonInformation lessonInfo) {
        this.initiator = initiator;
        this.creationDate = Instant.now();
        this.requiredLessonDetails = lessonInfo;

        if (this.closedDownDate == null) {
            this.status = BidRequestStatus.ALIVE;
        } else {
            this.status = BidRequestStatus.CLOSED_DOWN;
        }
    }

    // GETTER methods
    public String getId() {
        return id;
    }

    public Student getInitiator() {
        return initiator;
    }

    public String getBidName() {
        return initiator.getFullName() + " - " + requiredLessonDetails.getSubjectName();
    }

    public Subject getSubject() {
        return requiredLessonDetails.getSubject();
    }

    public Instant getCreationDate() { return creationDate; }

    public String getCreationDateString() {
        return creationDate.toString();
    }

    public BidRequestStatus getStatus() {
        return status;
    }

    public BidRequestType getType() {
        return type;
    }

    public String getTypeString() {
        return BidRequestType.bidTypeToString(type);
    }

    public LessonInformation getRequiredLessonDetails() {
        return requiredLessonDetails;
    }

    public ArrayList<BidOffer> getTutorBidOffers() {
        return tutorBidOffers;
    }

    public BidOffer getWinnerBidOffer() {
        return winnerBidOffer;
    }

    public String getBidSubjectId() {
        return requiredLessonDetails.getSubjectId();
    }

    public int getPreferredTutorCompetencyLevel() {
        return requiredLessonDetails.getPreferredTutorCompetencyLevel();
    }

    public String getPreferredTutorCompetencyLevelString() {
        return requiredLessonDetails.getPreferredTutorCompetencyLevelString();
    }

    // SETTER methods
    // method to add a new bid offer object
    public void addBidOffer(BidOffer bidOffer) {
        tutorBidOffers.add(bidOffer);
    }

    // method to update an existing bid offer object
    public void updateBidOffer(BidOffer updatedBidOffer) {
        if (tutorBidOffers != null) {
            for (int i = 0; i < tutorBidOffers.size();i++) {
                if (tutorBidOffers.get(i).getBidder().getId().equals(updatedBidOffer.getBidder().getId())) {
                    tutorBidOffers.set(i, updatedBidOffer);
                    break;
                }
            }
        }
    }

    // method to update the winner bid offer of the bid request
    public void setWinnerBidOffer(BidOffer bidOffer) {
        winnerBidOffer = bidOffer;
    }

    public abstract boolean isStillAlive();

    public abstract Instant getValidUntil();
}
