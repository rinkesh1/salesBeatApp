package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.newsalesbeatApp.services.MyFirebaseMessagingService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, MyFirebaseMessagingService.class);
        context.startService(startServiceIntent);
    }
}
