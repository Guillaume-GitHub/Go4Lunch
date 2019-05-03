package com.android.guillaume.go4launch.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.ChatMessage;
import com.android.guillaume.go4launch.model.User;
import com.android.guillaume.go4launch.view.MessageViewHolder;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageViewHolder> {
    
    private final String TAG = this.getClass().getSimpleName();

    private List<ChatMessage> messagesList;
    private RequestManager glide;
    private Context context;
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public MessageRecyclerAdapter(List<ChatMessage> messagesList, Context context)  {
        Log.d(TAG, "MessageRecyclerAdapter: ");
        this.messagesList = messagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_chat_item,viewGroup,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        holder.updateView(this.currentUserID,this.messagesList.get(position),this.context);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + this.messagesList.size());
        return this.messagesList.size();
    }

    public ChatMessage getMessage(int position){
        try {
            return this.messagesList.get(position);
        }
        catch (NullPointerException e){
            Log.w(TAG, "getMessage: Failed ! ",e );
            return  null;
        }
    }
}
