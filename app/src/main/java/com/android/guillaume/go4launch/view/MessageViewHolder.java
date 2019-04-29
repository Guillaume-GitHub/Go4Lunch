package com.android.guillaume.go4launch.view;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.ChatMessage;
import com.android.guillaume.go4launch.model.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private final String TAG = this.getClass().getSimpleName();

    @BindView(R.id.chat_activity_root_container) RelativeLayout rootContainer;
    @BindView(R.id.chat_activity_text_message_container) LinearLayout messageContainer;
    @BindView(R.id.chat_activity_text_message_container_textView) TextView messageText;
    @BindView(R.id.chat_activity_image_profile_imageView) ImageView imageProfile;
    @BindView(R.id.chat_activity_text_message_container_textView_date) TextView dateText;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        Log.d(TAG, "MessageViewHolder: ");
        ButterKnife.bind(this,itemView);
    }


    public void updateView(ChatMessage message, User user, Context context){

        // Apply Style
        if (message.getReceiverId().equals(user.getUid())){
            this.applySenderStyle(context);
        }

        // Load Image for user receiver
        switch (this.imageProfile.getVisibility()){

            case View.VISIBLE:
                this.loadImageProfile(user,context);
                break;

            case View.GONE:
            case View.INVISIBLE:
            default:
                break;
        }

        //Update message Text
        this.setMessageText(message.getMessageText());

        // Update date text
        if(message.getDateCreated() != null) {
            this.setMessageDate(message.getDateCreated());
        }

    }

    private void applySenderStyle(Context context){
        this.imageProfile.setVisibility(View.GONE);
        this.rootContainer.setGravity(Gravity.END);
        this.messageContainer.setBackground(context.getResources().getDrawable(R.drawable.radius_corner_background_grey));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.chat_activity_text_message_container);
        params.addRule(RelativeLayout.ALIGN_PARENT_START, R.id.chat_activity_text_message_container);
        this.dateText.setLayoutParams(params);
        this.messageText.setTextColor(context.getResources().getColor(R.color.grey_800));
    }


    private void loadImageProfile(User user, Context context){
        try{

            Glide.with(context).load(user.getUrlPicture())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(this.imageProfile);
        }
        catch (NullPointerException e){
            Log.w(TAG, "loadImageProfile: Failed ! ", e);
        }
    }

    private void setMessageText(String text){
        // Set message Text
        this.messageText.setText(text);
    }

    private void setMessageDate(Date date){
        SimpleDateFormat dateFormat;
        if(Locale.getDefault().getDisplayLanguage().equals("fr")){
            dateFormat = new SimpleDateFormat("HH:mm");
        }
        else {
            dateFormat = new SimpleDateFormat("hh:mm a");
        }
        this.dateText.setText(dateFormat.format(date));
    }
}
