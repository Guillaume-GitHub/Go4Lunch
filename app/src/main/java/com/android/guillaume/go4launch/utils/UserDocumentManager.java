package com.android.guillaume.go4launch.utils;

import android.util.Log;

import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.UserLunch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class UserDocumentManager {

    private static String TAG = UserDocumentManager.class.getSimpleName();

    public static void updateUserLunch(UserLunch userLunch){
        UserHelper.updateUserLunch(userLunch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User document was Updated");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: User document wasn't updated", e);
            }
        });
    }

    public static void addUserLikeItem(List<String> stringList, String placeId) {
        List<String> idList = stringList;
        if (idList != null){
            idList.add(placeId);
        }
        else {
            idList = new ArrayList<>();
            idList.add(placeId);
        }

        UserHelper.updateUserLike(idList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User Like list was added");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: User Like item wasn't added", e);
            }
        });
    }

    public static void removeUserLikeItem(List<String> idList, String placeId){
        if (idList != null){
            idList.remove(placeId);
        }

        UserHelper.updateUserLike(idList).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: User Like item was removed");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: User Like list item wasn't removed", e);
            }
        });

    }

}
