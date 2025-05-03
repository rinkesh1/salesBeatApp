package com.newsalesbeatApp.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.interfaces.VolleyCallback;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SBApplication;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class LoadAttendance extends IntentService {

    public static final int DOWNLOADED = 11;
    public static final int ERROR = 410;
    UtilityClass utilityClass;
    SalesBeatDb salesBeatDb;
    SharedPreferences prefSb;
    Bundle bundle = null;
    Context context;
    private String TAG = "LoadAttendance";
    private ResultReceiver receiver;

    public LoadAttendance() {
        super(LoadAttendance.class.getName());
        context = SBApplication.getInstance();
        utilityClass = new UtilityClass(context);
        salesBeatDb = SalesBeatDb.getHelper(context);
        bundle = new Bundle();
        prefSb = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        assert intent != null;
        String month = intent.getStringExtra("month");
        String year = intent.getStringExtra("year");
        receiver = intent.getParcelableExtra("receiver");

        if (utilityClass.isInternetConnected())
            getMonthlyAttendance(month, year);
    }

    @SuppressLint("RestrictedApi")
    private void getMonthlyAttendance(String month, String year) {

        //Log.e(TAG, "Month--->" + month + " & year-->" + year);

        getEmployeeRecordByMonthAndYear(month, year, result -> {

            String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
                    attendance = "", date = "", totalWorkingTime = "", totalRetailingTime = "", reason = "";

            try {

                JSONArray attendanceArr = result.getJSONArray("attendance");

                for (int i = 0; i < attendanceArr.length(); i++)
                {
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
                    String monthV = "";
                    String yearV = "";

                    if (!date.isEmpty()) {
                        String[] temp = date.split(" ");
                        tempDate = temp[0];
                        String[] tDate = tempDate.split("-");
                        monthV = tDate[1];
                        yearV = tDate[0];
                    }

                    salesBeatDb.insertUserAttendance(attendance, checkInTime, checkOutTime, tempDate, totalCall,
                            productiveCall, lineSold, totalWorkingTime, totalRetailingTime, reason, monthV, yearV);
                }

                bundle.putString("month", month);
                bundle.putString("year", year);
                receiver.send(DOWNLOADED, bundle);

            } catch (Exception e) {

                receiver.send(ERROR, bundle);
                // e.printStackTrace();
            }

        });
    }

    private void getEmployeeRecordByMonthAndYear(final String month, final String year, final VolleyCallback volleyCallback) {


        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_GET_EMP_RECORD_BY_MONTH,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                Log.e("Response", "EmployeeRecordByMonthAndYear=====" + responseObj);

                volleyCallback.onSuccessResponse(responseObj);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    SbLog.printError(TAG, "getEmpOutputByMonth",
                            String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                            prefSb.getString(getString(R.string.emp_id_key), ""));

                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("ERRR", "===== " + responseBody);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    error.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("month", month);
                params2.put("year", year);
                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSb.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(objectRequest);
    }
}
