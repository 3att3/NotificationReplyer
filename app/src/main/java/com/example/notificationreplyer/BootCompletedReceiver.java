package com.example.notificationreplyer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.w("boot_broadcast_poc", "starting service...");
        context.startService(new Intent(context, BackgroundService.class));
    }

}
