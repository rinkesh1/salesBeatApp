package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.DailyAssesmentActivity;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.UtilityClass;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by abc on 10/30/18.
 */

public class DailyAssesmentAdapter extends BaseAdapter {


    private static final String TAG = "DailyAssesmentAdapter";
    private static final int DAY_OFFSET = 1;
    private final Context _context;
    private final List<String> list;
    private final String[] months = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};

    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};

    private int currentDayOfMonth;
    private int currentWeekDay;

    private UtilityClass utilityClass;
    //private SalesBeatDb salesBeatDb;
    private Map<String, String> userStatus = new HashMap<>();
    private int flag;

    // Days in Current Month
    public DailyAssesmentAdapter(Context context, int month, int year, Map<String, String> userStatusParam, int flag) {
        super();

        this._context = context;
        utilityClass = new UtilityClass(context);
        this.flag = flag;
        //salesBeatDb = SalesBeatDb.getHelper(context);

        this.list = new ArrayList<>();
        this.userStatus = userStatusParam;

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

        int trailingSpaces, daysInPrevMonth, prevMonth, prevYear, nextMonth, nextYear;

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
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null)
                row = inflater.inflate(R.layout.custom_calender_cell, parent, false);
        }

        // Get a reference to the Day gridcell
        TextView tvDate = row.findViewById(R.id.date);
        //TextView tvDateLaod = row.findViewById(R.id.dateLoad);
        final RippleBackground rippleBackground = row.findViewById(R.id.content);

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

        //Log.e(TAG," Size: "+userStatus.size());
        if (userStatus != null && userStatus.size() > 0) {
            if (userStatus.get(date) != null && userStatus.get(date).equalsIgnoreCase("present")) {

                tvDate.setTextColor(Color.WHITE);
                tvDate.setBackgroundResource(R.drawable.green_circle);

            } else if (userStatus.get(date) != null && userStatus.get(date).equalsIgnoreCase("leave")) {

                tvDate.setTextColor(Color.WHITE);
                tvDate.setBackgroundResource(R.drawable.red_circle);

            } else if (userStatus.get(date) != null && userStatus.get(date).equalsIgnoreCase("absent")) {

                tvDate.setTextColor(Color.WHITE);
                tvDate.setBackgroundResource(R.drawable.red_circle);

            }

            rippleBackground.stopRippleAnimation();

        } else {

            if (flag == 0)
                rippleBackground.startRippleAnimation();
            else if (flag == 2)
                rippleBackground.stopRippleAnimation();

            // tvDateLaod.setBackgroundResource(R.drawable.dotted_circle);
//            Animation animRotate = AnimationUtils.loadAnimation(_context,
//                    R.anim.rotate);
//
//            tvDateLaod.startAnimation(animRotate);
        }


        // Set the Day GridCell
        tvDate.setText(theday);
        tvDate.setTag(theday + "-" + themonth + "-" + theyear);


        if (day_color[1].equals("GREY")) {
            tvDate.setTextColor(_context.getResources().getColor(R.color.textColor));
            tvDate.setVisibility(View.INVISIBLE);
            //tvDateLaod.setVisibility(View.INVISIBLE);
            rippleBackground.setVisibility(View.INVISIBLE);
        }

        if (day_color[1].equals("BLUE")) {

            tvDate.setTextColor(_context.getResources().getColor(R.color.colorAccent));
            tvDate.setBackgroundResource(R.drawable.circle_whitish);
        }

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(_context, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        getEmployeeRecordByDate(list, position);
                    }

                });


//                if (utilityClass.isInternetConnected()) {
//
//                    getEmployeeRecordByDate(list, position);
//
//                } else {
//                    Toast.makeText(_context, "You are not connected to internet", Toast.LENGTH_SHORT).show();
//                }
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

    private void getEmployeeRecordByDate(List<String> list, int position) {

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

        if (Integer.parseInt(year) > currentYear) {

            showToast();

        } else if (Integer.parseInt(year) == currentYear && Integer.parseInt(tempM) > currentMonth) {

            showToast();

        } else if (Integer.parseInt(tempM) == currentMonth && Integer.parseInt(day) > currentDay) {

            showToast();

        } else {

            Intent intent = new Intent(_context, DailyAssesmentActivity.class);
            intent.putStringArrayListExtra("list", (ArrayList<String>) list);
            intent.putExtra("pos", position);
            //((Activity) _context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            _context.startActivity(intent);


        }
    }

    private void showToast() {

        Toast.makeText(_context, "No data available", Toast.LENGTH_SHORT).show();
    }
}


