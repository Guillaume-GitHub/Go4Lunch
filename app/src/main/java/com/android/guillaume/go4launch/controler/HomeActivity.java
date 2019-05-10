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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.firebase.RestaurantHelper;
import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.DatabaseRestaurantDoc;
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.utils.ApproximateRectBounds;
import com.android.guillaume.go4launch.utils.GoogleMapManager;
import com.android.guillaume.go4launch.utils.NearbyPlaces;
import com.android.guillaume.go4launch.utils.NearbyPlacesListener;
import com.android.guillaume.go4launch.utils.RepositionClickListener;
import com.android.guillaume.go4launch.utils.UserLocation;
import com.android.guillaume.go4launch.utils.UserLocationListener;
import com.android.guillaume.go4launch.adapter.RestaurantRecyclerAdapter;
import com.android.guillaume.go4launch.adapter.ViewPagerAdapter;
import com.android.guillaume.go4launch.utils.notification.NotificationService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Calendar;
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

    private Boolean viewRestart;
    private List<RestoResult> restosList;

    private final int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        this.configureToolbar();
        this.configureNavigationDrawer();
        this.configureBottomNavigation();
        this.inflateNavDrawerHeaderItems();
        this.applySettings();
        this.viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        this.viewPager.setAdapter(viewPagerAdapter);
        this.viewPager.setOffscreenPageLimit(this.viewPagerAdapter.NB_PAGE);

        this.myActivity = this;
        this.viewRestart = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!this.viewRestart){
            this.updateUserInfo();
            this.getUserLocation();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.viewRestart = true;
    }

    //*********************************** NAVIGATION ********************************//

    // Catch click on navigation drawer Items
    private void configureNavigationDrawer(){
        this.navDrawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // close drawer when item is tapped
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (item.getItemId()) {
                    case R.id.menu_lunch_item:
                        // Show user Lunch
                        getUserLunch();
                        break;
                    case R.id.menu_settings_item:
                        // Show Setting Menu
                        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
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
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.navBottom_listView:
                        // ...
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.navBottom_workmates:
                        // ...
                        viewPager.setCurrentItem(2);
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

            case R.id.toolbar_main_search_button:
                this.launchPlaceAutocomplete();
                break;
            case R.id.toolbar_main_menu_button:
                Toast.makeText(myActivity, "REFRESHING", Toast.LENGTH_SHORT).show();
                this.refreshData();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main_menu, menu);
        return true;
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

    private void applySettings(){
        SharedPreferences sharedPref = getSharedPreferences(SettingsActivity.NOTIF_PREFS, Context.MODE_PRIVATE);
        if (sharedPref.getInt(SettingsActivity.KEY_NOTIF_PREFS,-1) == -1){
            NotificationService notificationService = new NotificationService(this);
            notificationService.createJob();
        }
    }

    // Refresh data on refresh button menu click
    private void refreshData(){
        switch (this.viewPager.getCurrentItem()){
            //Mapview and ListList do the same thing
            case 0 :
            case 1 :
                this.getUserLocation();
                break;
            //Fetch new users data
            case 2 :
                WorkmateFragment frag = (WorkmateFragment) this.viewPagerAdapter.getRegisteredFragments(2);
                frag.fetchUsersFromFirebase();
                break;
            default:
                break;
        }
    }

    //*********************************** METHODS ********************************///

    // Get the user logged
    private FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    // Get the user position (Geolocation)
    public void getUserLocation(){
        Log.d(TAG, "getUserLocation: ");
        UserLocation userLocation = new UserLocation(getApplicationContext(),this);
        userLocation.startGeolocation();
    }

    // Fetch restaurants nearby user position (2km around position)
    public void getNearbyRestaurant(Location location){
        NearbyPlaces nearbyPlaces = new NearbyPlaces(this);
        nearbyPlaces.getNearbyRestaurant(location);
    }

    public void getUserLunch(){
        UserHelper.getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if(user != null){
                    if (user.getLunch() != null && user.getLunch().getDate().equals(UserHelper.currentDate)) {
                        Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
                        intent.putExtra("PLACE_ID", user.getLunch().getPlaceID());
                        startActivity(intent);
                    }
                    else{
                        //NO RESTAURANT SELECTED TODAY
                        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_home_viewPager),getString(R.string.lunch_not_selected),Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                else{
                    // ERROR USER NULL
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_home_viewPager),getString(R.string.lunch_fetching_error),Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "onFailure: ", e);
                    }
                });
    }

    private void launchPlaceAutocomplete(){
        //Don't launch autocomplete when user is in workmates view
        if (this.viewPager.getCurrentItem() != 2){
            // Init Places
            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), getResources().getString(R.string.default_web_api_key));
            }

            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);


            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .setLocationRestriction(ApproximateRectBounds.getRectBounds(this.lastUserPosition.getLatitude(),this.lastUserPosition.getLongitude()))
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
        }

    }

    // Filter places with place autocomplete
    private void filterResto(String placeID){
        if(this.restosList != null){
            for (RestoResult resto : this.restosList) {
                if(resto.getPlaceId().equals(placeID)){

                    LatLng latLng = new LatLng(resto.getRestoGeometry().getLocation().getLat(),resto.getRestoGeometry().getLocation().getLng());

                    switch (viewPager.getCurrentItem()){
                        case 0:
                            MapFragment mapFragment = (MapFragment) this.viewPagerAdapter.getRegisteredFragments(viewPager.getCurrentItem());
                            mapFragment.moveToMarker(latLng,GoogleMapManager.ZOOM_BUILDINGS);
                            break;
                        case 1:
                            ListViewFragment listFragment = (ListViewFragment) this.viewPagerAdapter.getRegisteredFragments(viewPager.getCurrentItem());
                            RestaurantRecyclerAdapter adapter =  (RestaurantRecyclerAdapter) listFragment.recyclerView.getAdapter();
                            listFragment.recyclerView.smoothScrollToPosition(adapter.getRestaurantPosition(resto.getPlaceId()));
                            listFragment.recyclerView.scrollTo(listFragment.recyclerView.getScrollX(),listFragment.recyclerView.getScrollY() + 70);
                            break;
                        default:
                            break;
                    }
                }
                // LOOP ...
            }
        }
    }

    //*********************************** CALLBACKS ********************************//

    // Catch Activity DetailsRestaurantResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    this.filterResto(place.getId());
                }
                break;

            default:
                break;
        }
    }

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
    public void onReceiveNearbyPlaces(final List<RestoResult> restos) {
        this.restosList = restos;
        Log.d(TAG, "onReceiveNearbyPlaces : " + restos.size() + "places");
        // Get List of documents from Cloud Firestore Database (Filter by date)
        RestaurantHelper.getAllRestaurantDocumentsAtDate(Calendar.getInstance().getTime())
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()) {
                            // No result from database request -> set restaurant list from Google Nearby Places to Fragments
                            Log.d(TAG, "onSuccess: is EMPTY");
                            setDatasToFragment(restos);
                        }
                        else {
                            // Get DetailsRestaurantResult of database request and set it to compare method
                            List<DatabaseRestaurantDoc> list = queryDocumentSnapshots.toObjects(DatabaseRestaurantDoc.class);
                            compareRestoDataList(restos, list);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        // on fail -> set restaurant list from Google Nearby Places to Fragments
                        setDatasToFragment(restos);
                    }
                });
    }

    // Set list of restaurants to MapFragment & ListFragment
    private void setDatasToFragment(List<RestoResult> restos){
        MapFragment mapFrag = (MapFragment) viewPagerAdapter.getRegisteredFragments(0);
        ListViewFragment listFrag = (ListViewFragment) viewPagerAdapter.getRegisteredFragments(1);

        if (mapFrag != null && listFrag != null){
            mapFrag.setNearbyRestaurant(restos);
            listFrag.setDataToRecycler(restos,lastUserPosition);
        }
    }

    private void compareRestoDataList(List<RestoResult> apiRestoList,List<DatabaseRestaurantDoc> databaseRestolist){
        for(int i = 0; i < apiRestoList.size(); i++){
            Log.d(TAG, "apiRestoList -> position : " + i);
            if (databaseRestolist.isEmpty()){
                Log.d(TAG, "databaseRestoList = 0 --> BREAK");
                break;
            }
            else{
                for(int y = 0; y < databaseRestolist.size(); y++ ) {
                    Log.d(TAG, "databaseRestoList -> position: " + y);
                    if(apiRestoList.get(i).getPlaceId().equals(databaseRestolist.get(y).getPlaceID())){
                        // Add nb of workmates in RestoResult Object (-1 if current user is present in list)
                        if(databaseRestolist.get(y).getUsers().contains(getCurrentUser().getUid())){
                            apiRestoList.get(i).setNbWorkmate(databaseRestolist.get(y).getUsers().size() - 1);
                        }
                        else{
                            apiRestoList.get(i).setNbWorkmate(databaseRestolist.get(y).getUsers().size());
                        }
                        //Add list of IDs in RestoResult Object
                        apiRestoList.get(i).setUserIdList(databaseRestolist.get(y).getUsers());
                        // Delete match item to reduce research list size
                        databaseRestolist.remove(databaseRestolist.get(y));
                        break;
                    }
                    // ..... LOOP
                }
            }
            // ..... LOOP
        }
        Log.d(TAG, "All datas have been compared");
        setDatasToFragment(apiRestoList);
    }
    //**************************** RepositionClickListener INTERFACE ***************************************//

    @Override
    public void onRepositionButtonClick() {
        Log.d(TAG, "onRepositionButtonClick: ");
        this.getUserLocation();
    }

}
