package com.newsalesbeatApp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.MyPjpViewCellAdapter;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.pojo.MyPjp;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.sentry.ISpan;
import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.TransactionOptions;

/*
 * Created by MTC on 15-09-2017.
 */

public class MyPjpActivity extends AppCompatActivity {

    public static RecyclerView rvMyPjpList;
    public static TextView tvSelectedView, tvNoPjp;
    public static ProgressBar pbPjp;
    public static Button btnCreatePjp1;
    private static FirebaseAnalytics firebaseAnalytics;
    private final String dateTemplate = "MMMM yyyy";
    String TAG = "MyPjpActivity";
    int MY_SOCKET_TIMEOUT_MS = 50000;
    Calendar _calendar;
    SharedPreferences myPref, tempSfa;
    String date;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    GridView myPjpGridView;
    TextView monthTitle;
    ProgressDialog progressDialog;
    ServerCall serverCall;
    int month, year;
    ArrayList<MyPjp> myPjpArrayList = new ArrayList<>();
    FloatingActionButton btnEditPjp;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.my_pjp_activity);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempSfa = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        myPjpGridView = findViewById(R.id.gridview);
        monthTitle = findViewById(R.id.tvMonthName);
        tvNoPjp = findViewById(R.id.tvNoPjp);
        tvSelectedView = findViewById(R.id.tvSelectedView);
        rvMyPjpList = findViewById(R.id.rvPjpList);
        pbPjp = findViewById(R.id.pbPjp);
        btnEditPjp = findViewById(R.id.btnEditPjp);
        btnCreatePjp1 = findViewById(R.id.btnCreatePjp1);

        ImageView imgBack = findViewById(R.id.imgBack);
