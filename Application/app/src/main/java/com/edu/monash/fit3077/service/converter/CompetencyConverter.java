package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.Competency;
import com.edu.monash.fit3077.model.Subject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Converter for Competency
 */
public class CompetencyConverter extends Converter<Competency> {

    private static SubjectConverter subjectConverter;

    public CompetencyConverter() {
        super(CompetencyConverter::toJson, CompetencyConverter::fromJson);
        subjectConverter = new SubjectConverter();
    }

    // convert a Competency object to a JSON string, with Subject included
    private static String toJson(Competency competency) {
        String id = competency.getId();
        JsonObject subjectJson = subjectConverter.fromObjectToJsonObject(competency.getSubject());
        int level = competency.getLevel();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.add("subject", subjectJson);
        jsonObject.addProperty("level", level);

        String jsonString = jsonObject.toString();
        return jsonString;
    }

    // convert a JSON string into a Competency object
    private static Competency fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        String id = convertedObject.get("id").getAsString();

        Subject subject = null;
        if (convertedObject.get("subject") != null) {
            subject = subjectConverter.fromJsonStringToObject(convertedObject.get("subject").toString());
        }
        int level = convertedObject.get("level").getAsInt();

        Competency competency = new Competency(id, subject, level);
        return competency;
    }
}
