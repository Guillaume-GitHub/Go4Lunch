package com.android.guillaume.go4launch.connexion;

import android.app.Activity;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

public class FacebookSignInHelper {

    private CallbackManager callbackManager;
    private LoginManager loginManager;

    //Constants Permission
    private static final String EMAIL_PERMISSION = "email";
    private static final String PUBLIC_PROFILE = "public_profile";

    private String TAG = this.getClass().getSimpleName();

    public FacebookSignInHelper() {
        this.callbackManager = CallbackManager.Factory.create();
        this.loginManager = LoginManager.getInstance();
    }

    // Return CallbackManager Instance
    public CallbackManager getCallbackManager() {
        return callbackManager;
    }

    // Return CallbackManager
    public LoginManager getLoginManager() {
        return loginManager;
    }

    private List<String> getPermissionCollection() {
        List<String> permission = new ArrayList<>();
        permission.add(EMAIL_PERMISSION);
        permission.add(PUBLIC_PROFILE);

        return permission;
    }

    //Launch SignIn
    public void logIn(Activity activity) {
        this.loginManager.logInWithReadPermissions(activity, getPermissionCollection());
    }
}
