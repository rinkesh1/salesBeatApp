package com.newsalesbeatApp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.sblocation.SbFusedLocation;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class SbService_123 extends Service {

    private final static int INTERVAL = 1000 * 60 * 5; //5 minutes
    Handler mHandler = new Handler();
    private String TAG = "SbService";
    private ServerCall serverCall;
    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {

            if (serverCall != null) {
                serverCall.syncLocation();
                serverCall.syncData(); //@Umesh 22-08-2022
                Log.e(TAG, "syncData:"+new Date());
            }
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };
    private Timer timer;
    private boolean isNotConnected = false;
    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            isNotConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.newsalesbeat";
        String channelName = "NewSalesBeat";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.sb_logo)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        serverCall = new ServerCall(this);
        timer = new Timer();
        timer.schedule(createTimerTask(), 1000, 1000);

        if (!isNotConnected)
            startRepeatingTask();

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

        timer.cancel();

        stopRepeatingTask();

        try {

            this.unregisterReceiver(this.mConnReceiver);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "onDestroy ");
        super.onDestroy();
    }

    void startRepeatingTask() {

        try {
            mHandlerTask.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void stopRepeatingTask() {

        try {
            mHandler.removeCallbacks(mHandlerTask);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            public void run() {

                if (!isNotConnected)
                    periodicCall();
            }
        };
    }

    private void periodicCall() {

        if (serverCall != null) {
            //serverCall.syncData(); //@Umesh 22-08-2022
        }
    }
}