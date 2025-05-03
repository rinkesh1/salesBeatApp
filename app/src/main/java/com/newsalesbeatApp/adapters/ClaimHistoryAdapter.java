package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/*
 * Created by Dhirendra Thakur on 11-01-2018.
 */

public class ClaimHistoryAdapter extends RecyclerView.Adapter<ClaimHistoryAdapter.MyViewHolder> {

    Context context;

    ArrayList<ClaimHistoryItem> claimHistories;

    public ClaimHistoryAdapter(Context ctx, ArrayList<ClaimHistoryItem> claimHistories) {
        this.context = ctx;
        this.claimHistories = claimHistories;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.claim_history_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        try {

            String[] date = claimHistories.get(position).getDate().split(" ");
            String strDate = date[0];

            SimpleDateFormat month_date = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String month_name = "";
            try {
                Date dater = sdf.parse(strDate);
                month_name = month_date.format(dater);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (holder.tvOrderTakenDate.getText().toString().isEmpty()
                    || !holder.tvOrderTakenDate.getText().toString().contains(strDate)) {

                holder.tvOrderTakenDate.setText(month_name);
                holder.tvOrderTakenDate.setVisibility(View.VISIBLE);

            } else {
                holder.tvOrderTakenDate.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {
            e.getMessage();
        }

        if (claimHistories.get(position).getClaimType().equalsIgnoreCase("null")
                || claimHistories.get(position).getClaimType().isEmpty()) {
            holder.tvCLaimType.setText("NA");
        } else {
            holder.tvCLaimType.setText(claimHistories.get(position).getClaimType());
        }

        if (claimHistories.get(position).getSettled().equalsIgnoreCase("null")
                || claimHistories.get(position).getSettled().isEmpty()) {
            holder.tvSettled.setText("NA");
        } else {
            holder.tvSettled.setText(claimHistories.get(position).getSettled());
        }

        if (claimHistories.get(position).getDaType().equalsIgnoreCase("null")
                || claimHistories.get(position).getDaType().isEmpty()) {
            holder.tvDaType.setText("NA");
        } else {
            holder.tvDaType.setText(claimHistories.get(position).getDaType());
        }

        if (claimHistories.get(position).getExpense().equalsIgnoreCase("null")
                || claimHistories.get(position).getExpense().isEmpty()) {
            holder.tvExpense.setText("Rs.00");
        } else {
            holder.tvExpense.setText("Rs." + claimHistories.get(position).getExpense());
        }

        if (claimHistories.get(position).getApprovedExpense().equalsIgnoreCase("null")
                || claimHistories.get(position).getApprovedExpense().isEmpty()) {
            holder.tvApprovedExpense.setText("Rs.00");
        } else {
            holder.tvApprovedExpense.setText("Rs." + claimHistories.get(position).getApprovedExpense());
        }

        if (claimHistories.get(position).getOrigin().equalsIgnoreCase("null")
                || claimHistories.get(position).getOrigin().isEmpty()) {
            holder.tvOrigin.setText("NA");
        } else {
            holder.tvOrigin.setText(claimHistories.get(position).getOrigin());
        }

        if (claimHistories.get(position).getDestination().equalsIgnoreCase("null")
                || claimHistories.get(position).getDestination().isEmpty()) {
            holder.tvDestination.setText("NA");
        } else {
            holder.tvDestination.setText(claimHistories.get(position).getDestination());
        }

        if (claimHistories.get(position).getKmsTravel().equalsIgnoreCase("null")
                || claimHistories.get(position).getKmsTravel().isEmpty()) {
            holder.tvKmsTravelled.setText("NA");
        } else {
            holder.tvKmsTravelled.setText(claimHistories.get(position).getKmsTravel());
        }

        if (claimHistories.get(position).getRemarks().equalsIgnoreCase("null")
                || claimHistories.get(position).getRemarks().isEmpty()) {
            holder.tvRemarks.setText("NA");
        } else {
            holder.tvRemarks.setText(claimHistories.get(position).getRemarks());
        }

        if (claimHistories.get(position).getApproved().equalsIgnoreCase("null")
                || claimHistories.get(position).getApproved().isEmpty()) {
            holder.tvApproved.setText("NA");
        } else {
            holder.tvApproved.setText(claimHistories.get(position).getApproved());
        }

        if (!claimHistories.get(position).getClaimType().equalsIgnoreCase("ta")) {

            holder.llOrigin.setVisibility(View.GONE);
            holder.llDestination.setVisibility(View.GONE);
            holder.llKmsTravel.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return claimHistories.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCLaimType, tvRemarks, tvKmsTravelled, tvSettled, tvDaType,
                tvExpense, tvApprovedExpense, tvOrigin, tvDestination, tvApproved, tvOrderTakenDate;

        LinearLayout llOrigin, llDestination, llKmsTravel;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCLaimType = itemView.findViewById(R.id.tvClaimType);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            tvKmsTravelled = itemView.findViewById(R.id.tvKmsTravel);
            tvSettled = itemView.findViewById(R.id.tvSettled);
            tvDaType = itemView.findViewById(R.id.tvDaType);
            tvExpense = itemView.findViewById(R.id.tvExpense);
            tvApprovedExpense = itemView.findViewById(R.id.tvApprovedExpense);
            tvOrigin = itemView.findViewById(R.id.tvOrigin);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvApproved = itemView.findViewById(R.id.tvApproved);
            tvOrderTakenDate = itemView.findViewById(R.id.tvOrderTakenDate);
            llOrigin = itemView.findViewById(R.id.llOrigin);
            llDestination = itemView.findViewById(R.id.llDestination);
            llKmsTravel = itemView.findViewById(R.id.llKmsTravel);
        }
    }
}
