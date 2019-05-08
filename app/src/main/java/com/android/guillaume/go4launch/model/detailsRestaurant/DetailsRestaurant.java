package com.android.guillaume.go4launch.model.detailsRestaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DetailsRestaurant {
    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private DetailsRestaurantResult detailsRestaurantResult;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public DetailsRestaurantResult getDetailsRestaurantResult() {
        return detailsRestaurantResult;
    }

    public void setDetailsRestaurantResult(DetailsRestaurantResult detailsRestaurantResult) {
        this.detailsRestaurantResult = detailsRestaurantResult;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
