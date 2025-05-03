package com.newsalesbeatApp.netwotkcall;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsalesbeatApp.BuildConfig;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.interfaces.ApiIntentface;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.Config;
import com.newsalesbeatApp.utilityclass.ImageUtils;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;

public class ServerCall {

    private static boolean locFlag = false;
    //private final int DEFAULT_TIMEOUT = 60 * 1000;
    UtilityClass utilityClass;
    private final String TAG = "ServerCall";
    private final Context context;
    private SharedPreferences prefSFA;
    private final SharedPreferences tempSfa2;
    private final SharedPreferences tempSfa3;
    private final SalesBeatDb salesBeatDb;
    private final HashMap<String, String> requestQ;
    private final boolean locFlag2 = false;
    private final DateFormat dateFormat;
    // Api Client
    private final ApiIntentface apiIntentface;

    String eid, cid, username, password, token;

    @SuppressLint("SimpleDateFormat")
    public ServerCall(Context ctx) {
        Log.d(TAG, "app ServerCall");
        this.context = ctx;
        prefSFA = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa2 = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name_2), Context.MODE_PRIVATE);
        tempSfa3 = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        //salesBeatDb = new SalesBeatDb(ctx);
        utilityClass = new UtilityClass(ctx);
        salesBeatDb = SalesBeatDb.getHelper(ctx);
        requestQ = new HashMap<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cursor cursor = salesBeatDb.getEmpRocord();

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

            eid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_EID));
            cid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_CID));
            token = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_TOKEN));
            username = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_USERNAME));
            password = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_PASSWORD));
