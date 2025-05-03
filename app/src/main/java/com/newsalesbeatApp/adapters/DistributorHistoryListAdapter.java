package com.newsalesbeatApp.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.ViewDistributorDetails;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DistributorHistoryListAdapter extends RecyclerView.Adapter<DistributorHistoryListAdapter.MyViewHolder> {

    Context context;
    private SimpleDateFormat sdf, sdf2, sdf3;
    private ArrayList<ClaimHistoryItem> distributorHistoryList;
    private String imgLat;


    public DistributorHistoryListAdapter(Context ctx, ArrayList<ClaimHistoryItem> distributorHistoryList,String imgLat) {
        this.context = ctx;
        this.distributorHistoryList = distributorHistoryList;
        sdf = new SimpleDateFormat("dd MMM,yyyy", Locale.ENGLISH);
        sdf2 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        sdf3 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        this.imgLat = imgLat;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.distributor_history_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        if (distributorHistoryList.get(position).getAddedDate() != null
                && !distributorHistoryList.get(position).getAddedDate().equalsIgnoreCase("null"))
            holder.tvDateTime.setText(distributorHistoryList.get(position).getAddedDate());

        if (distributorHistoryList.get(position).getFirmName() != null
                && !distributorHistoryList.get(position).getFirmName().equalsIgnoreCase("null"))
            holder.tvFirmName.setText(distributorHistoryList.get(position).getFirmName());

        if (distributorHistoryList.get(position).getCity() != null
                && !distributorHistoryList.get(position).getCity().equalsIgnoreCase("null"))
            holder.tvTownCity.setText(distributorHistoryList.get(position).getCity());

        if (distributorHistoryList.get(position).getState() != null
                && !distributorHistoryList.get(position).getState().equalsIgnoreCase("null"))
            holder.tvStateD.setText(distributorHistoryList.get(position).getState());


        if (distributorHistoryList.get(position).getOwnerName() != null
                && !distributorHistoryList.get(position).getOwnerName().equalsIgnoreCase("null"))
            holder.tvOwnerName.setText(distributorHistoryList.get(position).getOwnerName());

        if (distributorHistoryList.get(position).getMobile1() != null
                && !distributorHistoryList.get(position).getMobile1().equalsIgnoreCase("null"))
            holder.tvMobile1.setText(distributorHistoryList.get(position).getMobile1());
        else
            holder.llMobile1.setVisibility(View.GONE);

        if (distributorHistoryList.get(position).getMobile2() != null
                && !distributorHistoryList.get(position).getMobile2().equalsIgnoreCase("null"))
            holder.tvMobile2.setText(distributorHistoryList.get(position).getMobile2());
        else
            holder.llMobile2.setVisibility(View.GONE);

        try {

            int count = 0;
            String[] temp = distributorHistoryList.get(position).getAddedDate().split(" ");
            Date date = null;
            if (temp[0].matches("([0-9]{4})-([0-9]{2})-([0-9]{2})")) {

                date = sdf2.parse(temp[0]);
                count = 1;

            } else if (temp[0].matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) {

                date = sdf3.parse(temp[0]);
                count = 2;
            }

            if (date != null)
                holder.tvAddedDate.setText(sdf.format(date) + " (" + getTimeFormate(temp, count) + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.imgCall1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phn = distributorHistoryList.get(position).getMobile1();
                if (!phn.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phn));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, "Number not available", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.imgCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phn = distributorHistoryList.get(position).getMobile2();
                if (!phn.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phn));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, "Number not available", Toast.LENGTH_SHORT).show();
                }


            }
        });

        holder.tvViewMoreD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "updated Lat: "+imgLat);
                Intent intent = new Intent(context, ViewDistributorDetails.class);
                intent.putExtra("data", distributorHistoryList.get(position));
                intent.putExtra("imgLat",imgLat);
                context.startActivity(intent);
            }
        });

        holder.tvShareNewDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDetails(position);
            }
        });

    }

    private void shareDetails(int position) {

        String text = "";
        if (distributorHistoryList.get(position).getFirmName() != null
                && !distributorHistoryList.get(position).getFirmName().equalsIgnoreCase("null"))
            text = text.concat("Distributor Name : " + distributorHistoryList.get(position).getFirmName());
        else
            text = text.concat("Distributor Name : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getFirmAddress() != null
                && !distributorHistoryList.get(position).getFirmAddress().equalsIgnoreCase("null"))
            text = text.concat("Distributor Address : " + distributorHistoryList.get(position).getFirmAddress());
        else
            text = text.concat("Distributor Address : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getCity() != null
                && !distributorHistoryList.get(position).getCity().equalsIgnoreCase("null"))
            text = text.concat("City : " + distributorHistoryList.get(position).getCity());
        else
            text = text.concat("City : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getState() != null
                && !distributorHistoryList.get(position).getState().equalsIgnoreCase("null"))
            text = text.concat("State : " + distributorHistoryList.get(position).getState());
        else
            text = text.concat("State : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getPin() != null
                && !distributorHistoryList.get(position).getPin().equalsIgnoreCase("null"))
            text = text.concat("Pincode : " + distributorHistoryList.get(position).getPin());
        else
            text = text.concat("Pincode : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getOwnerName() != null
                && !distributorHistoryList.get(position).getOwnerName().equalsIgnoreCase("null"))
            text = text.concat("Owner Name : " + distributorHistoryList.get(position).getOwnerName());
        else
            text = text.concat("Owner Name : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getMobile1() != null
                && !distributorHistoryList.get(position).getMobile1().equalsIgnoreCase("null"))
            text = text.concat("Owner Mobile No.: " + distributorHistoryList.get(position).getMobile1());
        else
            text = text.concat("Owner Mobile No.: " + "NA");

        text = text.concat("\n");


        if (distributorHistoryList.get(position).getOwnerEmail() != null
                && !distributorHistoryList.get(position).getOwnerEmail().equalsIgnoreCase("null"))
            text = text.concat("Distributor Email id : " + distributorHistoryList.get(position).getOwnerEmail());
        else
            text = text.concat("Distributor Email id : " + "NA");

        text = text.concat("\n");


        if (distributorHistoryList.get(position).getGstin() != null
                && !distributorHistoryList.get(position).getGstin().equalsIgnoreCase("null"))
            text = text.concat("Distributor GSTIN : " + distributorHistoryList.get(position).getGstin());
        else
            text = text.concat("Distributor GSTIN : " + "NA");

        text = text.concat("\n");


        if (distributorHistoryList.get(position).getFssai() != null
                && !distributorHistoryList.get(position).getFssai().equalsIgnoreCase("null"))
            text = text.concat("Distributor FSSAI : " + distributorHistoryList.get(position).getFssai());
        else
            text = text.concat("Distributor FSSAI : " + "NA");

        text = text.concat("\n");


        if (distributorHistoryList.get(position).getPan() != null
                && !distributorHistoryList.get(position).getPan().equalsIgnoreCase("null"))
            text = text.concat("Distributor PAN : " + distributorHistoryList.get(position).getPan());
        else
            text = text.concat("Distributor PAN : " + "NA");

        text = text.concat("\n");


//        if (distributorHistoryList.get(position).getMonthlyTurnOver() != null
//                && !distributorHistoryList.get(position).getMonthlyTurnOver().equalsIgnoreCase("null"))
//            text = text.concat("Monthly Turn Over : " + distributorHistoryList.get(position).getMonthlyTurnOver());
//        else
//            text = text.concat("Monthly Turn Over : " + "NA");
//
//        text = text.concat("\n");


        if (distributorHistoryList.get(position).getBeat1() != null
                && !distributorHistoryList.get(position).getBeat1().equalsIgnoreCase("null"))
            text = text.concat("Beat Name : " + distributorHistoryList.get(position).getBeat1() + ","
                    + distributorHistoryList.get(position).getBeat2());
        else
            text = text.concat("Beat Name : " + "NA");

        text = text.concat("\n");


//        if (distributorHistoryList.get(position).getNoOfShop() != null
//                && !distributorHistoryList.get(position).getNoOfShop().equalsIgnoreCase("null"))
//            text = text.concat("No. of shop in a beat : " + distributorHistoryList.get(position).getNoOfShop());
//        else
//            text = text.concat("No. of shop in a beat : " + "NA");
//
//        text = text.concat("\n");


//        if (distributorHistoryList.get(position).getInvestmentPlan() != null
//                && !distributorHistoryList.get(position).getInvestmentPlan().equalsIgnoreCase("null"))
//            text = text.concat("Investment Plan : " + distributorHistoryList.get(position).getInvestmentPlan());
//        else
//            text = text.concat("Investment Plan : " + "NA");
//
//        text = text.concat("\n");


        if (distributorHistoryList.get(position).getProduct() != null
                && !distributorHistoryList.get(position).getProduct().equalsIgnoreCase("null"))
            text = text.concat("Product Division : " + distributorHistoryList.get(position).getProduct());
        else
            text = text.concat("Product Division : " + "NA");

        text = text.concat("\n");


//        if (distributorHistoryList.get(position).getWorkingSince() != null
//                && !distributorHistoryList.get(position).getWorkingSince().equalsIgnoreCase("null"))
//            text = text.concat("Working Since : " + distributorHistoryList.get(position).getWorkingSince());
//        else
//            text = text.concat("Working Since : " + "NA");
//
//        text = text.concat("\n");


        if (distributorHistoryList.get(position).getOtherPerson() != null
                && !distributorHistoryList.get(position).getOtherPerson().equalsIgnoreCase("null"))
            text = text.concat("Other Contact Person Name : " + distributorHistoryList.get(position).getOtherPerson());
        else
            text = text.concat("Other Contact Person Name : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getOtherPersonMob() != null
                && !distributorHistoryList.get(position).getOtherPersonMob().equalsIgnoreCase("null"))
            text = text.concat("Other Contact Person Mobile : " + distributorHistoryList.get(position).getOtherPersonMob());
        else
            text = text.concat("Other Contact Person Mobile : " + "NA");

        text = text.concat("\n");

        if (distributorHistoryList.get(position).getOpDis() != null
                && !distributorHistoryList.get(position).getOpDis().equalsIgnoreCase("null"))
            text = text.concat("Opinion About Distributor : " + distributorHistoryList.get(position).getOpDis());
        else
            text = text.concat("Opinion About Distributor : " + "NA");

        text = text.concat("\n");


        if (distributorHistoryList.get(position).getRemarks() != null
                && !distributorHistoryList.get(position).getRemarks().equalsIgnoreCase("null"))
            text = text.concat("Comments : " + distributorHistoryList.get(position).getRemarks());
        else
            text = text.concat("Comments : " + "NA");

        text = text.concat("\n");

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Distributor");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(sharingIntent, "Share Distributor"));

    }

    private String getTimeFormate(String[] time, int count) {

        SimpleDateFormat sdf = null;
        Date dateObj = null;

        try {

            if (count == 1) {
                sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                dateObj = sdf.parse(time[1]);
                return new SimpleDateFormat("KK:mm a", Locale.ENGLISH).format(dateObj);

            } else if (count == 2)
                return time[1] + " " + time[2];

        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public int getItemCount() {
        return distributorHistoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFirmName, tvTownCity, tvStateD, tvOwnerName, tvMobile1,tvDateTime,
                tvMobile2, tvAddedDate, tvViewMoreD, tvShareNewDis;//,tvApprovedExpense,tvOrigin,tvDestination,tvApproved,
        LinearLayout llMobile1, llMobile2;
        private ImageView imgCall1, imgCall2;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvFirmName = itemView.findViewById(R.id.tvFirmName);
            tvTownCity = itemView.findViewById(R.id.tvTownCity);
            tvStateD = itemView.findViewById(R.id.tvStateD);
            tvOwnerName = itemView.findViewById(R.id.tvOwnerName);
            tvMobile1 = itemView.findViewById(R.id.tvMobile1);
            tvMobile2 = itemView.findViewById(R.id.tvMobile2);
            imgCall1 = itemView.findViewById(R.id.imgCall1);
            imgCall2 = itemView.findViewById(R.id.imgCall2);
            tvAddedDate = itemView.findViewById(R.id.tvAddedDate);
            tvViewMoreD = itemView.findViewById(R.id.tvViewMoreD);
            tvShareNewDis = itemView.findViewById(R.id.tvShareNewDis);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);

            llMobile1 = itemView.findViewById(R.id.llMobile1);
            llMobile2 = itemView.findViewById(R.id.llMobile2);
        }
    }
}
