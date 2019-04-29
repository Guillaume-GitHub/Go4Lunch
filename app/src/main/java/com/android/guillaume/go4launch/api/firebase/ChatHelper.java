package com.android.guillaume.go4launch.api.firebase;

import android.util.Log;

import com.android.guillaume.go4launch.model.ChatMessage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";
    private static final String SUBCOLLECTION_NAME = "messages";
    private static final String userChatDoc = "user";
    private static final String senderIdField = "senderId";
    private static final String receiverIdField = "receiverId";
    private static final String textField = "messageText";
    private static final String dateField = "dateCreated";
    private static final String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final int messageLimit = 30;

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> sendMessage(String userReceiverId, String message) {
        ChatMessage chatMessage = new ChatMessage(senderId,userReceiverId,message);
        return ChatHelper.getCollection().document(userChatDoc).collection(SUBCOLLECTION_NAME).document().set(chatMessage);
    }

    // --- GET ---

    public static Query getMessage(String workmateUserId){
        Log.d("TAG", "getMessage:  receiverid = " + workmateUserId);
        Log.d("TAG", "getMessage:  senderid = " + senderId);
        return ChatHelper.getCollection().document(userChatDoc).collection(SUBCOLLECTION_NAME)
                .whereEqualTo(receiverIdField,workmateUserId)
                .whereEqualTo(senderIdField,senderId)
                .orderBy(dateField)
                .limit(messageLimit);
    }

    // --- UPDATE ---

    public static Task<Void> updateMessage(String documentId,String messageText){
        return ChatHelper.getCollection().document(userChatDoc).collection(SUBCOLLECTION_NAME).document(documentId).update(textField,messageText);
    }

    // --- DELETE ---

    public static Task<Void> deleteMessage(String documentId) {
        return ChatHelper.getCollection().document(userChatDoc).collection(SUBCOLLECTION_NAME).document(documentId).delete();
    }
}
