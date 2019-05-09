package com.android.guillaume.go4launch.utils.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class NotificationService {

    private Context context;

    public NotificationService(Context context) {
        this.context = context;
    }

    public void createJob(){
        long time;
        Calendar calCurrent = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        //verify if current time isn't superior to notification time
        if (calCurrent.before(cal)) {
            // If it's not notification time yet, start today
            time = cal.getTimeInMillis();
        } else {
            // start tomorrow
            cal.add(Calendar.DATE, 1);
            time = cal.getTimeInMillis();
        }

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, JobReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pi);
    }

    public void cancelJob() {
        Intent intent = new Intent(context, JobReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pi);
    }

}
