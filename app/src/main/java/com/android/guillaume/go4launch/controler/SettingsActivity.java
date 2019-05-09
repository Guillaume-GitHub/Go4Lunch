package com.android.guillaume.go4launch.controler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.utils.notification.NotificationService;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.activity_settings_notification_textView) TextView notificationText;
    @BindView(R.id.activity_settings_notification_switch) Switch notificationSwitch;
    @BindView(R.id.activity_settings_notification_toolbar) Toolbar toolbar;

    private String TAG = getClass().getSimpleName();
    public static final String NOTIF_PREFS = "NOTIF_PREFS";
    public static final String KEY_NOTIF_PREFS = "KEY_NOTIF_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        this.setToolbar();
        this.getNotificationPrefs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.setToolbar();
        this.setCheckedChangeListener();
    }

    private void setToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Get notification's user preference an toggle swith
    private void getNotificationPrefs(){
        try{
            SharedPreferences sharedPref = getSharedPreferences(NOTIF_PREFS, Context.MODE_PRIVATE);

            // Check if notification prefs = Disable (O)
            if(sharedPref.getInt(KEY_NOTIF_PREFS,-1) == 0){
                this.setSwitchPositionOff();
            }

        }
        catch (NullPointerException e)
        {
            Log.w(TAG, "getNotificationPrefs: fail", e);
        }
    }

    // Toggle swith Button
    private void setSwitchPositionOff(){
        this.notificationSwitch.setChecked(false);
    }

    //Switch change Listener
    private void setCheckedChangeListener(){
        this.notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setNotificationPrefs(isChecked);
            }
        });
    }

    // SAVE notification's user prefs into SharedPrefs
    private void setNotificationPrefs(Boolean isChecked) {
        // SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(NOTIF_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        //NotificationService
        NotificationService notificationService= new NotificationService(this);

        if(isChecked){
            editor.putInt(KEY_NOTIF_PREFS,1);
            editor.apply();
            //create new alarm is user active notification
            notificationService.createJob();
        }
        else {
            editor.putInt(KEY_NOTIF_PREFS,0);
            editor.apply();
            //cancel the alarm is user deactivate notification
            notificationService.cancelJob();
        }
    }
}
