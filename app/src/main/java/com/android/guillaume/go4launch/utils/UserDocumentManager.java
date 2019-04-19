package com.android.guillaume.go4launch.utils;

import android.util.Log;

import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.UserLunch;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
}
