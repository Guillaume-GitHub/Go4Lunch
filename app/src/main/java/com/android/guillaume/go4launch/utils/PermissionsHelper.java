package com.android.guillaume.go4launch.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsHelper implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context;
    private Activity activity;
    private Boolean isGranted = false;

    // CONST permission Request Code
    public static final int LOCATION = 1;

    public PermissionsHelper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void askForPermission(String permission, int requestCode){

        if(Build.VERSION.SDK_INT >= 23) {
            //Check explicit permission if target API is > to API 23

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(context, "PERMISSION DENIED", Toast.LENGTH_SHORT).show();

                // Is called if user has denied the permission before
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    // Ask Permission Again
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                }
                // Is by default when permission has not accepted
                ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            } else {
                Toast.makeText(context, "PERMISSION ACCEPTED", Toast.LENGTH_SHORT).show();
                this.isGranted = true;
            }
        }
        // Don't need to check permissions explicitly if target API is < 23
        // ...
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    public Boolean isGranted() {
        return isGranted;
    }

}
