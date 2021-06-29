package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.ChatMessage;
import com.edu.monash.fit3077.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.time.Instant;

/**
 * Converter for chat message
 */
public class ChatMessageConverter extends Converter<ChatMessage> {
    private static UserConverter userConverter;

    public ChatMessageConverter() {
        super(ChatMessageConverter::toJson, ChatMessageConverter::fromJson);
        userConverter = new UserConverter();
    }

    // convert a ChatMessage object to a JSON string
    private static String toJson(ChatMessage message) {
        String posterId = message.getSender().getId();
        String datePosted = message.getPostDateString();
        String content = message.getMessageContent();
        String recipient = userConverter.fromObjectToJsonString(message.getRecipient());

        JsonObject additionalInfo = new JsonObject();
        additionalInfo.addProperty("recipient", recipient);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("posterId", posterId);
        jsonObject.addProperty("datePosted", datePosted);
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("additionalInfo", additionalInfo.toString());

        return jsonObject.toString();
    }

    // convert a JSON string into a ChatMessage object
    private static ChatMessage fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);
        String id = convertedObject.get("id").getAsString();

        User sender = userConverter.fromJsonStringToObject(convertedObject.get("poster").toString());
        JsonObject additionalInfo = convertedObject.getAsJsonObject("additionalInfo");
        User recipient = userConverter.fromJsonStringToObject(additionalInfo.get("recipient").toString());

        String postDateString = convertedObject.get("datePosted").getAsString();
        Instant postDate = Instant.parse(postDateString);

        JsonElement lastEditDateObj = convertedObject.get("dateLastEdited");
        Instant lastEditDate;
        if (lastEditDateObj.isJsonNull()) {
            lastEditDate = null;
        } else {
            lastEditDate = Instant.parse(lastEditDateObj.getAsString());
        }

        String content = convertedObject.get("content").getAsString();

        return new ChatMessage(id, sender, recipient, postDate, lastEditDate, content);
    }

    // generate the POST request body for chat message sending
    public String formMessagePost(String bidRequestId, ChatMessage message) {
        JsonObject messageObj = new JsonObject();
        messageObj.addProperty("bidId", bidRequestId);
        messageObj.addProperty("posterId", message.getSender().getId());
        messageObj.addProperty("datePosted", message.getPostDateString());
        messageObj.addProperty("content", message.getMessageContent());

        JsonObject additionalInfo = new JsonObject();
        JsonObject recipient = userConverter.fromObjectToJsonObject(message.getRecipient());
        additionalInfo.add("recipient", recipient);
        messageObj.add("additionalInfo", additionalInfo);

        return messageObj.toString();
    }
}
