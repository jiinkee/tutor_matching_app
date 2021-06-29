package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.Subject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Converter for lesson information
 */
public class LessonInformationConverter extends Converter<LessonInformation> {

    public LessonInformationConverter() {
        super(LessonInformationConverter::toJson, LessonInformationConverter::fromJson);
    }

    // convert a LessonInformation object to JSON string
    private static String toJson(LessonInformation lessonInfo) {
        int sessionNum = lessonInfo.getSessionNumPerWeek();
        double rate = lessonInfo.getRatePerSession();
        boolean freeLesson = lessonInfo.hasFreeLesson();
        int preferredTutorCompetencyLvl = lessonInfo.getPreferredTutorCompetencyLevel();
        String lessonStartDate = lessonInfo.getLessonStartDateString();
        String lessonEndDate = lessonInfo.getLessonEndDateString();

        // parse day time information of lesson
        HashMap<String, ArrayList<LocalTime[]>> dayTime = lessonInfo.getSessionDayTime();
        JsonArray dayTimeJsonArray = new JsonArray();
        for ( HashMap.Entry<String, ArrayList<LocalTime[]>> entry : dayTime.entrySet()) {
            String day = entry.getKey();

            for (LocalTime[] time: entry.getValue()) {
                LocalTime startTime = time[0];
                LocalTime endTime = time[1];
                JsonObject dayTimeJsonObj = new JsonObject();
                dayTimeJsonObj.addProperty("day", day);
                dayTimeJsonObj.addProperty("startTime", startTime.toString());
                dayTimeJsonObj.addProperty("endTime", endTime.toString());
                dayTimeJsonArray.add(dayTimeJsonObj);
            }
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("preferredTutorCompetencyLvl", preferredTutorCompetencyLvl);
        jsonObject.addProperty("sessionNumPerWeek", sessionNum);
        jsonObject.addProperty("ratePerSession", rate);
        jsonObject.addProperty("freeLesson", freeLesson);
        jsonObject.add("dayTime", dayTimeJsonArray);
        jsonObject.addProperty("lessonStartDate", lessonStartDate);
        jsonObject.addProperty("lessonEndDate", lessonEndDate);

        String jsonString = jsonObject.toString();
        return jsonString;
    }

    // convert a JSON string to a LessonInformation object
    private static LessonInformation fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        JsonArray dayTimeJsonArray = convertedObject.get("dayTime").getAsJsonArray();
        HashMap<String, ArrayList<LocalTime[]>> dayTime = new HashMap<>();

        if (dayTimeJsonArray != null) {
            for (int i = 0; i < dayTimeJsonArray.size(); i++) {
                JsonObject dayTimeObject = dayTimeJsonArray.get(i) != null?  dayTimeJsonArray.get(i).getAsJsonObject(): null;
                if (dayTimeObject == null) continue;

                // get day and time data from json
                String day = dayTimeObject.get("day").getAsString();
                LocalTime startTime = LocalTime.parse(dayTimeObject.get("startTime").getAsString(), timeFormatter);
                LocalTime endTime = LocalTime.parse(dayTimeObject.get("endTime").getAsString(), timeFormatter);
                LocalTime[] time = {startTime, endTime};

                // add day and time data into hashmap
                if (dayTime.get(day)!=null) {
                    ArrayList<LocalTime[]> existingTimes = dayTime.get(day);
                    existingTimes.add(time);
                    dayTime.put(day, existingTimes);
                } else {
                    dayTime.put(day, new ArrayList<LocalTime[]>(Arrays.<LocalTime[]>asList(time)));
                }
            }
        }

        int sessionNum = convertedObject.get("sessionNumPerWeek").getAsInt();
        double rate = convertedObject.get("ratePerSession").getAsDouble();
        boolean freeLesson = convertedObject.get("freeLesson").getAsBoolean();
        int preferredTutorCompetencyLvl = convertedObject.get("preferredTutorCompetencyLvl").getAsInt();

        Instant lessonStartDate = Instant.parse(convertedObject.get("lessonStartDate").getAsString());
        Instant lessonEndDate = Instant.parse(convertedObject.get("lessonEndDate").getAsString());

        LessonInformation lessonInfo = new LessonInformation(sessionNum, dayTime, rate, freeLesson, lessonStartDate, lessonEndDate, preferredTutorCompetencyLvl);
        return lessonInfo;
    }

    public LessonInformation fromJsonStringToObject(String jsonString, Subject subject) {
        LessonInformation lessonInformation = fromJson(jsonString);
        lessonInformation.setSubject(subject);
        return lessonInformation;
    }
}
