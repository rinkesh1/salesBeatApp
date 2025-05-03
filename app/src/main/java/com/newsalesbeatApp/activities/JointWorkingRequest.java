package com.newsalesbeatApp.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Dhirendra Thakur on 08-01-2018.
 */

public class JointWorkingRequest extends AppCompatActivity {

    private static FirebaseAnalytics firebaseAnalytics;
    TextView tvEmp1/*, tvEmp2, */, tvReason, tvStartDate/*, tvEndDate*/;

    Button btnReject, btnAccept, btnSubmit;

    LinearLayout llDeclineReason, llBtn, llSubmit;

    EditText edtDeclineReason;

    ImageView imgBack, imgCallJW;

    String id = "";

    String empId1, empId2, jsonData, start_time, reason, phn,
            emp1;

    UtilityClass utilityClass;
    private SharedPreferences myPref, temppref;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.joint_working_request);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        temppref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        tvEmp1 = findViewById(R.id.tvEmp1);
        //tvEmp2 = findViewById(R.id.tvEmp2);
        tvReason = findViewById(R.id.tvReason);
        tvStartDate = findViewById(R.id.tvStartDate);
        //tvEndDate = findViewById(R.id.tvEndDate);
        imgCallJW = findViewById(R.id.imgCallJW);

        btnReject = findViewById(R.id.btnReject);
        btnAccept = findViewById(R.id.btnAccept);
        btnSubmit = findViewById(R.id.btnSubmit);

        llBtn = findViewById(R.id.llBtn);
        llDeclineReason = findViewById(R.id.llDeclineReason);
        llSubmit = findViewById(R.id.llSubmit);

        //set up custom toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar3);
        imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        tvPageTitle.setText("Joint Working Request");
        setSupportActionBar(mToolbar);

        utilityClass = new UtilityClass(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        jsonData = getIntent().getStringExtra("data");

        Log.e("Datatata", "==>" + jsonData);

        JSONObject json = null;

        try {

            json = new JSONObject(jsonData);
            JSONObject data = json.getJSONObject("data");
            if (!data.isNull("data") && data.has("data")) {

                JSONObject datta = data.getJSONObject("data");
                start_time = datta.getString("start_time");
                reason = datta.getString("reason");
                //end_time = datta.getString("end_time");
                id = datta.getString("id");
                empId2 = datta.getString("eid2");
                empId1 = datta.getString("eid1");
//                emp2 = data.getString("emp2");
//                emp1 = data.getString("emp1");

            } else {

                start_time = data.getString("start_time");
                reason = data.getString("reason");
                //end_time = data.getString("end_time");
                id = data.getString("id");
                empId2 = data.getString("eid2");
                empId1 = data.getString("eid1");
//                emp2 = json.getString("emp2");
//                emp1 = json.getString("emp1");

            }

            if (!data.isNull("from") && data.has("from"))
                emp1 = data.getString("from");
            if (!data.isNull("contact") && data.has("contact"))
                phn = data.getString("contact");


            if (!json.isNull("from") && json.has("from"))
                emp1 = json.getString("from");
            if (!json.isNull("contact") && json.has("contact"))
                phn = json.getString("contact");

            String date = utilityClass.getDateFormat(start_time);

            tvStartDate.setText(date);
            tvReason.setText(reason);
            //tvEndDate.setText(end_time);
            tvEmp1.setText(emp1);
            // tvEmp2.setText(emp2);

        } catch (Exception e) {
            e.printStackTrace();
        }


//        id = getIntent().getStringExtra("id");
//        empId1 = getIntent().getStringExtra("emp_id1");
//        empId2 = getIntent().getStringExtra("emp_id2");
//
//        tvEmp1.setText(getIntent().getStringExtra("emp1"));
//        tvEmp2.setText(getIntent().getStringExtra("emp2"));
//        tvReason.setText(getIntent().getStringExtra("reason"));
//        tvStartDate.setText(getIntent().getStringExtra("start_time"));
//        tvEndDate.setText(getIntent().getStringExtra("end_time"));

//        Log.e("Datatata","==>"+empId1+","+empId2+","+id+","+getIntent().getStringExtra("emp1")
//                +","+getIntent().getStringExtra("emp2"));

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showJointWorkingRejectionDialog();

            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //String reason = tvReason.getText().toString();
                //String startdate = tvStartDate.getText().toString();
                //String enddate = tvEndDate.getText().toString();
                jointWorkingUpdate(null, ""/*, empId1, empId2, reason, startdate*//*, enddate*/);
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JointWorkingRequest.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imgCallJW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phn));
                if (ActivityCompat.checkSelfPermission(JointWorkingRequest.this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {

                    startActivity(intent);
                }
            }
        });

    }

    private void showJointWorkingRejectionDialog() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        final Dialog dialog = new Dialog(JointWorkingRequest.this, R.style.DialogTheme);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null)
            lp.copyFrom(dialog.getWindow().getAttributes());
        lp.dimAmount = 0.75f;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(R.layout.joint_working_rejection_dialog);

        edtDeclineReason = dialog.findViewById(R.id.edtDeclineReason);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String reasonD = edtDeclineReason.getText().toString();
                //String reason = tvReason.getText().toString();
                //String startdate = tvStartDate.getText().toString();
                //String enddate = tvEndDate.getText().toString();

                if (!reasonD.isEmpty())
                    jointWorkingUpdate(dialog, reasonD/*, empId1, empId2, reason, startdate, enddate*/);
                else
                    Toast.makeText(JointWorkingRequest.this, "Please provide reason", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();

    }

    public void onBackPressed() {
        Intent intent = new Intent(JointWorkingRequest.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void jointWorkingUpdate(final Dialog d, final String reasonD
                                   /* final String eid1, final String eid2, final String reason,
                                    final String startdate, final String enddate*/) {

        final ProgressDialog progressDialog = new ProgressDialog(JointWorkingRequest.this);
        progressDialog.setMessage("Please wait sending request");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_JOINT_WORKING_UPDATE,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "JOINT WORKING UPDATE===" + response);
                //{"data":true,"status":"success","statusMessage":"Success"}

                try {

                    progressDialog.dismiss();
                    //boolean data = response.getBoolean("data");
                    String status = response.getString("status");

                    if (/*data && */status.equalsIgnoreCase("success")) {

                        if (d != null)
                            d.dismiss();


                        Bundle params = new Bundle();
                        params.putString("Action", "Joint Working Request Received");
                        params.putString("UserId", "" + myPref.getString(getString(R.string.emp_id_key), ""));
                        firebaseAnalytics.logEvent("JointWorkingReq", params);

                        new MyTask(reasonD).execute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                try {
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("ERR", "===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("id", id);
//                params2.put("eid1", eid1);
//                params2.put("eid2", eid2);
//                params2.put("reason", reason);

                String status = "";
                if (!reasonD.isEmpty()) {
                    params2.put("decline_reason", reasonD);
                    status = "rejected";
                } else {
                    status = "accepted";
                }

//                params2.put("start_time", startdate);
//                params2.put("end_time", enddate);
                params2.put("status", status);

                Log.e("JSON", "===" + new JSONObject(params2).toString());

                return new JSONObject(params2).toString().getBytes();
            }

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

        Volley.newRequestQueue(JointWorkingRequest.this).add(jsonObjectRequest);

    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        String reason = "";

        private MyTask(String reasonD) {
            this.reason = reasonD;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                if (reason.isEmpty()) {

                    SharedPreferences.Editor editor = temppref.edit();
                    editor.putString(getString(R.string.jw_with_emp_key), emp1);
                    editor.putString(getString(R.string.jw_reason_key), reason);
                    editor.apply();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent intent = new Intent(JointWorkingRequest.this, MainActivity.class);
            if (reason.isEmpty()) {

                intent.putExtra("joint_working", true);
                intent.setAction("requestAccepted");

            }

            startActivity(intent);
            finish();
        }
    }
}
