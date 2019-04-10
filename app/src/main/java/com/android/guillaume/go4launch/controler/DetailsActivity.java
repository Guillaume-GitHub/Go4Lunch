package com.android.guillaume.go4launch.controler;

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
import com.android.guillaume.go4launch.api.places.RestoDetailsClient;
import com.android.guillaume.go4launch.model.detailsRestaurant.DetailsRestaurant;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
            this.fetchRestaurantDetails(this.placeId);
            this.displayRestaurantInfos();

        }
        else {
            Toast.makeText(this, R.string.displayDetailsError, Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    public void displayRestaurantInfos(){

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

    //******************************* APPLY STYLE **************************//
    private void setButtonStyle(){

        if(this.detailsRestaurant.getResult().getPhoneNumber() != null
            || !this.detailsRestaurant.getResult().getPhoneNumber().isEmpty()){
            this.callBtn.setTextColor(this.iconColor);
            setIconButtonColor(this.callBtn.getCompoundDrawables());
        }
        else{
            this.callBtn.setEnabled(false);
            this.callBtn.setAlpha(0.5f);
        }

        if(this.detailsRestaurant.getResult().getWebsite() != null
                && !this.detailsRestaurant.getResult().getWebsite().isEmpty()){
            this.websiteBtn.setTextColor(this.iconColor);
            setIconButtonColor(this.websiteBtn.getCompoundDrawables());
        }
        else {
            this.websiteBtn.setEnabled(false);
            this.websiteBtn.setAlpha(0.5f);
        }

        likeBtn.setTextColor(iconColor);
    }

    private void setIconButtonColor(Drawable[] compoundDrawables){
        for (Drawable drawable : compoundDrawables) {
            if (drawable != null){
                drawable.setColorFilter(getResources().getColor(R.color.go4lunchPrimary), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    //******************************* CLICK BUTTON EVENT **************************//

    @OnClick(R.id.details_activity_floating_btn)
    public void onSelectRestaurantClick(){
        Log.d(TAG, "onSelectRestaurantClick: ");
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
        //TODO : LIKE RESTAURANT
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
}
