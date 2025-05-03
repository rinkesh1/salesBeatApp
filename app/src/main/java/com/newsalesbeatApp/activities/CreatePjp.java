package com.newsalesbeatApp.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.PjpAdapter;
import com.newsalesbeatApp.pojo.MyPjp;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by abc on 2/26/19.
 */

public class CreatePjp extends AppCompatActivity implements NetworkChangeInterface {

    public static TextView tvSavePjp;
    private static FirebaseAnalytics firebaseAnalytics;
    private final String[] monthsArr = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    RelativeLayout rlCreatePjp;
    LinearLayout llCreatePjp;
    int month, year;
    int currentMonth = 0;
    private RelativeLayout rlTownList, rlDistributorList, rlBeatList, rlEmpList;
    private LinearLayout llTcPc;
    private EditText edtDate, edtRemarks, edtTc, edtPc, edtSale;
    private ImageView imgDate, imgActivityList, imgTownList, imgDistributorList, imgBeatList, imgEmployeeList;
    private AutoCompleteTextView actvActivityList, actvTownList, actvDistributorList, actvBeatList, actvEmployeeList;
    private Button btnCreatePjp;
    private ArrayList<String> activityList = new ArrayList<>();
    private ArrayList<String> empList = new ArrayList<>();
    private ArrayList<String> empIdList = new ArrayList<>();
    private ArrayList<String> townItems = new ArrayList<>();
    private ArrayList<String> disList = new ArrayList<>();
    private ArrayList<String> didList = new ArrayList<>();
    private ArrayList<String> beatList = new ArrayList<>();
    private ArrayList<String> beaIDtList = new ArrayList<>();
    private SalesBeatDb salesBeatDb;
    private UtilityClass utilityClass;
    private SharedPreferences myPref;
    private int pos = 0;
    private ArrayList<String> months = new ArrayList<>();
    private String did, bid, eid;
    private RecyclerView rvCreatePjp;
    private TextView monthTitle;
    // For Network Change Error
    private boolean createPJPFailure = false;
    private JSONObject createPJPJson = null;

    private boolean getEmpFailure = false;
    private boolean getDistributorFailure = false;
    private String disTown = "";

    private boolean getBeatFailure = false;
    private String didTown = "";

    private boolean getPJPFailure = false;
    private String pjpDate = "";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.create_pjp);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);

        rlTownList = findViewById(R.id.rlTownList);
        rlDistributorList = findViewById(R.id.rlDistributorList);
        rlBeatList = findViewById(R.id.rlBeatList);
        rlEmpList = findViewById(R.id.rlEmpList);
        llTcPc = findViewById(R.id.llTcPc);
        llCreatePjp = findViewById(R.id.llCreatePjp);
        rlCreatePjp = findViewById(R.id.rlCreatePjp);
        edtDate = findViewById(R.id.edtDate);
        edtRemarks = findViewById(R.id.edtRemarksPjp);
        edtTc = findViewById(R.id.edtTcPjp);
        edtPc = findViewById(R.id.edtPcPjp);
        edtSale = findViewById(R.id.edtSalePjp);
        imgDate = findViewById(R.id.imgDate);
        imgActivityList = findViewById(R.id.imgActivityList);
        imgTownList = findViewById(R.id.imgTownList);
        imgDistributorList = findViewById(R.id.imgDistributorList);
        imgBeatList = findViewById(R.id.imgBeatList);
        imgEmployeeList = findViewById(R.id.imgEmployeeList);
        actvActivityList = findViewById(R.id.actvActivityList);
        actvTownList = findViewById(R.id.actvTownList);
        actvDistributorList = findViewById(R.id.actvDistributorList);
        actvBeatList = findViewById(R.id.actvBeatList);
        actvEmployeeList = findViewById(R.id.actvEmployeeList);
        btnCreatePjp = findViewById(R.id.btnCreatePjp);
        tvSavePjp = findViewById(R.id.tvSavePjp);
        rvCreatePjp = findViewById(R.id.rvCreatePjp);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);
        monthTitle = findViewById(R.id.tvMonthName);

        tvPageTitle.setText("Create Pjp");

        activityList.add("Retailing");
        activityList.add("Joint Working");
        activityList.add("Distributor Search");
        activityList.add("Market Survey");
        activityList.add("Meeting");
        activityList.add("Office Visit");
        activityList.add("Leave");
        activityList.add("Weekly Off");
        activityList.add("Holiday");

        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");

        // Network Change Receiver
        NetworkChangeReceiver receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        receiver.InitNetworkListener(this);

        rlTownList.setVisibility(View.GONE);
        rlDistributorList.setVisibility(View.GONE);
        rlBeatList.setVisibility(View.GONE);
        rlEmpList.setVisibility(View.GONE);
        llTcPc.setVisibility(View.GONE);

        salesBeatDb = SalesBeatDb.getHelper(this);
        utilityClass = new UtilityClass(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        tvSavePjp.setClickable(false);


        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, activityList);

        actvActivityList.setAdapter(activityAdapter);

        String date = getIntent().getStringExtra("date");
        String from = getIntent().getStringExtra("from");
        if (from.equalsIgnoreCase("1")) {
            rlCreatePjp.setVisibility(View.VISIBLE);
            llCreatePjp.setVisibility(View.GONE);
        } else {
            rlCreatePjp.setVisibility(View.GONE);
            llCreatePjp.setVisibility(View.VISIBLE);
        }

        Log.d("CreatePjp", "  Date: " + date);

        if (date == null || date.isEmpty()) {

            SimpleDateFormat sdf = utilityClass.getYMDDateFormat();
            String currentDate = sdf.format(Calendar.getInstance().getTime());

            edtDate.setText(currentDate);

        } else {

//            String[] tD = date.split("-");
//            String day = tD[0];
//            String m = tD[1];
//            int month = months.indexOf(m);
//            month = month + 1;
//            String yr = tD[2];
//            if (month < 10)
//                edtDate.setText(day + "-0" + month + "-" + yr);
//            else
            edtDate.setText(date);
        }

        Calendar calendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        currentMonth = month;
        year = calendar.get(Calendar.YEAR);

        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(CreatePjp.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                intitializeCreatePjpList(month, year);
            } else {

                String dateF = "";

                if (month < 10)
                    dateF = year + "/0" + month;
                else
                    dateF = year + "/" + month;

                getPJPListFromServer(dateF);
            }

        });

