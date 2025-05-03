package com.newsalesbeatApp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.BeatList;
import com.newsalesbeatApp.fragments.DistributorList;
import com.newsalesbeatApp.fragments.SkusFragment;
import com.newsalesbeatApp.fragments.TownList;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/*
 * Created by MTC on 25-07-2017.
 */

public class OrderBookingRetailing extends AppCompatActivity {

    Fragment fragment;
    String changeBeat = "", changeBeatPjp = "", changeDistributorPjp = "", beat_id = "";
    GPSLocation locationProvider;
    SalesBeatDb salesBeatDb;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    private SharedPreferences tempSfa;
    private SharedPreferences tempPref, myPref;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.order_booking_retailing);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        //salesBeatDb = new SalesBeatDb(this);
        salesBeatDb = SalesBeatDb.getHelper(this);
        locationProvider = new GPSLocation(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        //locationProvider.unregisterReceiver();
        //check gps status if on/off
        locationProvider.checkGpsStatus();


        tempPref = OrderBookingRetailing.this.getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        if (tempPref.getString("dash", "").equals("1")) {
            attachFragment();
        } else if (tempPref.getString("dash", "").equalsIgnoreCase("2")) {
            attachFragment();
        } else {

            Log.e("ITIS", "---HERE");

            if (!tempPref.getString(getString(R.string.dis_id_key_noti), "").equals("")) {

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(OrderBookingRetailing.this.getString(R.string.dis_id_key), tempPref.getString(getString(R.string.dis_id_key_noti), ""));
                editor.putString(OrderBookingRetailing.this.getString(R.string.dis_name_key), tempPref.getString(getString(R.string.dis_name_key_noti), ""));
                editor.apply();

                Log.d("OrderBookRetail", tempPref.getString(getString(R.string.dis_id_key_noti), ""));

                Bundle bundles = new Bundle();
                bundles.putString("from", "stock");

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                Fragment fragment = new SkusFragment();
                fragment.setArguments(bundles);
                ft.replace(R.id.flContainer, fragment);
                ft.commit();

            } else {
                attachFragment();
            }
        }


    }

    private void attachFragment() {

//        Calendar cal = Calendar.getInstance();
//        String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        try {

            changeBeat = getIntent().getStringExtra("change_beat");
            changeBeatPjp = getIntent().getStringExtra("change_beat_pjp");
            changeDistributorPjp = getIntent().getStringExtra("change_distributor_pjp");
            beat_id = getIntent().getStringExtra("beat_id");
            if (beat_id == null) {
                beat_id = "";
            }

        } catch (Exception e) {
            changeBeat = "";
            e.printStackTrace();
        }

        if (changeBeat != null && changeBeat.equalsIgnoreCase("yes")) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            fragment = new BeatList();
            if (!beat_id.equalsIgnoreCase("")) {
                Bundle bundle = new Bundle();
                bundle.putString("beat_id", beat_id);
                fragment.setArguments(bundle);
            }
            ft.replace(R.id.flContainer, fragment);
            ft.commit();

        } else if (changeBeatPjp != null && changeBeatPjp.equalsIgnoreCase("yes")) {

            Intent intent = new Intent(OrderBookingRetailing.this, RetailerActivity.class);
            startActivity(intent);
            finish();

        } else if (changeDistributorPjp != null && changeDistributorPjp.equalsIgnoreCase("yes")) {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            fragment = new DistributorList();
            ft.replace(R.id.flContainer, fragment);
            ft.commit();

        } else if (tempSfa.getBoolean(getString(R.string.is_on_retailer_page), false)) {

            Intent intent = new Intent(OrderBookingRetailing.this, RetailerActivity.class);

            startActivity(intent);
            finish();

        } else {

//            if (tempSfa.getString(getString(R.string.town_name_key),"").isEmpty()){

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            fragment = new TownList();
            ft.replace(R.id.flContainer, fragment);
            ft.commit();

//            }else {
//
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
//                Fragment fragment = new DistributorList();
//                ft.replace(R.id.flContainer, fragment);
//                ft.commit();
//            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
//            registerReceiver(receiver, intentFilter);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(receiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    public void onPause() {
        super.onPause();
        try {

            unregisterReceiver(receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    public void onBackPressed() {


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                OrderBookingRetailing.this.finishAffinity();
            }
        }
    }
}
