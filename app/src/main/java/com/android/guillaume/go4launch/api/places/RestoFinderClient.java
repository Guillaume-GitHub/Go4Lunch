package com.android.guillaume.go4launch.api.places;

import android.location.Location;
import android.util.Log;

import com.android.guillaume.go4launch.model.detailsRestaurant.DetailsRestaurant;
import com.android.guillaume.go4launch.model.restaurant.Restaurant;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import io.reactivex.internal.operators.observable.ObservableJust;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestoFinderClient {

    public static final String NEARBY_PLACES_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/";
    private PlacesApi placesApi;
    private static RestoFinderClient instance;

    private final String placesType = "restaurant";
    private final String radiusSearch = "2000";

    private HashMap<String,String> map;

    // Construct new retrofit instance
    private RestoFinderClient(String typeOfSearch) {
        // Create New retroFit instance
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(typeOfSearch)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.placesApi = retrofit.create(PlacesApi.class);
    }

    // Get Instance
    public static RestoFinderClient getInstance(String typeOfSearch){
        if (instance == null) {
            instance = new RestoFinderClient(typeOfSearch);
        }
        return instance;
    }

    //Return Data
    public Observable<List<RestoResult>> getRestaurant(Location location) {
        this.map = new HashMap<>();
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());

        map.put("location", lat + "," + lng);
        map.put("radius", this.radiusSearch);
        map.put("type", this.placesType);

        return placesApi.listRestaurant(map)
                .map(new Function<Restaurant, List<RestoResult>>() {
                    @Override
                    public List<RestoResult> apply(Restaurant restaurant) throws Exception {
                        return restaurant.getResults();
                    }
                });
    }
}
