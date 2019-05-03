package com.android.guillaume.go4launch.controler;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.firebase.ChatHelper;
import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.ChatMessage;
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.model.UserLunch;
import com.android.guillaume.go4launch.utils.RecyclerItemClickListener;
import com.android.guillaume.go4launch.adapter.WorkmateRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkmateFragment extends Fragment {

    @BindView(R.id.workmate_fragment_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.workmate_fragment_floating_btn_chat) FloatingActionButton chatFloatingBtn;
    @BindView(R.id.workmate_fragment_badge_frame) TextView badgeText;


    private RecyclerView.LayoutManager layoutManager;
    private WorkmateRecyclerAdapter recyclerAdapter;

    private final String TAG = this.getClass().getSimpleName();

    private List<User> userList;
    private User user;

    public WorkmateFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workmate, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.setRecyclerView();
        this.fetchUsersFromFirebase();
    }

    private void setRecyclerView(){
        this.layoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerAdapter = new WorkmateRecyclerAdapter(new ArrayList<User>(),Glide.with(this));
    }

    public void setDataToRecycler(List<User> users){
        this.recyclerAdapter.setUserList(users);
        this.recyclerView.setAdapter(recyclerAdapter);
        this.recyclerAdapter.notifyDataSetChanged();
    }

    private void fetchUsersFromFirebase(){
        UserHelper.getAllUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userList = new ArrayList<>();

                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    // Create new User with data fetch to firebase
                    user = new User(
                            doc.getString("uid"), // set uid value
                            doc.getString("userName"), // set userName value
                            doc.getString("email"), // set email value
                            doc.getString("urlPicture"),
                            doc.get("lunch",UserLunch.class),
                            (List<String>) doc.get("like")); // set urlPicture value

                    // add User just create to list
                    userList.add(user);
                }
                // send list of users to recyclerView
                setDataToRecycler(userList);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
/*
    private void setOnClickRecyclerItem(){
        this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this.getContext(),
                this.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: ");
                startChatActivity(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    }
    */

    @OnClick(R.id.workmate_fragment_floating_btn_chat)
    public void onChatBtnClick(){
        startChatActivity();
    }

    private void startChatActivity() {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        startActivity(intent);
    }

}
