package com.newsalesbeatApp.receivers;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.SplashScreen;
import com.newsalesbeatApp.netwotkcall.VolleyMultipartRequest;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ACTIVITY_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    String TAG = "NotificationReceiver";
    SalesBeatDb salesBeatDb;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String action = intent.getStringExtra("action");
        Intent intent1 = intent.getParcelableExtra("noti_intent");
        salesBeatDb = SalesBeatDb.getHelper(context);
        this.context = context;

        SharedPreferences.Editor editor = context.getSharedPreferences(context.getString(R
                .string.pref_name), Context.MODE_PRIVATE).edit();
        editor.putString("userStatus", action);
        editor.apply();

        if (intent1 != null) {
            getActivity().startActivity(intent1);
        } else {
            assert action != null;
            if (action.equalsIgnoreCase("disableEmployee")) {
                showNotificationDialog(title, body, action);
            } else if (action.equalsIgnoreCase("logout")
                    || action.equalsIgnoreCase("fullReset")
                    || action.equalsIgnoreCase("clearData")) {

                clearAppData();

                Intent intentt = new Intent(getActivity(), SplashScreen.class);
                getActivity().startActivity(intentt);
                getActivity().finishAffinity();

    //        } else if (action.equalsIgnoreCase("fullReset")) {
    //
    //            clearAppData();
    //            Intent intentt = new Intent(getActivity(), SplashScreen.class);
    //            getActivity().startActivity(intentt);
    //            getActivity().finishAffinity();
    //
    //        } else if (action.equalsIgnoreCase("clearData")) {
    //
    //            clearAppData();
    //            Intent intentt = new Intent(getActivity(), SplashScreen.class);
    //            getActivity().startActivity(intentt);
    //            getActivity().finishAffinity();

            } else if (action.equalsIgnoreCase("sendLog")) {
                Log.d(TAG, "check log file");
                UtilityClass utilityClass = new UtilityClass(context);
                //SalesBeatDb salesBeatDb = new SalesBeatDb(context);

                if (utilityClass.isInternetConnected()) {
                    salesBeatDb.getMyDB();
                    sendDb(context);
                }

            } else if (action.equalsIgnoreCase("clearOldDateRecord")) {

                deleteAllDateRecord();
            }
        }
    }

    private void clearAppData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = context.getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear " + packageName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDb(final Context context) {

        final VolleyMultipartRequest request = new VolleyMultipartRequest(Request.Method.POST, SbAppConstants.API_SUBMIT_DB,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {

                            JSONObject obj = new JSONObject(new String(response.data));
                            Log.e(TAG, "Data Response: " + obj.toString());

                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("success")) {

                            }

                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    SbLog.printError(TAG, "submitDb", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                            context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE).getString(context.getString(R.string.emp_id_key), ""));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", context.getSharedPreferences(context.getString(R
                        .string.pref_name), Context.MODE_PRIVATE).getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart("db", getFileDataFromDrawable()));

                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        Volley.newRequestQueue(context).add(request);
    }

    private byte[] getFileDataFromDrawable() {

        try {
            String PATH = Environment.getExternalStorageDirectory() + "/CMNY/sb.db";
            File file = new File(PATH);
            //init array with file length
            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            return bytesArray;

        } catch (Exception e) {

            e.getMessage();
        }

        return null;
    }

    private void deleteAllDateRecord() {
        try {

            Calendar cal = Calendar.getInstance();
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

            salesBeatDb.deleteAllDataFromOderPlacedByRetailersTable3(date);
            salesBeatDb.deleteSpecificDataFromNewRetailerListTable3(date);
            salesBeatDb.deleteSpecificDataFromNewOrderEntryListTable3(date);
            salesBeatDb.deleteSpecificNewRetailerFromOrderPlacedByNewRetailersTable3(date);
            salesBeatDb.deleteSpecificDataFromOrderEntryListTable3(date);
            salesBeatDb.deleteAllDataFromSkuEntryListTable3(date);
            salesBeatDb.deleteAllFromDistributorOrderTable3(date);
            salesBeatDb.deleteAllDataFromNewDistributorTable3(date);
            salesBeatDb.deleteOtherActivity3(date);

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        }
    }

    private void showNotificationDialog(String title, String body, final String action) {

        final Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.notification_dialog);
        if (mDialog.getWindow() != null)
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(false);
        TextView tvNotificationTitle = mDialog.findViewById(R.id.tvNotificationTitle);
        TextView tvNotificationBody = mDialog.findViewById(R.id.tvNotificationBody);
        Button btnOk = mDialog.findViewById(R.id.btnNotiOk);

        tvNotificationTitle.setText(title);
        tvNotificationBody.setText(body);

        tvNotificationBody.setMovementMethod(new ScrollingMovementMethod());

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                if (action.equalsIgnoreCase("disableEmployee")) {
                    Intent intent = new Intent(getActivity(), SplashScreen.class);
                    getActivity().startActivity(intent);
                    getActivity().finishAffinity();
                }
            }
        });

        mDialog.show();
    }

    public Activity getActivity() {

        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

}
