package com.android.guillaume.go4launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guillaume.go4launch.utils.PermissionsHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {

    //Bind All Views
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.activity_home_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_navigationView) NavigationView navDrawer;
    @BindView(R.id.activity_home_bottomNavigation) BottomNavigationView navBottom;

    private TextView navDrawerEmail;
    private TextView navDrawerUsername;
    private ImageView navDrawerPicture;

    // For check Permission to start services
    PermissionsHelper permissionsHelper;

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
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.updateUserInfo();
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
                        showMap();
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

    private void displayFragment(){

        Fragment frag = new MapFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_home_frameLayout,frag)
                .commit();
    }

    // display map Fragment
    private void showMap() {
        // check if permission are allow to start map
        this.permissionsHelper = new PermissionsHelper(getApplicationContext(), this);
        permissionsHelper.askForPermission(Manifest.permission.ACCESS_FINE_LOCATION,PermissionsHelper.LOCATION);

        if (this.permissionsHelper.isGranted()) {
            displayFragment();
        }
    }
}
