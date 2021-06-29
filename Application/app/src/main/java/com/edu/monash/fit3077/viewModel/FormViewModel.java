package com.edu.monash.fit3077.viewModel;

import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.service.converter.Converter;
import com.edu.monash.fit3077.service.repository.BidRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * View model class responsible for form
 */
public abstract class FormViewModel extends BaseViewModel {

    // stored the lesson information and bid request type corresponds to options in bid form
    protected LessonInformation preferredLessonInformation;
    protected BidRequestType preferredBidRequestType;
    protected int preferredLessonDuration;
    // send and get bid related data from bid repository
    protected BidRepository bidRepository;

    /**
     * Constructor
     */
    public FormViewModel() {
        bidRepository = new BidRepository();
        preferredLessonInformation = new LessonInformation();
        // default lesson duration is 6 months
        preferredLessonDuration = 6;
    }

    /** GETTER methods for default form options **/
    public ArrayList<Integer> getNumSessionPerWeekOptions() {
        Integer minNumOfSessionPerWeek = LessonInformation.MIN_NUM_OF_SESSION_PER_WEEK;
        Integer maxNumOfSessionPerWeek = LessonInformation.MAX_NUM_OF_SESSION_PER_WEEK;
        // create a range of integer based on the min and max number of session per week
        ArrayList<Integer> numOfSessionPerWeekOptions = new ArrayList<>();
        for (Integer i=minNumOfSessionPerWeek; i <= maxNumOfSessionPerWeek; i++) {
            numOfSessionPerWeekOptions.add(i);
        }
        // set the first item in the options as preferred number of session per week if it has not been set yet
        if (preferredLessonInformation.getSessionNumPerWeek() <= 0) setNumSessionPerWeek(numOfSessionPerWeekOptions.get(0));

        return numOfSessionPerWeekOptions;
    }

    public ArrayList<Integer> getDurationOptions() {
        return LessonInformation.LESSON_DURATION_OPTIONS;
    }

    public HashMap<String, ArrayList<LocalTime[]>> getLessonDayTimeOptions() {
        // set number of lesson day time options, by default 1
        int numOfOptions = preferredLessonInformation.getSessionNumPerWeek() <= 0? 1: preferredLessonInformation.getSessionNumPerWeek();
        HashMap<String, ArrayList<LocalTime[]>> lessonDayTimeOptions = new HashMap<>();

        // create start and end time pair
        final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("HH:mm")
                .toFormatter();
        LocalTime time = LocalTime.parse("00:00", Converter.timeFormatter);
        LocalTime[] defaultTime = {time, time};

        // create a list of start end time
        ArrayList<LocalTime[]> defaultTimes = new ArrayList<>();
        for (int i=0; i<numOfOptions; i++) {
            defaultTimes.add(defaultTime);
        }
        lessonDayTimeOptions.put("Mon", defaultTimes);

        return lessonDayTimeOptions;
    }

    public ArrayList<String> getLessonDayOptions() {
        return LessonInformation.LESSON_DAY_OPTIONS;
    }

    public ArrayList<BidRequestType> getBidTypeOptions() {
        // get a list of bid request types options
        ArrayList<BidRequestType> bidRequestTypes = new ArrayList<>(Arrays.asList(BidRequestType.values()));
        // set default bid request type
        if (preferredBidRequestType == null) setBidRequestType(bidRequestTypes.get(0));
        return bidRequestTypes;
    }

    public ArrayList<Subject> getSubjectOptions() {
        return super.getLoggedInUser().getSubjects();
    }

    public ArrayList<Integer> getPreferredTutorCompetencyLvlOptions() {
        int minPreferredTutorCompetencyLvl = BidRequest.MIN_PREFERRED_TUTOR_COMPETENCY_LVL;
        int maxPreferredTutorCompetencyLvl = BidRequest.MAX_PREFERRED_TUTOR_COMPETENCY_LVL;

        // create a range of integer based on the minimum and maximum tutor competency level
        ArrayList<Integer> preferredTutorCompetencyLvlOptions = new ArrayList<>();
        for (int i = minPreferredTutorCompetencyLvl; i <= maxPreferredTutorCompetencyLvl; i++) {
            preferredTutorCompetencyLvlOptions.add(i);
        }

        // set default preferred tutor competency level
        if (preferredLessonInformation.getPreferredTutorCompetencyLevel() <= 0) preferredLessonInformation.setPreferredTutorCompetencyLevel(preferredTutorCompetencyLvlOptions.get(0));

        return preferredTutorCompetencyLvlOptions;
    }

