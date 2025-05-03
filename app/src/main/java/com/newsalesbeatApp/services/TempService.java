package com.newsalesbeatApp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.SBApplication;

public class TempService extends IntentService {

    public static final int DOWNLOAD_ERROR = 10;
    public static final int DOWNLOAD_SUCCESS = 11;
    ResultReceiver receiver = null;
    Bundle bundle = null;
    GPSLocation locationProvider;

    String TAG = getClass().getName();

    public TempService() {
        super(TempService.class.getName());

        Context context = SBApplication.getInstance();
        locationProvider = new GPSLocation(context);
        bundle = new Bundle();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        receiver = intent.getParcelableExtra("receiver");

        String address = locationProvider.getAddressLine();
        String district = locationProvider.getLocality();
        String locality = locationProvider.getSubLocality();
        String pincode = locationProvider.getPostalCode();
        String state = locationProvider.getState();
        double latitude = locationProvider.getLatitude();
        double longitude = locationProvider.getLongitude();

        bundle.putString("address", address);
        bundle.putString("district", district);
        bundle.putString("locality", locality);
        bundle.putString("pincode", pincode);
        bundle.putString("state", state);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);


        Log.e(TAG, "==>" + address + "," + district + "," + locality + "," + pincode + "," + state + ","
                + latitude + " AND " + longitude);

        receiver.send(DOWNLOAD_SUCCESS, bundle);

    }
}
