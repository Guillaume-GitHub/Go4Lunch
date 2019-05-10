package com.android.guillaume.go4launch.controler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.firebase.ChatHelper;
import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.model.UserLunch;
import com.android.guillaume.go4launch.adapter.WorkmateRecyclerAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
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
    @BindView(R.id.workmate_fragment_swipeRefresh) SwipeRefreshLayout swipeRefresh;

    private RecyclerView.LayoutManager layoutManager;
    private WorkmateRecyclerAdapter recyclerAdapter;

    private final String TAG = this.getClass().getSimpleName();

    private List<User> userList;
    private User user;

    //LISTENER
    private ListenerRegistration messageListener;

    //SHARED PREFS
    private final String BADGES_PREFS = "BADGE_PREFS";
    private final String KEY_MESSAGE_PREFS = "KEY_MESSAGE_PREFS ";

    //EXTRA VALUES
    private final String EXTRA_MESSAGE_COUNT = "EXTRA_MESSAGE_COUNT";
    private int messageCount;


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
        this.setSwipeToRefresh();
        this.fetchUsersFromFirebase();
        this.getPreferences();
        this.setMessageListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.badgeText.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStop() {
        this.badgeText.setVisibility(View.INVISIBLE);
        this.removeMessageListener();
        super.onStop();
    }

    //***************************************** RECYCLER VIEW  *****************************************//

    private void setRecyclerView(){
        this.layoutManager = new LinearLayoutManager(getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.userList = new ArrayList<>();
        this.recyclerAdapter = new WorkmateRecyclerAdapter(this.userList,Glide.with(this));
        this.recyclerView.setAdapter(this.recyclerAdapter);
    }

    //*****************************************  FIREBASE DATAS *****************************************//
    public void fetchUsersFromFirebase(){
        UserHelper.getAllUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Clean list
                userList.clear();

                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()){
                    // Create new User with data fetch to firebase
                    user = new User(
                            doc.getString("uid"), // set uid value
                            doc.getString("userName"), // set userName value
                            doc.getString("email"), // set email value
                            doc.getString("urlPicture"),
                            doc.get("lunch",UserLunch.class),
                            (List<String>) doc.get("like")); // set urlPicture value

                    if(!user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        // add User just create to list if not current user
                        userList.add(user);
                    }
                }
                // send list of users to recyclerView
                recyclerAdapter.notifyDataSetChanged();
                Log.d(TAG, "onSuccess: " + userList.toString());
                swipeRefresh.setRefreshing(false);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }


    // ************************************************ CONTROLS *******************************************//
    @OnClick(R.id.workmate_fragment_floating_btn_chat)
    public void onChatBtnClick(){
        startChatActivity();
    }

    // ************************************************  DATABASE LISTENER **********************************//

    //Set Listener
    private void setMessageListener(){
        Log.d(TAG, "setMessageListener: WORKMATE");
        this.messageListener = ChatHelper.getMessage().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "onEvent: fail", e);
                }
                else {
                    Log.d(TAG, "onEvent: success WORKMATE");
                    if (queryDocumentSnapshots != null ){
                        int nbNewMessage = queryDocumentSnapshots.size();
                        showBadge(nbNewMessage);
                    }
                }
            }
        });
    }

    // Remove Listener
    private void removeMessageListener(){
        this.messageListener.remove();
    }

    // ***************************************** UI UPDATE *******************************************//

    // SHOW new messages's badge
    private void showBadge(int nbMessage){
        if(this.messageCount < nbMessage  && (nbMessage - this.messageCount ) > 0){
            this.badgeText.setVisibility(View.VISIBLE);
            this.badgeText.setText(String.valueOf(nbMessage - this.messageCount ));
        }
    }

    // ***************************************** ACTIVITY CALLBACK *******************************************//

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 250) {
            if(resultCode == Activity.RESULT_OK){
                Log.d(TAG, "onActivityResult: resultOK");
                this.messageCount = data.getIntExtra(EXTRA_MESSAGE_COUNT,0);
                this.setMessageListener();
            }
            else {
                Log.d(TAG, "onActivityResult: resultCancel");
                getPreferences();
                this.setMessageListener();
            }
        }
    }

    // ***************************************** SHARED PREFERENCES *******************************************//

    // Get last number of messages view by the user
    private void getPreferences(){
        try{
            SharedPreferences sharedPref = getContext().getSharedPreferences(BADGES_PREFS, Context.MODE_PRIVATE);
            this.messageCount = sharedPref.getInt(KEY_MESSAGE_PREFS,0);
        }
        catch (NullPointerException e)
        {
            Log.w(TAG, "getPreferences: fail", e);
        }
    }

    // ***************************************** INTENT *******************************************//
    private void startChatActivity() {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        startActivityForResult(intent,250);
    }
    // ***************************************** SWIPE TO REFRESH DATA *******************************************//
    private void setSwipeToRefresh(){
        this.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: ");
                fetchUsersFromFirebase();
            }
        });
    }

}
