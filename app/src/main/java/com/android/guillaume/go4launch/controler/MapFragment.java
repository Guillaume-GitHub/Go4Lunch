package com.android.guillaume.go4launch.controler;

import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.utils.GoogleMapCallbacks;
import com.android.guillaume.go4launch.utils.GoogleMapManager;
import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.utils.RepositionClickListener;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MapFragment extends Fragment implements GoogleMapCallbacks {

    @BindView(R.id.floating_btn)
    FloatingActionButton myPositionButton;

    private static String TAG = MapFragment.class.getSimpleName();
    private View rootView;
    private SupportMapFragment mapFragment;
    private Location newPosition;
    private GoogleMapManager googleMapManager;

    private RepositionClickListener callback;

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

        this.callback = (RepositionClickListener) getActivity();

        return this.rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.googleMapManager = new GoogleMapManager(this.mapFragment, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    //************************************** UI CONTROLS *****************************//

    @OnClick(R.id.floating_btn)
    public void onFloatButtonClick(){

        if(newPosition != null){
            LatLng latLng = new LatLng(newPosition.getLatitude(),newPosition.getLongitude());
            this.googleMapManager.repositioningCamera(latLng);
        }
        else{
            this.callback.onRepositionButtonClick();
        }
    }

    //************************************** METHODS *****************************//

    public void setNearbyRestaurant(final List<RestoResult> restos){
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
    public void onClickRestaurantWindowMarker(RestoResult restaurant) {
        startActivity(DetailsActivity.getDetailsActivityIntent(getContext(),restaurant));
    }
}
