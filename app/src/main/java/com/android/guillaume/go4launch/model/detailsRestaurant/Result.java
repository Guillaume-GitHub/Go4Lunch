package com.android.guillaume.go4launch.model.detailsRestaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("formatted_phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }
}
