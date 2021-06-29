package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.time.Instant;

public class BidOffer implements Serializable {
    private Tutor bidder;
    private Instant creationDate;
    private LessonInformation lessonInfo;

    // constructor for bid offer data retrieval
    public BidOffer(Tutor bidder, Instant creationDate, LessonInformation lessonInfo) {
        this.bidder = bidder;
        this.creationDate = creationDate;
        this.lessonInfo = lessonInfo;
    }

    // constructor for bid offer creation
    public BidOffer(Tutor bidder, LessonInformation lessonInfo) {
        this.bidder = bidder;
        this.creationDate = Instant.now();
        this.lessonInfo = lessonInfo;
    }

    // GETTER methods
    public Tutor getBidder() {
        return bidder;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public String getCreationDateString() {
        return creationDate.toString();
    }

    public LessonInformation getLessonInfo() {
        return lessonInfo;
    }

    public Competency getTutorCompetency(String subjectId) {
        return bidder.getCompetencyForSubject(subjectId);
    }

    // SETTER
    public void setBidSubject(Subject subject) {
        lessonInfo.setSubject(subject);
    }

    // method to set the lesson end date based on specified duration in months
    public void setLessonEndDate(int duration) {
        if (lessonInfo!=null && lessonInfo.getLessonStartDate()!=null) {
            lessonInfo.setLessonEndDate(duration);
        }
    }
}
