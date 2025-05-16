package com.newsalesbeatApp.activities;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.LeftMenuAdapter;
import com.newsalesbeatApp.adapters.OfflineNotificationAdapter;
import com.newsalesbeatApp.duomenu.DuoDrawerLayout;
import com.newsalesbeatApp.duomenu.DuoDrawerToggle;
import com.newsalesbeatApp.duomenu.DuoMenuView;
import com.newsalesbeatApp.fragments.DashboardFragment;
import com.newsalesbeatApp.fragments.MyMenuFragment;
import com.newsalesbeatApp.fragments.NotificationFragment;
import com.newsalesbeatApp.interfaces.OnHomePressedListener;
import com.newsalesbeatApp.netwotkcall.VolleyMultipartRequest;
import com.newsalesbeatApp.receivers.AlarmReceiver;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.services.IsActiveService;
import com.newsalesbeatApp.services.SbService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.HomeWatcher;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.TransactionOptions;


public class MainActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener, NetworkChangeInterface {


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static CoordinatorLayout mainActivityLayout;
    public static MainActivity mainActivity;
    private static int tabPos = 0;
    MyAsynchTask myAsynchTask;
    HomeWatcher mHomeWatcher;
    TextView imgUploadDb;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    RecyclerView eventRecycler;
    //String strTitle = "ATTENDANCE DELAY", strBody = "Please mark your attendance/leave, else absent will be considered.";
    ArrayList<OfflineNotificationModel> townItems = new ArrayList<>();
    private String TAG = "MainActivity";
    private SharedPreferences prefSFA, tempSfa;
    private LeftMenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    private Handler handler;
    private Runnable runnable;
    private UtilityClass utilityClass;
    private SalesBeatDb salesBeatDb;
    private TextView internetMode;
    private ProgressBar pbRefreshing;
    private ImageView imgRefresh, imgNotification, closeNotification;
    private Toolbar toolbar;
    private TabLayout bottomTab;
    private int count = 0;
    private ArrayList<String> mTitles = new ArrayList<>();
    private GPSLocation locationProvider;
    private ViewPager homePager;
    private TextView notifCount;
    private FrameLayout frameLayout;
    private NotificationFragment notifFrag;
    private int newNotifCount = 0;
    private boolean isNotifOpen = false;
    private boolean newNotif = false;
    private Animation expand, collapse;
    private SharedPreferences myPref;
    private int REQ_CODE = 191;
    //Rajeev code
    private ArrayList<OfflineNotificationModel> PrayerModelList = new ArrayList<>();
    private OfflineNotificationAdapter mAdapter;
    AlarmManager alarmManager;
    PendingIntent receiverIntent;


    //ServerCall serverCall;

    @TargetApi(Build.VERSION_CODES.S)
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TAG", "MainActivity page");
        /*FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:292187759442:android:56dbc7cc5fa95edb95508b") // mobilesdk_app_id
                .setApiKey("AIzaSyAcFIbgmm8Q1wqCOs1PnPyV-neUhyQD41M") // current_key
                // Add other necessary configurations if needed (like Database URL, Storage Bucket, etc.)
                .build();

        FirebaseApp.initializeApp(this *//* Context *//*, options, "secondary");*/


//        IntentFilter intentFilter1 = new IntentFilter(ConnectivityChangeReceiver.NETWORK_AVAILABLE_ACTION);
//
//        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
//                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";
//
//                Toast.makeText(context, "Network Status: " + networkStatus, Toast.LENGTH_SHORT).show();
////                Snackbar.make(findViewById(R.id.activity_main), "Network Status: " + networkStatus, Snackbar.LENGTH_LONG).show();
//            }
//        }, intentFilter1);


//        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content), (v, insets) -> {
//            Insets statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
//            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
//
//            v.setPadding(0, statusInsets.top, 0, navInsets.bottom);
//            return insets;
//        });

        sentryException();
        performNetworkRequest();

        long startTime = SystemClock.elapsedRealtime();
        long endTime = SystemClock.elapsedRealtime();
        long durationMs = endTime - startTime;
        myPref = getApplicationContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        /*Sentry.startTransaction("NetworkRequestTransaction", "network_request");

        Sentry.getCurrentHub().configureScope(scope -> {
            scope.setTag("request_type", "network");
            scope.setTransaction("Network Request Transaction");
        });*/


        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        mainActivityLayout = findViewById(R.id.main_content);
        internetMode = findViewById(R.id.internetMode);
        bottomTab = findViewById(R.id.bottomTabs);
        homePager = findViewById(R.id.viewpager);
        frameLayout = findViewById(R.id.frame_notif);
        notifCount = findViewById(R.id.notif_count);
        notifFrag = new NotificationFragment();
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent myIntent = new Intent(this, AlarmReceiver.class);

