package com.android.guillaume.go4launch.controler;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.utils.ImageRecyclerItemClickListener;
import com.android.guillaume.go4launch.adapter.RestaurantRecyclerAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {

    @BindView(R.id.list_fragment_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.list_fragment_message_container) LinearLayout messageContainer;

    private RecyclerView.LayoutManager layoutManager;
    private RestaurantRecyclerAdapter recyclerAdapter;

    private ImageRecyclerItemClickListener RecyclerItemClickListener;

    private final String TAG = this.getClass().getSimpleName();

    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setOnRecyclerItemClick();
        this.setRecyclerView();
    }

    // Display message when list is empty
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Log.d(TAG, "setUserVisibleHint: ");
            if(this.recyclerAdapter.getItemCount() == 0){
                this.messageContainer.setVisibility(View.VISIBLE);
                this.recyclerView.setVisibility(View.GONE);
            }
            else {
                this.messageContainer.setVisibility(View.GONE);
                this.recyclerView.setVisibility(View.VISIBLE);
            }
        }

    }

    private void setRecyclerView(){
        this.layoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerAdapter = new RestaurantRecyclerAdapter(new ArrayList<RestoResult>(), Glide.with(this),null, this.RecyclerItemClickListener);
    }

    public void setDataToRecycler(List<RestoResult> restoList,Location userPosition){
        this.recyclerAdapter.setRestos(restoList);
        this.recyclerAdapter.setNewUserPosition(userPosition);
        this.recyclerView.setAdapter(recyclerAdapter);
        this.recyclerAdapter.notifyDataSetChanged();
    }

    private void runDetailsActivityIntent(RestoResult restaurant){
        Intent intent = new Intent(this.getContext(),DetailsActivity.class);
        intent.putExtra("PLACE_ID",restaurant.getPlaceId());
        startActivity(intent);
    }

    private void setOnRecyclerItemClick(){
        this.RecyclerItemClickListener = new ImageRecyclerItemClickListener() {
            @Override
            public void onRecyclerItemClick(int position) {
                runDetailsActivityIntent(recyclerAdapter.getRestos().get(position));
            }
        };
    }
}
