package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.CloseBidRequest;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.OpenBidRequest;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Subject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Converter for bid request
 */
public class BidRequestConverter extends Converter<BidRequest>{
    private static UserConverter userConverter;
    private static  SubjectConverter subjectConverter;
    private static LessonInformationConverter lessonInfoConverter;
    private static BidOfferConverter bidOfferConverter;

    public BidRequestConverter() {
        super(BidRequestConverter::fromBidRequestToJson, BidRequestConverter::fromJsonToBidRequest);
        userConverter = new UserConverter();
        subjectConverter = new SubjectConverter();
        lessonInfoConverter = new LessonInformationConverter();
        bidOfferConverter = new BidOfferConverter();
    }

    // convert a BidRequest object to a JSON string compatible with the API format
    private static String fromBidRequestToJson(BidRequest bidRequest) {
        String type = bidRequest.getTypeString();
        String initiatorId = bidRequest.getInitiator().getId();
        String dateCreated = bidRequest.getCreationDateString();
        String subjectId = bidRequest.getBidSubjectId();
        JsonObject additionalInfo =  fromAdditionalInfoToJsonObject(bidRequest);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", type);
        jsonObject.addProperty("initiatorId", initiatorId);
        jsonObject.addProperty("dateCreated", dateCreated);
        jsonObject.addProperty("subjectId", subjectId);
        jsonObject.add("additionalInfo", additionalInfo);

        return jsonObject.toString();
    }

    // convert the additionalInfo part of the bid request into a JSON string
    public static JsonObject fromAdditionalInfoToJsonObject(BidRequest bidRequest) {
        JsonObject lessonInfo = lessonInfoConverter.fromObjectToJsonObject(bidRequest.getRequiredLessonDetails());

        JsonObject winnerBid = null;
        if (bidRequest.getWinnerBidOffer() != null) {
            winnerBid = bidOfferConverter.fromObjectToJsonObject(bidRequest.getWinnerBidOffer());
        }

        JsonArray bidOffer = new JsonArray();
        if (bidRequest.getTutorBidOffers() != null) {
            bidOffer = bidOfferConverter.fromObjectsToJsonArray(bidRequest.getTutorBidOffers());
        }

        JsonObject additionalInfo = new JsonObject();
        additionalInfo.add("requiredLessonInfo", lessonInfo);
        additionalInfo.add("winnerBid", winnerBid);
        additionalInfo.add("tutorBidOffer", bidOffer);

        return additionalInfo;
    }

    // convert a JSON string into a bid request object
    private static BidRequest fromJsonToBidRequest(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        // retrieve the type of the bid request
        String type = convertedObject.get("type").getAsString();

        // start converting the attributes required to create BidRequest
        // get bid request id
        String id = convertedObject.get("id").getAsString();

        // get bid request initiator
        Student initiator = (Student) userConverter.fromJsonStringToObject(convertedObject.get("initiator").toString());

        // get bid request creation date
        String creationDateString = convertedObject.get("dateCreated").getAsString();
        Instant creationDate = Instant.parse(creationDateString);

        // get bid request closed down date
        JsonElement closedDownDateJsonObj = convertedObject.get("dateClosedDown");
        Instant closedDownDate;
        if (closedDownDateJsonObj.isJsonNull()) {
            closedDownDate = null;
        } else {
            closedDownDate = Instant.parse(closedDownDateJsonObj.getAsString());
        }

        // get subject
        Subject subject = subjectConverter.fromJsonStringToObject(convertedObject.get("subject").toString());

        // get additional information
        JsonObject additionalInfo = convertedObject.getAsJsonObject("additionalInfo");
        // get lesson information
        LessonInformation requiredLessonDetails = lessonInfoConverter.fromJsonObjectToObject(additionalInfo.getAsJsonObject("requiredLessonInfo"));
        requiredLessonDetails.setSubject(subject);

        // get winner bid
        JsonElement winnerBidJsonObj = additionalInfo.get("winnerBid");
        BidOffer winnerBid;
        if (winnerBidJsonObj.isJsonNull()) {
            winnerBid = null;
        } else {
            winnerBid = bidOfferConverter.fromJsonStringToObject(winnerBidJsonObj.toString(), subject);
        }

        // get a list of tutor bid offer
        ArrayList<BidOffer> bidOffers = bidOfferConverter.fromJsonArrayToObjects(additionalInfo.getAsJsonArray("tutorBidOffer"), subject);

        // create different instances of bid request object depends on the bid type
        BidRequest convertedBidRequest = null;
        if (type.equals(BidRequestType.bidTypeToString(BidRequestType.OPEN))) {
            convertedBidRequest = new OpenBidRequest(id, initiator, creationDate, closedDownDate, requiredLessonDetails, bidOffers, winnerBid);
        } else {
            convertedBidRequest = new CloseBidRequest(id, initiator, creationDate, closedDownDate, requiredLessonDetails, bidOffers, winnerBid);
        }
        return convertedBidRequest;
    }
}
