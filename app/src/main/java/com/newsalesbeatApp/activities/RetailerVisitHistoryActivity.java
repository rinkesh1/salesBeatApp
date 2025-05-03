package com.newsalesbeatApp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.RetailerVisitHistoryAdapter;
import com.newsalesbeatApp.pojo.RetailerVisitItem;
import com.newsalesbeatApp.pojo.SkuItem;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.TransactionOptions;

/*
 * Created by Dhirendra Thakur on 26-12-2017.
 */

public class RetailerVisitHistoryActivity extends AppCompatActivity {

    RecyclerView rvRetailerVisit;
    MaterialCalendarView calendarDisRetHistoryMonth;
    UtilityClass utilityClass;
    int cMonth = 0;
    int cYear = 0;
    TextView tvNoData;
    SwipeRefreshLayout retailerRefresh;
    String startDate, endDate;
    private String TAG = "RetailerVisitHistoryActivity";
    private SharedPreferences prefSFA;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.retailer_visit_history);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        rvRetailerVisit = findViewById(R.id.rvVisitHistory);
        tvNoData = findViewById(R.id.tvNoData);
        retailerRefresh = findViewById(R.id.retailerRefresh);
        calendarDisRetHistoryMonth = findViewById(R.id.calendarDisRetHistoryMonth);

        Toolbar mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        //ImageView imgSettings = (ImageView) mToolbar.findViewById(R.id.imgSettings);
        final TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);


        setSupportActionBar(mToolbar);
        utilityClass = new UtilityClass(this);

        //current date string
        Calendar cc = java.util.Calendar.getInstance();
        cMonth = cc.get(Calendar.MONTH);
        cYear = cc.get(Calendar.YEAR);
        final SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdff.format(cc.getTime());

        Pair pair = utilityClass.getDateRange(0, 0);
        startDate = (String) pair.first;


        selectCall(tvPageTitle, startDate, endDate);

        calendarDisRetHistoryMonth.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                int val = date.getMonth() - cMonth;
                int year = date.getYear() - cYear;
                Pair pair = utilityClass.getDateRange(val, year);
                startDate = (String) pair.first;
                endDate = (String) pair.second;
                Log.e(TAG, " DATE<--->" + startDate + "<--->" + endDate);

                selectCall(tvPageTitle, startDate, endDate);

            }
        });


        Calendar max = Calendar.getInstance();
        max.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));

        calendarDisRetHistoryMonth.state().edit()
                .setMaximumDate(max)
                .commit();


        retailerRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                selectCall(tvPageTitle, startDate, endDate);

            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetailerVisitHistoryActivity.this.finish();
            }
        });
    }

    private void selectCall(TextView tvPageTitle, String startDate, String endDate) {

        try {

            String from = getIntent().getStringExtra("from");

            if (from.equalsIgnoreCase("retailer")) {

                tvPageTitle.setText("Retailer Order History");
                String rid = getIntent().getStringExtra("rid");
                String name = getIntent().getStringExtra("name");
                SharedPreferences prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                Sentry.configureScope(scope -> {
                    scope.setTag("page_locale", "en_US");
                    scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//                    scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
                });

                TransactionOptions txOptions = new TransactionOptions();
                txOptions.setBindToScope(true);
                ITransaction transaction = Sentry.startTransaction("RetailerVisitHistoryActivity", "showVisitHistory",txOptions);
                try {
                    if (transaction == null) {
                        transaction = Sentry.startTransaction("processOrderBatch()", "task");
                    }

                    showVisitHistory(rid, name, startDate, endDate, from);

                } catch (Exception e) {
                    transaction.setThrowable(e);
                    transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                    throw e;
                } finally {
                    transaction.finish();
                }



            } else if (from.equalsIgnoreCase("distributor")) {

                tvPageTitle.setText("Distributor Order History");
                String did = getIntent().getStringExtra("did");
                String name = getIntent().getStringExtra("name");
                SharedPreferences prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                Sentry.configureScope(scope -> {
                    scope.setTag("page_locale", "en_US");
                    scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//                    scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
                });

                TransactionOptions txOptions = new TransactionOptions();
                txOptions.setBindToScope(true);
                ITransaction transaction = Sentry.startTransaction("RetailerVisitHistoryActivity", "showDisVisitHistory",txOptions);
                try {
                    if (transaction == null) {
                        transaction = Sentry.startTransaction("processOrderBatch()", "task");
                    }

                    showDisVisitHistory(did, name, startDate, endDate, from);

                } catch (Exception e) {
                    transaction.setThrowable(e);
                    transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                    throw e;
                } finally {
                    transaction.finish();
                }


            } else if (from.equalsIgnoreCase("distributor_s")) {

                tvPageTitle.setText("Stock Capture History");
                String did = getIntent().getStringExtra("did");
                String name = getIntent().getStringExtra("name");
                SharedPreferences prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                Sentry.configureScope(scope -> {
                    scope.setTag("page_locale", "en_US");
                    scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//                    scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
                });

                TransactionOptions txOptions = new TransactionOptions();
                txOptions.setBindToScope(true);
                ITransaction transaction = Sentry.startTransaction("RetailerVisitHistoryActivity", "showCapturedHistory",txOptions);
                try {
                    if (transaction == null) {
                        transaction = Sentry.startTransaction("processOrderBatch()", "task");
                    }

                    showCapturedHistory(did, name, startDate, endDate, from);

                } catch (Exception e) {
                    transaction.setThrowable(e);
                    transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                    throw e;
                } finally {
                    transaction.finish();
                }


            } else if (from.equalsIgnoreCase("dis_closing")) {

                tvPageTitle.setText("Distributor Closing History");
                String did = getIntent().getStringExtra("did");
                String name = getIntent().getStringExtra("name");
                SharedPreferences prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                Sentry.configureScope(scope -> {
                    scope.setTag("page_locale", "en_US");
                    scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//                    scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
                });

                TransactionOptions txOptions = new TransactionOptions();
                txOptions.setBindToScope(true);
                ITransaction transaction = Sentry.startTransaction("RetailerVisitHistoryActivity", "showClosingHistory",txOptions);
                try {
                    if (transaction == null) {
                        transaction = Sentry.startTransaction("processOrderBatch()", "task");
                    }

                    showClosingHistory(did, name, startDate, endDate, from);

                } catch (Exception e) {
                    transaction.setThrowable(e);
                    transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                    throw e;
                } finally {
                    transaction.finish();
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showClosingHistory(String did, String name, String startDate, String endDate, String from) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DIS_CLOSING_HISTORY + "/" + did + "?fromDate=" + startDate + "&toDate=" + endDate,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, " onResponse Dis Closing history===" + response);
                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {

                    ArrayList<RetailerVisitItem> retailerVisitList = new ArrayList<>();
                    retailerVisitList.clear();

                    JSONObject data = response.getJSONObject("data");
                    JSONArray stocks = data.getJSONArray("stocks");

                    for (int i = 0; i < stocks.length(); i++) {

                        RetailerVisitItem visitItem = new RetailerVisitItem();
                        JSONObject object = (JSONObject) stocks.get(i);
                        visitItem.setCheckIn(object.getString("takenAt"));
                        visitItem.setCheckOut(object.getString("takenAt"));
                        visitItem.setComment("----------");
                        JSONObject emp = object.getJSONObject("employee");
                        visitItem.setEmpName(emp.getString("name"));
                        visitItem.setEmpPhone(emp.getString("phone1"));
                        visitItem.setEmpEmail(emp.getString("email1"));
                        visitItem.setEmpZone(emp.getString("zoneid"));

                        if (!object.isNull("catalogue") && object.has("catalogue")) {

                            JSONArray catalogArr = object.optJSONArray("catalogue");
                            ArrayList<SkuItem> skuItemList = new ArrayList<>();

                            for (int r = 0; r < catalogArr.length(); r++) {

                                SkuItem skuItem = new SkuItem();
                                JSONObject catObj = (JSONObject) catalogArr.get(r);
                                skuItem.setOpening(catObj.getString("qty"));
                                JSONObject sObj = catObj.getJSONObject("sku");
                                skuItem.setSku(sObj.getString("sku"));
                                skuItem.setPrice(sObj.getString("price"));
                                skuItem.setUnit(sObj.getString("unit"));
                                skuItem.setConversionFactor(sObj.getString("conversionFactor"));
                                skuItem.setweight(sObj.getString("weight"));
                                skuItemList.add(skuItem);
                            }

                            visitItem.setOrderList(skuItemList);
                        }

                        retailerVisitList.add(visitItem);

                    }

                    if (retailerVisitList.size() > 0) {

                        RetailerVisitHistoryAdapter adapter =
                                new RetailerVisitHistoryAdapter(RetailerVisitHistoryActivity.this,
                                        retailerVisitList, from, name);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(RetailerVisitHistoryActivity.this);
                        rvRetailerVisit.setLayoutManager(layoutManager);
                        //rvRetailerVisit.addItemDecoration(new SimpleDividerItemDecoration(4));
                        rvRetailerVisit.setAdapter(adapter);
                        rvRetailerVisit.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    } else {

                        rvRetailerVisit.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {
                    Toast.makeText(RetailerVisitHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(RetailerVisitHistoryActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(RetailerVisitHistoryActivity.this).add(jsonObjectRequest);


    }

    private void showCapturedHistory(String did, final String name, final String startDate, final String endDate, final String from) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DIS_OPENINGSTOCK_HISTORY + "?did=" + did + "&fromDate=" + startDate + "&toDate=" + endDate,
                //SbAppConstants.API_GET_DIS_OPENINGSTOCK_HISTORY + "?did=234&fromDate=2018-08-07&toDate=2018-08-07",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, " onResponse Dis Stock history===" + response);
                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {

                    ArrayList<RetailerVisitItem> retailerVisitList = new ArrayList<>();
                    retailerVisitList.clear();
                    //@Umesh 13-March-2022
                    if(response.getInt("status")==1) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray stocks = data.getJSONArray("stocks");

                        for (int i = 0; i < stocks.length(); i++) {

                            RetailerVisitItem visitItem = new RetailerVisitItem();
                            JSONObject object = (JSONObject) stocks.get(i);
                            visitItem.setCheckIn(object.getString("takenAt"));
                            visitItem.setCheckOut(object.getString("takenAt"));
                            visitItem.setComment("----------");
                            JSONObject emp = object.getJSONObject("employees");
                            visitItem.setEmpName(emp.getString("name"));
                            visitItem.setEmpPhone(emp.getString("phone1"));
                            visitItem.setEmpEmail(emp.getString("email1"));
                            visitItem.setEmpZone(emp.getString("zoneid"));

                            if (!object.isNull("openingStockCatalogue") && object.has("openingStockCatalogue"))
                            {
                                JSONArray catalogArr = object.optJSONArray("openingStockCatalogue");
                                ArrayList<SkuItem> skuItemList = new ArrayList<>();

                                for (int r = 0; r < catalogArr.length(); r++) {

                                    SkuItem skuItem = new SkuItem();
                                    JSONObject catObj = (JSONObject) catalogArr.get(r);
                                    skuItem.setOpening(catObj.getString("qty"));
                                    JSONObject sObj = catObj.getJSONObject("skus");
                                    skuItem.setSku(sObj.getString("sku"));
                                    skuItem.setPrice(sObj.getString("price"));
                                    skuItem.setUnit(sObj.getString("unit"));
                                    skuItem.setConversionFactor(sObj.getString("conversionFactor"));
                                    skuItem.setweight(sObj.getString("weight"));

                                    skuItemList.add(skuItem);
                                }

                                visitItem.setOrderList(skuItemList);
                            }

                            retailerVisitList.add(visitItem);

                        }
                    }

                    if (retailerVisitList.size() > 0) {

                        RetailerVisitHistoryAdapter adapter =
                                new RetailerVisitHistoryAdapter(RetailerVisitHistoryActivity.this,
                                        retailerVisitList, from, name);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(RetailerVisitHistoryActivity.this);
                        rvRetailerVisit.setLayoutManager(layoutManager);
                        //rvRetailerVisit.addItemDecoration(new SimpleDividerItemDecoration(4));
                        rvRetailerVisit.setAdapter(adapter);
                        rvRetailerVisit.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    } else {

                        rvRetailerVisit.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {
                    Toast.makeText(RetailerVisitHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(RetailerVisitHistoryActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(RetailerVisitHistoryActivity.this).add(jsonObjectRequest);


    }

    private void showDisVisitHistory(String did, final String name, final String startDate, final String endDate, final String from) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DIS_VISIT_HISTORY + "?did=" + did + "&fromDate=" + startDate + "&toDate=" + endDate,
                //SbAppConstants.API_GET_DIS_VISIT_HISTORY + "?did=277&fromDate=2018-09-01&toDate=2018-09-09",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, " onResponse Dis Order history===" + response);
                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {

                    ArrayList<RetailerVisitItem> retailerVisitList = new ArrayList<>();
                    retailerVisitList.clear();
                    //@Umesh 13-March-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray orders = data.getJSONArray("orders");

                        for (int i = 0; i < orders.length(); i++)
                        {

                            RetailerVisitItem visitItem = new RetailerVisitItem();
                            JSONObject object = (JSONObject) orders.get(i);
                            visitItem.setCheckIn(object.getString("takenAt"));
                            visitItem.setCheckOut(object.getString("takenAt"));
                            visitItem.setComment("----------");
                            JSONObject emp = object.getJSONObject("employees");
                            visitItem.setEmpName(emp.getString("name"));
                            visitItem.setEmpPhone(emp.getString("phone1"));
                            visitItem.setEmpEmail(emp.getString("email1"));
                            visitItem.setEmpZone(emp.getString("zoneid"));

                            if (!object.isNull("distributorOrderCatalogue") && object.has("distributorOrderCatalogue")) {

                                JSONArray catalogArr = object.optJSONArray("distributorOrderCatalogue");
                                ArrayList<SkuItem> skuItemList = new ArrayList<>();
                                for (int r = 0; r < catalogArr.length(); r++) {

                                    SkuItem skuItem = new SkuItem();
                                    JSONObject catObj = (JSONObject) catalogArr.get(r);
                                    skuItem.setOpening(catObj.getString("qty"));
                                    JSONObject sObj = catObj.getJSONObject("skus");
                                    //JSONObject sObj = (JSONObject) sArr.get(0);
                                    skuItem.setSku(sObj.getString("sku"));
                                    skuItem.setPrice(sObj.getString("price"));
                                    skuItem.setUnit(sObj.getString("unit"));
                                    skuItem.setConversionFactor(sObj.getString("conversionFactor"));
                                    skuItem.setweight(sObj.getString("weight"));
                                    skuItemList.add(skuItem);
                                }

                                visitItem.setOrderList(skuItemList);
                            }

                            retailerVisitList.add(visitItem);

                        }
                    }
                    if (retailerVisitList.size() > 0) {

                        RetailerVisitHistoryAdapter adapter =
                                new RetailerVisitHistoryAdapter(RetailerVisitHistoryActivity.this,
                                        retailerVisitList, from, name);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(RetailerVisitHistoryActivity.this);
                        rvRetailerVisit.setLayoutManager(layoutManager);
                        //rvRetailerVisit.addItemDecoration(new SimpleDividerItemDecoration(4));
                        rvRetailerVisit.setAdapter(adapter);
                        rvRetailerVisit.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();

                    } else {
                        rvRetailerVisit.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {
                    Toast.makeText(RetailerVisitHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(RetailerVisitHistoryActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(RetailerVisitHistoryActivity.this).add(jsonObjectRequest);

    }

    public void onBackPressed() {
        RetailerVisitHistoryActivity.this.finish();
    }

    private void showVisitHistory(String rid, final String name, final String startDate, final String endDate, final String from) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        Log.e("RetailerOrderHistory", "==>" + rid + ",," + startDate + ",,," + endDate);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_VISIT_HISTORY + "?rid="+rid + "&fromDate=" + startDate + "&toDate=" + endDate,
                //SbAppConstants.API_GET_VISIT_HISTORY + "?rid=6968&fromDate=2018-03-20&toDate=2018-03-20",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, " onResponse Retailer order history===" + response);

                loader.dismiss();
                retailerRefresh.setRefreshing(false);
                try {

                    ArrayList<RetailerVisitItem> retailerVisitList = new ArrayList<>();
                    retailerVisitList.clear();
                    //@Umesh 10-March-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray visits = data.getJSONArray("visits");

                        for (int i = 0; i < visits.length(); i++)
                        {

                            RetailerVisitItem visitItem = new RetailerVisitItem();
                            JSONObject object = (JSONObject) visits.get(i);
                            visitItem.setCheckIn(object.getString("checkIn"));
                            visitItem.setCheckOut(object.getString("checkOut"));
                            visitItem.setComment(object.getString("comments"));
                            JSONObject emp = object.getJSONObject("employees");
                            visitItem.setEmpName(emp.getString("name"));
                            visitItem.setEmpPhone(emp.getString("phone1"));
                            visitItem.setEmpEmail(emp.getString("email1"));
                            visitItem.setEmpZone(emp.getString("zoneid"));

                            JSONArray ordersArr = object.getJSONArray("orders");

                            if (ordersArr.length() > 0)
                            {
                                JSONObject object1 = (JSONObject) ordersArr.get(0);
                                JSONArray catalogArr = object1.optJSONArray("orderCatalogue");
                                ArrayList<SkuItem> skuItemList = new ArrayList<>();
                                for (int r = 0; r < catalogArr.length(); r++) {

                                    SkuItem skuItem = new SkuItem();
                                    JSONObject catObj = (JSONObject) catalogArr.get(r);
                                    skuItem.setOpening(catObj.getString("qty"));
                                    //JSONArray sArr = catObj.getJSONArray("sku");
                                    JSONObject sObj = catObj.getJSONObject("skus");
                                    //JSONObject sObj = (JSONObject) sArr.get(0);
                                    skuItem.setSku(sObj.getString("sku"));
                                    skuItem.setPrice(sObj.getString("price"));
                                    skuItem.setUnit(sObj.getString("unit"));
                                    skuItem.setConversionFactor(sObj.getString("conversionFactor"));
                                    skuItem.setweight(sObj.getString("weight"));
                                    skuItemList.add(skuItem);
                                }

                                visitItem.setOrderList(skuItemList);
                            }


                            retailerVisitList.add(visitItem);

                        }
                    }

                    if (retailerVisitList.size() > 0) {

                        RetailerVisitHistoryAdapter adapter =
                                new RetailerVisitHistoryAdapter(RetailerVisitHistoryActivity.this,
                                        retailerVisitList, from, name);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(RetailerVisitHistoryActivity.this);
                        rvRetailerVisit.setLayoutManager(layoutManager);
                        //rvRetailerVisit.addItemDecoration(new SimpleDividerItemDecoration(4));
                        rvRetailerVisit.setAdapter(adapter);
                        rvRetailerVisit.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    } else {

                        rvRetailerVisit.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader.dismiss();
                retailerRefresh.setRefreshing(false);

                try {
                    Toast.makeText(RetailerVisitHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(RetailerVisitHistoryActivity.this, "Exception", Toast.LENGTH_SHORT).show();
                }
                error.printStackTrace();

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(RetailerVisitHistoryActivity.this).add(jsonObjectRequest);
    }

}
