package com.newsalesbeatApp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.MessageListAdapter;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.Config;
import com.newsalesbeatApp.utilityclass.NotificationUtils;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;

/**
 * Created by MTC on 28-10-2017.
 */

public class MessageList extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    MessageListAdapter adapter;
    ArrayList<String> datelist = new ArrayList<>();
    ArrayList<String> messagelist = new ArrayList<>();
    ShimmerRecyclerView rvMessageList;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.message_list);
        rvMessageList = (ShimmerRecyclerView) findViewById(R.id.rvMessageList);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView tvPageTitle = (TextView) findViewById(R.id.pageTitle);

        tvPageTitle.setText("Message List");

        utilityClass = new UtilityClass(MessageList.this);

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        String message = getIntent().getStringExtra("message");
        final String time_stamp = getIntent().getStringExtra("time_stamp");

        //final SalesBeatDb salesBeatDb = new SalesBeatDb(this);
        final SalesBeatDb salesBeatDb = SalesBeatDb.getHelper(this);

        if (message != null && time_stamp != null
                && !time_stamp.isEmpty() && !message.isEmpty()) {
            salesBeatDb.insertIntoMessageListTable(time_stamp, message);
        }

        Cursor cursor = salesBeatDb.getAllDataFromMessageListTable();

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                datelist.add(cursor.getString(cursor.getColumnIndex("time_stamp")));
                messagelist.add(cursor.getString(cursor.getColumnIndex("message")));
            } while (cursor.moveToNext());
        }


        adapter = new MessageListAdapter(this, datelist, messagelist);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMessageList.setLayoutManager(layoutManager);
        rvMessageList.setAdapter(adapter);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");
                    String time_st = intent.getStringExtra("time_stamp");

                    if (message != null && time_st != null
                            && !time_st.isEmpty() && !message.isEmpty()) {
                        salesBeatDb.insertIntoMessageListTable(time_st, message);
                    }

                    datelist.add(time_st);
                    messagelist.add(message);

                    adapter = new MessageListAdapter(MessageList.this, datelist, messagelist);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MessageList.this);
                    rvMessageList.setLayoutManager(layoutManager);
                    rvMessageList.setAdapter(adapter);
                    rvMessageList.smoothScrollToPosition(adapter.getItemCount());
                    adapter.notifyDataSetChanged();
                }
            }
        };


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageList.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        displayFirebaseRegId();

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
    }
}
