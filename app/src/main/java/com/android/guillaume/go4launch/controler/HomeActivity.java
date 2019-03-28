package com.android.guillaume.go4launch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guillaume.go4launch.model.restaurant.Restaurant;
import com.android.guillaume.go4launch.utils.RestoFinderClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity{

    //Bind All Views
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.activity_home_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_navigationView) NavigationView navDrawer;
    @BindView(R.id.activity_home_bottomNavigation) BottomNavigationView navBottom;

    private TextView navDrawerEmail;
    private TextView navDrawerUsername;
    private ImageView navDrawerPicture;

    private final int RC_LOCATION_PERMISSIONS = 100;

    private Disposable disposable;
    private Activity myActivity;

    // Fragment
    private Fragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        this.configureToolbar();
        this.configureNavigationDrawer();
        this.configureBottomNavigation();
        this.inflateNavDrawerHeaderItems();
        this.navBottom.setSelectedItemId(R.id.navBottom_mapView);
        this.myActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.updateUserInfo();
        this.getUserLocation();
    }

    //*********************************** NAVIGATION ********************************//

    // Catch click on navigation drawer Items
    private void configureNavigationDrawer(){
        this.navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_lunch_item:
                        // ...
                        break;
                    case R.id.menu_settings_item:
                        // ...
                        break;
                    case R.id.menu_logout_item:
                        //logout User
                        Log.d("TAG", "BEFORE SIGN OUT : " + getCurrentUser());
                        FirebaseAuth.getInstance().signOut();
                        // Close Activity and return in Connexion screen
                        finish();
                        Log.d("TAG", "AFTER SIGN OUT: " + getCurrentUser());
                }
                return true;
            }
        });
    }

    // Catch click on bottom navigation Items
    private void configureBottomNavigation(){
        this.navBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navBottom_mapView:
                        // ...
                        displayMapFragment();
                        break;
                    case R.id.navBottom_listView:
                        // ...
                        break;
                    case R.id.navBottom_workmates:
                        // ...
                }
                return true;
            }
        });
    }

    // Open Menu when user click on Home Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // User can't return to the connexion page without logout
    @Override
    public void onBackPressed() {
    }

    //*********************************** UI ********************************//

    private void configureToolbar(){
        setSupportActionBar(this.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Update user info in navigation drawer
    private void updateUserInfo(){
        this.navDrawerEmail.setText(this.getCurrentUser().getEmail());
        this.navDrawerUsername.setText(this.getCurrentUser().getDisplayName());

        // Get user profile image
        if (this.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.getCurrentUser()
                    .getPhotoUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(this.navDrawerPicture);
        }
        else{ // default picture
            this.navDrawerPicture.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            this.navDrawerPicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_foreground));
        }
    }

    //*********************************** METHODS ********************************//

    // Get the user logged
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    private void inflateNavDrawerHeaderItems(){
        View view = this.navDrawer.getHeaderView(0);
        this.navDrawerUsername = view.findViewById(R.id.nav_drawer_username);
        this.navDrawerEmail = view.findViewById(R.id.nav_drawer_email);
        this.navDrawerPicture = view.findViewById(R.id.nav_drawer_picture);
    }

    // display map Fragment
    private void displayMapFragment(){

        this.frag = getSupportFragmentManager().findFragmentById(R.id.activity_home_frameLayout);

        if (this.frag == null || !this.frag.isVisible()) {

            Fragment frag = new MapFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_home_frameLayout, frag)
                    .commit();
        }
    }


    // HANDLE LocationSettingsRequest Result from MapFragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("HOME ACTIVITY", "onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        /*
        Fragment frg = getSupportFragmentManager().findFragmentById(R.id.activity_home_frameLayout);
        if (frg != null) {
            frg.onActivityResult(requestCode, resultCode, data);
        }
        */
    }



    private UserLocationListener locationCallback = new UserLocationListener() {
        @Override
        public void newUserLocationDetected(Location location) {

            Log.d("TAG", "newUserLocationDetected: ");

            if(getSupportFragmentManager().findFragmentById(R.id.activity_home_frameLayout) != null){
                Log.d("TAG", "fragGGGGGGGGGGGGGGGG ");
               MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_home_frameLayout);
               mapFrag.setNewPosition(location);
            }

            getNearbyRestaurants();
        }

        @Override
        public void permissionsDenied() {
            Log.d("HOME ACTIVITY", "permissionsDenied: ");
            if (Build.VERSION.SDK_INT >= 23) {
                Log.d("TAG", "permissionsDenied: request");
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_LOCATION_PERMISSIONS);
            }
        }

        @Override
        public void positionServiceStatus(int statusCode) {
            if(statusCode == UserLocation.LOCATION_ENEABLE){
                Log.d("TAG", "positionServiceStatus:  ACTIVE");
            }
            else {
                Log.d("TAG", "positionServiceStatus:  DESACTIVE");
                UserLocation.checkLocationService(getApplicationContext(),myActivity);
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    Log.d("HOME ACTIVITY", "Permission Granted ");
                    getUserLocation();
                } else {
                    // permission denied
                    // Disable the map functionality
                }
                return;
            }
            // ...
        }
    }

    private void getUserLocation(){
        UserLocation userLocation = new UserLocation(getApplicationContext(),locationCallback);
        userLocation.startGeolocation();
    }

    private void getNearbyRestaurants(){
        RestoFinderClient.getInstance().getRestaurant()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Restaurant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Restaurant restaurant) {
                        Log.d("TAG", "onNext: ");
                        if(getSupportFragmentManager().findFragmentById(R.id.activity_home_frameLayout) != null) {
                            MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_home_frameLayout);
                            mapFrag.setNearbyRestaurant(restaurant.getResults());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                    }
                });
    }
}

