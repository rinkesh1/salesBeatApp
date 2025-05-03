package com.newsalesbeatApp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.newsalesbeatApp.pojo.CheckCon;

import java.net.InetAddress;

import static android.content.Context.CONNECTIVITY_SERVICE;

/*
 * Created by abc on 12/7/18.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_AVAILABLE_ACTION = "com.salesbeat.NetworkChangeReceiver";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";
    public static boolean IS_CONNECTED = true;

    NetworkChangeInterface networkChangeInterface = null;
    CheckCon checkCon;

    public static boolean isConnectedToInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                IS_CONNECTED = (networkInfo != null && networkInfo.isConnected());
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(ConnectivityChangeReceiver.class.getName(), e.getMessage());
            return false;
        }
    }

    public void InitNetworkListener(NetworkChangeInterface networkInterface) {
        this.networkChangeInterface = networkInterface;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context,"Network Change Receive", Toast.LENGTH_SHORT).show();
        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE, isConnectedToInternet(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent);

        if (networkChangeInterface != null)
            networkChangeInterface.connectionChange(IS_CONNECTED);

//        Toast.makeText(context,context.toString(),Toast.LENGTH_SHORT).show();

        checkCon = new CheckCon();
        new CheckConnection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class CheckConnection extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {

                InetAddress ipAddr = InetAddress.getByName("google.com");
                //You can replace it with your name
                return !ipAddr.equals("");

            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);
            checkCon.setConnected(flag);
        }
    }

}

