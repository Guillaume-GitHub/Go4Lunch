package com.android.guillaume.go4launch;

import com.android.guillaume.go4launch.utils.ApproximateRectBounds;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.model.RectangularBounds;

import org.junit.Assert;
import org.junit.Test;

public class ApproximateRectBoundsTest {

    @Test
    public void transformLatLngToBound(){
        double latitude = 12.6543;
        double longitude = 7.3579;

        double southLat = latitude + (0.009 * 2);
        double southLng = longitude + (0.009 * 2);

        double northLat = latitude - (0.009 * 2);
        double northLng = longitude - (0.009 * 2);

        RectangularBounds bounds = RectangularBounds.newInstance(new LatLngBounds((new LatLng(northLat,northLng)),new LatLng(southLat,southLng)));

        Assert.assertEquals(bounds, ApproximateRectBounds.getRectBounds(latitude,longitude));
    }
}
