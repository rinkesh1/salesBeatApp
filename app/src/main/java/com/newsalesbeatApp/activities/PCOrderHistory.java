package com.newsalesbeatApp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.PCOrderHistoryListAdapter;
import com.newsalesbeatApp.pojo.MyProduct;
import com.newsalesbeatApp.pojo.PCOrderHistoryItem;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Created by Dhirendra Thakur on 12-03-2018.
 */

public class PCOrderHistory extends AppCompatActivity {

    RecyclerView rvOrderHistory;
    PCOrderHistoryListAdapter pcOrderHistoryListAdapter;
    SharedPreferences myPref;
    ArrayList<PCOrderHistoryItem> orderHistoryList = new ArrayList<>();
    ArrayList<PCOrderHistoryItem> orderHistoryList2 = new ArrayList<>();
    ArrayList<PCOrderHistoryItem> finalOrderHistoryList = new ArrayList<>();
    FloatingActionButton btnFilter;
    private List<String> distributorList = new ArrayList<>();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_history);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        rvOrderHistory = findViewById(R.id.rvOrderHistory);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvTitle = findViewById(R.id.pageTitle);
        TextView tvShare = findViewById(R.id.tvShare);
        btnFilter = findViewById(R.id.btnFilter);

        tvTitle.setText("Order History");

        String date = getIntent().getStringExtra("date");
        getOrderHistory(date);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PCOrderHistory.this.finish();
            }
        });

        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initializeFinalOrderList(orderHistoryList);
                sharePCOrderHistory();
            }
        });

        rvOrderHistory.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                initializeFinalOrderList(orderHistoryList);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog();
            }
        });
    }

    private void showFilterDialog() {

        final Dialog dialog = new Dialog(PCOrderHistory.this);
        dialog.setContentView(R.layout.sku_filter_layout);

        final AutoCompleteTextView actvFilterBy = dialog.findViewById(R.id.actvFilterBy);
        final AutoCompleteTextView actvFilterWith = dialog.findViewById(R.id.actvFilterWith);
        ImageView imgDropDownFilter = dialog.findViewById(R.id.imgDropDownFilter);
        ImageView imgDropDownFilterWith = dialog.findViewById(R.id.imgDropDownFilterWith);
        RelativeLayout rlFBy = dialog.findViewById(R.id.rlFBy);
        Button btnApply = dialog.findViewById(R.id.btnApply);

        //to remove duplicate value from list
        Set<String> hs = new LinkedHashSet<>();
        hs.addAll(distributorList);
        distributorList.clear();
        distributorList.addAll(hs);

        ArrayAdapter adapter2 = new ArrayAdapter(PCOrderHistory.this,
                android.R.layout.simple_spinner_dropdown_item, distributorList);

        actvFilterWith.setAdapter(adapter2);

        actvFilterWith.setHint("select distributor");

        rlFBy.setVisibility(View.GONE);

        actvFilterWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFilterWith.showDropDown();
            }
        });

        imgDropDownFilterWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFilterWith.showDropDown();
            }
        });


        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filetrWith = actvFilterWith.getText().toString();
                ArrayList<PCOrderHistoryItem> filteredOrderHistoryList = new ArrayList<>();
                if (!filetrWith.isEmpty()) {

                    for (int i = 0; i < orderHistoryList.size(); i++) {
                        if (orderHistoryList.get(i).getDisName().equalsIgnoreCase(filetrWith)) {
                            filteredOrderHistoryList.add(orderHistoryList.get(i));
                        }
                    }

                    initializeList2(filteredOrderHistoryList);
                }

                dialog.dismiss();
            }
        });


        dialog.show();

    }

    private void initializeList2(ArrayList<PCOrderHistoryItem> filteredOrderHistoryList) {

        for (int temp = 0; temp < filteredOrderHistoryList.size(); temp++) {

            orderHistoryList2.add(temp, null);
        }

        pcOrderHistoryListAdapter = new PCOrderHistoryListAdapter(PCOrderHistory.this, filteredOrderHistoryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PCOrderHistory.this);
        rvOrderHistory.setLayoutManager(layoutManager);
        rvOrderHistory.setAdapter(pcOrderHistoryListAdapter);
        pcOrderHistoryListAdapter.notifyDataSetChanged();
    }

    private void initializeFinalOrderList(ArrayList<PCOrderHistoryItem> orderHistoryList) {

        if (pcOrderHistoryListAdapter != null && pcOrderHistoryListAdapter.getItemCount() > 0) {

            for (int position = 0; position < orderHistoryList.size(); position++) {

                RecyclerView.ViewHolder holder = rvOrderHistory.findViewHolderForAdapterPosition(position);

                if (holder != null) {

                    CheckBox check = (CheckBox) holder.itemView.findViewById(R.id.checkb);
                    Log.e("PCOrderHistory", "Order Check===" + position + "===" + check.isChecked());

                    if (check.isChecked()) {

                        PCOrderHistoryItem pcOrderHistoryItem = new PCOrderHistoryItem();
                        pcOrderHistoryItem.setDid(orderHistoryList.get(position).getDid());
                        pcOrderHistoryItem.setDisName(orderHistoryList.get(position).getDisName());
                        pcOrderHistoryItem.setRid(orderHistoryList.get(position).getRid());
                        pcOrderHistoryItem.setRetName(orderHistoryList.get(position).getRetName());
                        pcOrderHistoryItem.setRetAddress(orderHistoryList.get(position).getRetAddress());
                        pcOrderHistoryItem.setRetPhone(orderHistoryList.get(position).getRetPhone());
                        pcOrderHistoryItem.setTakenDate(orderHistoryList.get(position).getTakenDate());
                        pcOrderHistoryItem.setOrderType(orderHistoryList.get(position).getOrderType());
                        pcOrderHistoryItem.setMyOrderHistoryList(orderHistoryList.get(position).getMyOrderHistoryList());

                        orderHistoryList2.set(position, pcOrderHistoryItem);

                    } else {

                        orderHistoryList2.set(position, null);

                    }
                }
            }
        }

    }

    private void sharePCOrderHistory() {

        for (int pos = 0; pos < orderHistoryList2.size(); pos++) {

            if (orderHistoryList2.get(pos) != null) {

                finalOrderHistoryList.add(orderHistoryList2.get(pos));
            }
        }

        if (finalOrderHistoryList.size() > 0)
            shareText(finalOrderHistoryList);
        else
            Toast.makeText(PCOrderHistory.this, "Please select retailer to share", Toast.LENGTH_SHORT).show();

    }

    private void shareText(ArrayList<PCOrderHistoryItem> orderHistoryList2) {

        new ShareTextTask(orderHistoryList2).execute();

    }

    private void getOrderHistory(final String date) {

        final Dialog loader = new Dialog(PCOrderHistory.this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        //@Umesh 02-Feb-2022
        //String date1="2018-04-10";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_ORDER_HISTORY + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", "EMP_Order_BY_DATE=====" + response);
                loader.dismiss();
                try {
                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        response = response.getJSONObject("data");
                        //existing retailer's order
                        JSONArray orders = response.getJSONArray("orders");
                        for (int index = 0; index < orders.length(); index++) {
                            JSONObject orderObj = (JSONObject) orders.get(index);

                            PCOrderHistoryItem PCOrderHistoryItem = new PCOrderHistoryItem();

                            String orderType = orderObj.getString("orderType");
                            String orderDate = orderObj.getString("takenAt");
                            String rid = orderObj.getString("rid");
                            String did = orderObj.getString("did");

                            JSONArray catalog = orderObj.getJSONArray("catalog");
                            ArrayList<MyProduct> myProductList = new ArrayList<>();

                            for (int i = 0; i < catalog.length(); i++) {

                                MyProduct myProduct = new MyProduct();
                                JSONObject object = (JSONObject) catalog.get(i);
                                String quantity = object.getString("qty");
                                myProduct.setQuantity(quantity);
                                String skuName = "", brandName = "", unit = "";
                                skuName = object.getString("sku");
                                brandName = object.getString("primaryCategory");
                                unit = object.getString("unit");


                                myProduct.setMySkus(skuName);
                                myProduct.setBrand(brandName);
                                myProduct.setUnit(unit);

                                myProductList.add(myProduct);
                            }

                            JSONObject ret = orderObj.getJSONObject("retailer");
                            String retName = ret.getString("name");
                            String retaddress = ret.getString("address");
                            String retPhone = ret.getString("ownersPhone1");

                            JSONObject dis = orderObj.getJSONObject("distributor");
                            String disName = dis.getString("name");

                            PCOrderHistoryItem.setRid(rid);
                            PCOrderHistoryItem.setRetName(retName);
                            PCOrderHistoryItem.setRetAddress(retaddress);
                            PCOrderHistoryItem.setRetPhone(retPhone);
                            PCOrderHistoryItem.setDid(did);
                            PCOrderHistoryItem.setDisName(disName);
                            PCOrderHistoryItem.setOrderType(orderType);
                            PCOrderHistoryItem.setTakenDate(orderDate);
                            PCOrderHistoryItem.setMyOrderHistoryList(myProductList);

                            distributorList.add(disName);
                            orderHistoryList.add(PCOrderHistoryItem);
                        }

                        //newly added retailer's order
                        JSONArray newOrders = response.getJSONArray("neworders");

                        for (int index = 0; index < newOrders.length(); index++) {
                            JSONObject newOrderObj = (JSONObject) newOrders.get(index);

                            PCOrderHistoryItem PCOrderHistoryItem = new PCOrderHistoryItem();

                            String orderType = newOrderObj.getString("orderType");
                            String orderDate = newOrderObj.getString("takenAt");
                            String rid = newOrderObj.getString("nrid");
                            String did = newOrderObj.getString("did");

                            JSONArray catalog = newOrderObj.getJSONArray("catalog");
                            ArrayList<MyProduct> myProductList = new ArrayList<>();

                            for (int i = 0; i < catalog.length(); i++) {

                                MyProduct myProduct = new MyProduct();
                                JSONObject object = (JSONObject) catalog.get(i);
                                String quantity = object.getString("qty");
                                myProduct.setQuantity(quantity);

                                String skuName = "", brandName = "", unit = "";


                                skuName = object.getString("sku");
                                brandName = object.getString("primaryCategory");
                                unit = object.getString("unit");


                                myProduct.setMySkus(skuName);
                                myProduct.setBrand(brandName);
                                myProduct.setUnit(unit);

                                myProductList.add(myProduct);
                            }

                            JSONObject ret = newOrderObj.getJSONObject("retailer");
                            String retName = ret.getString("name");
                            String retaddress = ret.getString("address");
                            String retPhone = ret.getString("ownersPhone1");

                            JSONObject dis = newOrderObj.getJSONObject("distributor");
                            String disName = dis.getString("name");

                            PCOrderHistoryItem.setRid(rid);
                            PCOrderHistoryItem.setRetName(retName);
                            PCOrderHistoryItem.setRetAddress(retaddress);
                            PCOrderHistoryItem.setRetPhone(retPhone);
                            PCOrderHistoryItem.setDid(did);
                            PCOrderHistoryItem.setDisName(disName);
                            PCOrderHistoryItem.setOrderType(orderType);
                            PCOrderHistoryItem.setTakenDate(orderDate);
                            PCOrderHistoryItem.setMyOrderHistoryList(myProductList);

                            orderHistoryList.add(PCOrderHistoryItem);

                        }

                        initializeList();
                    }
                    else
                    {
                        loader.dismiss();
                        Toast.makeText(PCOrderHistory.this, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                headers.put("authorization", myPref.getString("token", ""));
                return headers;
            }
        };

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(PCOrderHistory.this).add(objectRequest);

    }

    private void initializeList() {

        for (int temp = 0; temp < orderHistoryList.size(); temp++) {

            orderHistoryList2.add(temp, null);
        }

        Collections.sort(orderHistoryList, new Comparator<PCOrderHistoryItem>() {
            @Override
            public int compare(PCOrderHistoryItem o1, PCOrderHistoryItem o2) {
                return o2.getTakenDate().compareTo(o1.getTakenDate());
            }
        });

        pcOrderHistoryListAdapter = new PCOrderHistoryListAdapter(PCOrderHistory.this, orderHistoryList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PCOrderHistory.this);
        rvOrderHistory.setLayoutManager(layoutManager);
        rvOrderHistory.setAdapter(pcOrderHistoryListAdapter);
    }

    private class ShareTextTask extends AsyncTask<Void, Void, Void> {
        String text = "";
        ArrayList<PCOrderHistoryItem> orderHistoryList;

        public ShareTextTask(ArrayList<PCOrderHistoryItem> orderHistoryList2) {
            this.orderHistoryList = orderHistoryList2;
        }

        protected void onPreExecute() {
            for (int position = 0; position < orderHistoryList.size(); position++) {

                text = text.concat("Distributor : " + orderHistoryList.get(position).getDisName());
                text = text.concat("\n");
                text = text.concat("Date-05 : " + orderHistoryList.get(position).getTakenDate());
                text = text.concat("\n");
                text = text.concat("Shop Name : " + orderHistoryList.get(position).getRetName());
                text = text.concat("\n");
                text = text.concat("Address : " + orderHistoryList.get(position).getRetAddress());
                text = text.concat("\n");
                text = text.concat("Contact : " + orderHistoryList.get(position).getRetPhone());
                text = text.concat("\n");
                text = text.concat("\n");

                ArrayList<MyProduct> myOrderHistoryList = orderHistoryList.get(position).getMyOrderHistoryList();
                double total = 0;

                for (int pos = 0; pos < myOrderHistoryList.size(); pos++) {
                    MyProduct myProduct = myOrderHistoryList.get(pos);

                    text = text.concat(String.valueOf(pos + 1) + ". " + myProduct.getMySkus() + "    " + myProduct.getBrand() + "    "
                            + "Quantity : " + myProduct.getQuantity() + myProduct.getUnit());

                    total = total + Double.valueOf(myProduct.getQuantity());
                    text = text.concat("\n");
                }

                text = text.concat("\n");
                text = text.concat("Total Quantity : " + String.valueOf(total) + "Kg");
                text = text.concat("\n");
                text = text.concat("------------------------------------------------");
                text = text.concat("\n");
                text = text.concat("\n");

                Log.e("Share Text", "==" + text);
                Log.e("Share Text", "******************************************");
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        protected void onPostExecute(Void v) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Today Summary");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(sharingIntent, "Share summary"));
        }
    }
}
