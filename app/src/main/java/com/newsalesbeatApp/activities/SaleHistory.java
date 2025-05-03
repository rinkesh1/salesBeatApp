package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.SaleHistoryDay;
import com.newsalesbeatApp.fragments.SaleHistoryMonth;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Dhirendra Thakur on 30-03-2018.
 */

public class SaleHistory extends AppCompatActivity {

    public static UtilityClass utilityClass;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.sale_history_dialog);
        TabLayout saleHistoryTab = findViewById(R.id.saleHistoryTab);
        ViewPager saleHistoryPager = findViewById(R.id.saleHistoryPager);

        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        utilityClass = new UtilityClass(this);

        setUpSalePager(saleHistoryPager);
        saleHistoryTab.setupWithViewPager(saleHistoryPager);

        tvPageTitle.setText("Leaderboard History");


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaleHistory.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private void setUpSalePager(ViewPager saleHistoryPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SaleHistoryDay(), "DayWise");
        adapter.addFragment(new SaleHistoryMonth(), "MonthWise");

        saleHistoryPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
