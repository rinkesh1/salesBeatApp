package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.CatalogList;
import com.newsalesbeatApp.sblocation.GPSLocation;


/*
 * Created by MTC on 31-07-2017.
 */

public class Documets extends AppCompatActivity {
    Fragment fragment;
    GPSLocation locationProvider;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.documents);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        tvPageTitle.setText(getIntent().getStringExtra("page_title"));

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                Documets.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        fragment = new CatalogList();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    public void onBackPressed() {
        super.onBackPressed();
        //locationProvider.unregisterReceiver();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
