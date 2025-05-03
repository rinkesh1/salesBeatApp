package com.newsalesbeatApp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.OrderConfirmationDialogAdapter;
import com.newsalesbeatApp.pojo.MyProduct;

import java.util.ArrayList;
import java.util.Locale;

/*
 * Created by Dhirendra Thakur on 13-03-2018.
 */

public class PCOrderHistoryView extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_confirmation_dialog);
        RecyclerView orderConfirmRV = findViewById(R.id.rvSkuListDialog);
        TextView tvTotalWeightInKg = findViewById(R.id.tvTotalWieghtInKg);
        TextView tvTotalBox = findViewById(R.id.tvTotalBox);
        TextView tvTotalPcs = findViewById(R.id.tvTotalPcs);
        TextView tvTotalUnit = findViewById(R.id.tvTotalUnit);
        TextView tvText = findViewById(R.id.tvText);
        LinearLayout llConfirm = findViewById(R.id.llConfirm);

        tvText.setText("SHARE");

        final String date = getIntent().getStringExtra("date");
        final String disName = getIntent().getStringExtra("distributor");
        final String retName = getIntent().getStringExtra("retailer");
        final String retAddress = getIntent().getStringExtra("retailer_address");
        final String retPhone = getIntent().getStringExtra("retailer_phone");
        final ArrayList<MyProduct> myProducts = (ArrayList<MyProduct>) getIntent().getSerializableExtra("orders");
        final OrderConfirmationDialogAdapter adapter2 = new OrderConfirmationDialogAdapter(this, myProducts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        orderConfirmRV.setLayoutManager(layoutManager);
        orderConfirmRV.setAdapter(adapter2);


        double kg = 0, box = 0, pcs = 0, total = 0;

        for (int ind = 0; ind < myProducts.size(); ind++) {

            if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("kg")) {

                kg = kg + Integer.parseInt(myProducts.get(ind).getQuantity());

            } else if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("box")) {

                box = box + Integer.parseInt(myProducts.get(ind).getQuantity());

            } else if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("pc")) {

                pcs = pcs + Double.valueOf(myProducts.get(ind).getQuantity());

            }

        }


        if (kg != 0)
            tvTotalWeightInKg.setText(String.valueOf(kg) + "Kg");
        if (box != 0)
            tvTotalBox.setText(String.valueOf(box) + "Bag");
        if (pcs != 0)
            tvTotalPcs.setText(String.valueOf(pcs) + "Pcs");

        total = kg + box + pcs;

        if (total != 0)
            tvTotalUnit.setText(String.valueOf(total) + " Unit");


        llConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareOrder(date, disName, retName, retAddress, retPhone, myProducts);
            }
        });


    }

    private void shareOrder(String date, String disName, String retName, String retAddress,
                            String retPhone, ArrayList<MyProduct> myProducts) {

        String text = "";

        text = text.concat("Distributor : " + disName);
        text = text.concat("\n");
        text = text.concat("Date-06 : " + date);
        text = text.concat("\n");
        text = text.concat("Shop Name : " + retName);
        text = text.concat("\n");
        text = text.concat("Address : " + retAddress);
        text = text.concat("\n");
        text = text.concat("Contact : " + retPhone);
        text = text.concat("\n");
        text = text.concat("\n");


        int total = 0;
        for (int pos = 0; pos < myProducts.size(); pos++) {
            MyProduct myProduct = myProducts.get(pos);

            text = text.concat(String.valueOf(pos + 1) + ". " + myProduct.getMySkus() + "    " + myProduct.getBrand() + "    "
                    + "Quantity : " + myProduct.getQuantity() + myProduct.getUnit());

            try {
                total = total + Integer.parseInt(myProduct.getQuantity());
            } catch (Exception e) {
                e.printStackTrace();
            }

            text = text.concat("\n");
        }

        text = text.concat("\n");
        text = text.concat("Total Quantity : " + String.valueOf(total) + "Kg");
        text = text.concat("\n");
        text = text.concat("------------------------------------------------");
        text = text.concat("\n");
        text = text.concat("\n");

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Today Summary");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share summary"));
    }
}

