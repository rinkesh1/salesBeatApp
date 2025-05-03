package com.newsalesbeatApp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.DetailedListAdapter;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.pojo.SkuItem;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Dhirendra Thakur on 13-04-2018.
 */

public class WorkDetails extends AppCompatActivity implements NetworkChangeInterface {

    DetailedListAdapter detailedListAdapter;
    List<SkuItem> skuItemList;
    List<RetailerItem> retailerItemList;
    List<RetailerItem> newRetailerItemList;
    ArrayList<String> beatList;
    ArrayList<String> beatVList;
    List<Item> fullDayActivityList;
    List<String> jointWorkingWith;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.work_details);
        RecyclerView detailedList = findViewById(R.id.detailedList);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvTitle = findViewById(R.id.pageTitle);


        String type = getIntent().getStringExtra("type");
        if (type.equalsIgnoreCase("L"))
            skuItemList = (List<SkuItem>) getIntent().getSerializableExtra("list");

        if (type.equalsIgnoreCase("T"))
            retailerItemList = (List<RetailerItem>) getIntent().getSerializableExtra("list");

        if (type.equalsIgnoreCase("BA"))
            beatList = getIntent().getStringArrayListExtra("list");

        if (type.equalsIgnoreCase("BV"))
            beatVList = getIntent().getStringArrayListExtra("list");

        if (type.equalsIgnoreCase("OA")) {

            tvTitle.setText("Other Activity");
            fullDayActivityList = (List<Item>) getIntent().getSerializableExtra("list");
            jointWorkingWith = (List<String>) getIntent().getSerializableExtra("empList");
        }

        if (type.equalsIgnoreCase("NC"))
            newRetailerItemList = (List<RetailerItem>) getIntent().getSerializableExtra("list");

        try {

            if (type.equalsIgnoreCase("L")) {
                detailedListAdapter = new DetailedListAdapter(WorkDetails.this, skuItemList, type);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WorkDetails.this);
                detailedList.setLayoutManager(layoutManager);
                detailedList.setAdapter(detailedListAdapter);
            }

        } catch (Exception e) {

        }


        try {
            if (type.equalsIgnoreCase("T")) {
                detailedListAdapter = new DetailedListAdapter(WorkDetails.this, retailerItemList, type);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WorkDetails.this);
                detailedList.setLayoutManager(layoutManager);
                detailedList.setAdapter(detailedListAdapter);
            }

        } catch (Exception e) {

        }


        try {

            if (type.equalsIgnoreCase("BA")) {
                detailedListAdapter = new DetailedListAdapter(WorkDetails.this, beatList, type);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WorkDetails.this);
                detailedList.setLayoutManager(layoutManager);
                detailedList.setAdapter(detailedListAdapter);
            }

        } catch (Exception e) {

        }


        try {

            if (type.equalsIgnoreCase("BV")) {
                detailedListAdapter = new DetailedListAdapter(WorkDetails.this, beatVList, type);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WorkDetails.this);
                detailedList.setLayoutManager(layoutManager);
                detailedList.setAdapter(detailedListAdapter);
            }

        } catch (Exception e) {

        }


        try {

            if (type.equalsIgnoreCase("OA")) {
                detailedListAdapter = new DetailedListAdapter(WorkDetails.this, fullDayActivityList, jointWorkingWith, type, "");
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WorkDetails.this);
                detailedList.setLayoutManager(layoutManager);
                detailedList.setAdapter(detailedListAdapter);
            }

        } catch (Exception e) {

        }


        try {

            if (type.equalsIgnoreCase("NC")) {
                detailedListAdapter = new DetailedListAdapter(WorkDetails.this, newRetailerItemList, type);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WorkDetails.this);
                detailedList.setLayoutManager(layoutManager);
                detailedList.setAdapter(detailedListAdapter);
            }

        } catch (Exception e) {

        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                WorkDetails.this.finish();

            }
        });
    }

    public void onBackPressed() {
        //overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        WorkDetails.this.finish();
    }

    @Override
    public void connectionChange(boolean status) {

    }
}
