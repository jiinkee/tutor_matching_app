package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.Competency;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.Qualification;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.model.Tutor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Converter for bid offer
 */
public class BidOfferConverter extends Converter<BidOffer> {
    private static UserConverter userConverter;
    private static LessonInformationConverter lessonInfoConverter;

    public BidOfferConverter() {
        super(BidOfferConverter::toJson, BidOfferConverter::fromJson);
        userConverter = new UserConverter();
        lessonInfoConverter = new LessonInformationConverter();
    }

    // convert a BidOffer object to a JSON string compatible with the API request body format
    private static String toJson(BidOffer bidOffer) {
        // convert various bid offer attribute to required format to be stored in json object
        JsonObject bidder = userConverter.fromObjectToJsonObject(bidOffer.getBidder());
        Instant creationDate = bidOffer.getCreationDate();
        JsonObject lessonInfo = lessonInfoConverter.fromObjectToJsonObject(bidOffer.getLessonInfo());

        // create a json object and add properties
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("bidder", bidder);
        jsonObject.addProperty("dateCreated", creationDate.toString());
        jsonObject.add("lessonInfo", lessonInfo);

        // return json string
        return jsonObject.toString();
    }

    // convert a JSON string into a BidOffer object
    private static BidOffer fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        Tutor bidder =  (Tutor) userConverter.fromJsonStringToObject(convertedObject.get("bidder").toString());
        String creationDateString = convertedObject.get("dateCreated").getAsString();
        Instant creationDate = Instant.parse(creationDateString);
        LessonInformation lessonInfo = lessonInfoConverter.fromJsonStringToObject(convertedObject.get("lessonInfo").toString());

        return new BidOffer(bidder, creationDate, lessonInfo);
    }

    public BidOffer fromJsonStringToObject(String jsonString, Subject subject) {
        BidOffer bidOffer = fromJson(jsonString);
        bidOffer.setBidSubject(subject);
        return bidOffer;
    }

    public ArrayList<BidOffer> fromJsonArrayToObjects(JsonArray jsonArray, Subject subject) {
        ArrayList<BidOffer> bidOffers = fromJsonArrayToObjects(jsonArray);
        for (int i=0; i<bidOffers.size(); i++) {
            bidOffers.get(i).setBidSubject(subject);
        }
        return bidOffers;
    }
}
