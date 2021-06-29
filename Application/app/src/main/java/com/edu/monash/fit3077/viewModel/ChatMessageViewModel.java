package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.edu.monash.fit3077.model.ChatMessage;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.ChatMessageRepository;
import com.edu.monash.fit3077.service.repository.UserRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ChatMessageViewModel extends BaseViewModel {
    private ChatMessageRepository chatRepo;
    // observable live data that get a list of logged in user's chat messages in the private chat room
    private MediatorLiveData<MyResponse<ArrayList<ChatMessage>>> chatMessages;
    // observable live data that stores the status of message sending
    private MediatorLiveData<MyResponse<ChatMessage>> messageSendingStatus;

    public ChatMessageViewModel() {
        chatRepo = new ChatMessageRepository();
        chatMessages = new MediatorLiveData<>();
        messageSendingStatus = new MediatorLiveData<>();
    }

    public void getChatMessages(String bidRequestId, ArrayList<String> chatParticipantsId) {
        chatMessages.addSource(chatRepo.getChatMessages(bidRequestId, chatParticipantsId), chatMessagesResponse -> {
            // order the messages in chronological order
            if (chatMessagesResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                // sort by date the chat message is posted
                Collections.sort(chatMessagesResponse.data, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage chat1, ChatMessage chat2) {
                        return chat1.getPostDate().compareTo(chat2.getPostDate());
                    }
                });
            }
            chatMessages.setValue(chatMessagesResponse);
        });
    }

    public void sendChatMessage(String bidRequestId, User sender, User recipient, String message) {
        ChatMessage newMessage = new ChatMessage(sender, recipient, Instant.now(), message);
        messageSendingStatus.addSource(chatRepo.sendChatMessage(bidRequestId, newMessage), newMessageResponse -> {
            messageSendingStatus.setValue(newMessageResponse);
        });
    }

    public LiveData<MyResponse<ArrayList<ChatMessage>>> getChatMessages() {
        return chatMessages;
    }

    public LiveData<MyResponse<ChatMessage>> getMessageSendingStatus() {
        return messageSendingStatus;
    }
}
