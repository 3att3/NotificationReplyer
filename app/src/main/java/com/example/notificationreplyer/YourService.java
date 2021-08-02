package com.example.notificationreplyer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.notificationreplyer.NotificationPack.NotificationService;

import java.util.Timer;
import java.util.TimerTask;

public class YourService extends Service {

    private static final int NOTIFICATION_ID = 24211;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        // do your jobs here

        // not really needed, the important part is the NotificationService witch is running on background while the app is running
        // this class is only needed to keep the app alive

        ShPref shPref = new ShPref(this);
        if (shPref.getRunInBackground()){

            startForeground();

        }
        else {
            stopForeground(flags);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    PendingIntent pendingIntent;
    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        String channelName = "This background service is needed to keep the app up and running for listening on notifications and send them to your computer";

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET); // VISIBILITY_PRIVATE

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(channel);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.drawable.appimgdr)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(NOTIFICATION_ID, notification);


    }

    private void notificationBuilderMethod(){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.drawable.appimgdr)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }


}