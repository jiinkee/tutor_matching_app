package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.time.Instant;

public class ChatMessage implements Serializable {
    private String id, messageContent;
    private User sender, recipient;
    private Instant postDate, lastEditDate;

    // constructor for chat message retrieval
    public ChatMessage(String id, User sender, User recipient, Instant postDate, Instant lastEditDate, String content) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.postDate = postDate;
        this.lastEditDate = lastEditDate;
        this.messageContent = content;
    }

    // constructor for chat message posting
    public ChatMessage(User sender, User recipient, Instant postDate, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.postDate = postDate;
        this.messageContent = content;
    }

    // GETTER methods
    public User getSender() {
        return sender;
    }

    public String getSenderFullName() {
        return sender.getFullName();
    }

    public User getRecipient() {
        return recipient;
    }

    public String getRecipientFullName() {
        return recipient.getFullName();
    }

    public Instant getPostDate() {
        return postDate;
    }

    public String getPostDateString() {
        return postDate.toString();
    }

    public String getMessageContent() {
        return messageContent;
    }

}
