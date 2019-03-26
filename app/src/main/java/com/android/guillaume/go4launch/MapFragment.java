package com.android.guillaume.go4launch;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.floating_btn)
    FloatingActionButton myPositionButton;

    private static String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private GoogleMap googleMap;
    private SupportMapFragment mapFragment;
    private LocationManager locationManager;
    private Location lastPosition;
    private boolean GpsStatus;
    private boolean permissionStatus;

    private final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;
    private final String NETWORK_PROVIDER = LocationManager.NETWORK_PROVIDER;

    private final int RC_LOCATION = 50;
    private final int RC_PERMISSION = 100;

    private final int ZOOM = 15; //1: World, 5: Landmass/continent, 10: City, 15: Streets, 20: Buildings


    public MapFragment() {
        // Required empty public constructor
    }

    //************************************** LIFECYCLE METHODS *****************************//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_map_container, container, false);
        ButterKnife.bind(this, rootView);

        //use SupportMapFragment for using in fragment instead of activity
        this.mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        return this.rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        this.initLocationService();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    //**************************************  LISTENER METHODS *****************************//

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("TAG", "onLocationChanged--------------->");
            // Stop immediately location updates, because we need position just one Time
            stopReceiveLocationUpdate();
            pingAndMoveToPosition(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged: ");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled: ");
            initLocationService();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled: ");
            askGPSSActivation();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (this.lastPosition != null){
            Log.d(TAG, "onMapReady: NON NULL");
            // Move map to last position know if NonNull
            LatLng latLng = new LatLng(this.lastPosition.getLatitude(), lastPosition.getLongitude());
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM));
        }
        else {
            Log.d(TAG, "onMapReady: NULL");
            // Set default position to Paris
            LatLng latLng = new LatLng(48.866667, 2.333333);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        }

    }

    //************************************** MAP SERVICE METHODS*****************************//

    private void initLocationService(){
        // Acquire a reference to the system Location Manager
        this.locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        // Get Position of device
        this.getLastPosition();
    }

    private  void getLastPosition() {

        Log.d(TAG, "getLastPosition: ");

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermissions();
        }
        else {

            if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
                this.locationManager.requestLocationUpdates(GPS_PROVIDER, 5000, 100, locationListener);
            }
            else{
                this.locationManager.requestLocationUpdates(NETWORK_PROVIDER, 5000, 100, locationListener);
            }
        }
    }

    private void stopReceiveLocationUpdate() {
        this.locationManager.removeUpdates(locationListener);
    }

    private void pingAndMoveToPosition(Location location){
        if (this.googleMap != null){
            this.lastPosition = location;
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,ZOOM));

            googleMap.addMarker(new MarkerOptions().position(latLng));
        }
        else{
            // GoogleMap is not ready
            // ...
        }
    }

    @OnClick(R.id.floating_btn)
    public void repositionMapCamera() {

        if(this.googleMap != null && this.lastPosition != null) {
            this.googleMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(this.lastPosition.getLatitude(), this.lastPosition.getLongitude()), ZOOM));
        }

        else if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "repositionMapCamera:  GEOLOC are not OK");
            this.askGPSSActivation();
        }

        else if (!this.permissionStatus) {
            Log.d(TAG, "repositionMapCamera:  Permission are not OK");
            this.askLocationPermissions();

        }
    }

    //************************************** PERMISSIONS METHODS *****************************//

    private void askLocationPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSION);
    }

    private void askGPSSActivation() {
        Log.d(TAG, "askGPSSActivation: ");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(getContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                initLocationService();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(), RC_LOCATION);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    //************************************* CALLBACKS RESULTS *****************************************************//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    Log.d("TAG", "Permission Granted ");
                    this.initLocationService();
                } else {
                    // permission denied
                    // Disable the map functionality
                }
                return;
            }
            // ...
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case RC_LOCATION:
                if(resultCode == -1){
                    initLocationService();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
