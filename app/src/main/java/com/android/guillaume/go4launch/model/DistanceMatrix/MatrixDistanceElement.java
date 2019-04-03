package com.android.guillaume.go4launch.model.DistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MatrixDistanceElement {
    @SerializedName("distance")
    @Expose
    private MatrixDistanceDistance distance;
    @SerializedName("duration")
    @Expose
    private MatrixDistanceDuration duration;
    @SerializedName("status")
    @Expose
    private String status;

    public MatrixDistanceDistance getDistance() {
        return distance;
    }

    public void setDistance(MatrixDistanceDistance distance) {
        this.distance = distance;
    }

    public MatrixDistanceDuration getDuration() {
        return duration;
    }

    public void setDuration(MatrixDistanceDuration duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
