package com.newsalesbeatApp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.MainActivity;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.SBApplication;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by abc on 2/7/19.
 */

public class MarkAttendanceService extends IntentService {

    public static final int DOWNLOAD_SUCCESS_PRESENT = 11;
    public static final int DOWNLOAD_SUCCESS_CHECK_OUT = 22;
    public static final int DOWNLOAD_ERROR1 = 410;
    public static final int DOWNLOAD_ERROR2 = 500;
    public static final int DOWNLOAD_SUCCESS_LEAVE = 33;
    String TAG = "MarkAttendanceService";
    ServerCall serverCall;
    Bundle bundle = null;
    Context context;
    Location location;
    GPSLocation locationProvider;
    private SharedPreferences prefSFA, tempPref;
    private ResultReceiver receiver;

    public MarkAttendanceService() {

        super(MarkAttendanceService.class.getName());

        context = SBApplication.getInstance();
        prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        serverCall = new ServerCall(context);
        bundle = new Bundle();
        locationProvider = new GPSLocation(context);
        location = locationProvider.getLocation();
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String attendance = intent.getStringExtra("attendance");
        String reason = intent.getStringExtra("reason");
        String checkInTime = intent.getStringExtra("checkInTime");
        String checkOutTime = intent.getStringExtra("checkOutTime");
        String workingHour = intent.getStringExtra("workingHour");
        ArrayList<String> activity_type = intent.getStringArrayListExtra("activityType");
        String workingtown = intent.getStringExtra("workingTown");
        String disId = intent.getStringExtra("did");
        String disName = intent.getStringExtra("disName");
        String cmnt = intent.getStringExtra("cmnt");
        receiver = intent.getParcelableExtra("receiver");

        //@Umesh 20220914
        String jointwrkempid = intent.getStringExtra("jointwrkemp_id");
        String jointwrkempname = intent.getStringExtra("jointwrkemp_name");
        Log.d(TAG, "get checkInTime: "+checkInTime);
        //markAttendance(attendance, reason, checkInTime, checkOutTime, workingHour);
        markAttendanceNew(attendance, reason, checkInTime, checkOutTime, workingHour, activity_type, workingtown, disId, disName, cmnt,jointwrkempid,jointwrkempname);

    }

