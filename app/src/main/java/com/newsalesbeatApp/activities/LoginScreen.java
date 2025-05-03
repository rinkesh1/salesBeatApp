package com.newsalesbeatApp.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.newsalesbeatApp.BuildConfig;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.FcmTokenManager;
import com.newsalesbeatApp.receivers.ConnectivityChangeReceiver;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.services.IsActiveService;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.Config;
import com.newsalesbeatApp.utilityclass.SBApplication;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.newsalesbeatApp.receivers.ConnectivityChangeReceiver.IS_NETWORK_AVAILABLE;

import io.sentry.Attachment;
import io.sentry.Sentry;

/*
 * Created by MTC on 17-07-2017.
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener {

    String TAG = "LoginScreen";
    Button btnLogin;
    EditText inputUsername, inputPassword;
    ImageView cmnyLogo;
    TextView tvCmnyName;
    SharedPreferences prefSFA, tempSfa;
    int MY_SOCKET_TIMEOUT_MS = 50000;
    IntentFilter intentFilter;
    NetworkChangeReceiver receiver;
    private UtilityClass utilityClassObj;
    private SalesBeatDb salesBeatDb;

    Context context;
    RequestQueue requestQueue;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_screen);

        /*try {
            FirebaseApp.initializeApp(this);
            Log.d("FirebaseInit", "Firebase initialized successfully.");
        } catch (Exception e) {
            Log.e("FirebaseInit", "Firebase initialization failed.", e);
        }
*/
        /*if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:292187759442:android:56dbc7cc5fa95edb95508b") // mobilesdk_app_id
                    .setApiKey("AIzaSyAcFIbgmm8Q1wqCOs1PnPyV-neUhyQD41M") // current_key
                    .setProjectId("292187759442") // Extracted from client_id
                    .build();

            FirebaseApp.initializeApp(this, options);
        }*/

        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        inputUsername = findViewById(R.id.inputUsername);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        cmnyLogo = findViewById(R.id.cmnyLogo);
        tvCmnyName = findViewById(R.id.tvCmnyName);
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        utilityClassObj = new UtilityClass(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        salesBeatDb = SalesBeatDb.getHelper(this);

        String cmnyName = prefSFA.getString("cmny_name", "");
        String cmnyLogoUrl = prefSFA.getString("logo_url", "");

        tvCmnyName.setText(cmnyName);

        cmnyLogo.setImageURI(Uri.parse(cmnyLogoUrl));

        btnLogin.setOnClickListener(this);

        context = SBApplication.getInstance();
        requestQueue =  Volley.newRequestQueue(context);

        if (!prefSFA.getString("username", "").isEmpty() && !prefSFA.getString("password", "").isEmpty()) {

            inputUsername.setText(prefSFA.getString("username", ""));
            inputPassword.setText(prefSFA.getString("password", ""));
        }

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (utilityClassObj.isInternetConnected())
                        logInUser();
                    else
                        Toast.makeText(LoginScreen.this, getString(R.string.internetUnavailableMsg), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

//        FirebaseApp.initializeApp(this);
        Log.e(TAG, "LoginScreen");

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    FcmTokenManager.setFcmToken(token);
                });

        IntentFilter intentFilter1 = new IntentFilter(ConnectivityChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                Log.e(TAG, "LoginScreen Receiver");
                Toast.makeText(context, "Network Status: " + networkStatus, Toast.LENGTH_SHORT).show();
//                Snackbar.make(findViewById(R.id.activity_main), "Network Status: " + networkStatus, Snackbar.LENGTH_LONG).show();
            }
        }, intentFilter1);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
                registerReceiver(receiver, intentFilter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                registerReceiver(receiver, intentFilter);
            }
//            registerReceiver(receiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();
        try {

            unregisterReceiver(receiver);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnLogin) {
            if (utilityClassObj.isInternetConnected()) {
                logInUser();
            } else {
                Toast.makeText(this, getString(R.string.internetUnavailableMsg), Toast.LENGTH_SHORT).show();
            }
        }


