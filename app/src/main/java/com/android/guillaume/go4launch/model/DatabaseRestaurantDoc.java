package com.android.guillaume.go4launch.model;

import java.util.List;

public class DatabaseRestaurantDoc {
    private String date;
    private String placeID;
    private List<String> users;

    public DatabaseRestaurantDoc() {
    }

    public DatabaseRestaurantDoc(String date, String placeID, List<String> users) {
        this.date = date;
        this.placeID = placeID;
        this.users = users;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
