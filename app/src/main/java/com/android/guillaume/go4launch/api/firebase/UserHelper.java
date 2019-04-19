package com.android.guillaume.go4launch.api.firebase;

import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.model.UserLunch;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";
    private static final String lunchField = "lunch";
    private static final String likeField = "like";
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, z");
    public static String currentDate = dateFormat.format(Calendar.getInstance().getTime());

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(FirebaseUser firebaseUser) {
        User userToCreate = new User(firebaseUser);
        return UserHelper.getUsersCollection().document(firebaseUser.getUid()).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllUsers(){
        return UserHelper.getUsersCollection().get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUserLunch(UserLunch userLunch) {
        return UserHelper.getUsersCollection().document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(lunchField, userLunch);
    }

    public static Task<Void> updateUserLike(List<String> placeIdList){
        return UserHelper.getUsersCollection().document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(likeField,placeIdList);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }
}
