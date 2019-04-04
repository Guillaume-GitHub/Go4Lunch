package com.android.guillaume.go4launch.utils.adapter;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.restaurant.RestoResult;
import com.android.guillaume.go4launch.view.RestaurantViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private final String TAG = this.getClass().getSimpleName();

    private List<RestoResult> restos;
    private RequestManager glide;
    private Location userPosition;

    public RestaurantRecyclerAdapter(List<RestoResult> restoList, RequestManager glide, Location userPosition) {
        Log.d(TAG, "RestaurantRecyclerAdapter: ");
        this.restos = restoList;
        this.glide = glide;
        this.userPosition = userPosition;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_list_item, viewGroup, false);
        return new RestaurantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        Log.d(TAG, this.userPosition.toString());
        holder.updateView(this.restos.get(position),this.glide,userPosition);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+ restos.size());
        return this.restos.size();
    }

    public List<RestoResult> getRestos() {
        return restos;
    }

    public void setRestos(List<RestoResult> restos) {
        this.restos = restos;
    }

    public void setNewUserPosition(Location userPosition) {
        this.userPosition = userPosition;
    }
}
