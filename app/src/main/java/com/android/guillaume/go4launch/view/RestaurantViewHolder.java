package com.android.guillaume.go4launch.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.places.MatrixDistanceClient;
import com.android.guillaume.go4launch.api.places.RestoDetailsClient;
import com.android.guillaume.go4launch.model.DistanceMatrix.MatrixDistance;
import com.android.guillaume.go4launch.model.DistanceMatrix.MatrixDistanceDistance;
import com.android.guillaume.go4launch.model.detailsRestaurant.OpeningHours;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.utils.UserLocation;
import com.bumptech.glide.RequestManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

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

    private Disposable detailsDisposable;
    private Disposable matrixDisposable;
    private Context context;
    //private UserLocation

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context = itemView.getContext();
        Log.d("TAG", "RestaurantViewHolder: ");
    }

    public void updateView(RestoResult resto, RequestManager glide, Location userPosition){
        Log.d("TAG", "updateView: ");

            this.restaurantName.setText(resto.getName());
            this.restaurantAddress.setText(resto.getVicinity());

            this.setRestaurantRating(resto);

            this.setRestaurantHours(resto);

            try {
                String base = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
                String ref = resto.getRestoPhotos().get(0).getPhotoReference();
                String end = "&key=AIzaSyCAN_KzcnTVx_TanS_bXdOK5CnlgI8_zj4";
                glide.load(base + ref + end)
                        .centerCrop()
                        .into(imageView);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            this.setMatrixDistance(resto,userPosition);
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

    private void setRestaurantHours(final RestoResult resto) {
        Log.d("TAG", "setRestaurantHours: " + resto.getName());
        Log.d("TAG", "setRestaurantHours: " +resto.getPlaceId());
        fetchOpeningHours(resto.getPlaceId());
    }


    private void setMatrixDistance(RestoResult resto,Location userPosition){
        Log.d("TAG", "setMatrixDistance: ");
            fetchMatrixDistance(userPosition.getLatitude(),
                    userPosition.getLongitude(),
                    resto.getRestoGeometry().getLocation().getLat(),
                    resto.getRestoGeometry().getLocation().getLng());
    }

    private void fetchOpeningHours(String placeId){
        RestoDetailsClient.getInstance()
                .detailsRestaurant(placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<OpeningHours>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                                detailsDisposable = d;
                    }

                    @Override
                    public void onNext(OpeningHours openingHours) {
                        Calendar calendar = Calendar.getInstance();
                        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;

                        if (openingHours.getOpenNow()) {
                            chipHours.setText("Open until " + openingHours.getPeriods().get(day).getClose().getTime());
                        } else {
                            chipHours.setText("Reopen at : " + openingHours.getPeriods().get(day + 1).getOpen().getTime());
                            chipHours.setChipBackgroundColorResource(R.color.quantum_grey800);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        detailsDisposable.dispose();
                    }
                });
    }


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
                        Log.d("GET MATRIX DISTANCE", "" + matrixDistanceDistance.getDistanceText());
                        chipDistance.setText("~ "+ matrixDistanceDistance.getDistanceText());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.w("TAGGGGGGGGGGGGGGGGGGGGG", "onError: ",e);
                    }

                    @Override
                    public void onComplete() {
                        matrixDisposable.dispose();
                    }
                });

    }

}