    private void markAttendanceNew(String attendance, String reason, String checkInTime,
                                   String checkOutTime, String workingHour, ArrayList<String> activity_type,
                                   String workingtown, String disId, String disName, String cmnt,String jointwrkempid,String jointwrkempname ) {

        //@Umesh 10-March-2022
        JSONObject obj = new JSONObject();
        try {
            obj.put("attendance", attendance);
            obj.put("activity_type", activity_type.toString());
            obj.put("workingtown", workingtown);
            if(!disId.isEmpty())
            obj.put("did", Integer.parseInt(disId));

//            if (!reason.isEmpty()) {
//
//                if (!disName.isEmpty()) {
//                    obj.put("comment", "Comment: " + cmnt + "  Reason: " + reason + " Distributor Name:" + disName);
//                } else {
//                    obj.put("comment", "Comment: " + cmnt + "  Reason: " + reason);
//                }
//
//            } else {
                obj.put("comment", cmnt);  // As Per Naman Sir Discussion On 20221011
            //}

            String lat = "", longt = "";
            if (location != null) {
                lat = String.valueOf(location.getLatitude());
                longt = String.valueOf(location.getLongitude());
            }
            obj.put("latitude", lat);
            obj.put("longitude", longt);
            obj.put("zoneId", prefSFA.getString(getString(R.string.zone_id_key), ""));
            obj.put("jointwrkemp_id", Integer.parseInt(jointwrkempid));
            obj.put("jointwrkemp_name", jointwrkempname);
        }
        catch (Exception ex)
        {

        }


        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_TO_MARK_ATTENDANCE_NEW,obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        // response
                        Log.e("Response", "NEW Attendance====" + response);

                        try {
                            //loader.dismiss();
                            //@Umesh 10-March-2022
                            if(response.getInt("status")==1)
                            {
                                JSONObject object = response.getJSONObject("data");

                                Log.e("leave trial Response", "Status Attendance: " + attendance
                                        + " Reason:" + reason);

                                String closingStartDate = "", closingEndDate = "", activityType = "";
                                if (!object.isNull("activityType") && object.has("activityType")) {

                                    Log.e("New Start1", "--->" + object.getString("activityType"));
                                    activityType = object.getString("activityType").replace("\\[", "");
                                    activityType = activityType.replace("\\]", "");
                                    Log.e("New Start2", "--->" + activityType);
                                }
                                if (!object.isNull("closingStartDate") && object.has("closingStartDate"))
                                    closingStartDate = object.getString("closingStartDate");
                                if (!object.isNull("closingEndDate") && object.has("closingEndDate"))
                                    closingEndDate = object.getString("closingEndDate");

                                Log.d(TAG, "get Check In Time Attendance: "+checkInTime);
                                Log.d(TAG, "Time Attendance: "+attendance);
                                if(attendance.equalsIgnoreCase("Present"))
                                {
                                    Log.d(TAG, "update Attendance: "+attendance);

                                    SharedPreferences.Editor editor = tempPref.edit();
                                    editor.putString(getString(R.string.attendance_key), attendance);
                                    editor.putString(getString(R.string.check_in_time_key), checkInTime);
                                    editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                    editor.putString(getString(R.string.town_name_key), workingtown);

                                    Log.e("STartWorking", "" + closingStartDate + " to " + closingEndDate + " actType" + activityType);
                                    if (closingStartDate != null && !closingStartDate.isEmpty()
                                            && closingStartDate.equalsIgnoreCase("null")

                                    ) {
                                        editor.putString(getString(R.string.closing_start_date_key), closingStartDate);
                                        editor.putString(getString(R.string.closing_end_date_key), closingEndDate);
                                    } else {
                                        editor.putString(getString(R.string.closing_start_date_key), "2020-01-28");
                                        editor.putString(getString(R.string.closing_end_date_key), "2020-02-05");
                                    }

                                    if (!activityType.isEmpty()) {
                                        editor.putString(getString(R.string.act_type_key), activityType);
                                    }

                                    editor.apply();

                                    receiver.send(DOWNLOAD_SUCCESS_PRESENT, bundle);
                                }
                                else if(attendance.equalsIgnoreCase("checkOut"))
                                {
                                    Log.d(TAG, "get Check In Time Attendance1: "+checkInTime);
                                    SharedPreferences.Editor editor = tempPref.edit();
                                    editor.putString(getString(R.string.attendance_key), attendance);
                                    editor.putString(getString(R.string.check_in_time_key), checkInTime);
                                    editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                    editor.putString(getString(R.string.working_time_key), workingHour);
                                    editor.apply();

                                    receiver.send(DOWNLOAD_SUCCESS_CHECK_OUT, bundle);
                                }
                                else if(attendance.equalsIgnoreCase("leave") ||
                                        attendance.equalsIgnoreCase("long leave") ||
                                        attendance.equalsIgnoreCase("holiday") ||
                                        attendance.equalsIgnoreCase("week off"))
                                {
                                    Log.d(TAG, "get Check In Time Attendance2: "+checkInTime);
                                    SharedPreferences.Editor editor = tempPref.edit();
                                    editor.putString(getString(R.string.attendance_key), attendance);
                                    editor.putString(getString(R.string.reason_key), reason);
                                    editor.putString(getString(R.string.check_in_time_key), checkInTime);
                                    editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                    editor.putString(getString(R.string.working_time_key), workingHour);
                                    editor.apply();
                                    receiver.send(DOWNLOAD_SUCCESS_LEAVE, bundle);
                                }
                            }
                            else {
                                //receiver.send(DOWNLOAD_ERROR1, bundle);
                                Toast.makeText(context, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e)
                        {
                            receiver.send(DOWNLOAD_ERROR2, bundle);
                            //loader.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                //loader.dismiss();

                Log.e(TAG, " ##########" + error.getMessage());

                try {

                    if (error.networkResponse.statusCode == 410) {

                        receiver.send(DOWNLOAD_ERROR1, bundle);
                        //showNotificationDialog("Alert!!", "API deprecated please update your app.");

                    } else {
                        receiver.send(DOWNLOAD_ERROR2, bundle);
                        //Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    serverCall.handleError(error, TAG, "markEmpAttendanceNew");

                } catch (Exception e) {
                    receiver.send(DOWNLOAD_ERROR2, bundle);
                    //Toast.makeText(getContext(), "null error code", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }) {
            /*@Override
            protected Map<String, String> getParams() {

                String lat = "", longt = "";
                if (location != null) {
                    lat = String.valueOf(location.getLatitude());
                    longt = String.valueOf(location.getLongitude());
                }


                Map<String, String> params = new HashMap<>();
                params.put("attendance", attendance);
                params.put("activity_type", activity_type.toString());
                params.put("workingtown", workingtown);
                params.put("did", disId);

//                if (attendance.equalsIgnoreCase("Present"))
//                    params.put("comment", cmnt);
//                else {
//                    if (disName.isEmpty())
//                        params.put("comment", reason);
//                    else
//                        params.put("comment", reason + " Distributor Name:" + disName);
//                }

                if (!reason.isEmpty()) {

                    if (!disName.isEmpty()) {
                        params.put("comment", "Comment: " + cmnt + "  Reason: " + reason + " Distributor Name:" + disName);
                    } else {
                        params.put("comment", "Comment: " + cmnt + "  Reason: " + reason);
                    }

                } else {
                    params.put("comment", "Comment: " + cmnt);
                }

                params.put("latitude", lat);
                params.put("zoneId", prefSFA.getString(getString(R.string.zone_id_key), ""));
                params.put("longitude", longt);

                Log.e("New Mark Attendance", " #### JSON: " + params.toString());

                return params;
            }*/

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        postRequest.setShouldCache(false);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(postRequest);

    }


    private void markAttendance(final String attendance, final String reason, final String checkInTime,
                                final String checkOutTime, final String workingHour) {

        StringRequest postRequest = new StringRequest(Request.Method.POST, SbAppConstants.API_TO_MARK_ATTENDANCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // response
                        Log.e("Response", "Attendance====" + response);

                        try {
                            //loader.dismiss();
                            JSONObject object = new JSONObject(response);
                            String status = object.getString("status");
                            String msg = object.getString("statusMessage");


                            if (status.equalsIgnoreCase("success")
                                    && attendance.equalsIgnoreCase("Present")) {
                                Log.d(TAG, "get Check In Time Attendance3: "+checkInTime);
                                SharedPreferences.Editor editor = tempPref.edit();
                                editor.putString(getString(R.string.attendance_key), attendance);
                                editor.putString(getString(R.string.check_in_time_key), checkInTime);
                                editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                editor.apply();

                                receiver.send(DOWNLOAD_SUCCESS_PRESENT, bundle);

//                                tvAttendanceStatus.setText(tempPref.getString(getString(R.string.attendance_key), ""));
//                                tvCheckInTime.setText(tempPref.getString(getString(R.string.check_in_time_key), ""));
//                                tvCheckOutTime.setText(tempPref.getString(getString(R.string.check_out_time_key), ""));
//
//                                llCurrentStatus.setVisibility(View.VISIBLE);
//                                llMarkAttendance.setVisibility(View.GONE);
//
//                                //showTownList();
//                                refreshPage();

                            } else if (status.equalsIgnoreCase("success")
                                    && attendance.equalsIgnoreCase("checkOut")) {
                                Log.d(TAG, "get Check In Time Attendance4: "+checkInTime);
                                SharedPreferences.Editor editor = tempPref.edit();
                                editor.putString(getString(R.string.attendance_key), attendance);
                                editor.putString(getString(R.string.check_in_time_key), checkInTime);
                                editor.putString(getString(R.string.check_out_time_key), checkOutTime);
                                editor.putString(getString(R.string.working_time_key), workingHour);
                                editor.apply();

                                receiver.send(DOWNLOAD_SUCCESS_CHECK_OUT, bundle);

//                                tvCheckOutTime.setVisibility(View.VISIBLE);
//                                btnCheckOut.setVisibility(View.GONE);
//                                btnBookOrder.setVisibility(View.GONE);
//                                llCurrentStatus.setVisibility(View.VISIBLE);
//                                llMarkAttendance.setVisibility(View.GONE);
//                                llShareReport.setVisibility(View.VISIBLE);
//
//                                handler1.removeCallbacks(runnable1);
//
//                                refreshPage();

                            }
                            if (status.equalsIgnoreCase("error")) {

                                receiver.send(DOWNLOAD_ERROR1, bundle);
                                //showNotificationDialog("API deprecated.", "Kindly update your app or contact admin!");

                            } else {

                                //receiver.send(DOWNLOAD_ERROR2, bundle);
                                //Toast.makeText(requireContext(), "" + msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            receiver.send(DOWNLOAD_ERROR2, bundle);
                            //loader.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                //loader.dismiss();

                try {

                    if (error.networkResponse.statusCode == 410) {

                        receiver.send(DOWNLOAD_ERROR1, bundle);
                        //showNotificationDialog("Alert!!", "API deprecated please update your app.");

                    } else {
                        receiver.send(DOWNLOAD_ERROR2, bundle);
                        //Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    serverCall.handleError(error, TAG, "markEmpAttendance");

                } catch (Exception e) {
                    receiver.send(DOWNLOAD_ERROR2, bundle);
                    //Toast.makeText(getContext(), "null error code", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {

                String lat = "", longt = "";
                if (location != null) {
                    lat = String.valueOf(location.getLatitude());
                    longt = String.valueOf(location.getLongitude());
                }

                Map<String, String> params = new HashMap<>();
                params.put("attendance", attendance);
                params.put("comment", reason);
                params.put("latitude", lat);
                params.put("longitude", longt);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        postRequest.setShouldCache(false);

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(postRequest);
    }
}
