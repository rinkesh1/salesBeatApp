//package com.newsalesbeat.services;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.FirebaseInstanceIdService;
//import com.newsalesbeat.utilityclass.Config;
//
///*
// * Created by MTC on 27-10-2017.
// */
//
//public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
//    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
//
//    @Override
//    public void onTokenRefresh() {
//        super.onTokenRefresh();
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//
//        // Saving reg id to shared preferences
//        storeRegIdInPref(refreshedToken);
//
//        // sending reg id to your server
//        sendRegistrationToServer(refreshedToken);
//
//        // Notify UI that registration has completed, so the progress indicator can be hidden.
//        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
//        registrationComplete.putExtra("token", refreshedToken);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
//    }
//
//    private void sendRegistrationToServer(final String token) {
//        // sending gcm token to server
//        Log.e(TAG, "sendRegistrationToServer: " + token);
//    }
//
//    private void storeRegIdInPref(String token) {
//        Log.e(TAG, " FCM Token: " + token);
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString("regId", token);
//        editor.apply();
//    }
//}
