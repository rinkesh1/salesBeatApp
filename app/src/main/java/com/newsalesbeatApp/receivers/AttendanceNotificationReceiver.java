package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.newsalesbeatApp.services.NotificationIntentService;

public class AttendanceNotificationReceiver extends BroadcastReceiver {

    String TAG = "NotificationReceiver";

    public AttendanceNotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(TAG, "onReceive");

        Intent intent1 = new Intent(context, NotificationIntentService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else {
            context.startService(intent1);
        }


    }
}