package com.android.guillaume.go4launch.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.view.WorkmateViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WorkmateRecyclerAdapter extends RecyclerView.Adapter<WorkmateViewHolder> {

    private String TAG = this.getClass().getSimpleName();
    private List<User> users;
    private RequestManager glide;

    public WorkmateRecyclerAdapter(List<User> users, RequestManager glide) {
        this.users = users;
        this.glide = glide;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_workmate_item, viewGroup, false);
        return new WorkmateViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.updateView(this.users.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return users.size();
    }

    public void setUserList(List<User> users){
        this.users = users;
    }


    public User getUser(int position){
        try {
            return this.users.get(position);
        }
        catch (NullPointerException e){
            Log.w(TAG, "getUser: Failed ! ",e );
            return  null;
        }
    }
}
