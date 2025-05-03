package com.newsalesbeatApp.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.CustomViewPager;
import com.newsalesbeatApp.customview.Tools;
import com.newsalesbeatApp.fragments.LeastActiveRetailerFragment;
import com.newsalesbeatApp.fragments.ScheduledRetailerList;
import com.newsalesbeatApp.fragments.SortRetailerFragment;
import com.newsalesbeatApp.fragments.VeryActiveRetailerFragment;
import com.newsalesbeatApp.fragments.VisitedRetailerList;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.services.DownloadDataService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/*
 * Created by MTC on 06-11-2017.
 */

public class RetailerActivity extends AppCompatActivity implements NetworkChangeInterface {

    public static ImageView imgBack, imgUpload, imgFilterBy;
    public static TextView tvTc, tvPc, tvSale, tvNc;
    public static TabLayout retailerTab;
    public static CustomViewPager retailerViewPager;
    public static LinearLayout headerTCPC;
    //    private Handler handler;
//    private Runnable runnable;
    public static boolean val = false;
    SalesBeatDb salesBeatDb;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    private String TAG = "RetailerActivity";
    private UtilityClass utilityClass;
    //ServerCall serverCall;
    private SharedPreferences tempPref;
    private GPSLocation locationProvider;
    private ArrayList<RetailerItem> visitedRetailerList;
    private int tabPosition = 0;
    //ArrayList<String> myList = new ArrayList<String>();
    private Boolean downloadDataFailed = false;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.retailer_activity);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        salesBeatDb = SalesBeatDb.getHelper(this);
        retailerTab = findViewById(R.id.retailerTab);
        headerTCPC = findViewById(R.id.headerTCPC);

        imgFilterBy = findViewById(R.id.imgFilterByValue);

        retailerViewPager = findViewById(R.id.retailerViewpager);
        tvPc = findViewById(R.id.tvPcLocal);
        tvTc = findViewById(R.id.tvTcLocal);
//        tvNc = findViewById(R.id.tvNcLocal);
        tvSale = findViewById(R.id.tvSaleLocal);

        // myList = (ArrayList<String>) getIntent().getSerializableExtra("BeatList");
        utilityClass = new UtilityClass(RetailerActivity.this);
        locationProvider = new GPSLocation(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        //handler = new Handler();
        //serverCall = new ServerCall(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        //set up custom toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar2);
        final TextView userImage = mToolbar.findViewById(R.id.userPic);
        RelativeLayout rlImg = mToolbar.findViewById(R.id.rlImg);
        imgBack = mToolbar.findViewById(R.id.imgBack);
        imgUpload = mToolbar.findViewById(R.id.imgUpload);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        setSupportActionBar(mToolbar);
        tvPageTitle.setText(tempPref.getString(getString(R.string.beat_name_key), ""));

        //MainActivity.var = true;


        rlImg.setVisibility(View.GONE);
        headerTCPC.setVisibility(View.GONE);

        try {
            tabPosition = getIntent().getIntExtra("tabPosition", 0);
        } catch (Exception e) {
            tabPosition = 0;
        }

        //set up view pager
        setUpViewPager(retailerViewPager);
        retailerTab.setSelectedTabIndicatorColor(Color.WHITE);
        retailerTab.setSelectedTabIndicatorHeight(2);
        retailerTab.setupWithViewPager(retailerViewPager);
        retailerTab.getTabAt(tabPosition).select();
        visitedRetailerList = getVisitedRetailerList();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //locationProvider.unregisterReceiver();

                if (tempPref.getBoolean(getString(R.string.is_on_retailer_page), false)) {

                    Intent intent = new Intent(RetailerActivity.this, MainActivity.class);
                    startActivity(intent);
                    RetailerActivity.this.finish();
                    //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                } else {

                    Intent intent = new Intent(RetailerActivity.this, OrderBookingRetailing.class);
                    intent.putExtra("change_beat", "yes");
                    startActivity(intent);
                    finish();
                    //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                }

            }
        });

        imgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (utilityClass.isInternetConnected()) {

                    //set up view pager
                    setUpViewPager(retailerViewPager);
                    retailerTab.setSelectedTabIndicatorColor(Color.WHITE);
                    retailerTab.setSelectedTabIndicatorHeight(2);
                    retailerTab.setupWithViewPager(retailerViewPager);
                    retailerTab.getTabAt(tabPosition).select();

                    //serverCall.addNewRet();

                } else {
                    Toast.makeText(RetailerActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });


        receiver.InitNetworkListener(this);
        //start service
        startServiceToDownloadData();
    }

