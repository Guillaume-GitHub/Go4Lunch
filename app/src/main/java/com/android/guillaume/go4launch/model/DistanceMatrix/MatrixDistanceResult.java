package com.android.guillaume.go4launch.model.DistanceMatrix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MatrixDistanceResult {
    @SerializedName("elements")
    @Expose
    private List<MatrixDistanceElement> elements = null;

    public List<MatrixDistanceElement> getElements() {
        return elements;
    }

    public void setElements(List<MatrixDistanceElement> elements) {
        this.elements = elements;
    }
}
