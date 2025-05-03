package com.newsalesbeatApp.utilityclass;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.google.firebase.FirebaseApp;
import com.newsalesbeatApp.R;

/*
 * Created by Dhirendra Thakur on 29-11-2017.
 */

public class SBApplication extends Application {

    public static String SENTRY_DNS = "";
    public static String DOMAIN = "";
    private static SBApplication mInstance;
    private static int stateCounter;

    public static synchronized SBApplication getInstance() {
        return mInstance;
    }
    /**
     * @return true if application is on background
     */
    public static boolean isApplicationOnBackground() {
        return stateCounter == 0;
    }

    //to be called on each Activity onStart()
    public static void activityStarted() {
        stateCounter++;
    }

    //to be called on each Activity onStop()
    public static void activityStopped() {
        stateCounter--;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        stateCounter = 0;

        mInstance = this;
        try {
            if (isMainProcess()){
            FirebaseApp.initializeApp(this);
            Log.d("FirebaseInit", "Firebase initialized successfully_123."+  FirebaseApp.getApps(this).size());
       }
        } catch (Exception e) {
            Log.e("FirebaseInit", "Firebase initialization failed.", e);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (getString(R.string.url_mode).equalsIgnoreCase("T")) {
            DOMAIN = getString(R.string.new_test_url);
            SENTRY_DNS = getString(R.string.new_sentry_test_dns); //@Umesh 20220904
        } else if (getString(R.string.url_mode).equalsIgnoreCase("L")) {
            DOMAIN = getString(R.string.live_url);
            SENTRY_DNS = getString(R.string.sentry_live_dns);
        } else if (getString(R.string.url_mode).equalsIgnoreCase("D")) {
            DOMAIN = getString(R.string.demo_url);
            SENTRY_DNS = getString(R.string.sentry_test_dns);
        } else if (getString(R.string.url_mode).equalsIgnoreCase("Loc")) {
            DOMAIN = getString(R.string.local_url);
            SENTRY_DNS = getString(R.string.sentry_test_dns);
        }else if (getString(R.string.url_mode).equalsIgnoreCase("N")) {
            DOMAIN = getString(R.string.new_live_url);
            SENTRY_DNS = getString(R.string.new_sentry_live_dns); //@Umesh
        }

    }
private boolean isMainProcess() {
    int pid = android.os.Process.myPid();
    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    if (manager != null) {
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return getPackageName().equals(processInfo.processName);
            }
        }
    }
    return false;
}
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
