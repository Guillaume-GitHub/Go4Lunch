package com.android.guillaume.go4launch.api.firebase;

import com.android.guillaume.go4launch.model.ChatMessage;
import com.android.guillaume.go4launch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class ChatHelper {

    private static final String COLLECTION_NAME = "chat";
    private static final String MESSAGE_COLLECTION_NAME = "messages";
    private static final String roomDoc = "room";
    private static final String textField = "messageText";
    private static final String groupUserIdField = "groupUsersId";
    private static final String dateField = "dateCreated";
    private static final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final int messageLimit = 50;

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> saveMessage(String message) {
        User currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());
        ChatMessage chatMessage = new ChatMessage(message,currentUser);
        return ChatHelper.getCollection().document(roomDoc)
                .collection(MESSAGE_COLLECTION_NAME)
                .document().set(chatMessage);
    }

    // --- GET ---
    public static Query getMessage(){
        return ChatHelper.getCollection().document(roomDoc)
                .collection(MESSAGE_COLLECTION_NAME)
                .orderBy(dateField)
                .limit(messageLimit);
    }

    // --- UPDATE ---

    public static Task<Void> updateMessage(String documentId,ChatMessage message){
        return ChatHelper.getCollection().document(roomDoc).collection(MESSAGE_COLLECTION_NAME).document(documentId).update("dateCreated",message.getDateCreated());
    }
    // --- DELETE ---

    public static Task<Void> deleteMessage(String documentId) {
        return ChatHelper.getCollection().document(roomDoc)
                .collection(MESSAGE_COLLECTION_NAME)
                .document(documentId).delete();
    }

}
