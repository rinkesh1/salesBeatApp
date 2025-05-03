package com.newsalesbeatApp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.sblocation.SbFusedLocation;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SbService extends Service {

    private static final int INTERVAL = 1000 * 60 * 5; // 5 minutes
    private static final int NOTIFICATION_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "com.newsalesbeat";
    private static final String TAG = "SbService";

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean isNotConnected = false;
    private ServerCall serverCall;
    private Timer timer;

    private final BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            isNotConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        }
    };

    private final Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (serverCall != null) {
                serverCall.syncLocation();
                serverCall.syncData();
                Log.e(TAG, "syncData: " + new Date());
            }
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }

        registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "NewSalesBeat", NotificationManager.IMPORTANCE_LOW);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(R.drawable.sb_logo)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION |
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        serverCall = new ServerCall(this);
        timer = new Timer();
        timer.schedule(createTimerTask(), 1000, 1000);

        if (!isNotConnected) {
            startRepeatingTask();
        }

        new SbFusedLocation(this);
        Log.e(TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        stopRepeatingTask();
        try {
            unregisterReceiver(mConnReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "onDestroy");
    }

    private void startRepeatingTask() {
        try {
            mHandlerTask.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRepeatingTask() {
        try {
            mHandler.removeCallbacks(mHandlerTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            public void run() {
                if (!isNotConnected) {
                    periodicCall();
                }
            }
        };
    }

    private void periodicCall() {
        if (serverCall != null) {
            // serverCall.syncData();
        }
    }
}