//        switch (view.getId()) {
//            case R.id.btnLogin:
//                if (utilityClassObj.isInternetConnected())
//                    logInUser();
//                else
//                    Toast.makeText(this, getString(R.string.internetUnavailableMsg), Toast.LENGTH_SHORT).show();
//                break;
//            default:
//                break;
//        }
    }
    @Override
    public void onBackPressed() {
        LoginScreen.this.finishAffinity();
    }
    @SuppressLint({"MissingPermission", "HardwareIds"})
    private void logInUser() {

//        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
//        loader.setContentView(R.layout.loader);
//        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        if (!inputUsername.getText().toString().isEmpty() && !inputPassword.getText().toString().isEmpty()) {
            //mView.show(getSupportFragmentManager(), "");
            //loader.show();
            enableDisableView("Loading...", false);
            JSONObject orderrrr = new JSONObject();
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                Log.e("FIREBASE", "Firebase reg id: " + regId);
                Log.d(TAG, "logInUser FCM Token: "+FcmTokenManager.getFcmToken());

                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                orderrrr.put("auth", getString(R.string.apikey));
                orderrrr.put("cid", Integer.parseInt(prefSFA.getString("cmny_id",""))); //@Umesh
                orderrrr.put("username", inputUsername.getText().toString());
                orderrrr.put("password", inputPassword.getText().toString());
                orderrrr.put("ismobileuser", true);
                orderrrr.put("app_version",  BuildConfig.VERSION_NAME);
                //orderrrr.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
                orderrrr.put("os_version", Build.VERSION.RELEASE);
                orderrrr.put("fcmtoken", FcmTokenManager.getFcmToken());
                orderrrr.put("model", Build.BRAND + " " + Build.MODEL);
                try {

                    if (manager != null)
                        orderrrr.put("imei", manager.getDeviceId());

                } catch (SecurityException e) {
                    orderrrr.put("imei", "restricted in Q");
                }
                orderrrr.put("token", regId);

                Log.e("AUTH", "====" + orderrrr.toString());
                pref.edit().putString(getString(R.string.login_json), orderrrr.toString()).apply();

                Log.d("Login json", pref.getString(getString(R.string.login_json), ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TAG", "logInUser Request: "+new Gson().toJson(orderrrr));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_USER_LOG_IN,
                    orderrrr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response)
                {
                    Log.e(TAG, "User Login page===" + response);

                    try {
                        if(response.getInt("status")==1)
                        {
                            JSONObject data = response.getJSONObject("data");

                            JSONObject employee = data.getJSONObject("emp");
                            String cmny_id = employee.getString("cid");
                            String emp_id = employee.getString("eid");
                            String emp_name = employee.getString("name");
                            String username = employee.getString("username");
                            String emp_ph_no = employee.getString("phone1");
                            String emp_email = employee.getString("email1");
                            String headquarter = employee.getString("headquarter");
                            String zone = employee.getString("zone");
                            String zoneid = employee.getString("zoneid");
                            String state = employee.getString("state");
                            //String report_to = employee.getString("reportingTo");
                            String report_to = employee.getString("reportingToEuid"); //@Umesh
                            String designation = employee.getString("designation");
                            String emp_photo_url = employee.getString("profilePic");
                            if (emp_photo_url == null || emp_photo_url.isEmpty() || emp_photo_url.equalsIgnoreCase("null"))
                                emp_photo_url = SbAppConstants.PLACEHOLDER_URL;

                            //String token = response.getString("token");
//                            String token = employee.getString("fcmToken");//@Umesh

                            JSONObject authtoken = data.getJSONObject("authtoken");
                            Log.d(TAG, "json Token: "+new Gson().toJson(authtoken));
                            String TokenValidTo=authtoken.getString("expiration");
                            String token =authtoken.getString("token");
                            SharedPreferences.Editor Teditor = prefSFA.edit();
                            Teditor.putString("token", token);
                            Teditor.putString("TokenValidTo", TokenValidTo);
                            Teditor.apply();

                            startService(new Intent(getBaseContext(), IsActiveService.class));
                            //startService(new Intent(getBaseContext(), DownloadDataService.class));

                            if (cmny_id.equalsIgnoreCase(prefSFA.getString("cmny_id", "")))//@Umesh
                            {
                                new DownloadImage(emp_id, emp_name, username, emp_ph_no, emp_email, headquarter, zone, zoneid,
                                        state, report_to, designation, emp_photo_url, token)
                                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {

                                enableDisableView("LogIn", true);
                                Toast.makeText(LoginScreen.this, response.getString(",message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Sentry.captureMessage(response.getString("message")); //@Umesh
                            showErrorAlertMsg(response.getString("message"), true);
                        }
                    }
                    catch (Exception ex)
                    {
                        showErrorAlertMsg(ex.getMessage(), true);
                        enableDisableView("LogIn", true);
                        Toast.makeText(LoginScreen.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                        SbLog.printException("LoginScreen", "employeeLogin", ex.getMessage(), "0");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //loader.dismiss();
                    enableDisableView("LogIn", true);
                    try {

                        SbLog.printError(TAG, "employeeLogin", String.valueOf(error.networkResponse.statusCode), error.getMessage(), "");
                        String responseError = new String(error.networkResponse.data, "utf-8");
                        showErrorAlertMsg(responseError, false);

                    } catch (Exception e) {
                        e.printStackTrace();
                        SbLog.printException("LoginScreen", "employeeLogin", e.getMessage(), "0");
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    MY_SOCKET_TIMEOUT_MS,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(LoginScreen.this).add(jsonObjectRequest);

        } else {
            Toast.makeText(this, getString(R.string.inputUsernamePasswordMsg), Toast.LENGTH_SHORT).show();
        }

    }

    private void enableDisableView(String text, boolean flag) {

        btnLogin.setText(text);
        btnLogin.setClickable(flag);
        inputUsername.setFocusable(flag);
        inputPassword.setFocusable(flag);
        inputUsername.setFocusableInTouchMode(flag);
        inputPassword.setFocusableInTouchMode(flag);
    }


    private void showErrorAlertMsg(String responseBody, boolean error200) {
        String message = "";

        if (!error200) {
            try {
                JSONObject object = new JSONObject(responseBody);
                Log.e("LogInError", "===>>" + object.toString());
                if (!object.isNull("statusMessage") && object.has("statusMessage"))
                    message = object.getString("statusMessage");

                if (!object.isNull("message") && object.has("message"))
                    message = object.getString("message");

                JSONObject errorr = null;
                if (!object.isNull("errorr") && object.has("errorr")) {
                    errorr = object.getJSONObject("errors");
                    message = message.concat("\n");
                    message = message.concat(errorr.toString());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            message = responseBody;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginScreen.this);
        dialog.setTitle("Message!");
        dialog.setMessage(message);

        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                enableDisableView("LogIn", true);
            }
        });

        Dialog dialog1 = dialog.create();
        dialog1.show();

    }

    private class DownloadImage extends AsyncTask<Void, Void, String> {

        //Dialog loader;

        String emp_id, emp_name, username, emp_ph_no, emp_email, headquarter, zone, zoneid, state, report_to,
                designation, emp_photo_url, token;

        public DownloadImage(String emp_id, String emp_name, String username, String emp_ph_no, String emp_email,
                             String headquarter, String zone, String zoneid, String state, String report_to, String
                                     designation, String emp_photo_url, String token) {

            this.emp_id = emp_id;
            this.emp_name = emp_name;
            this.username = username;
            this.emp_ph_no = emp_ph_no;
            this.emp_email = emp_email;
            this.headquarter = headquarter;
            this.zone = zone;
            this.zoneid = zoneid;
            this.state = state;
            this.report_to = report_to;
            this.designation = designation;
            this.emp_photo_url = emp_photo_url;
            this.token = token;

        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                String PATH = Environment.getExternalStorageDirectory() + "/CMNY/";
                File folder = new File(PATH);

                if (!folder.exists()) {
                    folder.mkdir();
                }

                URL downloadURL = new URL(emp_photo_url);
                HttpURLConnection conn = (HttpURLConnection) downloadURL
                        .openConnection();
                int responseCode = conn.getResponseCode();

                Log.e(TAG, " ====>>>" + responseCode);

                //if (responseCode != 200)
                // throw new Exception("Error in connection");
                InputStream is = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(PATH + emp_id);
                byte buffer[] = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteCount);
                }
                os.close();
                is.close();

                return folder.getPath() + "/" + emp_id;

            } catch (Exception e) {
                //SbLog.printException("LoginScreen", "employeeLogin", e.getMessage(), "0");
                e.printStackTrace();
            }

            //return "";
            return SbAppConstants.IMAGE_PREFIX + emp_photo_url; //@Umesh
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            loader= new Dialog(LoginScreen.this, R.style.DialogActivityTheme);
//            loader.setContentView(R.layout.loader);
//            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            loader.show();
        }


        @Override
        protected void onPostExecute(String filepath) {
            super.onPostExecute(filepath);

            Log.e("LoginScreen", "==" + filepath);
            //loader.dismiss();

            //@Umesh 18-08-2022
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdff =
                    new SimpleDateFormat("yyyy-MM-dd");
            final String date = sdff.format(Calendar.getInstance().getTime());
            getEmployeeRecordByDate(date);

            enableDisableView("LogIn", true);

            if (!filepath.isEmpty()) {

                SharedPreferences.Editor editor = prefSFA.edit();
                editor.putString(getString(R.string.emp_id_key), emp_id);
                editor.putString(getString(R.string.emp_name_key), emp_name);
                editor.putString(getString(R.string.emp_phoneno_key), emp_ph_no);
                editor.putString(getString(R.string.emp_emailid_key), emp_email);
                editor.putString("username", inputUsername.getText().toString());
                editor.putString("password", inputPassword.getText().toString());
                editor.putString(getString(R.string.zone_key), zone);
                editor.putString(getString(R.string.zone_id_key), zoneid);
                editor.putString(getString(R.string.state_key), state);
                editor.putString(getString(R.string.emp_headq_key), headquarter);
                editor.putString(getString(R.string.emp_reportingto_key), report_to);
                editor.putString(getString(R.string.emp_designation_key), designation);
                editor.putString(getString(R.string.emp_pic_url_key), filepath);
                editor.putString("token", "Bearer " + token);
                editor.apply();

                salesBeatDb.insertEmployeeRecord(emp_id, "1", emp_name, emp_ph_no, emp_email,
                        inputUsername.getText().toString(), inputPassword.getText().toString(),
                        zone, zoneid, state, headquarter, report_to, designation, filepath,
                        "Bearer " + token);

                Intent intent = new Intent(LoginScreen.this, PinSetup.class);
                intent.putExtra("fromLogin", true);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();

            } else {

                SharedPreferences.Editor editor = prefSFA.edit();
                editor.putString(getString(R.string.emp_id_key), emp_id);
                editor.putString(getString(R.string.emp_name_key), emp_name);
                editor.putString(getString(R.string.emp_phoneno_key), emp_ph_no);
                editor.putString(getString(R.string.emp_emailid_key), emp_email);
                editor.putString("username", inputUsername.getText().toString());
                editor.putString("password", inputPassword.getText().toString());
                editor.putString(getString(R.string.zone_key), zone);
                editor.putString(getString(R.string.zone_id_key), zoneid);
                editor.putString(getString(R.string.state_key), state);
                editor.putString(getString(R.string.emp_headq_key), headquarter);
                editor.putString(getString(R.string.emp_reportingto_key), report_to);
                editor.putString(getString(R.string.emp_designation_key), designation);
                editor.putString(getString(R.string.emp_pic_url_key), "");
                editor.putString("token", "Bearer " + token);
                editor.apply();

                salesBeatDb.insertEmployeeRecord(emp_id, "1", emp_name, emp_ph_no, emp_email,
                        inputUsername.getText().toString(), inputPassword.getText().toString(),
                        zone, zoneid, state, headquarter, report_to, designation, filepath,
                        "Bearer " + token);

                Intent intent = new Intent(LoginScreen.this, PinSetup.class);
                intent.putExtra("fromLogin", true);
                startActivity(intent);
                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();

                Toast.makeText(LoginScreen.this, "Oops!missing profile image", Toast.LENGTH_SHORT).show();
            }
        }

        public void getEmployeeRecordByDate(final String date) {
            Log.e("TAG", "login Date: "+date);
            JsonObjectRequest empRecordRequst = new JsonObjectRequest(Request.Method.GET,
                    //SbAppConstants.GET_EMP_RECORD_BY_DATE+date,
                    SbAppConstants.API_GET_EMP_RECORD_BY_DATE + "?date=" + date,
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        //JSONObject response = new JSONObject(res.body().toString());

                        Log.e(TAG, "Response EmployeeRecordByDate: " + response);

                        //@Umesh 02-Feb-2022
                        if (response.getInt("status") == 1) {
                            response = response.getJSONObject("data");

                            String checkInTime = "", checkOutTime = "", status = "", workingTown = ""; //@Umesh 14-06-2022

                            if (response.has("attendance") && !response.isNull("attendance"))
                                status = response.getString("attendance");

                            if (response.has("checkIn") && !response.isNull("checkIn"))
                                checkInTime = response.getString("checkIn");

                            if (response.has("checkOut") && !response.isNull("checkOut"))
                                checkOutTime = response.getString("checkOut");

                            //@Umesh 14-06-2022
                            if (response.has("workingTown") && !response.isNull("workingTown"))
                                workingTown = response.getString("workingTown");


                            String closingStartDate = "", closingEndDate = "", activityType = "";

                            if (!response.isNull("activityType") && response.has("activityType")) {

                                activityType = response.getString("activityType").replace("\\[", "");
                                activityType = activityType.replace("\\]", "");
                            }

                            if (!response.isNull("closingStartDate") && response.has("closingStartDate"))
                                closingStartDate = response.getString("closingStartDate");
                            if (!response.isNull("closingEndDate") && response.has("closingEndDate"))
                                closingEndDate = response.getString("closingEndDate");


                            if (status.toLowerCase(Locale.ROOT).equalsIgnoreCase("leave")) {
                                SharedPreferences.Editor editor = tempSfa.edit();
                                editor.putString(getString(R.string.attendance_key), status);
                                editor.apply();
                            } else if (status.toLowerCase(Locale.ROOT).equalsIgnoreCase("present")
                                    && !checkInTime.isEmpty()
                                    && checkOutTime.isEmpty()) {

                                Log.d(TAG, "get Check In Time-1: "+checkInTime);

                                String[] temp = checkInTime.split(" ");
                                String[] time = temp[0].split(":");
                                String frt = temp[1];

                                String chkIn = "";
                                if (frt.equalsIgnoreCase("pm")) {

                                    int h = Integer.parseInt(time[0]) - 12;
                                    int m = Integer.parseInt(time[1]);

                                    chkIn = h + ":" + m + ":" + "00" + " " + frt;
                                    Log.d(TAG, "chkIn 1: "+chkIn);

                                } else {

                                    int h = Integer.parseInt(time[0]);
                                    int m = Integer.parseInt(time[1]);

                                    chkIn = h + ":" + m + ":" + "00" + " " + frt;
                                }
                                Log.d(TAG, "get Check In Time: "+chkIn);

                                SharedPreferences.Editor editor = tempSfa.edit();
                                editor.putString(getString(R.string.attendance_key), "present");
//                                editor.putString(getString(R.string.attendance_key), status);
                                editor.putString(getString(R.string.check_in_time_key), chkIn);
                                editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                editor.putString("workingTown", workingTown); //@Umesh 14-06-2022

                                if (closingStartDate != null && !closingStartDate.isEmpty()
                                        && closingStartDate.equalsIgnoreCase("null")

                                ) {
                                    editor.putString(getString(R.string.closing_start_date_key), closingStartDate);
                                    editor.putString(getString(R.string.closing_end_date_key), closingEndDate);
                                } else {
                                    Calendar cal = Calendar.getInstance();
                                    int sMonth = cal.get(Calendar.MONTH) + 1;
                                    int sYear = cal.get(Calendar.YEAR);

                                    int eMonth = 0, eYear = 0;
                                    if (sMonth == 12) {

                                        eMonth = 1;
                                        eYear = sYear + 1;

                                    } else {

                                        eMonth = sMonth + 1;
                                        eYear = sYear;
                                    }

                                    if (sMonth < 10)
                                        editor.putString(getString(R.string.closing_start_date_key), sYear + "-0" + sMonth + "-28");
                                    else
                                        editor.putString(getString(R.string.closing_start_date_key), sYear + "-" + sMonth + "-28");

                                    if (eMonth < 10)
                                        editor.putString(getString(R.string.closing_end_date_key), eYear + "-0" + eMonth + "-05");
                                    else
                                        editor.putString(getString(R.string.closing_end_date_key), eYear + "-" + eMonth + "-05");
                                }

                                if (!activityType.isEmpty()) {
                                    editor.putString(getString(R.string.act_type_key), activityType);
                                }
                                editor.apply();


                            } else if (status.toLowerCase(Locale.ROOT).equalsIgnoreCase("present")
                                    && !checkInTime.isEmpty()
                                    && !checkOutTime.isEmpty()) {

                                Log.d(TAG, "get Check In Time2: "+checkInTime);
                                salesBeatDb.deleteUserAttendance();

                                SharedPreferences.Editor editor = tempSfa.edit();
                                editor.putString(getString(R.string.attendance_key), "checkOut");
                                editor.putString(getString(R.string.check_in_time_key), checkInTime);
                                editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                long mills = 0;

                                try {
                                    final SimpleDateFormat format = new SimpleDateFormat("HH:mm ");
                                    final Date Date1;
                                    Date1 = format.parse(checkInTime);
                                    //String temp = format.format(Calendar.getInstance().getTime());
                                    Date Date2 = format.parse(checkOutTime);
                                    mills = Date2.getTime() - Date1.getTime();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                editor.putString(getString(R.string.working_time_key), getFormattedTime(mills));

                                if (closingStartDate != null && !closingStartDate.isEmpty()
                                        && closingStartDate.equalsIgnoreCase("null")

                                ) {
                                    editor.putString(getString(R.string.closing_start_date_key), closingStartDate);
                                    editor.putString(getString(R.string.closing_end_date_key), closingEndDate);
                                } else {
                                    Calendar cal = Calendar.getInstance();
                                    int sMonth = cal.get(Calendar.MONTH) + 1;
                                    int sYear = cal.get(Calendar.YEAR);

                                    int eMonth = 0, eYear = 0;
                                    if (sMonth == 12) {

                                        eMonth = 1;
                                        eYear = sYear + 1;

                                    } else {

                                        eMonth = sMonth + 1;
                                        eYear = sYear;
                                    }

                                    if (sMonth < 10)
                                        editor.putString(getString(R.string.closing_start_date_key), sYear + "-0" + sMonth + "-28");
                                    else
                                        editor.putString(getString(R.string.closing_start_date_key), sYear + "-" + sMonth + "-28");

                                    if (eMonth < 10)
                                        editor.putString(getString(R.string.closing_end_date_key), eYear + "-0" + eMonth + "-05");
                                    else
                                        editor.putString(getString(R.string.closing_end_date_key), eYear + "-" + eMonth + "-05");
                                }

                                if (!activityType.isEmpty()) {
                                    editor.putString(getString(R.string.act_type_key), activityType);
                                }

                                editor.apply();

                            }
                        } else {
                            //loader.dismiss();
                            Toast.makeText(LoginScreen.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    serverCall.handleError2(error.networkResponse.statusCode, TAG,
//                            error.getMessage(), "employee-reports/date/");
//                    Sentry.capture(error.getMessage());
                    Attachment attachment = new Attachment(SbAppConstants.API_GET_EMP_RECORD_BY_DATE);
                    Sentry.configureScope(scope -> scope.addAttachment(attachment));
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("authorization", prefSFA.getString("token", ""));
                    return headers;
                }
            };

            setRetryPolicyToRequests(empRecordRequst);
            addToRequestQueue(empRecordRequst);

        }

        @SuppressLint("DefaultLocale")
        public String getFormattedTime(long mills) {
            final String FORMAT = "%02d:%02d:%02d";
            return String.format(FORMAT,
                    TimeUnit.MILLISECONDS.toHours(mills),
                    TimeUnit.MILLISECONDS.toMinutes(mills) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(mills)),
                    TimeUnit.MILLISECONDS.toSeconds(mills) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(mills)));

        }

        public void setRetryPolicyToRequests(JsonObjectRequest jsonObjectRequest) {
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        public void addToRequestQueue(JsonObjectRequest jsonObjectRequest) {
            requestQueue.add(jsonObjectRequest);
            requestQueue.addRequestEventListener(new RequestQueue.RequestEventListener() {
                @Override
                public void onRequestEvent(Request<?> request, int event) {
//                    Log.e(TAG, "MSG==>" + request.hasHadResponseDelivered() + " " + event);
                }
            });
        }
    }
}
