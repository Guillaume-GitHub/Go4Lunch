package com.android.guillaume.go4launch.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class UserLocation implements LocationListener {


    private Context context;
    private UserLocationListener locationCallback;
    public static final  int LOCATION_ENABLE = 0;
    public static final  int LOCATION_DISABLE = -1;

    public UserLocation(Context context, UserLocationListener callback) {
        Log.d("TAG", "UserLocation: ");
        this.context = context;
        locationCallback = callback;
    }

    public void startGeolocation() {
        Log.d("TAG", "startGeolocation: ");
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            this.locationCallback.permissionsDenied();
        }
        else{
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER,this,null);
        }
    }

    /**
     * Check if Location service is active, if not, this methods Prompt user to activate it.
     * @param context
     * @param activity
     */
    public static void checkLocationService(Context context, final Activity activity) {
        Log.d("TAG", "checkLocationService: ");

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: ");
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity, 200);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    //*******************************************************************************//

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG", "onLocationChanged:");
        this.locationCallback.newUserLocationDetected(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("TAG", "onProviderEnabled: ");
        this.locationCallback.positionServiceStatus(LOCATION_ENABLE);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("TAG", "onProviderDisabled: ");
        this.locationCallback.positionServiceStatus(LOCATION_DISABLE);
    }
}
