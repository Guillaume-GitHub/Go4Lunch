package com.android.guillaume.go4launch.controler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.api.places.RestoDetailsClient;
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.model.UserLunch;
import com.android.guillaume.go4launch.model.detailsRestaurant.DetailsRestaurant;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.utils.RestaurantDocumentManager;
import com.android.guillaume.go4launch.utils.UserDocumentManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.details_activity_floating_btn) FloatingActionButton buttonSelectRestaurant;
    @BindView(R.id.details_activity_restaurant_call_btn) Button callBtn;
    @BindView(R.id.details_activity_restaurant_like_btn) Button likeBtn;
    @BindView(R.id.details_activity_restaurant_website_btn) Button websiteBtn;
    @BindView(R.id.details_activity_restaurant_image) ImageView imageView;
    @BindView(R.id.details_activity_restaurant_name) TextView nameText;
    @BindView(R.id.details_activity_restaurant_address) TextView addressText;
    @BindView(R.id.details_activity_ratingBar) RatingBar ratingBar;

    private final String TAG = this.getClass().getSimpleName();

    private String placeId;
    private String name;
    private String address;
    private String photoRef;
    private Double rating;

    private Disposable disposable;
    private DetailsRestaurant detailsRestaurant;
    private int iconColor;
    private boolean isSelected = false;
    private boolean isLiked = false;
    private boolean isSelectChange;
    private boolean isLikeChange;
    private User user;

    //CONST
    private static final String PLACE_ID = "PLACE_ID";
    private static final String NAME = "NAME";
    private static final String ADDRESS = "ADDRESS";
    private static final String RATING = "RATING";
    private static final String PHOTO = "PHOTO";

    public DetailsActivity() {

    }

    public static Intent getDetailsActivityIntent(Context context, RestoResult restaurant){
        Intent intent= new Intent(context,DetailsActivity.class);
        // Set data with intent
        intent.putExtra(PLACE_ID, restaurant.getPlaceId());
        intent.putExtra(NAME,restaurant.getName());
        intent.putExtra(ADDRESS,restaurant.getVicinity());
        intent.putExtra(RATING,restaurant.getRating());

        if (restaurant.getRestoPhotos() != null)
            intent.putExtra(PHOTO,restaurant.getRestoPhotos().get(0).getPhotoReference());
        else
            intent.putExtra(PHOTO,"");

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null){
            this.placeId = intent.getStringExtra(PLACE_ID);
            Log.d(TAG, "placeID : " + this.placeId);

            this.name = intent.getStringExtra(NAME);
            Log.d(TAG, "name : " + this.name);

            this.address = intent.getStringExtra(ADDRESS);
            Log.d(TAG, "address : " + this.address);

            this.rating = intent.getDoubleExtra(RATING,-1);
            Log.d(TAG, "address : " + this.rating);

            this.photoRef = intent.getStringExtra(PHOTO);
            Log.d(TAG, "photo : " + this.photoRef);

            this.iconColor = getResources().getColor(R.color.go4lunchPrimary);


            this.fetchUserLunch();
            this.fetchRestaurantDetails(this.placeId);
            this.displayRestaurantInfos();

        }
        else {
            Toast.makeText(this, R.string.displayDetailsError, Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        this.saveChangesToFirebase();
        super.onBackPressed();
    }

    //****************************** FETCH DATAS ******************************//

    private void fetchUserLunch() {
        UserHelper.getUser(FirebaseAuth.getInstance().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            displayUserInfo(user);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: Cannot fetch User infos from Firebase", e);
                    }
                });
    }


    private void fetchRestaurantDetails(String placeId){
        RestoDetailsClient.getInstance().detailsRestaurant(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<DetailsRestaurant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(DetailsRestaurant restaurant) {
                        detailsRestaurant = restaurant;
                        setButtonStyle();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });
    }

    //********************************* DISPLAY DATAS *****************************//

    private void displayUserInfo(User user){
        try{
            // Check if this restaurant is already select this day
            if (user.getLunch().getPlaceID().equals(placeId)
                    &&  user.getLunch().getDate().equals(UserHelper.currentDate)) {
                setSelectedStyle(true);
            }
            // Check if this restaurant is already liked
            if (user.getLike() != null && user.getLike().contains(placeId)) {
                setLikedStyle(true);
            }
        }
        catch (NullPointerException e){
            Log.w(TAG, "onSuccess: Cannot Read userLunch data ",e);
        }
    }

    private void displayRestaurantInfos(){

        this.nameText.setText(this.name);
        this.addressText.setText(this.address);

        this.rating = ((this.rating * 3) /5);
        this.ratingBar.setRating((this.rating.floatValue()));

        if (this.photoRef != null || !this.photoRef.isEmpty())
            try {
                String base = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=";
                String end = "&key=" + getApplicationContext().getResources().getString(R.string.default_web_api_key);

                Glide.with(this)
                        .load(base + this.photoRef + end)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(this.imageView);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
    }

    //******************************* APPLY STYLE **************************//
    private void setButtonStyle(){

        // Style Call Button
        if(this.detailsRestaurant.getResult().getPhoneNumber() != null
            || !this.detailsRestaurant.getResult().getPhoneNumber().isEmpty()){
            this.callBtn.setTextColor(this.iconColor);
            setIconButtonColor(this.callBtn.getCompoundDrawables(),R.color.go4lunchPrimary);
        }
        else{
            this.callBtn.setEnabled(false);
            this.callBtn.setAlpha(0.5f);
        }

        // Style Website Button
        if(this.detailsRestaurant.getResult().getWebsite() != null
                && !this.detailsRestaurant.getResult().getWebsite().isEmpty()){
            this.websiteBtn.setTextColor(this.iconColor);
            setIconButtonColor(this.websiteBtn.getCompoundDrawables(),R.color.go4lunchPrimary);
        }
        else {
            this.websiteBtn.setEnabled(false);
            this.websiteBtn.setAlpha(0.5f);
        }

        // Style Like Button
        likeBtn.setTextColor(iconColor);
    }

    private void setIconButtonColor(Drawable[] compoundDrawables, int color){
        for (Drawable drawable : compoundDrawables) {
            if (drawable != null){
                drawable.setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void setSelectedStyle(Boolean selected){
        if(selected){
            this.buttonSelectRestaurant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.go4lunchSelectedGreen)));
            this.buttonSelectRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.filled_check_tick_icon));
            this.isSelected = true;
        }
        else {
            this.buttonSelectRestaurant.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.go4lunchAccent)));
            this.buttonSelectRestaurant.setImageDrawable(getResources().getDrawable(R.drawable.round_restaurant_menu_white_24));
            this.isSelected = false;
        }
    }


    private void setLikedStyle(Boolean liked){
        Log.d(TAG, "setLikeBtnStyle: ");

        if (liked){
            likeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.filled_star_icon),null,null);
            setIconButtonColor(this.likeBtn.getCompoundDrawables(), R.color.go4lunchPrimary);
            this.isLiked = true;
        }
        else{
            likeBtn.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.filled_star_border_icon),null,null);
            this.isLiked = false;
        }
    }

    //******************************* CLICK BUTTON EVENT **************************//

    @OnClick(R.id.details_activity_floating_btn)
    public void onSelectRestaurantClick(){
        // Detect if user change is choice
        this.detectSelectChange();

        // Apply style
        if(!isSelected){
            this.setSelectedStyle(true);
        }
        else {
            this.setSelectedStyle(false);
        }
    }

    @OnClick(R.id.details_activity_restaurant_call_btn)
    public void onCallBtnClick(){
        Log.d(TAG, "onCallBtnClick: ");

        if (this.callBtn.isEnabled()){
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel: " + this.detailsRestaurant.getResult().getPhoneNumber()));
                startActivity(intent);
            }
            catch (ActivityNotFoundException e){
                e.printStackTrace();
            }
        }
        // ... Do Nothing
    }

    @OnClick(R.id.details_activity_restaurant_like_btn)
    public void onLikeBtnClick(){
        Log.d(TAG, "onLikeBtnClick: ");
        this.detectLikeChange();

        // Apply style
        if(!isLiked){
            this.setLikedStyle(true);
        }
        else {
            this.setLikedStyle(false);
        }

    }

    @OnClick(R.id.details_activity_restaurant_website_btn)
    public void onWebsiteBtnClick(){
        Log.d(TAG, "onWebsiteBtnClick: ");

        if (this.websiteBtn.isEnabled()){
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(this.detailsRestaurant.getResult().getWebsite()));
                startActivity(intent);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        // ..... do nothing
    }

    //**************************** DETECT CHANGES *************************//

    private void detectSelectChange(){
        this.isSelectChange = !this.isSelectChange;
    }

    private void detectLikeChange(){
        this.isLikeChange = !this.isLikeChange;
    }
    //************************* SAVING DATA - FIREBASE *******************//

    private void saveChangesToFirebase(){
        this.saveRestaurantChanges();
        this.saveUserChanges();
    }

    private void saveRestaurantChanges(){
        // Restaurant changes
        if(this.isSelectChange){
            if(this.isSelected){
                Log.d(TAG, "saveChangesToFirebase: User selected this restaurant");
                UserDocumentManager.updateUserLunch(new UserLunch(
                        UserHelper.dateFormat.format(Calendar.getInstance().getTime()),
                        this.placeId,
                        this.name,
                        this.address));

                RestaurantDocumentManager.saveRestaurantDocChanges(this.placeId);
            }
            else{
                Log.d(TAG, "saveChangesToFirebase: User unselected this restaurant");
                // remove doc
                UserHelper.updateUserLunch(null);
                RestaurantDocumentManager.cleanUserIdInAllRestaurantDocuments();
            }
        }
    }

    private void saveUserChanges(){
        // User changes
        if (this.isLikeChange) {
            if (this.isLiked){
                UserDocumentManager.addUserLikeItem(this.user.getLike(), this.placeId);
            }
            else {
                UserDocumentManager.removeUserLikeItem(this.user.getLike(), this.placeId);
            }
        }
    }


}
