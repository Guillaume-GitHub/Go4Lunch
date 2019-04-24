package com.android.guillaume.go4launch.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.RectangularBounds;

public class ApproximateRectBounds {

    public static RectangularBounds getRectBounds(Double latitude,Double longitude){

        LatLng latLng1 = new LatLng(latitude - (0.009 * 2), longitude - (0.009 * 2));
        LatLng latLng2 = new LatLng(latitude + (0.009 * 2), longitude + (0.009 * 2));

        LatLngBounds latLngBounds = LatLngBounds.builder().include(latLng1).include(latLng2).build();

        RectangularBounds bounds = RectangularBounds.newInstance(latLngBounds);

        return bounds;
    }
}
