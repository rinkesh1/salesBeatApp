package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.MyPjpActivity;
import com.newsalesbeatApp.pojo.MyPjp;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPjpViewCellAdapter extends BaseAdapter {

    private static final int DAY_OFFSET = 1;
    public static String date = "";
    private final String TAG = "MyPjpViewCellAdapter";
    private final Context _context;
    private final List<String> list = new ArrayList<>();
    private final ArrayList<ArrayList<MyPjp>> myPjp = new ArrayList<>();
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    SharedPreferences myPref;
    //private int daysInMonth;
    private int currentDayOfMonth;
    private int currentWeekDay;
    private ArrayList<MyPjp> eventsPerMonthMap;
    private int pos = -1;

    // Days in Current Month
    public MyPjpViewCellAdapter(Context context, int month, int year, ArrayList<MyPjp> eventsPerMonthMap) {
        super();
        this._context = context;

        date = "";

        myPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);

        // Find Number of Events
        this.eventsPerMonthMap = eventsPerMonthMap;
    }

    @SuppressLint("LongLogTag")
    private void printMonth(int mm, int yy) {

        Log.e(TAG, " Month=" + mm + " Year=" + yy);
        int trailingSpaces;
        int daysInPrevMonth;
        int prevMonth;
        int prevYear;
        int nextMonth;
        int nextYear;

        if (mm == 0)
            mm = 1;
        int currentMonth = mm - 1;
        int currentYear = yy;
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

            myPjp.add(null);
        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {

            if (i == getCurrentDayOfMonth() && currentMonth == Calendar.getInstance().get(Calendar.MONTH)
                    && currentYear == Calendar.getInstance().get(Calendar.YEAR)) {
                list.add(String.valueOf(i) + "-BLUE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);
            } else {
                list.add(String.valueOf(i) + "-WHITE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);
            }

            myPjp.add(null);
        }

        // Leading Month days
        for (int i = 0; i < list.size() % 7; i++) {
            list.add(String.valueOf(i + 1) + "-GREY" + "-"
                    + getMonthAsString(nextMonth) + "-" + nextYear);

            myPjp.add(null);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder viewHolder;

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                row = inflater.inflate(R.layout.custom_calender2_cell, parent, false);
            }

            viewHolder = new ViewHolder();
            // Get a reference to the Day gridcell
            viewHolder.tvDate = row.findViewById(R.id.date);
            viewHolder.imgDot = row.findViewById(R.id.imgDot);

            row.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) row.getTag();
        }

        String[] day_color = list.get(position).split("-");
        String theday = day_color[0];
        int themonth = 0;//Integer.parseInt(day_color[2]);
        for (int i = 0; i < months.length; i++) {

            if (months[i].equalsIgnoreCase(day_color[2])) {
                themonth = i + 1;
            }
        }

        int theyear = Integer.parseInt(day_color[3]);
        //checking for PJP
        if (eventsPerMonthMap != null && eventsPerMonthMap.size() > 0) {

            boolean chkPjp = checkForPJP(theday, themonth, theyear);

            Log.d(TAG, "Check for Pjp: " + chkPjp);

            if (chkPjp) {

                ArrayList<MyPjp> myPjps = eventsPerMonthMap.get(pos).getMyPjpArrayList();
                myPjp.set(position, myPjps);
                viewHolder.imgDot.setVisibility(View.VISIBLE);

            } else {

                myPjp.set(position, null);
                viewHolder.imgDot.setVisibility(View.INVISIBLE);
            }

        }

        // Set the Day GridCell
        viewHolder.tvDate.setText(theday);
        viewHolder.tvDate.setTag(theday + "-" + themonth + "-" + theyear);

        if (day_color[1].equals("GREY")) {
            viewHolder.tvDate.setTextColor(_context.getResources().getColor(R.color.textColor));
            viewHolder.tvDate.setVisibility(View.INVISIBLE);
            viewHolder.imgDot.setVisibility(View.INVISIBLE);
        }

        if (day_color[1].equals("BLUE")) {

            viewHolder.tvDate.setTextColor(_context.getResources().getColor(R.color.green_like));
            setPjp(position);

            showPjpDetails(position);
        }

        viewHolder.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(_context, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {

                        showPjpDetails(position);
                    }

                });

            }
        });

        return row;
    }

    private void showPjpDetails(int position) {

        MyPjpActivity.tvNoPjp.setVisibility(View.GONE);
        MyPjpActivity.btnCreatePjp1.setVisibility(View.GONE);
        MyPjpActivity.rvMyPjpList.setVisibility(View.GONE);
        MyPjpActivity.pbPjp.setVisibility(View.VISIBLE);

        String[] dateArr = list.get(position).split("-");
        String theday = dateArr[0];
        String themonth = dateArr[2];
        String theyear = dateArr[3];

        int themonthInt = 0;//Integer.parseInt(day_color[2]);
        for (int i = 0; i < months.length; i++) {

            if (months[i].equalsIgnoreCase(dateArr[2])) {
                themonthInt = i + 1;
            }
        }

        int day = Integer.parseInt(theday);

        if (themonthInt < 10) {

            if (day < 10)
                date = theyear + "-0" + themonthInt + "-0" + day;
            else
                date = theyear + "-0" + themonthInt + "-" + day;
        } else {

            if (day < 10)
                date = theyear + "-" + themonthInt + "-0" + day;
            else
                date = theyear + "-" + themonthInt + "-" + day;
        }


        String date2 = theyear + "-" + themonth + "-" + theday;
        //new LoaderTask(position).execute();
        getPJPListFromServer(date, date2);

    }

    private String setPjp(int position) {

        try {

            ArrayList<MyPjp> myPjpArrayList = myPjp.get(position);

            if (myPjpArrayList != null && myPjpArrayList.size() > 0) {

                String[] day_color = list.get(position).split("-");
                String theday = day_color[0];
                String themonth = day_color[2];
                String theyear = day_color[3];

                MyPjpListAdapter myPjpListAdapter = new MyPjpListAdapter(_context, myPjpArrayList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(_context);
                MyPjpActivity.rvMyPjpList.setLayoutManager(layoutManager);
                MyPjpActivity.rvMyPjpList.setAdapter(myPjpListAdapter);

                MyPjpActivity.tvSelectedView.setText(theday + " " + themonth + "," + theyear);
                MyPjpActivity.tvNoPjp.setVisibility(View.GONE);
                MyPjpActivity.btnCreatePjp1.setVisibility(View.GONE);
                MyPjpActivity.pbPjp.setVisibility(View.GONE);
                MyPjpActivity.rvMyPjpList.setVisibility(View.VISIBLE);

                return "";

            } else {

                String[] day_color = list.get(position).split("-");
                String theday = day_color[0];
                String themonth = day_color[2];
                String theyear = day_color[3];
                MyPjpActivity.tvSelectedView.setText(theday + " " + themonth + "," + theyear);
                MyPjpActivity.rvMyPjpList.setVisibility(View.GONE);
                MyPjpActivity.pbPjp.setVisibility(View.GONE);
                MyPjpActivity.btnCreatePjp1.setVisibility(View.VISIBLE);
                MyPjpActivity.tvNoPjp.setVisibility(View.VISIBLE);

                String date = theday + "-" + themonth + "-" + theyear;

                return date;

            }

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private boolean checkForPJP(String fillingDay, int fillMonth, int fillYear) {

        for (int i = 0; i < eventsPerMonthMap.size(); i++) {

            try {

                String[] pjp = eventsPerMonthMap.get(i).getDate().split("-");
                //@Umesh 13-March-2022
                int pjpDay = Integer.parseInt(pjp[2].replace("T00:00:00",""));
                int pjpMonthh = Integer.parseInt(pjp[1]);
                int pjpYear = Integer.parseInt(pjp[0]);

                int fillDay = Integer.parseInt(fillingDay);

                if ((fillDay == pjpDay) && (fillMonth == pjpMonthh) && (fillYear == pjpYear)) {
                    pos = i;
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    private String getMonthAsString(int i) {
        return months[i];
    }

    private int getNumberOfDaysOfMonth(int i) {

        Log.e(TAG, " *****i = " + i);
        return daysOfMonth[i];
    }

    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    private void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }

    private void getPJPListFromServer(String date, final String date2) {


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_BEAT_PLAN_BY_DATE +"?date="+date, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "My PJP ===" + response);
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
                            //pjp.setAssigneeEmp(object.getString("assignee")); Not Found
                            pjp.setAssigneeAdmin("");//(object.getString("assigneeAdmin"));
                            pjp.setActivity(object.getString("activity"));
                            pjp.setTc(object.getString("tc"));
                            pjp.setPc(object.getString("pc"));
                            pjp.setSale(object.getString("sale"));
                            pjp.setRemarks(object.getString("remarks"));

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
                        initializeCalendar(myPjpArrayList, date2);
                    }
                    else
                    {
                        //Toast.makeText(this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                    String[] day_color = date2.split("-");
                    String theday = day_color[2];
                    String themonth = day_color[1];
                    String theyear = day_color[0];
                    MyPjpActivity.tvSelectedView.setText(theday + " " + themonth + "," + theyear);
                    MyPjpActivity.rvMyPjpList.setVisibility(View.GONE);
                    MyPjpActivity.pbPjp.setVisibility(View.GONE);
                    MyPjpActivity.btnCreatePjp1.setVisibility(View.VISIBLE);
                    MyPjpActivity.tvNoPjp.setVisibility(View.VISIBLE);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
                error.printStackTrace();
//                SbLog.printError(TAG, "getPjps", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
//                        myPref.getString(getString(R.string.emp_id_key), ""));

                // serverCall.handleError(error, "MyPjpViewCellAdapter", "getPjps");

                try {

                    String[] day_color = date2.split("-");
                    String theday = day_color[2];
                    String themonth = day_color[1];
                    String theyear = day_color[0];
                    MyPjpActivity.tvSelectedView.setText(theday + " " + themonth + "," + theyear);
                    MyPjpActivity.rvMyPjpList.setVisibility(View.GONE);
                    MyPjpActivity.pbPjp.setVisibility(View.GONE);
                    MyPjpActivity.btnCreatePjp1.setVisibility(View.VISIBLE);
                    MyPjpActivity.tvNoPjp.setVisibility(View.VISIBLE);


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
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(_context).add(jsonObjectRequest);
    }

    private void initializeCalendar(ArrayList<MyPjp> myPjpArrayList, String date2) {

        if (myPjpArrayList != null && myPjpArrayList.size() > 0) {

            String[] day_color = date2.split("-");
            String theday = day_color[2];
            String themonth = day_color[1];
            String theyear = day_color[0];

            MyPjpListAdapter myPjpListAdapter = new MyPjpListAdapter(_context, myPjpArrayList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(_context);
            MyPjpActivity.rvMyPjpList.setLayoutManager(layoutManager);
            MyPjpActivity.rvMyPjpList.setAdapter(myPjpListAdapter);

            MyPjpActivity.tvSelectedView.setText(theday + " " + themonth + "," + theyear);
            MyPjpActivity.tvNoPjp.setVisibility(View.GONE);
            MyPjpActivity.btnCreatePjp1.setVisibility(View.GONE);
            MyPjpActivity.pbPjp.setVisibility(View.GONE);
            MyPjpActivity.rvMyPjpList.setVisibility(View.VISIBLE);

            //return "";

        } else {

            String[] day_color = date2.split("-");
            String theday = day_color[2];
            String themonth = day_color[1];
            String theyear = day_color[0];
            MyPjpActivity.tvSelectedView.setText(theday + " " + themonth + "," + theyear);
            MyPjpActivity.rvMyPjpList.setVisibility(View.GONE);
            MyPjpActivity.pbPjp.setVisibility(View.GONE);
            MyPjpActivity.btnCreatePjp1.setVisibility(View.VISIBLE);
            MyPjpActivity.tvNoPjp.setVisibility(View.VISIBLE);

        }
    }

    static class ViewHolder {
        TextView tvDate;
        ImageView imgDot;
    }
}

