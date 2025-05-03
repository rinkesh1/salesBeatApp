package com.newsalesbeatApp.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.TouchImageView;

/*
 * Created by Dhirendra Thakur on 15-01-2018.
 */

public class FullImageActivity extends AppCompatActivity {

    TouchImageView imgProductView;
    ViewPager fullScreenImagePager;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.full_screen_image_layout);
        fullScreenImagePager = (ViewPager) findViewById(R.id.fullScreenImagePager);
        imgProductView = (TouchImageView) findViewById(R.id.imgProductView);

        fullScreenImagePager.setVisibility(View.GONE);

        String url = getIntent().getStringExtra("url");

        //set up custom toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        ImageView imgBack = (ImageView) mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = (TextView) mToolbar.findViewById(R.id.pageTitle);
        tvPageTitle.setText("Campaign Banner View");
        setSupportActionBar(mToolbar);

        imgProductView.setImageURI(Uri.parse(url));


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullImageActivity.this.finish();
            }
        });
    }

    public void onBackPressed() {
        FullImageActivity.this.finish();
    }
}
