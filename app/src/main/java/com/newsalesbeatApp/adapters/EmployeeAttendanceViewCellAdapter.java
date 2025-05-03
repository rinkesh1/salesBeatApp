package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAttendanceViewCellAdapter extends BaseAdapter {


    private static final String TAG = "EmployeeAttendanceViewCellAdapter";
    private static final int DAY_OFFSET = 1;
    private final Context _context;
    private final List<String> list;
    private final String[] months = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    ServerCall serverCall;
    private int currentDayOfMonth;
    private int currentWeekDay;

    private ArrayList<String> marketName = new ArrayList<>();
    private ArrayList<String> town = new ArrayList<>();
    private ArrayList<String> beatName = new ArrayList<>();
    private ArrayList<String> contact = new ArrayList<>();
    private ArrayList<String> totalOpening = new ArrayList<>();
    private ArrayList<String> totalSecondary = new ArrayList<>();
    private ArrayList<String> totalClosing = new ArrayList<>();
    private ArrayList<ArrayList<SkuItem>> stocksList2 = new ArrayList<>();

    private SharedPreferences myPref;
    private UtilityClass utilityClass;
    private SalesBeatDb salesBeatDb;

    // Days in Current Month
    public EmployeeAttendanceViewCellAdapter(Context context, int month, int year/*, HashMap<String, String> eventsPerMonthMap*/) {
        super();
        this._context = context;
        myPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        utilityClass = new UtilityClass(context);
        //salesBeatDb = new SalesBeatDb(context);
        salesBeatDb = SalesBeatDb.getHelper(context);
        serverCall = new ServerCall(context);

        this.list = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);
    }

    private String getMonthAsString(int i) {
        return months[i];
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @SuppressLint("LongLogTag")
    private void printMonth(int mm, int yy) {

        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        //String currentMonthName = getMonthAsString(currentMonth);
        int daysInMonth = getNumberOfDaysOfMonth(currentMonth);

        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {

            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {

            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;

        } else {

            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);

        }

        currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        trailingSpaces = currentWeekDay;

        if (cal.isLeapYear(cal.get(Calendar.YEAR)))
            if (mm == 2)
                ++daysInMonth;
            else if (mm == 3)
                ++daysInPrevMonth;

        // Trailing Month days
        for (int i = 0; i < trailingSpaces; i++) {

            list.add(String
                    .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                            + i)
                    + "-GREY"
                    + "-"
                    + getMonthAsString(prevMonth)
                    + "-"
                    + prevYear);
        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {

            if (i == getCurrentDayOfMonth() && currentMonth == Calendar.getInstance().get(Calendar.MONTH)) {
                list.add(String.valueOf(i) + "-BLUE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);
            } else {
                list.add(String.valueOf(i) + "-WHITE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);
            }
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {

            list.add(String.valueOf(i + 1) + "-GREY" + "-"
                    + getMonthAsString(nextMonth) + "-" + nextYear);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) _context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.custom_calender_cell, parent, false);
        }

        // Get a reference to the Day gridcell
        TextView tvDate = row.findViewById(R.id.date);

        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        int themonth = 0;//Integer.parseInt(day_color[2]);
        for (int i = 0; i < months.length; i++) {

            if (months[i].equalsIgnoreCase(day_color[2])) {
                themonth = i + 1;
            }
        }

        int theyear = Integer.parseInt(day_color[3]);

        String date = "";
        if (themonth < 10) {

            if (Integer.parseInt(theday) < 10)
                date = theyear + "-0" + themonth + "-0" + theday;
            else
                date = theyear + "-0" + themonth + "-" + theday;

        } else {

            if (Integer.parseInt(theday) < 10)
                date = theyear + "-" + themonth + "-0" + theday;
            else
                date = theyear + "-" + themonth + "-" + theday;
        }

        Cursor cursor = null;

        try {

            cursor = salesBeatDb.getAllRecordFromUserAttendanceTable2(date);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                String attendance = cursor.getString(cursor.getColumnIndex("attendance"));

                if (attendance.equalsIgnoreCase("present")) {

                    tvDate.setTextColor(Color.WHITE);
                    tvDate.setBackgroundResource(R.drawable.green_circle);

                } else if (attendance.equalsIgnoreCase("leave")) {

                    tvDate.setTextColor(Color.WHITE);
                    tvDate.setBackgroundResource(R.drawable.red_circle);

                } else if (attendance.equalsIgnoreCase("absent")) {

                    tvDate.setTextColor(Color.WHITE);
                    tvDate.setBackgroundResource(R.drawable.red_circle);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        // Set the Day GridCell
        tvDate.setText(theday);
        tvDate.setTag(theday + "-" + themonth + "-" + theyear);

//        Log.d(tag, "Setting GridCell " + theday + "-" + themonth + "-" + theyear);

        if (day_color[1].equals("GREY")) {
            tvDate.setTextColor(_context.getResources().getColor(R.color.textColor));
            tvDate.setVisibility(View.INVISIBLE);
        }

        if (day_color[1].equals("BLUE")) {

            tvDate.setTextColor(_context.getResources().getColor(R.color.colorAccent));
            tvDate.setBackgroundResource(R.drawable.circle_whitish);
        }

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                final Dialog dialog = new Dialog(_context, R.style.DialogActivityTheme);
//                dialog.setContentView(R.layout.emp_details_withdate);
//                dialog.getWindow().setGravity(Gravity.BOTTOM);
//                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//
//                dialog.show();

                if (utilityClass.isInternetConnected()) {

                    String[] separatedTime = list.get(position).split("-");
                    String day = separatedTime[0];
                    String month = separatedTime[2];
                    String year = separatedTime[3];

                    Calendar calendar = Calendar.getInstance();
                    int currentDay = calendar.get(Calendar.DATE);
                    int currentMonth = calendar.get(Calendar.MONTH) + 1;
                    int currentYear = calendar.get(Calendar.YEAR);

                    int m = 0;
                    for (int i = 0; i < months.length; i++) {

                        if (months[i].equalsIgnoreCase(month)) {
                            m = i + 1;
                        }
                    }

                    String tempM = "";
                    tempM = String.valueOf(m);
                    if (tempM.length() == 1) {
                        tempM = "";
                        tempM = tempM.concat("0");
                        tempM = tempM.concat(String.valueOf(m));
                    }

                    String temp2 = "";
                    if (day.length() == 1)
                        temp2 = temp2.concat("0");

                    temp2 = temp2.concat(day);

                    if (Integer.parseInt(day) <= currentDay
                            && Integer.parseInt(tempM) <= currentMonth
                            && Integer.parseInt(year) <= currentYear) {

                        String date = year + "-" + tempM + "-" + temp2;
                        getEmployeeRecordByDate(date);


                    } else {

                        Toast.makeText(_context, "No data available", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(_context, "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return row;
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }

    //public int getCurrentWeekDay() {
//        return currentWeekDay;
//    }

    private void getEmployeeRecordByDate(final String date) {
        Log.e("TAG", "check Date: "+date);
        final Dialog loader = new Dialog(_context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_RECORD_BY_DATE  + "?date=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Response", "EMP_RECORD_BY_DATE=====" + response);
                loader.dismiss();

                String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
                        status = "", totalWorkingTime = "", totalRetailingTime = "", reason = "",
                        target = "", achievement = "", beatAssigned = "", beatVisited = "", newCounter = "", sale = "";

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


                        final List<String> jointWorkingList = new ArrayList<>();
                        if (response.has("jointWorking") && !response.isNull("jointWorking")) {
                            JSONArray fullDayActivityAArr = response.getJSONArray("jointWorking");

                            for (int i = 0; i < fullDayActivityAArr.length(); i++) {

                                String emp = (String) fullDayActivityAArr.get(i);

                                jointWorkingList.add(emp);
                            }
                        }


                        final Dialog dialog = new Dialog(_context, R.style.DialogActivityTheme);
                        dialog.setContentView(R.layout.dailyassesment_dailog);
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        //dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        final TextView tvDateView = dialog.findViewById(R.id.tvDateView);
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
                        TextView tvSale = dialog.findViewById(R.id.tvSale);
                        TextView tvFullDayActivity = dialog.findViewById(R.id.tvFullDayActivity);
                        ImageView imgForward = dialog.findViewById(R.id.imgForward);
                        ImageView imgForwardL = dialog.findViewById(R.id.imgForwardL);
                        ImageView imgForwardT = dialog.findViewById(R.id.imgForwardT);
                        ImageView imgForwardBA = dialog.findViewById(R.id.imgForwardBA);
                        ImageView imgForwardBV = dialog.findViewById(R.id.imgForwardBV);
                        ImageView imgForwardOA = dialog.findViewById(R.id.imgForwardOA);
                        ImageView imgForwardNC = dialog.findViewById(R.id.imgForwardNC);

                        LinearLayout llOk = dialog.findViewById(R.id.llCancel);
                        LinearLayout llShowSummary = dialog.findViewById(R.id.llShowSummary);

                        String arr[] = date.split("-");
                        String yr = arr[0];
                        String mn = arr[1];
                        String dy = arr[2];
                        int tempCount = Integer.parseInt(mn) - 1;
                        String monthStr = months[tempCount];
                        String strDate = dy + ", " + monthStr + ", " + yr;
                        tvDateView.setText(strDate);

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
                        tvNewCounter.setText(newCounter);


                        if (totalRetailingTime != null && !totalRetailingTime.isEmpty())
                            tvTotalRetailingHour.setText(totalRetailingTime);

                        if (totalWorkingTime != null && !totalWorkingTime.isEmpty())
                            tvTotalWorkingHour.setText(totalWorkingTime);

                        tvFullDayActivity.setText(fullDayActivity);

                        if (productiveCall.equalsIgnoreCase("0")
                                || productiveCall.isEmpty()) {
                            imgForward.setVisibility(View.INVISIBLE);
                        }

                        if (skuItemList == null || skuItemList.size() == 0)
                            imgForwardL.setVisibility(View.INVISIBLE);

                        if (retailerItemList == null || retailerItemList.size() == 0)
                            imgForwardT.setVisibility(View.INVISIBLE);

                        if (beatList2 == null || beatList2.size() == 0)
                            imgForwardBA.setVisibility(View.INVISIBLE);

                        if (beatList == null || beatList.size() == 0)
                            imgForwardBV.setVisibility(View.INVISIBLE);

                        if (fullDayActivityList == null || fullDayActivityList.size() == 0)
                            imgForwardOA.setVisibility(View.INVISIBLE);

                        if (newRetailerItemList == null || newRetailerItemList.size() == 0)
                            imgForwardNC.setVisibility(View.INVISIBLE);

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
                                //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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
                            Toast.makeText(_context, "No record found", Toast.LENGTH_SHORT).show();
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
                try {

                    serverCall.handleError(error, TAG, "employee-reports/date/");

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

        objectRequest.setShouldCache(false);

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(_context).add(objectRequest);
    }

    private void showSummary(String date) {

        final Dialog loader = new Dialog(_context, R.style.DialogActivityTheme);
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
                            beatName.clear();
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
                            String nc = data.getString("ncCount");
                            String mSale = data.getString("msale");

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

                                stocksList2.add(stocksList);
                            }

                            String status = data.getString("status");

                            if (status.equalsIgnoreCase("success")) {

                                getNewDitributorHistory(date, name, tc, pc, sale, mtc, mpc, mSale, nc);

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
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(_context).add(postRequest);

    }


    private void getNewDitributorHistory(final String date, final String name, final String tc, final String pc, final String sale,
                                         final String mtc, final String mpc, final String mSale, final String nc) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_NEW_DISTRIBUTOR_HISTORY + "?fromDate=" + date + "&toDate=" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("onResponse", "New Distributor history===" + response);
                //loader.dismiss();
                try {

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

                    String stt = response.getString("status");
                    if (stt.equalsIgnoreCase("success")) {

                        showDailySummaryDailog(date, name, tc, pc, sale, mtc, mpc, mSale,
                                marketName, town, contact, totalOpening, totalSecondary, totalClosing, stocksList2,
                                nc, newDistributorDetails);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                showDailySummaryDailog(date, name, tc, pc, sale, mtc, mpc, mSale,
                        marketName, town, contact, totalOpening, totalSecondary, totalClosing, stocksList2,
                        nc, null);
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


    private void showDailySummaryDailog(String date, String name, String tc, String pc, String sale, String mtc,
                                        String mpc, String mSale, ArrayList<String> marketName, ArrayList<String> town,
                                        ArrayList<String> contact, ArrayList<String> totalOpening,
                                        ArrayList<String> totalSecondary, ArrayList<String> totalClosing,
                                        ArrayList<ArrayList<SkuItem>> stocksList2, String nc,
                                        ArrayList<ClaimHistoryItem> newDistributorDetails) {

        final Dialog dialog = new Dialog(_context, R.style.DialogTheme);
        dialog.setContentView(R.layout.employee_summary_layout);
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
        final TextView tvTodayTotalSale = dialog.findViewById(R.id.tvTodayTotalSale);
        final TextView tvMTD = dialog.findViewById(R.id.tvMTD);
        final TextView tvTC = dialog.findViewById(R.id.tvTC);
        final TextView tvPC = dialog.findViewById(R.id.tvPC);
        Button btnShare = dialog.findViewById(R.id.btnShare);
        LinearLayout container = dialog.findViewById(R.id.container);
        LinearLayout container2 = dialog.findViewById(R.id.container2);

        String arr[] = date.split("-");
        String yr = arr[0];
        String mn = arr[1];
        String dy = arr[2];
        int tempCount = Integer.parseInt(mn) - 1;
        String monthStr = months[tempCount];
        String strDate = dy + ", " + monthStr + ", " + yr;

        tvDate.setText(strDate);
        tvAsmTsiName.setText(name);
        tvTC.setText(mtc);
        tvPC.setText(mpc);
        tvMTD.setText(mSale + _context.getString(R.string.unitt));
        tvTodayTc.setText(tc);
        tvTodayPc.setText(pc);
        tvTodayNc.setText(nc);
        tvTodayTotalSale.setText(sale + "Kg");


        LinearLayout.LayoutParams paramsH = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout llVerticalH = new LinearLayout(_context);
        llVerticalH.setLayoutParams(paramsH);
        llVerticalH.setOrientation(LinearLayout.VERTICAL);

        String text = "";

        text = text.concat("Date-01 : " + tvDate.getText().toString());
        text = text.concat("\n");
        text = text.concat("ASM/SO/TSI Name : " + tvAsmTsiName.getText().toString());

        text = text.concat("\n");
        text = text.concat("Today :-  Tc : " + tvTodayTc.getText().toString() + "  Pc : " + tvTodayPc.getText().toString()
                + "  Nc : " + tvTodayNc.getText().toString() + "  Total Sale : " + tvTodayTotalSale.getText().toString());
        text = text.concat("\n");
        text = text.concat("\n");
        text = text.concat("\n");


        for (int i = 0; i < marketName.size(); i++) {

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            TextView tvMarkNameLabel = new TextView(_context);
            tvMarkNameLabel.setText("Party Name :");
            tvMarkNameLabel.setPadding(15, 15, 15, 15);
            tvMarkNameLabel.setTextColor(Color.GRAY);
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
            tvStationNameLabel.setText("Station Name :");
            tvStationNameLabel.setPadding(15, 15, 15, 15);
            tvStationNameLabel.setTextColor(Color.GRAY);
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
            tvBeatNameLabel.setTextColor(Color.GRAY);
            tvBeatNameLabel.setLayoutParams(paramsV);

            TextView tvBeatNameValue = new TextView(_context);
            tvBeatNameValue.setText(beatName.get(i));
            tvBeatNameValue.setPadding(15, 15, 15, 15);
            tvBeatNameValue.setTextColor(Color.parseColor("#424242"));
            tvBeatNameValue.setLayoutParams(paramsV);

            LinearLayout llBeatName = new LinearLayout(_context);
            llBeatName.setLayoutParams(paramsH);
            llBeatName.setOrientation(LinearLayout.HORIZONTAL);
            llBeatName.addView(tvBeatNameLabel);
            llBeatName.addView(tvBeatNameValue);

            llVerticalH.addView(llBeatName);

            TextView tvPartyContactPersonLabel = new TextView(_context);
            tvPartyContactPersonLabel.setText("Party Contact Person :");
            tvPartyContactPersonLabel.setPadding(15, 15, 15, 15);
            tvPartyContactPersonLabel.setTextColor(Color.GRAY);
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
            tvMobileLabel.setTextColor(Color.GRAY);
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

            LinearLayout.LayoutParams paramsDivided = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            paramsDivided.weight = (float) 0.15;
            paramsDivided.gravity = Gravity.CENTER;

            LinearLayout.LayoutParams paramsDivided2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            paramsDivided2.weight = (float) 0.55;
            paramsDivided2.gravity = Gravity.CENTER;

            TextView tvSKuLabel = new TextView(_context);
            tvSKuLabel.setText("SKU");
            tvSKuLabel.setTextColor(Color.GRAY);
            tvSKuLabel.setPadding(15, 15, 15, 15);
            tvSKuLabel.setLayoutParams(paramsDivided2);

            TextView tvOpeningLabel = new TextView(_context);
            tvOpeningLabel.setText("OS");
            tvOpeningLabel.setPadding(15, 15, 15, 15);
            tvOpeningLabel.setTextColor(Color.GRAY);
            tvOpeningLabel.setLayoutParams(paramsDivided);

            TextView tvSecondaryLabel = new TextView(_context);
            tvSecondaryLabel.setText("SEC");
            tvSecondaryLabel.setPadding(15, 15, 15, 15);
            tvSecondaryLabel.setTextColor(Color.GRAY);
            tvSecondaryLabel.setLayoutParams(paramsDivided);

            TextView tvPrimaryLabel = new TextView(_context);
            tvPrimaryLabel.setText("CS");
            tvPrimaryLabel.setPadding(15, 15, 15, 15);
            tvPrimaryLabel.setTextColor(Color.GRAY);
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
            line1.setBackgroundColor(Color.GRAY);
            line1.setLayoutParams(paramsL);

            LinearLayout line2 = new LinearLayout(_context);
            line2.setBackgroundColor(Color.GRAY);
            line2.setLayoutParams(paramsL);


            LinearLayout line3 = new LinearLayout(_context);
            line3.setBackgroundColor(Color.GRAY);
            line3.setLayoutParams(paramsL);


            LinearLayout line4 = new LinearLayout(_context);
            line4.setBackgroundColor(Color.GRAY);
            line4.setLayoutParams(paramsL);


            llVerticalH.addView(line1);
            llVerticalH.addView(llHeader);
            llVerticalH.addView(line2);

            ArrayList<SkuItem> stocks = stocksList2.get(i);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(_context);

            for (int index = 0; index < stocks.size(); index++) {

                SkuItem skuItem = stocks.get(index);

                if (!skuItem.getOpening().isEmpty() && !skuItem.getOpening().equalsIgnoreCase("0")
                        || !skuItem.getClosing().isEmpty() && !skuItem.getClosing().equalsIgnoreCase("0")
                        || !skuItem.getSecondary().isEmpty() && !skuItem.getSecondary().equalsIgnoreCase("0")) {

                    TextView tvSKuValue = new TextView(_context);
                    tvSKuValue.setText(skuItem.getSku());
                    tvSKuValue.setPadding(15, 15, 15, 15);
                    tvSKuValue.setTextColor(Color.parseColor("#424242"));
                    tvSKuValue.setLayoutParams(paramsDivided2);

                    TextView tvOpeningValue = new TextView(_context);
                    tvOpeningValue.setText(skuItem.getOpening());
                    tvOpeningValue.setPadding(15, 15, 15, 15);
                    tvOpeningValue.setTextColor(Color.parseColor("#424242"));
                    tvOpeningValue.setLayoutParams(paramsDivided);

                    TextView tvSecondaryValue = new TextView(_context);
                    tvSecondaryValue.setText(skuItem.getSecondary());
                    tvSecondaryValue.setPadding(15, 15, 15, 15);
                    tvSecondaryValue.setTextColor(Color.parseColor("#424242"));
                    tvSecondaryValue.setLayoutParams(paramsDivided);

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
        text = text.concat("MTD(sale) = " + tvMTD.getText().toString());
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
        if (newDistributorDetails != null && newDistributorDetails.size() > 0) {

            LinearLayout.LayoutParams paramsV = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout llVertical = new LinearLayout(_context);
            llVertical.setLayoutParams(params);
            llVertical.setOrientation(LinearLayout.VERTICAL);

            TextView tvSKuValue = new TextView(_context);
            tvSKuValue.setText("***** NEW DISTRIBUTOR SERVEY ******");
            tvSKuValue.setPadding(15, 15, 15, 15);
            tvSKuValue.setTextColor(Color.parseColor("#424242"));
            tvSKuValue.setLayoutParams(paramsV);

            llVertical.addView(tvSKuValue);

            text = text.concat("\n");
            text = text.concat("*New Distributor Added*");
            text = text.concat("\n");
            text = text.concat("\n");

            for (int i = 0; i < newDistributorDetails.size(); i++) {

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
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Today Summary");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, finalText);
                _context.startActivity(Intent.createChooser(sharingIntent, "Share summary"));

                dialog.dismiss();

            }
        });

        dialog.show();

    }

}

