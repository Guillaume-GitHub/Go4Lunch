package com.android.guillaume.go4launch.model.restaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestoGeometry {

    @SerializedName("location")
    @Expose
    private RestoLocation location;
    @SerializedName("restoViewport")
    @Expose
    private RestoViewport restoViewport;

    public RestoLocation getLocation() {
        return location;
    }

    public void setLocation(RestoLocation location) {
        this.location = location;
    }

    public RestoViewport getRestoViewport() {
        return restoViewport;
    }

    public void setRestoViewport(RestoViewport restoViewport) {
        this.restoViewport = restoViewport;
    }
}
