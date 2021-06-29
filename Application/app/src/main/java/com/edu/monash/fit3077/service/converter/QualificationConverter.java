package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.Qualification;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Converter for qualification
 */
public class QualificationConverter extends Converter<Qualification> {

    public QualificationConverter() {
        super(QualificationConverter::toJson, QualificationConverter::fromJson);
    }

    // convert a Qualification object to JSON string
    private static String toJson(Qualification qualification) {
        String id = qualification.getId();
        String title = qualification.getTitle();
        String description = qualification.getDescription();
        boolean isVerified = qualification.isVerified();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("title", title);
        jsonObject.addProperty("description", description);
        jsonObject.addProperty("verified", isVerified);

        String jsonString = jsonObject.toString();
        return jsonString;
    }

    // convert a JSON string to a Qualification object
    private static Qualification fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        String id = convertedObject.get("id").getAsString();
        String title = convertedObject.get("title").getAsString();
        String description = null;
        if (!convertedObject.get("description").isJsonNull()){
            description = convertedObject.get("description").getAsString();
        }
        boolean isVerified = convertedObject.get("verified").getAsBoolean();

        Qualification qualification = new Qualification(id, title, description, isVerified);

        return qualification;
    }
}
