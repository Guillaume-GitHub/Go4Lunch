package com.android.guillaume.go4launch.utils;

import android.location.Location;
import android.util.Log;

import com.android.guillaume.go4launch.api.firebase.RestaurantHelper;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class GoogleMapManager implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap googleMap;

    // Zoom level
    public  static final int ZOOM_WORLD = 1;
    public static final int ZOOM_LANDMASS = 5;
    public static final int ZOOM_CITY = 10;
    public static final int ZOOM_STREETS = 15;
    public static final int ZOOM_BUILDINGS = 20;

    private List<RestoResult> restoResults;

    private GoogleMapCallbacks callback;

    public GoogleMapManager(SupportMapFragment map, GoogleMapCallbacks googleMapCallbacks) {
        Log.d("TAG", "GoogleMapManager: ");
        map.getMapAsync(this);
        this.callback = googleMapCallbacks;
    }

    // Create map
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("GOOGLE MAP MANAGER", "onMapReady: ");

        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.setOnInfoWindowClickListener(this);
        UiSettings settings = this.googleMap.getUiSettings();
        settings.setMapToolbarEnabled(false);// disable Google maps toolbar

        // Set default position to Paris
        LatLng latLng = new LatLng(48.866667, 2.333333);
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LANDMASS));
    }

    // mark the user position and move camera
    public void addUserMarker(Location location){

        if(location != null && this.googleMap != null) {
            Log.d("TAG", "addUserMarker: ");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker;

           marker = this.googleMap.addMarker(new MarkerOptions()
                    .position(latLng));
           marker.setTag(-1);

            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_STREETS));
        }
    }

    // Mark all nearby restaurant
    public void addRestaurantMarker(List<RestoResult> restos){
        Log.d("TAG", "addRestaurantMarker: ");

        LatLng latLng;
        this.restoResults = restos;

        if(restos != null && restos.size() > 0) {
            int i = 0;

            for (RestoResult resto : restos) {
                Log.d("TAG", "addRestaurantMarker: " + resto.getName());

                try {
                    latLng = new LatLng(resto.getRestoGeometry().getLocation().getLat(), resto.getRestoGeometry().getLocation().getLng());
                    Marker marker;

                    if (resto.getNbWorkmate() >=1){

                       marker = this.googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(resto.getName())
                                .snippet(resto.getVicinity())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                    else {
                        marker = this.googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(resto.getName())
                                .snippet(resto.getVicinity()));
                    }

                    marker.setTag(i);
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }
                // LOOP .....
                // incrementation index
                i++;
            }

        }
    }


    // move camera to a specific position
    public void repositioningCamera(LatLng latLng, int zoom){
        if (latLng != null) this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        try{
            int index = (Integer) marker.getTag();
            if (index != -1){this.callback.onClickRestaurantWindowMarker(this.restoResults.get(index));}
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

}
