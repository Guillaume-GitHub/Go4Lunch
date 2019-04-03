package com.android.guillaume.go4launch.model.DistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatrixDistanceDistance {
    @SerializedName("text")
    @Expose
    private String distanceText;
    @SerializedName("value")
    @Expose
    private Integer distanceValue;

    public String getDistanceText() {
        return distanceText;
    }

    public void setDistanceText(String text) {
        this.distanceText = text;
    }

    public Integer getDistanceValue() {
        return distanceValue;
    }

    public void setDistanceValue(Integer value) {
        this.distanceValue = value;
    }
}