//        if (utilityClass.isInternetConnected()) {
//
//            String dateF = "";
//
//            if (month < 10)
//                dateF = year + "/0" + month;
//            else
//                dateF = year + "/" + month;
//
//            getPJPListFromServer(dateF);
//
//        } else {
//
//            intitializeCreatePjpList(month, year);
//        }


        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        ImageView previousMonth = findViewById(R.id.imgBackMonth);

        previousMonth.setClickable(false);
        previousMonth.setColorFilter(ContextCompat.getColor(this,
                R.color.material_ripple_light), android.graphics.PorterDuff.Mode.MULTIPLY);

        previousMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (month > currentMonth) {

                    previousMonth.setClickable(true);
                    previousMonth.setColorFilter(ContextCompat.getColor(CreatePjp.this,
                            R.color.textColor), android.graphics.PorterDuff.Mode.MULTIPLY);


                    if (month <= 1) {
                        month = 12;
                        year--;
                    } else {
                        month--;
                    }


                    if (utilityClass.isInternetConnected()) {

                        String dateF = "";

                        if (month < 10)
                            dateF = year + "/0" + month;
                        else
                            dateF = year + "/" + month;

                        getPJPListFromServer(dateF);

                    } else {

                        intitializeCreatePjpList(month, year);
                    }

                    if (month == currentMonth) {

                        previousMonth.setClickable(false);
                        previousMonth.setColorFilter(ContextCompat.getColor(CreatePjp.this,
                                R.color.material_ripple_light), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }

                } else {

                    previousMonth.setClickable(false);
                    previousMonth.setColorFilter(ContextCompat.getColor(CreatePjp.this,
                            R.color.material_ripple_light), android.graphics.PorterDuff.Mode.MULTIPLY);
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

                Log.e("CreatePjp", curMonth + "  " + curYear + "----->" + month + "  " + year);

                if (month > curMonth) {

                    previousMonth.setClickable(true);
                    previousMonth.setColorFilter(ContextCompat.getColor(CreatePjp.this,
                            R.color.textColor), android.graphics.PorterDuff.Mode.MULTIPLY);

                }

                if (year < curYear) {

                    if (utilityClass.isInternetConnected()) {

                        String dateF = "";

                        if (month < 10)
                            dateF = year + "/0" + month;
                        else
                            dateF = year + "/" + month;

                        getPJPListFromServer(dateF);

                    } else {

                        intitializeCreatePjpList(month, year);
                    }


                } else if (year == curYear && (month <= curMonth || month == curMonth + 1)) {

                    Log.e("CreatePjp", String.valueOf(month));

                    if (utilityClass.isInternetConnected()) {

                        String dateF = "";

                        if (month < 10)
                            dateF = year + "/0" + month;
                        else
                            dateF = year + "/" + month;

                        getPJPListFromServer(dateF);

                    } else {

                        intitializeCreatePjpList(month, year);
                    }

                } else {

                    if (month <= 1) {
                        month = 12;
                        year--;
                    } else {
                        month--;
                    }
                }
            }
        });


        tvSavePjp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(CreatePjp.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {

                        syncPjp();
                    }

                });
            }
        });


        actvActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pos = position;
                if (position == 0) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.VISIBLE);
                    rlBeatList.setVisibility(View.VISIBLE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.VISIBLE);

                    resetByActivity();
                    getTownList();

                } else if (position == 1) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.VISIBLE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.VISIBLE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();
                    getTownList();

                    getEmpList();

                } else if (position == 2) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();
                    getTownList();

                } else if (position == 3) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();
                    getTownList();

                } else if (position == 4) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();
                    getTownList();

                } else if (position == 5) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();

                } else if (position == 6) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();

                } else if (position == 7) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();

                } else if (position == 8) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    resetByActivity();

                }
            }
        });

        actvActivityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvActivityList.setError(null);
                actvActivityList.showDropDown();
            }
        });

        imgActivityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvActivityList.setError(null);
                actvActivityList.showDropDown();
            }
        });


        actvTownList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvTownList.setError(null);
                actvTownList.showDropDown();
            }
        });

        imgTownList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvTownList.setError(null);
                actvTownList.showDropDown();
            }
        });

        actvTownList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (pos == 0 || pos == 1) {

                    String townName = townItems.get(position);

                    getDisList(townName);

                }

                resetByTown();

            }
        });

        actvDistributorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvDistributorList.setError(null);
                actvDistributorList.showDropDown();
            }
        });

        actvDistributorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                did = didList.get(position);
                if (pos == 0)
                    getBeatList(did);

                resetByDistributor();

            }
        });

        imgDistributorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvDistributorList.setError(null);
                actvDistributorList.showDropDown();
            }
        });

        actvBeatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvBeatList.setError(null);
                actvBeatList.showDropDown();
            }
        });

        actvBeatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bid = beaIDtList.get(position);
            }
        });

        imgBeatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvBeatList.setError(null);
                actvBeatList.showDropDown();
            }
        });

        actvEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvEmployeeList.setError(null);
                actvEmployeeList.showDropDown();
            }
        });

        actvEmployeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eid = empIdList.get(position);
            }
        });

        imgEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvEmployeeList.setError(null);
                actvEmployeeList.showDropDown();
            }
        });


        btnCreatePjp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(CreatePjp.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        syncPjpForDate();
                    }
                });
            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CreatePjp.this, MyPjpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void syncPjpForDate() {
        String tDate = edtDate.getText().toString();
        String[] dateArr = tDate.split("-");
        String date = dateArr[0] + "-" + dateArr[1] + "-" + dateArr[2];
        String activity = actvActivityList.getText().toString();
        String town = actvTownList.getText().toString();
        String distributor = actvDistributorList.getText().toString();
        String beat = actvBeatList.getText().toString();
        String emp = actvEmployeeList.getText().toString();
        String remarks = edtRemarks.getText().toString();

        if (!date.isEmpty() && !activity.isEmpty()) {

            JSONArray pjpArr = new JSONArray();

            final JSONObject reqEntity = new JSONObject();

            try {

                // 1 Retailing
                // 2 Meeting
                // 3 Joint Working
                // 4 Distributor Search
                // 5 Leave
                // 6 Weak Off
                // 7 Market Survey
                // 8 Holiday
                // 9 Office Visit

                String actId = "";

                reqEntity.put("date", date);

                if (activity.equalsIgnoreCase("Retailing")) {

                    actId = "1";

                    if (!town.isEmpty() && !distributor.isEmpty() && !beat.isEmpty()
                            && !edtTc.getText().toString().isEmpty() && !edtPc.getText().toString().isEmpty()
                            && !edtSale.getText().toString().isEmpty()) {

                        int tcN = 0, pcN = 0;
                        try {

                            tcN = Integer.parseInt(edtTc.getText().toString());
                            pcN = Integer.parseInt(edtPc.getText().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (tcN > pcN) {

                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);
                            reqEntity.put("distributor", did);
                            reqEntity.put("beat", bid);
                            reqEntity.put("tc", edtTc.getText().toString());
                            reqEntity.put("pc", edtPc.getText().toString());
                            reqEntity.put("sale", edtSale.getText().toString());
                            reqEntity.put("remarks", remarks);

                            pjpArr.put(reqEntity);

                            final JSONObject createBeatPlan = new JSONObject();

                            try {

                                createBeatPlan.put("pjps", pjpArr);

                            } catch (Exception e) {
                                Log.e("CreatePjp", "==" + e.getMessage());
                            }

                            Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());

                            createPjp(createBeatPlan);

                        } else {

                            Toast.makeText(this, "PC can not greater then TC", Toast.LENGTH_SHORT).show();
                        }


                    } else if (town.isEmpty()) {

                        actvTownList.setError("Field can't be empty");
                        actvTownList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Town can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (distributor.isEmpty()) {

                        actvDistributorList.setError("Field can't be empty");
                        rlDistributorList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Distributor can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (beat.isEmpty()) {

                        actvBeatList.setError("Field can't be empty");
                        actvBeatList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Beat can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (edtTc.getText().toString().isEmpty()) {

                        edtTc.setError("Tc can't be empty");
                        edtTc.requestFocus();

                    } else if (edtPc.getText().toString().isEmpty()) {

                        edtPc.setError("Pc can't be empty");
                        edtPc.requestFocus();

                    } else if (edtSale.getText().toString().isEmpty()) {

                        edtSale.setError("Sale can't be empty");
                        edtSale.requestFocus();

                    } else {

                        Toast.makeText(CreatePjp.this, "Please provide all details", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Meeting")) {

                    actId = "2";

                    if (!town.isEmpty()) {

                        reqEntity.put("activity", actId);
                        reqEntity.put("town", town);
                        reqEntity.put("remarks", remarks);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                        createPjp(createBeatPlan);

                    } else {

                        actvTownList.setError("Please provide town name");
                        actvTownList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Please provide town name", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Joint Working")) {

                    actId = "3";

                    if (!town.isEmpty() && !distributor.isEmpty() && !emp.isEmpty()) {

                        reqEntity.put("activity", actId);
                        reqEntity.put("town", town);
                        reqEntity.put("distributor", did);
                        reqEntity.put("jw_eid", eid);
                        reqEntity.put("remarks", remarks);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                        createPjp(createBeatPlan);

                    } else if (town.isEmpty()) {

                        actvTownList.setError("Please provide town name");
                        actvTownList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Please provide town name", Toast.LENGTH_SHORT).show();

                    } else if (distributor.isEmpty()) {

                        actvDistributorList.setError("Please provide Distributor name");
                        actvDistributorList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Please provide Distributor", Toast.LENGTH_SHORT).show();

                    } else if (emp.isEmpty()) {

                        actvEmployeeList.setError("Please provide Employee name");
                        actvEmployeeList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Please provide Employee name", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(CreatePjp.this, "Please provide all details", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Distributor Search")) {

                    actId = "4";

                    if (!town.isEmpty()) {

                        reqEntity.put("activity", actId);
                        reqEntity.put("town", town);
                        reqEntity.put("remarks", remarks);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                        createPjp(createBeatPlan);

                    } else {
                        actvTownList.setError("Please provide town name");
                        actvTownList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Please provide town name", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Leave")) {

                    actId = "5";

                    if (!remarks.isEmpty()) {

                        reqEntity.put("activity", actId);
                        //reqEntity.put("town", town);
                        reqEntity.put("remarks", remarks);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());

                        createPjp(createBeatPlan);

                    } else {

                        edtRemarks.setError("Remarks can't be empty");
                        edtRemarks.requestFocus();
//                        Toast.makeText(CreatePjp.this, "Please provide remarks", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Weekly Off")) {

                    actId = "6";
                    if (!remarks.isEmpty()) {

                        reqEntity.put("activity", actId);
                        //reqEntity.put("town", town);
                        reqEntity.put("remarks", remarks);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                        createPjp(createBeatPlan);

                    } else {

                        edtRemarks.setError("Remarks can't be empty");
                        edtRemarks.requestFocus();
//                        Toast.makeText(CreatePjp.this, "Please provide remarks", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Market Survey")) {

                    actId = "7";
                    if (!town.isEmpty()) {

                        reqEntity.put("activity", actId);
                        reqEntity.put("town", town);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                        createPjp(createBeatPlan);

                    } else {

                        actvTownList.setError("Please provide Town name");
                        actvTownList.requestFocus();
                        Toast.makeText(CreatePjp.this, "Please provide town name", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Holiday")) {

                    actId = "8";

                    if (!remarks.isEmpty()) {

                        reqEntity.put("activity", actId);
                        //reqEntity.put("town", town);
                        reqEntity.put("remarks", remarks);

                        pjpArr.put(reqEntity);

                        final JSONObject createBeatPlan = new JSONObject();

                        try {

                            createBeatPlan.put("pjps", pjpArr);

                        } catch (Exception e) {
                            Log.e("CreatePjp", "==" + e.getMessage());
                        }

                        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                        createPjp(createBeatPlan);

                    } else {

                        edtRemarks.setError("Remarks can't be empty");
                        edtRemarks.requestFocus();
//                        Toast.makeText(CreatePjp.this, "Please provide remarks", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Office Visit")) {

                    actId = "9";
                    reqEntity.put("activity", actId);
                    //reqEntity.put("town", town);
                    reqEntity.put("remarks", remarks);

                    pjpArr.put(reqEntity);

                    final JSONObject createBeatPlan = new JSONObject();

                    try {

                        createBeatPlan.put("pjps", pjpArr);

                    } catch (Exception e) {
                        Log.e("CreatePjp", "==" + e.getMessage());
                    }

                    Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());


                    createPjp(createBeatPlan);

                }

            } catch (Exception e) {
                Log.e("CreatePjp", "==" + e.getMessage());
            }

        } else if (date.isEmpty()) {

            edtDate.setError("Date Required");
            actvActivityList.requestFocus();
            Toast.makeText(CreatePjp.this, "Date required", Toast.LENGTH_SHORT).show();

        } else if (activity.isEmpty()) {

            actvActivityList.setError("Activity Required");
            actvActivityList.requestFocus();
            Toast.makeText(CreatePjp.this, "Activity required", Toast.LENGTH_SHORT).show();

        }

//        else {
//            Toast.makeText(CreatePjp.this, "Date & Activity required", Toast.LENGTH_SHORT).show();
//        }
    }

    private void syncPjp() {

        Cursor cursor = null;
        JSONArray pjpArr = new JSONArray();

        try {

            cursor = salesBeatDb.getAllPjp();


            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    String date = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_PJP_DATE));
                    String activity = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_ACTIVITY));
                    String town = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_TOWN));
                    String did = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRIBUTOR_ID));
                    String distributor = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRIBUTOR));
                    String bid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_BEAT_ID));
                    String beat = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_BEAT));
                    String eid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_EMP_ID));
                    String emp = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_EMP));
                    String tc = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_TC_PJP));
                    String pc = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_PC_PJP));
                    String sale = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SALE_PJP));
                    String remarks = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_OTHER_ACTIVITY_REMARKS));

                    final JSONObject reqEntity = new JSONObject();

                    try {

                        // 1 Retailing
                        // 2 Meeting
                        // 3 Joint Working
                        // 4 Distributor Search
                        // 5 Leave
                        // 6 Weak Off
                        // 7 Market Survey
                        // 8 Holiday
                        // 9 Office Visit

                        String actId = "";

                        reqEntity.put("date", date);
                        //reqEntity.put("eid", myPref.getString(getString(R.string.emp_id_key), ""));
                        //String activity = pjpList.get(i).getActivity();

                        if (activity.equalsIgnoreCase("Retailing")) {

                            actId = "1";

                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);
                            reqEntity.put("distributor", did);
                            reqEntity.put("beat", bid);
                            reqEntity.put("tc", tc);
                            reqEntity.put("pc", pc);
                            reqEntity.put("sale", sale);

                        } else if (activity.equalsIgnoreCase("Meeting")) {

                            actId = "2";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        } else if (activity.equalsIgnoreCase("Joint Working")) {

                            actId = "3";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);
                            reqEntity.put("distributor", did);
                            reqEntity.put("jw_eid", eid);

                        } else if (activity.equalsIgnoreCase("Distributor Search")) {

                            actId = "4";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        } else if (activity.equalsIgnoreCase("Leave")) {

                            actId = "5";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        } else if (activity.equalsIgnoreCase("Weak Off")) {

                            actId = "6";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        } else if (activity.equalsIgnoreCase("Market Survey")) {

                            actId = "7";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        } else if (activity.equalsIgnoreCase("Holiday")) {

                            actId = "8";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        } else if (activity.equalsIgnoreCase("Office Visit")) {

                            actId = "9";
                            reqEntity.put("activity", actId);
                            reqEntity.put("town", town);

                        }

                        reqEntity.put("remarks", remarks);

                    } catch (Exception e) {
                        Log.e("CreatePjp", "==" + e.getMessage());
                    }

                    pjpArr.put(reqEntity);

                } while (cursor.moveToNext());

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("CreatePjp", "==" + e.getMessage());

        } finally {
            if (cursor != null)
                cursor.close();
        }


        final JSONObject createBeatPlan = new JSONObject();

        try {

            createBeatPlan.put("pjps", pjpArr);

        } catch (Exception e) {
            Log.e("CreatePjp", "==" + e.getMessage());
        }

        Log.e("CreatePjp", "JSon==>> " + createBeatPlan.toString());

        createPjp(createBeatPlan);
    }

    private void intitializeCreatePjpList(int month, int year) {

        ArrayList<MyPjp> myPjpArrayList = new ArrayList<>();

        PjpAdapter adapter = new PjpAdapter(CreatePjp.this, myPjpArrayList, month, year);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvCreatePjp.setLayoutManager(layoutManager);
        //rvCreatePjp.addItemDecoration(new GridDividerDecoration2(this));
        rvCreatePjp.setAdapter(adapter);

        monthTitle.setText(monthsArr[month - 1] + " " + year);
    }

    public void onBackPressed() {

        Intent intent = new Intent(CreatePjp.this, MyPjpActivity.class);
        startActivity(intent);
        finish();
    }

    private void createPjp(JSONObject reqEntity) {

        btnCreatePjp.setText("Creating...");
        btnCreatePjp.setClickable(false);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Working on...");
        progressDialog.show();

        JsonObjectRequest newlyAddedRetailerRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_CREATE_PJP, reqEntity, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                createPJPFailure = false;

                btnCreatePjp.setClickable(true);
                btnCreatePjp.setText("Create");
                progressDialog.dismiss();

                Log.e("CreatePjp", "Response: " + response.toString());
                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {

//                        SBApplication.getInstance().trackEvent("CreatePjp", "PjpCreated",
//                                "Pjp Successfully Created by:"+myPref.getString(getString(R.string.emp_id_key),"")
//                        +" on the date: "+edtDate.getText().toString());


                        Bundle params = new Bundle();
                        params.putString("Action", "Create Pjp");
                        params.putString("UserId", "" + myPref.getString(getString(R.string.emp_id_key), ""));
                        params.putString("Message", "Pjp created successfully");
                        params.putString("date", "" + edtDate.getText().toString());
                        firebaseAnalytics.logEvent("PjpCreated", params);

                        salesBeatDb.deleteAllPjp();

                        Toast.makeText(CreatePjp.this, "Created Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreatePjp.this, MyPjpActivity.class);
                        startActivity(intent);
                        finish();

                    }
                    else {
                        Toast.makeText(CreatePjp.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO create pjp error
                if (!utilityClass.isInternetConnected()) {
                    createPJPFailure = true;
                    createPJPJson = reqEntity;
                } else {
                    createPJPFailure = false;
                }

                btnCreatePjp.setClickable(true);
                btnCreatePjp.setText("Create");
                progressDialog.dismiss();
                try {

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
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        newlyAddedRetailerRequest.setShouldCache(false);
        newlyAddedRetailerRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(newlyAddedRetailerRequest);
    }

    private void showDateDialog() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                int m = month + 1;
                if (m < 10)
                    edtDate.setText(dayOfMonth + "-0" + m + "-" + year);
                else
                    edtDate.setText(dayOfMonth + "-" + m + "-" + year);

            }
        }, year, month, day);


        datePickerDialog.show();
    }

    private void getEmpList() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_LIST + myPref.getString(getString(R.string.zone_id_key), "") + "/employees",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                getEmpFailure = false;

                Log.e("CreatePJP", "onResponse JOINT WORKING EMP LIST===" + response);

                try {

                    empIdList.clear();
                    empList.clear();

                    JSONObject data = response.getJSONObject("data");
                    JSONArray emp = data.getJSONArray("employees");

                    for (int index = 0; index < emp.length(); index++) {
                        JSONObject list = (JSONObject) emp.get(index);

                        String eid = list.getString("eid");
                        if (!myPref.getString(getString(R.string.emp_id_key), "").equalsIgnoreCase(eid)) {

                            empIdList.add(eid);
                            empList.add(list.getString("name"));

                        }

                    }

                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        ArrayAdapter empAdapter = new ArrayAdapter<>(CreatePjp.this,
                                android.R.layout.simple_spinner_dropdown_item, empList);

                        actvEmployeeList.setAdapter(empAdapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO get emp list error
                if (!utilityClass.isInternetConnected()) {
                    getEmpFailure = true;
                } else {
                    getEmpFailure = false;
                }

                try {
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("CreatePjp", "Error===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(CreatePjp.this).add(jsonObjectRequest);

    }

    private void getTownList() {

        new LoadTownList().execute();
    }

    private void getDisList(String townName) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DISTRIBUTORS_2 + "?town=" + townName,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                getDistributorFailure = false;

                Log.e("CreatePJP", "onResponse Dis LIST===" + response);

                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {

                        didList.clear();
                        disList.clear();
                        JSONArray distributors = response.getJSONArray("data");
                        for (int i = 0; i < distributors.length(); i++) {


                            JSONObject obj = (JSONObject) distributors.get(i);

                            //JSONObject zoneObj = obj.getJSONObject("zone");

//                            DistrebutorItem item = new DistrebutorItem();
//                            item.setDistrebutorName();
//                            item.setDistrebutorId(obj.getString("did"));

                            didList.add(obj.getString("did"));
                            disList.add(obj.getString("name"));


                        }

                        ArrayAdapter disAdapter = new ArrayAdapter<>(CreatePjp.this,
                                android.R.layout.simple_spinner_dropdown_item, disList);

                        actvDistributorList.setAdapter(disAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!utilityClass.isInternetConnected()) {
                    getDistributorFailure = true;
                    disTown = townName;
                } else {
                    getDistributorFailure = false;
                }

                try {
                    // TODO get distributor list error

                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("CreatePjp", "Error===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(CreatePjp.this).add(jsonObjectRequest);

    }

    private void getBeatList(String did) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_BEATS_2 + "did=" + did,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                getEmpFailure = false;
                Log.e("CreatePJP", "onResponse Dis LIST===" + response);

                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {

                        beaIDtList.clear();
                        beatList.clear();

                        JSONObject data = response.getJSONObject("data");
                        JSONArray beats = data.getJSONArray("beats");
                        for (int i = 0; i < beats.length(); i++) {

                            JSONObject obj = (JSONObject) beats.get(i);
//                            BeatItem beatItem = new BeatItem();
//
//                            beatItem.setBeatName(obj.getString("name"));
//                            beatItem.setBeatId(obj.getString("bid"));

                            beaIDtList.add(obj.getString("bid"));
                            beatList.add(obj.getString("name"));

//                            salesBeatDb.insertBeatList2(obj.getString("bid"),
//                                    obj.getString("name"), "", obj.getString("updated_at"));

                        }

                        ArrayAdapter beatAdapter = new ArrayAdapter<>(CreatePjp.this,
                                android.R.layout.simple_spinner_dropdown_item, beatList);

                        actvBeatList.setAdapter(beatAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO get beat list error
                if (!utilityClass.isInternetConnected()) {
                    getBeatFailure = true;
                    didTown = did;
                } else {
                    getBeatFailure = false;
                }

                try {

                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("CreatePjp", "Error===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(CreatePjp.this).add(jsonObjectRequest);

    }

    private void getPJPListFromServer(String date) {

        ProgressDialog progressDialog = new ProgressDialog(CreatePjp.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        //@Umesh 02-Feb-2022
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_BEAT_PLAN_BY_DATE +"?date="+date, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                getPJPFailure = false;

                Log.e("onResponse", "My PJP ===" + response);
                progressDialog.dismiss();
                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");

                        JSONArray myPjp = data.getJSONArray("beatPlan");
                        ArrayList<MyPjp> myPjpArrayList = new ArrayList<>();

                        for (int i = 0; i < myPjp.length(); i++) {

                            MyPjp pjp = new MyPjp();
                            JSONObject object = (JSONObject) myPjp.get(i);
                            pjp.setDate(object.getString("date"));
                            pjp.setPjpId(object.getString("id"));
                            pjp.setAssigneeEmp("");//(object.getString("assigneeEmployee"));
                            pjp.setAssigneeAdmin("");//(object.getString("assigneeAdmin"));
                            pjp.setActivity(object.getString("activity"));
                            pjp.setTc(object.getString("tc"));
                            pjp.setPc(object.getString("pc"));
                            pjp.setSale(object.getString("sale"));
                            String town = object.getString("town");
                            if (town != null && !town.equalsIgnoreCase("null"))
                                pjp.setTownName(town);

                            if (!object.isNull("beats") && object.has("beats")) {

                                JSONObject beat = object.getJSONObject("beats");
                                pjp.setBeat_id(beat.getString("bid"));
                                pjp.setBeatName(beat.getString("name"));
                            }

                            if (!object.isNull("distributors") && object.has("distributors")) {

                                JSONObject distributor = object.getJSONObject("distributors");
                                pjp.setDistributor_id(distributor.getString("did"));
                                String townn = distributor.getString("town");
                                if (townn != null && !townn.equalsIgnoreCase("null"))
                                    pjp.setTownName(townn);
                                pjp.setDistributorName(distributor.getString("name"));
                            }

                            if (!object.isNull("joint_workings") && object.has("joint_workings")) {
                                JSONObject jwemployee = object.getJSONObject("joint_workings");
                                pjp.setJointworkingwith(jwemployee.getString("name"));
                            }
                            myPjpArrayList.add(pjp);
                        }
                        initializeCalendar(myPjpArrayList);
                    }
                    else
                    {
                        Toast.makeText(CreatePjp.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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

                // serverCall.handleError(error, "MyPjpViewCellAdapter", "getPjps");\


                // TODO get pjp error
                if (!utilityClass.isInternetConnected()) {
                    pjpDate = date;
                    getPJPFailure = true;
                } else {
                    getPJPFailure = false;
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
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(CreatePjp.this).add(jsonObjectRequest);
    }

    private void initializeCalendar(ArrayList<MyPjp> myPjpArrayList) {

        PjpAdapter adapter = new PjpAdapter(CreatePjp.this, myPjpArrayList, month, year);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvCreatePjp.setLayoutManager(layoutManager);
        //rvCreatePjp.addItemDecoration(new GridDividerDecoration2(this));
        rvCreatePjp.setAdapter(adapter);

        monthTitle.setText(monthsArr[month - 1] + " " + year);
    }

    private void resetByActivity() {

        actvTownList.setText("");
        resetByTown();

    }

    private void resetByTown() {

        actvDistributorList.setText("");
        resetByDistributor();

    }

    private void resetByDistributor() {

        actvEmployeeList.setText("");
        actvBeatList.setText("");
        edtTc.setText("");
        edtPc.setText("");
        edtRemarks.setText("");
        edtSale.setText("");

        actvTownList.setError(null);
        actvDistributorList.setError(null);
        actvBeatList.setError(null);
        actvEmployeeList.setError(null);
    }

    @Override
    public void connectionChange(boolean status) {

        if (status) {
//            Toast.makeText(this, "You are connected", Toast.LENGTH_SHORT).show();

            if (createPJPFailure) {
                createPjp(createPJPJson);
            }

            if (getEmpFailure) {
                getEmpList();
            }

            if (getDistributorFailure) {
                getDisList(disTown);
            }

            if (getBeatFailure) {
                getBeatList(didTown);
            }

            if (getPJPFailure) {
                getPJPListFromServer(pjpDate);
            }

        } else {
            Toast.makeText(this, "You are disconnected", Toast.LENGTH_SHORT).show();
        }
    }

    private class LoadTownList extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            Cursor cursor = null;

            try {
                cursor = salesBeatDb.getAllRecordFromTownListTable();
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    do {
                        townItems.add(cursor.getString(cursor.getColumnIndex("town_name")));
                    } while (cursor.moveToNext());

                    Collections.sort(townItems, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                }

            } catch (Exception e) {
                e.getMessage();
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
        protected void onPostExecute(ArrayList<String> townItems) {
            super.onPostExecute(townItems);

            if (townItems.size() > 0) {

                ArrayAdapter townAdapter = new ArrayAdapter<>(CreatePjp.this,
                        android.R.layout.simple_spinner_dropdown_item, townItems);

                actvTownList.setAdapter(townAdapter);

            }
        }
    }

}
