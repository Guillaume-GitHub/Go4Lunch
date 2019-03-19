package com.android.guillaume.go4launch;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment{
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentDeviceLocation;
    private final int ZOOM = 15; //1: World, 5: Landmass/continent, 10: City, 15: Streets, 20: Buildings

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //use SuppoprtMapFragment for using in fragment instead of activity
        this.mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        this.configureMap();

        this.displayMap();

        return rootView;
    }


    private Location getLastLocation() {
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 20, locationListener);
        return this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private void displayMap() {

        Log.d("TAG", "displayMap: ");
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                googleMap = map;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                googleMap.clear(); //clear old markers

                // Get current position
                currentDeviceLocation = getLastLocation();

                if(currentDeviceLocation != null) {
                    LatLng latLng = new LatLng(currentDeviceLocation.getLatitude(), currentDeviceLocation.getLongitude());

                    // add new marker
                    googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title("You'r Here !"));

                    // move camera
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM));
                }
                else{
                    Log.w("TAG", " LAST POSITION = NULL");
                }
            }
        });

    }

    //************************************** LOCATION LISTENER METHODS *****************************//

    private void configureMap(){

        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        this.locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("TAG", "onLocationChanged------------------------------------------->");

                // Get current position
                currentDeviceLocation = location;

                LatLng latLng = new LatLng(currentDeviceLocation.getLatitude(),currentDeviceLocation.getLongitude());

                googleMap.clear(); //clear old markers

                // add new Marker
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(currentDeviceLocation.getLatitude() + " " + currentDeviceLocation.getLongitude()));
                // move camera
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("TAG", "onProviderEnabled" );
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("TAG", "onProviderDisabled");
            }
        };
    }

}