        //@Umesh 15-Aug-2022
        receiverIntent = PendingIntent.getBroadcast(this, REQ_CODE,
                myIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        //FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ld_activityScreenTitles)));
        //salesBeatDb = new SalesBeatDb(MainActivity.this);
        salesBeatDb = SalesBeatDb.getHelper(this);
        utilityClass = new UtilityClass(MainActivity.this);
        mainActivity = MainActivity.this;
        mViewHolder = new ViewHolder();
        handler = new Handler();
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        intentFilter.addAction(NetworkChangeReceiver.NETWORK_AVAILABLE_ACTION);

        receiver = new NetworkChangeReceiver();
        locationProvider = new GPSLocation(this);
        SbAppConstants.isAppAlive = true;
        SbLog.recordScreen("MainActivity");
        ValidateToken();

        receiver.InitNetworkListener(this);

        // Handle toolbar actions.............
        handleToolbar();

        // Handle menu actions................
        handleMenu();

        // Handle drawer actions..............
        handleDrawer();

//        //Setting initial info of user........
//        initializeUserInfo();

        //To get app version..................
        setAppVersion();
        //forefully close app even pressing device home button
        homePresss();

        new PingServer(internet -> {
            /* do something with boolean response */
            if (internet) {
                //Check user availability.............
                checkUserAvailability();
            }
        });

        new LoadBackGroundNotificationList().execute();

        if (tempSfa.getString("createpjp", "").equalsIgnoreCase("0")) {
            Intent intent2 = new Intent(MainActivity.this, MyPjpActivity.class);
            intent2.putExtra("page_title", "MyPJP");
            startActivity(intent2);
        }

//        Toast.makeText(mainActivity, "Main Activity", Toast.LENGTH_SHORT).show();
//        Toast.makeText(mainActivity, "Main Activity"+ConnectivityChangeReceiver.isConnectedToInternet(this), Toast.LENGTH_SHORT).show();

//        Sentry.capture(new Exception("Test4...."));
        //throw new RuntimeException("Test Crash"); // Force a crash


    }

