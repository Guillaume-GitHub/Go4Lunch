package com.android.guillaume.go4launch.model;

import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String userName;
    private String email;
    @Nullable
    private String urlPicture;

    public User(FirebaseUser firebaseUser) {
        this.uid = firebaseUser.getUid();
        this.userName = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
        this.urlPicture = firebaseUser.getPhotoUrl().toString();
    }

    public User(String uid, String username, String email, String urlPicture){
        this.uid = uid;
        this.userName = username;
        this.email = email;
        this.urlPicture = urlPicture;
    }

    //*******************  GETTER ****************//

    public String getUid() {return uid; }
    public String getUserName() {return userName;}
    public String getEmail() {return email;}
    @Nullable
    public String getUrlPicture() { return urlPicture;}

    //*******************  SETTER ****************//

    public void setUid(String uid) {this.uid = uid;}
    public void setUserName(String userName) {this.userName = userName;}
    public void setEmail(String email) {this.email = email;}
    public void setUrlPicture(@Nullable String urlPicture) {this.urlPicture = urlPicture;}
}
