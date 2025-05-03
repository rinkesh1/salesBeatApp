package com.newsalesbeatApp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sblocation.GPSLocation;

/*
 * Created by Dhirendra Thakur on 21-12-2017.
 */

public class PromotionActivity extends AppCompatActivity {

    GPSLocation locationProvider;
    WebView myWebView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.promotion_layout);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);
        TextView tvTerms = findViewById(R.id.tvTerms);
        ImageView imgMoreInfo = findViewById(R.id.imgMoreInfo);
        myWebView = findViewById(R.id.myWebView);

        tvPageTitle.setText("Campaign");

        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();

        final String url = getIntent().getStringExtra("url");
        String content = getIntent().getStringExtra("content");
        String strSlideStatus = getIntent().getStringExtra("intSlideStatus");

//        if (strSlideStatus.equals("0")) {
//            Glide.with(PromotionActivity.this).load(url).into(imgMoreInfo);
//        } else {
        imgMoreInfo.setImageURI(Uri.parse(url));
//        }

        tvTerms.setText(Html.fromHtml(content));
        myWebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                PromotionActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        imgMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PromotionActivity.this, FullImageActivity.class);
                intent.putExtra("url", url);
                startActivity(intent);
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
}

