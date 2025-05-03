package com.newsalesbeatApp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.DailyAssesmentActivity;
import com.newsalesbeatApp.activities.PCOrderHistory;
import com.newsalesbeatApp.activities.WorkDetails;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.pojo.SkuItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.TransactionOptions;
import okhttp3.Call;
import okhttp3.OkHttpClient;

/*
 * Created by abc on 10/30/18.
 */

public class DailyAssesmentFragment extends Fragment {

    private final String[] months = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    String TAG = "DailyAssesmentFragment";
    SharedPreferences myPref;
    ServerCall serverCall;
    UtilityClass utilityClass;
    private ArrayList<String> marketName = new ArrayList<>();
    private ArrayList<String> town = new ArrayList<>();
    private ArrayList<String> beatName = new ArrayList<>();
    private ArrayList<String> contact = new ArrayList<>();
    private ArrayList<String> totalOpening = new ArrayList<>();
    private ArrayList<String> totalSecondary = new ArrayList<>();
    private ArrayList<String> totalClosing = new ArrayList<>();
    private ArrayList<ArrayList<SkuItem>> stocksList2 = new ArrayList<>();
    String strCheckInTime = "",strCheckOutTime = "";
    SalesBeatDb salesBeatDb;
    ArrayList<RetailerItem> visitedRetailerList = new ArrayList<>();
    Cursor visitedRetailerCursor = null;
    private int Vi;
    public File imagePath;
    private WebView webView;
    private Button btnShareImgWeb;
    private String whatAppName;
    ImageView imageView;
    ProgressBar progressBar;
    private static final int REQUEST_PERMISSION_CODE = 100;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.daily_assessment_layout_new, parent, false);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        serverCall = new ServerCall(requireContext());

        utilityClass = new UtilityClass(requireContext());
        salesBeatDb = SalesBeatDb.getHelper(getActivity());
        //Bundle bundle1 = getArguments();

//        if (bundle1 != null) {
//
//            String date = bundle1.getString("date");

        Log.e("DailyAssesmentFragment", "Selected Date: " + DailyAssesmentActivity.strDate);
        SharedPreferences prefSFA = getActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        Sentry.configureScope(scope -> {
            scope.setTag("page_locale", "en_US");
            scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
        });

        TransactionOptions txOptions = new TransactionOptions();
        txOptions.setBindToScope(true);
        ITransaction transaction = Sentry.startTransaction("DailyAssesmentFragment", "employee_reports",txOptions);
        try {
            if (transaction == null) {
                transaction = Sentry.startTransaction("processOrderBatch()", "task");
            }
            getEmployeeRecordByDate(getContext(), view, DailyAssesmentActivity.strDate);

        } catch (Exception e) {
            transaction.setThrowable(e);
            transaction.setStatus(SpanStatus.INTERNAL_ERROR);
            throw e;
        } finally {
            transaction.finish();
        }

