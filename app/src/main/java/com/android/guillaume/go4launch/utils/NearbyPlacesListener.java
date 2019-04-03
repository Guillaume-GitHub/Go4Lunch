package com.android.guillaume.go4launch.utils;

import com.android.guillaume.go4launch.controler.ListViewFragment;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;

import java.util.List;

public interface NearbyPlacesListener {
    void onReceiveNearbyPlaces(List<RestoResult> restos);
}
