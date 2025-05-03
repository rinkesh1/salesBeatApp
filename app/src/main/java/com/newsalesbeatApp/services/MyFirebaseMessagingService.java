package com.newsalesbeatApp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.CreatePjp;
import com.newsalesbeatApp.activities.JointWorkingRequest;
import com.newsalesbeatApp.activities.LoginScreen;
import com.newsalesbeatApp.activities.MainActivity;
import com.newsalesbeatApp.activities.MyPjpActivity;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.activities.SplashScreen;
import com.newsalesbeatApp.netwotkcall.VolleyMultipartRequest;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static com.newsalesbeatApp.customview.Tools.drawableToBitmap;


/*
 * Created by MTC on 27-10-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "MyFirebaseMessaging";
    SalesBeatDb salesBeatDb;
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private SharedPreferences tempPref;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived");

        if (remoteMessage == null)
            return;


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json, remoteMessage);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        } else if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Intent notifyIntent = new Intent(this, JointWorkingRequest.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            String body = remoteMessage.getNotification().getBody();
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0,
                    notifyIntent, PendingIntent.FLAG_ONE_SHOT);

            // show the notification in notification tray
            showNotificationMessage("SalesBeat", body, notifyPendingIntent);
        }
    }

    private void handleDataMessage(JSONObject json, RemoteMessage remoteMessage) {
        Log.e(TAG, "handleDataMessage: " + json.toString());
        salesBeatDb = SalesBeatDb.getHelper(this);
        try {
            SharedPreferences tempPref;
            tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

            if (remoteMessage.getNotification() != null) {

                Intent notifyIntent = null;

                final String title = remoteMessage.getNotification().getTitle();
                final String body = remoteMessage.getNotification().getBody();
                final String action = remoteMessage.getNotification().getClickAction();

                /*----------------Defining action for notification--------------*/
                Log.e(TAG, "Action: " + action);

                if (action != null && action.equalsIgnoreCase("JointWorkingRequest")) {

                    notifyIntent = new Intent(this, JointWorkingRequest.class);
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    notifyIntent.putExtra("data", json.toString());

                } else if (action != null && action.equalsIgnoreCase("MainActivity")) {

                    notifyIntent = setRequest(json);

                } else if (action != null && action.equalsIgnoreCase("NewPjp")) {

                    notifyIntent = new Intent(this, MyPjpActivity.class);
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                } else if (action != null && action.equalsIgnoreCase("startWorking")) {

//                    Intent intent = null;
//                    Bundle bundle = new Bundle();
//
//                    if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("Present")) {
//                        intent = new Intent(this, MainActivity.class);
//                        bundle.putString("FromNotification", "LateMark");
//                    } else {
//                        intent = new Intent(this, LoginScreen.class);
//                        bundle.putString("FromNotification", "LateMark");
//                    }
//                    intent.putExtras(bundle);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                    SharedPreferences.Editor editor = tempPref.edit();
//                    editor.putString("date", date);
//                    editor.apply();

                    notifyIntent = new Intent(this, SplashScreen.class);
                    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Notifications(title, body, action, notifyIntent);
                    long notifi = salesBeatDb.insertInappNotification(title, body, "", date);

                } else if (action != null && action.equalsIgnoreCase("pjpScheduled")) {

                    Intent intent = null;
                    Bundle bundle = new Bundle();


                    if (!tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("Present")) {
                        intent = new Intent(this, CreatePjp.class);
                        bundle.putString("FromNotification", "createpjp");

                        SharedPreferences.Editor editors = tempPref.edit();
                        editors.putString("createpjp", "0");
                        editors.apply();

                    } else {
                        intent = new Intent(this, SplashScreen.class);
                        bundle.putString("FromNotification", "createpjp");
                        SharedPreferences.Editor editors = tempPref.edit();
                        editors.putString("createpjp", "0");
                        editors.apply();
                    }
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Notifications(title, body, action, intent);

                } else if (action != null && action.equalsIgnoreCase("openingStock")) {

                    Intent intent = null;
                    Bundle bundle = new Bundle();

                    JSONObject jsonObject = json.getJSONObject("data");
                    String strId = jsonObject.getString("did");
                    Log.d(TAG, strId);

                    if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("Present")) {
                        bundle.putString("FromNotification", "openingStock");
                        intent = new Intent(this, OrderBookingRetailing.class);
                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putString("dash", "0");
                        editor.putString(getString(R.string.dis_id_key_noti), strId);
                        editor.putString(getString(R.string.dis_name_key_noti), tempPref.getString(getString(R.string.dis_name_key), ""));
                        editor.apply();
                    } else {
                        bundle.putString("FromNotification", "openingStock");
                        intent = new Intent(this, MainActivity.class);
                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putString("dash", "0");
                        editor.putString(getString(R.string.dis_id_key_noti), strId);
                        editor.putString(getString(R.string.dis_name_key_noti), tempPref.getString(getString(R.string.dis_name_key), ""));
                        editor.apply();
                    }
                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Notifications(title, body, action, intent);

                } else if (action != null && action.equalsIgnoreCase("finishWorking")) {

                    Intent intent = null;
                    Bundle bundle = new Bundle();

                    intent = new Intent(this, MainActivity.class);
                    bundle.putString("FromNotification", "finishWorking");

                    intent.putExtras(bundle);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Notifications(title, body, action, intent);

                } else {

                    notifyIntent = null;
                }

                //  ------Checking if app open or closed and performing action on the basis of app status---
                if (SbAppConstants.isAppAlive) {

                    Intent intent = new Intent("com.salesbeat.start_notification");
                    intent.putExtra("title", title);
                    intent.putExtra("body", body);
                    intent.putExtra("action", action);
                    intent.putExtra("noti_intent", notifyIntent);
                    sendBroadcast(intent);

                } else {

                    PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0,
                            notifyIntent, PendingIntent.FLAG_ONE_SHOT);

                    // show the notification in notification tray
                    showNotificationMessage(title, body, notifyPendingIntent);


                }


            } else {

                try {

                    Log.e(TAG, "===" + json.toString());
                    JSONObject data = json.getJSONObject("data");
                    String action = data.getString("action");
                    String title = data.getString("title");
                    String body = data.getString("body");
                    String strDid = data.getString("did");

                    SharedPreferences.Editor editor = getSharedPreferences(getString(R
                            .string.pref_name), Context.MODE_PRIVATE).edit();

                    Log.e(TAG, "====" + action);
                    if (action.equalsIgnoreCase("disableEmployee")) {

                        editor.putString("userStatus", "disableEmployee");
                        editor.apply();
                        sendMyBroadcast(action);

                    } else if (action.equalsIgnoreCase("logout")) {

                        editor.putString("userStatus", "logout");
                        editor.apply();
                        sendMyBroadcast(action);

                    } else if (action.equalsIgnoreCase("clearData")) {

                        editor.putString("userStatus", "clearData");
                        editor.apply();
                        sendMyBroadcast(action);

                    } else if (action.equalsIgnoreCase("fullReset")) {

                        editor.putString("userStatus", "fullReset");
                        editor.apply();
                        sendMyBroadcast(action);

                    } else if (action.equalsIgnoreCase("sendLog")) {

                        UtilityClass utilityClass = new UtilityClass(getApplicationContext());
                        //SalesBeatDb salesBeatDb = new SalesBeatDb(getApplicationContext());
                        SalesBeatDb salesBeatDb = SalesBeatDb.getHelper(getApplicationContext());
                        if (utilityClass.isInternetConnected()) {
                            salesBeatDb.getMyDB();
                            sendDb();
                        }

                    } else if (action.equalsIgnoreCase("clearOldDateRecord")) {

                        deleteAllDateRecord();

                    } else if (action.equalsIgnoreCase("startWorking")) {

                        Intent intent = null;
                        Bundle bundle = new Bundle();

                        intent = new Intent(this, MainActivity.class);
                        bundle.putString("FromNotification", "LateMark");

                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        Notifications(title, body, action, intent);

                    } else if (action.equalsIgnoreCase("pjpScheduled")) {

                        Intent intent = null;
                        Bundle bundle = new Bundle();


                        if (!tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("Present")) {
                            bundle.putString("FromNotification", "createpjp");
                            intent = new Intent(this, CreatePjp.class);
                        } else {
                            bundle.putString("FromNotification", "createpjp");
                            intent = new Intent(this, LoginScreen.class);
                        }
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        Notifications(title, body, action, intent);

                    } else if (action.equalsIgnoreCase("openingStock")) {

                        Intent intent = null;
                        Bundle bundle = new Bundle();

                        if (tempPref.getString(getString(R.string.attendance_key), "").equalsIgnoreCase("Present")) {
                            bundle.putString("FromNotification", "openingStock");
                            intent = new Intent(this, OrderBookingRetailing.class);
                            SharedPreferences.Editor editors = tempPref.edit();
                            editors.putString("dash", "0");
                            editors.putString(getString(R.string.dis_id_key_noti), strDid);
                            editors.putString(getString(R.string.dis_name_key_noti), tempPref.getString(getString(R.string.dis_name_key), ""));
                            editors.apply();
                        } else {
                            bundle.putString("FromNotification", "openingStock");
                            intent = new Intent(this, MainActivity.class);
                            SharedPreferences.Editor editors = tempPref.edit();
                            editors.putString("dash", "0");
                            editors.putString(getString(R.string.dis_id_key_noti), strDid);
                            editors.putString(getString(R.string.dis_name_key_noti), tempPref.getString(getString(R.string.dis_name_key), ""));
                            editors.apply();
                        }

                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Notifications(title, body, action, intent);

                    } else if (action.equalsIgnoreCase("finishWorking")) {

                        Intent intent = null;
                        Bundle bundle = new Bundle();


                        intent = new Intent(this, MainActivity.class);
                        bundle.putString("FromNotification", "finishWorking");


                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Notifications(title, body, action, intent);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } catch (Exception e) {

            e.printStackTrace();
            Log.e(TAG, "Json Exception: " + e.getMessage());
        }
    }

    private void Notifications(String title, String message, String action, Intent intent) {
        Random random = new Random();
        String CHANNEL_ID = "SalesBeat";
        CharSequence name = "SalesBeat";
        String Description = "SalesBeat";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentText(message)
                    .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.logo)))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setSmallIcon(R.drawable.new_logo_noti)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorPrimary));
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.drawable.new_logo_noti)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.logo)))
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.drawable.new_logo_noti)
                    .setAutoCancel(true)
                    .setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setLargeIcon(drawableToBitmap(getResources().getDrawable(R.drawable.logo)))
                    .setColor(getResources().getColor(android.R.color.transparent))
                    .setContentText(message)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
        }
        if (notificationManager != null) {
            notificationManager.notify(random.nextInt(1000), notificationBuilder.build());
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, MyFirebaseMessagingService.class.getSimpleName());
        wl.acquire(15000);

    }

    private Intent setRequest(JSONObject json) {
        String jwStatus = "";

        Intent notifyIntent = null;
        try {

            if (!json.isNull("data") && json.has("data")) {

                JSONObject data = json.getJSONObject("data");

                if (!data.isNull("status") && data.has("status"))
                    jwStatus = data.getString("status");

            } else {

                if (!json.isNull("status") && json.has("status"))
                    jwStatus = json.getString("status");
            }

            if (jwStatus.equalsIgnoreCase("accepted")) {

                notifyIntent = new Intent(this, MainActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                notifyIntent.putExtra("joint_working", true);
                notifyIntent.setAction("JWAccepted");

            } else {

                notifyIntent = new Intent(this, MainActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                notifyIntent.putExtra("joint_working", true);
                notifyIntent.setAction("JWRejected");

            }

            return notifyIntent;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifyIntent;
    }

    private void deleteAllDateRecord() {
        try {

            Calendar cal = Calendar.getInstance();
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            //SalesBeatDb salesBeatDb = new SalesBeatDb(getApplicationContext());
            SalesBeatDb salesBeatDb = SalesBeatDb.getHelper(getApplicationContext());
            salesBeatDb.deleteAllDataFromOderPlacedByRetailersTable3(date);
            salesBeatDb.deleteSpecificDataFromNewRetailerListTable3(date);
            salesBeatDb.deleteSpecificDataFromNewOrderEntryListTable3(date);
            salesBeatDb.deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable3(date);
            salesBeatDb.deleteSpecificDataFromOrderEntryListTable3(date);
            salesBeatDb.deleteAllDataFromSkuEntryListTable3(date);
            salesBeatDb.deleteAllFromDistributorOrderTable3(date);
            salesBeatDb.deleteAllDataFromNewDistributorTable3(date);
            salesBeatDb.deleteOtherActivity3(date);

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        }
    }

    private void sendMyBroadcast(String action) {

//        if (SbAppConstants.isAppAlive) {

            Intent intent = new Intent("com.salesbeat.start_notification");
            intent.putExtra("title", "");
            intent.putExtra("body", "");
            intent.putExtra("action", action);
            intent.putExtra("noti_intent", "");
            sendBroadcast(intent);
//        }
    }

    public void sendDb() {

        final VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, SbAppConstants.API_SUBMIT_DB,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {

                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.e(TAG, "Data Response: " + obj.toString());

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("success")) {

                            }

                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                SbLog.printError(TAG, "submitDb", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                        getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE).getString(getString(R.string.emp_id_key), ""));
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "Bearer" + " " + getSharedPreferences(getString(R
                        .string.pref_name), Context.MODE_PRIVATE).getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart("db", getFileDataFromDrawable()));

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private byte[] getFileDataFromDrawable() {

        try {
            String PATH = Environment.getExternalStorageDirectory() + "/CMNY/sb.db";
            File file = new File(PATH);
            //init array with file length
            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            return bytesArray;

        } catch (Exception e) {

            e.getMessage();
        }

        return null;
    }

    /*
     * Showing notification with text only
     */
    private void showNotificationMessage(String title, String body, PendingIntent resultPIntent) {

        Log.e(TAG, "custom notification");

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.sb_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(resultPIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (notificationManager != null) {
            notificationManager.cancel(0);
            notificationManager.notify(0, notificationBuilder.build());
        }


    }


}