//            String transactionId = cursor.getString(cursor.getColumnIndex("transactionId"));
        }


        apiIntentface = RetrofitClient.getClient().create(ApiIntentface.class);

        SbLog.recordScreen("ServerCall");
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void syncData() {

        if (requestQ.size() != 0)
            Log.e(TAG, "Request queue length: " + requestQ.size()+requestQ);

        if (requestQ.size() == 0 && !SbAppConstants.STOP_SYNC && utilityClass.isInternetConnected()) //@Umesh 26-06-2022
        {

            //@Umesh
            syncBeatVisited(); //EMP_BEAT_VISIT working ok

            getExistingRetailersProductiveCalls(); //Submit_Order  working ok

            getExistingRetailersNonProductiveCalls(); //Submit_Order  working ok

            getNewlyAddedRetailers(); //ADD_NEW_RETAILER

            getNewlyAddedRetailersProductiveAndNonProductiveCalls(); //Submit_Order

            //syncExistingRetailersCanceledOrders(); //SUBMIT_ORDER2 Managed By getExistingRetailersProductiveCalls

            syncExistingDistributorsOrder(); //SUBMIT_DISTRIBUTOR_ORDER

            syncExistingDistributorsStock(); // SUBMIT_DISTRIBUTOR_STOCK_INFO

//            syncExistingDistributorsClosing(); // SUBMIT_DISTRIBUTOR_CLOSING Pending...

            syncExistingDistributorCancelledOrder(); //CANCEL_DISTRIBUTOR_ORDER

            syncNewlyAddedDistributors(); //ADD_NEW_DISTRIBUTOR

            syncOtherActivity(); //FULL_DAY_ACTIVITY



        }
    }


    public void handleError(VolleyError error, String screen, String methodName) {
        try {

            error.printStackTrace();

            if (error.networkResponse.statusCode != 0) {

//                SbLog.printError(screen, methodName, String.valueOf(error.networkResponse.statusCode), error.getMessage(),eid);

                if (error.networkResponse.statusCode == 422) {

                    showErrorDialog(error);

                } else if (error.networkResponse.statusCode == 401 || error.networkResponse.statusCode == 403) {

                    refreshUserDetails();

                } else if (error.networkResponse.statusCode == 408) {

                    Toast.makeText(context, "Try after some time", Toast.LENGTH_SHORT).show();

                } else if (error.networkResponse.statusCode == 500) {

                    Toast.makeText(context, "Internal server error", Toast.LENGTH_SHORT).show();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleError2(int statusCode, String screen, String msg, String methodName) {

        try {

            if (String.valueOf(statusCode)!=null && statusCode != 0)  //@Umesh 21-08-2022
            {

//                SbLog.printError(screen, methodName, String.valueOf(statusCode), msg, eid);

                if (statusCode == 422) {

                    Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();

                } else if (statusCode == 401 || statusCode == 403) {

                    refreshUserDetails();

                } else if (statusCode == 408) {

                    Toast.makeText(context, "Try after some time", Toast.LENGTH_SHORT).show();

                } else if (statusCode == 500) {

                    Toast.makeText(context, "Internal server error", Toast.LENGTH_SHORT).show();

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleError3(int statusCode, String msg, String methodName, String reqNumber) {

        try {

            if (statusCode != 500)
                requestQ.remove(reqNumber);

            if (statusCode != 0) {

//                SbLog.printError(TAG, methodName, String.valueOf(statusCode), msg, eid);

                if (statusCode == 401 || statusCode == 403 || statusCode == 400) {

                    refreshUserDetails();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void refreshUserDetails() {

        if (!username.isEmpty() && !password.isEmpty()) {

            JSONObject orderrrr = new JSONObject();
            try {

                SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
                String regId = pref.getString("regId", null);

                Log.e("FIREBASE", "Firebase reg id: " + regId);

                //TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

                orderrrr.put("auth", context.getString(R.string.apikey));
                orderrrr.put("cid", cid);
                orderrrr.put("username", username);
                orderrrr.put("password", password);
                orderrrr.put("ismobileuser", true);
                orderrrr.put("app_version",  BuildConfig.VERSION_NAME);
                //orderrrr.put("os_version", String.valueOf(Build.VERSION.SDK_INT));
                orderrrr.put("os_version", Build.VERSION.RELEASE);
                orderrrr.put("model", Build.BRAND + " " + Build.MODEL);
                orderrrr.put("imei", "restricted in Q");
                orderrrr.put("token", regId);

            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    SbAppConstants.API_USER_LOG_IN,
                    orderrrr, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, "User Re-Login Refresh data: " + response);
                    try {
                        String status = String.valueOf(response.getInt("status")); //@Umesh
                        JSONObject data = response.getJSONObject("data");

                        JSONObject authtoken = data.getJSONObject("authtoken");
                        String TokenValidTo=authtoken.getString("expiration");
                        String token =authtoken.getString("token");
                        SharedPreferences.Editor Teditor = prefSFA.edit();
                        Teditor.putString("TokenValidTo", TokenValidTo);
                        Teditor.apply();


                        JSONObject employee = data.getJSONObject("emp");
                        String cmny_id = employee.getString("cid");
                        String emp_id = employee.getString("eid");
                        String emp_name = employee.getString("name");
                        //String username = employee.getString("username");
                        String emp_ph_no = employee.getString("phone1");
                        String emp_email = employee.getString("email1");
                        String headquarter = employee.getString("headquarter");
                        String zone = employee.getString("zone");
                        String zoneid = employee.getString("zoneid");
                        String state = employee.getString("state");
                        //String report_to = employee.getString("reportingTo");
                        String report_to = employee.getString("reportingToEuid"); //@Umesh
                        String designation = employee.getString("designation");
                        //String emp_photo_url = employee.getString("profilePic");

                        //String token = response.getString("token");
//                        String token = employee.getString("fcmToken");//@Umesh

                        if (status.equalsIgnoreCase("1") && cmny_id.equalsIgnoreCase(cid))
                        {

                            SharedPreferences prefSFA = context.getSharedPreferences(
                                    context.getString(R.string.pref_name), Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = prefSFA.edit();
                            editor.putString(context.getString(R.string.emp_id_key), emp_id);
                            editor.putString(context.getString(R.string.emp_name_key), emp_name);
                            editor.putString(context.getString(R.string.emp_phoneno_key), emp_ph_no);
                            editor.putString(context.getString(R.string.emp_emailid_key), emp_email);
                            editor.putString(context.getString(R.string.zone_key), zone);
                            editor.putString(context.getString(R.string.zone_id_key), zoneid);
                            editor.putString(context.getString(R.string.state_key), state);
                            editor.putString(context.getString(R.string.emp_headq_key), headquarter);
                            editor.putString(context.getString(R.string.emp_reportingto_key), report_to);
                            editor.putString(context.getString(R.string.emp_designation_key), designation);
                            editor.putString("token", "Bearer " + token);
                            //editor.putString("token_validto", token);
                            editor.apply();

                            salesBeatDb.insertEmployeeRecord(emp_id, "1", emp_name, emp_ph_no, emp_email,
                                    username, password, zone, zoneid, state, headquarter, report_to, designation,
                                    "", "Bearer " + token);

                            Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    handleError(error, TAG, "employee-reports/date/");
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
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(context).add(jsonObjectRequest);

        }

    }

    private void showErrorDialog(VolleyError error) {

        try {

            String responseBody = null;
            responseBody = new String(error.networkResponse.data, "utf-8");
            Log.e(TAG, "Error: " + responseBody);
            JSONObject object = new JSONObject(responseBody);
            String message = object.getString("message");
            JSONObject errorr = object.getJSONObject("errors");

            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Message!");
            dialog.setMessage(message + "\n" + errorr.toString());

            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            Dialog dialog1 = dialog.create();
            dialog1.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmpPath(Location locationObj) {

        String attendance= tempSfa3.getString(context.getString(R.string.attendance_key), ""); //@Umesh 20221007
        String checkout= tempSfa3.getString(context.getString(R.string.check_out_time_key), ""); //@Umesh 20221007

        if(attendance.toLowerCase(Locale.ROOT).equals("present") && checkout.isEmpty()) //@Umesh 20221007
        {
            String latitude = "", longitude = "";
            String latSaved = tempSfa2.getString(context.getString(R.string.lat_key), "");
            String longtSaved = tempSfa2.getString(context.getString(R.string.longi_key), "");
            String latTemp = String.valueOf(locationObj.getLatitude());
            String longtTemp = String.valueOf(locationObj.getLongitude());
            String[] latArrT = latTemp.split("\\.");
            String[] longtArrT = longtTemp.split("\\.");

            if (latArrT[1].length() > 4)
                latitude = latArrT[1].substring(0, 4);
            else
                latitude = latArrT[1];

            if (longtArrT[1].length() > 4)
                longitude = longtArrT[1].substring(0, 4);
            else
                longitude = longtArrT[1];

            Log.e(TAG, " Latitude: " + latSaved + " == " + latitude
                    + " and Longitude: " + longtSaved + " == " + longitude);

            long lat1 = 0, longt1 = 0, lat2 = 0, longt2 = 0;

            if (!latSaved.isEmpty())
                lat1 = Long.parseLong(latSaved);
            if (!longtSaved.isEmpty())
                longt1 = Long.parseLong(longtSaved);
            if (!latitude.isEmpty())
                lat2 = Long.parseLong(latitude);
            if (!longitude.isEmpty())
                longt2 = Long.parseLong(longitude);

            if (lat1 != lat2 && longt1 != longt2) {

                tempSfa2.edit().clear().apply();
                tempSfa2.edit().putString(context.getString(R.string.lat_key), latitude).apply();
                tempSfa2.edit().putString(context.getString(R.string.longi_key), longitude).apply();

                String currentTime = dateFormat.format(Calendar.getInstance().getTime());

                boolean val = salesBeatDb.insertActivityTrackingTableWalkLatLong(currentTime,
                        String.valueOf(locationObj.getLatitude()), String.valueOf(locationObj.getLongitude()),
                        String.valueOf(locationObj.getAccuracy()));

                Log.d(TAG, "Saved in databse status: " + val);
            }
        }
    }

    public void syncLocation() {


        Cursor loc = null;
        //JSONArray pathArrayLocal = new JSONArray();
        List<HashMap> pathArrayLocal = new ArrayList<>();
        if (!locFlag) {

            try {

                loc = salesBeatDb.getAllDataFromActivityTrackingTable();

                if (loc != null && loc.getCount() > 0 && loc.moveToFirst()) {

                    do {

                        HashMap<String, Object> object = new HashMap<>();
                        //JSONObject object = new JSONObject();

                        String time = loc.getString(loc.getColumnIndex(SalesBeatDb.KEY_DATE_A));
                        String lat = loc.getString(loc.getColumnIndex(SalesBeatDb.KEY_WALK_LAT1));
                        String longt = loc.getString(loc.getColumnIndex(SalesBeatDb.KEY_WALK_LONGT1));
                        String accuracy = loc.getString(loc.getColumnIndex(SalesBeatDb.KEY_ACCURACY));

                        object.put("latitude", lat);
                        object.put("longitude", longt);
                        object.put("accuracy", accuracy);
                        object.put("time", time);

                        pathArrayLocal.add(object);

                    } while (loc.moveToNext());

                }

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (loc != null)
                    loc.close();
            }

        }

        try {

            if (pathArrayLocal.size() > 0) {

                String timestamp = eid + "_" + Calendar.getInstance().getTimeInMillis();

                locFlag = true;

//                JSONObject jObjectInput = new JSONObject();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("path", pathArrayLocal);
                hashMap.put("timestamp", timestamp);

                Log.e(TAG, "Emp path json: " + hashMap);

               // JSONObject obj = new JSONObject((Map) pathArrayLocal);

                Call<JsonObject> callSyncLocation = apiIntentface.syncLocation(token,pathArrayLocal);

                // Retrofit
                callSyncLocation.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                        Log.e(TAG, "Response Emp Path: " + response.body());
                        if (response.isSuccessful())
                        {
                            // called when response HTTP status is "200 OK"

                            try {

                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                if(jsonObject.getInt("status")==1)
                                {
                                    //@Umesh 20220916
                                    boolean val = salesBeatDb.deleteAllDataFromActivityTrackingTable();
                                    if (val)
                                        locFlag = false;
                                    Log.e(TAG, "Emp Path deleted status: " + val);
                                }
                                else
                                {
                                    Log.e(TAG, "Emp Path Error: " + jsonObject.getString("message"));
                                }

                            } catch (Exception e) {
                                locFlag = false;
                                e.printStackTrace();
                            }

                        } else {

                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.e(TAG, "Emp Path error: " + response.message() + "   " + response.code());
                            locFlag = false;

                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                        Log.e(TAG, "Path upload failure: " + t.getMessage());

                    }
                });
            }

        } catch (Exception e) {
            locFlag = false;
            e.printStackTrace();
        }
    }

    private void syncBeatVisited() {
        Log.d(TAG, "syncBeatVisited: "+requestQ);
        Cursor beatVisitedCursor = null;
        try {

            if (!requestQ.containsKey("R1")) {

                //get visited beats from db
                beatVisitedCursor = salesBeatDb.getAllVisitedBeat();

                if (beatVisitedCursor != null && beatVisitedCursor.getCount() > 0 && beatVisitedCursor.moveToFirst()) {

//                do {

                    String beatId = beatVisitedCursor.getString(beatVisitedCursor.getColumnIndex("beat_id"));
                    String dId = beatVisitedCursor.getString(beatVisitedCursor.getColumnIndex("distributor_id"));
                    String lat = beatVisitedCursor.getString(beatVisitedCursor.getColumnIndex("beat_visited_lat"));
                    String longt = beatVisitedCursor.getString(beatVisitedCursor.getColumnIndex("beat_visited_longt"));
                    String timeStamp = beatVisitedCursor.getString(beatVisitedCursor.getColumnIndex("beat_visited_time"));
                    String transactionId = beatVisitedCursor.getString(beatVisitedCursor.getColumnIndex("transactionId"));

//                    if (!requestQ.containsKey("R1")) {

                    requestQ.put("R1", "added");
                    markEmpVisitedBeat(dId, beatId, lat, longt, timeStamp, transactionId);
//                    }


//                } while (beatVisitedCursor.moveToNext());
                }

            }


        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (beatVisitedCursor != null)
                beatVisitedCursor.close();
        }

    }


    private void markEmpVisitedBeat(final String distrebutorId, final String beatId, final String lat, final String longt,
                                    final String checkInTimeStamp, final String transactionId) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("did", Integer.parseInt(distrebutorId));
        params.put("bid", Integer.parseInt(beatId));
        params.put("latitude", lat);
        params.put("longitude", longt);
        //params.put("visitedAt", checkInTimeStamp);
        params.put("transactionId", transactionId);

        Call<JsonObject> callMarkEmpVisitedBeat = apiIntentface.markEmployeeVisitedBeat(token, params);

        // Retrofit
        callMarkEmpVisitedBeat.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                Log.e(TAG, "Response emp beat visited: " + response.body());
                if (response.isSuccessful())
                {
                    // called when response HTTP status is "200 OK"
                    try {

                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().toString());

                        //String status = jsonObject.getString("status");
                        //@Umesh 02-Feb-2022
                        if(jsonObject.getInt("status")==1)
                        {
                            boolean val = salesBeatDb.updateBeatVisited(beatId, distrebutorId, "success");
                            if (val)
                                requestQ.remove("R1");
                        }
                        else
                        {
                            Log.e(TAG, "Beat visit Error: " + jsonObject.getString("message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    handleError3(response.code(), response.message(), "employeeBeatVisit", "R1");

                    try {

                        if (response.code() == 500)
                        {

                            JSONObject paramss = new JSONObject();
                            paramss.put("did", distrebutorId);
                            paramss.put("bid", beatId);
                            paramss.put("latitude", lat);
                            paramss.put("longitude", longt);
                            paramss.put("visitedAt", checkInTimeStamp);
                            paramss.put("transactionId", transactionId);

                            submitError(paramss, SbAppConstants.API_EMP_BEAT_VISIT, response.code(),
                                    "R1", beatId, distrebutorId, "", "", "", "");

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Beat visit Error: " + t.getMessage());
            }
        });

        /*
        RequestParams params = new RequestParams();
        params.add("did", distrebutorId);
        params.add("bid", beatId);
        params.add("latitude", lat);
        params.add("longitude", longt);
        params.add("visitedAt", checkInTimeStamp);
        params.add("transactionId", transactionId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0, DEFAULT_TIMEOUT);
        client.addHeader("authorization", prefSFA.getString("token", ""));
        client.post(SbAppConstants.API_EMP_BEAT_VISIT, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        Log.e(TAG, "RESPONSE EMP BEAT VISITED==> " + res);

                        try {

                            JSONObject object = new JSONObject(res);
                            String status = object.getString("status");
                            if (status.equalsIgnoreCase("success")) {

                                boolean val = salesBeatDb.updateBeatVisited(beatId, distrebutorId, "success");
                                if (val)
                                    requestQ.remove("R1");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {

                        handleError3(statusCode, t.getMessage(), "employeeBeatVisit", "R1");

                        try {

                            if (statusCode == 500) {

                                JSONObject paramss = new JSONObject();
                                paramss.put("did", distrebutorId);
                                paramss.put("bid", beatId);
                                paramss.put("latitude", lat);
                                paramss.put("longitude", longt);
                                paramss.put("visitedAt", checkInTimeStamp);
                                paramss.put("transactionId", transactionId);

                                submitError(paramss, SbAppConstants.API_EMP_BEAT_VISIT, statusCode,
                                        "R1", beatId, distrebutorId, "", "", "", "");

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public boolean getUseSynchronousMode() {
                        return false;
                    }
                }
        );

         */
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getNewlyAddedRetailers() {
        Log.d(TAG, "getNewlyAddedRetailers: "+requestQ);
        if (!requestQ.containsKey("R2")) {
            Cursor newRetailerCursor = null;
            try {

                //submit new  retailers to server
                newRetailerCursor = salesBeatDb.getAllDataFromNewRetailerListTable();
                if (newRetailerCursor != null && newRetailerCursor.getCount() > 0 && newRetailerCursor.moveToFirst()) {

                    do {
                        String new_rid = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("nrid"));
                        String did = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("distributor_id"));
                        String shop_name = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_name"));
                        String shop_address = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_address"));
                        String shop_phone = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_phone"));
                        String owner_name = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_owner_name"));
                        String owner_mobile_no = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_owner_phone"));
                        String whatsappNo = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_whatsapp_no"));
                        String lat = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_latitude"));
                        String longt = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_longtitude"));
                        String state = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_state"));
                        String zone = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_zone"));
                        String locality = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_locality"));
                        String district = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_district"));
                        String pincode = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_pin"));
                        String email_id = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_email"));
                        String gstin = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_gstin"));
                        String target = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_target"));
                        String fssai_no = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_fssai"));
                        String grade = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_grade"));
                        String outlet_channel = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_outletchannel"));
                        String shop_type = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_type"));
                        String owner_image = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_owner_image"));
                        String image_time_stamp = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("image_time_stamp"));
                        String bid = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("beat_id"));
                        String shop_image1 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image1"));
                        String shop_image2 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image2"));
                        String shop_image3 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image3"));
                        String shop_image4 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image4"));
                        //String shop_image5 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image5"));
                        String transactionId = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("transactionId"));

                        requestQ.put("R2", "added");
                        syncNewlyRetailerToServer(new_rid, did, shop_name, shop_address, shop_phone,
                                owner_name, owner_mobile_no, whatsappNo, lat, longt, state, zone,
                                locality, district, pincode, email_id, gstin, target, fssai_no,
                                grade, outlet_channel, shop_type, owner_image, image_time_stamp,
                                shop_image1, shop_image2, shop_image3, shop_image4, bid, transactionId);

                    } while (newRetailerCursor.moveToNext());
                }
            } catch (Exception e) {
                e.printStackTrace();
                requestQ.remove("R2");
            } finally {

                if (newRetailerCursor != null)
                    newRetailerCursor.close();
            }

        }
    }

    //function to upload  new retailer data to server
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void syncNewlyRetailerToServer(final String tempRid, String did, final String shop_name,
                                           final String shop_address, final String shop_phone,
                                           final String owner_name, final String owner_mobile_no,
                                           final String whatsAppNo, final String lat, final String longt,
                                           final String state, final String zone, final String locality,
                                           final String district, final String pincode, final String email_id,
                                           final String gstin, final String target, final String fssai_no,
                                           final String grade, final String outlet_channel, final String shop_type,
                                           final String ownerImage, final String imageTimeStamp, final String shopImage1,
                                           final String shopImage2, final String shopImage3, final String shopImage4,
                                           final String beat_id, String transactionId) {
        Log.d(TAG, "sync RetailerToServer");

//        final JSONObject jObjectInput = new JSONObject();
//        final HashMap<String, Object> newRetailer = new HashMap<>();

//        try {
//
//            newRetailer.put("shop_name", shop_name);
//            newRetailer.put("shop_address", shop_address);
//
//            if (!shop_phone.isEmpty())
//                newRetailer.put("shop_phone", shop_phone);
//
//            newRetailer.put("owner_name", owner_name);
//
//            if (!owner_mobile_no.isEmpty())
//                newRetailer.put("owner_mobile_no", owner_mobile_no);
//
//            if (!whatsAppNo.isEmpty())
//                newRetailer.put("whatsappNo", whatsAppNo);
//
//            newRetailer.put("state", state);
//            newRetailer.put("zone", zone);
//            newRetailer.put("locality", locality);
//            newRetailer.put("district", district);
//            newRetailer.put("pincode", pincode);
//
//            if (!email_id.isEmpty())
//                newRetailer.put("email_id", email_id);
//
//            newRetailer.put("gstin", gstin);
//            newRetailer.put("target", target);
//            newRetailer.put("fssai_no", fssai_no);
//            newRetailer.put("grade", grade);
//            newRetailer.put("outlet_channel", outlet_channel);
//            newRetailer.put("shop_type", shop_type);
//            newRetailer.put("image_time_stamp", imageTimeStamp);
//            newRetailer.put("latitude", lat);
//            newRetailer.put("longitude", longt);
//            newRetailer.put("bid", beat_id);
//            newRetailer.put("addedOn", imageTimeStamp);
//            newRetailer.put("transactionId", transactionId);
//
//
//            Log.e(TAG, "New Retailer Json: " + newRetailer.toString());
//
//            if (ownerImage != null && !ownerImage.isEmpty() && compressImage(ownerImage) != null)
//                newRetailer.put("image", getStringImage(compressImage(ownerImage)));
//
//            if (shopImage1 != null && !shopImage1.isEmpty() && compressImage(shopImage1) != null)
//                newRetailer.put("shop_image[0]", getStringImage(compressImage(shopImage1)));
//
//            if (shopImage2 != null && !shopImage2.isEmpty() && compressImage(shopImage2) != null)
//                newRetailer.put("shop_image[1]", getStringImage(compressImage(shopImage2)));
//
//            if (shopImage3 != null && !shopImage3.isEmpty() && compressImage(shopImage3) != null)
//                newRetailer.put("shop_image[2]", getStringImage(compressImage(shopImage3)));
//
//            if (shopImage4 != null && !shopImage4.isEmpty() && compressImage(shopImage4) != null)
//                newRetailer.put("shop_image[3]", getStringImage(compressImage(shopImage4)));
//
//            Call newretailerCall = apiIntentface.addNewRetailerToServer(token, newRetailer);
//
//            // Retrofit
//            newretailerCall.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                    Log.e(TAG, "Response add New Retailer: " + response.body());
//
//                    if (response.isSuccessful()) {
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response.body().toString());
//
//                            String nrid = jsonObject.getString("rid");
//                            String status = jsonObject.getString("status");
//
//                            if (status.equalsIgnoreCase("success")) {
//
//                                boolean val1 = salesBeatDb.updateNewRetailerListTable(tempRid, nrid, "success");
//                                Log.e(TAG, "New Retailer: " + val1);
//                                if (val1) {
//                                    requestQ.remove("R2");
//                                    deleteLocalFile(ownerImage, shopImage1, shopImage2, shopImage3, shopImage4);
//                                }
//
//                            } else {
//
//                                boolean val1 = salesBeatDb.updateNewRetailerListTableIfError(tempRid,
//                                        did, "error", String.valueOf(response.code()),
//                                        response.message());
//                                if (val1)
//                                    requestQ.remove("R2");
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            boolean val1 = salesBeatDb.updateNewRetailerListTableIfError(tempRid,
//                                    did, "error", String.valueOf(response.code()),
//                                    response.message());
//                            if (val1)
//                                requestQ.remove("R2");
//                            SbLog.printError(TAG, "addNewRetailer", String.valueOf(e.getMessage()), e.getMessage(),
//                                    eid);
//                        }
//                    } else {
//                        handleError3(response.code(), response.message(), "addRetailer", "R2");
//                        boolean val1 = salesBeatDb.updateNewRetailerListTableIfError(tempRid,
//                                did, "error", String.valueOf(response.code()),
//                                response.message());
//                        if (val1)
//                            requestQ.remove("R2");
//                        SbLog.printError(TAG, "addNewRetailer", String.valueOf(response.code()),
//                                String.valueOf(response.errorBody()), eid);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    Log.e(TAG, "Add new retailer error: " + t.getMessage());
//                    boolean val1 = salesBeatDb.updateNewRetailerListTableIfError(tempRid,
//                            did, "error", String.valueOf(t.hashCode()),
//                            t.getMessage());
//                    if (val1)
//                        requestQ.remove("R2");
//                    SbLog.printError(TAG, "addNewRetailer", String.valueOf(t.getMessage()),
//                            t.getMessage(), eid);
//                }
//            });
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            boolean val1 = salesBeatDb.updateNewRetailerListTableIfError(tempRid,
//                    did, "error", "excep",
//                    e.getMessage());
//            if (val1)
//                requestQ.remove("R2");
//        }


        try {

            JSONObject saveNewRetailerDetailsParams = new JSONObject();
            //@Umesh
            saveNewRetailerDetailsParams.put("name", shop_name);
            saveNewRetailerDetailsParams.put("address", shop_address);

            if (!shop_phone.isEmpty())
                saveNewRetailerDetailsParams.put("shopPhone", shop_phone);

            saveNewRetailerDetailsParams.put("ownersName", owner_name);

            if (!owner_mobile_no.isEmpty())
                saveNewRetailerDetailsParams.put("ownersPhone1", owner_mobile_no);

            if (!whatsAppNo.isEmpty())
                saveNewRetailerDetailsParams.put("whatsappNo", whatsAppNo);

            saveNewRetailerDetailsParams.put("state", state);
           // saveNewRetailerDetailsParams.put("zone", zone);
            saveNewRetailerDetailsParams.put("locality", locality);
            saveNewRetailerDetailsParams.put("district", district);
            saveNewRetailerDetailsParams.put("pin", pincode);

            if (!email_id.isEmpty())
                saveNewRetailerDetailsParams.put("email", email_id);

            saveNewRetailerDetailsParams.put("gstin", gstin);
            saveNewRetailerDetailsParams.put("target", target);
            saveNewRetailerDetailsParams.put("fssai", fssai_no);
            saveNewRetailerDetailsParams.put("grade", grade);
            saveNewRetailerDetailsParams.put("outletChannel", outlet_channel);
            saveNewRetailerDetailsParams.put("shopType", shop_type);
            //saveNewRetailerDetailsParams.put("image_time_stamp", imageTimeStamp);
            saveNewRetailerDetailsParams.put("latitude", lat);
            saveNewRetailerDetailsParams.put("longitude", longt);
            saveNewRetailerDetailsParams.put("bid", Integer.valueOf(beat_id));
//            saveNewRetailerDetailsParams.put("addedOn", imageTimeStamp);
           // saveNewRetailerDetailsParams.put("addedOnstr", imageTimeStamp);
            saveNewRetailerDetailsParams.put("transactionId", transactionId);


//            Log.e(TAG, "New Retailer Json: " + saveNewRetailerDetailsParams.toString());
            Log.e(TAG, "New Retailer Json: " + new Gson().toJson(saveNewRetailerDetailsParams));

            File compressedOwnerImage = (ownerImage != null && !ownerImage.isEmpty()) ? compressImage(ownerImage) : null;
            if (compressedOwnerImage != null) {
                saveNewRetailerDetailsParams.put("image", ImageUtils.getBase64FromFile(compressedOwnerImage));
            }

            File compressedShopImage1 = (shopImage1 != null && !shopImage1.isEmpty()) ? compressImage(shopImage1) : null;
            if (compressedShopImage1 != null) {
                saveNewRetailerDetailsParams.put("ShopImage0", ImageUtils.getBase64FromFile(compressedShopImage1));
            }

            File compressedShopImage2 = (shopImage2 != null && !shopImage2.isEmpty()) ? compressImage(shopImage2) : null;
            if (compressedShopImage2 != null) {
                saveNewRetailerDetailsParams.put("ShopImage1", ImageUtils.getBase64FromFile(compressedShopImage2));
            }

            File compressedShopImage3 = (shopImage3 != null && !shopImage3.isEmpty()) ? compressImage(shopImage3) : null;
            if (compressedShopImage3 != null) {
                saveNewRetailerDetailsParams.put("ShopImage2", ImageUtils.getBase64FromFile(compressedShopImage3));
            }

            File compressedShopImage4 = (shopImage4 != null && !shopImage4.isEmpty()) ? compressImage(shopImage4) : null;
            if (compressedShopImage4 != null) {
                saveNewRetailerDetailsParams.put("ShopImage3", ImageUtils.getBase64FromFile(compressedShopImage4));
            }

            Log.d(TAG, "syncNewlyRetailerToServer: "+new Gson().toJson(saveNewRetailerDetailsParams));
            JsonObjectRequest saveNewRetailerDetailsRequest = new JsonObjectRequest(Request.Method.POST,
                    SbAppConstants.ADD_NEW_RETAILER, saveNewRetailerDetailsParams, response -> {

                Log.e(TAG, "New Retailer Response: " + response.toString());


                if (!response.isNull("status")
                        && response.has("status")) {
                    try {
                        //@Umesh 23-Feb-2022
                        if(response.getInt("status")==1)
                        {
                            JSONObject data = response.getJSONObject("data");
                            String nrid = data.getString("rid");
                            boolean val1 = salesBeatDb.updateNewRetailerListTable(tempRid, nrid, "success");
                            Log.e(TAG, "New Retailer: " + val1);
                            if (val1) {
                                requestQ.remove("R2");
                                deleteLocalFile(ownerImage, shopImage1, shopImage2, shopImage3, shopImage4);
                            }
                        }
                        else
                        {
                            SbLog.printError("Add New Retailer",
                                    "addRetailer", "Not available",
                                    "" + response.getString("message"), eid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    requestQ.remove("R2");
                    try {
                        SbLog.printError("Add New Retailer",
                                "addRetailer", "Not available",
                                "" + response.toString(), eid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }, error -> {
                Log.e(TAG,"New Ret Error:"+error.toString());
                requestQ.remove("R2");
                if(error!=null && error.networkResponse!=null) //@Umesh 20221007
                {
                    boolean val1 = salesBeatDb.updateNewRetailerListTableIfError(tempRid,
                            did, "error", String.valueOf(error.networkResponse.statusCode),
                            "" + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("authorization", token);
                    return headers;
                }
            };

            saveNewRetailerDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );

            Volley.newRequestQueue(context).add(saveNewRetailerDetailsRequest);


        } catch (Exception e) {
            e.printStackTrace();
            requestQ.remove("R2");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getExistingRetailersNonProductiveCalls() {
        Log.d(TAG, "getExistingRetailersNonProductiveCalls: "+requestQ);
        if (!requestQ.containsKey("R3")) {

            Cursor noOrderC = null;
            try {

                //submit no orders from existing retailers to server
                noOrderC = salesBeatDb.getNonProductiveOrdersFromOderPlacedByRetailersTable("no order");
                if (noOrderC != null && noOrderC.getCount() > 0 && noOrderC.moveToFirst()) {

                    do {
                        String rid = noOrderC.getString(noOrderC.getColumnIndex("rid"));
                        String did = noOrderC.getString(noOrderC.getColumnIndex("did"));
                        String checkIn = noOrderC.getString(noOrderC.getColumnIndex("check_in_time"));
                        String checkOut = noOrderC.getString(noOrderC.getColumnIndex("check_out_time"));
                        String lat = noOrderC.getString(noOrderC.getColumnIndex("order_lat"));
                        //String orderType = noOrderC.getString(noOrderC.getColumnIndex("order_type"));
                        String longt = noOrderC.getString(noOrderC.getColumnIndex("order_long"));
                        String reason = noOrderC.getString(noOrderC.getColumnIndex("order_comment"));
                        String transactionId = noOrderC.getString(noOrderC.getColumnIndex("transactionId"));

                        requestQ.put("R3", "added");
                        syncExistingRetailersNonProductiveCall(rid, did, checkIn, checkOut,
                                lat, longt, reason, transactionId);

                    } while (noOrderC.moveToNext());
                }

            } catch (Exception e) {
                e.printStackTrace();
                requestQ.remove("R3");
            } finally {

                if (noOrderC != null)
                    noOrderC.close();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void syncExistingRetailersNonProductiveCall(final String rid, final String did, String checkIn,
                                                        String checkOut, String lat, String longt, String reason,
                                                        String transactionId) {

//        HashMap<String, Object> orderHash = new HashMap<>();
//        final JSONObject orders = new JSONObject();
//        try {
//
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("rid", rid);
//            hashMap.put("did", did);
//            hashMap.put("checkIn", checkIn);
//            hashMap.put("checkOut", checkOut);
//            hashMap.put("latitude", lat);
//            hashMap.put("longitude", longt);
//            hashMap.put("comments", reason);
//            List<HashMap> retailerOutCalls = new ArrayList<>();
//            retailerOutCalls.add(hashMap);
//
//            orders.put("retailerCalls", retailerOutCalls);
//            orders.put("transactionId", transactionId);
//            orderHash.put("retailerCalls", retailerOutCalls);
//            orderHash.put("transactionId", transactionId);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        Log.e(TAG, "No order from Existing retailer Json: " + orders.toString());
//
//
//        Call<JsonObject> callNoOrder = apiIntentface.submitOrder(
//                token,
//                orderHash
//        );
//
//        // Retrofit
//        callNoOrder.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                Log.e(TAG, "Response NO Order existing retailer: " + response.body());
//
//                if (response.isSuccessful()) {
//
//                    boolean val = salesBeatDb.updateInOderPlacedByRetailersTable2(rid, "success", did);
//                    if (val)
//                        requestQ.remove("R3");
//
//                } else {
//
//                    handleError3(response.code(), response.message(),
//                            "submitOrders", "R3");
//
//                    boolean val = salesBeatDb.updateInOderPlacedByRetailersTable22(rid, did,
//                            "error", String.valueOf(response.code()), response.message());
//                    if (val)
//                        requestQ.remove("R3");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Log.e(TAG, "no order error");
//                boolean val = salesBeatDb.updateInOderPlacedByRetailersTable22(rid, did,
//                        "error", String.valueOf(t.hashCode()), t.getMessage());
//                if (val)
//                    requestQ.remove("R3");
//            }
//        });


        try {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("rid", Integer.parseInt(rid));
            hashMap.put("did", Integer.parseInt(did));
//            hashMap.put("checkIn", checkIn);
//            hashMap.put("checkOut", checkOut);
            hashMap.put("checkInstr", checkIn);//Umesh
            hashMap.put("checkOutstr", checkOut); //Umesh
            hashMap.put("latitude", lat);
            hashMap.put("longitude", longt);
            hashMap.put("comments", reason);

            List<HashMap> retailerOutCalls = new ArrayList<>();
            retailerOutCalls.add(hashMap);

            HashMap<String, Object> orderHash = new HashMap<>();
            orderHash.put("orders", new ArrayList<>());
            orderHash.put("retailerCalls", retailerOutCalls);
            orderHash.put("transactionId", transactionId);

            Log.e(TAG, "No order from Existing retailer Json: " + orderHash.toString());

            JsonObjectRequest nonProductiveCallRequest = new JsonObjectRequest(Request.Method.POST,
                    SbAppConstants.SUBMIT_ORDER, new JSONObject(orderHash), response -> {

                Log.e(TAG, "Existing Retailer Non Productive call Response: " + response.toString());

                try {
                    //@Umesh 23-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        boolean val = salesBeatDb.updateInOderPlacedByRetailersTable2
                                (rid, "success", did);
                        if (val)
                            requestQ.remove("R3");
                    } else {

                        requestQ.remove("R3");
                        try {
                           /* SbLog.printError("Existing Retailer Non Productive Call",
                                    "submitOrders", "Not available",
                                    "" + response.toString(), eid);*/

                            Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }, error -> {
                error.printStackTrace();
                requestQ.remove("R3");
                boolean val = salesBeatDb.updateInOderPlacedByRetailersTable22(rid, did,
                        "error", String.valueOf(error.networkResponse.statusCode),
                        "" + error.getMessage());
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("authorization", token);
                    return headers;
                }
            };

            nonProductiveCallRequest.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );

            Volley.newRequestQueue(context).add(nonProductiveCallRequest);

        } catch (Exception e) {
            e.printStackTrace();
            requestQ.remove("R3");
        }


    }


    private void getExistingRetailersProductiveCalls() {
        Log.d(TAG, "getExistingRetailersProductiveCalls: "+requestQ);
        if (!requestQ.containsKey("R4")) {

            Cursor orderPlacedByRetailers = null;
            try {

                //submitting existing retailars  orders to server
                orderPlacedByRetailers =
                        salesBeatDb.getProductiveOrdersFromOderPlacedByRetailersTable("no order");

                if (orderPlacedByRetailers != null && orderPlacedByRetailers.getCount() > 0
                        && orderPlacedByRetailers.moveToFirst()) {

                    do {

                        String rid = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("rid"));
                        String did = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("did"));
                        String orderTakenAt = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("check_in_time"));
                        String orderType = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_type"));
                        String checkIn = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("check_in_time"));
                        String checkOutTime = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("check_out_time"));
                        String lat = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_lat"));
                        String longt = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_long"));
                        String transactionId = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("transactionId"));
                        String reason = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_comment"));

//                        if (orderType.contains("cancelled")) {
//
//                            orderType = "onShop";
//                        }
                        requestQ.put("R4", "added");
                        syncExistingRetailersProductiveCall(rid, did, orderTakenAt, orderType, checkIn,
                                checkOutTime, lat, longt,/* prevRid, prevCheckInT, */transactionId,reason);

                    } while (orderPlacedByRetailers.moveToNext());
                }

            } catch (Exception e) {
                e.printStackTrace();
                requestQ.remove("R4");
            } finally {

                if (orderPlacedByRetailers != null)
                    orderPlacedByRetailers.close();
            }

        }
    }

    private void syncExistingRetailersProductiveCall(final String ridSaved, final String did,
                                                     String taken_at, String ordertype, final String checkInT,
                                                     String checkOutT, String lat, String longt, String transactionId,String reason) {
//
//        List<HashMap> catalog = new ArrayList<>();
//        Cursor catalogList = null;
//
//        try {
//            catalogList = salesBeatDb.getSpecificDataFromOrderEntryListTable(ridSaved, did/*, "fail"*/);
//            if (catalogList != null && catalogList.getCount() > 0 && catalogList.moveToFirst()) {
//
//                do {
//
//                    HashMap<String, Object> item = new HashMap<>();
//
//                    item.put("skuid", catalogList.getString(catalogList.getColumnIndex("sku_id")));
//                    item.put("qty", catalogList.getString(catalogList.getColumnIndex("brand_qty")));
//                    catalog.add(item);
//
//                } while (catalogList.moveToNext());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (catalogList != null)
//                catalogList.close();
//        }
//
//        if (catalog.size() > 0) {
//
//            HashMap<String, Object> params = new HashMap<>();
//            HashMap<String, Object> orderArrayItem = new HashMap<>();
//
//            try {
//
//                orderArrayItem.put("cid", cid);
//                orderArrayItem.put("rid", ridSaved);
//                orderArrayItem.put("orderType", ordertype);
//                orderArrayItem.put("eid", eid);
//                orderArrayItem.put("did", did);
//                orderArrayItem.put("takenAt", taken_at);
//                orderArrayItem.put("latitude", lat);
//                orderArrayItem.put("longitude", longt);
//                orderArrayItem.put("catalogue", catalog);
//
////                JSONArray orders = new JSONArray();
//                List<HashMap> orders = new ArrayList<>();
//                orders.add(orderArrayItem);
//
//                HashMap<String, Object> retailerCallsArrayItem = new HashMap<>();
////                JSONObject retailerCallsArrayItem = new JSONObject();
//                retailerCallsArrayItem.put("rid", ridSaved);
//                retailerCallsArrayItem.put("did", did);
//
//                retailerCallsArrayItem.put("checkIn", checkInT);
//                retailerCallsArrayItem.put("checkOut", checkOutT);
//                retailerCallsArrayItem.put("latitude", lat);
//                retailerCallsArrayItem.put("longitude", longt);
//                retailerCallsArrayItem.put("comments", "");
//
//                List<HashMap> retailerCalls = new ArrayList<>();
////                JSONArray retailerCalls = new JSONArray();
//                retailerCalls.add(retailerCallsArrayItem);
//
//                params.put("orders", orders);
//                params.put("transactionId", transactionId);
//                params.put("retailerCalls", retailerCalls);
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            Log.e(TAG, "Existing Retailer Order json: " + params.toString());
//
//            try {
//
//                Call<JsonObject> callSubmitOrder = apiIntentface.submitOrder(token, params);
//
//                // Retrofit
//                callSubmitOrder.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                        Log.e(TAG, "Response Existing retailer order: " + response.isSuccessful());
//
//                        if (response.isSuccessful()) {
//
//                            boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable2(ridSaved, "success", did);
//                            if (val2)
//                                requestQ.remove("R4");
//
//
//                        } else {
//
//                            Log.e(TAG, "Response Existing retailer Error:" + response.message());
//
////                            salesBeatDb.insertInPendingOrders(params,response.code(),response.message());
//
//                            boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable22(ridSaved, did,
//                                    "error", String.valueOf(response.code()), response.message());
//                            if (val2)
//                                requestQ.remove("R4");
//
//                            handleError3(response.code(), response.message(), "submitOrders", "R4");
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//
//                        boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable22(ridSaved, did,
//                                "error", String.valueOf(t.hashCode()), t.getMessage());
//                        if (val2)
//                            requestQ.remove("R4");
//
//                        Log.e(TAG, "Existing retailer order error: " + t.getMessage());
//                    }
//                });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        JSONArray catalog = new JSONArray();
        Cursor catalogList = null;

        try {
            catalogList = salesBeatDb.getSpecificDataFromOrderEntryListTable(ridSaved, did/*, "fail"*/);
            if (catalogList != null && catalogList.getCount() > 0 && catalogList.moveToFirst()) {

                do {

                    JSONObject item = new JSONObject();

                    item.put("skuid", catalogList.getString(catalogList.getColumnIndex("sku_id")));
                    item.put("qty", catalogList.getString(catalogList.getColumnIndex("brand_qty")));
                    catalog.put(item);

                } while (catalogList.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (catalogList != null)
                catalogList.close();
        }

        if (catalog.length() > 0) {


            try {

                JSONObject orderArrayItem = new JSONObject();
                orderArrayItem.put("cid", Integer.valueOf(cid));
                orderArrayItem.put("rid",Integer.valueOf(ridSaved) );
                orderArrayItem.put("orderType", ordertype);
                orderArrayItem.put("eid",Integer.valueOf(eid));
                orderArrayItem.put("did",Integer.valueOf(did));
//                orderArrayItem.put("takenAt", taken_at);
                orderArrayItem.put("takenAtStr",taken_at); //Umesh
                orderArrayItem.put("latitude", lat);
                orderArrayItem.put("longitude", longt);
                //orderArrayItem.put("catalogue", catalog);
                orderArrayItem.put("orderCatalogue", catalog); //@Umesh

//                JSONArray orders = new JSONArray();
                JSONArray orders = new JSONArray();
                orders.put(orderArrayItem);

                // HashMap<String, Object> retailerCallsArrayItem = new HashMap<>();
                JSONObject retailerCallsArrayItem = new JSONObject();
                retailerCallsArrayItem.put("rid",Integer.valueOf(ridSaved));
                retailerCallsArrayItem.put("did",Integer.valueOf(did));
                retailerCallsArrayItem.put("eid", Integer.valueOf(prefSFA.getString(context.getString(R.string.emp_id_key), "")));
//                retailerCallsArrayItem.put("checkIn", checkInT);
//                retailerCallsArrayItem.put("checkOut", checkOutT);
                retailerCallsArrayItem.put("checkInstr",checkInT); //Umesh
                retailerCallsArrayItem.put("checkOutstr",checkOutT); //Umesh
                retailerCallsArrayItem.put("latitude", lat);
                retailerCallsArrayItem.put("longitude", longt);
                retailerCallsArrayItem.put("comments", reason);

//                List<HashMap> retailerCalls = new ArrayList<>();
                JSONArray retailerCalls = new JSONArray();
                retailerCalls.put(retailerCallsArrayItem);

                JSONObject productiveCallParams = new JSONObject();
                productiveCallParams.put("orders", orders);
                productiveCallParams.put("transactionId", transactionId);
                productiveCallParams.put("retailerCalls", retailerCalls);

//                Log.e(TAG, "Existing Retailer Productive call json: " + productiveCallParams.toString());
                Log.e(TAG, "Existing Retailer Productive call json service: " +new Gson().toJson(productiveCallParams));


                JsonObjectRequest productiveCallRequest = new JsonObjectRequest(Request.Method.POST,
                        SbAppConstants.SUBMIT_ORDER, productiveCallParams, response -> {
                    try {

//                    Log.e(TAG,"TOKEN:"+token);
                        Log.e(TAG,"URL:"+SbAppConstants.SUBMIT_ORDER);
                        Log.e(TAG, "Existing Retailer Productive call Response: " + response);

                        //@Umesh 02-Feb-2022
                        if(response.getInt("status")==1) {
                            boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable2(
                                    ridSaved, "success", did);
                            if (val2)
                                requestQ.remove("R4");

                        } else {
                            requestQ.remove("R4");
                            try {
                                /*SbLog.printError("Existing Retailer Productive Call",
                                        "submitOrders", "Not available",
                                        "" + response.toString(), eid);*/
                                Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch (Exception ex)
                    {

                    }

                }, error -> {
                    requestQ.remove("R4");
                    boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable22(ridSaved, did,
                            "error", String.valueOf(error.networkResponse.statusCode),
                            "" + error.getMessage());
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        headers.put("authorization", token);
                        return headers;
                    }
                };

                productiveCallRequest.setRetryPolicy(new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );

                Volley.newRequestQueue(context).add(productiveCallRequest);

            } catch (Exception e) {
                requestQ.remove("R4");
                e.printStackTrace();
            }
        } else {
            requestQ.remove("R4");
        }
    }


    private void syncExistingRetailersCanceledOrders() {

        Cursor orderCancelledByRetailers = null;
        try {

            if (!requestQ.containsKey("R5")) {

                //submit cancel order request to server
                orderCancelledByRetailers = salesBeatDb.getRetailersFromOderPlacedByRetailersTable3("cancelled", "fail");
                if (orderCancelledByRetailers != null && orderCancelledByRetailers.getCount() > 0
                        && orderCancelledByRetailers.moveToFirst()) {

//                do {

                    String rid = orderCancelledByRetailers.getString(orderCancelledByRetailers.getColumnIndex("rid"));
                    String did = orderCancelledByRetailers.getString(orderCancelledByRetailers.getColumnIndex("did"));
                    String reason = orderCancelledByRetailers.getString(orderCancelledByRetailers.getColumnIndex("order_comment"));
                    String transactionId = orderCancelledByRetailers.getString(orderCancelledByRetailers.getColumnIndex("transactionId"));

//                    if (!requestQ.containsKey("R5")) {

                    requestQ.put("R5", "added");
                    cancelOrderRequest(rid, did, reason, transactionId);
//                    }

//                } while (orderCancelledByRetailers.moveToNext());

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (orderCancelledByRetailers != null)
                orderCancelledByRetailers.close();
        }
    }


    private void cancelOrderRequest(final String rid, final String did, final String reason,
                                    final String transactionId)
            throws JSONException {

        /*HashMap<String, Object> params = new HashMap<>();
//        JSONObject params = new JSONObject();
        params.put("rid", Integer.valueOf(rid));
        params.put("did", Integer.valueOf(did));
        params.put("reason", reason);
        params.put("transactionId", transactionId);
*/





        //@Umesh
        List<HashMap> catalog = new ArrayList<>();
        Cursor catalogList = null;

        try {
            catalogList = salesBeatDb.getSpecificDataFromOrderEntryListTable(rid, did/*, "fail"*/);
            if (catalogList != null && catalogList.getCount() > 0 && catalogList.moveToFirst()) {

                do {
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("skuid", catalogList.getString(catalogList.getColumnIndex("sku_id")));
                    item.put("qty", catalogList.getString(catalogList.getColumnIndex("brand_qty")));
                    catalog.add(item);

                } while (catalogList.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (catalogList != null)
                catalogList.close();
        }

        HashMap<String, Object> orderArrayItem = new HashMap<>();
        orderArrayItem.put("cid", Integer.valueOf(cid));
        orderArrayItem.put("rid",Integer.valueOf(rid) );
        orderArrayItem.put("orderType", "cancelled");
        orderArrayItem.put("eid",Integer.valueOf(eid));
        orderArrayItem.put("did",Integer.valueOf(did));
//                orderArrayItem.put("takenAt", taken_at);
        //orderArrayItem.put("takenAtStr",taken_at); //Umesh
        //orderArrayItem.put("latitude", lat);
        //orderArrayItem.put("longitude", longt);
        //orderArrayItem.put("catalogue", catalog);
        orderArrayItem.put("orderCatalogue", catalog); //@Umesh

//                JSONArray orders = new JSONArray();
        List<HashMap> orders = new ArrayList<>();
        orders.add(orderArrayItem);




        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("rid", Integer.parseInt(rid));
        hashMap.put("did", Integer.parseInt(did));
        hashMap.put("eid", Integer.valueOf(prefSFA.getString(context.getString(R.string.emp_id_key), "")));
//            hashMap.put("checkIn", checkIn);
//            hashMap.put("checkOut", checkOut);
        //hashMap.put("checkInstr", checkIn);//Umesh
        //hashMap.put("checkOutstr", checkOut); //Umesh
        //hashMap.put("latitude", lat);
        //hashMap.put("longitude", longt);
        hashMap.put("comments", reason);

        List<HashMap> retailerOutCalls = new ArrayList<>();
        retailerOutCalls.add(hashMap);

        HashMap<String, Object> orderHash = new HashMap<>();
        orderHash.put("orders", orders);
        orderHash.put("retailerCalls", retailerOutCalls);
        orderHash.put("transactionId", transactionId);




        Call<JsonObject> callCancelOrder = apiIntentface.submitOrder(token, orderHash);

        // Retrofit
        callCancelOrder.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                Log.e(TAG, "Response canceled order: " + response.body());
                if (response.isSuccessful())
                {

                    // called when response HTTP status is "200 OK"

                    try {

                        assert response.body() != null;
                        JSONObject jsonObject = new JSONObject(response.body().toString());

                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("1")) {

                            boolean val1 = salesBeatDb.updateInOderPlacedByRetailersTable2(rid, "success", did);
                            if (val1) {
//                                    boolean val2 = salesBeatDb.updateOrderEntryListTable(rid, did, "success");
//                                    if (val2)
                                requestQ.remove("R5");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    //requestQ.remove("R5");
//                        Log.e(TAG, "= cancel order error==" + res + "   " + statusCode);
//                        SbLog.printError(TAG, "cancelOrder", String.valueOf(statusCode), res,
//                                prefSFA.getString(context.getString(R.string.emp_id_key), ""));

                    handleError3(response.code(), response.message(), "cancelOrder", "R5");

                    try {

                        if (response.code() == 500) {

                            JSONObject object = new JSONObject();
                            object.put("rid", rid);
                            object.put("did", did);
                            object.put("reason", reason);
                            object.put("transactionId", transactionId);

                            submitError(object, SbAppConstants.API_TO_CANCEL_ORDER, response.code(),
                                    "R5", rid, did, "", "", "", "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Retailer cancelled order error: " + t.getMessage());
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void syncNewlyAddedDistributors() {

        Cursor newDistributorCursor = null;
        try {
            Log.d(TAG, "syncNewlyAddedDistributors: "+requestQ);
            if (!requestQ.containsKey("R6")) {

                //submitting data from new distributor table
                newDistributorCursor = salesBeatDb.getAllDataFromNewDistributorTable();
                if (newDistributorCursor != null && newDistributorCursor.getCount() > 0 && newDistributorCursor.moveToFirst()) {

//                do {

                    String tempDid = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("new_distributor_id"));
                    String name_of_firm = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("name_of_firm"));
                    String firm_address = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("firm_address"));
                    String pincode = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("pincode"));
                    String city = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("city"));
                    String state = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("state"));
                    String owner_name = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("owner_name"));
                    String owner_mobile_no1 = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("owner_mobile_no1"));
                    String owner_mobile_no2 = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("owner_mobile_no2"));
                    String email_id = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("email_id"));
                    String gstin = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("gstin"));
                    String fssai_no = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("fssai_no"));
                    String pan_no = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("pan_no"));
                    String owner_image = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("owner_image"));//newDistributorDetails.get(13));
                    String owner_image_time_stamp = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("owner_image_time_stamp"));
                    String owner_image_LatLong = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("owner_image_LatLong"));
                    String beat_name = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("beat_name"));
                    String product_division = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("product_division"));
                    String other_contact_person_name = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("other_contact_person_name"));
                    String other_contact_person_phn = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("other_contact_person_phn"));
                    String firm_image = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("firm_image"));//newDistributorDetails.get(31));
                    String firm_image_time_stamp = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("firm_image_time_stamp"));
                    String firm_image_LatLong = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("firm_image_LatLong"));
                    String opinion_about_distributor = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("opinion_about_distributor"));
                    String comment = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("comment"));
                    String transactionId = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("transactionId"));
                    String brandName = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("brand_name"));
                    String recording = newDistributorCursor.getString(newDistributorCursor.getColumnIndex("recording"));

//                    if (!requestQ.containsKey("R6")) {
                    Log.d(TAG, "call service Data");
                    requestQ.put("R6", "added");
                    uploadNewDistributorDetailsToServer(tempDid, name_of_firm, firm_address, pincode, city, state,
                            owner_name, owner_mobile_no1, owner_mobile_no2, email_id, gstin, fssai_no, pan_no, owner_image,
                            owner_image_time_stamp, owner_image_LatLong, beat_name, product_division, other_contact_person_name,
                            other_contact_person_phn, firm_image, firm_image_time_stamp, firm_image_LatLong, opinion_about_distributor,
                            comment, transactionId,brandName,recording);
//                    }

//                } while (newDistributorCursor.moveToNext());

                }

            }

        } catch (Exception e) {
            Log.e(TAG, "===" + e.getMessage());
        } finally {

            if (newDistributorCursor != null)
                newDistributorCursor.close();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadNewDistributorDetailsToServer(final String tempDid, String
            name_of_firm, String firm_address, String pincode,
                                                     String city, String state, String owner_name, String owner_mobile_no1,
                                                     String owner_mobile_no2, String email_id, String gstin, String fssai_no,
                                                     String pan_no, final String owner_image, String owner_image_time_stamp,
                                                     String owner_image_LatLong, String beat_name, String product_division,
                                                     String other_contact_person_name, String other_contact_person_phn,
                                                     final String firm_image, String firm_image_time_stamp, String firm_image_LatLong,
                                                     String opinion_about_distributor, String comment, String transactionId, String brandName,String rec) {
        Log.d(TAG, "upload New Distributor Image: "+owner_image);
        String recData = "";

//        final JSONObject jObjectInput = new JSONObject();
        final HashMap<String, Object> jObjectInput = new HashMap<>();
        try {
            jObjectInput.put("firmName", name_of_firm);
            jObjectInput.put("firmAddress", firm_address);
            jObjectInput.put("pin", Integer.valueOf(pincode));
            jObjectInput.put("city", city);
            jObjectInput.put("state", state);
            jObjectInput.put("ownerName", owner_name);
            if(!owner_mobile_no1.equals("null"))
            jObjectInput.put("ownerMobile1", Long.valueOf(owner_mobile_no1));
            if(!owner_mobile_no2.equals("null") && !owner_mobile_no2.equals(""))
            jObjectInput.put("ownerMobile2", Long.valueOf(owner_mobile_no2));
            jObjectInput.put("email", email_id);
            jObjectInput.put("gstin", gstin);
            jObjectInput.put("fsi", fssai_no);
            jObjectInput.put("pan", pan_no);
            //jObjectInput.put("owner_image_time_stamp",owner_image_time_stamp);
            jObjectInput.put("ownerImageTimeStampStr", owner_image_time_stamp);  //Umesh
            jObjectInput.put("ownerImageTimeStamp", null);  //Umesh
            jObjectInput.put("ownerImageLatLong", owner_image_LatLong);
            jObjectInput.put("beatName", beat_name);
            jObjectInput.put("productDivision", product_division);
            jObjectInput.put("otherContactPersonNames", other_contact_person_name);
            jObjectInput.put("otherContactPersonPhones", other_contact_person_phn);
            //jObjectInput.put("firm_image_time_stamp", new Date(firm_image_time_stamp));
            jObjectInput.put("firmImageTimeStamp", null); //Umesh
            jObjectInput.put("firmImageLatLong", firm_image_LatLong);
            jObjectInput.put("opinion", opinion_about_distributor);
            jObjectInput.put("comment", comment);
            jObjectInput.put("transactionId", transactionId);
            jObjectInput.put("other_brand", brandName);
            jObjectInput.put("RecordingId", rec);
            jObjectInput.put("ownerImage", owner_image);
            jObjectInput.put("firmImage", firm_image);

            Log.e(TAG, "Add new distributor json: " +new Gson().toJson(jObjectInput));

        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//
//
//            if (owner_image != null && !owner_image.isEmpty()) {
//
////                Bitmap bitmap = compressImage(owner_image);
//                File bitmapFile = compressImage(owner_image);
//                if (bitmapFile != null) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getAbsolutePath());
//                    jObjectInput.put("ownerImage", getStringImage(bitmap));
//                    Log.d(TAG, "uploadNewDistributorDetailsToServer ownerImage: "+getStringImage(bitmap));
//                }
//            }
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        try {
//
//            if (firm_image != null && !firm_image.isEmpty()) {
//
//                File bitmap = compressImage(firm_image);
//
//                if (bitmap != null) {
//                    Bitmap bitmapp = BitmapFactory.decodeFile(bitmap.getAbsolutePath());
//                    jObjectInput.put("firmImage", getStringImage(bitmapp));
//                    Log.d(TAG, "uploadNewDistributorDetailsToServer firmImage: "+getStringImage(bitmapp));
////                    jObjectInput.put("firmImage", getStringImage(bitmap));
//                }
//
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {

            // StringEntity entity = new StringEntity(jObjectInput.toString());

            Call<JsonObject> callUploadNewDistributor = apiIntentface.newDistributorToServer(token, jObjectInput);

            // Retrofit
            callUploadNewDistributor.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    Log.e(TAG, "Response new distributor: " + response.body());

                    if (response.isSuccessful())
                    {
                        try {

                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            //String status = jsonObject.getString("status");

                            File file1, file2;
                            boolean temp = false;

                            //@Umesh 23-March-2022
                            if(jsonObject.getInt("status")==1)
                            {

                                if (owner_image != null && !owner_image.isEmpty()) {
                                    file1 = new File(owner_image);
                                    if (file1.exists())
                                        temp = file1.delete();
                                }

                                if (firm_image != null && !firm_image.isEmpty()) {
                                    file2 = new File(firm_image);
                                    if (file2.exists())
                                        temp = file2.delete();
                                }

                                boolean check = salesBeatDb.deleteSpecificDataFromNewDistributorTable(tempDid);
                                if (check && temp) {
                                    requestQ.remove("R6");
                                }
                            }
                            else
                            {
                                Log.e("Add New Distributor",jsonObject.getString("message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        handleError3(response.code(), response.message(), "newDistributorSearch", "R6");

                        try {

                            if (response.code() == 500) {

                                JSONObject obj = new JSONObject(jObjectInput);

                                submitError(obj, SbAppConstants.API_TO_ADD_NEW_DISTRIBUTOR, response.code(),
                                        "R6", tempDid, owner_image, firm_image, "", "", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Add new distributor error: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void syncExistingDistributorsStock() {

        Cursor distributorOrder = null;
        try {
            Log.d(TAG, "syncExistingDistributorsStock: "+requestQ);
            if (!requestQ.containsKey("R7")) {

                distributorOrder = salesBeatDb.getStockFromDistributorOrderTable("fail");
                if (distributorOrder != null && distributorOrder.getCount() > 0 && distributorOrder.moveToFirst()) {

//                do {

                    String did = distributorOrder.getString(distributorOrder.getColumnIndex("did_o"));
                    String takenAt = distributorOrder.getString(distributorOrder.getColumnIndex("taken_at_o"));
                    //String date = distributorOrder.getString(distributorOrder.getColumnIndex("date_o"));
                    String lat = distributorOrder.getString(distributorOrder.getColumnIndex("lat_o"));
                    String longt = distributorOrder.getString(distributorOrder.getColumnIndex("longt_o"));
                    String transactionId = distributorOrder.getString(distributorOrder.getColumnIndex("transactionId"));

                    //syncing stock captured to server if any
                    Cursor stockAvailable = salesBeatDb.getAllDataFromSkuEntryListTable(did);
                    if (stockAvailable != null && stockAvailable.getCount() > 0 /*&& !requestQ.containsKey("R7")*/) {

                        requestQ.put("R7", "added");
                        submitSkuDetailToServer(stockAvailable, did, takenAt,/* date,*/ transactionId, lat, longt);
                    }


//                } while (distributorOrder.moveToNext());

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (distributorOrder != null)
                distributorOrder.close();
        }

    }


    private void syncExistingDistributorsClosing() {

        Cursor distributorOrder = null;
        try {

            if (!requestQ.containsKey("R12")) {

                distributorOrder = salesBeatDb.getClosingFromDistributorOrderTable("fail");
                if (distributorOrder != null && distributorOrder.getCount() > 0 && distributorOrder.moveToFirst()) {

//                do {

                    String did = distributorOrder.getString(distributorOrder.getColumnIndex("did_o"));
                    String takenAt = distributorOrder.getString(distributorOrder.getColumnIndex("taken_at_o"));
                    //String date = distributorOrder.getString(distributorOrder.getColumnIndex("date_o"));
                    String lat = distributorOrder.getString(distributorOrder.getColumnIndex("lat_o"));
                    String longt = distributorOrder.getString(distributorOrder.getColumnIndex("longt_o"));
                    String transactionId = distributorOrder.getString(distributorOrder.getColumnIndex("transactionId"));

                    //syncing stock captured to server if any
                    Cursor closingAvailable = salesBeatDb.getAllDataFromClosingEntryListTable(did);
                    if (closingAvailable != null && closingAvailable.getCount() > 0 /*&& !requestQ.containsKey("R12")*/) {

                        requestQ.put("R12", "added");
                        submitClosingDetailToServer(closingAvailable, did, takenAt,/* date,*/ transactionId, lat, longt);
                    }


//                } while (distributorOrder.moveToNext());

                }

            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (distributorOrder != null)
                distributorOrder.close();
        }

    }


    private void syncExistingDistributorsOrder() {

        Cursor distributorOrder = null;
        try {
            Log.d(TAG, "syncExistingDistributorsOrder: "+requestQ);
            if (!requestQ.containsKey("R8")) {

                distributorOrder = salesBeatDb.getOrdersFromDistributorOrderTable("fail");
                if (distributorOrder != null && distributorOrder.getCount() > 0 && distributorOrder.moveToFirst()) {

//                do {

                    String did = distributorOrder.getString(distributorOrder.getColumnIndex("did_o"));
                    String takenAt = distributorOrder.getString(distributorOrder.getColumnIndex("taken_at_o"));
                    //String date = distributorOrder.getString(distributorOrder.getColumnIndex("date_o"));
                    String lat = distributorOrder.getString(distributorOrder.getColumnIndex("lat_o"));
                    String longt = distributorOrder.getString(distributorOrder.getColumnIndex("longt_o"));
                    String transactionId = distributorOrder.getString(distributorOrder.getColumnIndex("transactionId"));

                    //syncing orders to server if any
                    Cursor orderAvailable = salesBeatDb.getSpecificDataFromSkuEntryListTable(did, "fail");
                    if (orderAvailable != null && orderAvailable.getCount() > 0 /*&& !requestQ.containsKey("R8")*/) {

                        requestQ.put("R8", "added");
                        uploadDistributorOrdersToServer(orderAvailable, did, takenAt, /*date,*/ transactionId, lat, longt);
                    }

//                } while (distributorOrder.moveToNext());

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (distributorOrder != null)
                distributorOrder.close();
        }

    }

    private void submitSkuDetailToServer(Cursor cursor, final String did, String
            takenAt/*, String date*/,
                                         String transactionId, String lat, String longt) {

        List<HashMap> stocks = new ArrayList<>();
        HashMap<String, Object> stockCaptured = new HashMap<>();
        String type = "";

        try {

            if (cursor.moveToFirst()) {
                do {

                    HashMap<String, Object> values = new HashMap<>();
                    values.put("skuid", cursor.getString(cursor.getColumnIndex("sku_id")));
                    values.put("qty", cursor.getString(cursor.getColumnIndex("brand_qty")));
                    type = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SKU_STOCK_TYPE));

                    stocks.add(values);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        try {

            stockCaptured.put("did", Integer.valueOf(did));
//            stockCaptured.put("takenAt", takenAt);
            stockCaptured.put("takenAtstr", takenAt); //@Umesh
            stockCaptured.put("latitude", Double.valueOf(lat));
            stockCaptured.put("longitude", Double.valueOf(longt));
            //stockCaptured.put("stockType", type);
            stockCaptured.put("transactionId", transactionId);
//            stockCaptured.put("catalogue", stocks);
            stockCaptured.put("openingStockCatalogue", stocks);//Umesh
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Stock Capture json: " + new Gson().toJson(stockCaptured));

        try {

            Call<JsonObject> callSubmitDistributorStockInfo = apiIntentface.submitDistributorStockInfo(
                    token,
                    stockCaptured
            );

            // Retrofit
            callSubmitDistributorStockInfo.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    Log.e(TAG, "Response Stock Capture:" + response.body());
                    if (response.isSuccessful())
                    {

                        // called when response HTTP status is "200 OK"

                        try {

                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            //@Umesh
                            if (jsonObject.getInt("status")==1)
                            {

//                                    boolean flag1 = salesBeatDb.updateDataInSkuEntryListTable(did, "success");
//                                    if (flag1) {
                                boolean flag2 = salesBeatDb.updateInDistributorStock(did, "success");
                                if (flag2)
                                    requestQ.remove("R7");
                            }
//                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        //requestQ.remove("R7");
                        //Log.e(TAG, "=Stock Capture error==" + response.message() + "   " + response.code());
                        SbLog.printError(TAG, "submitOpeningStock", String.valueOf(response.code()),
                                response.message(), eid);

                        handleError3(response.code(), response.message(), "submitOpeningStock", "R7");

                        try {

                            if (response.code() == 500) {
                                submitError(new JSONObject(stockCaptured), SbAppConstants.API_SUBMIT_DISTRIBUTOR_STOCK_INFO, response.code(),
                                        "R7", did, did, "", "", "", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Stock capture error: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitClosingDetailToServer(Cursor cursor, final String did, String
            takenAt/*, String date*/,
                                             String transactionId, String lat, String longt) {

        List<HashMap> stocks = new ArrayList<>();
//        JSONArray stocks = new JSONArray();
        HashMap<String, Object> stockCaptured = new HashMap<>();
//        final JSONObject stockCaptured = new JSONObject();

        String type = "";

        try {

            if (cursor.moveToFirst()) {
                do {

                    HashMap<String, Object> values = new HashMap<>();
//                    JSONObject values = new JSONObject();


                    values.put("skuid", cursor.getString(cursor.getColumnIndex("sku_id")));
                    values.put("qty", cursor.getString(cursor.getColumnIndex("brand_qty")));
                    type = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SKU_STOCK_TYPE));

                    stocks.add(values);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        try {

            stockCaptured.put("did", did);
            stockCaptured.put("takenAt", takenAt);
            stockCaptured.put("latitude", lat);
            stockCaptured.put("longitude", longt);
            stockCaptured.put("closingType", type);
            stockCaptured.put("transactionId", transactionId);
//            stockCaptured.put("catalogue", stocks);
            stockCaptured.put("openingStockCatalogue", stocks);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Closing Capture json: " + stockCaptured.toString());

        try {

            Call<JsonObject> callSubmitClosingDetail = apiIntentface.submitDistributorClosing(
                    token,
                    stockCaptured
            );

            // Retrofit
            callSubmitClosingDetail.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    Log.e(TAG, "Response Closing Capture: " + response.body());
                    if (response.isSuccessful()) {

                        try {

                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            //@Umesh
                            if(jsonObject.getInt("status")==1)
                            {
//                                    boolean flag1 = salesBeatDb.updateDataInSkuEntryListTable(did, "success");
//                                    if (flag1) {
                                boolean flag2 = salesBeatDb.updateInDistributorClosing(did, "success");
                                if (flag2)
                                    requestQ.remove("R12");
                            }
//                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        //requestQ.remove("R7");
                        //Log.e(TAG, "=Stock Capture error==" + response.message() + "   " + response.code());
                        SbLog.printError(TAG, "submitClosing", String.valueOf(response.code()),
                                response.message(), eid);

                        handleError3(response.code(), response.message(), "submitClosing", "R12");

                        try {

                            if (response.code() == 500) {
                                submitError(new JSONObject(stockCaptured), SbAppConstants.API_SUBMIT_DISTRIBUTOR_STOCK_INFO, response.code(),
                                        "R12", did, did, "", "", "", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Closing Capture error: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void uploadDistributorOrdersToServer(Cursor cursor, final String did, String
            takenAt,
            /*String date,*/ String transactionId, String lat, String longt) {
        List<HashMap> catalogue = new ArrayList<>();
        HashMap<String, Object> orderTaken = new HashMap<>();

        String type = "";
        try {

            if (cursor.moveToFirst()) {

                do {
                    HashMap<String, Object> values = new HashMap<>();
                    values.put("skuid", cursor.getString(cursor.getColumnIndex("sku_id")));
                    values.put("qty", cursor.getString(cursor.getColumnIndex("distributor_order")));
                    type = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRIBUTOR_ORDE_TYPE));

                    catalogue.add(values);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        if (catalogue.size() > 0) {

            try {

                orderTaken.put("orderType", type);
                orderTaken.put("transactionId", transactionId); //@Umesh 20220916
                orderTaken.put("eid", Integer.valueOf(eid));
                orderTaken.put("did", Integer.valueOf(did));
               // orderTaken.put("takenAt", takenAt);
                orderTaken.put("takenAtstr", takenAt); //@Umesh
                orderTaken.put("latitude", lat);
                orderTaken.put("longitude", longt);
//                orderTaken.put("catalogue", catalogue);
                orderTaken.put("distributorOrderCatalogue", catalogue); //@Umesh
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        List<HashMap> orderOuterArray = new ArrayList<>();
        final HashMap<String, Object> orders = new HashMap<>();
        try {

            orderOuterArray.add(orderTaken);

            orders.put("orders", orderOuterArray);
            orders.put("transactionId", transactionId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Distributor Order json: " + new Gson().toJson(orders));

        try {

            Call<JsonObject> callUploadDistributor = apiIntentface.submitDistributorOrder(
                    token,
                    orders
            );

            // Retrofit
            callUploadDistributor.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                    Log.e(TAG, "RESPONSE DISTRIBUTOR ORDER: " + response.body());
                    if (response.isSuccessful())
                    {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            //@Umesh 23-March-2022
                            if(jsonObject.getInt("status")==1)
                            {
//                                    boolean flag1 = salesBeatDb.updateDataInOrderEntryListTable(did, "success");
//                                    if (flag1) {
                                boolean flag2 = salesBeatDb.updateInDistributorOrder(did, "success");
                                if (flag2)
                                    requestQ.remove("R8");
//                                    }
                            }
                            else
                            {
                                Log.e(TAG, "Distributor order error: " + jsonObject.getString("message"));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {

                        handleError3(response.code(), response.message(), "submitDistOrders", "R8");

                        try {

                            if (response.code() == 500) {
                                submitError(new JSONObject(orders), SbAppConstants.API_SUBMIT_DISTRIBUTOR_ORDER, response.code(),
                                        "R8", did, did, "", "", "", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "Distributor order error: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getNewlyAddedRetailersProductiveAndNonProductiveCalls() {
        Log.d(TAG, "getNewlyAddedRetailersProductiveAndNonProductiveCalls: "+requestQ);
        if (!requestQ.containsKey("R9")) {

            Cursor orderPlacedBy = null;
            try {
                //submit new retailers order to server
                orderPlacedBy = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable2(/*nrid*/);
                if (orderPlacedBy != null && orderPlacedBy.getCount() > 0 && orderPlacedBy.moveToFirst()) {

                    do {

                        String nrid = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("nrid"));
                        String did = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("distributor_id"));
                        String orderTakenAt = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_taken_at"));
                        String orderType = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_type"));
                        String checkIn = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_check_in_time"));
                        String checkOut = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_check_out_time"));
                        String lat = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_lat"));
                        String longt = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_long"));
                        String cmnt = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_comment"));
                        String transactionId = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("transactionId"));

                        if (cmnt.equalsIgnoreCase("new productive")) {

                            requestQ.put("R9", "added");
                            syncProductiveCallFromNewRetailersToServer(nrid, did,
                                    orderTakenAt, orderType, checkIn,
                                    checkOut, lat, longt, transactionId);

                        } else {

                            requestQ.put("R9", "added");
                            syncNonProductiveCallFromNewRetailerToServer(nrid, did, transactionId);
                        }


                    } while (orderPlacedBy.moveToNext());
                }

            } catch (Exception e) {
                e.printStackTrace();
                requestQ.remove("R9");
            } finally {
                if (orderPlacedBy != null)
                    orderPlacedBy.close();
            }
        }
    }


    private void syncProductiveCallFromNewRetailersToServer(final String nridSaved, final String did,
                                                            String taken_at, String ordertype, String checkInT,
                                                            String checkOutT, String lat, String longt,
                                                            String transactionId) {


//        List<HashMap> catalogue = new ArrayList<>();
////        JSONArray catalogue = new JSONArray();
//
//        Cursor cursorOrderList = salesBeatDb.getSpecificDataFromNewOrderEntryListTable(nridSaved, did
//                /*,"success", "fail"*/);
//
//        try {
//
//            if (cursorOrderList != null && cursorOrderList.getCount() > 0 && cursorOrderList.moveToFirst()) {
//
//                do {
//
//                    HashMap<String, Object> jObjectInput = new HashMap<>();
////                    JSONObject jObjectInput = new JSONObject();
//                    jObjectInput.put("skuid", cursorOrderList.getString(cursorOrderList.getColumnIndex("new_sku_id")));
//                    jObjectInput.put("qty", cursorOrderList.getString(cursorOrderList.getColumnIndex("new_brand_qty")));
//                    catalogue.add(jObjectInput);
//
//                } while (cursorOrderList.moveToNext());
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "==" + e.getMessage());
//        } finally {
//            if (cursorOrderList != null)
//                cursorOrderList.close();
//        }
//
//        if (catalogue.size() > 0) {
//
//            final HashMap<String, Object> ordersData = new HashMap<>();
////            final JSONObject ordersData = new JSONObject();
//
//            try {
//
//                HashMap<String, Object> values1 = new HashMap<>();
////                JSONObject values1 = new JSONObject();
//                values1.put("cid", cid);
//                values1.put("rid", nridSaved);
//                values1.put("orderType", ordertype);
//                values1.put("eid", eid);
//                values1.put("did", did);
//                values1.put("takenAt", taken_at);
//                values1.put("latitude", lat);
//                values1.put("longitude", longt);
//                values1.put("catalogue", catalogue);
//
//                List<HashMap> orders = new ArrayList<>();
////                JSONArray orders = new JSONArray();
//                orders.add(values1);
//
//                HashMap<String, Object> values2 = new HashMap<>();
////                JSONObject values2 = new JSONObject();
//                values2.put("rid", nridSaved);
//                values2.put("checkIn", checkInT);
//                values2.put("checkOut", checkOutT);
//                values2.put("latitude", lat);
//                values2.put("longitude", longt);
//                values2.put("comments", "");
//
//                List<HashMap> retailerCalls = new ArrayList<>();
////                JSONArray retailerCalls = new JSONArray();
//                retailerCalls.add(values2);
//
//                ordersData.put("orders", orders);
//                ordersData.put("retailerCalls", retailerCalls);
//
//                ordersData.put("transactionId", transactionId + did);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            Log.e(TAG, "New retailer order json: " + ordersData.toString());
//
//            try {
//
//                Call<JsonObject> callSubmitOrder = apiIntentface.submitOrder(
//                        token,
//                        ordersData
//                );
//
//                // Retrofit
//                callSubmitOrder.enqueue(new Callback<JsonObject>() {
//                    @Override
//                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                        Log.e(TAG, "Response new retailer order: " + response.body());
//
//                        if (response.isSuccessful()) {
//
//                            try {
//
//                                JSONObject jsonObject = new JSONObject(response.body().toString());
//
//                                String status = jsonObject.getString("status");
//                                //String msg = response.getString("statusMessage");
//                                if (status.equalsIgnoreCase("success")) {
//
//                                    boolean val1 = salesBeatDb.updateNewRetailerListTable2(nridSaved, did, "success");
//                                    Log.e(TAG, "New Retailer Order status: " + val1);
//                                    if (val1) {
//
//                                        requestQ.remove("R9");
//                                    }
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
//
//                            handleError3(response.code(), response.message(), "submitOrders", "R9");
//
//                            try {
//
//                                if (response.code() == 500) {
//                                    submitError(new JSONObject(ordersData), SbAppConstants.API_SUBMIT_ORDER, response.code(),
//                                            "R9", nridSaved, did, "", "", "", "");
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        Log.e(TAG, "New Retailer Order error: " + t.getMessage());
//                    }
//                });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }

        JSONArray catalog = new JSONArray();
        Cursor catalogList = null;

        try {
            catalogList = salesBeatDb.getSpecificDataFromNewOrderEntryListTable(nridSaved, did);
            if (catalogList != null && catalogList.getCount() > 0 && catalogList.moveToFirst()) {

                do {

                    JSONObject item = new JSONObject();

                    item.put("skuid", catalogList.getString(catalogList.getColumnIndex("new_sku_id")));
                    item.put("qty", catalogList.getString(catalogList.getColumnIndex("new_brand_qty")));
                    catalog.put(item);

                } while (catalogList.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (catalogList != null)
                catalogList.close();
        }

        if (catalog.length() > 0) {


            try {

                JSONObject orderArrayItem = new JSONObject();
                orderArrayItem.put("cid", Integer.valueOf(cid));
                if(!nridSaved.equals("null")) //@Umesh 26-06-2022
                {orderArrayItem.put("rid", Integer.valueOf(nridSaved));}
                orderArrayItem.put("orderType", ordertype);
                orderArrayItem.put("eid", Integer.valueOf(eid));
                orderArrayItem.put("did", Integer.valueOf(did));
//                orderArrayItem.put("takenAt", taken_at);
                orderArrayItem.put("takenAtStr", taken_at); //Umesh
                orderArrayItem.put("latitude", Double.valueOf(lat));
                orderArrayItem.put("longitude", Double.valueOf(longt));
//                orderArrayItem.put("catalogue", catalog);
                orderArrayItem.put("orderCatalogue", catalog); //@Umesh

                JSONArray orders = new JSONArray();
                orders.put(orderArrayItem);

                // HashMap<String, Object> retailerCallsArrayItem = new HashMap<>();
                JSONObject retailerCallsArrayItem = new JSONObject();
                if(!nridSaved.equals("null")) //@Umesh 26-06-2022
                retailerCallsArrayItem.put("rid", Integer.valueOf(nridSaved));
                retailerCallsArrayItem.put("did", Integer.valueOf(did));
                //            retailerCallsArrayItem.put("checkIn", checkIn);
                //            retailerCallsArrayItem.put("checkOut", checkOut);
                retailerCallsArrayItem.put("checkInstr", checkInT);//Umesh
                retailerCallsArrayItem.put("checkOutstr", checkOutT); //Umesh
                retailerCallsArrayItem.put("latitude", lat);
                retailerCallsArrayItem.put("longitude", longt);
                retailerCallsArrayItem.put("comments", "");

//                List<HashMap> retailerCalls = new ArrayList<>();
                JSONArray retailerCalls = new JSONArray();
                retailerCalls.put(retailerCallsArrayItem);

                JSONObject productiveCallParams = new JSONObject();
                productiveCallParams.put("orders", orders);
                productiveCallParams.put("transactionId", transactionId);
                productiveCallParams.put("retailerCalls", retailerCalls);

//                Log.e(TAG, "New Retailer Productive call json: " + productiveCallParams.toString());
                Log.e(TAG, "New Retailer Productive call json: " + new Gson().toJson(productiveCallParams));

                JsonObjectRequest productiveCallRequest = new JsonObjectRequest(Request.Method.POST,
                        SbAppConstants.SUBMIT_ORDER, productiveCallParams, response -> {

                    Log.e(TAG, "New Retailer Productive call Response: " + response.toString());

                    try {
                    //@Umesh 23-Feb-2022
                    if(response.getInt("status")==1)
                    {
//                            String status = response.getString("status");
                            if (response.getInt("status")==1) {
                               // Log.e(TAG, "val2: "+nridSaved+"  did:"+did);
                                boolean val2 = salesBeatDb.updateNewRetailerListTable2(
                                        nridSaved, did,"success");
                               // Log.e(TAG, "val2: "+val2);
                                if (val2)
                                    requestQ.remove("R9");



                    } else {
                        requestQ.remove("R9");
                        try {
                            SbLog.printError("New Retailer Productive Call",
                                    "submitOrders", "Not available",
                                    "" + response.toString(), eid);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                        }
                    } catch (JSONException e) {
                        requestQ.remove("R9");
                        e.printStackTrace();
                    }

                }, error -> {
                    requestQ.remove("R9");
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Accept", "application/json");
                        headers.put("authorization", token);
                        return headers;
                    }
                };

                productiveCallRequest.setRetryPolicy(new DefaultRetryPolicy(
                                50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                        )
                );

                Volley.newRequestQueue(context).add(productiveCallRequest);

            } catch (Exception e) {
                requestQ.remove("R9");
                e.printStackTrace();
            }
        } else {
            requestQ.remove("R9");
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void syncNonProductiveCallFromNewRetailerToServer(final String nrid,
                                                              final String did, String transactionId) {

        Cursor noOrderC = null;
        try {

            noOrderC = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable22(nrid);

            if (noOrderC != null && noOrderC.getCount() > 0 && noOrderC.moveToFirst()) {

                do {

                    final String rid = noOrderC.getString(noOrderC.getColumnIndex("nrid"));
                    String checkIn = noOrderC.getString(noOrderC.getColumnIndex("new_check_in_time"));
                    String checkOut = noOrderC.getString(noOrderC.getColumnIndex("new_check_in_time"));
                    String lat = noOrderC.getString(noOrderC.getColumnIndex("new_order_lat"));
                    String longt = noOrderC.getString(noOrderC.getColumnIndex("new_order_long"));
                    String reason = noOrderC.getString(noOrderC.getColumnIndex("new_order_comment"));
//
//                    HashMap<String, Object> jsonObject = new HashMap<>();
////                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("rid", rid);
//                    jsonObject.put("checkIn", checkIn);
//                    jsonObject.put("checkOut", checkOut);
//                    jsonObject.put("latitude", lat);
//                    jsonObject.put("longitude", longt);
//                    jsonObject.put("comments", reason);
//
//                    List<HashMap> retailerOutCalls = new ArrayList<>();
////                    JSONArray retailerOutCalls = new JSONArray();
//                    retailerOutCalls.add(jsonObject);
//
////                    final JSONObject orders = new JSONObject();
//                    final HashMap<String, Object> orders = new HashMap<>();
//                    orders.put("retailerCalls", retailerOutCalls);
//                    orders.put("transactionId", transactionId + did);
//
//                    Log.e(TAG, "No order From new Retailer json: " + orders.toString());
//
//                    Call<JsonObject> callSubmitOrder = apiIntentface.submitOrder(
//                            token,
//                            orders
//                    );
//
//                    // Retrofit
//                    callSubmitOrder.enqueue(new Callback<JsonObject>() {
//                        @Override
//                        public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                            Log.e(TAG, "Response NO Order new retailer: " + response.body());
//
//                            if (response.isSuccessful()) {
//
//                                try {
//
//                                    JSONObject jsonObject1 = new JSONObject(response.body().toString());
//
//                                    String status = jsonObject1.getString("status");
//                                    //String msg = response.getString("statusMessage");
//
//                                    if (status.equalsIgnoreCase("success")) {
//
//                                        boolean val1 = salesBeatDb.updateNewRetailerListTable2(nrid, did, "success");
//                                        Log.e(TAG, "New Retailer No Order status: " + val1);
//                                        if (val1) {
//
//                                            requestQ.remove("R9");
//                                        }
//                                    }
//
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            } else {
//
//                                Log.e("Submit no order", "fail");
//
//                                handleError3(response.code(), response.message(), "submitOrders", "R9");
//
//                                try {
//
//                                    if (response.code() == 500) {
//                                        submitError(new JSONObject(orders), SbAppConstants.API_SUBMIT_ORDER, response.code(),
//                                                "R9", nrid, did, "", "", "", "");
//                                    }
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<JsonObject> call, Throwable t) {
//                            Log.e(TAG, "New Retailer No Order error: " + t.getMessage());
//                        }
//                    });

                    try {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("rid", Integer.parseInt(rid));
                        hashMap.put("did", Integer.parseInt(did));
                        //            hashMap.put("checkIn", checkIn);
                        //            hashMap.put("checkOut", checkOut);
                        hashMap.put("checkInstr", checkIn);//Umesh
                        hashMap.put("checkOutstr", checkOut); //Umesh
                        hashMap.put("latitude", lat);
                        hashMap.put("longitude", longt);
                        hashMap.put("comments", reason);

                        List<HashMap> retailerOutCalls = new ArrayList<>();
                        retailerOutCalls.add(hashMap);

                        HashMap<String, Object> orderHash = new HashMap<>();
                        orderHash.put("orders", new ArrayList<>());
                        orderHash.put("retailerCalls", retailerOutCalls);
                        orderHash.put("transactionId", transactionId);

                        Log.e(TAG, "No order From new Retailer json: " + orderHash.toString());

                        JsonObjectRequest nonProductiveCallRequest = new JsonObjectRequest(Request.Method.POST,
                                SbAppConstants.SUBMIT_ORDER, new JSONObject(orderHash), response -> {

                            Log.e(TAG, "No order From new Retailer Response: " + response.toString());

                            try {
                                if(response.getInt("status")==1)
                                {
                                    try {
                                        String status = response.getString("status");
                                        if (status.equalsIgnoreCase("success")) {
                                            boolean val1 = salesBeatDb.updateNewRetailerListTable2(nrid,
                                                    did, "success");
                                            if (val1)
                                                requestQ.remove("R9");
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    requestQ.remove("R9");
                                    try {
//                                        SbLog.printError("Existing Retailer Non Productive Call",
//                                                "submitOrders", "Not available",
//                                                "" + response.toString(), eid);
                                        Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            requestQ.remove("R9");
                            boolean val = salesBeatDb.updateInOderPlacedByRetailersTable22(rid, did,
                                    "error", String.valueOf(error.networkResponse.statusCode),
                                    "" + error.getMessage());
                        }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Accept", "application/json");
                                headers.put("authorization", token);
                                return headers;
                            }
                        };

                        nonProductiveCallRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        50000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                )
                        );

                        Volley.newRequestQueue(context).add(nonProductiveCallRequest);

                    } catch (Exception e) {
                        e.printStackTrace();
                        requestQ.remove("R9");
                    }

                } while (noOrderC.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            requestQ.remove("R9");
        } finally {
            if (noOrderC != null)
                noOrderC.close();
        }
    }


    private void syncExistingDistributorCancelledOrder() {

        Cursor cancelDisOrderCursor = null;
        try {
            Log.d(TAG, "syncExistingDistributorCancelledOrder: "+requestQ);
            if (!requestQ.containsKey("R10")) {

                cancelDisOrderCursor = salesBeatDb.getAllFromDistributorOrderTable2("cancelled", "cancelled");
                if (cancelDisOrderCursor != null && cancelDisOrderCursor.getCount() > 0 && cancelDisOrderCursor.moveToFirst()) {

//                do {

                    String did = cancelDisOrderCursor.getString(cancelDisOrderCursor.getColumnIndex("did_o"));
                    String transactionId = cancelDisOrderCursor.getString(cancelDisOrderCursor.getColumnIndex("transactionId"));
//                    if (!requestQ.containsKey("R10")) {

                    requestQ.put("R10", "added");
                    cancelDistributorOrder(did, transactionId);
//                    }

//                } while (cancelDisOrderCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cancelDisOrderCursor != null)
                cancelDisOrderCursor.close();
        }
    }

    private void cancelDistributorOrder(final String did, final String transactionId) {

//        RequestParams params = new RequestParams();
        HashMap<String, Object> params = new HashMap<>();
        params.put("did", did);
        params.put("transactionId", transactionId);

        Call<JsonObject> callCancelOrder = apiIntentface.cancelDistributorOrder(
                token,
                params
        );

        // Retrofit
        callCancelOrder.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                Log.e(TAG, "Respone distributor cancel order: " + response.body());

                if (response.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        //@Umesh
                        if(jsonObject.getInt("status")==1)
                        {

                            boolean flag = salesBeatDb.updateInDistributorOrder2(did, "success");
                            if (flag)
                                requestQ.remove("R10");


                        } else {
                            requestQ.remove("R10");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    handleError3(response.code(), response.message(), "cancelDistOrder", "R10");

                    try {

                        if (response.code() == 500) {

                            JSONObject paramss = new JSONObject();
                            paramss.put("did", did);
                            paramss.put("transactionId", transactionId);

                            submitError(paramss, SbAppConstants.API_CANCEL_DISTRIBUTOR_ORDER, response.code(),
                                    "R10", did, did, "", "", "", "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "distributor cancel order error: " + t.getMessage());
            }
        });

    }


    private void syncOtherActivity() {

        Cursor otherActCursor = null;

        try {
            Log.d(TAG, "syncOtherActivity: "+requestQ);
            if (!requestQ.containsKey("R11")) {

                otherActCursor = salesBeatDb.getOtherActivity("fail");
                if (otherActCursor != null && otherActCursor.getCount() > 0 && otherActCursor.moveToFirst())
                {

//                do {

                    String activity = otherActCursor.getString(otherActCursor.getColumnIndex("activity"));
                    String remarks = otherActCursor.getString(otherActCursor.getColumnIndex("remarks"));
                    String lat = otherActCursor.getString(otherActCursor.getColumnIndex("other_lat"));
                    String longt = otherActCursor.getString(otherActCursor.getColumnIndex("other_longt"));
                    String activity_date = otherActCursor.getString(otherActCursor.getColumnIndex("activity_date"));
                    String transactionId = otherActCursor.getString(otherActCursor.getColumnIndex("transactionId"));

//                    if (!requestQ.containsKey("R11")) {

                    requestQ.put("R11", "added");
                    submitActivity(activity, remarks, transactionId, lat, longt, activity_date);
//                    }

//                } while (otherActCursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (otherActCursor != null)
                otherActCursor.close();
        }
    }


    private void submitActivity(final String activity, final String remarks,
                                final String transactionId,
                                final String lat, final String longt, final String activity_date) {

        HashMap<String, Object> activityhash = new HashMap<>();
        activityhash.put("activity", activity);
        activityhash.put("remarks", remarks);
        activityhash.put("latitude", Float.valueOf(lat));
        activityhash.put("longitude", Float.valueOf(longt));
//        activityhash.put("takenAt", activity_date);
        activityhash.put("takenAtstr", activity_date);
        activityhash.put("transactionId", transactionId);

        Call<JsonObject> callSubmitActivity = apiIntentface.submitActivity(
                token,
                activityhash
        );

        // Retrofit
        callSubmitActivity.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.e(TAG, "Response MeetingActivity: " + response.body());

                if (response.isSuccessful())
                {

                    // called when response HTTP status is "200 OK"

                    try {

                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        //@Umesh 23-March-2022
                        if(jsonObject.getInt("status")==1)
                        {

                            boolean flag = salesBeatDb.updateOtherActivity(transactionId, "success");
                            if (flag)
                                requestQ.remove("R11");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    //requestQ.remove("R11");
                    Log.e(TAG, "=MeetingActivity error==" + response.message() + "   " + response.code());
                    SbLog.printError(TAG, "fullDayActivity", String.valueOf(response.code()),
                            response.message(), eid);


                    handleError3(response.code(), response.message(), "fullDayActivity", "R11");

                    try {

                        if (response.code() == 500) {
                            JSONObject paramss = new JSONObject();
                            paramss.put("activity", activity);
                            paramss.put("remarks", remarks);
                            paramss.put("latitude", lat);
                            paramss.put("longitude", longt);
                            paramss.put("takenAt", activity_date);
                            paramss.put("transactionId", transactionId);

                            submitError(paramss, SbAppConstants.API_FULL_DAY_ACTIVITY, response.code(),
                                    "R11", transactionId, "", "", "", "", "");

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "MeetingActivity Error: " + t.getMessage());
            }
        });


    }

    //helper methods -----------------------------

    /*private Bitmap compressImage1(String filePath) {
        try {
            Log.e(TAG, "===> " + filePath);

            File compressedFile = new Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToFile(new File(filePath));

            return BitmapFactory.decodeFile(compressedFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public static File compressImage(String imagePath) {
        try {
            // Load image dimensions without loading full bitmap into memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Compute the scale factor
            int inSampleSize = 1;
            while (originalWidth / inSampleSize > 640 || originalHeight / inSampleSize > 480) {
                inSampleSize *= 2;
            }

            // Load the scaled-down image
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            if (bitmap == null) return null;

            // Resize bitmap to exactly 640x480
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);

            // Create output file
            File compressedFile = new File(imagePath.replace(".jpg", "_compressed.jpg"));
            FileOutputStream fileOutputStream = new FileOutputStream(compressedFile);

            // Compress image (JPEG, 75% quality)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

            // Recycle bitmaps to free memory
            bitmap.recycle();
            resizedBitmap.recycle();

            return compressedFile; // Return the compressed image file

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    private void deleteLocalFile(String ownerImage, String shopImage1, String shopImage2,
                                 String shopImage3, String shopImage4) {

        File file, file1, file2, file3, file4;
        boolean temp = false;

        try {

            if (!ownerImage.isEmpty()) {
                file = new File(ownerImage);
                if (file.exists())
                    temp = file.delete();
            }

            if (!shopImage1.isEmpty()) {
                file1 = new File(shopImage1);
                if (file1.exists())
                    temp = file1.delete();
            }

            if (!shopImage2.isEmpty()) {
                file2 = new File(shopImage2);
                if (file2.exists())
                    temp = file2.delete();
            }

            if (!shopImage3.isEmpty()) {
                file3 = new File(shopImage3);
                if (file3.exists())
                    temp = file3.delete();
            }

            if (!shopImage4.isEmpty()) {
                file4 = new File(shopImage4);
                if (file4.exists())
                    temp = file4.delete();
            }

            Log.e(TAG, " File deleted:" + temp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return temp;
    }


    private void submitError(final JSONObject jObjectInput, final String api,
                             final int statusCode,
                             final String reqNumber, final String tempRid, final String did,
                             final String shopImage1, final String shopImage2, final
                             String shopImage3, final String shopImage4) {


        // Volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_SUBMIT_ERROR, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "Response submit error: " + response);
                //{"status":"success","statusMessage":"Success"}
                String status = null;

                try {

                    status = response.getString("status");

                    if (reqNumber.equalsIgnoreCase("R2") && status.equalsIgnoreCase("success")) {

                        boolean val1 = salesBeatDb.updateNewRetailerListTable(tempRid, tempRid, "success");
                        boolean val2 = salesBeatDb.updateIdDataInNewOrderEntryListTable(tempRid, tempRid);
                        boolean val3 = salesBeatDb.updateInOderPlacedByNewRetailersTable(tempRid, tempRid, "success");

                        if (val1 && val2 && val3) {

                            requestQ.remove(reqNumber);
                            deleteLocalFile(did, shopImage1, shopImage2, shopImage3, shopImage4);
                        }

                    } else if (reqNumber.equalsIgnoreCase("R9") && status.equalsIgnoreCase("success")) {

                        boolean flag1 = salesBeatDb.updateSpecificDataInNewOrderEntryListTable(tempRid, did, "success");
                        boolean flag2 = salesBeatDb.updateStatusInOderPlacedByNewRetailersTable(tempRid, did, "success");
                        if (flag1 && flag2)
                            requestQ.remove(reqNumber);

                    } else if (reqNumber.equalsIgnoreCase("R6") && status.equalsIgnoreCase("success")) {

                        if (!did.isEmpty()) {
                            File file1 = new File(did);
                            if (file1.exists())
                                file1.delete();
                        }

                        if (!shopImage1.isEmpty()) {
                            File file2 = new File(shopImage1);
                            if (file2.exists())
                                file2.delete();
                        }

                        boolean check = salesBeatDb.deleteSpecificDataFromNewDistributorTable(tempRid);
                        if (check) {
                            requestQ.remove(reqNumber);
                        }

                    } else if (reqNumber.equalsIgnoreCase("R3") && status.equalsIgnoreCase("success")) {

                        if (status.equalsIgnoreCase("success")) {

                            boolean val = salesBeatDb.updateInOderPlacedByRetailersTable2(tempRid, "success", did);
                            if (val)
                                requestQ.remove("R3");

                        }

                    } else if (reqNumber.equalsIgnoreCase("R4") && status.equalsIgnoreCase("success")) {

                        boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable2(tempRid, "success", did);
                        if (val2)
                            requestQ.remove("R4");

                    } else if (reqNumber.equalsIgnoreCase("R5") && status.equalsIgnoreCase("success")) {

                        boolean val1 = salesBeatDb.updateInOderPlacedByRetailersTable2(tempRid, "success", did);
                        if (val1) {
                            requestQ.remove("R5");
                        }

                    } else if (reqNumber.equalsIgnoreCase("R8") && status.equalsIgnoreCase("success")) {

                        boolean flag2 = salesBeatDb.updateInDistributorOrder(did, "success");
                        if (flag2)
                            requestQ.remove("R8");

                    } else if (reqNumber.equalsIgnoreCase("R7") && status.equalsIgnoreCase("success")) {

                        boolean flag2 = salesBeatDb.updateInDistributorStock(did, "success");
                        if (flag2)
                            requestQ.remove("R7");

                    } else if (reqNumber.equalsIgnoreCase("R10") && status.equalsIgnoreCase("success")) {

                        boolean flag = salesBeatDb.updateInDistributorOrder2(tempRid, "success");
                        if (flag)
                            requestQ.remove("R10");

                    } else if (reqNumber.equalsIgnoreCase("R11") && status.equalsIgnoreCase("success")) {

                        boolean flag = salesBeatDb.updateOtherActivity(tempRid, "success");
                        if (flag)
                            requestQ.remove("R11");
                    } else if (reqNumber.equalsIgnoreCase("R1") && status.equalsIgnoreCase("success")) {

                        boolean val = salesBeatDb.updateBeatVisited(tempRid, did, "success");
                        if (val)
                            requestQ.remove("R1");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, error -> {

            error.printStackTrace();

            try {

                SbLog.printError(TAG, "submitError",
                        String.valueOf(error.networkResponse.statusCode),
                        error.getMessage(), eid);

                if (error.networkResponse.statusCode == 422
                        || error.networkResponse.statusCode == 401
                        || error.networkResponse.statusCode == 403
                        || error.networkResponse.statusCode == 408
                        || error.networkResponse.statusCode == 400) {

                    requestQ.remove(reqNumber);

                } else if (error.networkResponse.statusCode == 500) {


                    if (reqNumber.equalsIgnoreCase("R2")) {

                        boolean val1 = salesBeatDb.updateNewRetailerListTable(tempRid, tempRid, "error");
                        boolean val2 = salesBeatDb.updateIdDataInNewOrderEntryListTable(tempRid, tempRid);
                        boolean val3 = salesBeatDb.updateInOderPlacedByNewRetailersTable(tempRid, tempRid, "error");

                        if (val1 && val2 && val3) {

                            requestQ.remove(reqNumber);
                            deleteLocalFile(did, shopImage1, shopImage2, shopImage3, shopImage4);
                        }

                    } else if (reqNumber.equalsIgnoreCase("R9")) {

                        boolean flag1 = salesBeatDb.updateSpecificDataInNewOrderEntryListTable(tempRid, did, "error");
                        boolean flag2 = salesBeatDb.updateStatusInOderPlacedByNewRetailersTable(tempRid, did, "error");
                        if (flag1 && flag2)
                            requestQ.remove(reqNumber);

                    } else if (reqNumber.equalsIgnoreCase("R6")) {

                        //boolean check = salesBeatDb.deleteSpecificDataFromNewDistributorTable(tempRid);
                        requestQ.remove(reqNumber);

                    } else if (reqNumber.equalsIgnoreCase("R3")) {

                        boolean val = salesBeatDb.updateInOderPlacedByRetailersTable2(tempRid, "error", did);

                        if (val)
                            requestQ.remove("R3");

                    } else if (reqNumber.equalsIgnoreCase("R4")) {

                        boolean val1 = salesBeatDb.updateSpecificDataInOrderEntryListTable(tempRid, did, "error");
                        boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable2(tempRid, "error", did);

                        if (val1 && val2)
                            requestQ.remove("R4");

                    } else if (reqNumber.equalsIgnoreCase("R5")) {

                        boolean val1 = salesBeatDb.updateInOderPlacedByRetailersTable2(tempRid, "error", did);
                        boolean val2 = salesBeatDb.updateOrderEntryListTable(tempRid, did, "error");

                        if (val1 && val2)
                            requestQ.remove("R5");

                    } else if (reqNumber.equalsIgnoreCase("R8")) {

                        boolean flag1 = salesBeatDb.updateDataInOrderEntryListTable(tempRid, "error");
                        boolean flag2 = salesBeatDb.updateInDistributorOrder(tempRid, "error");

                        if (flag1 && flag2)
                            requestQ.remove("R8");

                    } else if (reqNumber.equalsIgnoreCase("R7")) {

                        boolean flag1 = salesBeatDb.updateDataInSkuEntryListTable(tempRid, "error");
                        boolean flag2 = salesBeatDb.updateInDistributorStock(tempRid, "error");

                        if (flag1 && flag2)
                            requestQ.remove("R7");

                    } else if (reqNumber.equalsIgnoreCase("R10")) {

                        boolean flag = salesBeatDb.updateInDistributorOrder2(tempRid, "error");
                        if (flag)
                            requestQ.remove("R10");

                    } else if (reqNumber.equalsIgnoreCase("R11")) {

                        boolean flag = salesBeatDb.updateOtherActivity(tempRid, "error");
                        if (flag)
                            requestQ.remove("R11");

                    } else if (reqNumber.equalsIgnoreCase("R1")) {

                        boolean val = salesBeatDb.updateBeatVisited(tempRid, did, "error");
                        if (val)
                            requestQ.remove("R1");

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            error.printStackTrace();
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", token);
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() {

                HashMap<String, String> params2 = new HashMap<>();
                params2.put("data", jObjectInput.toString());
                params2.put("api", api);
                params2.put("response", String.valueOf(statusCode));
                return new JSONObject(params2).toString().getBytes();
            }

        };

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }


}
