package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.Calendar;
import java.util.Date;

import io.sentry.Sentry;

/*
 * Created by abc on 9/22/18.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";
    UtilityClass utilityClass;
    SharedPreferences preSFA, tempSFA;
    //SalesBeatDb salesBeatDb;
    GPSLocation locationProvider;
    ServerCall serverCall;
    //CheckCon checkCon;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Recurring alarm; requesting location tracking.");
        preSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSFA = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        //checkCon = new CheckCon();
        utilityClass = new UtilityClass(context);
        //salesBeatDb = SalesBeatDb.getHelper(context);
        locationProvider = new GPSLocation(context);
        serverCall = new ServerCall(context);


//        if (SbAppConstants.isAppAlive) {
//
//            startServiceIfAppOpen(context);
//
//        } else {

        synDataAndRecordLocation();

//        }

        /* calculating time for auto check out......*/
        Calendar rightNow = Calendar.getInstance();
        // return the hour in 24 hrs format (ranging from 0-23)
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        if (currentHour == 0)
        {
            //@Umesh 19-08-2022
            Sentry.captureMessage("AutoCheckOut Or ClearData Time:"+rightNow.getTime()+" From Alarm");
            utilityClass.initailizeAndResetPrefAndDatabase();
        }
    }

    private void synDataAndRecordLocation() {

        if (serverCall != null) {

            new PingServer(internet -> {
                /* do something with boolean response */
                if (internet) {
                    Log.d("TAG", "RecordLocation");
                    serverCall.syncData();
                    serverCall.syncLocation();
                }
            });
        }

        Location mLocation = locationProvider.getLoc();
        if (mLocation != null && mLocation.getAccuracy() < 35)
            serverCall.sendEmpPath(mLocation);
        Log.d(DEBUG_TAG, "LocationInsert: Alarm Reciver."+new Date()); //@Umesh 20221007
    }

//    private void startServiceIfAppOpen(Context context) {
//
//        Intent tracking2 = new Intent(context, SbService.class);
//
//        if (tempSFA.getString(context.getString(R.string.attendance_key), "")
//                .equalsIgnoreCase("present")) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//                try {
//
//                    context.startForegroundService(tracking2);
//
//                } catch (Exception e) {
//                    // e.printStackTrace();
//                }
//
//            } else {
//
//                try {
//
//                    context.startService(tracking2);
//
//                } catch (Exception e) {
//                    // e.printStackTrace();
//                }
//            }
//
//        } else if (isMyServiceRunning(context, SbService.class)) {
//
//            try {
//
//                context.stopService(tracking2);
//
//            } catch (Exception e) {
//                //e.printStackTrace();
//            }
//        }
//
//    }
//
//    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (manager != null)
//            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//                if (serviceClass.getName().equals(service.service.getClassName())) {
//                    return true;
//                }
//            }
//        return false;
//    }
}