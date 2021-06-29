package com.edu.monash.fit3077.service.converter;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Base class for Converter
 * @param <T> data type of object to be converted to json String
 */
public class Converter<T> {
    private final Function<T, String> fromObjectToJsonString;
    private final Function<String, T> fromJsonStringToObject;
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    public static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm")
            .toFormatter();

    public Converter(final Function<T, String> fromObjectToJsonString, final Function<String, T> fromJsonStringToObject) {
        this.fromObjectToJsonString = fromObjectToJsonString;
        this.fromJsonStringToObject = fromJsonStringToObject;
    }

    // convert from an object to a JSON string
    public final String fromObjectToJsonString(final T object) {
        return fromObjectToJsonString.apply(object);
    }

    // convert from a JSON string to an object of type T
    public final T fromJsonStringToObject(final String jsonString) {
        return fromJsonStringToObject.apply(jsonString);
    }

    // convert from a JsonObject to an object of type T
    public final T fromJsonObjectToObject(JsonObject jsonObject) {
        return fromJsonStringToObject(jsonObject.toString());
    }

    // convert from JsonArray to an ArrayList of objects of type T
    public final ArrayList<T> fromJsonArrayToObjects(JsonArray jsonArray) {
        return fromJsonStringToObjects(jsonArray.toString());
    }

    // convert from an object of type T to a JsonObject
    public final JsonObject fromObjectToJsonObject(T object) {
        return new Gson().fromJson(fromObjectToJsonString(object), JsonObject.class);
    }

    // convert from an ArrayList of object of type T to a JsonArray
    public final JsonArray fromObjectsToJsonArray(ArrayList<T> objects) {
        return new Gson().fromJson(fromObjectsToJsonString(objects), JsonArray.class);
    }

    // convert from a JsonString to an ArrayList of Objects of type T
    public final ArrayList<T> fromJsonStringToObjects(String jsonString) {
        if (jsonString == "") return new ArrayList<T>();
        JsonArray jsonArray = new Gson().fromJson((java.lang.String) jsonString, JsonArray.class);
        ArrayList<T> objects = new ArrayList<>();
        for (int i=0; i <jsonArray.size(); i++) {
            objects.add(fromJsonStringToObject((String) jsonArray.get(i).toString()));
        }
        return objects;
    }

    // convert from an ArrayList of object of type T to a single JsonString
    public final String fromObjectsToJsonString(final ArrayList<T> objects) {
        if (objects.size() <= 0) {
            return "[]";
        }
        java.lang.String result = "[";
        result += fromObjectToJsonString(objects.get(0));
        result += ",";
        for (int i = 1 ; i < objects.size(); i++) {
            result += fromObjectToJsonString(objects.get(i));
            result += ",";
        }
        result = result.substring(0, result.length()-1);
        result += "]";
        return result;
    }
}
