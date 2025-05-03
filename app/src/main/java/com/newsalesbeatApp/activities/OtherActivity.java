package com.newsalesbeatApp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.OtherActivityCategoryAdapter;
import com.newsalesbeatApp.sblocation.GPSLocation;


/*
 * Created by MTC on 31-07-2017.
 */

public class OtherActivity extends AppCompatActivity {

    private final int backImage[] = {R.drawable.ic_add_circle_black_24dp, R.drawable.ic_joint_work_black_24dp,
            R.drawable.ic_people_black_24dp, R.drawable.ic_menu_grey_24dp};
    RecyclerView rvOtherActivity;
    String[] otherCategoryItem_e = new String[]{"New Distributor Search",
            /*"Joint Working",*/ /*"Meeting & More",*/ "Coming Soon"};

    GPSLocation locationProvider;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.other_activity);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);
        rvOtherActivity = findViewById(R.id.rvOtherActivity);

        tvPageTitle.setText(getIntent().getStringExtra("page_title"));

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        OtherActivityCategoryAdapter adapter = new OtherActivityCategoryAdapter(this, otherCategoryItem_e, backImage);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvOtherActivity.setLayoutManager(layoutManager);
        rvOtherActivity.setAdapter(adapter);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                OtherActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    public void onBackPressed() {
        super.onBackPressed();
        //locationProvider.unregisterReceiver();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                OtherActivity.this.finishAffinity();
            }
        }
    }
}
