package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.Competency;
import com.edu.monash.fit3077.model.Qualification;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Converter for user
 */
public class UserConverter extends Converter<User>{

    private static QualificationConverter qualificationConverter;
    private static CompetencyConverter competencyConverter;

    public UserConverter() {
        super(UserConverter::toJson, UserConverter::fromJson);
        qualificationConverter = new QualificationConverter();
        competencyConverter = new CompetencyConverter();
    }

    // convert a User object to JSON string
    private static String toJson(User user) {
        String id = user.getId();
        String givenName = user.getGivenName();
        String familyName = user.getFamilyName();
        String userName = user.getUserName();

        JsonArray competencies = null;
        if (user.getCompetencies() != null) {
            competencies = competencyConverter.fromObjectsToJsonArray(user.getCompetencies());
        }

        // convert qualifications of tutor
        JsonArray qualifications = null;
        if (user instanceof Tutor && ((Tutor) user).getQualifications() != null) {
            qualifications = qualificationConverter.fromObjectsToJsonArray(((Tutor) user).getQualifications());
        }

        // convert subscribed bid IDs of tutor
        JsonObject additionalInfo = new JsonObject();
        JsonArray subscribedBidsId = null;
        if (user instanceof Tutor && ((Tutor) user).getSubscribedBidsId() != null && !((Tutor) user).getSubscribedBidsId().isEmpty()) {
            subscribedBidsId = new Gson().fromJson(((Tutor) user).getSubscribedBidsId().toString(), JsonArray.class);
            additionalInfo.add("subscribedBids", subscribedBidsId);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("givenName", givenName);
        jsonObject.addProperty("familyName", familyName);
        jsonObject.addProperty("userName", userName);
        jsonObject.addProperty("isStudent", user instanceof Student);
        jsonObject.addProperty("isTutor", user instanceof Tutor);
        jsonObject.add("competencies", competencies);
        jsonObject.add("qualifications", qualifications);
        jsonObject.add("additionalInfo", additionalInfo);

        String jsonString = jsonObject.toString();
        return jsonString;
    }

    // convert a JSON string to a User object
    private static User fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);
        String id = convertedObject.get("id").getAsString();
        String userName = convertedObject.get("userName").getAsString();
        String givenName = convertedObject.get("givenName").getAsString();
        String familyName = convertedObject.get("familyName").getAsString();
        boolean isStudent = convertedObject.get("isStudent").getAsBoolean();
        boolean isTutor = convertedObject.get("isTutor").getAsBoolean();

        ArrayList<Competency> competencies = null;
        if (!(convertedObject.get("competencies")==null) && !(convertedObject.get("competencies").isJsonNull())) {
            competencies = competencyConverter.fromJsonStringToObjects(convertedObject.get("competencies").toString());
        }

        // create different type of user based on user role
        User user = null;
        if (isStudent) {
            user = new Student(id, userName, givenName, familyName, competencies);
        } else if (isTutor) {
            // get tutor's qualifications
            ArrayList<Qualification> qualifications = null;
            if (!(convertedObject.get("qualifications")==null) && !(convertedObject.get("qualifications").isJsonNull())) {
                qualifications = qualificationConverter.fromJsonStringToObjects(convertedObject.get("qualifications").toString());
            }

            // get tutor's subscribed bids from additional info
            ArrayList<String> subscribedBids = null;
            JsonObject tutorAdditionalInfo = convertedObject.getAsJsonObject("additionalInfo");
            if (tutorAdditionalInfo != null && !tutorAdditionalInfo.isJsonNull()) {
                JsonArray tutorSubscribedBids = tutorAdditionalInfo.getAsJsonArray("subscribedBids");
                Type subscribedBidListType = new TypeToken<ArrayList<String>>(){}.getType();
                subscribedBids = new Gson().fromJson(tutorSubscribedBids, subscribedBidListType);
            }

            user = new Tutor(id, userName, givenName, familyName, competencies, qualifications, subscribedBids);
        }
        return user;
    }

}
