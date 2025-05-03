package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Dheeraj Kotwani on 14-08-2020.
 */
public class ConnectivityChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_AVAILABLE_ACTION = "com.salesbeat.NetworkAvailable";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    public static boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(ConnectivityChangeReceiver.class.getName(), e.getMessage());
            return false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Receive", Toast.LENGTH_SHORT).show();
        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE, isConnectedToInternet(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent);
    }

    // Interface
    public interface connectivityChangeListener {
        void onNetworkConnectivityChange(boolean isConnected);
    }

}
