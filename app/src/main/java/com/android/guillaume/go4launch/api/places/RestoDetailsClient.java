package com.android.guillaume.go4launch.api.places;

import android.util.Log;

import com.android.guillaume.go4launch.model.detailsRestaurant.DetailsRestaurant;
import com.android.guillaume.go4launch.model.detailsRestaurant.OpeningHours;
import com.android.guillaume.go4launch.model.detailsRestaurant.Result;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestoDetailsClient {

    private PlacesApi placesApi;
    private static RestoDetailsClient instance;
    public static final String DETAILS_PLACES_URL = "https://maps.googleapis.com/maps/api/place/details/";
    private final String  hours = "opening_hours";
    private final String formattedNumber = "formatted_phone_number";
    private final String website = "website";
    private final String language = Locale.getDefault().getLanguage();

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

        Log.d("DISPLAY LANGUAGE DEVICE", "RestoDetailsClient: =" + language);
    }


    public Observable<DetailsRestaurant> detailsRestaurant(String placeId) {
        map = new HashMap<>();
        map.put("placeid", placeId);
        map.put("fields", hours + "," + formattedNumber + "," + website);
        map.put("language",language);

        return this.placesApi.detailRestaurant(map);
    }


    public Observable<OpeningHours> detailsRestaurantHours(String placeId){
        return detailsRestaurant(placeId)
                .map(new Function<DetailsRestaurant, OpeningHours>() {
                    @Override
                    public OpeningHours apply(DetailsRestaurant detailsRestaurant) throws Exception {
                        return detailsRestaurant.getResult().getOpeningHours();
                    }
                });
    }
}
