package com.android.guillaume.go4launch.api.places;

import android.util.Log;

import com.android.guillaume.go4launch.model.DistanceMatrix.MatrixDistance;
import com.android.guillaume.go4launch.model.DistanceMatrix.MatrixDistanceDistance;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MatrixDistanceClient {

    private PlacesApi placesApi;
    private static MatrixDistanceClient instance;
    public static final String MATRIX_DISTANCE_URL = "https://maps.googleapis.com/maps/api/distancematrix/";

    private HashMap<String,String> map;

    // Get Instance
    public static MatrixDistanceClient getInstance(){
        if (instance == null) {
            instance = new MatrixDistanceClient();
        }
        return instance;
    }

    // Construct new retrofit instance
    private MatrixDistanceClient() {
        // Create New retroFit instance
        Retrofit retrofit = new  Retrofit.Builder()
                .baseUrl(MATRIX_DISTANCE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        this.placesApi = retrofit.create(PlacesApi.class);
    }

    public Observable<MatrixDistanceDistance> matrixDistanceDistanceObservable (double lat1,double lng1,double lat2,double lng2){
        map = new HashMap<>();
        map.put("origins", String.valueOf(lat1)+","+String.valueOf(lng1));
        map.put("destinations", String.valueOf(lat2)+","+String.valueOf(lng2));
        map.put("units", "metric");

        return this.placesApi.matrixDistance(map)
                .map(new Function<MatrixDistance, MatrixDistanceDistance>() {
                    @Override
                    public MatrixDistanceDistance apply(MatrixDistance matrixDistance) throws Exception {
                        return matrixDistance.getRows().get(0).getElements().get(0).getDistance();
                    }
                });
    }
}

