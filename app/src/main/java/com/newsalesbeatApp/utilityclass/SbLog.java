package com.newsalesbeatApp.utilityclass;


/*
 * Created by abc on 9/18/18.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.sentry.Breadcrumb;
import io.sentry.Sentry;
//import io.sentry.event.BreadcrumbBuilder;

public class SbLog {

    public static void printError(String TAG, String methodName, String errorCode,
                                  String errorMessage, String employeeId) {

        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(calendar.getTime());

        if (errorMessage == null || errorMessage.equalsIgnoreCase("null"))
            Sentry.captureMessage(TAG + ": " + methodName + " - " + errorCode + "(Not Available)" + " with id " + employeeId + " Date: " + date);
        else
            Sentry.captureMessage(TAG + ": " + methodName + " - " + errorCode + "(" + errorMessage + ")" + " with id " + employeeId + " Date: " + date);
    }

    public static void printException(String TAG, String methodName, String errorMessage, String employeeId) {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(calendar.getTime());

        Sentry.captureMessage(TAG + ": " + methodName + "- " + errorMessage + " with id " + employeeId + " Date: " + date);
    }

    public static void recordScreen(String screenName) {
        // Record a breadcrumb that will be sent with the next event(s)
//        Sentry.captureEvent(new Breadcrumb().setMessage(screenName).build());
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setMessage(screenName);
//        breadcrumb.setData("custom_data_key", "custom_data_value");

// Record the breadcrumb
        Sentry.addBreadcrumb(breadcrumb);
    }
}
