package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.PCOrderHistory;
import com.newsalesbeatApp.activities.WorkDetails;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.pojo.SkuItem;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * Created by MTC on 18-07-2017.
 */
public class CustomCalendarAdapter extends BaseAdapter {

    private Context mContext;
    private Calendar calendar;
    private GregorianCalendar gregorianCalendar;
    private DateFormat df;
    private HashMap<String, String> empAttendanceStatusList;
    private List<String> dayString;
    private int firstDay;
    private String curentDateString;
    private SharedPreferences myPref;
    private UtilityClass utilityClass;
    //private Cursor cursor;

    private ArrayList<String> marketName = new ArrayList<>();
    private ArrayList<String> town = new ArrayList<>();
    private ArrayList<String> contact = new ArrayList<>();
    private ArrayList<String> totalOpening = new ArrayList<>();
    private ArrayList<String> totalSecondary = new ArrayList<>();
    private ArrayList<String> totalClosing = new ArrayList<>();
    private ArrayList<ArrayList<SkuItem>> stocksList2 = new ArrayList<>();

    private int[] EXTRA_DAYS = {0, 6, 0, 1, 2, 3, 4, 5};

    //constructer
    public CustomCalendarAdapter(Context c, Calendar monthCalendar, String currentDate,
                                 int monthDiffernce, HashMap<String, String> empAttendanceStatusList) {
        mContext = c;

        utilityClass = new UtilityClass(mContext);

        myPref = c.getSharedPreferences(c.getString(R.string.pref_name), Context.MODE_PRIVATE);

        this.empAttendanceStatusList = empAttendanceStatusList;

        df = new SimpleDateFormat("dd-MM-yyyy");

        String[] separatedTime1 = currentDate.split("-");
        String dy = separatedTime1[0].replaceFirst("^0*", "");
        String mn = separatedTime1[1].replaceFirst("^0*", "");
        String yr = separatedTime1[2].replaceFirst("^0*", "");

        dayString = new ArrayList<>();
        calendar = monthCalendar;
        calendar.set(Integer.parseInt(yr), Integer.parseInt(mn) - 1, Integer.parseInt(dy));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + monthDiffernce);

        //current date string
        Calendar cc = Calendar.getInstance();
        curentDateString = df.format(cc.getTime());

