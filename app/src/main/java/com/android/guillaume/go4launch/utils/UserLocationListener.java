package com.android.guillaume.go4launch.utils;

import android.location.Location;

public interface UserLocationListener {

    void newUserLocationDetected(Location location);

    void permissionsDenied();

    void positionServiceStatus(int statusCode);

}
