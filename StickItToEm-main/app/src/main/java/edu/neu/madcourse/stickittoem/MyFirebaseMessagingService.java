package edu.neu.madcourse.stickittoem;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String CHANNEL_ID = "CHANNEL_ID";
    private static final String CHANNEL_NAME = "CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "CHANNEL_DESCRIPTION";


    @Override
    public void onNewToken(String newToken) {
        super.onNewToken(newToken);
        Log.d(TAG, "Refreshed token: " + newToken);
        // TODO update token
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);
        }
        extractPayloadDataForegroundCase(remoteMessage);
    }


    private void showNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else {
            builder = new NotificationCompat.Builder(this);
        }
        notification = builder.setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0, notification);
    }

    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void extractPayloadDataForegroundCase(RemoteMessage remoteMessage) {
        if (remoteMessage.getData() != null) {
            postToastMessage(remoteMessage.getData().get("title"));
        }
    }

}
