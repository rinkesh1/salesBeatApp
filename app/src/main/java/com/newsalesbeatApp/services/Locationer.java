package com.newsalesbeatApp.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.newsalesbeatApp.netwotkcall.ServerCall;

import java.util.Date;

/*
 * Created by abc on 9/25/18.
 */

class Locationer implements LocationListener {

    private static final String DEBUG_TAG = "Locationer";
    private static final double ACCU_THRESHOLD = 100.0;
    //private Context ctx;
    private ServerCall serverCall;

    public Locationer(Context context) {
        //ctx = context;
        serverCall = new ServerCall(context);
    }

    @Override
    public void onLocationChanged(Location location) {

        if ((location == null) || (location.getAccuracy() > ACCU_THRESHOLD)) {
            Log.e(DEBUG_TAG, "location unavailable.");
            return;
        }

        Log.e(DEBUG_TAG, "onLocationChanged method invoked by " + location.getProvider());

        insertLocation(location);

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(DEBUG_TAG, provider + " disabled.");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(DEBUG_TAG, provider + " enabled.");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(DEBUG_TAG, provider + " statu changed" + status);
    }

    private void insertLocation(Location location) {

        serverCall.sendEmpPath(location);
        Log.d(DEBUG_TAG, "LocationInsert: Locationer."+new Date()); //@Umesh 20221007
    }


}
