package com.android.guillaume.go4launch.model.DistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatrixDistanceDuration {

    @SerializedName("text")
    @Expose
    private String durationText;
    @SerializedName("value")
    @Expose
    private Integer durationValue;

    public String getDurationText() {
        return durationText;
    }

    public void setDurationText(String text) {
        this.durationText = text;
    }

    public Integer getDurationValue() {
        return durationValue;
    }

    public void setDurationValue(Integer value) {
        this.durationValue = value;
    }
}
