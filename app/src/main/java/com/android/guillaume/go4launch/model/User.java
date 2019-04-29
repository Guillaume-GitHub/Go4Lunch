package com.android.guillaume.go4launch.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import androidx.annotation.Nullable;

public class User implements Parcelable {

    private String uid;
    private String userName;
    private String email;
    @Nullable
    private String urlPicture;
    @Nullable
    private UserLunch lunch;
    @Nullable
    private List<String> like;

    public User() {
    }

    public User(FirebaseUser firebaseUser) {
        this.uid = firebaseUser.getUid();
        this.userName = firebaseUser.getDisplayName();
        this.email = firebaseUser.getEmail();
        this.urlPicture = firebaseUser.getPhotoUrl().toString();
        this.lunch = null;
        this.like = null;
    }

    public User(String uid, String username, String email, @Nullable String urlPicture, @Nullable UserLunch lunch, @Nullable List<String> like){
        this.uid = uid;
        this.userName = username;
        this.email = email;
        this.urlPicture = urlPicture;
        this.lunch = lunch;
        this.like = like;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    //*******************  GETTER ****************//

    protected User(Parcel in) {
        uid = in.readString();
        userName = in.readString();
        email = in.readString();
        urlPicture = in.readString();
        like = in.createStringArrayList();
    }

    public String getUid() {return uid; }
    public String getUserName() {return userName;}
    public String getEmail() {return email;}
    @Nullable
    public String getUrlPicture() { return urlPicture;}
    @Nullable
    public UserLunch getLunch() {return lunch;}
    @Nullable
    public List<String> getLike() {return like; }
    //*******************  SETTER ****************//

    public void setUid(String uid) {this.uid = uid;}
    public void setUserName(String userName) {this.userName = userName;}
    public void setEmail(String email) {this.email = email;}
    public void setUrlPicture(@Nullable String urlPicture) {this.urlPicture = urlPicture;}
    public void setLunch(UserLunch lunch) {this.lunch = lunch;}
    public void setLike(List<String> like) {this.like = like;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(urlPicture);
        dest.writeStringList(like);
    }
}
