package com.android.guillaume.go4launch.connexion;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.guillaume.go4launch.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleSignInHelper {

    private Context context;
    private GoogleSignInClient signInClient;
    private GoogleSignInOptions signInOptions;

    public GoogleSignInHelper(Context context) {
        this.context = context;
        this.createGoogleSignInObject();
    }

    private void createGoogleSignInObject(){

        this.signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options
        this.signInClient = GoogleSignIn.getClient(context,signInOptions);
        Log.d("TAG", "signIn: ");
    }

    public Intent getSignInIntent(){
        return this.signInClient.getSignInIntent();
    }

}
