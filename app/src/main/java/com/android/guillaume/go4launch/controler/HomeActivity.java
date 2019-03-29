package com.android.guillaume.go4launch.controler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.utils.UserLocation;
import com.android.guillaume.go4launch.utils.UserLocationListener;
import com.android.guillaume.go4launch.model.restaurant.Restaurant;
import com.android.guillaume.go4launch.api.places.RestoFinderClient;
import com.android.guillaume.go4launch.utils.ViewPagerAdapter;
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
    @BindView(R.id.activity_home_viewPager)
    ViewPager viewPager;

    private TextView navDrawerEmail;
    private TextView navDrawerUsername;
    private ImageView navDrawerPicture;

    private ViewPagerAdapter viewPagerAdapter;

    private Disposable disposable;
    private Activity myActivity;

    private final String TAG = this.getClass().getSimpleName();
    private final int RC_LOCATION_PERMISSIONS = 100;

    // Fragment
    private MapFragment mapFragment;
    private ListViewFragment listViewFragment;

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
        viewPager.setAdapter(viewPagerAdapter);

        //viewPager.setCurrentItem(0);

        //this.navBottom.setSelectedItemId(R.id.navBottom_mapView);

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
    //*********************************** FRAGMENTS ********************************//
/*
    // display map Fragment
    private void displayMapFragment(){
        //Fragment frag = getSupportFragmentManager().findFragmentByTag("MAP_FRAGMENT");

       if (mapFragment != null && !mapFragment.isVisible()){
           getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment_frameLayout,mapFragment);
           Log.d(TAG, "MAP: OLD");
       }
       else {
           this.mapFragment = new MapFragment();
           getSupportFragmentManager()
                   .beginTransaction()
                   .addToBackStack("B")
                   .replace(R.id.activity_home_frameLayout,mapFragment,"MAP_FRAGMENT")
                   .commit();
           Log.d(TAG, "MAP: NEW");
       }
    }
*//*
    // display map Fragment
    private void displayListFragment(){
        //Fragment frag = getSupportFragmentManager().findFragmentByTag("LISTVIEW_FRAGMENT");

        if (listViewFragment != null && !listViewFragment.isVisible()){
            getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment_frameLayout,listViewFragment);
            Log.d(TAG, "LIST: OLD");
        }
        else {
            this.listViewFragment = new ListViewFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack("A")
                    .replace(R.id.activity_home_frameLayout,listViewFragment,"LISTVIEW_FRAGMENT")
                    .commit();
            Log.d(TAG, "LIST : NEW");
        }
    }

    //*********************************** METHODS ********************************///

    // Get the user logged
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Get the user position (Geolocation)
    private void getUserLocation(){
        UserLocation userLocation = new UserLocation(getApplicationContext(),locationCallback);
        userLocation.startGeolocation();
    }

    //*********************************** API REQUEST ********************************//

    @SuppressLint("CheckResult")
    private void getNearbyRestaurants(Location location){
        RestoFinderClient.getInstance()
                .getRestaurant(location)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Restaurant>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Restaurant restaurant) {
                        Log.d(TAG, "onNext: ");
                        MapFragment mapFrag = (MapFragment) viewPagerAdapter.getRegisteredFragments(0);

                        if (mapFrag != null){
                            Log.d(TAG, "onNext: FRAGMENT");
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

    //*********************************** CALLBACKS ********************************//

    // INTERFACE callback
    private UserLocationListener locationCallback = new UserLocationListener() {
        @Override
        public void newUserLocationDetected(Location location) {
            Log.d("TAG", "newUserLocationDetected: ");

            MapFragment mapFrag = (MapFragment) viewPagerAdapter.getRegisteredFragments(0);

            if(mapFrag != null) {
                Log.d(TAG, "newUserLocationDetected: FRAGMENT");
                mapFrag.setNewPosition(location);
            }
            getNearbyRestaurants(location);

        }

        // Ask permissions to user
        @Override
        public void permissionsDenied() {
            Log.d(TAG, "permissionsDenied: ");
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_LOCATION_PERMISSIONS);
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
    };

    //
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

    //*******************************************************************//
}

