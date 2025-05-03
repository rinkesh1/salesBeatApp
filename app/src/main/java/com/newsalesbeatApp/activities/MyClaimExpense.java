package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.ClaimFragment;
import com.newsalesbeatApp.sblocation.GPSLocation;


/*
 * Created by MTC on 31-07-2017.
 */

public class MyClaimExpense extends AppCompatActivity {

    Fragment fragment;
    GPSLocation locationProvider;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.myclaim_expense);
        ImageView imgBack = (ImageView) findViewById(R.id.imgBack);
        TextView tvPageTitle = (TextView) findViewById(R.id.pageTitle);

        tvPageTitle.setText(getIntent().getStringExtra("page_title"));

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                MyClaimExpense.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        fragment = new ClaimFragment();
        ft.replace(R.id.myClaimFragment, fragment);
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

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == 1000) {
//            if(resultCode == Activity.RESULT_OK){
//                //String result=data.getStringExtra("result");
//
//                //GPSLocation.builder1 = null;
//            } if (resultCode == Activity.RESULT_CANCELED) {
//                //GPSLocation.builder1 = null;
//                MyClaimExpense.this.finishAffinity();
//            }
//        }
//    }
}

