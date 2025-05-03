package com.newsalesbeatApp.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.newsalesbeatApp.BuildConfig;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.Config;
import com.newsalesbeatApp.utilityclass.SBApplication;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.TransactionOptions;

//import com.loopj.android.http.RequestParams;
//import com.loopj.android.http.SyncHttpClient;
//import com.loopj.android.http.TextHttpResponseHandler;

public class DownloadDataService
        extends IntentService implements NetworkChangeInterface {

    String TAG = "DownloadDataService";
    Calendar calendar;
    String month = "", year = "";
    UtilityClass utilityClass;
    ServerCall serverCall;
    String app_version = "";
    SalesBeatDb salesBeatDb;
    Bundle bundle = null;
    String userAttendance;
    Context context;
    private SharedPreferences prefSFA, tempPref;
    private ClientInterface apiIntentface;

    private Boolean fetchEmployeeRecordByDate = false;
    private Boolean fetchDistributorTarget = false;
    private Boolean fetchEmpPrimarySale = false;
    private Boolean fetchEmpSecondarySale = false;
    private Boolean fetchCampaign = false;
    private Boolean fetchIncentive = false;
    private Boolean fetchEmpKra = false;
    private Boolean fetchLeaderboard = false;

    private String empRecordDate = "";

    RequestQueue requestQueue;

    public DownloadDataService() {

        super(DownloadDataService.class.getName());
        apiIntentface = RetrofitClient.getClient().create(ClientInterface.class);

        context = SBApplication.getInstance();
        prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        calendar = Calendar.getInstance();
        month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        year = String.valueOf(calendar.get(Calendar.YEAR));
        userAttendance = prefSFA.getString(context.getString(R.string.attendance_key), "");
        salesBeatDb = SalesBeatDb.getHelper(context);
        utilityClass = new UtilityClass(context);
        serverCall = new ServerCall(context);
        bundle = new Bundle();

        salesBeatDb.deleteAllFromTablePrimarySaleHistory();
        NetworkChangeReceiver receiver = new NetworkChangeReceiver();
        receiver.InitNetworkListener(this);
        requestQueue =  Volley.newRequestQueue(context);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleIntent(Intent intent) {

        app_version = intent.getStringExtra("appVersion");

        SharedPreferences preferences = getApplicationContext().getSharedPreferences(
                Config.SHARED_PREF, MODE_PRIVATE);

        if (prefSFA.getString("token", "").equalsIgnoreCase("")) {

            logInUser(preferences.getString(getString(R.string.login_json), ""), app_version);

        } else {

            if (userAttendance.isEmpty())
            {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdff =
                        new SimpleDateFormat("yyyy-MM-dd");
                final String date = sdff.format(Calendar.getInstance().getTime());
                SharedPreferences prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                Sentry.configureScope(scope -> {
                    scope.setTag("page_locale", "en_US");
                    scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//                    scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
                });

                TransactionOptions txOptions = new TransactionOptions();
                txOptions.setBindToScope(true);
                ITransaction transaction = Sentry.startTransaction("DownloadDataService All", "EmployeeRecordByDate",txOptions);
                try {
                    if (transaction == null) {
                        transaction = Sentry.startTransaction("processOrderBatch()", "task");
                    }
                    // span operation: task, span description: operation
//                    ISpan innerSpan = transaction.startChild("SalesBeat Check", "operation");
                    getEmployeeRecordByDate(date);

                    getLeaderboardData(prefSFA.getString("token", ""));

                    getEmpKraByDate(prefSFA.getString("token", ""));

                    getEmpPrimarySaleByDate(prefSFA.getString("token", ""));

                    getEmpSecondarySaleByDate(prefSFA.getString("token", ""));

                    getPrimarySaleHistory(utilityClass.getCurrentDateInMYformat(), prefSFA.getString("token", ""));

                    getDistributorTargetAchievement(utilityClass.getCurrentDateInMYformat(), prefSFA.getString("token", ""));

                } catch (Exception e) {
                    transaction.setThrowable(e);
                    transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                    throw e;
                } finally {
                    transaction.finish();
                }
            }

            if (!app_version.isEmpty()) {

                salesBeatDb.deletetCampaign();
                deleteCampaignFromLocal();

                //@Umesh 20220908
                //getTownListFromServer(prefSFA.getString("token", ""));
                getIncentive(prefSFA.getString("token", ""));
                getCampaign(prefSFA.getString("token", ""));
                //get current month record
                getEmployeeRecordByMonthAndYear(month, year, true, prefSFA.getString("token", ""));
            }


        }

    }

    private void getEmployeeRecordByDate(final String date) {
        Log.e("TAG", "check-2 Date: "+date);
        JsonObjectRequest empRecordRequst = new JsonObjectRequest(Request.Method.GET,
                //SbAppConstants.GET_EMP_RECORD_BY_DATE+date,
                SbAppConstants.API_GET_EMP_RECORD_BY_DATE + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    //JSONObject response = new JSONObject(res.body().toString());

                    Log.e(TAG, "Response EmployeeRecordByDate check: " + response);

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        response = response.getJSONObject("data");

                    String checkInTime = "", checkOutTime = "", status = "", workingTown=""; //@Umesh 14-06-2022

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


                    if (status.toLowerCase(Locale.ROOT).equalsIgnoreCase("leave"))
                    {
                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putString(getString(R.string.attendance_key), status);
                        editor.apply();
                    }
                   else if (status.toLowerCase(Locale.ROOT).equalsIgnoreCase("present")
                            && !checkInTime.isEmpty()
                            && checkOutTime.isEmpty()) {

                        Log.d(TAG, "onResponse check in Time: "+checkInTime);
                        String[] temp = checkInTime.split(" ");
                        String[] time = temp[0].split(":");
                        String frt = temp[1];

                        String chkIn = "";
                        if (frt.equalsIgnoreCase("pm")) {

                            int h = Integer.parseInt(time[0]) - 12;
                            int m = Integer.parseInt(time[1]);

                            chkIn = h + ":" + m + ":" + "00" + " " + frt;

                        } else {

                            int h = Integer.parseInt(time[0]);
                            int m = Integer.parseInt(time[1]);

                            chkIn = h + ":" + m + ":" + "00" + " " + frt;
                        }
                        Log.d(TAG, "get Check In Time Download: "+chkIn);
                        SharedPreferences.Editor editor = tempPref.edit();
//                        editor.putString(getString(R.string.attendance_key), status);
                        editor.putString(getString(R.string.attendance_key), "present");
                        editor.putString(getString(R.string.check_in_time_key_new), checkInTime);
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
                            && !checkOutTime.isEmpty())
                    {
                        Log.d(TAG, "get Check In Time Download1: "+checkInTime);
                        salesBeatDb.deleteUserAttendance();

                        SharedPreferences.Editor editor = tempPref.edit();
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
                    }
                    else
                    {
                        //loader.dismiss();
                        Toast.makeText(DownloadDataService.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                serverCall.handleError2(error.networkResponse.statusCode, TAG,
//                        error.getMessage(), "employee-reports/date/"); @Umesh 20221002
                Sentry.captureMessage(error.getMessage());
            }
        }){
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
    private String getFormattedTime(long mills) {
        final String FORMAT = "%02d:%02d:%02d";
        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(mills)),
                TimeUnit.MILLISECONDS.toSeconds(mills) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(mills)));

    }

    private void getDistributorTargetAchievement(String curentDateString, String token) {

//        SyncHttpClient client = new SyncHttpClient();
//        client.addHeader("authorization", token);

//        Call<JsonObject> jsonObjectCall = apiIntentface.getDistributorTargetAchievement(token, curentDateString);
//
//        if (prefSFA.getString("token", "").equalsIgnoreCase("")) {
//            Toast.makeText(context, "U r not logged in", Toast.LENGTH_SHORT).show();
//        } else {
////            client.addHeader("authorization", token);
//            jsonObjectCall = apiIntentface.getDistributorTargetAchievement(token, curentDateString);
//        }
//
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//                fetchDistributorTarget = false;
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//
//
//                    try {
//
//                        JSONObject response = new JSONObject(res.body().toString());
//                        Log.e(TAG, "Response Distributor Tar Ach: " + response);
//
//                        String sts = response.getString("status");
//                        if (sts.equalsIgnoreCase("success")) {
//
//                            salesBeatDb.deleteDisTarAch();
//
//                            JSONArray tagets = response.getJSONArray("targets");
//                            for (int i = 0; i < tagets.length(); i++) {
//                                JSONObject jsonObject = (JSONObject) tagets.get(i);
//                                String target = jsonObject.getString("target");
//                                String ach = jsonObject.getString("achievment");
//                                String did = jsonObject.getString("did");
//
//                                salesBeatDb.insertIntoDisTarAch(did, target, ach);
//                            }
//
//                        }
//                    } catch (Exception e) {
//                        //e.printStackTrace();
//                    }
//                } else {
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getMonthlyDistributorTargetAchievement");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                fetchDistributorTarget = false;
//                Log.e("Get Target", t.getMessage());
//            }
//        });
//
        JsonObjectRequest distributorTarAchRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DISTRIBUTOR_TAR_ACH + "?date=" + curentDateString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e(TAG, "Response Distributor Tar Ach: " + response);
                            //@Umesh 02-Feb-2022
                            if(response.getInt("status")==1)
                            {
                                salesBeatDb.deleteDisTarAch();
                                JSONObject data = response.getJSONObject("data");
                                JSONArray tagets = data.getJSONArray("targets");
                                for (int i = 0; i < tagets.length(); i++)
                                {
                                    JSONObject jsonObject = (JSONObject) tagets.get(i);
                                    String target = jsonObject.getString("target");
                                    //String ach = jsonObject.getString("achievment");
                                    String ach = jsonObject.getString("achieve"); //@Umesh
                                    String did = jsonObject.getString("did");
                                    salesBeatDb.insertIntoDisTarAch(did, target, ach);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Sentry.captureMessage(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(DownloadDataService.this);
//                dialog.setTitle("getMonthlyDistributorTargetAchievement!");
//                dialog.setMessage(error.getMessage());
//                dialog.show();

               /* serverCall.handleError2(error.networkResponse.statusCode, TAG,
                        error.getMessage(), "getMonthlyDistributorTargetAchievement");
                Sentry.capture(error.getMessage());*/
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(distributorTarAchRequest);
        addToRequestQueue(distributorTarAchRequest);
    }

    private void deleteCampaignFromLocal() {

        try {
            File direct = new File(Environment.getExternalStorageDirectory() + "/SalesBeat2/");
            if (direct.exists() && direct.isDirectory()) {
                String[] children = direct.list();
                for (int i = 0; i < children.length; i++) {
                    new File(direct, children[i]).delete();
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        try {

            File direct2 = new File(Environment.getExternalStorageDirectory() + "/SalesBeat2/");

            if (!direct2.exists()) {
                File wallpaperDirectory = new File("/sdcard/SalesBeat2/");
                wallpaperDirectory.mkdirs();
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }

    }

    private void getEmpPrimarySaleByDate(String token) {

        final String date = utilityClass.getMYDateFormat().format(Calendar.getInstance().getTime());

//        Call<JsonObject> jsonObjectCall = apiIntentface.getEmpPrimarySaleByDate(token, date);
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//
//
//                    try {
//
//                        JSONObject response = new JSONObject(res.body().toString());
//
//                        Log.e(TAG, "Response EmpPrimarySale: " + response);
//
//                        boolean flag = salesBeatDb.deletePrimarySale();
//
//                        //if (flag){
//
//                        String saleAchievement = "0", saleTarget = "0";
//
//                        if (!response.isNull("saleTarget") && response.has("saleTarget"))
//                            saleTarget = response.getString("saleTarget");
//                        if (!response.isNull("saleAchievement") && response.has("saleAchievement"))
//                            saleAchievement = response.getString("saleAchievement");
//
//                        String status = response.getString("status");
//
//                        if (status.equalsIgnoreCase("success")) {
//
//
//                            salesBeatDb.insertPrimarySale(saleAchievement, saleTarget);
//                        }
//
//                        //  }
//
//                    } catch (Exception e) {
//                        // e.printStackTrace();
//                    }
//                } else {
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getMonthlyPrimaryKraByDate");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("EmpPrimSaleBydate", t.getMessage());
//            }
//        });

        JsonObjectRequest empPrimarySaleByDateRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_PRIMARY_SALE_BY_DATE + "?date=" + date, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e(TAG, "Response EmpPrimarySale: " + response);

                            boolean flag = salesBeatDb.deletePrimarySale();

                            //@Umesh 02-Feb-2022
                            if(response.getInt("status")==1)
                            {
                                JSONObject data = response.getJSONObject("data");
                                String saleAchievement = "0", saleTarget = "0";

                                if (!data.isNull("saleTarget") && data.has("saleTarget"))
                                    saleTarget = data.getString("saleTarget");
                                if (!data.isNull("saleAchievement") && data.has("saleAchievement"))
                                    saleAchievement = data.getString("saleAchievement");

                                salesBeatDb.insertPrimarySale(saleAchievement, saleTarget);
                            }
                            else
                            {
                                Sentry.captureMessage(response.getString("message"));
                            }

                        } catch (Exception e) {
                             e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverCall.handleError2(error.networkResponse.statusCode, TAG,
                        error.getMessage(), "getMonthlyPrimaryKraByDate");
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(empPrimarySaleByDateRequest);
        addToRequestQueue(empPrimarySaleByDateRequest);

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getEmpSecondarySaleByDate(String token) {

        final String date = utilityClass.getMYDateFormat().format(Calendar.getInstance().getTime());

//        Call<JsonObject> jsonObjectCall = apiIntentface.getEmpSecondarySaleByDate(token, date);
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//
//                    try {
//
//                        JSONObject response = new JSONObject(res.body().toString());
//                        Log.e(TAG, "Response EmpSecondarySale: " + response);
//
//                        boolean flag = salesBeatDb.deleteSecondarySale();
//                        // if (flag){
//
//                        String saleAchievement = "0", saleTarget = "0";
//
//                        if (!response.isNull("saleTarget") && response.has("saleTarget"))
//                            saleTarget = response.getString("saleTarget");
//                        if (!response.isNull("saleAchievement") && response.has("saleAchievement"))
//                            saleAchievement = response.getString("saleAchievement");
//
//                        String status = response.getString("status");
//
//                        if (status.equalsIgnoreCase("success")) {
//
//                            salesBeatDb.insertSecondarySale(saleAchievement, saleTarget);
//                        }
//                        // }
//
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getMonthlySecondaryKraByDate");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("Emp Secondar Sale", t.getMessage());
//            }
//        });

        JsonObjectRequest empSecondarySaleByDateRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_SECONDARY_SALE_BY_DATE+"?date="+date, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.e(TAG, "Response EmpSecondarySale: " + response);

                            boolean flag = salesBeatDb.deleteSecondarySale();
                            //@Umesh 02-Feb-2022
                            if(response.getInt("status")==1)
                            {
                                String saleAchievement = "0", saleTarget = "0";

                                if (!response.isNull("saleTarget") && response.has("saleTarget"))
                                    saleTarget = response.getString("saleTarget");
                                if (!response.isNull("saleAchievement") && response.has("saleAchievement"))
                                    saleAchievement = response.getString("saleAchievement");

                                salesBeatDb.insertSecondarySale(saleAchievement, saleTarget);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Sentry.captureMessage(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverCall.handleError2(error.networkResponse.statusCode, TAG,
                        error.getMessage(), "getMonthlySecondaryKraByDate");
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(empSecondarySaleByDateRequest);
        addToRequestQueue(empSecondarySaleByDateRequest);
    }

    private void getCampaign(String token) {


//        Call<String> jsonObjectCall = apiIntentface.getPromotion("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjE3MiwiaXNzIjoiaHR0cHM6Ly9teWFwaS5zYWxlc2JlYXQuaW4vYXBpL3Y0L2VtcGxveWVlTG9naW4iLCJpYXQiOjE1OTIyNTQyODksImV4cCI6MjgwMTg1NDI4OSwibmJmIjoxNTkyMjU0Mjg5LCJqdGkiOiJZM1NTTDJVakd2dHN4eFZ1In0.noHikSEc3VVDigcQ2nlX6pWYs69JfQkPqh3e83cZA_U");
//        Call<JsonObject> jsonObjectCall = apiIntentface.getPromotion(token);
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//
//                    try {
//
//                        JSONObject object = new JSONObject(res.body().toString());
//
//                        Log.e(TAG, "Response Campaigns: " + object.toString());
//
//                        JSONArray campaign = object.getJSONArray("campaigns");
//                        String[] url = new String[campaign.length()];
//                        String[] content = new String[campaign.length()];
//
//                        SharedPreferences.Editor editor = tempPref.edit();
//                        editor.putString("campaignJson", object + "");
//                        editor.apply();
//                        // Log.e(TAG,"######-----------> length"+ campaign.length());
//                        for (int i = 0; i < campaign.length(); i++) {
//
//                            JSONObject object1 = (JSONObject) campaign.get(i);
//                            url[i] = object1.getString("campaignImg");
//                            content[i] = object1.getString("terms");
//
//                            String imageName = String.valueOf(System.currentTimeMillis());
//                            downloadCampaign(url[i], content[i], imageName);
//                            //Log.e(TAG,"######-----------> "+content[i]+"   "+url[i]);
//
//                        }
//
//                        String status = object.getString("status");
//                        if (status.equalsIgnoreCase("success")) {
//
//                            if (campaign.length() == 0) {
//
//                                String imageUrl = getURLForResource();
//                                salesBeatDb.insertCampaignDetail(imageUrl, "no campaign available");
//
//                            }
//
//                        }
//
//                    } catch (Exception e) {
//                        //e.printStackTrace();
//                        String imageUrl = getURLForResource2();
//                        salesBeatDb.insertCampaignDetail(imageUrl, "Error in loading");
//                    }
//                } else {
//                    //e.printStackTrace();
//                    String imageUrl = getURLForResource2();
//                    salesBeatDb.insertCampaignDetail(imageUrl, "Error in loading");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("Get Promotions", t.getMessage());
//            }
//        });

        JsonObjectRequest runningCampaignRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.GET_PROMOTION, null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONObject object) {
                        try {

                            //JSONObject object = new JSONObject(res.body().toString());

                            Log.e(TAG, "Response Campaigns: " + object.toString());

                            JSONArray campaign = object.getJSONArray("campaigns");
                            String[] url = new String[campaign.length()];
                            String[] content = new String[campaign.length()];

                            SharedPreferences.Editor editor = tempPref.edit();
                            editor.putString("campaignJson", object + "");
                            editor.apply();
                            // Log.e(TAG,"######-----------> length"+ campaign.length());
                            for (int i = 0; i < campaign.length(); i++) {

                                JSONObject object1 = (JSONObject) campaign.get(i);
                                url[i] = object1.getString("campaignImg");
                                content[i] = object1.getString("terms");

                                String imageName = String.valueOf(System.currentTimeMillis());
                                downloadCampaign(url[i], content[i], imageName);
                                //Log.e(TAG,"######-----------> "+content[i]+"   "+url[i]);

                            }

                            String status = object.getString("status");
                            if (status.equalsIgnoreCase("success")) {

                                if (campaign.length() == 0) {

                                    String imageUrl = getURLForResource();
                                    salesBeatDb.insertCampaignDetail(imageUrl, "no campaign available");

                                }

                            }

                        } catch (Exception e) {
                            //e.printStackTrace();
                            String imageUrl = getURLForResource2();
                            salesBeatDb.insertCampaignDetail(imageUrl, "Error in loading");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(runningCampaignRequest);
        addToRequestQueue(runningCampaignRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getURLForResource() {

        Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.no_camp);

        File mFile1 = Environment.getExternalStorageDirectory();

        String fileName = "no_campaign.jpg";

        File mFile2 = new File(mFile1, fileName);

        File file = new File(mFile2.getPath());
        if (file.exists()) {
            boolean t = file.delete();
        }

        try {

            FileOutputStream outStream;

            outStream = new FileOutputStream(mFile2);

            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();

            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mFile1.getAbsolutePath().toString() + "/" + fileName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getURLForResource2() {

        Bitmap bitMap = BitmapFactory.decodeResource(getResources(), R.drawable.error);

        File mFile1 = Environment.getExternalStorageDirectory();

        String fileName = "error.jpg";

        File mFile2 = new File(mFile1, fileName);

        File file = new File(mFile2.getPath());
        if (file.exists()) {
            boolean t = file.delete();
        }

        try {

            FileOutputStream outStream;

            outStream = new FileOutputStream(mFile2);

            bitMap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

            outStream.flush();

            outStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mFile1.getAbsolutePath().toString() + "/" + fileName;
    }

    private void getIncentive(String token) {

        final String date = utilityClass.getMYDateFormat().format(Calendar.getInstance().getTime());

        JsonObjectRequest getEmpIncentiveRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.GET_INCENTIVE + "?date=" + date, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");
                        String incentive = data.getString("incentive");
                        boolean incentiveStatus = data.getBoolean("incentiveStatus");

                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE).edit();
                        editor.putString(getString(R.string.incentive_key), incentive);
                        editor.putBoolean(getString(R.string.incentive_status_key), incentiveStatus);
                        editor.apply();
                    }
                } catch (JSONException e) {
                     e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverCall.handleError2(error.networkResponse.statusCode, TAG,
                        error.getMessage(), "getMonthlyIncentive");
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(getEmpIncentiveRequest);
        addToRequestQueue(getEmpIncentiveRequest);
    }

    private void getEmpKraByDate(String token) {

        final String date = utilityClass.getYMDDateFormat().format(Calendar.getInstance().getTime());

        JSONObject params = new JSONObject();
        // RequestParams params = new RequestParams();
        try {
            params.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Call<JsonObject> jsonObjectCall = apiIntentface.getEmpKraByDate(token, params);
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//                    //Log.e(TAG, "Response KRA===" + res);
//
//                    try {
//
//                        JSONObject response = new JSONObject(res.body().toString());
//
//                        String tcAchievement = "0";
//                        if (!response.isNull("tc") && response.has("tc"))
//                            tcAchievement = response.getString("tc");
//
//                        String pcAchievement = "0";
//                        if (!response.isNull("pc") && response.has("pc"))
//                            pcAchievement = response.getString("pc");
//
//                        String tcTarget = "0", pcTarget = "0";
//                        if (!response.isNull("kra") && response.has("kra")) {
//
//                            JSONObject kra = response.getJSONObject("kra");
//
//                            if (!kra.isNull("tc") && kra.has("tc"))
//                                tcTarget = kra.getString("tc");
//
//                            if (!kra.isNull("pc") && kra.has("pc"))
//                                pcTarget = kra.getString("pc");
//
//                        }
//
//
//                        String status = response.getString("status");
//                        //String msg = response.getString("statusMessage");
//
//                        if (status.equalsIgnoreCase("success")) {
//
//                            Calendar cal = Calendar.getInstance();
//                            String date = utilityClass.getYMDDateFormat().format(cal.getTime());
//                            salesBeatDb.insertEmpKraDetail(tcAchievement, pcAchievement, tcTarget, pcTarget, date);
//
//                        }
//
//                    } catch (Exception e) {
//                        // e.printStackTrace();
//                    }
//                } else {
//
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getKraByDate");
//
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("getEmpKraByDate", t.getMessage());
//            }
//        });

        //@Umesh
        JsonObjectRequest getEmpKraRequest = new JsonObjectRequest(Request.Method.GET,
                //SbAppConstants.GET_EMP_KRA_BY_DATE, params, new Response.Listener<JSONObject>() {
                SbAppConstants.API_GET_EMP_KRA_BY_DATE+"?date="+date,null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
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
                    }
                    else
                    {
                        Toast.makeText(DownloadDataService.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                     e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null && error.networkResponse!=null) { //@Umesh 20221007
                    serverCall.handleError2(error.networkResponse.statusCode,
                            TAG, error.getMessage(), "getKraByDate");
                }
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(getEmpKraRequest);
        addToRequestQueue(getEmpKraRequest);
    }

    private void getLeaderboardData(String token) {

        java.util.Calendar cc = java.util.Calendar.getInstance();
        String curentDateString = utilityClass.getYMDDateFormat().format(cc.getTime());

        JSONObject params = new JSONObject();
        //RequestParams params = new RequestParams();
        try {
            params.put("filter", "SALES");
            params.put("date", curentDateString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Call<JsonObject> jsonObjectCall = apiIntentface.getLeaderboardData(token, params);
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//                if (res.isSuccessful()) {
//
//                    // called when response HTTP status is "200 OK"
//                    Log.e(TAG, "Response LEADER_BOARD===" + res);
//
//                    salesBeatDb.deleteLeaderboardDetail();
//
//                    try {
//                        Calendar cal = Calendar.getInstance();
//                        String date = utilityClass.getYMDDateFormat().format(cal.getTime());
//
//                        JSONObject response = new JSONObject(res.body().toString());
//
//                        Log.e(TAG, "Response leaderboard: " + res);
//
//                        if (!response.isNull("top") && response.has("top")) {
//
//                            JSONArray topEmpArr = response.getJSONArray("top");
//
//                            for (int i = 0; i < topEmpArr.length(); i++) {
//                                JSONObject object = (JSONObject) topEmpArr.get(i);
//
//                                salesBeatDb.insertLeaderboardDetail(object.getString("eid"),
//                                        object.getString("name"), object.getString("profilePic"),
//                                        object.getString("totalCalls"), object.getString("productiveCalls"),
//                                        object.getString("totalWeight"), date);
//                            }
//
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getEmployeeLeaderboard");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                //Log.e("getLeaderboardData", t.getMessage());
//                Log.e(TAG, "leaderbaord onFailure");
//            }
//        });

        JsonObjectRequest empsSaleForLeaderboardRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_GET_EMP_LEADER_BOARD, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                salesBeatDb.deleteLeaderboardDetail();

                try {
                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        Calendar cal = Calendar.getInstance();
                        String date = utilityClass.getYMDDateFormat().format(cal.getTime());
                        JSONArray topEmpArr = response.getJSONArray("data");
                        salesBeatDb.deleteLeaderboardDetail();
                        for (int i = 0; i < topEmpArr.length(); i++)
                        {
                            JSONObject object = (JSONObject) topEmpArr.get(i);

                            salesBeatDb.insertLeaderboardDetail(object.getString("eid"),
                                    object.getString("name"), object.getString("profilePic"),
                                    object.getString("totalCalls"), object.getString("productiveCalls"),
                                    object.getString("totalWeight"), date);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                serverCall.handleError2(error.networkResponse.statusCode, TAG,
//                        error.getMessage(), "getEmployeeLeaderboard");
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("filter", "SALES");
                return new JSONObject(params2).toString().getBytes();
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(empsSaleForLeaderboardRequest);
        addToRequestQueue(empsSaleForLeaderboardRequest);

    }

    private void getEmployeeRecordByMonthAndYear(final String month,
                                                 final String year,
                                                 final boolean flag, String token) {

        JSONObject params = new JSONObject();
        // RequestParams params = new RequestParams();
        try {
            params.put("month", month);
            params.put("year", year);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Call<JsonObject> jsonObjectCall = apiIntentface.getEmployeeRecordByMonthAndYear(token, params);
//        jsonObjectCall.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//                    //Log.e(TAG, "Response EmployeeRecordByMonthAndYear=====" + res);
//
//                    String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
//                            attendance = "", date = "", totalWorkingTime = "", totalRetailingTime = "", reason = "";
//
//                    try {
//
//                        JSONObject responseObj = new JSONObject(res.body().toString());
//
//                        JSONArray attendanceArr = responseObj.getJSONArray("attendance");
//
//                        for (int i = 0; i < attendanceArr.length(); i++) {
//
//                            JSONObject response = (JSONObject) attendanceArr.get(i);
//
//                            if (response.has("attendance") && !response.isNull("attendance"))
//                                attendance = response.getString("attendance");
//                            else
//                                attendance = "";
//
//                            if (response.has("date") && !response.isNull("date"))
//                                date = response.getString("date");
//                            else
//                                date = "";
//
//                            if (response.has("checkIn") && !response.isNull("checkIn"))
//                                checkInTime = response.getString("checkIn");
//                            else
//                                checkInTime = "";
//
//                            if (response.has("checkOut") && !response.isNull("checkOut"))
//                                checkOutTime = response.getString("checkOut");
//                            else
//                                checkOutTime = "";
//
//                            if (response.has("totalCalls") && !response.isNull("totalCalls"))
//                                totalCall = response.getString("totalCalls");
//                            else
//                                totalCall = "";
//
//                            if (response.has("productiveCalls") && !response.isNull("productiveCalls"))
//                                productiveCall = response.getString("productiveCalls");
//                            else
//                                productiveCall = "";
//
//                            if (response.has("linesSold") && !response.isNull("linesSold"))
//                                lineSold = response.getString("linesSold");
//                            else
//                                lineSold = "";
//
//                            if (response.has("totalWorkingTime") && !response.isNull("totalWorkingTime"))
//                                totalWorkingTime = response.getString("totalWorkingTime");
//                            else
//                                totalWorkingTime = "";
//
//                            if (response.has("totalRetailingTime") && !response.isNull("totalRetailingTime"))
//                                totalRetailingTime = response.getString("totalRetailingTime");
//                            else
//                                totalRetailingTime = "";
//
//                            if (response.has("reason") && !response.isNull("reason"))
//                                reason = response.getString("reason");
//                            else
//                                reason = "";
//
//                            String tempDate = "";
//                            String month = "";
//                            String year = "";
//
//                            if (!date.isEmpty()) {
//                                String[] temp = date.split(" ");
//                                tempDate = temp[0];
//                                String[] tDate = tempDate.split("-");
//                                month = tDate[1];
//                                year = tDate[0];
//                            }
//
//                            salesBeatDb.insertUserAttendance(attendance, checkInTime, checkOutTime, tempDate, totalCall,
//                                    productiveCall, lineSold, totalWorkingTime, totalRetailingTime, reason, month, year);
//
//                        }
//
//                        if (flag) {
//
//                            String mnth = "";
//                            String yr = year;
//                            //get previous month record in advance
//                            if (Integer.parseInt(month) == 1) {
//
//                                mnth = "12";
//                                yr = String.valueOf(Integer.parseInt(year) - 1);
//
//                            } else {
//
//                                mnth = String.valueOf(Integer.parseInt(month) - 1);
//                            }
//
//                            getEmployeeRecordByMonthAndYear(mnth, yr, false, token);
//                        }
//
//                    } catch (Exception e) {
//                        //e.printStackTrace();
//                    }
//                } else {
//                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
//                    //Log.e(TAG, "EmployeeRecordByMonthAndYear error: " + res + " " + statusCode);
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getEmpOutputByMonth");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("EmployeeRecord", t.getMessage());
//            }
//        });

        //@Umesh
        JsonObjectRequest empRecordByMonthYearRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.GET_EMP_RECORD_BY_MONTH, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
                        attendance = "", date = "", totalWorkingTime = "", totalRetailingTime = "", reason = "";

                try {

                    if(responseObj.getInt("status")==1)
                    {
                        JSONArray attendanceArr = responseObj.getJSONArray("data");
                        for (int i = 0; i < attendanceArr.length(); i++) {

                            JSONObject response = (JSONObject) attendanceArr.get(i);

                            if (response.has("attendance") && !response.isNull("attendance"))
                                attendance = response.getString("attendance");
                            else
                                attendance = "";

                            if (response.has("date") && !response.isNull("date"))
                                date = response.getString("date");
                            else
                                date = "";

                            if (response.has("checkIn") && !response.isNull("checkIn"))
                                checkInTime = response.getString("checkIn");
                            else
                                checkInTime = "";

                            if (response.has("checkOut") && !response.isNull("checkOut"))
                                checkOutTime = response.getString("checkOut");
                            else
                                checkOutTime = "";

                            if (response.has("totalCalls") && !response.isNull("totalCalls"))
                                totalCall = response.getString("totalCalls");
                            else
                                totalCall = "";

                            if (response.has("productiveCalls") && !response.isNull("productiveCalls"))
                                productiveCall = response.getString("productiveCalls");
                            else
                                productiveCall = "";

                            if (response.has("linesSold") && !response.isNull("linesSold"))
                                lineSold = response.getString("linesSold");
                            else
                                lineSold = "";

                            if (response.has("totalWorkingTime") && !response.isNull("totalWorkingTime"))
                                totalWorkingTime = response.getString("totalWorkingTime");
                            else
                                totalWorkingTime = "";

                            if (response.has("totalRetailingTime") && !response.isNull("totalRetailingTime"))
                                totalRetailingTime = response.getString("totalRetailingTime");
                            else
                                totalRetailingTime = "";

                            if (response.has("reason") && !response.isNull("reason"))
                                reason = response.getString("reason");
                            else
                                reason = "";

                            String tempDate = "";
                            String month = "";
                            String year = "";

                            if (!date.isEmpty()) {
                                String[] temp = date.split(" ");
                                tempDate = temp[0];
                                String[] tDate = tempDate.split("-");
                                month = tDate[1];
                                year = tDate[0];
                            }

                            salesBeatDb.insertUserAttendance(attendance, checkInTime, checkOutTime, tempDate, totalCall,
                                    productiveCall, lineSold, totalWorkingTime, totalRetailingTime, reason, month, year);

                        }

                        if (flag) {

                            String mnth = "";
                            String yr = year;
                            //get previous month record in advance
                            if (Integer.parseInt(month) == 1) {

                                mnth = "12";
                                yr = String.valueOf(Integer.parseInt(year) - 1);

                            } else {

                                mnth = String.valueOf(Integer.parseInt(month) - 1);
                            }

                            getEmployeeRecordByMonthAndYear(mnth, yr, false, token);
                        }
                    }

                } catch (Exception e) {
                    Sentry.captureMessage(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error!=null && error.networkResponse!=null) { //@Umesh 20221007
                    serverCall.handleError2(error.networkResponse.statusCode, TAG, error.getMessage(),
                            "getEmpOutputByMonth");
                }
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(empRecordByMonthYearRequest);
        addToRequestQueue(empRecordByMonthYearRequest);

    }

    // Its Not Working From 20220908
    private void getTownListFromServer(String token) {

        //@Umesh
        JsonObjectRequest townListRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.GET_TOWN_LIST, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject JsonResponse) {
                Cursor cursor = salesBeatDb.getAllRecordFromTownListTable();
                if (cursor != null && cursor.getCount() > 0)
                    salesBeatDb.deleteAllFromTownList();

                try {
                    //@Umesh 09-March-2022
                    if(JsonResponse.getInt("status")==1)
                    {
                        JSONArray towns = JsonResponse.getJSONArray("data");
                        for (int i = 0; i < towns.length(); i++)
                        {
                            String town = towns.get(i).toString();
                            salesBeatDb.insertTownList(town);
                        }
                        if (towns.length() == 0)
                        {
                            SharedPreferences.Editor editor = tempPref.edit();
                            editor.putString(getString(R.string.townErrorKey), "No data: In Towns");
                            editor.apply();
                        }
                    }

                } catch (Exception e) {
                     e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(getString(R.string.townErrorKey),
                        error.networkResponse.statusCode + ": " + error.getMessage());
                editor.apply();

                serverCall.handleError2(error.networkResponse.statusCode,
                        TAG, error.getMessage(), "getTowns");
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(townListRequest);
        addToRequestQueue(townListRequest);

    }

    @Override
    public void connectionChange(boolean status) {

        if (status) {
            if (fetchEmployeeRecordByDate) {
                getEmployeeRecordByDate(empRecordDate);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void downloadCampaign(String url, final String content, final String imageName) {

        Log.e(TAG, "downloadCampaign url:" + url + "  content: " + content);

        try {

            /*Glide.with(getApplicationContext())
                    .asBitmap() // Explicitly request a Bitmap
                    .load(url) // Load the image from the URL
                    .into(new CustomTarget<Bitmap>(500, 300) { // Specify the desired width and height
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Log.e(TAG, "onResourceReady: " + resource); // Log the Bitmap object
                            saveImage(resource, imageName, content); // Save the image using your method
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle cleanup if needed (e.g., clearing a placeholder or freeing resources)
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Log.e(TAG, "onLoadFailed: Failed to load image from " + url);
                        }
                    });*/

            /*Glide.with(getApplicationContext())
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(500, 300) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            Log.e(TAG, "onResourceReady: " + resource);
                            saveImage(resource, imageName, content);
                        }
                    });*/

            Glide.with(getApplicationContext())
                    .asBitmap() // Load as Bitmap
                    .load(url)
                    .into(new CustomTarget<Bitmap>(500, 300) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            Log.e(TAG, "onResourceReady: " + resource);
                            saveImage(resource, imageName, content);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Handle cleanup if needed
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            Log.e(TAG, "Image load failed");
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "######-----------> download campaign: " + e.getMessage());
            String imageUrl = getURLForResource2();
            salesBeatDb.insertCampaignDetail(imageUrl, "Error in loading");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveImage(Bitmap finalBitmap, String image_name, String content) {

        String PATH = Environment.getExternalStorageDirectory() + "/SalesBeat2/";
        File myDir = new File(PATH);
        myDir.mkdirs();
        String fname = "Image-" + image_name + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();

        // Log.i("LOAD", PATH + fname);

        try {

            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            String filePath = myDir.getPath() + "/" + fname;

            salesBeatDb.insertCampaignDetail(filePath, content);
            Log.e(TAG, "Campaign Image Local Path" + filePath);

        } catch (Exception e) {
            Log.e(TAG, "######-----------> save image: " + e.getMessage());
            e.printStackTrace();
            String imageUrl = getURLForResource2();
            salesBeatDb.insertCampaignDetail(imageUrl, "Error in loading");
        }
    }

    private void getPrimarySaleHistory(String curentDateString, String token) {

//        Call<JsonObject> callGetPrimarySaleHistory = apiIntentface.getPrimarySaleHistory(token, curentDateString);
//        callGetPrimarySaleHistory.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> res) {
//                if (res.isSuccessful()) {
//                    // called when response HTTP status is "200 OK"
//                    Log.e(TAG, "Response Primary Sale History: " + res.body().toString());
//
//                    try {
//
//                        JSONObject response = new JSONObject(res.body().toString());
//                        JSONArray saleTarget = response.getJSONArray("saleTarget");
//
//                        if (saleTarget.length() > 0)
//                            salesBeatDb.deleteAllFromTablePrimarySaleHistory();
//
//                        for (int i = 0; i < saleTarget.length(); i++) {
//
//                            Item item = new Item();
//                            JSONObject obj = (JSONObject) saleTarget.get(i);
//                            String target = obj.getString("target");
//                            JSONObject distributor = obj.getJSONObject("distributor");
//                            item.setItem1(distributor.getString("did"));
//                            item.setItem2(distributor.getString("name"));
//                            item.setItem3(target);
//
//                            JSONArray sales = obj.getJSONArray("sales");
//                            int ach = 0;
//                            String dateStr = "";
//                            for (int j = 0; j < sales.length(); j++) {
//
//                                JSONObject obj2 = (JSONObject) sales.get(j);
//                                dateStr = obj2.getString("date");
//                                ach = ach + Integer.parseInt(obj2.getString("sale"));
//                            }
//
//                            item.setItem4(String.valueOf(ach));
//
//                            DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                            DateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//
//                            String formattedDate = "";
//                            try {
//
//                                String temp[] = dateStr.split(" ");
//                                Date date = simpleDateFormat2.parse(temp[0]);
//                                formattedDate = simpleDateFormat.format(date);
//
//                            } catch (ParseException e) {
//                                // e.printStackTrace();
//                            }
//
//                            item.setItem5(formattedDate);
//
//                            salesBeatDb.insertInPrimarySaleHistory(item);
//                        }
//
//                    } catch (Exception e) {
//                        //e.printStackTrace();
//                    }
//                } else {
//                    serverCall.handleError2(res.code(), TAG, res.message(), "getMonthlyPrimaryKraByDateHistory");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e("getPrimarySaleHistory", t.getMessage());
//            }
//        });

        JsonObjectRequest getEmpPrimarySaleHistoryReqest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_PRIMARY_SALE_HISTORY+"?date=" + curentDateString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //@Umesh 02-Feb-2022
                            if(response.getInt("status")==1)
                            {
                                JSONObject data = response.getJSONObject("data");
                                JSONArray saleTarget = data.getJSONArray("saleTarget");

                                if (saleTarget.length() > 0)
                                    salesBeatDb.deleteAllFromTablePrimarySaleHistory();

                                for (int i = 0; i < saleTarget.length(); i++)
                                {
                                    Item item = new Item();
                                    JSONObject obj = (JSONObject) saleTarget.get(i);
                                    String target = obj.getString("target");
                                    String did = obj.getString("did");
                                    //@Umesh 17-08-2022
                                    if(obj.get("distributors").equals(null)) {
                                        item.setItem1(did);
                                        item.setItem2("");
                                    }
                                    else {
                                        JSONObject distributor = obj.getJSONObject("distributors");
                                        item.setItem1(distributor.getString("did"));
                                        item.setItem2(distributor.getString("name"));
                                    }
                                    item.setItem3(target);

                                    JSONArray sales = obj.getJSONArray("sales");
                                    int ach = 0;
                                    String dateStr = "";
                                    for (int j = 0; j < sales.length(); j++) {

                                        JSONObject obj2 = (JSONObject) sales.get(j);
                                        dateStr = obj2.getString("date");
                                        ach = ach + Integer.parseInt(obj2.getString("sale"));
                                    }

                                    item.setItem4(String.valueOf(ach));

                                    DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    DateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

                                    String formattedDate = "";
                                    try {

                                        String temp[] = dateStr.split(" ");
                                        Date date = simpleDateFormat2.parse(temp[0]);
                                        formattedDate = simpleDateFormat.format(date);

                                    } catch (ParseException e) {
                                        // e.printStackTrace();
                                    }

                                    item.setItem5(formattedDate);

                                    salesBeatDb.insertInPrimarySaleHistory(item);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Sentry.captureMessage(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                serverCall.handleError2(error.networkResponse.statusCode, TAG,
                        error.getMessage(), "getMonthlyPrimaryKraByDateHistory");
                Sentry.captureMessage(error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("authorization", token);
                return headers;
            }
        };

        setRetryPolicyToRequests(getEmpPrimarySaleHistoryReqest);
        addToRequestQueue(getEmpPrimarySaleHistoryReqest);
    }

    //Volley
    @SuppressLint("MissingPermission")
    private void logInUser(String string, String app_version) {

        if (!prefSFA.getString("username", "").isEmpty() && !prefSFA.getString("password", "").isEmpty()) {

            JSONObject orderrrr = new JSONObject();
            try {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                Log.e("FIREBASE", "Firebase reg id: " + regId);

                TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                orderrrr.put("auth", getString(R.string.apikey));
                orderrrr.put("cid", prefSFA.getString("cmny_id", ""));
                orderrrr.put("username", prefSFA.getString("username", ""));
                orderrrr.put("password", prefSFA.getString("password", ""));
                orderrrr.put("ismobileuser", true);
                orderrrr.put("app_version", BuildConfig.VERSION_NAME);
                //orderrrr.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
                orderrrr.put("os_version", Build.VERSION.RELEASE);
                orderrrr.put("model", Build.BRAND + " " + Build.MODEL);
                try {

                    if (manager != null)
                        orderrrr.put("imei", manager.getDeviceId());

                } catch (SecurityException e) {
                    orderrrr.put("imei", "restricted in Q");
                }
                orderrrr.put("token", regId);


            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    SbAppConstants.API_USER_LOG_IN,
                    orderrrr, new Response.Listener<JSONObject>() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onResponse(JSONObject response) {

                    Log.e(TAG, "Download User Login===" + response);
                    String status = "";
                    String statusMessage = "";
                    try {
                        status = String.valueOf(response.getInt("status"));
                        statusMessage = response.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //@Umesh 01-feb-2022
                    if (!status.equals("1")) {
                        //showErrorAlertMsg(statusMessage, true);
                    } else {
                        try {
                            JSONObject data = response.getJSONObject("data");

                            JSONObject authtoken = data.getJSONObject("authtoken");
                            String TokenValidTo = authtoken.getString("expiration");
                            String token =authtoken.getString("token");
                            SharedPreferences.Editor Teditor = prefSFA.edit();
                            Teditor.putString("TokenValidTo", TokenValidTo);
                            Teditor.apply();

                            JSONObject employee = data.getJSONObject("emp");
                            //String token = response.getString("token");
//                            String token = employee.getString("fcmToken"); //@Umesh
                            prefSFA.edit().putString("token", "Bearer " + token).apply();

                            if (userAttendance.isEmpty()) {

                                SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
                                final String date = sdff.format(Calendar.getInstance().getTime());
                                getEmployeeRecordByDate(date);
                            }

                            if (!app_version.isEmpty()) {

                                salesBeatDb.deletetCampaign();
                                deleteCampaignFromLocal();

                                //@Umesh 20220908
                                //getTownListFromServer(prefSFA.getString("token", ""));
                                getIncentive(prefSFA.getString("token", ""));
                                getCampaign(prefSFA.getString("token", ""));
                                //get current month record
                                getEmployeeRecordByMonthAndYear(month, year, true, prefSFA.getString("token", ""));
                            }

                            getLeaderboardData(prefSFA.getString("token", ""));

                            getEmpKraByDate(prefSFA.getString("token", ""));

                            getEmpPrimarySaleByDate(prefSFA.getString("token", ""));

                            getEmpSecondarySaleByDate(prefSFA.getString("token", ""));

                            getPrimarySaleHistory(utilityClass.getCurrentDateInMYformat(), prefSFA.getString("token", ""));

                            getDistributorTargetAchievement(utilityClass.getCurrentDateInMYformat(), prefSFA.getString("token", ""));


                        } catch (JSONException e) {
                            //loader.dismiss();
                            e.printStackTrace();
                            SbLog.printException("LoginScreen", "employeeLogin", e.getMessage(), "0");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //loader.dismiss();

                    try {

                        SbLog.printError(TAG, "employeeLogin", String.valueOf(error.networkResponse.statusCode), error.getMessage(), "");
                        String responseError = new String(error.networkResponse.data, "utf-8");


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

            setRetryPolicyToRequests(jsonObjectRequest);
            Volley.newRequestQueue(context).add(jsonObjectRequest);
        }
    }

    private void setRetryPolicyToRequests(JsonObjectRequest jsonObjectRequest){
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void addToRequestQueue(JsonObjectRequest jsonObjectRequest){
        requestQueue.add(jsonObjectRequest);
        requestQueue.addRequestEventListener(new RequestQueue.RequestEventListener() {
            @Override
            public void onRequestEvent(Request<?> request, int event) {
//                Log.e(TAG,"MSG==>"+request.hasHadResponseDelivered()+" "+event);
            }
        });
    }

}
