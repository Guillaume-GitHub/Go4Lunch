package com.android.guillaume.go4launch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.auth = FirebaseAuth.getInstance();

        this.checkUserAuth();

        setContentView(R.layout.activity_main);
    }

    private void checkUserAuth() {
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null){
            //User is sign in
            Toast.makeText(this, "Already connect", Toast.LENGTH_LONG).show();
        } else{
            //User isn't sign in
            Toast.makeText(this, "Not connect", Toast.LENGTH_LONG).show();
        }
    }

}
