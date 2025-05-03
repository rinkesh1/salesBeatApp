package com.newsalesbeatApp.services;

import android.app.IntentService;
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
import android.os.PowerManager;

import androidx.core.app.NotificationCompat;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.MainActivity;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static com.newsalesbeatApp.customview.Tools.drawableToBitmap;

public class NotificationIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 3;
    SalesBeatDb salesBeatDb;
    private SharedPreferences tempPref;

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    /*    Notification.Builder builder = new Notification.Builder(this);
        tempPref = this.getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

       *//* builder.setContentTitle(tempPref.getString("Title", ""));
        builder.setStyle(new Notification.BigTextStyle().bigText(tempPref.getString("Body", "")));*//*

        builder.setContentTitle("ATTENDANCE DELAY");
        builder.setStyle(new Notification.BigTextStyle().bigText("Please mark your attendance/leave, else absent will be considered."));

        *//* builder.setContentTitle("ATTENDANCE DELAY");
        builder.setContentText("Please mark your attendance/leave, else absent will be considered.");*//*

        builder.setSmallIcon(R.drawable.logo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setBadgeIconType(R.drawable.logo);
        }*/
        //builder.setLargeIcon(R.drawable.logo)

        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);*/
        tempPref = this.getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!tempPref.getString("date", "").equalsIgnoreCase(date)) {
            Intent notifyIntent = new Intent(this, MainActivity.class);
            Notifications("ATTENDANCE DELAY",
                    "Please mark your attendance/leave, else absent will be considered.", "",
                    notifyIntent);
            salesBeatDb = SalesBeatDb.getHelper(this);


            SharedPreferences.Editor editor = tempPref.edit();
            editor.putString("date", date);
            editor.apply();

            long notifi = salesBeatDb.insertInappNotification("ATTENDANCE DELAY",
                    "Please mark your attendance/leave, else absent will be considered.", "", date);

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
                    .setSmallIcon(R.drawable.ic_stat_sb)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.colorPrimary));
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_sb)
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
            notificationBuilder.setSmallIcon(R.drawable.ic_stat_sb)
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

}