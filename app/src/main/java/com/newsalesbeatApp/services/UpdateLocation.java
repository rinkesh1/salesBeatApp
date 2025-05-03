package com.newsalesbeatApp.services;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.newsalesbeatApp.netwotkcall.ServerCall;

import java.util.Timer;
import java.util.TimerTask;

/*
 * Created by abc on 9/22/18.
 */

public class UpdateLocation extends Service {

    private static final String DEBUG_TAG = "MyService";
    private ServiceHandler mServiceHandler;
    private LocationManager mgr;
    private Locationer gps_locationer, network_locationer;
    private ServerCall serverCall;

    @Override
    public void onCreate() {

        startForeground(1, new Notification());
        HandlerThread thread = new HandlerThread("ServiceStartArguments", android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverCall = new ServerCall(getBaseContext());
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        Timer timer = new Timer();
        timer.schedule(createTimerTask(), 1000, 1000);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mgr.removeUpdates(gps_locationer);
        mgr.removeUpdates(network_locationer);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    private TimerTask createTimerTask() {
        return new TimerTask() {
            public void run() {

                periodicCall();
            }
        };
    }

    private void periodicCall() {
        Log.d("TAG", "periodicCall");
        if (serverCall != null) {
            serverCall.syncData();
            serverCall.syncLocation();
        }
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.e(DEBUG_TAG, "running...");
            mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
            gps_locationer = new Locationer(getBaseContext());
            network_locationer = new Locationer(getBaseContext());

            Criteria criteria = new Criteria();
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            criteria.setPowerRequirement(Criteria.POWER_LOW);

            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String providerFine = mgr.getBestProvider(criteria, true);

            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String providerCoarse = mgr.getBestProvider(criteria, true);

            if (ActivityCompat.checkSelfPermission(UpdateLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(UpdateLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (providerCoarse != null) {
                mgr.requestLocationUpdates(providerCoarse, 2000, 0, network_locationer);
            }

            if (providerFine != null) {
                mgr.requestLocationUpdates(providerFine, 2000, 0, gps_locationer);
            }
        }
    }
}