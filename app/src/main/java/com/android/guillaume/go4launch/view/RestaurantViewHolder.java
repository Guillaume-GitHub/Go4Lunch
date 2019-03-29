package com.android.guillaume.go4launch.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.bumptech.glide.RequestManager;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.card_view) CardView cardView;
    @BindView(R.id.restaurant_image) ImageView imageView;
    @BindView(R.id.restaurant_name) TextView restaurantName;
    @BindView(R.id.restaurant_address) TextView restaurantAddress;
    @BindView(R.id.restaurant_rating) RatingBar restaurtRating;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        Log.d("TAG", "RestaurantViewHolder: ");
    }

    public void updateView(RestoResult resto, RequestManager glide){
        Log.d("TAG", "updateView: ");
            this.restaurantName.setText(resto.getName());
            this.restaurantAddress.setText(resto.getVicinity());

            try {
                String base = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=";
                String ref = resto.getRestoPhotos().get(0).getPhotoReference();
                String end = "&key=AIzaSyCAN_KzcnTVx_TanS_bXdOK5CnlgI8_zj4";
                glide.load(base + ref + end)
                        .centerCrop()
                        .into(imageView);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

            Double score =(resto.getRating() * 3) /5;
            float rating = score.floatValue();
            this.restaurtRating.setRating(rating);
    }
}
