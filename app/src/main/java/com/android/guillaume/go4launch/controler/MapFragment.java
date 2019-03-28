package com.android.guillaume.go4launch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.model.restaurant.Restaurant;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.utils.RestoFinderClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements UserLocationListener {

    @BindView(R.id.floating_btn)
    FloatingActionButton myPositionButton;

    private static String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private SupportMapFragment mapFragment;
    private Location newPosition;
    private GoogleMapManager googleMapManager;
    private UserLocation userLocation;
private Disposable disposable;
    public MapFragment() {
        // Required empty public constructor
    }

    //************************************** LIFECYCLE METHODS *****************************//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");

        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.fragment_map_container, container, false);
        ButterKnife.bind(this, rootView);

        //use SupportMapFragment for using in fragment instead of activity
        this.mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        return this.rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.googleMapManager = new GoogleMapManager(this.mapFragment);
    }

    @OnClick(R.id.floating_btn)
    public void onFloatButtonClick(){
        if(newPosition != null){
            LatLng latLng = new LatLng(newPosition.getLatitude(),newPosition.getLongitude());
            this.googleMapManager.repositioningCamera(latLng);
        }
        else{
            userLocation = new UserLocation(getContext(),this);
            userLocation.startGeolocation();
        }
    }


    public void setNearbyRestaurant(List<RestoResult> restos){
        if(restos != null || !restos.isEmpty()){
            this.googleMapManager.addRestaurantMarker(restos);
        }
        else{
            //TODO : No restaurant found message
        }
    }

    public void setNewPosition(Location newPosition) {
        this.newPosition = newPosition;
        this.googleMapManager.addUserMarker(newPosition);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    Log.d("FRAGMENT", "Permission Granted ");
                    this.userLocation.startGeolocation();
                } else {
                    // permission denied
                    // Disable the map functionality
                }
                return;
            }
            // ...
        }
    }


    @Override
    public void newUserLocationDetected(Location location) {
        this.setNewPosition(location);
        this.getNearbyRestaurant();
    }

    @Override
    public void permissionsDenied() {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("TAG", "permissionsDenied: request");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    @Override
    public void positionServiceStatus(int statusCode) {
        if(statusCode == UserLocation.LOCATION_ENEABLE){
            Log.d("TAG", "positionServiceStatus:  ACTIVE");
        }
        else {
            Log.d("TAG", "positionServiceStatus:  DESACTIVE");
            UserLocation.checkLocationService(getContext(),getActivity());
        }

    }


    private void getNearbyRestaurant(){
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
                        Log.d("FRAGMENT", "onNext: ");
                            setNearbyRestaurant(restaurant.getResults());
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
