package com.newsalesbeatApp.sblocation;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.newsalesbeatApp.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

public class GPSLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "GPSLocation";
    boolean isShowing = false;
    private Context context;
    private AlertDialog.Builder builder1 = null;
    private AlertDialog alert1;
    private AlertDialog.Builder builder2 = null;
    private AlertDialog alert2;
    private Handler handler;
    private Location location;
    private LocationManager locationManager;

    public GPSLocation() {
    }

    public GPSLocation(final Context context) {
        this.context = context;
        handler = new Handler();
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        location = getLocation();
    }

    public Location getLocation() {

        if (locationManager != null) {

            Location locByNet, locByGps, locByPassive;
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,
                    0, new MyLocationListener());

            locByNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, new MyLocationListener());

            locByGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (locByGps != null) {

                if (locByNet != null) {

                    if (locByNet.getTime() >= locByGps.getTime()) {

                        if (locByNet.getAccuracy() <= locByGps.getAccuracy())
                            return locByNet;
                        else
                            return locByGps;

                    } else {

                        if (locByNet.getAccuracy() <= locByGps.getAccuracy())
                            return locByNet;
                        else
                            return locByGps;
                    }

                } else {

                    return locByGps;
                }

            } else {

                return locByNet;
            }
        }

        return null;
    }

    public void checkGpsStatus() {

        Log.e(TAG, "########" + handler);
        final int FREQ = 500;
        handler.postDelayed(new Runnable() {
            public void run() {

                try {
                    checkGPSStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    checkforTimeAutoEnabled();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    handler.postDelayed(this, FREQ);
                }

            }
        }, FREQ);
    }

    private void checkforTimeAutoEnabled() {

        if (android.provider.Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.AUTO_TIME, 0) != 1) {
            if (builder2 == null)
                autoTimeAlertMessage();
        } else {
            if (alert2 != null)
                alert2.cancel();
            builder2 = null;
        }

    }

    private void autoTimeAlertMessage() {

        builder2 = new AlertDialog.Builder(context);
        builder2.setMessage("Your system date & time is not auto enabled")
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(Settings.ACTION_DATE_SETTINGS));
                    }
                });

        alert2 = builder2.create();
        alert2.show();
    }

    private void checkGPSStatus() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager != null && !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (!isShowing)
                EnableGPSAutoMatically();
        }

    }

    private void buildAlertMessageNoGps() {

        builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(context.getString(R.string.gpsAlertMsg))
                .setCancelable(false)
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        alert1 = builder1.create();
        alert1.show();

    }


    private void EnableGPSAutoMatically() {

        isShowing = true;

        GoogleApiClient googleApiClient = null;
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // **************************
        builder.setAlwaysShow(true); // this is the key ingredient
        // **************************

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result
                        .getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        toast("Success");
                        // All location settings are satisfied. The client can
                        // initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        //toast("GPS is not on");
                        // Location settings are not satisfied. But could be
                        // fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling
                            // startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult((Activity) context, 1000);

                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        toast("Setting change not allowed");
                        // Location settings are not satisfied. However, we have
                        // no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    public double getLatitude() {

        if (location != null)
            return location.getLatitude();

        return 0;
    }


    public double getLongitude() {

        if (location != null)
            return location.getLongitude();

        return 0;
    }


    public String getLatitudeStr() {

        if (location != null)
            return String.valueOf(location.getLatitude());

        return "";
    }

    public String getLongitudeStr() {

        if (location != null)
            return String.valueOf(location.getLongitude());

        return "";
    }


    public String getAddressLine() {

        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        } else {
            return null;
        }
    }

    public String getLocality() {

        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getLocality();
        } else {
            return null;
        }
    }

    public String getSubLocality() {

        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getSubLocality();
        } else {
            return null;
        }
    }

    public String getState() {

        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getAdminArea();
        } else {
            return null;
        }
    }

    public String getPostalCode() {

        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getPostalCode();
        } else {
            return null;
        }
    }

    public Location getLoc() {
        return location;
    }

    private List<Address> getGeocoderAddress() {

        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

        if (location != null) {

            try {

                return geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch (IOException e) {

                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        toast("Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        toast("Failed");
    }

    private void toast(String message) {
        try {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            // log("Window has been closed");
        }
    }

    private class MyLocationListener implements LocationListener {
        private String TAG = "MyLocationListener";

        @Override
        public void onLocationChanged(Location loc) {

            location = loc;
            //Log.d(TAG, "onLocationChanged :"+loc);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            /*Log.e(TAG,"onProviderEnabled: "+provider);*/
        }

        @Override
        public void onProviderDisabled(String provider) {
            /*Log.e(TAG,"onProviderEnabled: "+provider);*/
        }
    }
}