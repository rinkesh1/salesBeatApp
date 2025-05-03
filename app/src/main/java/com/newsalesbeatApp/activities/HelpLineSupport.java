package com.newsalesbeatApp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;


/**
 * Created by MTC on 31-07-2017.
 */

public class HelpLineSupport extends AppCompatActivity {

    UtilityClass utilityClass;
    WebView helpAndSupport;
    TextView tvNotConnectedToNet;
    SharedPreferences prefSFA;
    GPSLocation locationProvider;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.helpline_support);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        helpAndSupport = findViewById(R.id.helpAndSupport);
        tvNotConnectedToNet = findViewById(R.id.tvNotConnectedToNet);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        tvPageTitle.setText("Help & Support");

        utilityClass = new UtilityClass(HelpLineSupport.this);
        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        if (utilityClass.isInternetConnected()) {

            helpAndSupport.getSettings().setJavaScriptEnabled(true);
            helpAndSupport.getSettings().setDomStorageEnabled(true);
            //String postData =  prefSFA.getString("token", "");
            //helpAndSupport.postUrl(SbAppConstants.URL_HELP, EncodingUtils.getBytes(postData, "BASE64"));
            helpAndSupport.loadUrl(SbAppConstants.URL_HELP + "/" + prefSFA.getString(getString(R.string.zone_id_key), ""));
            Log.e("HelpSupport", "===>" + helpAndSupport.getUrl());

            tvNotConnectedToNet.setVisibility(View.GONE);
            helpAndSupport.setVisibility(View.VISIBLE);

        } else {

            tvNotConnectedToNet.setVisibility(View.VISIBLE);
            helpAndSupport.setVisibility(View.GONE);
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpLineSupport.this.finish();
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
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
