package com.newsalesbeatApp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.PartyOutstandingFragment;
import com.newsalesbeatApp.pojo.DistrebutorItem;
import com.newsalesbeatApp.pojo.Item;

import java.util.ArrayList;

/*
 * Created by abc on 1/4/19.
 */

public class PartyOutStandingActivity extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.partyoutstanding);
        LinearLayout llCall = findViewById(R.id.llCall);
        LinearLayout llCancel = findViewById(R.id.llCancelDialog);
        TabLayout tabPartOutStanding = findViewById(R.id.tabPartyOutStanding);
        ViewPager pagerPartOutStanding = findViewById(R.id.pagerPartOutStanding);
        TextView tvPartName = findViewById(R.id.tvPartyName);

        //set up custom toolbar
        Toolbar mToolbar = findViewById(R.id.toolbar_s);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        tvPageTitle.setText("Party Outstanding Details");
        setSupportActionBar(mToolbar);

        final ArrayList<DistrebutorItem> distrebutorItemList = (ArrayList<DistrebutorItem>) getIntent().getSerializableExtra("disList");
        ArrayList<Item> billList = (ArrayList<Item>) getIntent().getSerializableExtra("partyVal");
        final int pos = getIntent().getIntExtra("pos", 0);

        Log.e("********", " " + distrebutorItemList.size() + "  " + pos + "  " + billList.size());

        tvPartName.setText(distrebutorItemList.get(pos).getDistrebutorName());
        setUpViewPager(pagerPartOutStanding, billList);
        tabPartOutStanding.setupWithViewPager(pagerPartOutStanding);

        llCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phn = distrebutorItemList.get(pos).getDistrebutor_phone();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phn));
                if (ActivityCompat.checkSelfPermission(PartyOutStandingActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
            }
        });

        llCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PartyOutStandingActivity.this.finish();
            }
        });
    }

    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private void setUpViewPager(ViewPager partyViewPager, ArrayList<Item> billList) {

        PartyOutStandingPagerAdapter adapter = new PartyOutStandingPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < billList.size(); i++) {
            adapter.addFragment(new PartyOutstandingFragment(), "Bill" + (i + 1), billList.get(i));
        }

        partyViewPager.setAdapter(adapter);
        partyViewPager.setOffscreenPageLimit(adapter.getCount());
    }

    private class PartyOutStandingPagerAdapter extends FragmentPagerAdapter {

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> fragmentTitle = new ArrayList<>();

        public PartyOutStandingPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title, Item item) {

            Bundle bundle = new Bundle();
            bundle.putSerializable("itemVal", item);

            fragment.setArguments(bundle);

            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @Override
        public int getItemPosition(Object object) {
            return FragmentPagerAdapter.POSITION_UNCHANGED;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
