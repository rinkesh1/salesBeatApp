package com.newsalesbeatApp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.TownList2;

public class ClosingActivity extends AppCompatActivity {


    Fragment fragment;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.closing_activity);

//        ImageView imgBack = findViewById(R.id.imgBack);
//        TextView tvPageTitle = findViewById(R.id.pageTitle);
//
//        tvPageTitle.setText(getIntent().getStringExtra("page_title"));

//        locationProvider = new GPSLocation(this);
//        //check gps status if on/off
//        locationProvider.checkGpsStatus();

//        imgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //locationProvider.unregisterReceiver();
//                ClosingActivity.this.finish();
//                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
//            }
//        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        fragment = new TownList2();
        ft.replace(R.id.frmClosing, fragment);
        ft.commit();
    }
}
