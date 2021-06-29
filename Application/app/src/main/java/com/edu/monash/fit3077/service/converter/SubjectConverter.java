package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.Subject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Converter for subject
 */
public class SubjectConverter extends Converter<Subject>{

    public SubjectConverter() {
        super(SubjectConverter::toJson, SubjectConverter::fromJson);
    }

    // convert a Subject object to JSON string
    private static String toJson(Subject subject) {
        String id = subject.getId();
        String name = subject.getName();
        String description = subject.getDescription();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("name", name);
        jsonObject.addProperty("description", description);

        String jsonString = jsonObject.toString();
        return jsonString;
    }

    // convert a JSON string to a Subject object
    private static Subject fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        String id = convertedObject.get("id").getAsString();
        String name = convertedObject.get("name").getAsString();
        String description = convertedObject.get("description").getAsString();

        Subject subject = new Subject(id, name, description);
        return subject;
    }
}