    private void performNetworkRequest() {
        try {
            Thread.sleep(2000); // Simulate a delay of 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sentryException() {
        Log.d("TAG", "sentryException");
        SharedPreferences prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        Sentry.configureScope(scope -> {
            scope.setTag("page_locale", "en_US");
            scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//            scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
        });

        TransactionOptions txOptions = new TransactionOptions();
        txOptions.setBindToScope(true);
        ITransaction transaction = Sentry.startTransaction("SalesBeat Events", "task", txOptions);
        try {
            processOrderBatch(transaction);
        } catch (Exception e) {
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            throw e;
        } finally {
            transaction.finish();
        }
    }

    void processOrderBatch(ISpan span) {
        if (span == null) {
            span = Sentry.startTransaction("processOrderBatch()", "task");
        }
        // span operation: task, span description: operation
        ISpan innerSpan = span.startChild("SalesBeat Check", "operation");
        try {
            Log.d("TAG", "processOrderBatch call Token");
            ValidateToken();
        } catch (Exception e) {

        } finally {
            innerSpan.finish();
        }
    }


    protected void ValidateToken() {
        Log.d("TAG", "check ValidateToken");
        String token = prefSFA.getString("token", "");
        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        JsonObjectRequest ValidateRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_AuthencateUser, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e(TAG, "User Login Token===" + response);
                            if (response.getInt("status") == 0) {
                                Toast.makeText(getApplicationContext(), "Invalid Token!!", Toast.LENGTH_SHORT);
                                Log.e(TAG, "Invalid Token!!: ");

                                SharedPreferences.Editor editor = prefSFA.edit();
                                editor.putBoolean(getString(R.string.is_logged_in), false);
                                editor.putString("userStatus", "logout");
                                editor.apply();

                                Intent intent = new Intent();
                                intent.setClass(getApplicationContext(), SplashScreen.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(intent);

                            } else {
                                startService(new Intent(getBaseContext(), IsActiveService.class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("TAG", "check Exception: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Sentry.capture(error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        ValidateRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(ValidateRequest);
    }



    @Override
    public void connectionChange(boolean status) {
//        Toast.makeText(mainActivity, "Status"+status, Toast.LENGTH_SHORT).show();
    }

    public void openNotifications() {
        newNotif = false;
        notifCount.setVisibility(View.INVISIBLE);
        imgNotification.setVisibility(View.INVISIBLE);
        closeNotification.setVisibility(View.VISIBLE);

        imgNotification.startAnimation(collapse);
        closeNotification.startAnimation(expand);

        isNotifOpen = true;
        //bottomTab.setVisibility(View.INVISIBLE);
        //frameLayout.setVisibility(View.VISIBLE);
        //homePager.setVisibility(View.INVISIBLE);
        //loader.setVisibility(View.INVISIBLE);
        salesBeatDb.updateReadStatusNotif("read");
        if (!notifFrag.isAdded())
            getSupportFragmentManager().beginTransaction().add(R.id.frame_notif, notifFrag).commit();

        slideUp(frameLayout);
    }

    public void closeNotifications() {
        //frameLayout.setVisibility(View.INVISIBLE);
        //homePager.setVisibility(View.VISIBLE);
        //bottomTab.setVisibility(View.VISIBLE);
        isNotifOpen = false;
        newNotif = false;

        closeNotification.setVisibility(View.INVISIBLE);
        imgNotification.setVisibility(View.VISIBLE);
        notifCount.setVisibility(View.INVISIBLE);
        imgNotification.startAnimation(expand);
        closeNotification.startAnimation(collapse);

        slideDown(frameLayout);
    }

    // slide the view from below itself to the current position
    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                bottomTab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottomTab.setVisibility(View.VISIBLE);
                if (notifFrag.isAdded())
                    getSupportFragmentManager().beginTransaction().remove(notifFrag).commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check joint working.................
        checkJointWorking();

        //initialize main view................
        initializeViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Setting initial info of user........
        initializeUserInfo();

        //var = true;

        if (mMenuAdapter != null) {
            mMenuAdapter.setViewSelected(0, true);
            mViewHolder.mDuoDrawerLayout.closeDrawer();
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(receiver, intentFilter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //check gps status if on/off
        Log.e(TAG, "onResume");
        locationProvider.checkGpsStatus();

        //starting service
        //startBackgroundService();

        //startUpdateLocation
        setRecurringAlarm();

        if (!checkPlayServices()) {

            Toast.makeText(mainActivity, "Please install Google Play services.", Toast.LENGTH_SHORT).show();
        }

        newNotif = false;
        newNotifCount = 0;
        new LoadBackGroundNotificationList().execute();

        Calendar cal = Calendar.getInstance();
        String date = utilityClass.getYMDDateFormat().format(cal.getTime());

        //clearing SharedPreferences and database....on the basis of date change.....and record..status was success
        Log.e(TAG, "Saved Date = " + prefSFA.getString(getString(R.string.date_key), "") + " AND Current Date = " + date);

        if (!prefSFA.getString(getString(R.string.date_key), "").equalsIgnoreCase(date)) {
            Log.d(TAG, "PinSetup");
            new Intent(MainActivity.this, PinSetup.class);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        Log.e(TAG, "onPause");
        try {

            unregisterReceiver(receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {

        Log.e(TAG, "onStop");
        try {
            if (myAsynchTask != null)
                myAsynchTask.cancel(true);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (mHomeWatcher != null) {
            mHomeWatcher.stopWatch();
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        SbAppConstants.isAppAlive = false;
        stopService(new Intent(this, SbService.class));
        setRecurringAlarm();
        System.gc();
        super.onDestroy();
    }

    private void homePresss() {

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                // do something here...
                try {

                    mHomeWatcher.stopWatch();
                    handler.removeCallbacks(runnable);
                    MainActivity.this.finishAffinity();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onHomeLongPressed() {
                Log.e(TAG, "###############");
            }
        });

        mHomeWatcher.startWatch();
    }

    private void setRecurringAlarm() {

        if (SbAppConstants.isAppAlive) {

            try {
                alarmManager.cancel(receiverIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //startServiceIfAppOpen
            Intent syncService = new Intent(this, SbService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(syncService);
            } else {
                startService(syncService);
            }


        } else {

            Log.d("Carbon", "Alarm SET !!");

            try {
                stopService(new Intent(this, SbService.class));
            } catch (Exception e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();

            assert alarmManager != null;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    repeatTime(), receiverIntent);


        }

    }

    //    private long repeatTime() {
//        return 30000;
//    }
    //@Umesh 20221002
    private long repeatTime() {
        return 300000; // 5 Min
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void initializeUserInfo() {


        if (!prefSFA.getString(getString(R.string.emp_pic_url_key), "").isEmpty()) {

            final ImageView userProfilePic = findViewById(R.id.userProfilePic);

            Glide.with(this)
                    .load(new File(prefSFA.getString(getString(R.string.emp_pic_url_key), "")))
                    .override(100, 100)
                    .into(userProfilePic);
        }

        LinearLayout llIncentive = findViewById(R.id.llIncentive);

        TextView tvUserName = findViewById(R.id.duo_view_header_text_title);
        tvUserName.setText(prefSFA.getString(getString(R.string.emp_name_key), ""));

        TextView tvUserIncentive = findViewById(R.id.tvUserIncentive);
        ImageView imgIncentiveInfo = findViewById(R.id.imgIncentiveInfo);
        String inc = prefSFA.getString(getString(R.string.incentive_key), "");
        boolean incStatus = prefSFA.getBoolean(getString(R.string.incentive_status_key), false);
        double incentive = 0;
        if (!inc.isEmpty())
            incentive = Double.parseDouble(inc);
        tvUserIncentive.setText(getString(R.string.Rs) + " " + new DecimalFormat("##.##").format(incentive));
        if (incStatus) {

            llIncentive.setVisibility(View.VISIBLE);
        } else {

            llIncentive.setVisibility(View.GONE);
        }


        TextView tvUserDesignation = findViewById(R.id.duo_view_header_text_sub_title);
        tvUserDesignation.setText("Designation - " + prefSFA.getString(getString(R.string.emp_designation_key), ""));

        TextView tvUserMobile = findViewById(R.id.duo_view_header_text_mobile);
        tvUserMobile.setText(prefSFA.getString(getString(R.string.emp_phoneno_key), ""));

        TextView tvEmpName = findViewById(R.id.empName);
        tvEmpName.setText(prefSFA.getString(getString(R.string.emp_name_key), ""));

        TextView tvuserId = findViewById(R.id.userId);
        tvuserId.setText(prefSFA.getString("username", ""));

        try {

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            String verCode = String.valueOf(pInfo.versionCode);

            //to check live or test
            String app = "";
            if (getString(R.string.url_mode).equalsIgnoreCase("T"))
                app = "T";
            else if (getString(R.string.url_mode).equalsIgnoreCase("L"))
                app = "L";
            else if (getString(R.string.url_mode).equalsIgnoreCase("N"))
                app = "N";

            TextView duo_view_footer_text = findViewById(R.id.duo_view_footer_text);
            if (app.equalsIgnoreCase("T"))
                duo_view_footer_text.setText(version + " Test " + "(" + verCode + ")");
            else if (app.equalsIgnoreCase("L"))
                duo_view_footer_text.setText(version + " Live" + "(" + verCode + ")");
            else if (app.equalsIgnoreCase("N"))
                duo_view_footer_text.setText(version + " Live" + "(" + verCode + ")");


        } catch (Exception e) {
            e.printStackTrace();
        }


        imgUploadDb = findViewById(R.id.imgUploadDb);

        // set up spanned string with url
        SpannableString content = new SpannableString("Send Log");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        imgUploadDb.setText(content);

        imgUploadDb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDb();
            }
        });


        imgIncentiveInfo.setOnClickListener(view -> new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(MainActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                if (utilityClass.isInternetConnected()) {
                    Log.d("TAG", "initializeUserInfo");
                    Intent intent = new Intent(MainActivity.this, IncentiveHistory.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }

        }));

    }

    private void uploadDb() {
        Log.d(TAG, "uploadDb");
        String y = "", n = "";
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.alert));
        builder.setMessage("Are you sure you want to upload your database");
        y = getString(R.string.yes);
        n = getString(R.string.no);

        builder.setPositiveButton(y, (dialogInterface, i) -> new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(MainActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                if (utilityClass.isInternetConnected()) {

                    salesBeatDb.getMyDB();
                    sendDb();

                } else {
                    Toast.makeText(MainActivity.this, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }

        }));

        builder.setNegativeButton(n, (dialogInterface, i) -> dialogInterface.dismiss());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void sendDb() {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        final VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, SbAppConstants.API_SUBMIT_DB,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        loader.dismiss();
                        try {
                            //@Umesh 26-06-2022
                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.e(TAG, "Data Response: " + obj.toString());
                            if (obj.getInt("status") == 1) {
                                Toast.makeText(MainActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                Toast.makeText(MainActivity.this, "Error:" + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                try {

                    if (error != null && error.networkResponse != null) {

                        SbLog.printError(TAG, "submitDb", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                                prefSFA.getString(getString(R.string.emp_id_key), ""));
                    } else {

                        Toast.makeText(MainActivity.this, "Null error code", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart("db", getFileDataFromDrawable()));
                Log.e(TAG, "DB file path:" + params.toString());
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(this).add(request);
    }

    private byte[] getFileDataFromDrawable() {

        try {
            //String PATH = Environment.getExternalStorageDirectory() + "/CMNY/sb.db";
            //@Umesh 26-06-2022
            String PATH = Environment.getExternalStorageDirectory() + "/NewSalesBeat/sb.db";
            File file = new File(PATH);
            //init array with file length
            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            Log.e(TAG, "File array:" + Arrays.toString(bytesArray));
            return bytesArray;

        } catch (Exception e) {
            Log.e(TAG, "Exception");
            e.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SetTextI18n")
    private void setAppVersion() {


        try {

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            String verCode = String.valueOf(pInfo.versionCode);
            TextView tvAppVersion = findViewById(R.id.appVersion);

            //to check live or test
            String app = "";
            if (getString(R.string.url_mode).equalsIgnoreCase("T"))
                app = "T";
            else if (getString(R.string.url_mode).equalsIgnoreCase("L"))
                app = "L";

            tvAppVersion.setText(version + app + "(" + verCode + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void checkJointWorking() {


        try {

            Intent jwIntent = getIntent();
            boolean jwStatus = jwIntent.getBooleanExtra("joint_working", false);
            String action = jwIntent.getAction();

            if (jwStatus) {

                if (action != null && action.equalsIgnoreCase("requestAccepted")) {

                    SharedPreferences.Editor editor = tempSfa.edit();
                    editor.putBoolean(getString(R.string.isonjw_key), true);
                    editor.apply();

                    showOrderDialog();

                } else if (action != null && action.equalsIgnoreCase("JWAccepted")) {

                    SharedPreferences.Editor editor = tempSfa.edit();
                    editor.putBoolean(getString(R.string.isonjw_key), true);
                    editor.apply();

                    showAlertDialog("Your request of joint working has been accepted");

                } else if (action != null && action.equalsIgnoreCase("JWRejected")) {

                    showAlertDialog("Your request of joint working has been rejected");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showAlertDialog(String msg) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        Dialog dialog = alertDialog.create();
        dialog.show();
    }

    public void initializeViewPager() {

        if (homePager.getAdapter() == null) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new DashboardFragment(), getString(R.string.working_e));
            adapter.addFragment(new MyMenuFragment(), getString(R.string.menu));

            homePager.setAdapter(adapter);
            homePager.setOffscreenPageLimit(1);
        }

        /*Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (homePager != null) {
                ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                adapter.addFragment(new DashboardFragment(), getString(R.string.working_e));
                adapter.addFragment(new MyMenuFragment(), getString(R.string.menu));
                homePager.setAdapter(adapter);
            }
            handler.removeCallbacksAndMessages(null); // ✅ Stop repeating
        }, 100);*/



      /*  ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homePager.setAdapter(adapter);  // Set adapter first
        adapter.addFragment(new DashboardFragment(), getString(R.string.working_e));
        adapter.addFragment(new MyMenuFragment(), getString(R.string.menu));*/

//        bottomTab.setupWithViewPager(homePager);
//        TabLayout.Tab tab = bottomTab.getTabAt(tabPos);
//        if (tab != null)
//            tab.select();
//
//
//        homePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                tabPos = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });

        bottomTab.setupWithViewPager(homePager);

// Select the correct tab initially
        TabLayout.Tab tab = bottomTab.getTabAt(tabPos);
        if (tab != null) tab.select();

        bottomTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment = (Fragment) homePager.getAdapter().instantiateItem(homePager, position);

                if (fragment != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.detach(fragment); // Remove the fragment
                    ft.attach(fragment); // Re-add it to force reload
                    ft.commit();
                }

                homePager.setCurrentItem(position, false); // Set the selected tab
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment fragment = (Fragment) homePager.getAdapter().instantiateItem(homePager, position);

                if (fragment != null) {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();
                }
            }
        });

// Handle ViewPager Page Change
        homePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


        private void handleToolbar() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");

        imgRefresh = toolbar.findViewById(R.id.imgRefresh);
        imgNotification = toolbar.findViewById(R.id.imgNotification);
        closeNotification = toolbar.findViewById(R.id.close_notif);
        pbRefreshing = toolbar.findViewById(R.id.pbRefreshing);

        Log.d("TAG", "handleToolbar: " + getString(R.string.url_mode));
        if (getString(R.string.url_mode).equalsIgnoreCase("T")) {
            Log.d("TAG", "Check Color");
            toolbar.setBackgroundColor(Color.parseColor("#c0392b"));
        }

        expand = AnimationUtils.loadAnimation(this, R.anim.expand_fade);
        collapse = AnimationUtils.loadAnimation(this, R.anim.shrink_fade);

        pbRefreshing.setVisibility(View.GONE);
        imgRefresh.setVisibility(View.VISIBLE);

        /*Rajeev Data*/
        eventRecycler = (RecyclerView) findViewById(R.id.eventRecycler);
        imgNotification.setOnClickListener(v -> {
//                if (eventRecycler.getVisibility() == View.VISIBLE) {
//                    eventRecycler.setVisibility(View.GONE);
//                } else {
//                    eventRecycler.setVisibility(View.VISIBLE);
//                }
            if (townItems.size() > 0)
                openNotifications();
            else
                Toast.makeText(MainActivity.this, "No notification(s) available!", Toast.LENGTH_SHORT).show();
        });

        closeNotification.setOnClickListener(v -> {
            closeNotifications();
        });

        imgRefresh.setOnClickListener(view -> new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(MainActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                if (utilityClass.isInternetConnected()) {

                    try {

                        if (myAsynchTask == null) {

                            myAsynchTask = new MyAsynchTask(pbRefreshing, imgRefresh);
                            myAsynchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        } else {
                            myAsynchTask.cancel(true);
                            myAsynchTask = null;
                            myAsynchTask = new MyAsynchTask(pbRefreshing, imgRefresh);
                            myAsynchTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else
                    Toast.makeText(MainActivity.this, "Not connected to internet", Toast.LENGTH_SHORT).show();
            }

        }));

        setSupportActionBar(toolbar);


    }

    private void refreshDataBase() {

        //clear database from uploaded data
        try {
            Calendar cal = Calendar.getInstance();
            String date = utilityClass.getYMDDateFormat().format(cal.getTime());

            salesBeatDb.deleteAllDataFromOderPlacedByRetailersTable4(date);
            salesBeatDb.deleteSpecificDataFromNewRetailerListTable4(date);
            salesBeatDb.deleteSpecificDataFromNewOrderEntryListTable4(date);
            salesBeatDb.deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable4(date);
            salesBeatDb.deleteSpecificDataFromOrderEntryListTable4(date);
            salesBeatDb.deleteAllDataFromSkuEntryListTable4(date);
            salesBeatDb.deleteAllFromDistributorOrderTable4(date);
            salesBeatDb.deleteAllDataFromNewDistributorTable4(date);
            salesBeatDb.deleteLeaderboardDetail2(date);
            salesBeatDb.deleteEmpKraDetails2(date);
            salesBeatDb.deleteOtherActivity2(date);

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void handleDrawer() {
        Log.d(TAG, "click handleDrawer");

        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(MainActivity.this,
                mViewHolder.mDuoDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void handleMenu() {

        mMenuAdapter = new LeftMenuAdapter(mTitles);
        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
        mMenuAdapter.setViewSelected(0, true);
    }

    private void checkUserAvailability() {

        runnable = new Runnable() {
            @Override
            public void run() {

                if (utilityClass.isInternetConnected()) {
                    internetMode.setText(getString(R.string.online));

                    // serverCall.addNewRet();

                } else
                    internetMode.setText(getString(R.string.offline));

                handler.postDelayed(runnable, 500);

            }
        };

        handler.post(runnable);
    }

    private void showOrderDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("You are on joint working now!");
        builder.setCancelable(false);
        builder.setMessage("Will you BOOK ORDER today? / क्या आज आप आर्डर लेना चाहोगे ?");

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                getIntent().putExtra("joint_working", false);

                SharedPreferences.Editor tempEditor = tempSfa.edit();
                tempEditor.putString(getString(R.string.askfororder_key), "yes");
                tempEditor.apply();

            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                getIntent().putExtra("joint_working", false);
                SharedPreferences.Editor tempEditor = tempSfa.edit();
                tempEditor.putString(getString(R.string.askfororder_key), "no");
                tempEditor.apply();

            }
        });

        Dialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onBackPressed() {

        if (mViewHolder.mDuoDrawerLayout.isDrawerOpen()) {
            // Close the drawer
            mViewHolder.mDuoDrawerLayout.closeDrawer();

        } else if (isNotifOpen) {
//            frameLayout.setVisibility(View.INVISIBLE);
//            homePager.setVisibility(View.VISIBLE);
//            bottomTab.setVisibility(View.VISIBLE);
//            isNotifOpen = false;
//            getSupportFragmentManager().beginTransaction().remove(notifFrag).commit();

            closeNotifications();
        } else {

            Snackbar snackbar = Snackbar
                    .make(mainActivityLayout, "Press again to exit", Snackbar.LENGTH_LONG);

            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {

                    count = 0;
                }

                @Override
                public void onShown(Snackbar snackbar) {

                }
            });


            if (count == 2) {

                handler.removeCallbacks(runnable);
                MainActivity.this.finishAffinity();

            } else {

                snackbar.show();
            }

            count = 2;

        }
    }

    @Override
    public void onFooterClicked() {

    }

    @Override
    public void onHeaderClicked() {

    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {

        // Set the right options selected
        Log.d(TAG, "onOptionClicked: " + position);
        mMenuAdapter.setViewSelected(position, true);

        // Navigate to the right fragment
        switch (position) {

            case 0:

                // Close the drawer
                mViewHolder.mDuoDrawerLayout.closeDrawer();
                break;

            case 1:

                Intent intent2 = new Intent(MainActivity.this, MyPjpActivity.class);
                intent2.putExtra("page_title", "MyPJP");
                startActivity(intent2);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;

            case 2:

                Intent intent1 = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent1);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;

            case 3:

                showCustomLogoutDialog();
                /*Intent intent3 = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent3);*/
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;

            default:
                break;
        }

    }

    private void showCustomLogoutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_gdpr_basic);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        EditText edtPass = dialog.findViewById(R.id.edtPassword);
//        ((TextView) dialog.findViewById(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());


        ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginPass = prefSFA.getString("password", "");
                Log.d(TAG, "Login password: " + loginPass);
                String pass = edtPass.getText().toString();
                Log.d(TAG, "onClick password: " + pass);

                if(!pass.equalsIgnoreCase("")){
                    if(loginPass.equals(pass)){
//                        getString(R.string.temp_pref_name);
//                        getString(R.string.pref_name);
                        SharedPreferences preferences = getSharedPreferences(getString(R.string.pref_name), MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                       /* SharedPreferences.Editor editor = prefSFA.edit();
                        editor.putString("username", "");
                        editor.putString("password", "");
                        editor.putString(getString(R.string.check_in_time_key), "");
                        editor.putString(getString(R.string.check_out_time_key), "");
                        editor.remove(getString(R.string.saved_pin_key));
                        editor.putBoolean(getString(R.string.is_logged_in), true);
                        editor.apply();*/

                        Intent intent1 = new Intent(MainActivity.this, LoginScreen.class);
                        startActivity(intent1);
                    }else {
                        Toast.makeText(getApplicationContext(), "Invalid password..", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "please enter password..", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button Decline Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void getEmpKraByDate() {

        SimpleDateFormat sdff = utilityClass.getYMDDateFormat();
        final String date = sdff.format(Calendar.getInstance().getTime());
        //String date ="2018-03-20";
        JSONObject content = new JSONObject();
        try {
            content.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_KRA_BY_DATE + "?date=" + date, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("onResponse", "KRA===" + response);

                        try {
                            //@Umesh 02-Feb-2022
                            if (response.getInt("status") == 1) {
                                JSONObject data = response.getJSONObject("data");

                                String tcAchievement = "0";
                                if (!data.isNull("tcAchievement") && data.has("tcAchievement"))
                                    tcAchievement = String.valueOf(data.getLong("tcAchievement"));

                                String pcAchievement = "0";
                                if (!data.isNull("pcAchievement") && data.has("pcAchievement"))
                                    pcAchievement = String.valueOf(data.getString("pcAchievement"));

                                String tcTarget = "0", pcTarget = "0";
                                if (!data.isNull("tcTarget") && data.has("tcTarget"))
                                    tcTarget = String.valueOf(data.getInt("tcTarget"));

                                if (!data.isNull("pcTarget") && data.has("pcTarget"))
                                    pcTarget = String.valueOf(data.getInt("pcTarget"));

                                Calendar cal = Calendar.getInstance();
                                String date = utilityClass.getYMDDateFormat().format(cal.getTime());

                                salesBeatDb.deleteEmpKraDetails();

                                salesBeatDb.insertEmpKraDetail(tcAchievement, pcAchievement,
                                        tcTarget, pcTarget, date);

                                Intent intent = new Intent("com.salesbeat_kra");
                                sendBroadcast(intent);
                            } else {
                                Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    if (error != null && error.networkResponse != null) {

                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle("Message!");
                        dialog.setMessage(error.getMessage());
                        dialog.show();

                        /*SbLog.printError(TAG, "getKraByDate",
                                String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                                prefSFA.getString(getString(R.string.emp_id_key), ""));*/
                    } else {

                        Toast.makeText(MainActivity.this, "Null error code", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getEmpPrimarySaleByDate() {

        final String date = utilityClass.getMYDateFormat().format(Calendar.getInstance().getTime());
        //getMonthlyKraByDate/{month}/{year}
        //@Umesh 02-Feb-2022
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_PRIMARY_SALE_BY_DATE + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "SALE===" + response);

                try {

                    //@Umesh 02-Feb-2022
                    if (response.getInt("status") == 1) {
                        JSONObject data = response.getJSONObject("data");

                        String saleAchievement = "0", saleTarget = "0";
                        if (!data.isNull("saleAchievement") && data.has("saleAchievement"))
                            saleAchievement = data.getString("saleAchievement");

                        if (!data.isNull("saleTarget") && data.has("saleTarget"))
                            saleTarget = data.getString("saleTarget");

                        boolean flag = salesBeatDb.deletePrimarySale();
                        salesBeatDb.insertPrimarySale(saleAchievement, saleTarget);

                        Intent intent = new Intent("com.salesbeat_primary");
                        sendBroadcast(intent);
                    } else {
                        Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    if (error != null && error.networkResponse != null) {

                        SbLog.printError(TAG, "getMonthlyPrimaryKraByDate",
                                String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                                prefSFA.getString(getString(R.string.emp_id_key), ""));
                    } else {

                        Toast.makeText(MainActivity.this, "Null error code", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    //inner classes----------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getEmpSecondarySaleByDate() {

        final String date = utilityClass.getMYDateFormat().format(Calendar.getInstance().getTime());
        //getMonthlyKraByDate/{month}/{year}
        //@Umesh 02-Feb-2022
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_SECONDARY_SALE_BY_DATE + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "SALE===" + response);

                try {

                    //@Umesh 02-Feb-2022
                    if (response.getInt("status") == 1) {
                        JSONObject data = response.getJSONObject("data");

                        String saleAchievement = "0", saleTarget = "0";
                        if (!data.isNull("saleAchievement") && data.has("saleAchievement"))
                            saleAchievement = response.getString("saleAchievement");

                        if (!data.isNull("saleTarget") && data.has("saleTarget"))
                            saleTarget = response.getString("saleTarget");

                        boolean flag = salesBeatDb.deleteSecondarySale();
                        salesBeatDb.insertSecondarySale(saleAchievement, saleTarget);

                        Intent intent = new Intent("com.salesbeat_secondary");
                        sendBroadcast(intent);
                    } else {
                        Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error != null && error.networkResponse != null) {

                    android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("getMonthlySecondaryKraByDate");
                    dialog.setMessage(error.getMessage());
                    dialog.show();

                   /* SbLog.printError(TAG, "getMonthlySecondaryKraByDate",
                            String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                            prefSFA.getString(getString(R.string.emp_id_key), ""));*/
                } else {

                    Toast.makeText(MainActivity.this, "Null error code", Toast.LENGTH_SHORT).show();
                }

            }
        }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void getEmpLeaderBoard() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_GET_EMP_LEADER_BOARD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("onResponse", "LEADER_BOARD===" + response);

                        try {
                            //@Umesh 02-Feb-2022
                            if (response.getInt("status") == 1) {
                                Calendar cal = Calendar.getInstance();
                                String date = utilityClass.getYMDDateFormat().format(cal.getTime());
                                JSONArray topEmpArr = response.getJSONArray("data");
                                salesBeatDb.deleteLeaderboardDetail();
                                for (int i = 0; i < topEmpArr.length(); i++) {
                                    JSONObject object = (JSONObject) topEmpArr.get(i);

                                    salesBeatDb.insertLeaderboardDetail(object.getString("eid"),
                                            object.getString("name"), object.getString("profilePic"),
                                            object.getString("totalCalls"), object.getString("productiveCalls"),
                                            object.getString("totalWeight"), date);
                                }
                                Intent intent = new Intent("com.salesbeat_leaderboard");
                                sendBroadcast(intent);
                            } else {
                                Toast.makeText(MainActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error != null && error.networkResponse != null) {

                    SbLog.printError(TAG, "getEmployeeLeaderboard",
                            String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                            prefSFA.getString(getString(R.string.emp_id_key), ""));

                } else {

                    Toast.makeText(MainActivity.this, "Null error code", Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("filter", "SALES");
                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Toast.makeText(MainActivity.this, "onActivityResult Main Called", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                MainActivity.this.finishAffinity();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadBackGroundNotificationList extends AsyncTask<Void, Void, ArrayList<OfflineNotificationModel>> {
        @Override
        protected ArrayList<OfflineNotificationModel> doInBackground(Void... voids) {

            Cursor cursor = null;

            try {
                cursor = salesBeatDb.getAllRecordFromNotificationTable();
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    do {

                        OfflineNotificationModel item = new OfflineNotificationModel(cursor.getString(cursor.getColumnIndex("inappnoti_photo")),
                                cursor.getString(cursor.getColumnIndex("inappnoti_name")),
                                cursor.getString(cursor.getColumnIndex("inappnoti_date")),
                                cursor.getString(cursor.getColumnIndex("read_status")),
                                Integer.parseInt(cursor.getString(cursor.getColumnIndex("inappnoti_id"))));
                        //  item.setTownName(cursor.getString(cursor.getColumnIndex("town_name")));
                        //  cursor.getString(cursor.getColumnIndex("inappnoti_pic"));
                        Log.e(TAG, "doInBackground: " + cursor.getString(cursor.getColumnIndex("inappnoti_id")));
                        if (item.getReadStatus().equals("unread")) {
                            newNotif = true;
                            newNotifCount++;
                        }

                        townItems.add(item);
                    } while (cursor.moveToNext());

                    Collections.reverse(townItems);

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }


            return townItems;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            townItems.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<OfflineNotificationModel> prayerModels) {
            super.onPostExecute(prayerModels);
            Log.e(TAG, townItems.size() + " Total Notification");
            if (townItems.size() > 0) {

                Bundle bundle = new Bundle();
                bundle.putSerializable("notiflist", townItems);
                notifFrag.setArguments(bundle);
                if (newNotif && newNotifCount > 0) {
                    notifCount.setVisibility(View.VISIBLE);
                    Log.e(TAG, "newNotifCount: " + newNotifCount);
                    if (newNotifCount <= 9)
                        notifCount.setText("" + newNotifCount);
                    else
                        notifCount.setText("9+");
                }

//                mAdapter = new OfflineNotificationAdapter(townItems);
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
//                eventRecycler.setLayoutManager(mLayoutManager);
//
//                // eventRecycler.setAdapter(mAdapter);
//                eventRecycler.setNestedScrollingEnabled(false);
//
//                int resId = R.anim.layout_animation_fall_down;
//                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(MainActivity.this, resId);
//                eventRecycler.setLayoutAnimation(animation);
//                eventRecycler.setAdapter(mAdapter);
                //  mAdapter.getActualAdapter().notifyDataSetChanged();

                //  imgNoRecord.setVisibility(View.GONE);
                // tvTemp.setVisibility(View.GONE);
                // rvTownList.setVisibility(View.VISIBLE);

            } else
                notifCount.setVisibility(View.INVISIBLE);

//          else {
//
//                Toast.makeText(MainActivity.this, "No notifications available!", Toast.LENGTH_SHORT).show();
//                closeNotifications();
//                //  imgNoRecord.setVisibility(View.VISIBLE);
//                //  tvTemp.setVisibility(View.VISIBLE);
//                //   rvTownList.setVisibility(View.GONE);
//            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyAsynchTask extends AsyncTask<Integer, Void, Integer> {

        ProgressBar pbRefreshing;
        ImageView imgRefresh;

        private MyAsynchTask(ProgressBar pbRefreshing, ImageView imgRefresh) {

            this.pbRefreshing = pbRefreshing;
            this.imgRefresh = imgRefresh;
        }

        protected void onPreExecute() {
            pbRefreshing.setVisibility(View.VISIBLE);
            imgRefresh.setVisibility(View.GONE);

            utilityClass.refreshDataBase();
        }

        @Override
        protected Integer doInBackground(Integer... voids) {

            return 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            pbRefreshing.setVisibility(View.GONE);
            imgRefresh.setVisibility(View.VISIBLE);

            new PingServer(internet -> {
                /* do something with boolean response */
                if (!internet) {
                    Toast.makeText(MainActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                } else {
                    if (utilityClass.isInternetConnected()) {

                        getEmpLeaderBoard();
                        getEmpKraByDate();
                        getEmpPrimarySaleByDate();
                        getEmpSecondarySaleByDate();

                    }
                }

            });


        }
    }

    private class ViewHolder {

        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;

        ViewHolder() {
            mDuoDrawerLayout = findViewById(R.id.drawer);
            mDuoMenuView = mDuoDrawerLayout.findViewById(R.id.menuDuo);
        }
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<Fragment> fragmentsList = new ArrayList<>();
        private final ArrayList<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentsList.add(fragment);
            fragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE; // Forces ViewPager to refresh fragments when `notifyDataSetChanged()` is called
        }
    }


}


