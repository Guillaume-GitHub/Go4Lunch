package com.android.guillaume.go4launch.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.places.MatrixDistanceClient;
import com.android.guillaume.go4launch.api.places.RestoDetailsClient;
import com.android.guillaume.go4launch.model.DistanceMatrix.MatrixDistanceDistance;
import com.android.guillaume.go4launch.model.detailsRestaurant.OpeningHours;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.chip.Chip;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_view) CardView cardView;
    @BindView(R.id.restaurant_image) ImageView imageView;
    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.restaurant_address) TextView restaurantAddress;
    @BindView(R.id.restaurant_rating) RatingBar restaurantRating;
    @BindView(R.id.chip_hours) Chip chipHours;
    @BindView(R.id.chip_distance) Chip chipDistance;
    @BindView(R.id.chip_workmate) Chip chipWorkmate;

    private Disposable detailsDisposable;
    private Disposable matrixDisposable;
    private Context context;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context = itemView.getContext();
    }

    public void updateView(RestoResult resto, RequestManager glide, Location userPosition){
        Log.d("TAG", "updateView: ");

            this.restaurantName.setText(resto.getName());

            this.restaurantAddress.setText(resto.getVicinity());

            this.setImageView(resto,glide);

            this.setRestaurantRating(resto);

            this.setRestaurantHours(resto);

            this.setMatrixDistance(resto,userPosition);

            this.setNumberOfWorkmate(resto);
    }


    private void setRestaurantRating(RestoResult resto){
        try{
            Double score =(resto.getRating() * 3) /5;
            float rating = score.floatValue();
            this.restaurantRating.setRating(rating);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void setImageView(RestoResult resto, RequestManager glide){
        try {
            String base = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1200&photoreference=";
            String ref = resto.getRestoPhotos().get(0).getPhotoReference();
            String end = "&key=" + context.getResources().getString(R.string.default_web_api_key);
            glide.load(base + ref + end)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(imageView);
        }
        catch (NullPointerException e){
            e.printStackTrace();

            // impossible to load image -> default background
            this.imageView.setImageResource(R.drawable.no_image_background);
        }

    }

    private void setRestaurantHours(final RestoResult resto) {
        Log.d("TAG", "setRestaurantHours: ");
        fetchOpeningHours(resto.getPlaceId());
    }


    private void setMatrixDistance(RestoResult resto,Location userPosition){
            fetchMatrixDistance(userPosition.getLatitude(),
                    userPosition.getLongitude(),
                    resto.getRestoGeometry().getLocation().getLat(),
                    resto.getRestoGeometry().getLocation().getLng());
    }

    @SuppressLint("CheckResult")
    private void fetchOpeningHours(final String placeId){

        RestoDetailsClient.getInstance()
                .detailsRestaurantHours(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<OpeningHours>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                                detailsDisposable = d;
                    }

                    @Override
                    public void onNext(OpeningHours openingHours) {
                        Log.d("TAG", "onNext: ");

                        if (openingHours.getOpenNow()) {
                            chipHours.setText("Open");
                        }
                        else{
                            chipHours.setText("Close");
                            chipHours.setChipBackgroundColorResource(R.color.unselectedItem);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("TAG", "onError: ",e);
                        chipHours.setText(R.string.unavailable);
                        chipHours.setChipBackgroundColorResource(R.color.unselectedItem);
                    }

                    @Override
                    public void onComplete() {
                        detailsDisposable.dispose();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void fetchMatrixDistance(Double lat1,Double lng1,Double lat2,Double lng2){
        Log.d("TAG", "fetchMatrixDistance: ");
       MatrixDistanceClient.getInstance()
               .matrixDistanceDistanceObservable(lat1,lng1,lat2,lng2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<MatrixDistanceDistance>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        matrixDisposable = d;
                    }

                    @Override
                    public void onNext(MatrixDistanceDistance matrixDistanceDistance) {
                        if ( !matrixDistanceDistance.getDistanceText().isEmpty()) {
                            chipDistance.setText("~" + matrixDistanceDistance.getDistanceText());
                        }
                        else {
                            chipDistance.setChipBackgroundColorResource(R.color.quantum_grey800);
                            chipDistance.setText(R.string.unavailable);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("TAG", "onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        matrixDisposable.dispose();
                    }
                });

    }


    private void setNumberOfWorkmate(RestoResult resto){
        if (resto.getNbWorkmate() != 0){
            String text;

            switch (resto.getNbWorkmate()){
                case 1 :
                    text = context.getResources().getString(R.string.workmateChipSingle) + " (" + resto.getNbWorkmate() + ")";
                    this.chipWorkmate.setText(text);
                    this.chipWorkmate.setVisibility(View.VISIBLE);
                default:
                    text = context.getResources().getString(R.string.workmateChipMultiple) + " (" + resto.getNbWorkmate() + ")";
                    this.chipWorkmate.setVisibility(View.VISIBLE);
                    this.chipWorkmate.setText(text);
            }
        }
        else{
            chipWorkmate.setVisibility(View.GONE);
        }
    }

    public void cleanView(){
        chipHours.setChipBackgroundColorResource(R.color.go4lunchPrimary);
        chipHours.setText("");

        chipDistance.setChipBackgroundColorResource(R.color.go4lunchPrimary);
        chipDistance.setText("");

        chipWorkmate.setChipBackgroundColorResource(R.color.go4lunchPrimary);
        chipWorkmate.setText("");
    }

}