    /** GETTER for selected form options **/
    public String getBidRequestTypeString() {
        return preferredBidRequestType == null? "": preferredBidRequestType.toString();
    }

    public String getSubjectString() {
        return preferredLessonInformation.getSubject().toString();
    }

    public String getPreferredTutorCompetencyLvlString() {
        return preferredLessonInformation.getPreferredTutorCompetencyLevelString();
    }

    public Integer getNumSessionPerWeek() {
        return preferredLessonInformation.getSessionNumPerWeek();
    }

    public Integer getLessonDuration() {
        return preferredLessonDuration;
    }

    public HashMap<String, ArrayList<LocalTime[]>> getLessonDayTime() {
        HashMap<String, ArrayList<LocalTime[]>> dayTime;

        if (preferredLessonInformation.getSessionDayTime() != null) {
            dayTime = preferredLessonInformation.getSessionDayTime();
        } else {
            dayTime = getLessonDayTimeOptions();
        }

        return dayTime;
    }

    public String getRatePerSessionString() {
        return preferredLessonInformation.getRatePerSessionString();
    }

    public Boolean getHasFreeLesson() {
        return preferredLessonInformation.hasFreeLesson();
    }

    public String getLessonStartDateString() {
        return preferredLessonInformation.getLessonStartDateString();
    }

    /** GETTER for lesson information based on the selected bid form options **/
    protected LessonInformation getPreferredLessonInformation() throws Exception{
        boolean hasSetBidSubject = preferredLessonInformation.getSubject() != null;
        boolean hasSetPreferredTutorCompetencyLvl = preferredLessonInformation.getPreferredTutorCompetencyLevel() > 0;
        boolean hasSetNumSessionPerWeek = preferredLessonInformation.getSessionNumPerWeek() > 0;
        boolean hasSetRatePerSession = preferredLessonInformation.getRatePerSession() > 0.0;
        boolean hasSetLessonStartDate = preferredLessonInformation.getLessonStartDate() != null;

        // set lesson end date based on duration and lesson start date selected
        preferredLessonInformation.setLessonEndDate(preferredLessonDuration);

        // check if user has selected required options in bid form
        if (!(hasSetBidSubject && hasSetPreferredTutorCompetencyLvl && hasSetNumSessionPerWeek && hasSetRatePerSession && hasSetLessonStartDate)) {
            throw new Exception("All fields in this form must be filled up correctly.");
        }

        // checking for unselected bid day time options
        for ( HashMap.Entry<String, ArrayList<LocalTime[]>> entry : preferredLessonInformation.getSessionDayTime().entrySet()) {
            for (LocalTime[] time: entry.getValue()) {
                LocalTime startTime = time[0];
                LocalTime endTime = time[1];
                if (startTime.toString().equals("00.00") || endTime.toString().equals(("00:00")))
                    throw new Exception("All fields in this form must be filled up correctly.");

            }
        }

        // return lesson information if all fields have been completed
        return preferredLessonInformation;
    }

    // SETTER methods for form option
    public void setNumSessionPerWeek(Integer numSessionPerWeek) {
        preferredLessonInformation.setSessionNumPerWeek(numSessionPerWeek);
    }

    public void setLessonDayTime(HashMap<String, ArrayList<LocalTime[]>> lessonDayTime) {
        preferredLessonInformation.setSessionDayTime(lessonDayTime);
    }

    public void setRatePerSession(String ratePerSessionString) {
        if (ratePerSessionString!=null && !ratePerSessionString.equals("")) {
            preferredLessonInformation.setRatePerSession(Double.parseDouble(ratePerSessionString));
        }
    }

    public void setHasFreeLesson(Boolean hasFreeLesson) {
        preferredLessonInformation.setHasFreeLesson(hasFreeLesson);
    }

    public void setLessonStartDate(String lessonStartDate) {
        if (lessonStartDate!=null) {
            LocalDate startDate = LocalDate.parse(lessonStartDate, Converter.dateFormatter);
            Instant startDateInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            preferredLessonInformation.setLessonStartDate(startDateInstant);
        }
    }

    public void setLessonDuration(int lessonDuration) {
        this.preferredLessonDuration = lessonDuration;
    }


    public void setBidRequestType(BidRequestType selectedBidRequestType) {
        preferredBidRequestType = selectedBidRequestType;
    }

    public void setSubject(Subject selectedSubject) {
        preferredLessonInformation.setSubject(selectedSubject);
    }

    public void setPreferredTutorCompetencyLvl(int selectedTutorCompetencyLvl) {
        preferredLessonInformation.setPreferredTutorCompetencyLevel(selectedTutorCompetencyLvl);
    }
}
