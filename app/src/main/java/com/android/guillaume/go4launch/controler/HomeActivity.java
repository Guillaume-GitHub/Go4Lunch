package com.android.guillaume.go4launch.controler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.utils.NearbyPlaces;
import com.android.guillaume.go4launch.utils.NearbyPlacesListener;
import com.android.guillaume.go4launch.utils.RepositionClickListener;
import com.android.guillaume.go4launch.utils.UserLocation;
import com.android.guillaume.go4launch.utils.UserLocationListener;
import com.android.guillaume.go4launch.utils.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements NearbyPlacesListener, UserLocationListener, RepositionClickListener {

    //Bind All Views
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.activity_home_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_navigationView) NavigationView navDrawer;
    @BindView(R.id.activity_home_bottomNavigation) BottomNavigationView navBottom;
    @BindView(R.id.activity_home_viewPager)
    ViewPager viewPager;

    private TextView navDrawerEmail;
    private TextView navDrawerUsername;
    private ImageView navDrawerPicture;
    private Location lastUserPosition;

    private ViewPagerAdapter viewPagerAdapter;

    private Activity myActivity;

    private final String TAG = this.getClass().getSimpleName();
    private final int RC_LOCATION_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        this.configureToolbar();
        this.configureNavigationDrawer();
        this.configureBottomNavigation();
        this.inflateNavDrawerHeaderItems();
        this.viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(viewPagerAdapter);

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
                        Log.d(TAG, "BEFORE SIGN OUT : " + getCurrentUser());
                        FirebaseAuth.getInstance().signOut();
                        // Close Activity and return in Connexion screen
                        finish();
                        Log.d(TAG, "AFTER SIGN OUT: " + getCurrentUser());
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
                        Log.d(TAG, "onNavigationItemSelected: ");
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navBottom_listView:
                        // ...
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navBottom_workmates:
                        // ...
                        break;
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

    // Inflate Nav drawer items
    private void inflateNavDrawerHeaderItems(){
        View view = this.navDrawer.getHeaderView(0);
        this.navDrawerUsername = view.findViewById(R.id.nav_drawer_username);
        this.navDrawerEmail = view.findViewById(R.id.nav_drawer_email);
        this.navDrawerPicture = view.findViewById(R.id.nav_drawer_picture);
    }

    //*********************************** METHODS ********************************///

    // Get the user logged
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Get the user position (Geolocation)
    public void getUserLocation(){
        UserLocation userLocation = new UserLocation(getApplicationContext(),this);
        userLocation.startGeolocation();
    }

    public void getNearbyRestaurant(Location location){
        NearbyPlaces nearbyPlaces = new NearbyPlaces(this);
        nearbyPlaces.getNearbyRestaurant(location);
    }

    //*********************************** CALLBACKS ********************************//

    // Catch Request permission response
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    Log.d(TAG, "Permission Granted ");
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

    //**************************** UserLocationListener INTERFACE ***************************************//

    // INTERFACE callback
    @Override
    public void newUserLocationDetected(Location location) {
        Log.d("TAG", "newUserLocationDetected: ");

        this.lastUserPosition = location;

        MapFragment mapFrag = (MapFragment) viewPagerAdapter.getRegisteredFragments(0);

        if(mapFrag != null) {mapFrag.setNewPosition(location);}

        //Fetch
        this.getNearbyRestaurant(location);
    }

    // Ask permissions to user
    @Override
    public void permissionsDenied() {
        Log.d(TAG, "permissionsDenied: ");
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
            ,RC_LOCATION_PERMISSIONS);
        }
    }

    // Catch Enable/disable Location service
    @Override
    public void positionServiceStatus(int statusCode) {
        if(statusCode == UserLocation.LOCATION_ENABLE){
            Log.d(TAG, "positionServiceStatus:  ACTIVE");
        }
        else {
            Log.d(TAG, "positionServiceStatus:  DESACTIVE");
            UserLocation.checkLocationService(getApplicationContext(),myActivity);
        }
    }

    //**************************** NearbyPlacesListener INTERFACE  ***************************************//

    @Override
    public void onReceiveNearbyPlaces(List<RestoResult> restos) {
        MapFragment mapFrag = (MapFragment) viewPagerAdapter.getRegisteredFragments(0);
        ListViewFragment listFrag = (ListViewFragment) viewPagerAdapter.getRegisteredFragments(1);

        if (mapFrag != null && listFrag != null){
            Log.d(TAG, "onNext: FRAGMENT");
            mapFrag.setNearbyRestaurant(restos);
            listFrag.setDataToRecycler(restos,lastUserPosition);
        }
    }

    //**************************** RepositionClickListener INTERFACE ***************************************//
    @Override
    public void onRepositionButtonClick() {
        this.getUserLocation();
    }
}

