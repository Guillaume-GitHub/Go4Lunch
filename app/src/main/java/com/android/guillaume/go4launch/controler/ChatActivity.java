package com.android.guillaume.go4launch.controler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.DialogInterface;
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
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.utils.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.chat_activity_recyclerView) RecyclerView recyclerView;
    @BindView(R.id.chat_activity_editText) EditText messageEditText;
    @BindView(R.id.chat_activity_imageBtn) ImageButton submitButton;

    //For Recycler View
    private LayoutManager layoutManager;
    private MessageRecyclerAdapter recyclerAdapter;

    //For Data
    public static final String EXTRA_USER = "EXTRA_USER";
    private User user;
    private List<ChatMessage> messageList;

    private QuerySnapshot docSnapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "onCreate: ");
        this.getUserFromExtraIntent();
        ButterKnife.bind(this);
        this.setRecyclerView();
        this.setMessageListener();
        this.onItemClickListener();
        this.setOnSubmitButtonClick();

    }

    private void getUserFromExtraIntent(){
        Log.d(TAG, "getUserFromExtraIntent: ");
        try{
            this.user = getIntent().getExtras().getParcelable(EXTRA_USER);
            Log.d(TAG, "getUserFromExtraIntent:"+ this.user.getUserName());
            if (this.user == null){
                Log.d(TAG, "getUserFromExtraIntent: NULL");
                this.finish();
            }
        }
        catch (NullPointerException e){
            Log.w(TAG, "onCreate: Can't Find this User", e);
            Toast.makeText(this.getParent().getApplication(), "Sorry, I can't find this User", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setRecyclerView(){
        Log.d(TAG, "setRecyclerView: ");
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(layoutManager);

        this.messageList = new ArrayList<>();

        this.recyclerAdapter = new MessageRecyclerAdapter(this.messageList,this.user,this);
        this.recyclerView.setAdapter(this.recyclerAdapter);

        this.recyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                Log.d(TAG, "onItemRangeInserted: ");
                layoutManager.smoothScrollToPosition(recyclerView,null,recyclerAdapter.getItemCount());
            }
        });
    }
    
    // Listen all changes to real time update
    private void setMessageListener(){
        if (this.user != null) {
            // Listen on this query
            ChatHelper.getMessage(this.user.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    docSnapshot = queryDocumentSnapshots;
                    // Check all change
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        int position = dc.getOldIndex();
                        switch (dc.getType()) {
                            case ADDED:
                                Log.d(TAG, "Added: " + dc.getDocument().getData());
                                messageList.add(dc.getDocument().toObject(ChatMessage.class));
                                recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount() + 1);
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified: " + dc.getDocument().getData());
                                messageList.remove(position);
                                messageList.add(position,dc.getDocument().toObject(ChatMessage.class));
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed: " + dc.getDocument().getData());
                                messageList.remove(position);
                                break;
                        }
                    }
                    // Notify adapter that datas changed
                    recyclerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void setOnSubmitButtonClick() {
        Log.d(TAG, "setOnSubmitButtonClick: ");
        this.submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageEditText.getText().toString().isEmpty()){
                    ChatHelper.sendMessage(user.getUid(),messageEditText.getText().toString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "onFailure: ", e);
                        }
                    });
                    //clear Box
                    messageEditText.setText("");
                }
            }
        });
    }

    private void onItemClickListener(){
        this.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: ");
            }

            @Override
            public void onLongItemClick(View view, final int position) {
                Log.d(TAG, "onLongItemClick: ");
                    String docId = docSnapshot.getDocuments().get(position).getId();
                    ChatHelper.deleteMessage(docId).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, R.string.message_supress_error, Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }));
    }

}