//    private void refreshPage(){
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//
//                if (utilityClass.isInternetConnected()) {
//
//                    //set up view pager
//                    setUpViewPager(retailerViewPager);
//                    retailerTab.setSelectedTabIndicatorColor(Color.WHITE);
//                    retailerTab.setSelectedTabIndicatorHeight(2);
//                    retailerTab.setupWithViewPager(retailerViewPager);
//                    retailerTab.getTabAt(tabPosition).select();
//
//                }
//
//                handler.postDelayed(runnable, 500);
//
//            }
//        };
//
//        handler.post(runnable);
//    }

    @Override
    public void onResume() {
        super.onResume();

        SbAppConstants.STOP_SYNC = false;

        // refreshPage();

        try {
            registerReceiver(receiver, intentFilter);
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

        val = false;
        System.gc();
        super.onDestroy();
    }

    public void onBackPressed() {

        //locationProvider.unregisterReceiver();

        if (tempPref.getBoolean(getString(R.string.is_on_retailer_page), false)) {

            Intent intent = new Intent(RetailerActivity.this, MainActivity.class);
            startActivity(intent);
            RetailerActivity.this.finish();
            //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

        } else {

            Intent intent = new Intent(RetailerActivity.this, OrderBookingRetailing.class);
            intent.putExtra("change_beat", "yes");
            startActivity(intent);
            finish();
            //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }

    }

    private void startServiceToDownloadData() {

        if (utilityClass != null && utilityClass.isInternetConnected()) {

            downloadDataFailed = false;

            Intent startIntent = new Intent(RetailerActivity.this, DownloadDataService.class);
            startIntent.putExtra("appVersion", "");
            startService(startIntent);
        } else if (!utilityClass.isInternetConnected()) {
            downloadDataFailed = true;
            Log.e(TAG, "DownloadDataService Failed");
        }
    }

    @Override
    public void connectionChange(boolean status) {
        if (status && downloadDataFailed) {
            startServiceToDownloadData();
        }
    }

    public void onButtonTabClick(View v) {
        String title = ((Button) v).getText().toString();
        Log.d(TAG, "onButtonTabClick title: " + title);
//        retailerViewPager.setCurrentItem(4);
        if (title.equalsIgnoreCase("Sort")) {
            retailerViewPager.setCurrentItem(0);
        } else if (title.equalsIgnoreCase("Very Active")) {
            retailerViewPager.setCurrentItem(1);
        } else if (title.equalsIgnoreCase("Least Active")) {
            retailerViewPager.setCurrentItem(2);
        } else if (title.equalsIgnoreCase("Visited")) {
            retailerViewPager.setCurrentItem(4);
        } else if (title.equalsIgnoreCase("Scheduled")) {
            retailerViewPager.setCurrentItem(3);
        }

//        switchFragment(v.getId());
    }

   /* private void switchFragment(int id) {
        adapter1 = new ViewPagerAdapter(getSupportFragmentManager());
        Log.d(TAG, "switchFragment: " + id);
        switch (id) {
            case R.id.tab_sort:
                adapter1.addFragment(new VeryActiveRetailerFragment(), "Sort");
                break;
            case R.id.tab_active:
                adapter1.addFragment(new VeryActiveRetailerFragment(), "VeryActive");
                break;
            case R.id.tab_leastActive:
                adapter1.addFragment(new LeastActiveRetailerFragment(), "LeastActive");
                break;
            case R.id.tab_scheduled:
                adapter1.addFragment(new VisitedRetailerList(), "Visited");
                break;
            case R.id.tab_visited:
                adapter1.addFragment(new ScheduledRetailerList(), "Scheduled");
                break;
        }

        retailerViewPagerNew.setAdapter(adapter1);
    }*/

    private void setUpViewPager(ViewPager retailerViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        adapter.addFragment(new SortRetailerFragment(), "Sort");
//        adapter.addFragment(new VeryActiveRetailerFragment(), "VeryActive");
//        adapter.addFragment(new LeastActiveRetailerFragment(), "LeastActive");
        adapter.addFragment(new ScheduledRetailerList(), "Scheduled");
        adapter.addFragment(new VisitedRetailerList(), "Visited");


        /// adapter.addFragment(new VisitedRetailerList(), "Pending");
        retailerViewPager.setAdapter(adapter);

        if (tempPref.getBoolean("isRetVisited", false)) {
            imgBack.setImageResource(R.drawable.ic_home_white_24dp);
        }

        retailerViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private ArrayList<RetailerItem> getVisitedRetailerList() {

        ArrayList<RetailerItem> visitedRetailerList = new ArrayList<>();
        ArrayList<RetailerItem> newRetailerList = new ArrayList<>();
        ArrayList<String> orderPlacedByList = new ArrayList<>();

        Cursor orderPlacedByRetailer = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();
        orderPlacedByList.clear();

        try {

            if (orderPlacedByRetailer != null && orderPlacedByRetailer.getCount() > 0 && orderPlacedByRetailer.moveToFirst()) {

                do {

                    String rid = orderPlacedByRetailer.getString(orderPlacedByRetailer.getColumnIndex("rid"));
                    orderPlacedByList.add(rid);

                } while (orderPlacedByRetailer.moveToNext());
            }

        } catch (Exception e) {
            Log.e("Visted ret", "===" + e.getMessage());
        } finally {
            if (orderPlacedByRetailer != null)
                orderPlacedByRetailer.close();
        }


        //to remove duplicate value from list
        Set<String> hs = new LinkedHashSet<>();
        hs.addAll(orderPlacedByList);
        orderPlacedByList.clear();
        orderPlacedByList.addAll(hs);

        //LIFO list
        Collections.reverse(orderPlacedByList);

        String bid = tempPref.getString(getString(R.string.beat_id_key), "");
        Cursor visitedRetailerCursor = null;

        try {

            if (orderPlacedByList.size() > 0) {

                for (int i = 0; i < orderPlacedByList.size(); i++) {

                    String rid = orderPlacedByList.get(i);
                    visitedRetailerCursor = salesBeatDb.getAllDataFromRetailerListTable2(bid, rid);

                    if (visitedRetailerCursor != null && visitedRetailerCursor.getCount() > 0
                            && visitedRetailerCursor.moveToFirst()) {

                        RetailerItem visitedRetailerItem = new RetailerItem();

                        visitedRetailerItem.setRetailerId(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_id")));
                        visitedRetailerItem.setRetailerbeat_unic_id(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("beat_id_r")));
                        visitedRetailerItem.setRetailer_unic_id(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("ruid_id")));
                        visitedRetailerItem.setRetailerName(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_name")));
                        visitedRetailerItem.setRetailerPhone(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("owner_phone")));
                        visitedRetailerItem.setRetailerAddress(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_address")));
                        visitedRetailerItem.setRetailer_state(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_state")));
                        visitedRetailerItem.setRetailer_email(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_email")));
                        visitedRetailerItem.setRetailer_owner_name(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("owner_name")));
                        visitedRetailerItem.setRetailer_gstin(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_gstin")));
                        visitedRetailerItem.setRetailer_fssai(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_fssai")));
                        visitedRetailerItem.setReatilerWhatsAppNo(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("whatsapp_no")));
                        visitedRetailerItem.setRetailer_city(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("zone")));
                        visitedRetailerItem.setReatialerTarget(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("target")));
                        visitedRetailerItem.setRetailer_grade(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_grade")));
                        visitedRetailerItem.setRetailerLocality(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("locality")));
                        visitedRetailerItem.setRetailer_image(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_image")));
                        visitedRetailerItem.setRetailer_pin(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_pin")));
                        visitedRetailerItem.setLatitude(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_latitude")));
                        visitedRetailerItem.setLongtitude(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_longtitude")));

                        Cursor orderType = salesBeatDb.getOrderTypeFromOderPlacedByRetailersTable(orderPlacedByList.get(i));
                        orderType.moveToFirst();
                        visitedRetailerItem.setOrderType(orderType.getString(orderType.getColumnIndex("order_type")));
                        visitedRetailerItem.setTimeStamp(orderType.getString(orderType.getColumnIndex("taken_at")));
                        visitedRetailerItem.setServerStatus(orderType.getString(orderType.getColumnIndex("order_status")));

                        visitedRetailerList.add(visitedRetailerItem);
                    }
                }
            }

        } catch (Exception e) {
            e.getMessage();
            Log.e(TAG, "getVisitedRetailerList: " + e.getMessage());
        } finally {

            if (visitedRetailerCursor != null)
                visitedRetailerCursor.close();
        }

        //add new retailer list into visited retailer
        newRetailerList = getNewRetailerList(bid);

        visitedRetailerList.addAll(newRetailerList);

        Collections.sort(visitedRetailerList, new Comparator<RetailerItem>() {
            @Override
            public int compare(RetailerItem r1, RetailerItem r2) {
                return r2.getTimeStamp().compareTo(r1.getTimeStamp());
            }
        });

        // to disable the tab and swiping when the list size is 0
        if (visitedRetailerList.size() == 0) {
            //disableVisitedTab();
            retailerViewPager.setPagingEnabled(false);
        } else {
            retailerViewPager.setPagingEnabled(true);
        }


        return visitedRetailerList;
    }

    private ArrayList<RetailerItem> getNewRetailerList(String bidd) {
        Cursor orderPlacedByNew = null, cursor;
        ArrayList<RetailerItem> newRetailerList = new ArrayList<>();

        cursor = salesBeatDb.getAllDataFromNewRetailerListTable2(bidd);
        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    RetailerItem retailerItem = new RetailerItem();

//                    String new_rid = cursor.getString(cursor.getColumnIndex("nrid"));
                    String new_rid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_RETAILER_TEMP_IDD));
                    Log.e("Visited", "....NRID:" + new_rid);
                    String shop_name = cursor.getString(cursor.getColumnIndex("new_retailer_name"));
                    String shop_address = cursor.getString(cursor.getColumnIndex("new_retailer_address"));
                    String owner_name = cursor.getString(cursor.getColumnIndex("new_owner_name"));
                    String owner_mobile_no = cursor.getString(cursor.getColumnIndex("new_owner_phone"));
                    String whatsappNo = cursor.getString(cursor.getColumnIndex("new_whatsapp_no"));
                    String lat = cursor.getString(cursor.getColumnIndex("new_retailer_latitude"));
                    String longt = cursor.getString(cursor.getColumnIndex("new_retailer_longtitude"));
                    String state = cursor.getString(cursor.getColumnIndex("new_retailer_state"));
                    String zone = cursor.getString(cursor.getColumnIndex("new_zone"));
                    String locality = cursor.getString(cursor.getColumnIndex("new_locality"));
                    String pincode = cursor.getString(cursor.getColumnIndex("new_retailer_pin"));
                    String email_id = cursor.getString(cursor.getColumnIndex("new_retailer_email"));
                    String gstin = cursor.getString(cursor.getColumnIndex("new_retailer_gstin"));
                    String fssai_no = cursor.getString(cursor.getColumnIndex("new_retailer_fssai"));
                    String grade = cursor.getString(cursor.getColumnIndex("new_retailer_grade"));

                    retailerItem.setRetailerId(new_rid);
                    retailerItem.setRetailerbeat_unic_id(new_rid);
                    retailerItem.setRetailer_unic_id(new_rid);
                    retailerItem.setRetailerName(shop_name);
                    retailerItem.setRetailerPhone(owner_mobile_no);
                    retailerItem.setRetailerAddress(shop_address);
                    retailerItem.setRetailer_state(state);
                    retailerItem.setRetailer_email(email_id);
                    retailerItem.setRetailer_owner_name(owner_name);
                    retailerItem.setRetailer_gstin(gstin);
                    retailerItem.setRetailer_fssai(fssai_no);
                    retailerItem.setReatilerWhatsAppNo(whatsappNo);
                    retailerItem.setRetailer_city(zone);
                    retailerItem.setRetailer_grade(grade);
                    retailerItem.setRetailerLocality(locality);
                    retailerItem.setRetailer_image("");
                    retailerItem.setRetailer_pin(pincode);
                    retailerItem.setLatitude(lat);
                    retailerItem.setLongtitude(longt);

                    String orderType = "";
                    String orderTakenTime = "";
                    String serverStatus = "";

                    orderPlacedByNew = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable(new_rid);
                    Log.e("Visited", "....Count:" + orderPlacedByNew.getCount());
                    if (orderPlacedByNew != null && orderPlacedByNew.getCount() > 0 && orderPlacedByNew.moveToFirst()) {
                        orderType = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_order_comment"));
                        orderTakenTime = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_taken_at"));
                        serverStatus = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("server_status"));
                        Log.e("Visited", "....Status:" + serverStatus);
                    }

                    if (orderType.equalsIgnoreCase("new productive")) {
                        retailerItem.setOrderType("new productive");
                        retailerItem.setTimeStamp(orderTakenTime);
                    } else {
                        retailerItem.setOrderType("no order new");
                        retailerItem.setTimeStamp(orderTakenTime);
                    }

                    retailerItem.setServerStatus(serverStatus);

                    newRetailerList.add(retailerItem);

                } while (cursor.moveToNext());
            }

            //to remove duplicate value from list
            Set<RetailerItem> hs2 = new LinkedHashSet<>();
            hs2.addAll(newRetailerList);
            newRetailerList.clear();
            newRetailerList.addAll(hs2);

        } catch (Exception e) {
            Log.e("VisRet", "===" + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            if (orderPlacedByNew != null)
                orderPlacedByNew.close();
        }

        //to remove duplicate value from list
        ArrayList<RetailerItem> newList = new ArrayList<>();
        ArrayList<String> newList2 = new ArrayList<>();

        for (int i = 0; i < newRetailerList.size(); i++) {
            String nrid = newRetailerList.get(i).getRetailerId();
            RetailerItem newRetailerItem = newRetailerList.get(i);
            if (!newList2.contains(nrid)) {

                newList.add(newRetailerItem);
                newList2.add(nrid);
            }
        }

        return newList;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                RetailerActivity.this.finishAffinity();
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

//    private void disableVisitedTab(){
//
//        LinearLayout tabStrip = ((LinearLayout) retailerTab.getChildAt(0));
//        for (int i = 0; i < tabStrip.getChildCount(); i++) {
//            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    return true;
//                }
//            });
//        }
//        // To show the user that visited tab is non selectable.
//        ViewGroup visitedTab = (ViewGroup) tabStrip.getChildAt(1);
//        visitedTab.setAlpha((float) 0.25);
//    }

}
