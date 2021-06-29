package com.edu.monash.fit3077.service.repository;

import androidx.lifecycle.MutableLiveData;

import com.edu.monash.fit3077.model.ChatMessage;
import com.edu.monash.fit3077.service.ChatMessageAPIService;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.Util;
import com.edu.monash.fit3077.service.WebServiceGenerator;
import com.edu.monash.fit3077.service.converter.ChatMessageConverter;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatMessageRepository {
    private ChatMessageAPIService chatAPI;
    private ChatMessageConverter chatMessageConverter;

    public ChatMessageRepository() {
        chatAPI = WebServiceGenerator.createService(ChatMessageAPIService.class);
        chatMessageConverter = new ChatMessageConverter();
    }

    // get a list of all chat messages attached to the given bid request
    public MutableLiveData<MyResponse<ArrayList<ChatMessage>>> getChatMessages(String bidRequestId, ArrayList<String> chatParticipantsId) {
        final MutableLiveData<MyResponse<ArrayList<ChatMessage>>> chatMessagesResponse = new MutableLiveData<>();

        Call<ResponseBody> chatMessageRequestCall = chatAPI.getChatMessages(bidRequestId);
        chatMessageRequestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String bidRequestResponse = response.body().string();
                        JsonObject bidRequestObj = new Gson().fromJson(bidRequestResponse, JsonObject.class);
                        JsonArray messages = bidRequestObj.getAsJsonArray("messages");
                        ArrayList<ChatMessage> chatMessages = filterChatMessages(messages, chatParticipantsId);
                        // set mutable live data
                        chatMessagesResponse.setValue(MyResponse.successResponse(chatMessages));

                    } catch (IOException e) {
                        chatMessagesResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        chatMessagesResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        chatMessagesResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                chatMessagesResponse.setValue(MyResponse.errorResponse(Objects.requireNonNull(t.getMessage()), null));
            }
        });

        return chatMessagesResponse;
    }

    // filter the chat message list so that only the chat participants' messages are remained
    private ArrayList<ChatMessage> filterChatMessages(JsonArray messages, ArrayList<String> chatParticipantsId) {
        ArrayList<JsonObject> filteredChatMessages = new ArrayList<>();

        // filter messages using student & tutor id
        // the message's poster & recipient must be one of the two of them
        for (JsonElement message : messages) {
            JsonObject messageObj = message.getAsJsonObject();
            // get message poster
            JsonObject posterObj = messageObj.get("poster").getAsJsonObject();
            String posterId = posterObj.get("id").getAsString();

            // get message recipient
            JsonObject additionalInfoObj = messageObj.get("additionalInfo").getAsJsonObject();
            JsonObject recipientObj = additionalInfoObj.get("recipient").getAsJsonObject();
            String recipientId = recipientObj.get("id").getAsString();

            // check poster & recipient IDs against the given student & tutor IDs
            if (chatParticipantsId.contains(posterId) && chatParticipantsId.contains(recipientId)) {
                filteredChatMessages.add(messageObj);
            }
        }

        // convert messages from Json string to ChatMessage object
        return chatMessageConverter.fromJsonStringToObjects(filteredChatMessages.toString());
    }

    // post a new chat message
    public MutableLiveData<MyResponse<ChatMessage>> sendChatMessage(String bidRequestId, ChatMessage message) {
        final MutableLiveData<MyResponse<ChatMessage>> sendChatMessageResponse = new MutableLiveData<>();

        // preparing the request body
        String messageString = chatMessageConverter.formMessagePost(bidRequestId, message);

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), messageString);

        Call<ResponseBody> bidRequestCall = chatAPI.sendChatMessage(body);
        bidRequestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBodyString = response.body().string();
                        ChatMessage responseBodyMessage = chatMessageConverter.fromJsonStringToObject(responseBodyString);
                        sendChatMessageResponse.setValue(MyResponse.successResponse(responseBodyMessage));
                    } catch (IOException e) {
                        sendChatMessageResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        sendChatMessageResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        sendChatMessageResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                sendChatMessageResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return sendChatMessageResponse;
    }

}
