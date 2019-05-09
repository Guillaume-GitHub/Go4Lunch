package com.android.guillaume.go4launch.utils.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class JobReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationBuilder notificationBuilder = new NotificationBuilder(context);
        notificationBuilder.sendNotification();
    }
}
