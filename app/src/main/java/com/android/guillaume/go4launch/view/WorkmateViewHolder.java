package com.android.guillaume.go4launch.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.User;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmate_image) ImageView workmateImage;
    @BindView(R.id.workmate_text) TextView workmateText;

    private String TAG = this.getClass().getSimpleName();

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateView(User user, RequestManager glide){

            this.workmateText.setText(user.getUserName());

            try{
                glide.load(user.getUrlPicture())
                        .circleCrop()
                        .into(this.workmateImage);
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }
    }


    public void updateView(String userID, final RequestManager glide){
        Log.d(TAG, "updateView: ");

        UserHelper.getUser(userID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null){
                    updateView(user,glide);
                }
                Log.d(TAG, "onSuccess: User is null");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "onFailure: Cannot fetch User", e);
            }
        });
    }
}
