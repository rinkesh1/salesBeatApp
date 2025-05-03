package com.newsalesbeatApp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class SalesAddActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAGG = "SalesAddActivity";
    ArrayList<String> stringArrayListMonths = new ArrayList<>();
    ArrayList<String> stringArrayListMonthsNumber = new ArrayList<>();
    ArrayList<String> stringArrayListDistributor = new ArrayList<>();
    JSONArray jsonArrayDistributor = new JSONArray();
    String strSelecteddid = "", strMonthNumber = "", strYearNumber = "";
    private Spinner slctMonthSpinner;
    private TextView txtNoInternet;
    private LinearLayout linearNoInternet;
    private Spinner slctDistributorSpinner;
    private EditText edtAddtarget;
    private Button btnSubmitTarget;
    private SwipeRefreshLayout swipeUp;
    private SharedPreferences prefSFA;
    private TextView txtVisit;
    private EditText edtSecAddtarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_add);
        initView();
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                txtNoInternet.setVisibility(View.VISIBLE);
                linearNoInternet.setVisibility(View.GONE);
            } else {
                MonthAddData();
                txtNoInternet.setVisibility(View.GONE);
                linearNoInternet.setVisibility(View.VISIBLE);
            }

        });

        swipeUp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeUp.setRefreshing(false);
                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        txtNoInternet.setVisibility(View.VISIBLE);
                        linearNoInternet.setVisibility(View.GONE);
                    } else {
                        MonthAddData();
                        txtNoInternet.setVisibility(View.GONE);
                        linearNoInternet.setVisibility(View.VISIBLE);
                    }

                });
            }
        });

        tvPageTitle.setText("Add Target Sales");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalesAddActivity.this.finish();
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM");
        long date = System.currentTimeMillis();

        String dateString = dateFormat.format(date);
        Log.d("monthandyear", dateString);
        //getTargetDistributorData(dateString);
    }

    /*Method for SELECT MONTH SPINNER data here data is populated offline manually into spinner*/
    private void MonthAddData() {

        stringArrayListMonths.add("January");
        stringArrayListMonths.add("February");
        stringArrayListMonths.add("March");
        stringArrayListMonths.add("April");
        stringArrayListMonths.add("May");
        stringArrayListMonths.add("June");
        stringArrayListMonths.add("July");
        stringArrayListMonths.add("August");
        stringArrayListMonths.add("September");
        stringArrayListMonths.add("October");
        stringArrayListMonths.add("November");
        stringArrayListMonths.add("December");

        stringArrayListMonthsNumber.add("01");
        stringArrayListMonthsNumber.add("02");
        stringArrayListMonthsNumber.add("03");
        stringArrayListMonthsNumber.add("04");
        stringArrayListMonthsNumber.add("05");
        stringArrayListMonthsNumber.add("06");
        stringArrayListMonthsNumber.add("07");
        stringArrayListMonthsNumber.add("08");
        stringArrayListMonthsNumber.add("09");
        stringArrayListMonthsNumber.add("10");
        stringArrayListMonthsNumber.add("11");
        stringArrayListMonthsNumber.add("12");

        ArrayList<String> stringArrayList = new ArrayList<>();


        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        for (int i = month; i < stringArrayListMonths.size(); i++) {
            stringArrayList.add(stringArrayListMonths.get(i) + " " + year + "");
        }
        int nextYears = cal.get(Calendar.YEAR) + 1;
        for (int i = 0; i < month + 1; i++) {
            stringArrayList.add(stringArrayListMonths.get(i) + " " + nextYears + "");
        }

        Log.d(TAGG, stringArrayList + "");


        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        String month_name = month_date.format(cal.getTime());


        final ArrayAdapter<String> adapters = new ArrayAdapter<String>(this, R.layout.spinner_list_items, stringArrayList);
        slctMonthSpinner.setAdapter(adapters);

        // slctMonthSpinner.setSelection(postion);

        slctMonthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

                String strSelectedMonth = "";
                strSelectedMonth = arg0.getItemAtPosition(position).toString();
                adapters.notifyDataSetChanged();

                StringTokenizer tokens = new StringTokenizer(strSelectedMonth, " ");
                String first = tokens.nextToken();// this will contain "Fruit"
                String second = tokens.nextToken();

                for (int i = 0; i < stringArrayListMonthsNumber.size(); i++) {
                    if (first.equalsIgnoreCase(stringArrayListMonths.get(i))) {
                        strMonthNumber = stringArrayListMonthsNumber.get(i);
                        break;
                    }
                }

                Log.d(TAGG, first + second + strMonthNumber);
                strYearNumber = second;
                getTargetDistributorData(strYearNumber + "/" + strMonthNumber);


            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    private void initView() {
        slctMonthSpinner = findViewById(R.id.slctMonthSpinner);
        slctDistributorSpinner = findViewById(R.id.slctDistributorSpinner);
        edtAddtarget = findViewById(R.id.edtAddtarget);
        btnSubmitTarget = findViewById(R.id.btnSubmitTarget);
        btnSubmitTarget.setOnClickListener(this);
        txtVisit = findViewById(R.id.txtVisit);
        linearNoInternet = findViewById(R.id.linearNoInternet);
        txtNoInternet = findViewById(R.id.txtNoInternet);
        swipeUp = findViewById(R.id.swipeUp);
        edtSecAddtarget = findViewById(R.id.edtSecAddtarget);
    }

    /*This is the distributor spinner data method*/
    private void DistributorListData(JSONArray jsonArrayDistributor,
                                     ArrayList<String> stringArrayListDistributor) {

        final ArrayAdapter<String> adapters = new ArrayAdapter<String>(this, R.layout.spinner_list_items, stringArrayListDistributor);
        slctDistributorSpinner.setAdapter(adapters);

        slctDistributorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {

                adapters.notifyDataSetChanged();
                try {

                    JSONObject jsonObject = jsonArrayDistributor.getJSONObject(position);
                    txtVisit.setText(jsonObject.getString("visit") + "");
                    strSelecteddid = jsonObject.getString("did") + "";
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSubmitTarget) {
            if (edtAddtarget.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(SalesAddActivity.this, "Please enter primary target", Toast.LENGTH_SHORT).show();
            } else if (edtSecAddtarget.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(SalesAddActivity.this, "Please enter secondary target", Toast.LENGTH_SHORT).show();
            } else {
                SaveTargetDistributorData(
                        strYearNumber,
                        strMonthNumber,
                        edtAddtarget.getText().toString().trim(),
                        edtSecAddtarget.getText().toString().trim(),
                        strSelecteddid
                );
            }
        }


//        switch (v.getId()) {
//            default:
//                break;
//            case R.id.btnSubmitTarget:
//
//                if (edtAddtarget.getText().toString().equalsIgnoreCase("")) {
//                    Toast.makeText(SalesAddActivity.this, "Please enter primary target", Toast.LENGTH_SHORT).show();
//                } else if (edtSecAddtarget.getText().toString().equalsIgnoreCase("")) {
//                    Toast.makeText(SalesAddActivity.this, "Please enter secondary target", Toast.LENGTH_SHORT).show();
//                } else {
//                    SaveTargetDistributorData(strYearNumber, strMonthNumber, edtAddtarget.getText().toString().trim(),
//                            edtSecAddtarget.getText().toString().trim(), strSelecteddid);
//                }
//
//                break;
//        }
    }

    /*Save data APi called via this method*/
    private void SaveTargetDistributorData(String year, String month, String pri_target, String sec_target, String did) {

        final Dialog loader = new Dialog(SalesAddActivity.this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        StringRequest postRequest = new StringRequest(Request.Method.POST, SbAppConstants.API_POST_SAVETARGETDISTRIBUTORS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // response
                        Log.e("Response", "Save Data Api" + response);

                        try {
                            loader.dismiss();
                            JSONObject object = new JSONObject(response);
                            String status = object.getString("status");
                            String msg = object.getString("statusMessage");

                            if (status.equalsIgnoreCase("success")) {
                                SalesAddActivity.this.finish();
                                Toast.makeText(SalesAddActivity.this, "Successfully save your" + month + "/" + year + " " + "target", Toast.LENGTH_SHORT).show();
                            }
                            if (status.equalsIgnoreCase("error")) {
                                Toast.makeText(SalesAddActivity.this, "Kindly update your app or contact admin!", Toast.LENGTH_SHORT).show();
                                //showNotificationDialog("API deprecated.", "Kindly update your app or contact admin!");

                            } else {
                                Toast.makeText(SalesAddActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            loader.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                loader.dismiss();
                try {

                    if (error.networkResponse.statusCode == 410) {
                        Toast.makeText(SalesAddActivity.this, "API deprecated please update your app.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SalesAddActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    //  serverCall.handleError(error, TAG, "markEmpAttendance");

                } catch (Exception e) {
                    Toast.makeText(SalesAddActivity.this, "null error code", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("year", year);
                params.put("month", month);
                params.put("pri_target", pri_target);
                params.put("longitude", sec_target);
                params.put("did", did);

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

        Volley.newRequestQueue(SalesAddActivity.this).add(postRequest);

    }

    /*Get Distributor list api is called with parameter date pls find api url
     * https://myapi.salesbeat.in/api/v4/distributorListforTarget/2019/03
     * header -> Autorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjE3OSwiaXNzIjoiaHR0cHM6Ly9teWFwaS5zYWxlc
     * 2JlYXQuaW4vYXBpL3Y0L2VtcGxveWVlTG9naW4iLCJpYXQiOjE1NTE3MTkzMjIsImV4cCI6Mjc2MTMxOTMyMiwibmJmIjoxNTUxNzE5MzIyLCJqdGkiOiI1S2tCNHpaa
     * ldoTEJUWkk4In0.qMV8IqyNjtW_eNReGg037XiFJhdTAtBktUQ2T7X3uOY*/
    private void getTargetDistributorData(final String date) {

        final Dialog loader = new Dialog(SalesAddActivity.this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();
        Log.d("Distributor response", SbAppConstants.API_GET_TARGETDISTRIBUTORS + date);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, SbAppConstants.API_GET_TARGETDISTRIBUTORS + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", "Distributor list-01" + response);
                loader.dismiss();
                try {

                    //Distributor List
                    JSONArray orders = response.getJSONArray("distributors");
                    for (int index = 0; index < orders.length(); index++) {
                        JSONObject orderObj = (JSONObject) orders.get(index);

                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("visit", orderObj.getString("visit"));
                        jsonObject1.put("did", orderObj.getString("did"));
                        jsonArrayDistributor.put(jsonObject1);

                        JSONObject jsonObject = orderObj.getJSONObject("distributor");
                        String strDistributorName = "";
                        strDistributorName = jsonObject.getString("name");
                        stringArrayListDistributor.add(strDistributorName);

                    }

                    String status = response.getString("status");
                    if (status.equalsIgnoreCase("success")) {
                        DistributorListData(jsonArrayDistributor, stringArrayListDistributor);
                    }

                } catch (Exception e) {
                    loader.dismiss();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    loader.dismiss();
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("ERRR", "===== " + responseBody);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.getMessage();
                }


            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                return headers;
            }
        };

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(SalesAddActivity.this).add(objectRequest);

    }

}
