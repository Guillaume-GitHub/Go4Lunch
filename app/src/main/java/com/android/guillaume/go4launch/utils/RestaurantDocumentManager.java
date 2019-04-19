package com.android.guillaume.go4launch.utils;

import android.util.Log;

import com.android.guillaume.go4launch.api.firebase.RestaurantHelper;
import com.android.guillaume.go4launch.model.DatabaseRestaurantDoc;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

public class RestaurantDocumentManager {

    private static String TAG = RestaurantDocumentManager.class.getSimpleName();

    public static void saveRestaurantDocChanges(final String placeID){
        Log.d(TAG, "saveRestaurantDocChanges: ");
        setEventListener(placeID);

        RestaurantHelper.getRestaurantDocumentAtDate(Calendar.getInstance().getTime(), placeID)
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Check if result is superior to 0
                // doc exist ->
                if(queryDocumentSnapshots.getDocuments().size() > 0){
                    Log.d(TAG, "onSuccess: This Restaurant document already exist");

                    DatabaseRestaurantDoc restaurant = queryDocumentSnapshots.getDocuments().get(0).toObject(DatabaseRestaurantDoc.class);
                    String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Check if userID are already in the doc
                    if (restaurant.getUsers() != null && !restaurant.getUsers().contains(userID)) {
                        Log.d(TAG, "onSuccess: This userID isn't present in this document");
                        updateRestaurantDocument(queryDocumentSnapshots.getDocuments().get(0).getId(),restaurant);
                    }
                    Log.d(TAG, "onSuccess: This userID is already present in this document");
                    // Do nothing...
                }
                // Doc doesn't exist ->
                else{
                    Log.d(TAG, "onSuccess: This document doesn't exist");
                    createRestaurantDocument(placeID);
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Cannot fetch restaurants documents from Firebase", e);
            }
        });
    }

    private static void updateRestaurantDocument(final String documentID, DatabaseRestaurantDoc restaurantDocModel){
        Log.d(TAG, "updateRestaurantDocument: ");
        RestaurantHelper.updateRestaurantDocument(documentID,restaurantDocModel)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Restaurant document " + documentID + " is up to date");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Restaurant document " + documentID + " isn't up to date",e);
            }
        });
    }

    private static void createRestaurantDocument(String placeID){
        Log.d(TAG, "createRestaurantDocument: ");
        RestaurantHelper.createNewRestaurantDocument(placeID)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: New Restaurant document was correctly created");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onSuccess: New Restaurant document wasn't created",e);
            }
        });
    }

    private static void deleteRestaurantDocument(final String documentID){
        Log.d(TAG, "deleteRestaurantDocument: ");
        RestaurantHelper.deleteRestaurantDocument(documentID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Restaurant document " + documentID + " was correctly deleted");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onSuccess: Restaurant document " + documentID + " wasn't deleted",e);
                    }
                });
    }

    public static void cleanUserIdInAllRestaurantDocuments(){
        RestaurantHelper.getAllRestaurantDocumentsAtDate(Calendar.getInstance().getTime())
        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DatabaseRestaurantDoc> docs = queryDocumentSnapshots.toObjects(DatabaseRestaurantDoc.class);
                //check if list aren't empty
                if (docs.size() >= 1) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    for (int i = 0; i < docs.size(); i++) {
                        if (docs.get(i).getUsers().contains(userId)) {
                            Log.d(TAG, "onEvent: This doc contain entry to delete");

                            if (docs.get(i).getUsers().size() > 1) {
                                List<String> list = docs.get(i).getUsers();
                                list.remove(userId);

                                docs.get(i).setUsers(list);

                                updateRestaurantDocument(queryDocumentSnapshots.getDocuments().get(i).getId(), docs.get(i));
                            }
                        }
                        // LOOP ....
                    }
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Cannot fetch Restaurants documents");
            }
        });
    }

    private static void setEventListener(final String placeId){
        Log.d(TAG, "setEventListener: ");
        RestaurantHelper.getQueryOfAllRestaurantDocuments(Calendar.getInstance().getTime())
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                Log.d(TAG, "onEvent: Listen Work !");
                List<DatabaseRestaurantDoc> docs = queryDocumentSnapshots.toObjects(DatabaseRestaurantDoc.class);
                //check if list aren't empty
                if (docs.size() >=1){
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    for (int i = 0; i < docs.size(); i++) {
                        if(!docs.get(i).getPlaceID().equals(placeId) && docs.get(i).getUsers().contains(userId)){
                            Log.d(TAG, "onEvent: This doc contain entry to delete");

                            if(docs.get(i).getUsers().size() > 1){
                                List<String> list = docs.get(i).getUsers();
                                list.remove(userId);

                                docs.get(i).setUsers(list);

                                updateRestaurantDocument(queryDocumentSnapshots.getDocuments().get(i).getId(),docs.get(i));
                            }
                            else {
                                deleteRestaurantDocument(queryDocumentSnapshots.getDocuments().get(i).getId());
                            }
                        }
                        // LOOP ....
                    }
                }
                // No other documents find

            }
        });
    }

}
