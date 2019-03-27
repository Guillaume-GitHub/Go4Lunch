package com.android.guillaume.go4launch.model.restaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Restaurant {

    @SerializedName("html_attributions")
    @Expose
    private List<RestoAttributions> htmlAttributions = null;
    @SerializedName("results")
    @Expose
    private List<RestoResult> results = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<RestoAttributions> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<RestoAttributions> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public List<RestoResult> getResults() {
        return results;
    }

    public void setResults(List<RestoResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
