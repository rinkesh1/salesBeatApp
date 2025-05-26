package com.newsalesbeatApp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.OrderDetailsAdapter;
import com.newsalesbeatApp.pojo.SkuItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderDetails extends AppCompatActivity {

    String TAG = "OrderDetails";

    RecyclerView rvOrderDetailList;
    ArrayList<SkuItem> orderDetailList;
    OrderDetailsAdapter orderDetailsAdapter;
    Button btnShareOD;
    String name, empName, empMob, date;
    TextView tvTitle, tvTotal;
    LinearLayout llTotalOD;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_details);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//            window.setStatusBarColor(Color.TRANSPARENT); // Optional
//        }

        rvOrderDetailList = findViewById(R.id.rvOrderDetails);
        tvTotal = findViewById(R.id.tvTotalOD);
        llTotalOD = findViewById(R.id.llTotalOD);

        btnShareOD = findViewById(R.id.btnShareOD);

        ImageView imgBack = findViewById(R.id.imgBack);
        tvTitle = findViewById(R.id.pageTitle);

        String from = getIntent().getStringExtra("from");
        String nameF = getIntent().getStringExtra("name");
        String empF = getIntent().getStringExtra("empName");
        String empFMob = getIntent().getStringExtra("empMob");
        date = getIntent().getStringExtra("date");

        orderDetailList = (ArrayList<SkuItem>) getIntent().getSerializableExtra("list");


        if (from.equalsIgnoreCase("retailer")) {

            tvTitle.setText("Retailer Order Details");
            name = "Retailer Name: " + nameF;
            empName = "Employee Name: " + empF;
            empMob = "Employee Mobile: " + empFMob;

            calcuLateTotal();

            llTotalOD.setVisibility(View.VISIBLE);
        }


        if (from.equalsIgnoreCase("distributor")) {
            tvTitle.setText("Distributor Order Details");
            name = "Distributor Name: " + nameF;
            empName = "Employee Name: " + empF;
            empMob = "Employee Mobile: " + empFMob;

            calcuLateTotal();

            llTotalOD.setVisibility(View.VISIBLE);

        }


        if (from.equalsIgnoreCase("distributor_s")) {
            tvTitle.setText("Distributor Stock Details");
            name = "Distributor Name: " + nameF;
            empName = "Employee Name: " + empF;
            empMob = "Employee Mobile: " + empFMob;

            calcuLateTotal();

            llTotalOD.setVisibility(View.VISIBLE);
        }

        if (from.equalsIgnoreCase("dis_closing")) {
            tvTitle.setText("Distributor Closing Details");
            name = "Distributor Name: " + nameF;
            empName = "Employee Name: " + empF;
            empMob = "Employee Mobile: " + empFMob;

            calcuLateTotal();

            llTotalOD.setVisibility(View.VISIBLE);
        }

        orderDetailsAdapter = new OrderDetailsAdapter(this, orderDetailList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvOrderDetailList.setLayoutManager(layoutManager);
        rvOrderDetailList.setAdapter(orderDetailsAdapter);
        orderDetailsAdapter.notifyDataSetChanged();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OrderDetails.this.finish();
            }
        });

        btnShareOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareHistory();
            }
        });

    }

    private void calcuLateTotal() {

        double sale = 0;

        if (orderDetailList != null && orderDetailList.size() > 0) {

            for (int i = 0; i < orderDetailList.size(); i++) {

                String strSale = orderDetailList.get(i).getOpening();
                String cFactor = orderDetailList.get(i).getConversionFactor();
                String weight = orderDetailList.get(i).getweight(); //@Umesh 16-07-2022
                double s = 0;

                try {
                    s = Double.valueOf(strSale);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //sale = sale + s / Double.valueOf(cFactor);

                sale = sale + s * Double.valueOf(weight); //@Umesh 16-07-2022
            }

            tvTotal.setText(new DecimalFormat("##.##").format(sale));
        }

    }

    private void shareHistory() {

        String shareText = "";
        shareText = shareText.concat("\n");
        shareText = shareText.concat("\n");
        shareText = shareText.concat("*" + tvTitle.getText().toString() + "*");
        shareText = shareText.concat("\n");
        shareText = shareText.concat("\n");
        shareText = shareText.concat(name);
        shareText = shareText.concat("\n");
        shareText = shareText.concat(empName);
        shareText = shareText.concat("\n");
        shareText = shareText.concat(empMob);
        shareText = shareText.concat("\n");
        shareText = shareText.concat("Date: " + date);
        shareText = shareText.concat("\n");
        shareText = shareText.concat("-----------------------------------------");
        shareText = shareText.concat("\n");
        shareText = shareText.concat("SKU        Qty       Unit");
        shareText = shareText.concat("\n");
        shareText = shareText.concat("-----------------------------------------");
        shareText = shareText.concat("\n");
        for (int pos = 0; pos < orderDetailList.size(); pos++) {

            shareText = shareText.concat(orderDetailList.get(pos).getSku() /*+ "         " + orderDetailList.get(pos).getPrice()*/
                    + "        " + orderDetailList.get(pos).getOpening() + "       " + "/" + orderDetailList.get(pos).getUnit());

            shareText = shareText.concat("\n");
        }

        shareText = shareText.concat("\n");
        shareText = shareText.concat("\n");
        shareText = shareText.concat("\n");

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SUMMARY");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(sharingIntent, "Share summary"));

    }
}
