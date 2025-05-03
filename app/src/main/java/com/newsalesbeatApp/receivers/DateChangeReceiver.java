package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Created by abc on 9/13/18.
 */

public class DateChangeReceiver extends BroadcastReceiver {
    SharedPreferences tempPref, myPref;
    SalesBeatDb salesBeatDb;
    GPSLocation locationProvider;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("DateChangeReceiver", "ACTION_DATE_CHANGED received");
        myPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        //tempPref2 = context.getSharedPreferences(context.getString(R.string.temp_pref_name_2), Context.MODE_PRIVATE);
        //salesBeatDb = new SalesBeatDb(context);
        salesBeatDb = SalesBeatDb.getHelper(context);
        locationProvider = new GPSLocation(context);

        initailizeAndResetPrefAndDatabase(context);

    }

    private void initailizeAndResetPrefAndDatabase(Context ctx) {

        Calendar cal = Calendar.getInstance();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

        refreshDataBase();

        SharedPreferences.Editor tempEditor = tempPref.edit();
        tempEditor.clear();
        tempEditor.apply();

//        SharedPreferences.Editor tempEditor2 = tempPref2.edit();
//        tempEditor2.clear();
//        tempEditor2.apply();

        SharedPreferences.Editor editor = myPref.edit();
        editor.remove(ctx.getString(R.string.date_key));
        editor.apply();

        SharedPreferences.Editor sfaEditor = myPref.edit();
        sfaEditor.putString(ctx.getString(R.string.date_key), date);
        sfaEditor.apply();

    }

    private void refreshDataBase() {

        try {

            salesBeatDb.deleteAllDataFromOderPlacedByRetailersTable();
            salesBeatDb.deleteSpecificDataFromNewRetailerListTable();
            salesBeatDb.deleteSpecificDataFromNewOrderEntryListTable();
            salesBeatDb.deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable();
            salesBeatDb.deleteSpecificDataFromOrderEntryListTable();
            salesBeatDb.deleteAllDataFromSkuEntryListTable();
            salesBeatDb.deleteAllFromDistributorOrderTable();
            salesBeatDb.deleteLeaderboardDetail();
            salesBeatDb.deleteEmpKraDetails();
            salesBeatDb.deleteAllDataFromNewDistributorTable();
            salesBeatDb.deleteOtherActivity();

        } catch (Exception e) {
            //Log.e(TAG, "==" + e.getMessage());
        }
    }
}
