package com.android.guillaume.go4launch.api.places;

import com.android.guillaume.go4launch.model.detailsRestaurant.DetailsRestaurant;
import com.android.guillaume.go4launch.model.detailsRestaurant.OpeningHours;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestoDetailsClient {

    private PlacesApi placesApi;
    private static RestoDetailsClient instance;
    public static final String DETAILS_PLACES_URL = "https://maps.googleapis.com/maps/api/place/details/";

    private HashMap<String,String> map;

    // Get Instance
    public static RestoDetailsClient getInstance(){
        if (instance == null) {
            instance = new RestoDetailsClient();
        }
        return instance;
    }

    // Construct new retrofit instance
    private RestoDetailsClient() {
        // Create New retroFit instance
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(DETAILS_PLACES_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.placesApi = retrofit.create(PlacesApi.class);
    }

    public Observable<OpeningHours> detailsRestaurant (String placeId){
        map = new HashMap<>();
        map.put("placeid", placeId);
        map.put("fields", "opening_hours");

        return this.placesApi.detailRestaurant(map)
                .map(new Function<DetailsRestaurant, OpeningHours>() {
                    @Override
                    public OpeningHours apply(DetailsRestaurant detailsRestaurant) throws Exception {
                        return detailsRestaurant.getResult().getOpeningHours();
                    }
                });
    }
}
