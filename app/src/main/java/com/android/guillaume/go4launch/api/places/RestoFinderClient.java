package com.android.guillaume.go4launch.utils;

import com.android.guillaume.go4launch.model.restaurant.Restaurant;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestoFinderClient {

    private final String BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private PlacesApi placesApi;
    private static RestoFinderClient instance;

    private HashMap<String,String> map;

    // Construct new retrofit instance
    private RestoFinderClient() {
        // Create New retroFit instance
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.placesApi = retrofit.create(PlacesApi.class);
        this.createHashMapParams();
    }

    // Get Instance
    public static RestoFinderClient getInstance(){
        if (instance == null) {
            instance = new RestoFinderClient();
        }
        return instance;
    }

    private void createHashMapParams(){
        this.map = new HashMap<>();

        map.put("location","47.623,6.155819");
        map.put("radius","1000");
        map.put("type","restaurant");
    }

    //Return Data
    public Observable<Restaurant> getRestaurant(){
        return placesApi.listRestaurant(map);
    }
}