//        TextView tvPageTitle = findViewById(R.id.pageTitle);
//        tvPageTitle.setText("Pjp Of Month");

        utilityClass = new UtilityClass(MyPjpActivity.this);
        locationProvider = new GPSLocation(this);
        serverCall = new ServerCall(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //locationProvider.unregisterReceiver();

        //check gps status if on/off
        locationProvider.checkGpsStatus();

        /*For Notification Update*/
        SharedPreferences.Editor editors = tempSfa.edit();
        editors.putString("createpjp", "1");
        editors.apply();

        month = Calendar.getInstance().get(Calendar.MONTH);
        year = Calendar.getInstance().get(Calendar.YEAR);

        showCalender();

        sentryException();
        performNetworkRequest();






//        if (utilityClass.isInternetConnected()) {
//
//            Calendar calendar = Calendar.getInstance();
//            int month = calendar.get(Calendar.MONTH);
//            int year = calendar.get(Calendar.YEAR);
//
//            int m = month + 1;
//            String dateF = "";
//
//            if (month < 10)
//                dateF = year + "/0" + m;
//            else
//                dateF = year + "/" + m;
//
//            getPJPListFromServer(dateF, "out");
//
//        } else {
//
//            initializeCalendar(myPjpArrayList);
//        }


        ImageView previousMonth = findViewById(R.id.imgBackMonth);

        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (month <= 1) {
                    month = 12;
                    year--;
                } else {
                    month--;
                }

                //setGridCellAdapterToDate(month, year);

                if (utilityClass.isInternetConnected()) {

                    int m = month;
                    String dateF = "";

                    if (month < 10)
                        dateF = year + "/0" + m;
                    else
                        dateF = year + "/" + m;

                    //getPJPListFromServer(dateF, "in");

                    getPJPListFromServer(String.valueOf(year), String.valueOf(m),"in");
                } else {

                    initializeCalendar(myPjpArrayList);
                }
            }
        });

        ImageView nextMonth = findViewById(R.id.imgNextMonth);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (month > 11) {
                    month = 1;
                    year++;
                } else {
                    month++;
                }

                Calendar calendar = Calendar.getInstance();
                int curMonth = calendar.get(Calendar.MONTH) + 1;
                int curYear = calendar.get(Calendar.YEAR);

                Log.e(TAG, curMonth + "  " + curYear + "----->" + month + "  " + year);

                if (year < curYear) {

                    //setGridCellAdapterToDate(month, year);
                    if (utilityClass.isInternetConnected()) {

                        int m = month;
                        String dateF = "";

                        if (month < 10)
                            dateF = year + "/0" + m;
                        else
                            dateF = year + "/" + m;

                        //getPJPListFromServer(dateF, "in");
                        //@Umesh 13-March-2022
                        getPJPListFromServer(String.valueOf(year), String.valueOf(m),"in");

                    } else {

                        initializeCalendar(myPjpArrayList);
                    }

                } else if (year == curYear && (month <= curMonth || month == curMonth + 1)) {
                    Log.e(TAG, String.valueOf(month));
                    //setGridCellAdapterToDate(month, year);

                    if (utilityClass.isInternetConnected()) {

                        int m = month;
                        String dateF = "";

                        if (month < 10)
                            dateF = year + "/0" + m;
                        else
                            dateF = year + "/" + m;

                        // getPJPListFromServer(dateF, "in");
                        //@Umesh 13-March-2022
                        getPJPListFromServer(String.valueOf(year), String.valueOf(m),"in");

                    } else {

                        initializeCalendar(myPjpArrayList);
                    }

                } else {

                    if (month <= 1) {
                        month = 12;
                        year--;
                    } else {
                        month--;
                    }

                    Toast.makeText(MyPjpActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnEditPjp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("CreatePjp", "CreatePjpForDates",
//                        "Pjp Created by:"+myPref.getString(getString(R.string.emp_id_key),""));

                Bundle params = new Bundle();
                params.putString("Action", "Create Pjp For Dates");
                params.putString("UserId", "" + myPref.getString(getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("CreatePjp", params);

                Intent intent = new Intent(MyPjpActivity.this, CreatePjp.class);
                intent.putExtra("date", "");
                intent.putExtra("from", "1");
                startActivity(intent);
                finish();

            }
        });

        btnCreatePjp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("CreatePjp", "CreatePjpForDate",
//                        "Pjp Created by:"+myPref.getString(getString(R.string.emp_id_key),""));

                Bundle params = new Bundle();
                params.putString("Action", "Create Pjp For Particular Date");
                params.putString("UserId", "" + myPref.getString(getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("CreatePjp", params);

                Calendar calendar = Calendar.getInstance();
                int curMonth = calendar.get(Calendar.MONTH) + 1;
                if (month >= curMonth) {

                    Intent intent = new Intent(MyPjpActivity.this, CreatePjp.class);
                    intent.putExtra("date", MyPjpViewCellAdapter.date);
                    intent.putExtra("from", "2");
                    startActivity(intent);
                    finish();

                } else {

                    Toast.makeText(MyPjpActivity.this,
                            "You can not create pjp for back month", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPjpActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

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
        ITransaction transaction = Sentry.startTransaction("MyPjpActivity", "task",txOptions);
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
            new PingServer(internet -> {
                /* do something with boolean response */
                if (!internet) {
                    Toast.makeText(MyPjpActivity.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();

                    initializeCalendar(myPjpArrayList);

                } else {

                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    int m = month + 1;
                    String dateF = "";

                    if (month < 10)
                        dateF = year + "/0" + m;
                    else
                        dateF = year + "/" + m;

                    //getPJPListFromServer(dateF, "out");

                    getPJPListFromServer(String.valueOf(year), String.valueOf(m),"out");
                }

            });
        }catch (Exception e){

        }finally {
            innerSpan.finish();
        }
    }


    private void showCalender() {

        MyPjpViewCellAdapter adapter = new MyPjpViewCellAdapter(MyPjpActivity.this, month, year, myPjpArrayList);
        ;
        adapter.notifyDataSetChanged();
        myPjpGridView.setAdapter(adapter);
    }

    private void setGridCellAdapterToDate(int month, int year) {

        MyPjpViewCellAdapter adapter = new MyPjpViewCellAdapter(MyPjpActivity.this, month, year, myPjpArrayList);
        int changedMonth = month - 1;
        Log.e(TAG, String.valueOf(changedMonth));
        _calendar.set(Calendar.MONTH, changedMonth);
        //monthTitle.setText(DateFormat.format(dateTemplate,
        //      _calendar.getTime()));
        String monthName = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (changedMonth >= 0 && changedMonth <= 11) {
            monthName = months[changedMonth];
        }
        monthTitle.setText(monthName + " " + year);
        adapter.notifyDataSetChanged();
        myPjpGridView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    public void onDestroy() {
        try {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    private void getPJPListFromServer(String yr, String mth,String from) {

        progressDialog = new ProgressDialog(MyPjpActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        Log.d(TAG, "---> " + yr+"/"+mth);
        //@Umesh 02-Feb-2022
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_BEAT_PLAN +"?year="+yr+"&month="+mth, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "My PJP ===" + response);
                progressDialog.dismiss();
                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");

                        JSONArray myPjp = data.getJSONArray("beatPlan");
                        myPjpArrayList.clear();

                        for (int i = 0; i < myPjp.length(); i++)
                        {

                            MyPjp pjp = new MyPjp();
//                        ArrayList<MyPjp> myPjpArrayList2 = new ArrayList<>();
                            JSONObject object = (JSONObject) myPjp.get(i);
                            pjp.setDate(object.getString("date"));

//                        JSONArray myPjpArr = object.getJSONArray("pjps");
//                        for (int j = 0; j < myPjpArr.length(); j++) {
//                            JSONObject object1 = (JSONObject) myPjpArr.get(j);
//                            MyPjp myPjp1 = new MyPjp();
//                            myPjp1.setPjpId(object1.getString("pjpid"));
//                            myPjp1.setBeat_id(object1.getString("bid"));
//                            myPjp1.setBeatName(object1.getString("beatName"));
//                            myPjp1.setAssigneeEmp(object1.getString("assigneeEmployee"));
//                            myPjp1.setAssigneeAdmin(object1.getString("assigneeAdmin"));
//                            myPjp1.setDistributor_id(object1.getString("did"));
//                            myPjp1.setDistributorName(object1.getString("distributorName"));
//                            myPjp1.setTownName(object1.getString("town"));
//
//                            myPjpArrayList2.add(myPjp1);
//
//                        }
//
//                        pjp.setMyPjpArrayList(myPjpArrayList2);
                            myPjpArrayList.add(pjp);
                        }
                        if (from.equalsIgnoreCase("out"))
                            initializeCalendar(myPjpArrayList);
                        else
                            setGridCellAdapterToDate(month, year);
                    }
                    else
                    {
                        Toast.makeText(MyPjpActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
//                SbLog.printError(TAG, "getPjps", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
//                        myPref.getString(getString(R.string.emp_id_key), ""));

                serverCall.handleError(error, TAG, "getPjps");

                try {

                    initializeCalendar(myPjpArrayList);

                    error.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(MyPjpActivity.this).add(jsonObjectRequest);
    }

    private void initializeCalendar(ArrayList<MyPjp> myPjpArrayList) {

        // Initialised
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);
        MyPjpViewCellAdapter adapter = new MyPjpViewCellAdapter(MyPjpActivity.this, month, year, myPjpArrayList);
        adapter.notifyDataSetChanged();
        myPjpGridView.setAdapter(adapter);

        monthTitle.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

    }

    public void onBackPressed() {
        MyPjpActivity.this.finish();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
