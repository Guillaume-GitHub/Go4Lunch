package com.android.guillaume.go4launch.utils;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import com.android.guillaume.go4launch.api.places.RestoFinderClient;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NearbyPlaces{

    private final String TAG = this.getClass().getSimpleName();
    private Disposable disposable;
    private NearbyPlacesListener callback;

    public NearbyPlaces(NearbyPlacesListener nearbyPlacesListener) {
        this.callback = nearbyPlacesListener;
    }

    @SuppressLint("CheckResult")
    public void getNearbyRestaurant(Location location){
        RestoFinderClient.getInstance(RestoFinderClient.NEARBY_PLACES_URL).getRestaurant(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<RestoResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<RestoResult> restos) {
                        Log.d(TAG, "onNext: ");
                        callback.onReceiveNearbyPlaces(restos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });

    }
}
