package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyPjp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by MTC on 15-09-2017.
 */

public class CustomCalendarAdapter2 extends BaseAdapter {


    String TAG = getClass().getName();
    private Context mContext;
    private Calendar month;
    private Calendar pmonth;
    private ArrayList<MyPjp> myPjpArrayList;
    //private View previousView;
    private List<String> dayString = new ArrayList<>();
    private int firstDay, pos = -1;
    private String selectedDateString, curentDateString;
    private int[] EXTRA_DAYS = {0, 6, 0, 1, 2, 3, 4, 5};
    //private SharedPreferences tempPref;

    //constructer
    public CustomCalendarAdapter2(Context c, Calendar monthCalendar, String date,
                                  ArrayList<MyPjp> myPjpArrayList/*, String currentMonthName*/) {
        mContext = c;
        this.myPjpArrayList = myPjpArrayList;
        //tempPref = c.getSharedPreferences(c.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);

        String[] separatedTime1 = date.split("-");
        String dy = separatedTime1[0].replaceFirst("^0*", "");
        String mn = separatedTime1[1].replaceFirst("^0*", "");
        String yr = separatedTime1[2].replaceFirst("^0*", "");

        month = monthCalendar;
        month.set(Integer.parseInt(yr), Integer.parseInt(mn) - 1, Integer.parseInt(dy));
        month.set(Calendar.DAY_OF_MONTH, 1);

        selectedDateString = date;

        //current date string
        Calendar cc = Calendar.getInstance();
        SimpleDateFormat sdff = new SimpleDateFormat("dd-MM-yyyy");
        curentDateString = sdff.format(cc.getTime());

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

        final ViewHolder viewHolder;

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.custom_calender2_cell, null);
            viewHolder = new ViewHolder();
            viewHolder.dayView = convertView.findViewById(R.id.date);
            viewHolder.llCal = convertView.findViewById(R.id.llCal);
            viewHolder.tvTownName = convertView.findViewById(R.id.tvTownName);
            viewHolder.tvDistributor = convertView.findViewById(R.id.tvDistributorName);
//            viewHolder.tvDistributorId = convertView.findViewById(R.id.tvDistributorIdPJP);
//            viewHolder.tvBeatName = convertView.findViewById(R.id.tvBeatName);
//            viewHolder.tvBeatId = convertView.findViewById(R.id.tvBeatIDPJP);
//            viewHolder.tvAssigneeEmp = convertView.findViewById(R.id.tvAssigneeEmp);
//            viewHolder.llTW = convertView.findViewById(R.id.llTW);
//            viewHolder.llBTN = convertView.findViewById(R.id.llBTN);
//            viewHolder.llDSTN = convertView.findViewById(R.id.llDSTN);
            viewHolder.imgDot = convertView.findViewById(R.id.imgDot);

            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }


        String[] separatedTime = dayString.get(position).split("-");
        String fillingDay = separatedTime[0].replaceFirst("^0*", "");
        String fillingMonth = separatedTime[1].replaceFirst("^0*", "");

        String[] separatedTime1 = curentDateString.split("-");
        String currentDay = separatedTime1[0].replaceFirst("^0*", "");
        String currentMonth = separatedTime1[1].replaceFirst("^0*", "");


        // checking whether the day is in current month or not.
        if ((Integer.parseInt(fillingDay) > 1) && (position < firstDay)) {
            // setting offdays  color.
            viewHolder.dayView.setTextColor(Color.parseColor("#424242"));
            viewHolder.dayView.setEnabled(false);
            viewHolder.dayView.setVisibility(View.VISIBLE);
            viewHolder.dayView.setBackgroundColor(Color.parseColor("#424242"));
            viewHolder.imgDot.setVisibility(View.INVISIBLE);

        } else if ((Integer.parseInt(fillingDay) < 7) && (position > 28)) {
            // setting upcoming month's day  color.
            viewHolder.dayView.setTextColor(Color.parseColor("#424242"));
            viewHolder.dayView.setEnabled(false);
            viewHolder.dayView.setVisibility(View.VISIBLE);
            viewHolder.dayView.setBackgroundColor(Color.parseColor("#424242"));
            viewHolder.imgDot.setVisibility(View.INVISIBLE);

        } else if ((Integer.parseInt(fillingDay) <= Integer.parseInt(currentDay))
                && (fillingMonth.equals(currentMonth))) {
            //setting past days color
            viewHolder.dayView.setTextColor(Color.WHITE);
            viewHolder.dayView.setEnabled(true);
            viewHolder.dayView.setBackgroundColor(Color.parseColor("#424242"));
            viewHolder.imgDot.setVisibility(View.INVISIBLE);

        } else {
            // setting curent month's days in  color.
            viewHolder.dayView.setTextColor(Color.WHITE);
            viewHolder.dayView.setEnabled(true);
        }

        //filling month value
        viewHolder.dayView.setText(fillingDay);

        //checking for PJP
        if (myPjpArrayList != null && myPjpArrayList.size() > 0) {

            boolean chkPjp = checkForPJP(fillingDay, fillingMonth);

            if (chkPjp) {

                viewHolder.tvTownName.setText(myPjpArrayList.get(pos).getTownName());
                viewHolder.tvDistributor.setText(myPjpArrayList.get(pos).getDistributorName());
                viewHolder.tvDistributorId.setText(myPjpArrayList.get(pos).getDistributor_id());
                viewHolder.tvBeatName.setText(myPjpArrayList.get(pos).getBeatName());
                viewHolder.tvBeatId.setText(myPjpArrayList.get(pos).getBeat_id());

                if (myPjpArrayList.get(pos).getAssigneeEmp() == null
                        || myPjpArrayList.get(pos).getAssigneeEmp().isEmpty()
                        || myPjpArrayList.get(pos).getAssigneeEmp().equalsIgnoreCase("null"))
                    viewHolder.tvAssigneeEmp.setText(myPjpArrayList.get(pos).getAssigneeAdmin());
                else
                    viewHolder.tvAssigneeEmp.setText(myPjpArrayList.get(pos).getAssigneeEmp());

                viewHolder.imgDot.setVisibility(View.VISIBLE);

            } else {

                viewHolder.tvTownName.setText("");
                viewHolder.tvDistributor.setText("");
                viewHolder.tvDistributorId.setText("");
                viewHolder.tvBeatName.setText("");
                viewHolder.tvBeatId.setText("");
                viewHolder.tvAssigneeEmp.setText("");
                viewHolder.dayView.setBackgroundColor(Color.parseColor("#424242"));
                viewHolder.imgDot.setVisibility(View.INVISIBLE);
            }

        }

        //marking current date
        if (dayString.get(position).equals(curentDateString) && selectedDateString.equals(curentDateString)) {

            viewHolder.dayView.setTextColor(R.color.green_like);
            viewHolder.dayView.setBackgroundResource(R.drawable.circle_white22);

        } /*else if (dayString.get(position).equals(curentDateString)) {

            viewHolder.dayView.setTextColor(R.color.red_like);
            //viewHolder.llCal.setBackgroundColor(Color.GREEN);

        } */ else {
            viewHolder.dayView.setBackgroundColor(Color.parseColor("#424242"));
        }

        viewHolder.dayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    String assinee = viewHolder.tvAssigneeEmp.getText().toString();
                    String town = viewHolder.tvTownName.getText().toString();
                    String distributor = viewHolder.tvDistributor.getText().toString();
                    String beat = viewHolder.tvBeatName.getText().toString();

                    if (!assinee.isEmpty() || !town.isEmpty() || !distributor.isEmpty() || !beat.isEmpty()) {

//                        MyPjpActivity.tvAssineeName.setText(assinee);
//                        MyPjpActivity.tvTownName.setText(town);
//                        MyPjpActivity.tvDistributorName.setText(distributor);
//                        MyPjpActivity.tvBeatName.setText(beat);
//                        MyPjpActivity.tvSelectedView.setText(dayString.get(position));
//
//                        MyPjpActivity.llPjpDetails.setVisibility(View.VISIBLE);
//                        MyPjpActivity.tvNoPjp.setVisibility(View.GONE);

                    } else {

//                        MyPjpActivity.llPjpDetails.setVisibility(View.GONE);
//                        MyPjpActivity.tvNoPjp.setVisibility(View.VISIBLE);

                        Toast.makeText(mContext, "No Pjp assigned", Toast.LENGTH_SHORT).show();

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return convertView;
    }

    private boolean checkForPJP(String fillingDay, String fillingMonth) {

        for (int i = 0; i < myPjpArrayList.size(); i++) {

            try {

                String[] pjp = myPjpArrayList.get(i).getDate().split("-");
                int pjpDay = Integer.parseInt(pjp[2]);
                int pjpMonthh = Integer.parseInt(pjp[1]);

                int fillDay = Integer.parseInt(fillingDay);
                int fillMonth = Integer.parseInt(fillingMonth);

                if ((fillDay == pjpDay) && (fillMonth == pjpMonthh)) {
                    pos = i;
                    return true;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public void refreshDays() {
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        int maxP, maxWeeknumber, calMaxP, mnthlength;
        Calendar pmonthmaxset;
        dayString.clear();
        pmonth = (Calendar) month.clone();
        // month start day. ie; sun, mon, etc
        firstDay = month.get(Calendar.DAY_OF_WEEK);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        // finding number of weeks in current month.
        maxWeeknumber = getWeekCount(month, year);//month.getActualMaximum(Calendar.WEEK_OF_MONTH);
        // allocating maximum row number for the gridview.
        mnthlength = maxWeeknumber * 7;
        maxP = getMaxP(); // previous month maximum day 31,30....
        calMaxP = maxP - (firstDay - 1);// calendar offday starting 24,25 ...
        /*
         * Calendar instance for getting a complete gridview including the three
         * month's (previous,current,next) dates.
         */
        pmonthmaxset = (Calendar) pmonth.clone();
        /*
         * setting the start date as previous month's required date.
         */
        pmonthmaxset.set(Calendar.DAY_OF_MONTH, calMaxP + 1);

        /*
         * filling calendar gridview.
         */
        Log.e("MONTHL", "===" + mnthlength + ",,," + maxWeeknumber);
        //mnthlength = 35;
        for (int n = 0; n < mnthlength; n++) {

            String itemvalue = df2.format(pmonthmaxset.getTime());
            pmonthmaxset.add(Calendar.DATE, 1);
            dayString.add(itemvalue);
        }

        notifyDataSetChanged();
    }

    // Note: 0-based month as per the rest of java.util.Calendar
    private int getWeekCount(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, month);
        return cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
//        Calendar calendar = new GregorianCalendar(year, month, 1);
//        int dayOfWeekOfStartOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
//        int extraDays = EXTRA_DAYS[dayOfWeekOfStartOfMonth];
//        int regularDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int effectiveDaysInMonth = regularDaysInMonth + extraDays;
//        return effectiveDaysInMonth / 7;
    }

    private int getMaxP() {
        int maxP;
        if (month.get(Calendar.MONTH) == month.getActualMinimum(Calendar.MONTH)) {
            pmonth.set((month.get(Calendar.YEAR) - 1),
                    month.getActualMaximum(Calendar.MONTH), 1);
        } else {
            pmonth.set(Calendar.MONTH,
                    month.get(Calendar.MONTH) - 1);
        }
        maxP = pmonth.getActualMaximum(Calendar.DAY_OF_MONTH);

        return maxP;
    }

    public String refreshCalendar() {
        String monthh = (String) android.text.format.DateFormat.format("MMMM yyyy", month);
        monthh = monthh.substring(0, 3);
        return monthh;
    }

    static class ViewHolder {

        TextView dayView;
        LinearLayout llCal;

        TextView tvTownName;
        TextView tvDistributor;
        TextView tvDistributorId;
        TextView tvBeatName;
        TextView tvBeatId;
        TextView tvAssigneeEmp;
        LinearLayout llTW;
        LinearLayout llDSTN;
        LinearLayout llBTN;

        ImageView imgDot;
    }

}
