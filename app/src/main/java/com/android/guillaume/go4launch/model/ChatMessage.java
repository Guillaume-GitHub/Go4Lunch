package com.android.guillaume.go4launch.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatMessage {


    private Date dateCreated;
    private String messageText;
    private User userSender;

    public ChatMessage() {
    }

    public ChatMessage(String messageText,User userSender) {
        this.messageText = messageText;
        this.userSender = userSender;
    }


    @ServerTimestamp public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }
}
