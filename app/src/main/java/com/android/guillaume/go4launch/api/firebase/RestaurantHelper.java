package com.android.guillaume.go4launch.api.firebase;

import com.android.guillaume.go4launch.model.DatabaseRestaurantDoc;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RestaurantHelper {

    private static final String COLLECTION_NAME = "restaurants";
    private static final String dateField = "date";
    private static final String placeIdField = "placeID";
    private static final String usersField = "users";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, z");
    private static String currentDate = dateFormat.format(Calendar.getInstance().getTime());

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createNewRestaurantDocument(String placeID) {
        List<String> userIdList = new ArrayList<>();
        // Get userID
        userIdList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
        // Create new Object corresponding to database structure
        DatabaseRestaurantDoc restaurant = new DatabaseRestaurantDoc(currentDate,placeID,userIdList);

        return RestaurantHelper.getCollection().document().set(restaurant);
    }

    // --- GET ---

    public static Task<QuerySnapshot> getAllRestaurantDocumentsAtDate(Date date){
       // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, z");
        String formatDate = dateFormat.format(date);
        return RestaurantHelper.getCollection()
                .whereEqualTo(dateField, formatDate)
                .get();
    }

    public static Task<QuerySnapshot> getRestaurantDocumentAtDate(Date date, String placeID){
       // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, z");
        String formatDate = dateFormat.format(date);
        return RestaurantHelper.getCollection()
                .whereEqualTo(dateField, formatDate)
                .whereEqualTo(placeIdField, placeID)
                .get();
    }

    // --- UPDATE ---
    public static Task<Void> updateRestaurantDocument(String documentID, DatabaseRestaurantDoc databaseRestaurantDoc){
        return RestaurantHelper.getCollection().document(documentID).set(databaseRestaurantDoc);
    }
}
