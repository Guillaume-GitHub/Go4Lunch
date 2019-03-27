package com.android.guillaume.go4launch.utils;

import android.util.Log;

import com.android.guillaume.go4launch.model.restaurant.Restaurant;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestoFinderService implements Callback<Restaurant> {

    private final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private PlacesApi placesApi;

    private HashMap<String,String> map;

    public void fetchNearbyRestaurant() {

        // Create New retroFit instance
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.placesApi = retrofit.create(PlacesApi.class);
        this.createHashMapParams();

    }

    private void createHashMapParams(){
        this.map = new HashMap<>();

        map.put("location","47.623,6.155819");
        map.put("radius","100");
        map.put("type","restaurant");

        this.buildAndExecuteHttpRequest();
    }

    private void buildAndExecuteHttpRequest(){
        Call<Restaurant> call = placesApi.listRestaurant(map);
        Log.d("TAG", call.request().toString());
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Restaurant>call, Response<Restaurant> response) {
        List<RestoResult> list = response.body().getResults();
        for (RestoResult resto :list) {
            Log.d("TAG", resto.getName());
        }
    }

    @Override
    public void onFailure(Call<Restaurant> call, Throwable t) {
        Log.d("TAG", "onFailure: " + Log.getStackTraceString(t));
    }
}
