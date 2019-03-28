package com.android.guillaume.go4launch.utils;

import android.location.Location;
import android.util.Log;

import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import androidx.annotation.NonNull;

public class GoogleMapManager implements OnMapReadyCallback {

    private GoogleMap googleMap;

    // Zoom level
    private final int ZOOM_WORLD = 1;
    private final int ZOOM_LANDMASS = 5;
    private final int ZOOM_CITY = 10;
    private final int ZOOM_STREETS = 15;
    private final int ZOOM_BUILDINGS = 20;


    public GoogleMapManager(SupportMapFragment map) {
        Log.d("TAG", "GoogleMapManager: ");
        map.getMapAsync(this);
    }

    // Create map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("GOOGLE MAP MANAGER", "onMapReady: ");

        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Set default position to Paris
        LatLng latLng = new LatLng(48.866667, 2.333333);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LANDMASS));
    }

    // mark the user position and move camera
    public void addUserMarker(@NonNull Location location){
        Log.d("TAG", "addUserMarker: ");
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

        this.googleMap.addMarker(new MarkerOptions()
                    .position(latLng));

        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_STREETS));
    }

    // Mark all nearby restaurant
    public void addRestaurantMarker(List<RestoResult> restos){
        LatLng latLng;

        for (RestoResult resto : restos) {
            Log.d("TAG", "addRestaurantMarker: " + resto.getName());

            try {
                latLng = new LatLng(resto.getRestoGeometry().getLocation().getLat(),resto.getRestoGeometry().getLocation().getLng());
                this.googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(resto.getName()));
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    // move camera to a specific position
    public void repositioningCamera(LatLng latLng){
        if (latLng != null) this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

}
