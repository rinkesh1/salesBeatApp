package com.newsalesbeatApp.utilityclass;

import android.app.Application;

public class UtilitiesActi extends Application {
    private static int stateCounter;

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

    public void onCreate() {
        super.onCreate();
        stateCounter = 0;
    }
}