//        }

        visitedRetailerCursor = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();
        try {
            if (visitedRetailerCursor != null && visitedRetailerCursor.getCount() > 0
                    && visitedRetailerCursor.moveToFirst()) {

                do {
                    RetailerItem visitedRetailerItem = new RetailerItem();


//                    visitedRetailerCursor.moveToFirst();
                    visitedRetailerItem.setOrderType(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("order_type")));
                    visitedRetailerItem.setReatialerTarget(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("taken_at")));
                    visitedRetailerItem.setServerStatus(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("order_status")));
                    visitedRetailerItem.setReatialerPC(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("brand_kg")));
                    visitedRetailerItem.setReatialerTC(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("brand_unit")));
                    visitedRetailerItem.setTimeStamp(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("check_out_time")));


                    visitedRetailerList.add(visitedRetailerItem);

                    Log.d(TAG, "Get Realiter From DB: " + new Gson().toJson(visitedRetailerList));
                } while (visitedRetailerCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Visted ret", "===" + e.getMessage());
        } finally {
            if (visitedRetailerCursor != null)
                visitedRetailerCursor.close();
        }

        return view;
    }

    private void getEmployeeRecordByDate(final Context _context, final View dialog, final String date) {
        Log.e("TAG", "check 1 Date: " + date);
        final Dialog loader = new Dialog(_context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        //@Umesh 02-Feb-2022
        //String date1="2018-04-10";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_RECORD_BY_DATE + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG", "EMP_RECORD_BY_DATE=====" + response);
                loader.dismiss();

                String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
                        status = "", totalWorkingTime = "", totalRetailingTime = "", reason = "",
                        target = "", achievement = "", beatAssigned = "", beatVisited = "", newCounter = "",
                        saleKg = "", saleBdl = "", wc = "", cc = "";

                try {

                    //@Umesh 02-Feb-2022
                    if (response.getInt("status") == 1) {
                        response = response.getJSONObject("data");

                        if (response.has("attendance") && !response.isNull("attendance"))
                            status = response.getString("attendance");

                        if (response.has("checkIn") && !response.isNull("checkIn"))
                        {
                            checkInTime = response.getString("checkIn");
                            strCheckInTime = response.getString("checkIn");
                        }


                        if (response.has("checkOut") && !response.isNull("checkOut")){
                            checkOutTime = response.getString("checkOut");
                            strCheckOutTime = response.getString("checkOut");
                        }


                        if (response.has("totalCalls") && !response.isNull("totalCalls"))
                            totalCall = response.getString("totalCalls");

                        if (response.has("productiveCalls") && !response.isNull("productiveCalls"))
                            productiveCall = response.getString("productiveCalls");

                        if (response.has("linesSold") && !response.isNull("linesSold"))
                            lineSold = response.getString("linesSold");

                        if (response.has("wc") && !response.isNull("wc"))
                            wc = response.getString("wc");

                        if (response.has("cc") && !response.isNull("cc"))
                            cc = response.getString("cc");

                        if (response.has("totalWorkingTime") && !response.isNull("totalWorkingTime"))
                            totalWorkingTime = response.getString("totalWorkingTime");

                        if (response.has("totalRetailingTime") && !response.isNull("totalRetailingTime"))
                            totalRetailingTime = response.getString("totalRetailingTime");

                        if (response.has("reason") && !response.isNull("reason"))
                            reason = response.getString("reason");

                        if (response.has("target") && !response.isNull("target"))
                            target = response.getString("target");

                        //@Umesh 20221006
                        if (response.has("saleKg") && !response.isNull("saleKg"))
                            saleKg = response.getString("saleKg");

                        if (response.has("saleBdl") && !response.isNull("saleBdl"))
                            saleBdl = response.getString("saleBdl");

                        if (response.has("achievement") && !response.isNull("achievement"))
                            achievement = response.getString("achievement");

                        if (response.has("beatAssignedCount") && !response.isNull("beatAssignedCount"))
                            beatAssigned = response.getString("beatAssignedCount");

                        if (response.has("beatVisitedCount") && !response.isNull("beatVisitedCount"))
                            beatVisited = response.getString("beatVisitedCount");

                        final List<RetailerItem> newRetailerItemList = new ArrayList<>();
                        if (response.has("nc") && !response.isNull("nc")) {
                            JSONArray ncArr = response.getJSONArray("nc");
                            for (int t = 0; t < ncArr.length(); t++) {
                                RetailerItem retailerItem = new RetailerItem();
                                JSONObject ncObj = (JSONObject) ncArr.get(t);
                                retailerItem.setRetailerName(ncObj.getString("name"));
                                retailerItem.setRetailerAddress(ncObj.getString("address"));
                                retailerItem.setRetailer_state(ncObj.getString("state"));
                                retailerItem.setRetailer_owner_name(ncObj.getString("ownersName"));
                                retailerItem.setRetailerPhone(ncObj.getString("ownersPhone1"));
                                retailerItem.setRetailer_city(ncObj.getString("district"));
                                retailerItem.setRetailerLocality(ncObj.getString("locality"));
                                retailerItem.setRetailer_pin(ncObj.getString("pin"));
                                retailerItem.setTimeStamp(ncObj.getString("outletChannel"));
                                retailerItem.setOrderType(ncObj.getString("shopType"));
                                retailerItem.setRetailer_grade(ncObj.getString("grade"));
                                retailerItem.setRetailer_image(ncObj.getString("image"));

                                newRetailerItemList.add(retailerItem);
                            }
                        }

                        if (response.has("ncCount") && !response.isNull("ncCount"))
                            newCounter = response.getString("ncCount");

                        final List<RetailerItem> retailerItemList = new ArrayList<>();
                        if (response.has("tcDetail") && !response.isNull("tcDetail")) {


                            JSONArray tcDetails = response.getJSONArray("tcDetail");
                            for (int t = 0; t < tcDetails.length(); t++) {
                                RetailerItem retailerItem = new RetailerItem();
                                JSONObject tcObj = (JSONObject) tcDetails.get(t);
                                retailerItem.setRetailerName(tcObj.getString("name"));
                                retailerItem.setRetailer_pin(tcObj.getString("checkIn"));
                                retailerItem.setRetailerPhone(tcObj.getString("checkOut"));
                                retailerItem.setOrderType(tcObj.getString("comments"));

                                retailerItemList.add(retailerItem);
                            }
                        }

                        final List<SkuItem> skuItemList = new ArrayList<>();
                        if (response.has("linedSoldDetail") && !response.isNull("linedSoldDetail")) {
                            JSONArray lineSoldArr = response.getJSONArray("linedSoldDetail");

                            for (int l = 0; l < lineSoldArr.length(); l++) {
                                SkuItem skuItem = new SkuItem();
                                JSONObject skuArr = (JSONObject) lineSoldArr.get(l);
                                String item = skuArr.getString("sku");
                                String unit = skuArr.getString("unit");
                                String qty = skuArr.getString("qty");

                                skuItem.setSku(item);
                                skuItem.setOpening(qty);
                                skuItem.setClosing(unit);

                                skuItemList.add(skuItem);
                            }
                        }

                        final ArrayList<String> beatList = new ArrayList<>();
                        if (response.has("beatVisited") && !response.isNull("beatVisited")) {
                            JSONArray beatVArr = response.getJSONArray("beatVisited");

                            for (int b = 0; b < beatVArr.length(); b++) {
                                JSONObject beatVisit = (JSONObject) beatVArr.get(b);
                                beatList.add((String) beatVisit.getString("name"));
                            }
                        }

                        final ArrayList<String> beatList2 = new ArrayList<>();
                        if (response.has("beatAssigned") && !response.isNull("beatAssigned")) {
                            JSONArray beatAArr = response.getJSONArray("beatAssigned");

                            for (int b1 = 0; b1 < beatAArr.length(); b1++) {
                                JSONObject beatAssign = (JSONObject) beatAArr.get(b1);
                                beatList2.add((String) beatAssign.get("name"));
                            }
                        }

                        String fullDayActivity = "";
                        final List<Item> fullDayActivityList = new ArrayList<>();
                        if (response.has("fullDayActivity") && !response.isNull("fullDayActivity")) {
                            JSONArray fullDayActivityAArr = response.getJSONArray("fullDayActivity");
                            fullDayActivity = String.valueOf(fullDayActivityAArr.length());

                            for (int i = 0; i < fullDayActivityAArr.length(); i++) {

                                Item item = new Item();
                                JSONObject object = (JSONObject) fullDayActivityAArr.get(i);
                                item.setItem1(object.getString("activity"));
                                item.setItem2(object.getString("remarks"));

                                fullDayActivityList.add(item);
                            }
                        }


                        final List<String> jointWorkingList = new ArrayList<>();
                        if (response.has("jointWorking") && !response.isNull("jointWorking")) {
                            JSONArray fullDayActivityAArr = response.getJSONArray("jointWorking");

                            for (int i = 0; i < fullDayActivityAArr.length(); i++) {

                                String emp = (String) fullDayActivityAArr.get(i);

                                jointWorkingList.add(emp);
                            }
                        }


//                    final Dialog dialog = new Dialog(_context, R.style.DialogActivityTheme);
//                    dialog.setContentView(R.layout.dailyassesment_dailog);
//                    dialog.getWindow().setGravity(Gravity.BOTTOM);
//                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//                    final TextView tvDateView = dialog.findViewById(R.id.tvDateView);
                        TextView tvStatus = dialog.findViewById(R.id.tvStatus);
                        TextView tvCheckInTime = dialog.findViewById(R.id.tvCheckInTimeD);
                        TextView tvCheckOutTime = dialog.findViewById(R.id.tvCheckOutTimeD);
                        final TextView tvTotalCall = dialog.findViewById(R.id.tvTotalCall);
                        final TextView tvProductiveCall = dialog.findViewById(R.id.tvProductiveCall);
                        TextView tvLineSold = dialog.findViewById(R.id.tvLineSold);
                        TextView tvReason = dialog.findViewById(R.id.tvReason);
                        TextView tvTarget = dialog.findViewById(R.id.tvTarget);
                        TextView tvAchievement = dialog.findViewById(R.id.tvAchievement);
                        TextView tvBeatAssigned = dialog.findViewById(R.id.tvBeatAssigned);
                        TextView tvBeatVisited = dialog.findViewById(R.id.tvBeatVisited);
                        TextView tvNewCounter = dialog.findViewById(R.id.tvNewCounter);
                        TextView tvTotalRetailingHour = dialog.findViewById(R.id.tvTRT);
                        TextView tvTotalWorkingHour = dialog.findViewById(R.id.tvTWH);
                        TextView tvWcall = dialog.findViewById(R.id.tvWcall);
                        TextView tvCcall = dialog.findViewById(R.id.tvCcall);
//                        TextView tvSale = dialog.findViewById(R.id.tvSale);
                        //@Umesh 20221008
                        TextView tvSaleTea = dialog.findViewById(R.id.tvSaleTea);
                        TextView tvSaleMatch = dialog.findViewById(R.id.tvSaleMatch);
                        TextView tvFullDayActivity = dialog.findViewById(R.id.tvFullDayActivity);

                        CardView activityCard = dialog.findViewById(R.id.activity_card);
                        CardView reasonCard = dialog.findViewById(R.id.reason_card);
                        CardView checkCard = dialog.findViewById(R.id.check_card);
                        CardView callCard = dialog.findViewById(R.id.call_card);
                        CardView beatCard = dialog.findViewById(R.id.beat_card);
                        CardView targetCard = dialog.findViewById(R.id.target_card);
                        CardView achCard = dialog.findViewById(R.id.ach_card);
                        CardView timeCard = dialog.findViewById(R.id.time_card);
                        CardView lineCard = dialog.findViewById(R.id.lines_sold_card);
                        ImageView attend = dialog.findViewById(R.id.attend);

//                    ImageView imgForward = dialog.findViewById(R.id.imgForward);
//                    ImageView imgForwardL = dialog.findViewById(R.id.imgForwardL);
//                    ImageView imgForwardT = dialog.findViewById(R.id.imgForwardT);
//                    ImageView imgForwardBA = dialog.findViewById(R.id.imgForwardBA);
//                    ImageView imgForwardBV = dialog.findViewById(R.id.imgForwardBV);
//                    ImageView imgForwardOA = dialog.findViewById(R.id.imgForwardOA);
//                    ImageView imgForwardNC = dialog.findViewById(R.id.imgForwardNC);

                        TextView imgForward = dialog.findViewById(R.id.imgForward);
                        TextView imgForwardL = dialog.findViewById(R.id.imgForwardL);
                        TextView imgForwardT = dialog.findViewById(R.id.imgForwardT);
                        TextView imgForwardBA = dialog.findViewById(R.id.imgForwardBA);
                        TextView imgForwardBV = dialog.findViewById(R.id.imgForwardBV);
                        TextView imgForwardOA = dialog.findViewById(R.id.imgForwardOA);
                        TextView imgForwardNC = dialog.findViewById(R.id.imgForwardNC);
//
//                    LinearLayout llOk = dialog.findViewById(R.id.llCancel);
//                    LinearLayout llShowSummary = dialog.findViewById(R.id.llShowSummary);

                        TextView llShowSummary = dialog.findViewById(R.id.llShowSummary);

                        String arr[] = date.split("-");
                        String yr = arr[0];
                        String mn = arr[1];
                        String dy = arr[2];
                        int tempCount = Integer.parseInt(mn) - 1;
                        String monthStr = months[tempCount];
                        String strDate = dy + ", " + monthStr + ", " + yr;
//                    tvDateView.setText(strDate);

                        if (status != null && !status.isEmpty()) {
                            if (status.toLowerCase(Locale.ROOT).equals("present"))
                                tvStatus.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
                            else {
                                tvStatus.setTextColor(getContext().getResources().getColor(R.color.absent));
                                checkCard.setVisibility(View.GONE);
                                callCard.setVisibility(View.GONE);
                                beatCard.setVisibility(View.GONE);
                                lineCard.setVisibility(View.GONE);
                                targetCard.setVisibility(View.GONE);
                                achCard.setVisibility(View.GONE);
                                timeCard.setVisibility(View.GONE);
                                activityCard.setVisibility(View.GONE);
                                llShowSummary.setVisibility(View.GONE);
                                attend.setVisibility(View.VISIBLE);
                            }

                            tvStatus.setText(status);
                        }

                        if (checkInTime != null && !checkInTime.isEmpty())
                            tvCheckInTime.setText(checkInTime);

                        if (checkOutTime != null && !checkOutTime.isEmpty())
                            tvCheckOutTime.setText(checkOutTime);

                        if (totalCall != null && !totalCall.isEmpty())
                            tvTotalCall.setText(totalCall);

                        if (productiveCall != null && !productiveCall.isEmpty())
                            tvProductiveCall.setText(productiveCall);
                        else
                            imgForward.setVisibility(View.GONE);

                        if (lineSold != null && !lineSold.isEmpty())
                            tvLineSold.setText(lineSold);

                        if (wc != null && !wc.isEmpty())
                            tvWcall.setText(wc);
                        else
                            tvWcall.setText("0");

                        if (cc != null && !cc.isEmpty())
                            tvCcall.setText(cc);
                        else
                            tvCcall.setText("0");

                        if (reason != null && !reason.isEmpty())
                            tvReason.setText(reason);
                        else
                            reasonCard.setVisibility(View.GONE);

                        if (target.isEmpty())
                            target = "N/A";
                        tvTarget.setText(target);
                        tvAchievement.setText(achievement);
                        tvBeatAssigned.setText(beatAssigned);
                        tvBeatVisited.setText(beatVisited);
//                        tvSale.setText(saleKg+" KG|"+saleBdl+" BDL");
                        //@Umesh 20221008
                        tvSaleTea.setText(saleKg + " KG");
                        tvSaleMatch.setText(saleBdl + " BDL");
                        tvNewCounter.setText(newCounter);

                        if (achievement.isEmpty())
                            achCard.setVisibility(View.GONE);


                        if (totalRetailingTime != null && !totalRetailingTime.isEmpty())
                            tvTotalRetailingHour.setText(totalRetailingTime + " hrs");

                        if (totalWorkingTime != null && !totalWorkingTime.isEmpty())
                            tvTotalWorkingHour.setText(totalWorkingTime + " hrs");

                        tvFullDayActivity.setText(fullDayActivity);

                        if (productiveCall.equalsIgnoreCase("0")
                                || productiveCall.isEmpty()) {
                            imgForward.setVisibility(View.GONE);
                        }

                        if (skuItemList == null || skuItemList.size() == 0)
                            imgForwardL.setVisibility(View.GONE);

                        if (retailerItemList == null || retailerItemList.size() == 0)
                            imgForwardT.setVisibility(View.GONE);

                        if (beatList2 == null || beatList2.size() == 0) {
                            imgForwardBA.setVisibility(View.GONE);
                        }

                        if (beatList == null || beatList.size() == 0)
                            imgForwardBV.setVisibility(View.GONE);

                        if (fullDayActivityList == null || fullDayActivityList.size() == 0) {
//                        imgForwardOA.setVisibility(View.INVISIBLE);
                            activityCard.setVisibility(View.GONE);
                        }

                        if (newRetailerItemList == null || newRetailerItemList.size() == 0)
                            imgForwardNC.setVisibility(View.GONE);

                        imgForward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(_context, PCOrderHistory.class);
                                intent.putExtra("date", date);
                                _context.startActivity(intent);
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

                        imgForwardL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(_context, WorkDetails.class);
                                intent.putExtra("type", "L");
                                intent.putExtra("list", (Serializable) skuItemList);
                                _context.startActivity(intent);
                                // ((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

                        imgForwardT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(_context, WorkDetails.class);
                                intent.putExtra("type", "T");
                                intent.putExtra("list", (Serializable) retailerItemList);
                                _context.startActivity(intent);
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

                        imgForwardBA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(_context, WorkDetails.class);
                                intent.putExtra("type", "BA");
                                intent.putStringArrayListExtra("list", beatList2);
                                _context.startActivity(intent);
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

                        imgForwardBV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("BEAT", "==" + beatList2.size());
                                Intent intent = new Intent(_context, WorkDetails.class);
                                intent.putExtra("type", "BV");
                                intent.putStringArrayListExtra("list", beatList);
                                _context.startActivity(intent);
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

                        imgForwardOA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("BEAT", "==" + beatList2.size());
                                Intent intent = new Intent(_context, WorkDetails.class);
                                intent.putExtra("type", "OA");
                                intent.putExtra("list", (Serializable) fullDayActivityList);
                                intent.putExtra("empList", (Serializable) jointWorkingList);
                                _context.startActivity(intent);
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

                        imgForwardNC.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(_context, WorkDetails.class);
                                intent.putExtra("type", "NC");
                                intent.putExtra("list", (Serializable) newRetailerItemList);
                                _context.startActivity(intent);
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }
                        });

//                    llOk.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            //dialog.dismiss();
//                        }
//                    });


                        llShowSummary.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("TAG", "onClickSummary :"+date);
                                Log.d("TAG", "onClickSummary :"+myPref.getString("username", ""));
                                String uName = myPref.getString("username", "");
//                                showSummary(date,_context);
                                showSummaryWeb(date,uName,_context);

                            }
                        });

                    } else {
                        loader.dismiss();
                        Toast.makeText(DailyAssesmentFragment.super.getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    loader.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loader.dismiss();
                serverCall.handleError(error, TAG, "employee-reports/date/");
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                return headers;
            }
        };

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(_context).add(objectRequest);
    }

    private void showSummaryWeb(String date, String uName, final Context context) {
        Log.d(TAG, "Opening Summary WebView");

        // Create and configure dialog
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.employee_summary_layout_two);

        // Initialize WebView and Share button
        WebView webView = dialog.findViewById(R.id.webView);
        Button btnShareImgWeb = dialog.findViewById(R.id.btnShareImgWeb);

        configureWebView(webView);

        // Construct the URL
        String baseUrl = getString(R.string.url_mode).equalsIgnoreCase("T")
                ? "http://testsalesbeat.rungtatea.in/HtmlReport/SalesSummary/"
                : "https://salesbeat.rungtatea.in/HtmlReport/SalesSummary/";
        String fullUrl = baseUrl + uName + '/' + date;

        // Log the URL for debugging
        Log.d(TAG, "WebView URL: " + fullUrl);

        // Load the URL
        webView.loadUrl(fullUrl);

        // Share button click listener
        btnShareImgWeb.setOnClickListener(v -> {
            try {
                Bitmap bitmap = captureScreenshot(webView);
                if (bitmap != null) {
                    Uri imageUri = saveBitmapToFile(context, bitmap);
                    shareImageOnWhatsApp(imageUri, context);
                } else {
                    Toast.makeText(context, "Error capturing WebView content", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error saving image", e);
                Toast.makeText(context, "Error saving image", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void configureWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Set a custom User-Agent if required
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36";
        webSettings.setUserAgentString(userAgent);

        // Set WebViewClient to handle navigation within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.getUrl().toString(), getDesktopHeaders());
                }
                return true;
            }
        });
    }

    private Map<String, String> getDesktopHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
        return headers;
    }

    private Bitmap captureScreenshot(WebView webView) {
        try {
            // Measure WebView content height and width
            int width = webView.getMeasuredWidth();
            int height = (int) (webView.getContentHeight() * webView.getScale());

            // Create a bitmap with the content size
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // Draw the WebView content onto the canvas
            webView.draw(canvas);

            return bitmap;
        } catch (Exception e) {
            Log.e(TAG, "Error capturing WebView screenshot", e);
            return null;
        }
    }

    private Uri saveBitmapToFile(Context context, Bitmap bitmap) throws IOException {
        // Save the bitmap to a file in the external cache directory
        File file = new File(context.getExternalCacheDir(), "webview_screenshot.png");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        // Return the file URI using the correct authority
        return FileProvider.getUriForFile(context, "com.newsalesbeat.fileprovider", file);
    }

    private void shareImageOnWhatsApp(Uri imageUri, Context context) {
        Log.d(TAG, "shareImageOnWhatsApp: " + imageUri);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*"); // Use "image/*" instead of "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Don't set the package and let Android show available apps
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "Share Image via"));
        } else {
            Toast.makeText(context, "No app available to share the image.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showSummary(String date, final Context _context) {
        Log.d("TAG", "click showSummary");
        final Dialog loader = new Dialog(_context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        //@Umesh 02-Feb-2022
        //String date1="2018-04-10";
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, SbAppConstants.API_GET_DAILY_SUMMARY + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // response
                loader.dismiss();

                Log.e("TAG", "Daily summary====" + response);
                try {
                    //@Umesh 02-Feb-2022
                    if (response.getInt("status") == 1) {
                        stocksList2.clear();
                        marketName.clear();
                        town.clear();
                        beatName.clear();
                        contact.clear();
                        totalOpening.clear();
                        totalSecondary.clear();
                        totalClosing.clear();


                        JSONObject data = response.getJSONObject("data");
                        String date = data.getString("date");
                        String name = data.getString("name");
                        whatAppName = data.getString("name");
                        Log.d(TAG, "check Name : "+whatAppName);
                        String tc = data.getString("tc");
                        String pc = data.getString("pc");

                        //String sale = data.getString("sale");
                        //@Umesh 20221017
                        String saleKg = data.getString("saleKg");
                        String saleBdl = data.getString("saleBdl");
                        String saleTea = saleKg;
                        String saleMatch = saleBdl;


                        String mtc = data.getString("mtc");
                        String mpc = data.getString("mpc");
                        String nc = data.getString("ncCount");
                        //String mSale = data.getString("msale");
                        //@Umesh 20221017
                        String msaleKg = data.getString("msaleKg");
                        String msaleBdl = data.getString("msaleBdl");
                        String mSaleTea = msaleKg;
                        String mSaleMatch = msaleBdl;

                        JSONArray distributors = data.getJSONArray("distributors");
                        for (int index = 0; index < distributors.length(); index++) {

                            JSONObject object = (JSONObject) distributors.get(index);
                            marketName.add(object.getString("name"));
                            town.add(object.getString("town"));
                            beatName.add(object.getString("beat"));
                            contact.add(object.getString("contact"));
                            totalOpening.add(object.getString("opening"));
                            totalSecondary.add(object.getString("secondary"));
                            totalClosing.add(object.getString("closing"));

                            JSONArray stocks = object.getJSONArray("stocks");
                            ArrayList<SkuItem> stocksList = new ArrayList<>();
                            for (int i = 0; i < stocks.length(); i++) {

                                SkuItem skuItem = new SkuItem();
                                JSONObject objSt = (JSONObject) stocks.get(i);
                                skuItem.setSku(objSt.getString("sku"));
                                skuItem.setOpening(objSt.getString("opening"));
                                skuItem.setSecondary(objSt.getString("secondary"));
                                skuItem.setClosing(objSt.getString("closing"));

                                stocksList.add(skuItem);
                            }
                            Collections.sort(stocksList, new Comparator<SkuItem>() {
                                @Override
                                public int compare(SkuItem item1, SkuItem item2) {
                                    return item1.getSku().compareTo(item2.getSku());
                                }
                            });
                            stocksList2.add(stocksList);
                        }
                        getNewDitributorHistory(_context, date, name, tc, pc, saleTea, saleMatch, mtc, mpc, mSaleTea, mSaleMatch, nc);
                    } else {
                        loader.dismiss();
                        Toast.makeText(DailyAssesmentFragment.super.getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        loader.dismiss();

                        serverCall.handleError(error, TAG, "employeeSummary");

                        try {

                            if (error.networkResponse.statusCode == 422) {
                                String responseBody = null;
                                try {
                                    responseBody = new String(error.networkResponse.data, "utf-8");
                                    Log.e("ERRR", "===== " + responseBody);
                                    JSONObject object = new JSONObject(responseBody);
                                    String message = object.getString("message");
                                    JSONObject errorr = object.getJSONObject("errors");

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(_context);
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

                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                1200000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(_context).add(postRequest);

    }


    private void getNewDitributorHistory(final Context _context, final String date, final String name, final String tc, final String pc, final String saleTea,
                                         final String saleMatch,
                                         final String mtc, final String mpc, final String mSaleTea, final String mSaleMatch, final String nc) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_NEW_DISTRIBUTOR_HISTORY + "?fromDate=" + date + "&toDate=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("onResponse", "New Distributor history===" + response);
                //loader.dismiss();
                try {
                    //@Umesh
                    if (response.getInt("status") == 1) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray distributors = data.getJSONArray("distributors");
                        ArrayList<ClaimHistoryItem> newDistributorDetails = new ArrayList<>();
                        //List<String> listProductDivision = new ArrayList<>();
                        //List<String> listWorkingBrand = new ArrayList<>();
                        //List<String> listBeatName = new ArrayList<>();
                        //List<Double> ownerImageLatLong = new ArrayList<>();
                        //List<Double> firmImageLatLong = new ArrayList<>();

                        for (int i = 0; i < distributors.length(); i++) {

                            JSONObject object = (JSONObject) distributors.get(i);
                            ClaimHistoryItem disH = new ClaimHistoryItem();

                            disH.setFirmName(object.getString("firmName"));
                            disH.setFirmAddress(object.getString("firmAddress"));
                            disH.setPin(object.getString("pin"));
                            disH.setCity(object.getString("city"));
                            disH.setState(object.getString("state"));
                            disH.setOwnerName(object.getString("ownerName"));
                            disH.setMobile1(object.getString("ownerMobile1"));
                            disH.setMobile2(object.getString("ownerMobile2"));
                            disH.setOwnerEmail(object.getString("email"));
                            disH.setGstin(object.getString("gstin"));
                            disH.setFssai(object.getString("fsi"));
                            disH.setPan(object.getString("pan"));
                            disH.setMonthlyTurnOver(object.getString("monthlyTurnover"));
                            disH.setOpDis(object.getString("opinion"));
                            disH.setRemarks(object.getString("comment"));
                            List<String> listProductDivision = new ArrayList<>();
                            listProductDivision.add(object.getString("productDivision"));
                            disH.setProduct(listProductDivision.toString());
//                                listWorkingBrand.add(object.getString("workingBrand"));
//                                listBeatName.add(object.getString("beatName"));
//                                ownerImageLatLong.add(0.0);//Double.valueOf(object.getString("ownerImageLatLong"))
//                                firmImageLatLong.add(0.0);//Double.valueOf(object.getString("firmImageLatLong"))

                            newDistributorDetails.add(disH);

                        }

                        // WeakReference<requireActivity()> loginActivityWeakRef= new WeakReference<login >(loginActivity) ;

                        if (requireActivity() != null && !requireActivity().isFinishing()) {
                            Log.d("TAG", "check totalOpening: "+totalOpening);
                            Log.d("TAG", "check totalSecondary: "+totalSecondary);
                            Log.d("TAG", "check totalClosing: "+totalClosing);
                            /*showDailySummaryDailog(_context, date, name, tc, pc, saleTea, saleMatch, mtc, mpc, mSaleTea, mSaleMatch,
                                    marketName, town, contact, totalOpening, totalSecondary, totalClosing, stocksList2,
                                    nc, newDistributorDetails);*/
                        }
                    } else {
                        Toast.makeText(DailyAssesmentFragment.super.getContext(), "Error:" + response.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (requireActivity() != null && !requireActivity().isFinishing()) {

                    /*showDailySummaryDailog(_context, date, name, tc, pc, saleTea, saleMatch, mtc, mpc, mSaleTea, mSaleMatch,
                            marketName, town, contact, totalOpening, totalSecondary, totalClosing, stocksList2,
                            nc, null);*/
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

        Volley.newRequestQueue(_context).add(jsonObjectRequest);

    }

    private void showDailySummaryDailog(final Context _context, String date, String name, String tc, String pc, String saleTea, String saleMatch, String mtc,
                                        String mpc, String mSaleTea, String mSaleMatch, ArrayList<String> marketName, ArrayList<String> town,
                                        ArrayList<String> contact, ArrayList<String> totalOpening,
                                        ArrayList<String> totalSecondary, ArrayList<String> totalClosing,
                                        ArrayList<ArrayList<SkuItem>> stocksList2, String nc,
                                        ArrayList<ClaimHistoryItem> newDistributorDetails) {

        final Dialog dialog = new Dialog(_context, R.style.DialogTheme);
        dialog.setContentView(R.layout.employee_summary_layout);

        /*Log.d(TAG, "Get Final Realiter List: " + new Gson().toJson(visitedRetailerList));
        for (int i = 0; i < visitedRetailerList.size(); i++) {
            String dt = visitedRetailerList.get(i).getReatialerTarget();
            String strKG = visitedRetailerList.get(i).getReatialerTC();
            String strPC = visitedRetailerList.get(i).getReatialerPC();

            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);

            try {
                Date date1 = dateFormat.parse(dt);
                Log.d(TAG, "showDailySummaryDailog Date: "+date1);
                int getHours = date1.getHours();
                Log.d(TAG, "showDailySummaryDailog getHours: "+getHours);

                if(getHours == 10){

                }else if(getHours == 11){

                }else if(getHours == 12){

                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }*/
//        String currentTime = myPref.getString(getString(R.string.check_in_time_key), "");
//        Log.d(TAG, "showDailySummaryDailog currentTime: "+currentTime);
//        myPref.getString(getString(R.string.check_in_time_key), "");

//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        if (dialog.getWindow() != null)
//            lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setAttributes(lp);
//        if (dialog.getWindow() != null)
//            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final TextView tvDate = dialog.findViewById(R.id.tvDate);
        final TextView tvAsmTsiName = dialog.findViewById(R.id.tvAsmTsiName);
        final TextView tvTodayTc = dialog.findViewById(R.id.tvTodayTc);
        final TextView tvTodayPc = dialog.findViewById(R.id.tvTodayPc);
        final TextView tvTodayNc = dialog.findViewById(R.id.tvTodayNc);
        final TextView tvTodayTotalSaleTea = dialog.findViewById(R.id.tvTodayTotalSaleTea); //@Umesh 20221021
        final TextView tvTodayTotalSaleMatch = dialog.findViewById(R.id.tvTodayTotalSaleMatch); //@Umesh 20221021
        final TextView tvMTDTea = dialog.findViewById(R.id.tvMTDTea); //@Umesh 20221021
        final TextView tvMTDMatch = dialog.findViewById(R.id.tvMTDMatch);  //@Umesh 20221021
        final TextView tvTC = dialog.findViewById(R.id.tvTC);
        final TextView tvPC = dialog.findViewById(R.id.tvPC);
        final TextView tvInTime = dialog.findViewById(R.id.txtInTime);
        final TextView tvCheckOut = dialog.findViewById(R.id.txtCheckOut);
        final TextView tvFirstCall = dialog.findViewById(R.id.txtFirstCall);
        final TextView tvLastCall = dialog.findViewById(R.id.txtLastCall);
        Button btnShareTxt = dialog.findViewById(R.id.btnShareTxt); //@Umesh 20221021
        Button btnShareImg = dialog.findViewById(R.id.btnShareImg); //@Umesh 20221021
        LinearLayout container = dialog.findViewById(R.id.container);
        LinearLayout container2 = dialog.findViewById(R.id.container2);

        final TextView txt10Tc = dialog.findViewById(R.id.txt10Tc);
        final TextView txt10Pc = dialog.findViewById(R.id.txt10Pc);
        final TextView txt10Kg = dialog.findViewById(R.id.txt10Kg);

        final TextView txt11Tc = dialog.findViewById(R.id.txt11Tc);
        final TextView txt11Pc = dialog.findViewById(R.id.txt11Pc);
        final TextView txt11Kg = dialog.findViewById(R.id.txt11Kg);

        final TextView txt12Tc = dialog.findViewById(R.id.txt12Tc);
        final TextView txt12Pc = dialog.findViewById(R.id.txt12Pc);
        final TextView txt12Kg = dialog.findViewById(R.id.txt12Kg);

        final TextView txt1Tc = dialog.findViewById(R.id.txt1Tc);
        final TextView txt1Pc = dialog.findViewById(R.id.txt1Pc);
        final TextView txt1Kg = dialog.findViewById(R.id.txt1Kg);

        final TextView txt2Tc = dialog.findViewById(R.id.txt2Tc);
        final TextView txt2Pc = dialog.findViewById(R.id.txt2Pc);
        final TextView txt2Kg = dialog.findViewById(R.id.txt2Kg);

        final TextView txt3Tc = dialog.findViewById(R.id.txt3Tc);
        final TextView txt3Pc = dialog.findViewById(R.id.txt3Pc);
        final TextView txt3Kg = dialog.findViewById(R.id.txt3Kg);

        final TextView txt4Tc = dialog.findViewById(R.id.txt4Tc);
        final TextView txt4Pc = dialog.findViewById(R.id.txt4Pc);
        final TextView txt4Kg = dialog.findViewById(R.id.txt4Kg);

        final TextView txt5Tc = dialog.findViewById(R.id.txt5Tc);
        final TextView txt5Pc = dialog.findViewById(R.id.txt5Pc);
        final TextView txt5Kg = dialog.findViewById(R.id.txt5Kg);


        final TextView txtTotalTc = dialog.findViewById(R.id.txtTotalTc);
        final TextView txtTotalPc = dialog.findViewById(R.id.txtTotalPc);
        final TextView txtTotalKg = dialog.findViewById(R.id.txtTotalKg);

        Log.d(TAG, "Get Final Realiter List: " + new Gson().toJson(visitedRetailerList));
        int totalkgValue = 0,totalPcValue=0;
        List<Date> dates = new ArrayList<>();
        for (int i = 0; i < visitedRetailerList.size(); i++) {
            int kgValue = 0,PcValue=0,tenTc = 0,elevenTc = 0,twelveTc = 0,oneTc = 0,twoTc = 0,threeTc = 0,fourTc = 0;
            String dt = visitedRetailerList.get(i).getReatialerTarget();
            String strKG = visitedRetailerList.get(i).getReatialerTC();
            String strPC = visitedRetailerList.get(i).getReatialerPC();
            String strDate = visitedRetailerList.get(i).getTimeStamp();
//            String strDate = visitedRetailerList.get(i).get;

            String[] parts = strKG.split("_");
            if (parts.length > 0 && !parts[0].isEmpty()) {
                kgValue = Integer.parseInt(parts[0]);
                Log.d(TAG, "showDailySummaryDailog value: "+kgValue); // Output: 5
            }

            String[] parts1 = strPC.split("_");
            if (parts1.length > 0 && !parts1[0].isEmpty()) {
                PcValue = Integer.parseInt(parts1[0]);
                Log.d(TAG, "showDailySummaryDailog PcValue: "+PcValue); // Output: 5
            }

            totalkgValue += kgValue;
            totalPcValue += PcValue;
//            totalTCValue += Value;

            Log.d(TAG, "totalkgValue: "+totalkgValue);
            Log.d(TAG, "totalPcValue: "+totalPcValue);

            String format = "yyyy-MM-dd HH:mm:ss"; // The date format corresponding to the date string
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);

            try {
                Date date1 = dateFormat.parse(dt);
                Log.d(TAG, "showDailySummaryDailog Date: "+date1);
                int getHours = date1.getHours();
                Log.d(TAG, "showDailySummaryDailog getHours: "+getHours);

                if(getHours < 10){
                    tenTc++;
                    txt10Pc.setText((Integer.parseInt(txt10Pc.getText().toString())+PcValue)+"");
                    txt10Kg.setText((Integer.parseInt(txt10Kg.getText().toString())+kgValue)+"");
                    txt10Tc.setText((Integer.parseInt(txt10Tc.getText().toString())+tenTc)+"");
                }else if(getHours < 11){
                    tenTc++;
                    Log.d(TAG, "Total Call 11Am: "+visitedRetailerList.size());
                    txt11Pc.setText((Integer.parseInt(txt11Pc.getText().toString())+PcValue)+"");
                    txt11Kg.setText((Integer.parseInt(txt11Kg.getText().toString())+kgValue)+"");
                    txt11Tc.setText((Integer.parseInt(txt11Tc.getText().toString())+tenTc)+"");
                }else if(getHours < 12){
                    elevenTc++;
                    txt12Pc.setText((Integer.parseInt(txt12Pc.getText().toString())+PcValue)+"");
                    txt12Kg.setText((Integer.parseInt(txt12Kg.getText().toString())+kgValue)+"");
                    txt12Tc.setText((Integer.parseInt(txt12Tc.getText().toString())+elevenTc)+"");
                }else if(getHours < 13){
                    twelveTc++;
                    txt1Pc.setText((Integer.parseInt(txt1Pc.getText().toString())+PcValue)+"");
                    txt1Kg.setText((Integer.parseInt(txt1Kg.getText().toString())+kgValue)+"");
                    txt1Tc.setText((Integer.parseInt(txt1Tc.getText().toString())+twelveTc)+"");
                }else if(getHours < 14){
                    twoTc++;
                    Log.d(TAG, "Total Call 14Am: "+twoTc);
                    txt2Pc.setText((Integer.parseInt(txt2Pc.getText().toString())+PcValue)+"");
                    txt2Kg.setText((Integer.parseInt(txt2Kg.getText().toString())+kgValue)+"");
                    txt2Tc.setText((Integer.parseInt(txt2Tc.getText().toString())+twoTc)+"");
//                    txt1Tc.setText((Integer.parseInt(txt1Tc.getText().toString())+oneTc)+"");
                }else if(getHours < 15){
                    twoTc++;
                    txt3Pc.setText((Integer.parseInt(txt3Pc.getText().toString())+PcValue)+"");
                    txt3Kg.setText((Integer.parseInt(txt3Kg.getText().toString())+kgValue)+"");
                    txt3Tc.setText((Integer.parseInt(txt3Tc.getText().toString())+twoTc)+"");
                }else if(getHours < 16){
                    threeTc++;
                    txt4Pc.setText((Integer.parseInt(txt4Pc.getText().toString())+PcValue)+"");
                    txt4Kg.setText((Integer.parseInt(txt4Kg.getText().toString())+kgValue)+"");
                    txt4Tc.setText((Integer.parseInt(txt4Tc.getText().toString())+threeTc)+"");
                }else if(getHours < 17){
                    fourTc++;
                    txt5Pc.setText((Integer.parseInt(txt5Pc.getText().toString())+PcValue)+"");
                    txt5Kg.setText((Integer.parseInt(txt5Kg.getText().toString())+kgValue)+"");
                    txt5Tc.setText((Integer.parseInt(txt5Tc.getText().toString())+fourTc)+"");
                }

            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                Date dateMX = dateFormat.parse(strDate);
                dates.add(dateMX);


            if (!dates.isEmpty()) {

                Comparator<Date> timeComparator = new Comparator<Date>() {
                    @Override
                    public int compare(Date date1, Date date2) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String time1 = timeFormat.format(date1);
                        String time2 = timeFormat.format(date2);
                        return time1.compareTo(time2);
                    }
                };

                Date minTime = Collections.min(dates, timeComparator);
                Date maxTime = Collections.max(dates, timeComparator);

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

                Log.d(TAG, "check Min Time: "+timeFormat.format(minTime));
                Log.d(TAG, "check Max Time: "+timeFormat.format(maxTime));

                Date dateMin = timeFormat.parse(timeFormat.format(minTime));
                Date dateMax = timeFormat.parse(timeFormat.format(maxTime));

                SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a");
                tvFirstCall.setText(sdf12.format(dateMin));
                tvLastCall.setText(sdf12.format(dateMax));

            } else {
                System.out.println("The list of dates is empty.");
            }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int c1 = Integer.parseInt(txt10Tc.getText().toString());
        int c2 = Integer.parseInt(txt11Tc.getText().toString());
        int c3 = Integer.parseInt(txt12Tc.getText().toString());
        int c4 = Integer.parseInt(txt1Tc.getText().toString());
        int c5 = Integer.parseInt(txt2Tc.getText().toString());
        int c6 = Integer.parseInt(txt3Tc.getText().toString());
        int c7 = Integer.parseInt(txt4Tc.getText().toString());
        int c8 = Integer.parseInt(txt5Tc.getText().toString());
        int totalTC = c1 + c2 + c3 + c4 + c5 + c6 + c7 + c8;
        Log.d(TAG, "showDailySummaryDailog sum : "+totalTC);
        txtTotalKg.setText((Integer.parseInt(txtTotalKg.getText().toString())+totalkgValue)+"");
        txtTotalPc.setText((Integer.parseInt(txtTotalPc.getText().toString())+totalPcValue)+"");
        txtTotalTc.setText(totalTC+"");

        String arr[] = date.split("-");
        String yr = arr[0];
        String mn = arr[1];
        String dy = arr[2];
        int tempCount = Integer.parseInt(mn) - 1;
        String monthStr = months[tempCount];
        String strDate = dy + " " + monthStr + ", " + yr;

        tvInTime.setText(strCheckInTime);
        tvCheckOut.setText(strCheckOutTime);
        tvDate.setText(strDate);
        tvAsmTsiName.setText(name);
        tvTC.setText(mtc);
        tvPC.setText(mpc);
        //tvMTD.setText(mSale + _context.getString(R.string.unitt));
        //@Umesh 20221017
        tvMTDTea.setText(mSaleTea + " KG");
        tvMTDMatch.setText(mSaleMatch + " BDL");
        tvTodayTc.setText(tc);
        tvTodayPc.setText(pc);
        tvTodayNc.setText(nc);
        //tvTodayTotalSale.setText(sale + "Kg");
        //@Umesh 20221021
        tvTodayTotalSaleTea.setText(saleTea + " KG");
        tvTodayTotalSaleMatch.setText(saleMatch + " BDL");


        LinearLayout.LayoutParams paramsH = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout llVerticalH = new LinearLayout(_context);
        llVerticalH.setLayoutParams(paramsH);
        llVerticalH.setOrientation(LinearLayout.VERTICAL);
        Log.d("TAG", "check 1");
        String text = "";

        text = text.concat("Date : *" + tvDate.getText().toString() + "*");
        text = text.concat("\n");
        text = text.concat("ASM/SO/TSI Name : *" + tvAsmTsiName.getText().toString() + "*");

        text = text.concat("\n");
        text = text.concat("Today :-");
        text = text.concat("\n");
        text = text.concat("Tc : " + tvTodayTc.getText().toString() + "  Pc : " + tvTodayPc.getText().toString()
                + "  Nc : " + tvTodayNc.getText().toString());
        text = text.concat("\n");
        text = text.concat("Total Sale Tea : *" + tvTodayTotalSaleTea.getText().toString() + "*");
        text = text.concat("\n");
        text = text.concat("Total Sale Match : *" + tvTodayTotalSaleMatch.getText().toString() + "*");
        text = text.concat("\n");


        for (int i = 0; i < marketName.size(); i++) {

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView tvMarkNameLabel = new TextView(_context);
            tvMarkNameLabel.setText("Party :");
            tvMarkNameLabel.setPadding(15, 15, 15, 15);
            tvMarkNameLabel.setTextColor(Color.BLACK);
            tvMarkNameLabel.setLayoutParams(paramsV);

            TextView tvMarkNameValue = new TextView(_context);
            tvMarkNameValue.setText(marketName.get(i));
            tvMarkNameValue.setPadding(15, 15, 15, 15);
            tvMarkNameValue.setTextColor(Color.parseColor("#424242"));
            tvMarkNameValue.setLayoutParams(paramsV);

            LinearLayout llPartyName = new LinearLayout(_context);
            llPartyName.setLayoutParams(paramsH);
            llPartyName.setOrientation(LinearLayout.HORIZONTAL);
            llPartyName.addView(tvMarkNameLabel);
            llPartyName.addView(tvMarkNameValue);

            llVerticalH.addView(llPartyName);


            TextView tvStationNameLabel = new TextView(_context);
            tvStationNameLabel.setText("Station :");
            tvStationNameLabel.setPadding(15, 15, 15, 15);
            tvStationNameLabel.setTextColor(Color.BLACK);
            tvStationNameLabel.setLayoutParams(paramsV);

            TextView tvStationNameValue = new TextView(_context);
            tvStationNameValue.setText(town.get(i));
            tvStationNameValue.setPadding(15, 15, 15, 15);
            tvStationNameValue.setTextColor(Color.parseColor("#424242"));
            tvStationNameValue.setLayoutParams(paramsV);

            LinearLayout llStationName = new LinearLayout(_context);
            llStationName.setLayoutParams(paramsH);
            llStationName.setOrientation(LinearLayout.HORIZONTAL);
            llStationName.addView(tvStationNameLabel);
            llStationName.addView(tvStationNameValue);

            llVerticalH.addView(llStationName);


            TextView tvBeatNameLabel = new TextView(_context);
            tvBeatNameLabel.setText("Beat Name :");
            tvBeatNameLabel.setPadding(15, 15, 15, 15);
            tvBeatNameLabel.setTextColor(Color.BLACK);
            tvBeatNameLabel.setLayoutParams(paramsV);
            tvBeatNameLabel.setVisibility(View.GONE); //@Umesh 20221004

            TextView tvBeatNameValue = new TextView(_context);
            tvBeatNameValue.setText(beatName.get(i));
            tvBeatNameValue.setPadding(15, 15, 15, 15);
            tvBeatNameValue.setTextColor(Color.parseColor("#424242"));
            tvBeatNameValue.setLayoutParams(paramsV);
            tvBeatNameValue.setVisibility(View.GONE); //@Umesh 20221004

            LinearLayout llBeatName = new LinearLayout(_context);
            llBeatName.setLayoutParams(paramsH);
            llBeatName.setOrientation(LinearLayout.HORIZONTAL);
            llBeatName.addView(tvBeatNameLabel);
            llBeatName.addView(tvBeatNameValue);

            llVerticalH.addView(llBeatName);

            TextView tvPartyContactPersonLabel = new TextView(_context);
            tvPartyContactPersonLabel.setText("Contact Person :");
            tvPartyContactPersonLabel.setPadding(15, 15, 15, 15);
            tvPartyContactPersonLabel.setTextColor(Color.BLACK);
            tvPartyContactPersonLabel.setLayoutParams(paramsV);

            TextView tvPartyContactPersonValue = new TextView(_context);
            tvPartyContactPersonValue.setText("");
            tvPartyContactPersonValue.setPadding(15, 15, 15, 15);
            tvPartyContactPersonValue.setTextColor(Color.parseColor("#424242"));
            tvPartyContactPersonValue.setLayoutParams(paramsV);

            LinearLayout llPartyContactPerson = new LinearLayout(_context);
            llPartyContactPerson.setLayoutParams(paramsH);
            llPartyContactPerson.setOrientation(LinearLayout.HORIZONTAL);
            llPartyContactPerson.addView(tvPartyContactPersonLabel);
            llPartyContactPerson.addView(tvPartyContactPersonValue);

            llVerticalH.addView(llPartyContactPerson);


            TextView tvMobileLabel = new TextView(_context);
            tvMobileLabel.setText("Mobile :");
            tvMobileLabel.setPadding(15, 15, 15, 15);
            tvMobileLabel.setTextColor(Color.BLACK);
            tvMarkNameLabel.setLayoutParams(paramsV);

            TextView tvMobileValue = new TextView(_context);
            tvMobileValue.setText(contact.get(i));
            tvMobileValue.setPadding(15, 15, 15, 15);
            tvMobileValue.setTextColor(Color.parseColor("#424242"));
            tvMarkNameValue.setLayoutParams(paramsV);

            LinearLayout llMobile = new LinearLayout(_context);
            llMobile.setLayoutParams(paramsH);
            llMobile.setOrientation(LinearLayout.HORIZONTAL);
            llMobile.addView(tvMobileLabel);
            llMobile.addView(tvMobileValue);

            llVerticalH.addView(llMobile);


            text = text.concat("\n");
            text = text.concat("Party Name : " + tvMarkNameValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Station Name : " + tvStationNameValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Beat Name : " + tvBeatNameValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Party Contact Person : " + tvPartyContactPersonValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Mobile : " + tvMobileValue.getText().toString());

            text = text.concat("\n");
            text = text.concat("\n");
            text = text.concat("-------------------------------------------------------------");
            text = text.concat("\n");
            text = text.concat("SKU" + "      " + "Opening" + "    " + "Secondary" + "    " + "Closing");
            text = text.concat("\n");
            text = text.concat("-------------------------------------------------------------");
            text = text.concat("\n");


            /*------------------------------------*/

            LinearLayout.LayoutParams paramsDivided = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            paramsDivided.weight = (float) 0.15;
            paramsDivided.gravity = Gravity.CENTER;

            LinearLayout.LayoutParams paramsDivided2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            paramsDivided2.weight = (float) 0.55;
            paramsDivided2.gravity = Gravity.CENTER;

            TextView tvSKuLabel = new TextView(_context);
            tvSKuLabel.setText("SKU");
            tvSKuLabel.setTextColor(Color.BLACK);
            tvSKuLabel.setPadding(15, 15, 15, 15);
            tvSKuLabel.setLayoutParams(paramsDivided2);

            TextView tvOpeningLabel = new TextView(_context);
            tvOpeningLabel.setText("OS");
            tvOpeningLabel.setPadding(15, 15, 15, 15);
            tvOpeningLabel.setTextColor(Color.BLACK);
            tvOpeningLabel.setLayoutParams(paramsDivided);

            TextView tvSecondaryLabel = new TextView(_context);
            tvSecondaryLabel.setText("SEC");
            tvSecondaryLabel.setPadding(15, 15, 15, 15);
            tvSecondaryLabel.setTextColor(Color.BLACK);
            tvSecondaryLabel.setLayoutParams(paramsDivided);

            TextView tvPrimaryLabel = new TextView(_context);
            tvPrimaryLabel.setText("CS");
            tvPrimaryLabel.setPadding(15, 15, 15, 15);
            tvPrimaryLabel.setTextColor(Color.BLACK);
            tvPrimaryLabel.setLayoutParams(paramsDivided);


            LinearLayout llHeader = new LinearLayout(_context);
            llHeader.setLayoutParams(paramsH);
            llHeader.setOrientation(LinearLayout.HORIZONTAL);
            llHeader.addView(tvSKuLabel);
            llHeader.addView(tvOpeningLabel);
            llHeader.addView(tvSecondaryLabel);
            llHeader.addView(tvPrimaryLabel);

            LinearLayout.LayoutParams paramsL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

            LinearLayout line1 = new LinearLayout(_context);
            line1.setBackgroundColor(Color.BLACK);
            line1.setLayoutParams(paramsL);

            LinearLayout line2 = new LinearLayout(_context);
            line2.setBackgroundColor(Color.BLACK);
            line2.setLayoutParams(paramsL);


            LinearLayout line3 = new LinearLayout(_context);
            line3.setBackgroundColor(Color.BLACK);
            line3.setLayoutParams(paramsL);


            LinearLayout line4 = new LinearLayout(_context);
            line4.setBackgroundColor(Color.BLACK);
            line4.setLayoutParams(paramsL);


            llVerticalH.addView(line1);
            llVerticalH.addView(llHeader);
            llVerticalH.addView(line2);

            ArrayList<SkuItem> stocks = stocksList2.get(i);
            Log.d("TAG", "stocks list: "+new Gson().toJson(stocks));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(_context);

            for (int index = 0; index < stocks.size(); index++) {

                SkuItem skuItem = stocks.get(index);
                Log.d("TAG", "model skuItem: "+new Gson().toJson(skuItem));
                if (!skuItem.getOpening().isEmpty() && !skuItem.getOpening().equalsIgnoreCase("0")
                        || !skuItem.getClosing().isEmpty() && !skuItem.getClosing().equalsIgnoreCase("0")
                        || !skuItem.getSecondary().isEmpty() && !skuItem.getSecondary().equalsIgnoreCase("0")) {
                    Log.d("TAG", "check SKU: "+skuItem.getSku());
                    TextView tvSKuValue = new TextView(_context);
                    tvSKuValue.setText(skuItem.getSku());
                    tvSKuValue.setPadding(15, 15, 15, 15);
                    tvSKuValue.setTextColor(Color.parseColor("#424242"));
                    tvSKuValue.setLayoutParams(paramsDivided2);

                    Log.d(TAG, "check getOpening: "+skuItem.getOpening());
                    TextView tvOpeningValue = new TextView(_context);
                    tvOpeningValue.setText(skuItem.getOpening());
                    tvOpeningValue.setPadding(15, 15, 15, 15);
                    tvOpeningValue.setTextColor(Color.parseColor("#424242"));
                    tvOpeningValue.setLayoutParams(paramsDivided);

                    Log.d(TAG, "check getSecondary: "+skuItem.getSecondary());
                    TextView tvSecondaryValue = new TextView(_context);
                    tvSecondaryValue.setText(skuItem.getSecondary());
                    tvSecondaryValue.setPadding(15, 15, 15, 15);
                    tvSecondaryValue.setTextColor(Color.parseColor("#424242"));
                    tvSecondaryValue.setLayoutParams(paramsDivided);

                    Log.d(TAG, "check getClosing: "+skuItem.getClosing());
                    TextView tvPrimaryValue = new TextView(_context);
                    tvPrimaryValue.setText(skuItem.getClosing());
                    tvPrimaryValue.setPadding(15, 15, 15, 15);
                    tvPrimaryValue.setTextColor(Color.parseColor("#424242"));
                    tvPrimaryValue.setLayoutParams(paramsDivided);


                    LinearLayout llRow = new LinearLayout(_context);
                    llRow.setLayoutParams(paramsH);
                    llRow.setOrientation(LinearLayout.HORIZONTAL);
                    llRow.addView(tvSKuValue);
                    llRow.addView(tvOpeningValue);
                    llRow.addView(tvSecondaryValue);
                    llRow.addView(tvPrimaryValue);


                    llVertical.addView(llRow);
                    llVertical.setLayoutParams(params);
                    llVertical.setOrientation(LinearLayout.VERTICAL);


                    text = text.concat(tvSKuValue.getText().toString() + "          " + tvOpeningValue.getText().toString() + "        "
                            + tvSecondaryValue.getText().toString() + "        " + tvPrimaryValue.getText().toString());
                    Log.d("TAG", "Set tvSKuValue: "+tvSKuValue.getText().toString());
                    Log.d("TAG", "Set tvOpeningValue: "+tvOpeningValue.getText().toString());
                    Log.d("TAG", "Set tvSecondaryValue: "+tvSecondaryValue.getText().toString());
                    Log.d("TAG", "Set tvPrimaryValue: "+tvPrimaryValue.getText().toString());
                    text = text.concat("\n");


                }
            }

            llVerticalH.addView(llVertical);

            TextView tvSKuTotal = new TextView(_context);
            tvSKuTotal.setText("Total = ");
            tvSKuTotal.setPadding(15, 15, 15, 15);
            tvSKuTotal.setTextColor(Color.parseColor("#424242"));
            tvSKuTotal.setLayoutParams(paramsDivided2);

            TextView tvOpeningTotal = new TextView(_context);
            tvOpeningTotal.setText(totalOpening.get(i));
            tvOpeningTotal.setPadding(15, 15, 15, 15);
            tvOpeningTotal.setTextColor(Color.parseColor("#424242"));
            tvOpeningTotal.setLayoutParams(paramsDivided);

            TextView tvSecondaryTotal = new TextView(_context);
            tvSecondaryTotal.setText(totalSecondary.get(i));
            tvSecondaryTotal.setPadding(15, 15, 15, 15);
            tvSecondaryTotal.setTextColor(Color.parseColor("#424242"));
            tvSecondaryTotal.setLayoutParams(paramsDivided);

            TextView tvPrimaryTotal = new TextView(_context);
            tvPrimaryTotal.setText(totalClosing.get(i));
            tvPrimaryTotal.setPadding(15, 15, 15, 15);
            tvPrimaryTotal.setTextColor(Color.parseColor("#424242"));
            tvPrimaryTotal.setLayoutParams(paramsDivided);



            LinearLayout llFooter = new LinearLayout(_context);
            llFooter.setLayoutParams(paramsH);
            llFooter.setOrientation(LinearLayout.HORIZONTAL);
            llFooter.addView(tvSKuTotal);
            llFooter.addView(tvOpeningTotal);
            llFooter.addView(tvSecondaryTotal);
            llFooter.addView(tvPrimaryTotal);

            llVerticalH.addView(line3);
            llVerticalH.addView(llFooter);
            llVerticalH.addView(line4);

            Log.d(TAG, "check tvOpeningTotal getText: "+tvOpeningTotal.getText().toString());
            Log.d(TAG, "check tvSecondaryTotal getText: "+tvSecondaryTotal.getText().toString());
            Log.d(TAG, "check tvPrimaryTotal getText: "+tvPrimaryTotal.getText().toString());
            text = text.concat("\n");
            text = text.concat("---------------------------------------------------------");
            text = text.concat("\n");
            text = text.concat("               " + "      " + tvOpeningTotal.getText().toString() + "      " + tvSecondaryTotal.getText().toString() + "      " + tvPrimaryTotal.getText().toString());
            text = text.concat("\n");
            text = text.concat("---------------------------------------------------------");
            text = text.concat("\n");

            if (i > 0) {
                text = text.concat("*****************************************************");
                text = text.concat("\n");
            }
        }


        container.addView(llVerticalH);

        text = text.concat("\n");
        text = text.concat("Check In" + "      " + "Check Out" + "    " + "First Call" + "    " + "Last Call");
        text = text.concat("\n");
        text = text.concat("---------------------------------------------------------");

        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("MTD(Sale Tea) = *" + tvMTDTea.getText().toString() + "*");
        text = text.concat("\n");
        text = text.concat("MTD(Sale Match) = *" + tvMTDMatch.getText().toString() + "*");
        text = text.concat("\n");
        text = text.concat("Tc : " + tvTC.getText().toString());
        text = text.concat("\n");
        text = text.concat("Pc : " + tvPC.getText().toString());
        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("--------------------------------------------------------");
        text = text.concat("\n");
        text = text.concat("\n");

        /*----------------------------Add New Distributor----------------------------*/
        Log.d(TAG, "newDistributorDetails size : "+newDistributorDetails.size());
        if (newDistributorDetails != null && newDistributorDetails.size() > 0) {
            Log.d("TAG", "check here");

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(_context);
            llVertical.setLayoutParams(params);
            llVertical.setOrientation(LinearLayout.VERTICAL);

            TextView tvSKuValue = new TextView(_context);
            tvSKuValue.setText("***** NEW DISTRIBUTOR ADDED ******");
            tvSKuValue.setPadding(15, 15, 15, 15);
            tvSKuValue.setTextColor(Color.parseColor("#424242"));
            tvSKuValue.setLayoutParams(paramsV);

            llVertical.addView(tvSKuValue);

            text = text.concat("\n");
            text = text.concat("*New Distributor Added*");
            text = text.concat("\n");
            text = text.concat("\n");

            for (int i = 0; i < newDistributorDetails.size(); i++) {
                Log.d("TAG", "check again");
                TextView tvDistributorName = new TextView(_context);
                tvDistributorName.setText("Distributor Name: " + newDistributorDetails.get(i).getFirmName());
                tvDistributorName.setPadding(15, 15, 15, 15);
                tvDistributorName.setTextColor(Color.parseColor("#424242"));
                tvDistributorName.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorName);

                TextView tvDistributorAddress = new TextView(_context);
                tvDistributorAddress.setText("Distributor Address: " + newDistributorDetails.get(i).getFirmAddress());
                tvDistributorAddress.setPadding(15, 15, 15, 15);
                tvDistributorAddress.setTextColor(Color.parseColor("#424242"));
                tvDistributorAddress.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorAddress);


                TextView tvDistributorCity = new TextView(_context);
                tvDistributorCity.setText("City: " + newDistributorDetails.get(i).getCity());
                tvDistributorCity.setPadding(15, 15, 15, 15);
                tvDistributorCity.setTextColor(Color.parseColor("#424242"));
                tvDistributorCity.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorCity);


                TextView tvDistributorState = new TextView(_context);
                tvDistributorState.setText("State: " + newDistributorDetails.get(i).getState());
                tvDistributorState.setPadding(15, 15, 15, 15);
                tvDistributorState.setTextColor(Color.parseColor("#424242"));
                tvDistributorState.setLayoutParams(paramsV);

                llVertical.addView(tvDistributorState);


                TextView tvOwnerName = new TextView(_context);
                tvOwnerName.setText("Owner Name: " + newDistributorDetails.get(i).getOwnerName());
                tvOwnerName.setPadding(15, 15, 15, 15);
                tvOwnerName.setTextColor(Color.parseColor("#424242"));
                tvOwnerName.setLayoutParams(paramsV);

                llVertical.addView(tvOwnerName);


                TextView tvOwnerMobile = new TextView(_context);
                tvOwnerMobile.setText("Owner Mobile No.: " + newDistributorDetails.get(i).getMobile1());
                tvOwnerMobile.setPadding(15, 15, 15, 15);
                tvOwnerMobile.setTextColor(Color.parseColor("#424242"));
                tvOwnerMobile.setLayoutParams(paramsV);

                llVertical.addView(tvOwnerMobile);


                TextView tvOpnionDis = new TextView(_context);
                tvOpnionDis.setText("Opinion About Distributor: " + newDistributorDetails.get(i).getOpDis());
                tvOpnionDis.setPadding(15, 15, 15, 15);
                tvOpnionDis.setTextColor(Color.parseColor("#424242"));
                tvOpnionDis.setLayoutParams(paramsV);

                llVertical.addView(tvOpnionDis);


                TextView tvRemarks = new TextView(_context);
                tvRemarks.setText("Remarks: " + newDistributorDetails.get(i).getRemarks());
                tvRemarks.setPadding(15, 15, 15, 15);
                tvRemarks.setTextColor(Color.parseColor("#424242"));
                tvRemarks.setLayoutParams(paramsV);

                llVertical.addView(tvRemarks);


                TextView tvLine = new TextView(_context);
                tvLine.setText("--------------------------------------");
                tvLine.setPadding(15, 15, 15, 15);
                tvLine.setTextColor(Color.parseColor("#424242"));
                tvLine.setLayoutParams(paramsV);

                llVertical.addView(tvLine);


                text = text.concat("Distributor Name : " + newDistributorDetails.get(i).getFirmName());
                text = text.concat("\n");
                text = text.concat("Distributor Address : " + newDistributorDetails.get(i).getFirmAddress());
                text = text.concat("\n");
                text = text.concat("City : " + newDistributorDetails.get(i).getCity());
                text = text.concat("\n");
                text = text.concat("State : " + newDistributorDetails.get(i).getState());
                text = text.concat("\n");
                text = text.concat("Owner Name : " + newDistributorDetails.get(i).getOwnerName());
                text = text.concat("\n");
                text = text.concat("Owner Mobile No.: " + newDistributorDetails.get(i).getMobile1());
                text = text.concat("\n");
                text = text.concat("Product Division : " + newDistributorDetails.get(i).getProduct());
                text = text.concat("\n");
                text = text.concat("Opinion About Distributor : " + newDistributorDetails.get(i).getOpDis());
                text = text.concat("\n");
                text = text.concat("Comments : " + newDistributorDetails.get(i).getRemarks());
                text = text.concat("\n");
                text = text.concat("\n");
                text = text.concat("\n");
            }

            container2.addView(llVertical);

        }

        final String finalText = text;
        Log.d("TAG", "finalText: "+finalText);
        btnShareTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "click share text");
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Today Summary");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, finalText);
                _context.startActivity(Intent.createChooser(sharingIntent, "Share summary"));
                dialog.dismiss();
            }
        });


        btnShareImg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View view) {
                Log.d("TAG", "click share img");
                if (Environment.isExternalStorageManager()) {
                    Log.d("TAG", "check-1");
                    Bitmap bitmap = takeScreenshot(view.getRootView());
                    saveBitmap(bitmap);
                    shareIt(tvAsmTsiName.getText().toString());
                    dialog.dismiss();
                } else {
                    Log.d("TAG", "check-");
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", view.getContext().getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });

        dialog.show();

    }


    /*private Bitmap takeScreenshot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }*/

    private Bitmap takeScreenshot(View view) {
        // Original bitmap
        Bitmap originalBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a new bitmap with increased size
        int increasedWidth = originalBitmap.getWidth() * 2; // Increase width by a factor of 2
        int increasedHeight = originalBitmap.getHeight() * 2; // Increase height by a factor of 2
        Bitmap increasedBitmap = Bitmap.createBitmap(increasedWidth, increasedHeight, Bitmap.Config.ARGB_8888);

        // Create a canvas with the increased bitmap
        Canvas canvas = new Canvas(increasedBitmap);

        // Make sure the view is drawn before capturing the bitmap
        view.measure(View.MeasureSpec.makeMeasureSpec(increasedWidth, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(increasedHeight, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, increasedWidth, increasedHeight);
        view.draw(canvas);

        // Now, the increasedBitmap contains the original bitmap drawn at a larger size
        return increasedBitmap;
    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + File.separator + "screenshot.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
    }

    private void shareIt(String EmpName) {
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        //sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.setType("image/*");
        //String shareBody = "In Tweecher, My highest score with screen shot";
        String shareBody = EmpName;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Today Summary");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