        refreshDays();

    }

    public int getCount() {
        return dayString.size();
    }

    public Object getItem(int position) {

        return dayString.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ResourceAsColor")
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (vi != null)
                v = vi.inflate(R.layout.custom_calender_cell, null);
        }


        final TextView dayView = (TextView) v.findViewById(R.id.date);

        String[] separatedTime = dayString.get(position).split("-");
        int day = Integer.parseInt(separatedTime[0].replaceFirst("^0*", ""));
        int month = Integer.parseInt(separatedTime[1].replaceFirst("^0*", ""));
        int year = Integer.parseInt(separatedTime[2].replaceFirst("^0*", ""));

        String[] separatedTime1 = curentDateString.split("-");
        int currentDay = Integer.parseInt(separatedTime1[0].replaceFirst("^0*", ""));
        int currentMonth = Integer.parseInt(separatedTime1[1].replaceFirst("^0*", ""));
        int currentYear = Integer.parseInt(separatedTime1[2].replaceFirst("^0*", ""));

        //Resetting dayview textview
        dayView.setBackgroundColor(Color.TRANSPARENT);
        dayView.setTextColor(Color.parseColor("#424242"));
        dayView.setEnabled(true);

        // checking whether the day is in current month or not.
        if ((day > 1) && (position < firstDay)) {

            // setting offdays  color.
            dayView.setTextColor(Color.GRAY);
            dayView.setVisibility(View.INVISIBLE);
            dayView.setEnabled(true);

        } else if ((day < 7) && (position > 28)) {

            dayView.setTextColor(Color.GRAY);
            dayView.setVisibility(View.INVISIBLE);
            dayView.setEnabled(true);

        } else if (currentMonth > month || currentYear > year) {

            //setting past days color
            //dayView.setTextColor(Color.WHITE);
            dayView.setEnabled(true);
            dayView.setVisibility(View.VISIBLE);

            if (empAttendanceStatusList != null && empAttendanceStatusList.size() > 0) {

                Iterator myVeryOwnIterator = empAttendanceStatusList.keySet().iterator();

                while (myVeryOwnIterator.hasNext()) {

                    String key = (String) myVeryOwnIterator.next();
                    String value = empAttendanceStatusList.get(key);

                    String[] tempDateArr2 = key.split("-");
                    int markedDay = Integer.parseInt(tempDateArr2[2]);
                    int markedMonth = Integer.parseInt(tempDateArr2[1]);
                    //int markedYear = Integer.parseInt(tempDateArr2[0]);

                    if ((markedDay == day) && (markedMonth == month)
                            /* && (markedYear == 2017)*/ && (value.equalsIgnoreCase("present"))) {
                        dayView.setTextColor(Color.WHITE);
                        dayView.setBackgroundResource(R.drawable.green_circle);

                    } else if ((markedDay == day) && (markedMonth == month)
                            /*&& (markedYear == 2017)*/ && (value.equalsIgnoreCase("leave"))) {
                        dayView.setTextColor(Color.WHITE);
                        dayView.setBackgroundResource(R.drawable.red_circle);

                    } else if ((markedDay == day) && (markedMonth == month)
                            /*&& (markedYear == 2017)*/ && (value.equalsIgnoreCase("absent"))) {
                        dayView.setTextColor(Color.WHITE);
                        dayView.setBackgroundResource(R.drawable.red_circle);

                    }

                }

            }

        } else if (currentDay > day && currentMonth == month) {

            //setting past days color
            //dayView.setTextColor(Color.WHITE);
            dayView.setVisibility(View.VISIBLE);
            dayView.setEnabled(true);

            if (empAttendanceStatusList != null && empAttendanceStatusList.size() > 0) {

                Iterator myVeryOwnIterator = empAttendanceStatusList.keySet().iterator();

                while (myVeryOwnIterator.hasNext()) {

                    String key = (String) myVeryOwnIterator.next();
                    String value = empAttendanceStatusList.get(key);

                    String[] tempDateArr2 = key.split("-");
                    int markedDay = Integer.parseInt(tempDateArr2[2]);
                    int markedMonth = Integer.parseInt(tempDateArr2[1]);
                    int markedYear = Integer.parseInt(tempDateArr2[0]);

                    if ((markedDay == day) && (markedMonth == month)
                            /*&& (markedYear == 2017)*/ && (value.equalsIgnoreCase("present"))) {
                        dayView.setTextColor(Color.WHITE);
                        dayView.setBackgroundResource(R.drawable.green_circle);

                    } else if ((markedDay == day) && (markedMonth == month)
                            /*&& (markedYear == 2017)*/ && (value.equalsIgnoreCase("leave"))) {
                        dayView.setTextColor(Color.WHITE);
                        dayView.setBackgroundResource(R.drawable.red_circle);

                    } else if ((markedDay == day) && (markedMonth == month)
                            /*&& (markedYear == 2017)*/ && (value.equalsIgnoreCase("absent"))) {
                        dayView.setTextColor(Color.WHITE);
                        dayView.setBackgroundResource(R.drawable.red_circle);

                    }

                }

            }


        } else {

            // setting curent month's days in  color.
            //dayView.setTextColor(Color.WHITE);
            dayView.setVisibility(View.VISIBLE);
            dayView.setEnabled(true);

            //marking current date
            if ((day == currentDay) && month == currentMonth) {
                dayView.setBackgroundResource(R.drawable.circle_whitish);
                dayView.setTextColor(R.color.red_like);

            }
        }

        //filling month value
        dayView.setText(String.valueOf(day));

        dayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    String[] separatedTime = dayString.get(position).split("-");
                    String day = separatedTime[0];
                    String month = separatedTime[1];
                    String year = separatedTime[2];
                    String date = year + "-" + month + "-" + day;
                    getEmployeeRecordByDate(date);

                } else {
                    Toast.makeText(mContext, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        refreshDays();

        return v;
    }

    private void getEmployeeRecordByDate(final String date) {
        Log.e("TAG", "getEmployeeRecordByDate: "+date);
        final Dialog loader = new Dialog(mContext, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_RECORD_BY_DATE + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", "EMP_RECORD_BY_DATE=====" + response);
                loader.dismiss();

                String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
                        status = "", totalWorkingTime = "", totalRetailingTime = "", reason = "",
                        target = "", achievement = "", beatAssigned = "", beatVisited = "", sale = "";

                try {
                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {
                        response = response.getJSONObject("data");
                        if (response.has("attendance") && !response.isNull("attendance"))
                            status = response.getString("attendance");

                        if (response.has("checkIn") && !response.isNull("checkIn"))
                            checkInTime = response.getString("checkIn");

                        if (response.has("checkOut") && !response.isNull("checkOut"))
                            checkOutTime = response.getString("checkOut");

                        if (response.has("totalCalls") && !response.isNull("totalCalls"))
                            totalCall = response.getString("totalCalls");

                        if (response.has("productiveCalls") && !response.isNull("productiveCalls"))
                            productiveCall = response.getString("productiveCalls");

                        if (response.has("linesSold") && !response.isNull("linesSold"))
                            lineSold = response.getString("linesSold");

                        if (response.has("totalWorkingTime") && !response.isNull("totalWorkingTime"))
                            totalWorkingTime = response.getString("totalWorkingTime");

                        if (response.has("totalRetailingTime") && !response.isNull("totalRetailingTime"))
                            totalRetailingTime = response.getString("totalRetailingTime");

                        if (response.has("reason") && !response.isNull("reason"))
                            reason = response.getString("reason");

                        if (response.has("target") && !response.isNull("target"))
                            target = response.getString("target");

                        if (response.has("sale") && !response.isNull("sale"))
                            sale = response.getString("sale");

                        if (response.has("achievement") && !response.isNull("achievement"))
                            achievement = response.getString("achievement");

                        if (response.has("beatAssignedCount") && !response.isNull("beatAssignedCount"))
                            beatAssigned = response.getString("beatAssignedCount");

                        if (response.has("beatVisitedCount") && !response.isNull("beatVisitedCount"))
                            beatVisited = response.getString("beatVisitedCount");

                        final List<RetailerItem> retailerItemList = new ArrayList<>();
                        if (response.has("tcDetail") && !response.isNull("tcDetail")) {


                            JSONArray tcDetails = response.getJSONArray("tcDetail");
                            for (int t = 0; t < tcDetails.length(); t++) {
                                RetailerItem retailerItem = new RetailerItem();
                                JSONObject tcObj = (JSONObject) tcDetails.get(t);
                                retailerItem.setRetailerName(tcObj.getString("name"));
                                retailerItem.setRetailer_pin(tcObj.getString("checkIn"));
                                retailerItem.setRetailerPhone(tcObj.getString("checkOut"));

                                retailerItemList.add(retailerItem);
                            }
                        }

                        final List<SkuItem> skuItemList = new ArrayList<>();
                        if (response.has("linedSoldDetail") && !response.isNull("linedSoldDetail")) {
                            JSONArray lineSoldArr = response.getJSONArray("linedSoldDetail");

                            for (int l = 0; l < lineSoldArr.length(); l++) {
                                SkuItem skuItem = new SkuItem();
                                JSONArray skuArr = (JSONArray) lineSoldArr.get(l);
                                String item = skuArr.getString(0);
                                String unit = skuArr.getString(1);
                                String qty = skuArr.getString(2);

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
                                beatList.add((String) beatVArr.get(b));
                            }
                        }

                        final ArrayList<String> beatList2 = new ArrayList<>();
                        if (response.has("beatAssigned") && !response.isNull("beatAssigned")) {
                            JSONArray beatAArr = response.getJSONArray("beatAssigned");

                            for (int b1 = 0; b1 < beatAArr.length(); b1++) {
                                beatList2.add((String) beatAArr.get(b1));
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


                        final Dialog dialog = new Dialog(mContext, R.style.DialogActivityTheme);
                        dialog.setContentView(R.layout.dailyassesment_dailog);
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        final TextView tvDateView = (TextView) dialog.findViewById(R.id.tvDateView);
                        TextView tvStatus = (TextView) dialog.findViewById(R.id.tvStatus);
                        TextView tvCheckInTime = (TextView) dialog.findViewById(R.id.tvCheckInTimeD);
                        TextView tvCheckOutTime = (TextView) dialog.findViewById(R.id.tvCheckOutTimeD);
                        final TextView tvTotalCall = (TextView) dialog.findViewById(R.id.tvTotalCall);
                        final TextView tvProductiveCall = (TextView) dialog.findViewById(R.id.tvProductiveCall);
                        TextView tvLineSold = (TextView) dialog.findViewById(R.id.tvLineSold);
                        TextView tvReason = (TextView) dialog.findViewById(R.id.tvReason);
                        TextView tvTarget = (TextView) dialog.findViewById(R.id.tvTarget);
                        TextView tvAchievement = (TextView) dialog.findViewById(R.id.tvAchievement);
                        TextView tvBeatAssigned = (TextView) dialog.findViewById(R.id.tvBeatAssigned);
                        TextView tvBeatVisited = (TextView) dialog.findViewById(R.id.tvBeatVisited);
                        TextView tvTotalRetailingHour = (TextView) dialog.findViewById(R.id.tvTRT);
                        TextView tvTotalWorkingHour = (TextView) dialog.findViewById(R.id.tvTWH);
                        TextView tvSale = (TextView) dialog.findViewById(R.id.tvSale);
                        TextView tvFullDayActivity = (TextView) dialog.findViewById(R.id.tvFullDayActivity);
                        ImageView imgForward = (ImageView) dialog.findViewById(R.id.imgForward);
                        ImageView imgForwardL = (ImageView) dialog.findViewById(R.id.imgForwardL);
                        ImageView imgForwardT = (ImageView) dialog.findViewById(R.id.imgForwardT);
                        ImageView imgForwardBA = (ImageView) dialog.findViewById(R.id.imgForwardBA);
                        ImageView imgForwardBV = (ImageView) dialog.findViewById(R.id.imgForwardBV);
                        ImageView imgForwardOA = (ImageView) dialog.findViewById(R.id.imgForwardOA);

                        LinearLayout llOk = (LinearLayout) dialog.findViewById(R.id.llCancel);
                        LinearLayout llShowSummary = (LinearLayout) dialog.findViewById(R.id.llShowSummary);

                        tvDateView.setText(date);

                        if (status != null && !status.isEmpty())
                            tvStatus.setText(status);

                        if (checkInTime != null && !checkInTime.isEmpty())
                            tvCheckInTime.setText(checkInTime);

                        if (checkOutTime != null && !checkOutTime.isEmpty())
                            tvCheckOutTime.setText(checkOutTime);

                        if (totalCall != null && !totalCall.isEmpty())
                            tvTotalCall.setText(totalCall);

                        if (productiveCall != null && !productiveCall.isEmpty())
                            tvProductiveCall.setText(productiveCall);

                        if (lineSold != null && !lineSold.isEmpty())
                            tvLineSold.setText(lineSold);

                        if (reason != null && !reason.isEmpty())
                            tvReason.setText(reason);

                        tvTarget.setText(target);
                        tvAchievement.setText(achievement);
                        tvBeatAssigned.setText(beatAssigned);
                        tvBeatVisited.setText(beatVisited);
                        tvSale.setText(sale);

                        if (totalRetailingTime != null && !totalRetailingTime.isEmpty())
                            tvTotalRetailingHour.setText(totalRetailingTime);

                        if (totalWorkingTime != null && !totalWorkingTime.isEmpty())
                            tvTotalWorkingHour.setText(totalWorkingTime);

                        tvFullDayActivity.setText(fullDayActivity);

                        imgForward.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (tvProductiveCall.getText().toString().equalsIgnoreCase("0")
                                        || tvProductiveCall.getText().toString().isEmpty()) {
                                    Toast.makeText(mContext, "No record available", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(mContext, PCOrderHistory.class);
                                    intent.putExtra("date", tvDateView.getText().toString());
                                    mContext.startActivity(intent);
                                }

                            }
                        });

                        imgForwardL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(mContext, WorkDetails.class);
                                intent.putExtra("type", "L");
                                intent.putExtra("list", (Serializable) skuItemList);
                                mContext.startActivity(intent);
                            }
                        });

                        imgForwardT.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(mContext, WorkDetails.class);
                                intent.putExtra("type", "T");
                                intent.putExtra("list", (Serializable) retailerItemList);
                                mContext.startActivity(intent);
                            }
                        });

                        imgForwardBA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mContext, WorkDetails.class);
                                intent.putExtra("type", "BA");
                                intent.putStringArrayListExtra("list", beatList2);
                                mContext.startActivity(intent);
                            }
                        });

                        imgForwardBV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("BEAT", "==" + beatList2.size());
                                Intent intent = new Intent(mContext, WorkDetails.class);
                                intent.putExtra("type", "BV");
                                intent.putStringArrayListExtra("list", beatList);
                                mContext.startActivity(intent);
                            }
                        });

                        imgForwardOA.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.e("BEAT", "==" + beatList2.size());
                                Intent intent = new Intent(mContext, WorkDetails.class);
                                intent.putExtra("type", "OA");
                                intent.putExtra("list", (Serializable) fullDayActivityList);
                                mContext.startActivity(intent);
                            }
                        });

                        llOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        llShowSummary.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showSummary(date);
                            }
                        });


                        if (status != null && !status.isEmpty())
                            dialog.show();
                        else
                            Toast.makeText(mContext, "No record found", Toast.LENGTH_SHORT).show();
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

                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;

                        responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.e("ERRR", "===== " + responseBody);

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
                return headers;
            }
        };

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(mContext).add(objectRequest);
    }

    private void showSummary(String date) {
        final Dialog loader = new Dialog(mContext, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();


        StringRequest postRequest = new StringRequest(Request.Method.GET, SbAppConstants.API_GET_DAILY_SUMMARY + "?date=" + date,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        loader.dismiss();

                        Log.e("Response", "Daily summary====" + response);
                        try {

                            stocksList2.clear();
                            marketName.clear();
                            town.clear();
                            contact.clear();
                            totalOpening.clear();
                            totalSecondary.clear();
                            totalClosing.clear();

                            JSONObject data = new JSONObject(response);
                            String date = data.getString("date");
                            String name = data.getString("name");
                            String tc = data.getString("tc");
                            String pc = data.getString("pc");
                            String sale = data.getString("sale");
                            String mtc = data.getString("mtc");
                            String mpc = data.getString("mpc");
                            String mSale = data.getString("msale");

                            JSONArray distributors = data.getJSONArray("distributors");
                            for (int index = 0; index < distributors.length(); index++) {

                                JSONObject object = (JSONObject) distributors.get(index);
                                marketName.add(object.getString("name"));
                                town.add(object.getString("town"));
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

                                stocksList2.add(stocksList);
                            }

                            String status = data.getString("status");

                            if (status.equalsIgnoreCase("success")) {

                                showDailySummaryDailog(date, name, tc, pc, sale, mtc, mpc, mSale,
                                        marketName, town, contact, totalOpening, totalSecondary, totalClosing, stocksList2);

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
                        try {
                            if (error.networkResponse.statusCode == 422) {
                                String responseBody = null;
                                try {
                                    responseBody = new String(error.networkResponse.data, "utf-8");
                                    Log.e("ERRR", "===== " + responseBody);
                                    JSONObject object = new JSONObject(responseBody);
                                    String message = object.getString("message");
                                    JSONObject errorr = object.getJSONObject("errors");

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(mContext).add(postRequest);

    }


    private void showDailySummaryDailog(String date, String name, String tc, String pc, String sale, String mtc,
                                        String mpc, String mSale, ArrayList<String> marketName, ArrayList<String> town,
                                        ArrayList<String> contact, ArrayList<String> totalOpening,
                                        ArrayList<String> totalSecondary, ArrayList<String> totalClosing,
                                        ArrayList<ArrayList<SkuItem>> stocksList2) {

        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.employee_summary_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final TextView tvDate = (TextView) dialog.findViewById(R.id.tvDate);
        final TextView tvAsmTsiName = (TextView) dialog.findViewById(R.id.tvAsmTsiName);
        final TextView tvTodayTc = (TextView) dialog.findViewById(R.id.tvTodayTc);
        final TextView tvTodayPc = (TextView) dialog.findViewById(R.id.tvTodayPc);
        final TextView tvTodayTotalSale = (TextView) dialog.findViewById(R.id.tvTodayTotalSale);
        final TextView tvMTD = (TextView) dialog.findViewById(R.id.tvMTD);
        final TextView tvTC = (TextView) dialog.findViewById(R.id.tvTC);
        final TextView tvPC = (TextView) dialog.findViewById(R.id.tvPC);
        Button btnShare = (Button) dialog.findViewById(R.id.btnShare);
        LinearLayout container = (LinearLayout) dialog.findViewById(R.id.container);


        tvDate.setText(date);
        tvAsmTsiName.setText(name);
        tvTC.setText(mtc);
        tvPC.setText(mpc);
        tvMTD.setText(mSale);
        tvTodayTc.setText(tc);
        tvTodayPc.setText(pc);
        tvTodayTotalSale.setText(sale + "Kg");


        LinearLayout.LayoutParams paramsH = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout llVerticalH = new LinearLayout(mContext);
        llVerticalH.setLayoutParams(paramsH);
        llVerticalH.setOrientation(LinearLayout.VERTICAL);

        String text = "";

        text = text.concat("Date-02 : " + tvDate.getText().toString());
        text = text.concat("\n");
        text = text.concat("ASM/SO/TSI Name : " + tvAsmTsiName.getText().toString());

        text = text.concat("\n");
        text = text.concat("Today :-  Tc : " + tvTodayTc.getText().toString() + "  Pc : " + tvTodayPc.getText().toString() +
                "  Total Sale : " + tvTodayTotalSale.getText().toString());
        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("\n");


        for (int i = 0; i < marketName.size(); i++) {

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView tvMarkNameLabel = new TextView(mContext);
            tvMarkNameLabel.setText("Party Name :");
            tvMarkNameLabel.setPadding(15, 15, 15, 15);
            tvMarkNameLabel.setTextColor(Color.GRAY);
            tvMarkNameLabel.setLayoutParams(paramsV);

            TextView tvMarkNameValue = new TextView(mContext);
            tvMarkNameValue.setText(marketName.get(i));
            tvMarkNameValue.setPadding(15, 15, 15, 15);
            tvMarkNameValue.setTextColor(Color.parseColor("#424242"));
            tvMarkNameValue.setLayoutParams(paramsV);

            LinearLayout llPartyName = new LinearLayout(mContext);
            llPartyName.setLayoutParams(paramsH);
            llPartyName.setOrientation(LinearLayout.HORIZONTAL);
            llPartyName.addView(tvMarkNameLabel);
            llPartyName.addView(tvMarkNameValue);

            llVerticalH.addView(llPartyName);


            TextView tvStationNameLabel = new TextView(mContext);
            tvStationNameLabel.setText("Station Name :");
            tvStationNameLabel.setPadding(15, 15, 15, 15);
            tvStationNameLabel.setTextColor(Color.GRAY);
            tvStationNameLabel.setLayoutParams(paramsV);

            TextView tvStationNameValue = new TextView(mContext);
            tvStationNameValue.setText(town.get(i));
            tvStationNameValue.setPadding(15, 15, 15, 15);
            tvStationNameValue.setTextColor(Color.parseColor("#424242"));
            tvStationNameValue.setLayoutParams(paramsV);

            LinearLayout llStationName = new LinearLayout(mContext);
            llStationName.setLayoutParams(paramsH);
            llStationName.setOrientation(LinearLayout.HORIZONTAL);
            llStationName.addView(tvStationNameLabel);
            llStationName.addView(tvStationNameValue);

            llVerticalH.addView(llStationName);


            TextView tvPartyContactPersonLabel = new TextView(mContext);
            tvPartyContactPersonLabel.setText("Party Contact Person :");
            tvPartyContactPersonLabel.setPadding(15, 15, 15, 15);
            tvPartyContactPersonLabel.setTextColor(Color.GRAY);
            tvPartyContactPersonLabel.setLayoutParams(paramsV);

            TextView tvPartyContactPersonValue = new TextView(mContext);
            tvPartyContactPersonValue.setText("");
            tvPartyContactPersonValue.setPadding(15, 15, 15, 15);
            tvPartyContactPersonValue.setTextColor(Color.parseColor("#424242"));
            tvPartyContactPersonValue.setLayoutParams(paramsV);

            LinearLayout llPartyContactPerson = new LinearLayout(mContext);
            llPartyContactPerson.setLayoutParams(paramsH);
            llPartyContactPerson.setOrientation(LinearLayout.HORIZONTAL);
            llPartyContactPerson.addView(tvPartyContactPersonLabel);
            llPartyContactPerson.addView(tvPartyContactPersonValue);

            llVerticalH.addView(llPartyContactPerson);


            TextView tvMobileLabel = new TextView(mContext);
            tvMobileLabel.setText("Mobile :");
            tvMobileLabel.setPadding(15, 15, 15, 15);
            tvMobileLabel.setTextColor(Color.GRAY);
            tvMarkNameLabel.setLayoutParams(paramsV);

            TextView tvMobileValue = new TextView(mContext);
            tvMobileValue.setText(contact.get(i));
            tvMobileValue.setPadding(15, 15, 15, 15);
            tvMobileValue.setTextColor(Color.parseColor("#424242"));
            tvMarkNameValue.setLayoutParams(paramsV);

            LinearLayout llMobile = new LinearLayout(mContext);
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
            text = text.concat("Party Contact Person : " + tvPartyContactPersonValue.getText().toString());
            text = text.concat("\n");
            text = text.concat("Mobile  : " + tvMobileValue.getText().toString());

            text = text.concat("\n");
            text = text.concat("\n");
            text = text.concat("-------------------------------------------------------------");
            text = text.concat("\n");
            text = text.concat("SKU" + "      " + "Opening" + "    " + "Secondary" + "    " + "Closing");
            text = text.concat("\n");
            text = text.concat("-------------------------------------------------------------");
            text = text.concat("\n");


            /*------------------------------------*/

            TextView tvSKuLabel = new TextView(mContext);
            tvSKuLabel.setText("SKU");
            tvSKuLabel.setTextColor(Color.GRAY);
            tvSKuLabel.setPadding(15, 15, 15, 15);
            tvSKuLabel.setLayoutParams(paramsV);

            TextView tvOpeningLabel = new TextView(mContext);
            tvOpeningLabel.setText("OPENING");
            tvOpeningLabel.setPadding(15, 15, 15, 15);
            tvOpeningLabel.setTextColor(Color.GRAY);
            tvOpeningLabel.setLayoutParams(paramsV);

            TextView tvSecondaryLabel = new TextView(mContext);
            tvSecondaryLabel.setText("SECONDARY");
            tvSecondaryLabel.setPadding(15, 15, 15, 15);
            tvSecondaryLabel.setTextColor(Color.GRAY);
            tvSecondaryLabel.setLayoutParams(paramsV);

            TextView tvPrimaryLabel = new TextView(mContext);
            tvPrimaryLabel.setText("CLOSING");
            tvPrimaryLabel.setPadding(15, 15, 15, 15);
            tvPrimaryLabel.setTextColor(Color.GRAY);
            tvPrimaryLabel.setLayoutParams(paramsV);


            LinearLayout llHeader = new LinearLayout(mContext);
            llHeader.setLayoutParams(paramsH);
            llHeader.setOrientation(LinearLayout.HORIZONTAL);
            llHeader.addView(tvSKuLabel);
            llHeader.addView(tvOpeningLabel);
            llHeader.addView(tvSecondaryLabel);
            llHeader.addView(tvPrimaryLabel);

            LinearLayout.LayoutParams paramsL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

            LinearLayout line1 = new LinearLayout(mContext);
            line1.setBackgroundColor(Color.GRAY);
            line1.setLayoutParams(paramsL);

            LinearLayout line2 = new LinearLayout(mContext);
            line2.setBackgroundColor(Color.GRAY);
            line2.setLayoutParams(paramsL);


            LinearLayout line3 = new LinearLayout(mContext);
            line3.setBackgroundColor(Color.GRAY);
            line3.setLayoutParams(paramsL);


            LinearLayout line4 = new LinearLayout(mContext);
            line4.setBackgroundColor(Color.GRAY);
            line4.setLayoutParams(paramsL);


            llVerticalH.addView(line1);
            llVerticalH.addView(llHeader);
            llVerticalH.addView(line2);

            ArrayList<SkuItem> stocks = stocksList2.get(i);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(mContext);

            for (int index = 0; index < stocks.size(); index++) {

                SkuItem skuItem = stocks.get(index);

                if (!skuItem.getOpening().isEmpty() && !skuItem.getOpening().equalsIgnoreCase("0")
                        || !skuItem.getClosing().isEmpty() && !skuItem.getClosing().equalsIgnoreCase("0")
                        || !skuItem.getSecondary().isEmpty() && !skuItem.getSecondary().equalsIgnoreCase("0")) {

                    TextView tvSKuValue = new TextView(mContext);
                    tvSKuValue.setText(skuItem.getSku());
                    tvSKuValue.setPadding(15, 15, 15, 15);
                    tvSKuValue.setTextColor(Color.parseColor("#424242"));
                    tvSKuValue.setLayoutParams(paramsV);

                    TextView tvOpeningValue = new TextView(mContext);
                    tvOpeningValue.setText(skuItem.getOpening());
                    tvOpeningValue.setPadding(15, 15, 15, 15);
                    tvOpeningValue.setTextColor(Color.parseColor("#424242"));
                    tvOpeningValue.setLayoutParams(paramsV);

                    TextView tvSecondaryValue = new TextView(mContext);
                    tvSecondaryValue.setText(skuItem.getSecondary());
                    tvSecondaryValue.setPadding(15, 15, 15, 15);
                    tvSecondaryValue.setTextColor(Color.parseColor("#424242"));
                    tvSecondaryValue.setLayoutParams(paramsV);

                    TextView tvPrimaryValue = new TextView(mContext);
                    tvPrimaryValue.setText(skuItem.getClosing());
                    tvPrimaryValue.setPadding(15, 15, 15, 15);
                    tvPrimaryValue.setTextColor(Color.parseColor("#424242"));
                    tvPrimaryValue.setLayoutParams(paramsV);


                    LinearLayout llRow = new LinearLayout(mContext);
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

                    text = text.concat("\n");


                }
            }

            llVerticalH.addView(llVertical);

            TextView tvSKuTotal = new TextView(mContext);
            tvSKuTotal.setText("Total = ");
            tvSKuTotal.setPadding(15, 15, 15, 15);
            tvSKuTotal.setTextColor(Color.parseColor("#424242"));
            tvSKuTotal.setLayoutParams(paramsV);

            TextView tvOpeningTotal = new TextView(mContext);
            tvOpeningTotal.setText(totalOpening.get(i));
            tvOpeningTotal.setPadding(15, 15, 15, 15);
            tvOpeningTotal.setTextColor(Color.parseColor("#424242"));
            tvOpeningTotal.setLayoutParams(paramsV);

            TextView tvSecondaryTotal = new TextView(mContext);
            tvSecondaryTotal.setText(totalSecondary.get(i));
            tvSecondaryTotal.setPadding(15, 15, 15, 15);
            tvSecondaryTotal.setTextColor(Color.parseColor("#424242"));
            tvSecondaryTotal.setLayoutParams(paramsV);

            TextView tvPrimaryTotal = new TextView(mContext);
            tvPrimaryTotal.setText(totalClosing.get(i));
            tvPrimaryTotal.setPadding(15, 15, 15, 15);
            tvPrimaryTotal.setTextColor(Color.parseColor("#424242"));
            tvPrimaryTotal.setLayoutParams(paramsV);


            LinearLayout llFooter = new LinearLayout(mContext);
            llFooter.setLayoutParams(paramsH);
            llFooter.setOrientation(LinearLayout.HORIZONTAL);
            llFooter.addView(tvSKuTotal);
            llFooter.addView(tvOpeningTotal);
            llFooter.addView(tvSecondaryTotal);
            llFooter.addView(tvPrimaryTotal);

            llVerticalH.addView(line3);
            llVerticalH.addView(llFooter);
            llVerticalH.addView(line4);


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
        text = text.concat("\n");
        text = text.concat("MTD = " + tvMTD.getText().toString());
        text = text.concat("\n");
        text = text.concat("Tc : " + tvTC.getText().toString());
        text = text.concat("\n");
        text = text.concat("Pc : " + tvPC.getText().toString());


        final String finalText = text;
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Today Summary");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, finalText);
                mContext.startActivity(Intent.createChooser(sharingIntent, "Share summary"));

                dialog.dismiss();

            }
        });

        dialog.show();

    }


    public void refreshDays() {

        int maxP, maxWeeknumber, calMaxP, mnthlength;
        GregorianCalendar pmonthmaxset;
        dayString.clear();
        gregorianCalendar = (GregorianCalendar) calendar.clone();

        // month start day. ie; sun, mon, etc
        firstDay = calendar.get(GregorianCalendar.DAY_OF_WEEK);

//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
        // finding number of weeks in current month.
        maxWeeknumber = gregorianCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);//getWeekCount(year, month);
        //Log.e("CustomCalendarAdapter", "==>>For " + year + " AND Month" + month + "==>" + maxWeeknumber);

        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...

        /* Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.*/
        pmonthmaxset = (GregorianCalendar) gregorianCalendar.clone();

        ///* setting the start date as previous month's required date.*/
        pmonthmaxset.set(GregorianCalendar.DAY_OF_MONTH, calMaxP + 1);

        ///* filling calendar gridview.*/
        for (int n = 0; n < mnthlength; n++) {

            String itemvalue = df.format(pmonthmaxset.getTime());
            pmonthmaxset.add(GregorianCalendar.DATE, 1);
            dayString.add(itemvalue);
        }
    }

    private int getMaxP() {
        int maxP;
        if (calendar.get(GregorianCalendar.MONTH) == calendar.getActualMinimum(GregorianCalendar.MONTH)) {
            gregorianCalendar.set((calendar.get(GregorianCalendar.YEAR) - 1),
                    calendar.getActualMaximum(GregorianCalendar.MONTH), 1);
        } else {
            gregorianCalendar.set(GregorianCalendar.MONTH,
                    calendar.get(GregorianCalendar.MONTH) - 1);
        }
        maxP = gregorianCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);

        return maxP;
    }

    // Note: 0-based month as per the rest of java.util.Calendar
//    private int getWeekCount(int year, int month) {
//
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.YEAR, year);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        cal.set(Calendar.MONTH, month);
////        return cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
//        //Calendar calendar = new Calendar(year, month, Calendar.DAY_OF_MONTH);
//        int dayOfWeekOfStartOfMonth = cal.get(Calendar.DAY_OF_WEEK);
//        int extraDays = EXTRA_DAYS[dayOfWeekOfStartOfMonth];
//        int regularDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int effectiveDaysInMonth = regularDaysInMonth + extraDays;
//        return effectiveDaysInMonth / 7;
//    }

}

