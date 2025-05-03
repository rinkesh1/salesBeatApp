package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.DailyAssesmentFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Created by abc on 10/31/18.
 */

public class DailyAssesmentActivity extends AppCompatActivity {

    public static String strDate = "";
    private final String[] months = {"January", "February", "March",
            "April", "May", "June", "July", "August", "September",
            "October", "November", "December"};
    String TAG = "DailyAssesmentActivity";
    int pos;
    TextView tvPageTitle;
    ViewPager pagerDailyAssesment;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dailyassesment_with_pager);
        final TextView tvDateView = findViewById(R.id.tvDateView);
        ImageView nextDate = findViewById(R.id.nextDate);
        ImageView prevDate = findViewById(R.id.prevDate);
        pagerDailyAssesment = findViewById(R.id.pagerDailyAssesment);

        Toolbar mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        setSupportActionBar(mToolbar);

        tvPageTitle.setText("Daily Assesment");


        final List<String> dateList = getIntent().getStringArrayListExtra("list");
        pos = getIntent().getIntExtra("pos", 0);

        final String selectedDate = getDate(dateList, pos);


        if (!selectedDate.isEmpty())
            tvDateView.setText(getFormatedDate(selectedDate));

        initializeViewPager(selectedDate);

        prevDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos--;

                String date = getDate(dateList, pos);

                if (!date.isEmpty())
                    tvDateView.setText(getFormatedDate(date));

                initializeViewPager(date);

            }
        });

        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos++;

                String date = getDate(dateList, pos);

                if (!date.isEmpty()) {

                    tvDateView.setText(getFormatedDate(date));

                    initializeViewPager(date);

                } else {

                    pos--;
                }


            }
        });


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //MainActivity.tabPos = 1;
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                DailyAssesmentActivity.this.finish();
            }
        });

    }

    private String getFormatedDate(String selectedDate) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("dd MMM yyyy");
        Date date = null;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(selectedDate);
            //old date format to new date format
            output = outputformat.format(date);

            return output;

        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return "";
    }

    public void onBackPressed() {

        //MainActivity.tabPos = 1;
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        DailyAssesmentActivity.this.finish();
    }

    private void initializeViewPager(String date) {

        strDate = date;

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DailyAssesmentFragment());
        pagerDailyAssesment.setAdapter(null);
        pagerDailyAssesment.setAdapter(adapter);

    }

    private String getDate(List<String> dateList, int pos) {

        try {

            String[] separatedTime = dateList.get(pos).split("-");
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

            if (Integer.parseInt(year) > currentYear) {

                showToast();
                return "";

            } else if (Integer.parseInt(year) == currentYear && Integer.parseInt(tempM) > currentMonth) {

                showToast();
                return "";

            } else if (Integer.parseInt(tempM) == currentMonth && Integer.parseInt(day) > currentDay) {

                showToast();
                return "";

            } else {

                String date = year + "-" + tempM + "-" + temp2;

                return date;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private void showToast() {

        Toast.makeText(DailyAssesmentActivity.this, "No data available", Toast.LENGTH_SHORT).show();
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
