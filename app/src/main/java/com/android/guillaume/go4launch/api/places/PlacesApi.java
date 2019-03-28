package com.android.guillaume.go4launch.utils;

import com.android.guillaume.go4launch.model.restaurant.Restaurant;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface PlacesApi {
    String API_KEY = "AIzaSyCAN_KzcnTVx_TanS_bXdOK5CnlgI8_zj4";

    @GET("json?key=" + API_KEY)
    Observable<Restaurant> listRestaurant(@QueryMap Map<String,String> params);
}
