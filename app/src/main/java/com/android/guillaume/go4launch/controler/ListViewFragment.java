package com.android.guillaume.go4launch.controler;


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
import com.android.guillaume.go4launch.utils.RestaurantRecyclerAdapter;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment {

    @BindView(R.id.list_fragment_recyclerView) RecyclerView recyclerView;

    private RecyclerView.LayoutManager layoutManager;
    private RestaurantRecyclerAdapter recyclerAdapter;

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
        this.setRecyclerView();
    }

    private void setRecyclerView(){
        this.layoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerAdapter = new RestaurantRecyclerAdapter(new ArrayList<RestoResult>(), Glide.with(this));

    }

    public void setDataToRecycler(List<RestoResult> restoList){
        Log.d(TAG, "setDataToRecycler: ");
        this.recyclerAdapter.setRestos(restoList);
        this.recyclerAdapter = new RestaurantRecyclerAdapter(restoList, Glide.with(this));
        recyclerView.setAdapter(recyclerAdapter);
        this.recyclerAdapter.notifyDataSetChanged();

    }
}
