package com.newsalesbeatApp.sblocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.newsalesbeatApp.netwotkcall.ServerCall;

import java.util.Date;

/*
 * Created by abc on 12/5/18.
 */

public class SbFusedLocation implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Time difference threshold set for one minute.
     */
    private static final int TIME_DIFFERENCE_THRESHOLD = 1 * 60 * 1000;
    private String TAG = "SbFusedLocation";
    private Context context;
    private ServerCall serverCall;
    private Location oldLocation = null;
    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest = new LocationRequest();


    public SbFusedLocation(Context context) {

        this.context = context;
        serverCall = new ServerCall(context);
        mLocationClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /* 30 secs */
        //long UPDATE_INTERVAL = 30000;
        long UPDATE_INTERVAL = 30000*2*5; //@Umesh 20221007 5 Min
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        /* 15 secs */
       // long FASTEST_INTERVAL = 15000;
        long FASTEST_INTERVAL = 30000*2*5; //@Umesh 20221007 5 Min
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY;//by default

        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //Log.e("SbFusedLocation", "onLocationChanged :" + location);

        boolean isBetterLocation = isBetterLocation(getOldLocation(), location);
        doWorkWithNewLocation(isBetterLocation, location);

        Log.e(TAG, "onLocationChanged is better location: " + isBetterLocation);
    }

    private void syncPath(Location mLocation) {
        serverCall.sendEmpPath(mLocation);
        Log.d(TAG, "LocationInsert: SbFusedLocation."+new Date()); //@Umesh 20221007
    }

    /**
     * Make use of location after deciding if it is better than previous one.
     *
     * @param isBetterLocation parmam to define best known location
     * @param location         Newly acquired location.
     */
    private void doWorkWithNewLocation(boolean isBetterLocation, Location location) {

        if (isBetterLocation && location.getAccuracy() <= 500) {
            // If location is better, do some user preview.
            syncPath(location);
        }

        setOldLocation(location);
    }

    private Location getOldLocation() {
        return oldLocation;
    }

    private void setOldLocation(Location location) {
        oldLocation = location;
    }

    /**
     * Decide if new location is better than older by following some basic criteria.
     * This algorithm can be as simple or complicated as your needs dictate it.
     * Try experimenting and get your best location strategy algorithm.
     *
     * @param oldLocation Old location used for comparison.
     * @param newLocation Newly acquired location compared to old one.
     * @return If new location is more accurate and suits your criteria more than the old one.
     */
    private boolean isBetterLocation(Location oldLocation, Location newLocation) {
        // If there is no old location, of course the new location is better.
        if (oldLocation == null) {
            return true;
        }

        // Check if new location is newer in time.
        boolean isNewer = newLocation.getTime() > oldLocation.getTime();

        // Check if new location more accurate. Accuracy is radius in meters, so less is better.
        boolean isMoreAccurate = newLocation.getAccuracy() < oldLocation.getAccuracy();
        if (isMoreAccurate && isNewer) {
            // More accurate and newer is always better.
            return true;
        } else if (isMoreAccurate && !isNewer) {
            // More accurate but not newer can lead to bad fix because of user movement.
            // Let us set a threshold for the maximum tolerance of time difference.
            long timeDifference = newLocation.getTime() - oldLocation.getTime();

            // If time difference is not greater then allowed threshold we accept it.
            if (timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                return true;
            }
        }

        return false;
    }
}
