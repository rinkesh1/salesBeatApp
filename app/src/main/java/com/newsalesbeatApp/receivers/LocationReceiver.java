package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.newsalesbeatApp.netwotkcall.ServerCall;

import static com.google.android.gms.location.FusedLocationProviderApi.KEY_LOCATION_CHANGED;

import java.util.Date;

//import static android.location.LocationManager.KEY_LOCATION_CHANGED;

/*
 * Created by abc on 9/12/18.
 */

public class LocationReceiver extends BroadcastReceiver {

    ServerCall serverCall;

    @Override
    public void onReceive(Context context, Intent intent) {

        serverCall = new ServerCall(context);
        Location location = (Location) intent.getExtras().get(KEY_LOCATION_CHANGED);
        if (location != null && location.getAccuracy() < 25)
            syncPath(location);
    }

    private void syncPath(Location mLocation) {
        serverCall.sendEmpPath(mLocation);
        Log.d("", "LocationInsert: Location Recevier."+new Date()); //@Umesh 20221007
    }
}
