package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LessonInformation implements Serializable {
    private int sessionNumPerWeek;
    private HashMap<String, ArrayList<LocalTime[]>> sessionDayTime;
    private double ratePerSession;
    private boolean hasFreeLesson;
    private Instant lessonStartDate;
    private Instant lessonEndDate;
    private int preferredTutorCompetencyLevel;
    private Subject subject;
    public static Integer MIN_NUM_OF_SESSION_PER_WEEK = 1;
    public static Integer MAX_NUM_OF_SESSION_PER_WEEK = 7;
    public static ArrayList<String> LESSON_DAY_OPTIONS = new ArrayList<>(Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
    public static ArrayList<Integer> LESSON_DURATION_OPTIONS = new ArrayList<>(Arrays.asList(3,6,12,24,36));

    // constructor for lesson information retrieval (subject data has to be set after LessonInformation object is constructed)
    public LessonInformation(int sessionNum, HashMap<String, ArrayList<LocalTime[]>> dayTime,
                             double rate, boolean freeLesson, Instant lessonStartDate, Instant lessonEndDate, int preferredTutorCompetencyLevel) {
        this.sessionNumPerWeek = sessionNum;
        this.sessionDayTime = dayTime;
        this.ratePerSession = rate;
        this.hasFreeLesson = freeLesson;
        this.lessonStartDate = lessonStartDate;
        this.lessonEndDate = lessonEndDate;
        this.preferredTutorCompetencyLevel = preferredTutorCompetencyLevel;
    }

    // constructor for lesson information creation
    public LessonInformation(int sessionNum, HashMap<String, ArrayList<LocalTime[]>> dayTime,
                             double rate, boolean freeLesson, Instant lessonStartDate, Instant lessonEndDate, Subject subject, int preferredTutorCompetencyLevel) {
        this.sessionNumPerWeek = sessionNum;
        this.sessionDayTime = dayTime;
        this.ratePerSession = rate;
        this.hasFreeLesson = freeLesson;
        this.lessonStartDate = lessonStartDate;
        this.lessonEndDate = lessonEndDate;
        this.subject = subject;
        this.preferredTutorCompetencyLevel = preferredTutorCompetencyLevel;
    }

    // constructor for lesson information creation in bid form view model
    public LessonInformation() {}

    // SETTER
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    // GETTER methods
    public Instant getLessonStartDate() {
        return lessonStartDate;
    }

    public String getLessonStartDateString() {
        if (lessonStartDate!=null) {
            return lessonStartDate.toString();
        } else {
            return "";
        }
    }

    public Instant getLessonEndDate() {
        return lessonEndDate;
    }

    public String getLessonEndDateString() {
        if (lessonEndDate != null) {
            return lessonEndDate.toString();
        } else {
            return "";
        }
    }

    public int getSessionNumPerWeek() {
        return sessionNumPerWeek;
    }

    public String getSessionNumPerWeekString() {
        return Integer.toString(sessionNumPerWeek);
    }

    public HashMap<String, ArrayList<LocalTime[]>> getSessionDayTime() {
        return sessionDayTime;
    }

    // combine all session day time to a single string
    public String getSessionDayTimeString() {
        StringBuilder strRepresentation = new StringBuilder();
        for (Map.Entry<String, ArrayList<LocalTime[]>> entry : sessionDayTime.entrySet()) {
            String day = entry.getKey();

            for (LocalTime[] time: entry.getValue()) {
                String startTime = time[0].toString();
                String endTime = time[1].toString();
                strRepresentation.append(day).append(": ").append(startTime).append(" to ").append(endTime).append("\n");
            }
        }
        return strRepresentation.toString();
    }

    public double getRatePerSession() {
        return ratePerSession;
    }

    public String getRatePerSessionString() {
        return Double.toString(ratePerSession);
    }

    public boolean hasFreeLesson() {
        return hasFreeLesson;
    }

    public String hasFreeLessonString() {
        return hasFreeLesson ? "true" : "false";
    }

    public String getPreferredTutorCompetencyLevelString() {
        return Integer.toString(preferredTutorCompetencyLevel);
    }

    public int getPreferredTutorCompetencyLevel() {
        return preferredTutorCompetencyLevel;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getSubjectId() {
        return subject.getId();
    }

    public String getSubjectName() {
        if (subject != null) {
            return subject.getName();
        } else {
            return "";
        }
    }

    public int getDuration() {
        // default duration is set to 6
        int duration=6;
        if (lessonStartDate != null && lessonEndDate!=null) {
            LocalDate startDate = LocalDateTime.ofInstant(this.lessonStartDate , ZoneId.systemDefault()).toLocalDate();
            LocalDate endDate = LocalDateTime.ofInstant(this.lessonEndDate , ZoneId.systemDefault()).toLocalDate();
            long durationInLong = ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), endDate.withDayOfMonth(1));
            duration = Math.toIntExact(durationInLong);
        }
        return duration;
    }

    // SETTER METHODS

    public void setSessionNumPerWeek(int sessionNumPerWeek) {
        this.sessionNumPerWeek = sessionNumPerWeek;
    }

    public void setSessionDayTime(HashMap<String, ArrayList<LocalTime[]>> sessionDayTime) {
        this.sessionDayTime = sessionDayTime;
    }

    public void setRatePerSession(double ratePerSession) {
        this.ratePerSession = ratePerSession;
    }

    public void setHasFreeLesson(boolean hasFreeLesson) {
        this.hasFreeLesson = hasFreeLesson;
    }

    public void setLessonStartDate(Instant lessonStartDate) {
        this.lessonStartDate = lessonStartDate;
    }

    // set lesson end date based on specified duration in months starting from lesson start date
    public void setLessonEndDate(int duration) {
        if (this.lessonStartDate != null) {
            LocalDate startDate = LocalDateTime.ofInstant(this.lessonStartDate , ZoneId.systemDefault()).toLocalDate();
            LocalDate lessonEndDate = startDate.plusMonths(duration);
            this.lessonEndDate = lessonEndDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
    }

    public void setPreferredTutorCompetencyLevel(int preferredTutorCompetencyLevel) {
        this.preferredTutorCompetencyLevel = preferredTutorCompetencyLevel;
    }

}
