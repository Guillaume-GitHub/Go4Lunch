package com.android.guillaume.go4launch.view;

import android.content.Context;
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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmate_image) ImageView workmateImage;
    @BindView(R.id.workmate_text) TextView workmateText;
    @BindView(R.id.workmate_fragment_container) CardView itemContainer;

    private String TAG = this.getClass().getSimpleName();

    private String restaurantName;
    private boolean isRestaurantSelected = false;
    private Context context;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, z");


    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context = itemView.getContext();
    }

    // Use to display users list in WorkmateFragment.class
    public void updateView(User user, RequestManager glide){


        if(user.getLunch() != null){
            String dateOfDay = dateFormat.format(Calendar.getInstance().getTime());

            if (user.getLunch().getDate() != null && user.getLunch().getDate().equals(dateOfDay)){
                this.isRestaurantSelected = true;
            }
        }

        if(user.getUrlPicture() != null){
            getPicture(user.getUrlPicture(), glide);
        }

        this.applyNoRestaurantSelectedTheme();

        this.getDisplayItemText(user);

    }

    private void getPicture(String url,RequestManager glide){
        try{
            glide.load(url)
                    .circleCrop()
                    .into(this.workmateImage);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private void applyNoRestaurantSelectedTheme(){
        if(this.isRestaurantSelected){
            this.itemContainer.setAlpha(1f);
        }
        else{
            this.itemContainer.setAlpha(0.5f);
        }

    }

    private void getDisplayItemText(User user){

        String text;

        if (isRestaurantSelected){
           text = user.getUserName() + " " + "(" + user.getLunch().getName() + ")";
           this.workmateText.setText(text);
        }
        else{
            text =  user.getUserName() + " " + context.getResources().getString(R.string.restaurant_has_not_chosen);
            this.workmateText.setText(text);
        }
    }

    //***********************************************************************//

    // Use to display users list in DetailActivity.class
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
