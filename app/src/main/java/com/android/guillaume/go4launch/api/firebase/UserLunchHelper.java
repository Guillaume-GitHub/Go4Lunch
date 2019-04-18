package com.android.guillaume.go4launch.api.firebase;

import com.android.guillaume.go4launch.model.DatabaseUserLunchDoc;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserLunchHelper {

    private static final String COLLECTION_NAME = "userLunch";
    private static final String SUBCOLLECTION_NAME = "history";
    private static final String dateField = "date";
    private static final String placeIdField = "placeID";
    private static final String usersField = "users";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, z");
    private static String currentDate = dateFormat.format(Calendar.getInstance().getTime());

    // --- COLLECTION REFERENCE ---

    private static CollectionReference getCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createDocument(String restaurantPlaceID) {
        // Get userID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Create new Object corresponding to database structure
        DatabaseUserLunchDoc userLunchDoc = new DatabaseUserLunchDoc(currentDate,userID,restaurantPlaceID);

        return UserLunchHelper.getCollection().document(userID).collection(SUBCOLLECTION_NAME).document().set(userLunchDoc);
    }

    // --- GET ---
    public static Task<QuerySnapshot> getDocument(){
        // Get userID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return UserLunchHelper.getCollection().document(userID).collection(SUBCOLLECTION_NAME).whereEqualTo(dateField, currentDate).get();
    }


    // --- UPDATE ---
    public static Task<Void> updateDocument(String documentID ,DatabaseUserLunchDoc databaseUserLunchDoc){
        // Get userID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return UserLunchHelper.getCollection().document(userID).collection(SUBCOLLECTION_NAME).document(documentID).set(databaseUserLunchDoc);
    }

    // --- DELETE ---

    public static Task<Void> deleteDocument(String documentID){
        // Get userID
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return UserLunchHelper.getCollection().document(userID).collection(SUBCOLLECTION_NAME).document(documentID).delete();
    }
}
