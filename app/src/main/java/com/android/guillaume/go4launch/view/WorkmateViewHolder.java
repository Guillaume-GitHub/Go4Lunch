package com.android.guillaume.go4launch.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.model.User;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.workmate_image) ImageView workmateImage;
    @BindView(R.id.workmate_text) TextView workmateText;

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void updateView(User user, RequestManager glide){

        this.workmateText.setText(user.getUserName());

        try{
            glide.load(user.getUrlPicture())
                    .circleCrop()
                    .into(this.workmateImage);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

}
