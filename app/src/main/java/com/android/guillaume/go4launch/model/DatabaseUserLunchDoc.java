package com.android.guillaume.go4launch.model;

public class DatabaseUserLunchDoc {
    private String date;
    private String userID;
    private String placeID;

    public DatabaseUserLunchDoc() {
    }

    public DatabaseUserLunchDoc(String date, String userID, String placeID) {
        this.date = date;
        this.userID = userID;
        this.placeID = placeID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }
}
