package com.android.guillaume.go4launch.utils.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.android.guillaume.go4launch.MainActivity;
import com.android.guillaume.go4launch.R;
import com.android.guillaume.go4launch.api.firebase.RestaurantHelper;
import com.android.guillaume.go4launch.api.firebase.UserHelper;
import com.android.guillaume.go4launch.model.DatabaseRestaurantDoc;
import com.android.guillaume.go4launch.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationBuilder {
    private static final String CHANNEL_ID = "GO4LUNCH_APP" ;
    private String TAG = getClass().getSimpleName();

    private User currentUser;
    private DatabaseRestaurantDoc restaurantDoc;
    private List<User> userList;
    private Context context;

    public NotificationBuilder(Context context) {
        this.context = context;
        this.createNotificationChannel();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(){
        this.fetchData();
    }

    // Get user Lunch
    private void fetchData(){
        FirebaseApp app = FirebaseApp.initializeApp(context);

        UserHelper.getUser(FirebaseAuth.getInstance(app).getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                if(currentUser != null && currentUser.getLunch() != null){
                    if (currentUser.getLunch().getDate().equals(UserHelper.currentDate)) {
                        fetchRestaurant(currentUser.getLunch().getPlaceID());
                    }
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "fetchData() -> onFailure: ", e);
            }
        });
    }

    // Get workmates
    private void fetchRestaurant(String placeID){
        RestaurantHelper.getRestaurantDocumentAtDate(Calendar.getInstance().getTime(), placeID)
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        restaurantDoc = queryDocumentSnapshots.getDocuments().get(0).toObject((DatabaseRestaurantDoc.class));
                        if (restaurantDoc != null && restaurantDoc.getUsers() != null){
                            userList = new ArrayList<>();
                            for (String userId : restaurantDoc.getUsers()) {
                                UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);
                                        if (user != null && !user.getUid().equals(currentUser.getUid())){
                                            userList.add(user);
                                        }
                                    }
                                });
                            }

                            new Thread(new Runnable() {
                                public void run() {
                                    android.os.SystemClock.sleep(3000);
                                    createNotificationContent();
                                }
                            }).start();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "fetchRestaurant() -> onFailure: ",e);
                    }
                });
    }

    // Notification Body
    private void createNotificationContent(){

        switch (this.userList.size()){
            case 0:
                this.buildAndShowNotification(context.getString(R.string.notification_rendez_vous)
                        + " " + currentUser.getLunch().getName()
                        + " - " + currentUser.getLunch().getAddress());
                break;

            default:
               this.buildAndShowNotification(context.getString(R.string.notification_rendez_vous)
                       + " " + currentUser.getLunch().getName()
                       + " - " + currentUser.getLunch().getAddress()
                       + " " + context.getString(R.string.notification_rendez_vous_with) + "\n" + getUsersStr());
                break;
        }
    }

    // Get a string with list of workmates
    private String getUsersStr(){
        StringBuilder stringBuilder = new StringBuilder();
        for (User user : userList) {
        stringBuilder.append(user.getUserName());
        stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }


    // Create and show notification
    private void buildAndShowNotification(String content){

        // Create an explicit intent
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.filled_restaurant_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo))
                .setContentTitle(context.getString(R.string.notification_title))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }

}
