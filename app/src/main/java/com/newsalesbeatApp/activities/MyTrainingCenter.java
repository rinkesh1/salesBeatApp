package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.newsalesbeatApp.R;


/*
 * Created by MTC on 31-07-2017.
 */

public class MyTrainingCenter extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.my_training_center);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        tvPageTitle.setText(getIntent().getStringExtra("page_title"));


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTrainingCenter.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        super.onBackPressed();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}

