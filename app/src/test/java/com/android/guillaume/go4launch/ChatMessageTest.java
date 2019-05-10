package com.android.guillaume.go4launch;

import com.android.guillaume.go4launch.model.ChatMessage;
import com.android.guillaume.go4launch.model.User;

import org.junit.Test;

import static org.junit.Assert.*;

public class ChatMessageTest {

    private User user = new User("6Hdnd683_63Gdgsjhd7", "MARTIN", "martin@gmail.com",null, null, null);
    private ChatMessage message = new ChatMessage("It's message test content", user);

    @Test
    public void setAndGetMessageText() {
        message.setMessageText("Junit message test");
        assertEquals("Junit message test",message.getMessageText());
    }

    @Test
    public void setAndGetUserSender() {
        User user = new User("jdh9834jfdhg673_Hhdy", "PAUL", "paul@gmail.com",null, null, null);
        message.setUserSender(user);
        assertEquals(user,message.getUserSender());
    }
}