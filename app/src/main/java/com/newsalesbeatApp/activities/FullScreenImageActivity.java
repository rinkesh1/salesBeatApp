package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.FullScreenImageAdapter;
import com.newsalesbeatApp.customview.TouchImageView;

/**
 * Created by Dhirendra Thakur on 15-01-2018.
 */

public class FullScreenImageActivity extends AppCompatActivity {
    ViewPager fullScreenImagePager;
    TouchImageView imgProductView;

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.full_screen_image_layout);
        fullScreenImagePager = (ViewPager) findViewById(R.id.fullScreenImagePager);
        imgProductView = (TouchImageView) findViewById(R.id.imgProductView);

        //set up custom toolbar
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar3);
        ImageView imgBack = (ImageView) mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = (TextView) mToolbar.findViewById(R.id.pageTitle);


        tvPageTitle.setText("Product View");
        setSupportActionBar(mToolbar);


        imgProductView.setVisibility(View.GONE);


        int position = getIntent().getIntExtra("Position", 0);
        String[] filepath = (String[]) getIntent().getSerializableExtra("image_list");

        FullScreenImageAdapter fullScreenImageAdapter = new FullScreenImageAdapter(FullScreenImageActivity.this,
                filepath);

        fullScreenImagePager.setAdapter(fullScreenImageAdapter);

        fullScreenImagePager.setCurrentItem(position);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FullScreenImageActivity.this.finish();
            }
        });
    }

    public void onBackPressed() {
        FullScreenImageActivity.this.finish();
    }
}

