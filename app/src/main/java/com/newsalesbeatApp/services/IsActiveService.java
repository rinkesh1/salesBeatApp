package com.newsalesbeatApp.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.LoginScreen;
import com.newsalesbeatApp.activities.SplashScreen;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class IsActiveService extends Service {

    private final static int INTERVAL = 1000; //1 sec
    private Timer timer;
    private TimerTask timerTask;
    private String TAG = "IsActiveService";

    public static boolean IsActive =false;

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    /** Called when the service is being created. */
    @Override
    public void onCreate() {

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer();
        timerTask = createTimerTask();
        timer.schedule(timerTask, 1000, 1000);
        return mStartMode;
    }
    private TimerTask createTimerTask() {
        return new TimerTask() {
            public void run() {
                ValidateExpiredToken();
//                Handler h = new Handler(getApplicationContext().getMainLooper());
//                h.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(),"IsActiveService:"+ new Date(),Toast.LENGTH_SHORT).show();
//                    }
//                });
            }
        };
    }
    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        //timer.cancel();
    }

    private void ValidateExpiredToken()
    {
        Context context = getApplicationContext();
        //@Umesh ValidateToken 10-08-2022
        try {
            SharedPreferences prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
            String TokenValidTo = UtilityClass.UTCToLocal("yyyy-MM-dd'T'HH:mm:ss'Z'","yyyy-MM-dd HH:mm:ss",prefSFA.getString("TokenValidTo", ""));;
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date TokenTime = sdf.parse(TokenValidTo);
            Date CurrentTime= new Date();
            long Diff =TokenTime.getTime()-CurrentTime.getTime();
//            Log.d(TAG, "ValidateExpiredToken: "+Diff);
            if(Diff<0)
            {
                ValidateToken();
                //Log.e(TAG, "TokenValidTo: "+TokenTime+" Diff:"+Diff);
            }
            else {
//                Log.e(TAG, "TokenValidTo-1: "+TokenTime+" Diff:"+Diff);
            }
        }
        catch (Exception ex)
        {
            String msg = ex.getMessage();
            Log.e(TAG, "TokenValidToEx: " +msg);
        }
    }
    //@Umesh 10-08-2022
    protected void ValidateToken()
    {
        Log.d("TAG", "ValidateToken");
        Context context = getApplicationContext();
        SharedPreferences prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        String token =prefSFA.getString("token","");
        RequestQueue requestQueue =  Volley.newRequestQueue(context);
        JsonObjectRequest ValidateRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_AuthencateUser , null,
                new Response.Listener<JSONObject>() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getInt("status")==0)
                            {
                                timerTask.cancel();
                                timer.cancel();
                                timer=null;
                                Log.e(TAG, "Token Expired!!: ");

                                Intent intent = new Intent();
                                intent.setClass(context, LoginScreen.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else
                            {
                                JSONObject authtoken= response.getJSONObject("data");
                                if(authtoken!=null)
                                {
                                    String Token=authtoken.getString("token");
                                    String TokenValidTo=authtoken.getString("expiration");
                                    SharedPreferences.Editor Teditor = prefSFA.edit();
                                    Teditor.putString("token", Token);
                                    Teditor.putString("TokenValidTo", TokenValidTo);
                                    Teditor.apply();
                                }
                                else
                                {
                                    timerTask.cancel();
                                    timer.cancel();
                                    timer=null;
                                    Log.e(TAG, "Token Expired!!: ");

                                    SharedPreferences.Editor editor=prefSFA.edit();
                                    editor.putBoolean(getString(R.string.is_logged_in), false);
                                    editor.putString("userStatus","logout");
                                    editor.apply();

                                    Intent intent = new Intent();
                                    intent.setClass(getApplicationContext(), SplashScreen.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    getApplicationContext().startActivity(intent);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                //Sentry.capture(error.getMessage());
            }
        }){
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
}
