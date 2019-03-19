package com.android.guillaume.go4launch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.android.guillaume.go4launch.api.UserHelper;
import com.android.guillaume.go4launch.connexion.FacebookSignInHelper;
import com.android.guillaume.go4launch.connexion.GoogleSignInHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.activity_main_facebook_btn)
    Button facebookBtn;
    @BindView(R.id.activity_main_google_btn)
    Button googleBtn;

    private FirebaseAuth auth;

    private FacebookSignInHelper facebookClient;

    private static int RC_SIGN_IN = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if an user is already connect
        this.checkUserIsAuth();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("TAG", "onActivityResult: " + requestCode);

        if (requestCode == RC_SIGN_IN)
            // Get result activity
            this.handleResponseActivityResult(requestCode, resultCode, data);
        else
            // Pass the activity result back to the Facebook SDK
            facebookClient.getCallbackManager().onActivityResult(requestCode, resultCode, data);
    }

    // Check if user is signed in (non-null) and start action accordingly.
    private void checkUserIsAuth() {

        // Initialize Firebase Auth
        this.auth = FirebaseAuth.getInstance();

        //Initialize Firebase User
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) this.launchHomeActivity(); // if User is sign in --> Start HomeActivity

        Log.d("TAG", "checkUserIsAuth: " + currentUser);

    }

    //******************************* ACTIONS **********************************//

    //Launch sign in activity for result
    @OnClick(R.id.activity_main_google_btn)
    public void onClickGoogleBtn() {
        Log.d("TAG", "onClick: ");
        GoogleSignInHelper googleSignInHelper = new GoogleSignInHelper(getApplicationContext());
        startActivityForResult(googleSignInHelper.getSignInIntent(), RC_SIGN_IN);
    }

    //Launch sign in activity for result
    @OnClick(R.id.activity_main_facebook_btn)
    public void onClickFacebook(){
        Log.d("TAG", "onClickFacebook: ");
        this.facebookClient = new FacebookSignInHelper();
        facebookClient.logIn(this);
        this.handleFacebookConnexionResult();
    }

    //******************************* RESPONSE **********************************//

    //Handle Response after user sign in with Google
    private void handleResponseActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent;
        if (requestCode == RC_SIGN_IN) {

            switch(resultCode) {
                case RESULT_OK:
                    // The Task returned from this call.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        this.firebaseAuthWithGoogle(account);

                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w("TAG", "Google sign in failed", e);
                    }
                    break;

                case RESULT_CANCELED :
                    //Display canceled action message
                    Snackbar.make(constraintLayout, R.string.connexion_canceled, Snackbar.LENGTH_SHORT).show();
                default:
                    //Display error action message
                    Snackbar.make(constraintLayout, R.string.connexion_error, Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    //Handle Response after user sign in with Facebook
    private void handleFacebookConnexionResult(){
        CallbackManager callback = this.facebookClient.getCallbackManager();
        this.facebookClient.getLoginManager().registerCallback(callback, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook_Connexion", "onSuccess:");
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Facebook_Connexion", "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });
    }


    //******************************* AUTH **********************************//

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        this.auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("TAG", "signInWithCredential:success");
                            createUserInFirestore();
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(constraintLayout, R.string.authentication_error, Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        Log.d("TAG", "firebaseAuthWithFacebook:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            createUserInFirestore();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(constraintLayout, R.string.authentication_error, Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //******************************* FIREBASE ACTION **********************************//

    private void createUserInFirestore(){
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) UserHelper.createUser(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        launchHomeActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, R.string.user_creation_fail, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //******************************* METHODS **********************************//
    private void launchHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }
}
