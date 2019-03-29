package com.android.guillaume.go4launch.api.places;

import android.location.Location;

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

    private final String placesType = "restaurant";
    private final String radiusSearch = "1000";

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

    }

    //Return Data
    public Observable<Restaurant> getRestaurant(Location location){
        this.map = new HashMap<>();
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());

        map.put("location", lat + "," + lng);
        map.put("radius", this.radiusSearch);
        map.put("type", this.placesType);

        return placesApi.listRestaurant(map);
    }
}
