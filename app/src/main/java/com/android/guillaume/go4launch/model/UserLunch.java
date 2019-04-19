package com.android.guillaume.go4launch.model;

import javax.annotation.Nullable;

public class UserLunch {

    private String date;
    private String placeID;
    private String name;
    private String address;

    public UserLunch() {
    }

    public UserLunch(String date, String placeID, String name, String address) {
        this.date = date;
        this.placeID = placeID;
        this.name = name;
        this.address = address;
    }

    @Nullable
    public String getDate() {
        return date;
    }

    public void setDate(@Nullable String date) {
        this.date = date;
    }

    @Nullable
    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(@Nullable String placeID) {
        this.placeID = placeID;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Nullable
    public String getAddress() {
        return address;
    }

    public void setAddress(@Nullable String address) {
        this.address = address;
    }
}
