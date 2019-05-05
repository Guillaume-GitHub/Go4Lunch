package com.android.guillaume.go4launch.controler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.adapter.MessageRecyclerAdapter;
import com.android.guillaume.go4launch.api.firebase.ChatHelper;
import com.android.guillaume.go4launch.model.ChatMessage;
import com.android.guillaume.go4launch.utils.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity implements EventListener<QuerySnapshot> {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.chat_activity_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.chat_activity_editText) EditText messageEditText;
    @BindView(R.id.chat_activity_imageBtn) ImageButton submitButton;

    //For Recycler View
    private LayoutManager layoutManager;
    private MessageRecyclerAdapter recyclerAdapter;

    //For Data
    private List<ChatMessage> savedMessageList;
    private QuerySnapshot docSnapshot;

    //Listener
    private ListenerRegistration messageListener;

    //SHARED PREFS
    private final String BADGES_PREFS = "BADGE_PREFS";
    private final String KEY_MESSAGE_PREFS = "KEY_MESSAGE_PREFS ";

    //EXTRA VALUES
    private final String EXTRA_MESSAGE_COUNT = "EXTRA_MESSAGE_COUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "onCreate: ");
        ButterKnife.bind(this);
        this.setRecyclerView();
        this.setMessageListener();
        this.onItemClickListener();
        this.setOnSubmitButtonClick();

    }

    @Override
    protected void onStop() {
        super.onStop();
        this.removeMessageListener();
        // SAVE number of chat's messages into Prefs
        SharedPreferences sharedPref = getSharedPreferences(BADGES_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_MESSAGE_PREFS,docSnapshot.size());
        editor.apply();
    }

    // Save number of message view by the user
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_MESSAGE_COUNT, docSnapshot.size());
        setResult(Activity.RESULT_OK,returnIntent);
        this.finish();
    }

    // ******************************************* RECYCLER VIEW **********************************//
    private void setRecyclerView(){
        Log.d(TAG, "setRecyclerView: ");
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);

        this.savedMessageList = new ArrayList<>();

        this.recyclerAdapter = new MessageRecyclerAdapter(this.savedMessageList,this);
        this.recyclerView.setAdapter(this.recyclerAdapter);

        this.recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.d(TAG, "onItemRangeInserted: ");
            layoutManager.smoothScrollToPosition(recyclerView,null,recyclerAdapter.getItemCount());
            }
        });
    }

    // *************************************  DATABASE LISTENER **********************************//

    // Listen all changes to real time update
    private void setMessageListener(){
        // Listen on this query
        this.messageListener = ChatHelper.getMessage().addSnapshotListener(this);
    }

    private void removeMessageListener(){
        //Remove Listener
        this.messageListener.remove();
    }

    // Callback For RealTime messages changing
    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

        if (e != null) {
            Log.w(TAG, "onEvent: ", e);
        }
        else {
            Log.d(TAG, "onEvent: success CHAT");
            docSnapshot = queryDocumentSnapshots;

            // Check all change
            for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                int position = dc.getOldIndex();
                switch (dc.getType()) {
                    case ADDED:
                        Log.d(TAG, "Added: " + dc.getDocument().getData());
                        savedMessageList.add(dc.getDocument().toObject(ChatMessage.class));
                        recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount() + 1);
                        break;
                    case MODIFIED:
                        Log.d(TAG, "Modified: " + dc.getDocument().getData());
                        savedMessageList.remove(position);
                        savedMessageList.add(position,dc.getDocument().toObject(ChatMessage.class));
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                    case REMOVED:
                        Log.d(TAG, "Removed: " + dc.getDocument().getData());
                        savedMessageList.remove(position);
                        // Notify adapter that datas changed
                        recyclerAdapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }


    // *************************************  DATABASE  **********************************//

    private void saveMessageToDatabase(String messageText){
        ChatHelper.saveMessage(messageText).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, R.string.message_send_fail, Toast.LENGTH_LONG).show();
            }
        });
    }

    // *************************************  UI CONTROLS **********************************//

    // Submit message and save it to firebase
    private void setOnSubmitButtonClick() {
        Log.d(TAG, "setOnSubmitButtonClick: ");
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!messageEditText.getText().toString().isEmpty()) {
                    saveMessageToDatabase(messageEditText.getText().toString());
                    messageEditText.setText("");
                }
            }
        });
    }

    // FOR Supress User message when Long clik on item
    private void onItemClickListener() {
        this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: ");
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                Log.d(TAG, "onLongItemClick: ");
                ChatMessage message = recyclerAdapter.getMessage(position);
                if (message.getUserSender().getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    String docId = docSnapshot.getDocuments().get(position).getId();
                    ChatHelper.deleteMessage(docId).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, R.string.message_supress_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }));
    }

}
