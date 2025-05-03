package com.newsalesbeatApp.activities;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.services.DownloadDataService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.MockLocationChecker;
import com.newsalesbeatApp.utilityclass.SBApplication;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;

import io.sentry.ISpan;
import io.sentry.Sentry;
//import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.android.core.SentryAndroid;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;

/*
 * Created by MTC on 17-07-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.Q)
public class SplashScreen extends AppCompatActivity implements NetworkChangeInterface {

    final int MY_PERMISSIONS_REQUEST_CALL = 1208;
    SharedPreferences prefSFA, tempSfa;
    boolean isAllPermissionGranted = false;
    UtilityClass utilityClassObj;
    SalesBeatDb salesBeatDb;
    RequestQueue requestQueue;
    Context context;
    View view;
    //Permission required at run time
    String[] permissionsRequired = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
    };

    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    FirebaseAnalytics mFirebaseAnalytics;
    private final String TAG = "SplashScreen";

    protected void onCreate(Bundle bundle2) {
        super.onCreate(bundle2);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);


        context = SBApplication.getInstance();
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        // Obtain the FirebaseAnalytics instance.
        requestQueue = Volley.newRequestQueue(context);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        utilityClassObj = new UtilityClass(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        salesBeatDb = SalesBeatDb.getHelper(this);
        receiver.InitNetworkListener(this);

        SentryAndroid.init(this, options -> {
            // Set your DSN here
            options.setDsn(SBApplication.SENTRY_DNS);
            options.setEnablePerformanceV2(true);
            options.setEnableAutoSessionTracking(true);
//            options.setSessionTrackingIntervalMillis(60000);

            options.setTracesSampleRate(1.0);

            // Set custom traces sampler
            options.setTracesSampler(
                    context -> {
                        Log.d("SentryInit", "Checking Sentry Initialization");
                        return 1.0; // return 100% sampling rate
                    });
        });


        SbLog.recordScreen("SplashScreen");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");

        ISpan span = Sentry.getSpan();
        if (span != null) {
            span.finish();
        }

        //Check permission granted or not...............................................
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
//                && (ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[1]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[2]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[3]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[4]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[5]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[6]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[7]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[8]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[9]) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequiredForAndroidQ[10]) != PackageManager.PERMISSION_GRANTED)) {
//
//            ActivityCompat.requestPermissions(this,permissionsRequiredForAndroidQ, MY_PERMISSIONS_REQUEST_CALL);
//
//        }
        if (ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[5]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[6]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[7]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[8]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[9]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[10]) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(permissionsRequired, MY_PERMISSIONS_REQUEST_CALL);

        } else {

            initialCheck();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        try {
           /* MockLocationChecker mockLocationChecker = new MockLocationChecker(SplashScreen.this);

            if (mockLocationChecker.isMockLocationEnabled()) {
                Log.d("TAG", "Check Mock-1");
//                UtilityClass utils = new UtilityClass(SplashScreen.this);
                utils.toastError(view,"Mock location is turned ON, you must turn it OFF to continue using this app.");
                return;
            }

            if (mockLocationChecker.isAnyLocationMock()) {
                Log.d("TAG", "Check Mock-2");
                utils.toastError(view,"Mock location is turned ON, you must turn it OFF to continue using this app.");
                return;
            }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(receiver, intentFilter);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int grantResult : grantResults) {

            isAllPermissionGranted = grantResult == PackageManager.PERMISSION_GRANTED;
        }

        initialCheck();
    }

    private void initialCheck() {

        Log.e(TAG, "initialCheck " + new Date());

        String status = prefSFA.getString("userStatus", "");
        Log.e(TAG, "initialCheck status: " + status);

        if (status.equalsIgnoreCase("disableEmployee")) {

            showUserInactiveDialog();

        } else if (status.equalsIgnoreCase("logout")) {

            logInUser();

        } else if (status.equalsIgnoreCase("fullReset")) {

            utilityClassObj.refreshDataBase();

            logInUser();

        } else if (status.equalsIgnoreCase("clearData")) {

            utilityClassObj.initailizeAndResetPrefAndDatabase();
            redirectPin();
        } else {

            if (prefSFA.getBoolean(getString(R.string.is_logged_in), false)) {
                //start service
                startServiceToDownloadData();

                redirectPin();

            } else {

                logInUser();
            }
        }

    }

    private void startServiceToDownloadData() {

        if (utilityClassObj != null && utilityClassObj.isInternetConnected()) {

            //start service to download data
            Intent startIntent = new Intent(SplashScreen.this, DownloadDataService.class);
            startIntent.putExtra("appVersion", utilityClassObj.getAppVersion());
            startService(startIntent);
        }
    }

    private void redirectPin() {

        Intent intent = new Intent(SplashScreen.this, PinSetup.class);
        intent.putExtra("pinFrom", "splashscreen");
        startActivity(intent);
        finish();
    }

    private void logInUser() {

        Log.e(TAG, "logInUser " + new Date());

        SharedPreferences.Editor editor = prefSFA.edit();
        editor.clear();
        editor.apply();

        SharedPreferences.Editor tempEditor = tempSfa.edit();
        tempEditor.clear();
        tempEditor.apply();


        if (utilityClassObj.isInternetConnected()) {
            //new MyAsynTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //@Umesh 20220915
            GetCompanyInfo();
        } else
            Toast.makeText(this, getString(R.string.internetUnavailableMsg), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private void showNotificationDialog() {

        final Dialog mDialog = new Dialog(SplashScreen.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.notification_dialog);
        if (mDialog.getWindow() != null)
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(false);
        TextView tvNotTitle = mDialog.findViewById(R.id.tvNotTitle);
        TextView tvNotificationTitle = mDialog.findViewById(R.id.tvNotificationTitle);
        TextView tvNotificationBody = mDialog.findViewById(R.id.tvNotificationBody);
        Button btnOk = mDialog.findViewById(R.id.btnNotiOk);

        tvNotTitle.setText("Alert!!");
        tvNotificationTitle.setText("API deprecated.");
        tvNotificationBody.setText("Kindly update your app or contact admin!");

        btnOk.setOnClickListener(view -> {
            mDialog.dismiss();
            finishAffinity();
        });

        mDialog.show();
    }

    @Override
    public void connectionChange(boolean status) {

        if (status) {
            if (ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[3]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[4]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[5]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[6]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[7]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[8]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[9]) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(SplashScreen.this, permissionsRequired[10]) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(permissionsRequired, MY_PERMISSIONS_REQUEST_CALL);

            } else {
                initialCheck();
            }
        }

    }

    private void downloadCampaign(String cmny_id, String cmny_name, String cmny_logo_url) {

        new DownloadImage(cmny_id, cmny_name, cmny_logo_url, "company_logo").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showUserInactiveDialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreen.this);
        alertDialog.setTitle("Alert!");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("You are deactivated by admin.Please Login again or contact to admin");
        alertDialog.setPositiveButton("Login", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            logInUser();
        });

        Dialog dialog = alertDialog.create();
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class MyAsynTask2 extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            return "";
        }

        protected void onPostExecute(String result) {


        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadImage extends AsyncTask<Void, Void, String> {

        String cmnyid, cmny_name, url, imageName;

        DownloadImage(String cmny_id, String cmny_name, String url, String imageName) {

            this.cmnyid = cmny_id;
            this.cmny_name = cmny_name;
            this.url = url;
            this.imageName = imageName;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {


                String PATH = Environment.getExternalStorageDirectory() + "/CMNY/";
                File folder = new File(PATH);

                if (!folder.exists()) {
                    folder.mkdir();
                }

                return folder.getPath() + "/" + imageName;


            } catch (Exception e) {

                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                File direct = new File(Environment.getExternalStorageDirectory() + "/CMNY/");
                if (direct.exists() && direct.isDirectory()) {
                    String[] children = direct.list();
                    assert children != null;
                    for (int i = 0; i < children.length; i++) {
                        new File(direct, children[i]).delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                File direct1 = new File(Environment.getExternalStorageDirectory() + "/CMNY/");
                if (!direct1.exists()) {
                    @SuppressLint("SdCardPath") File wallpaperDirectory = new File("/sdcard/CMNY/");
                    wallpaperDirectory.mkdirs();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String filepath) {
            super.onPostExecute(filepath);

            Log.e("SplashScreen", "==" + filepath);
//            if (!filepath.isEmpty()) {

            SharedPreferences.Editor editor = prefSFA.edit();
            editor.putString(getString(R.string.company_id_key), cmnyid);
            editor.putString(getString(R.string.company_name_key), cmny_name);
            editor.putString(getString(R.string.company_logourl_key), filepath);
            editor.apply();

            Bundle bundle = new Bundle();
            bundle.putString("Activity", "SplashScreen");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
            startActivity(intent);
            //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            finish();
//            }
        }
    }


    public void GetCompanyInfo() {
        JSONObject initDetails = new JSONObject();

        try {

            initDetails.put("auth", getString(R.string.apikey));
            initDetails.put("cid", getString(R.string.cmny_id));

            //@umesh 01-Feb-2022
            initDetails.put("username", "xyz");
            initDetails.put("password", "xyz");

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_GET_CMNY_INFO, initDetails, response -> {
            Log.e(TAG, "SplashScreen: " + response);

            try {
                //@umesh 01-Feb-2022
                int Status = response.getInt("status");
                String Msg = response.getString("message");
                if (Status == 1) {
                    JSONObject company = response.getJSONObject("data");
                    String cmny_id = company.getString("cid");
                    String cmny_name = company.getString("name");
                    String cmny_logo_url = company.getString("logo");
                    downloadCampaign(cmny_id, cmny_name, cmny_logo_url);
                } else {
                    Log.e(TAG, "Error-1: " + Msg);
                    Toast.makeText(SplashScreen.this, Msg, Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e(TAG, "Error-2: " + e.getMessage());
                Toast.makeText(SplashScreen.this, "API_GET_CMNY_INFO1:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }, volleyError ->{}
//                Toast.makeText(SplashScreen.this, "API_GET_CMNY_INFO2:" + volleyError.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Log.e(TAG, "GetCompanyInfo: " + stringRequest);
        Volley.newRequestQueue(SplashScreen.this).add(stringRequest);
    }
}
