package com.android.guillaume.go4launch.model.restaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestoViewport {

    @SerializedName("restoNortheast")
    @Expose
    private RestoNortheast restoNortheast;
    @SerializedName("restoSouthwest")
    @Expose
    private RestoSouthwest restoSouthwest;

    public RestoNortheast getRestoNortheast() {
        return restoNortheast;
    }

    public void setRestoNortheast(RestoNortheast restoNortheast) {
        this.restoNortheast = restoNortheast;
    }

    public RestoSouthwest getRestoSouthwest() {
        return restoSouthwest;
    }

    public void setRestoSouthwest(RestoSouthwest restoSouthwest) {
        this.restoSouthwest = restoSouthwest;
    }
}
