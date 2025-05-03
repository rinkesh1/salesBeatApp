package com.newsalesbeatApp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.EmloyeeListAdapter;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class JointWorking extends AppCompatActivity implements NetworkChangeInterface {

    private static FirebaseAnalytics firebaseAnalytics;
    ArrayList<String> empId = new ArrayList<>();
    ArrayList<String> empName = new ArrayList<>();
    ArrayList<String> empPhone = new ArrayList<>();
    LinearLayout llProceed, llAddEditText;
    ImageView addOtherView;
    CheckBox chbCoverageDown, chbAnyOther, chbNewMarketLaunch, chbPlacementDown,
            chbSpecialSkuFocus, chbQPSSchemeFocus, chbNewConsumerOffer;
    TextView tvStartTime, tvEndTime, tvProceed;
    RecyclerView empListRecyclerView;
    LinearLayout llEmpList, llReasonList;
    EditText edtComment;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    boolean anyOther = false;
    private SharedPreferences myPref, temppref;
    private String TAG = "JointWorking";
    // Network Error
    private NetworkChangeReceiver receiver = new NetworkChangeReceiver();
    private Boolean getEmpFailure = false;
    private Boolean jointWorkingFailure = false;

    private ArrayList<String> jointWorkingReason;
    private String jointWorkingStart = "";
    private String jointWorkingComment = "";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.joint_working);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        temppref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        setUpToolBar();

        findViewsById();

        utilityClass = new UtilityClass(JointWorking.this);
        locationProvider = new GPSLocation(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        receiver.InitNetworkListener(this);

        if (temppref.getBoolean(getString(R.string.isonjw_key), false)) {
            llProceed.setVisibility(View.GONE);
            showMessageDialog();
        }

        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(JointWorking.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                if (utilityClass.isInternetConnected())
                    getEmpList();
                else
                    Toast.makeText(JointWorking.this, "You are not connected to internet", Toast.LENGTH_SHORT).show();
            }

        });


        if (temppref.getBoolean(getString(R.string.isonjw_key), false)) {

            tvProceed.setText("End Joint Working");
            llEmpList.setClickable(false);
            llReasonList.setClickable(false);
            llEmpList.setEnabled(false);
            llReasonList.setEnabled(false);
        }

        setViewListeners();
    }

    private void setViewListeners() {

        addOtherView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int sizeInDp = 10;

                float scale = getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (sizeInDp * scale + 0.5f);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                params.setMargins(20, 20, 20, 20);
                EditText editOther = new EditText(JointWorking.this);
                editOther.setPadding(dpAsPixels, dpAsPixels, dpAsPixels, dpAsPixels);
                editOther.setHint("joint working with other person");
                editOther.setBackgroundResource(R.drawable.custom_rectangle);
                editOther.setLayoutParams(params);


                llAddEditText.addView(editOther);

            }
        });

        llProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(JointWorking.this, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (utilityClass.isInternetConnected()) {


                            if (EmloyeeListAdapter.eidList.size() > 0) {

                                ArrayList<String> reason = new ArrayList<>();

                                if (chbCoverageDown.isChecked())
                                    reason.add(chbCoverageDown.getText().toString());
                                if (chbAnyOther.isChecked()) {
                                    reason.add(chbAnyOther.getText().toString());
                                    anyOther = true;
                                }
                                if (chbNewConsumerOffer.isChecked())
                                    reason.add(chbNewConsumerOffer.getText().toString());
                                if (chbNewMarketLaunch.isChecked())
                                    reason.add(chbNewConsumerOffer.getText().toString());
                                if (chbPlacementDown.isChecked())
                                    reason.add(chbPlacementDown.getText().toString());
                                if (chbQPSSchemeFocus.isChecked())
                                    reason.add(chbQPSSchemeFocus.getText().toString());
                                if (chbSpecialSkuFocus.isChecked())
                                    reason.add(chbSpecialSkuFocus.getText().toString());


                                String comment = edtComment.getText().toString();

                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String time = dateFormat.format(Calendar.getInstance().getTime());

                                if (reason.size() > 0) {
                                    if (anyOther && comment.isEmpty())
                                        Toast.makeText(JointWorking.this, "Please specify the reason.", Toast.LENGTH_SHORT).show();
                                    else {
                                        if (!temppref.getBoolean(getString(R.string.isonjw_key), false)) {
                                            jointWorkingRequest(reason, time, comment);
                                        }
                                    }

                                } else {

                                    Toast.makeText(JointWorking.this, "No reason selected", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(JointWorking.this, "Please select employee", Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(JointWorking.this, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                        }
                    }

                });


            }
        });

    }

    private void findViewsById() {

        addOtherView = findViewById(R.id.addOtherView);
        llAddEditText = findViewById(R.id.llAddEditText);
        llProceed = findViewById(R.id.llProceed);
        llEmpList = findViewById(R.id.llEmpList);
        llReasonList = findViewById(R.id.llReasonList);

        chbCoverageDown = findViewById(R.id.chbCoverageDown);
        chbAnyOther = findViewById(R.id.chbAnyOther);
        chbNewMarketLaunch = findViewById(R.id.chbNewMarketLaunch);
        chbPlacementDown = findViewById(R.id.chbPlacementDown);
        chbSpecialSkuFocus = findViewById(R.id.chbSpecialSkuFocus);
        chbQPSSchemeFocus = findViewById(R.id.chbQPSSchemeFocus);
        chbNewConsumerOffer = findViewById(R.id.chbNewConsumerOffer);

        tvEndTime = findViewById(R.id.tvEndTime);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvProceed = findViewById(R.id.tvProceed);

        edtComment = findViewById(R.id.edtCommentJ);

        empListRecyclerView = findViewById(R.id.empListRecyclerView);

    }

    private void setUpToolBar() {

        Toolbar mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        setSupportActionBar(mToolbar);

        tvPageTitle.setText("Joint Working");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                JointWorking.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    private void showMessageDialog() {

        final Dialog mDialog = new Dialog(JointWorking.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_message);
        mDialog.setCancelable(false);
        TextView tvActivity = mDialog.findViewById(R.id.tvActivity);
        TextView tvRemarksM = mDialog.findViewById(R.id.tvRemarksM);
        LinearLayout dRoot = mDialog.findViewById(R.id.dRoot);
        RelativeLayout rlMessage = mDialog.findViewById(R.id.rlMessage);
        LinearLayout llDataList = mDialog.findViewById(R.id.llDataList);
        Button btnOk = mDialog.findViewById(R.id.btnOkJW);
        CardView cvJointW = mDialog.findViewById(R.id.cvJointW);
        TextView tvEmployee = mDialog.findViewById(R.id.tvEmployee);
        TextView tvJwReason = mDialog.findViewById(R.id.tvJWReason);
        TextView tvJWRemarks = mDialog.findViewById(R.id.tvJWRemarks);

        rlMessage.setVisibility(View.GONE);
        llDataList.setVisibility(View.GONE);
        cvJointW.setVisibility(View.VISIBLE);

        tvEmployee.setText(" 1." + temppref.getString(getString(R.string.jw_with_emp_key), ""));

        tvJwReason.setText(" " + temppref.getString(getString(R.string.jw_reason_key), ""));

        tvJWRemarks.setText(" " + temppref.getString(getString(R.string.jw_reason_key), ""));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDialog.dismiss();
                JointWorking.this.finish();
            }
        });

        mDialog.show();

    }

    private void getEmpList() {

        final Dialog loader = new Dialog(JointWorking.this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_LIST + myPref.getString(getString(R.string.zone_id_key), "") + "/employees",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                getEmpFailure = false;

                Log.e(TAG, "onResponse JOINT WORKING EMP LIST===" + response);
                loader.dismiss();
                try {

                    JSONObject data = response.getJSONObject("data");
                    JSONArray emp = data.getJSONArray("employees");

                    for (int index = 0; index < emp.length(); index++) {
                        JSONObject list = (JSONObject) emp.get(index);

                        String eid = list.getString("eid");
                        if (!myPref.getString(getString(R.string.emp_id_key), "").equalsIgnoreCase(eid)) {

                            empId.add(eid);
                            empName.add(list.getString("name"));
                            empPhone.add(list.getString("phone1"));
                        }

                    }

                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        EmloyeeListAdapter emloyeeListAdapter = new EmloyeeListAdapter(JointWorking.this, empId, empName, empPhone);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(JointWorking.this);
                        empListRecyclerView.setLayoutManager(layoutManager);
                        //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                        empListRecyclerView.setAdapter(emloyeeListAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO get emp error
                if (!utilityClass.isInternetConnected()) {
                    getEmpFailure = true;
                } else {
                    getEmpFailure = false;
                }

                loader.dismiss();
                try {
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e(TAG, "Error===" + message + "===" + errorr);

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

        Volley.newRequestQueue(JointWorking.this).add(jsonObjectRequest);

    }

    public void onBackPressed() {
        super.onBackPressed();
        //locationProvider.unregisterReceiver();
    }

    private void jointWorkingRequest(final ArrayList<String> reason, final String startTime, final String comment) {

        final ProgressDialog progressDialog = new ProgressDialog(JointWorking.this);
        progressDialog.setMessage("Sending request...");
        progressDialog.show();

        JSONArray eidArr = new JSONArray();
        for (int i = 0; i < EmloyeeListAdapter.eidList.size(); i++) {
            eidArr.put(EmloyeeListAdapter.eidList.get(i));
        }

        JSONObject jw = new JSONObject();
        try {

            jw.put("eids", eidArr);
            jw.put("reasons", reason.toString());
            jw.put("start_time", startTime);
            jw.put("comment", comment);
            jw.put("latitude", locationProvider.getLatitudeStr());
            jw.put("longitude", locationProvider.getLongitudeStr());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "JOINT WORKING DATA==>" + jw.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_JOINT_WORKING_REQUEST, jw, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                jointWorkingFailure = false;

                Log.e("onResponse", "JOINT WORKING REQ===" + response);
                progressDialog.dismiss();
                try {

                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        SharedPreferences.Editor editor = temppref.edit();
                        editor.putString(getString(R.string.jw_with_emp_key), EmloyeeListAdapter.empList.toString());
                        editor.putString(getString(R.string.jw_reason_key), reason.toString());
                        editor.apply();


                        Bundle params = new Bundle();
                        params.putString("Action", "Joint Working Request Sent");
                        params.putString("UserId", "" + myPref.getString(getString(R.string.emp_id_key), ""));
                        firebaseAnalytics.logEvent("JointWorking", params);

                        showDialog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                // TODO joint working error
                if (!utilityClass.isInternetConnected()) {
                    jointWorkingFailure = true;

                    jointWorkingReason = reason;
                    jointWorkingStart = startTime;
                    jointWorkingComment = comment;

                } else {
                    jointWorkingFailure = false;
                }

                try {
                    progressDialog.dismiss();
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e(TAG, "ERROR===" + message + "===" + errorr);


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
                headers.put("authorization", "Bearer" + " " + myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(JointWorking.this).add(jsonObjectRequest);
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(JointWorking.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.joint_working_dialog);
        dialog.setCancelable(false);

        LinearLayout llOk = dialog.findViewById(R.id.llOk);

        llOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                JointWorking.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        dialog.show();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                JointWorking.this.finishAffinity();
            }
        }
    }

    @Override
    public void connectionChange(boolean status) {

        if (status) {

            if (getEmpFailure)
                getEmpList();

            if (jointWorkingFailure) {
                jointWorkingRequest(jointWorkingReason, jointWorkingStart, jointWorkingComment);
            }

        }

    }
}
