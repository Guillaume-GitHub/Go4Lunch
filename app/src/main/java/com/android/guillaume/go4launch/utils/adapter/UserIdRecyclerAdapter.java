package com.android.guillaume.go4launch.utils.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.view.WorkmateViewHolder;
import com.bumptech.glide.RequestManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserIdRecyclerAdapter extends RecyclerView.Adapter<WorkmateViewHolder> {

    private String TAG = this.getClass().getSimpleName();
    private List<String> usersID;
    private RequestManager glide;

    public UserIdRecyclerAdapter(List<String> usersID, RequestManager glide) {
        this.usersID = usersID;
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
        holder.updateView(this.usersID.get(position), this.glide);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return usersID.size();
    }

    public void setIdList(List<String> usersID){
        this.usersID = usersID;
    }
}
