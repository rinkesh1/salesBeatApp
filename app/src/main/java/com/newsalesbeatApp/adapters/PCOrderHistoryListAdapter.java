package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.PCOrderHistoryView;
import com.newsalesbeatApp.pojo.PCOrderHistoryItem;

import java.util.ArrayList;

/*
 * Created by Dhirendra Thakur on 12-03-2018.
 */

public class PCOrderHistoryListAdapter extends RecyclerView.Adapter<PCOrderHistoryListAdapter.MyViewholder> {

    private Context mContext;
    private ArrayList<PCOrderHistoryItem> historyItems;
    private boolean flag = false;

    public PCOrderHistoryListAdapter(Context context, ArrayList<PCOrderHistoryItem> list) {
        this.mContext = context;
        this.historyItems = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.order_history_row, parent, false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewholder holder, final int position) {

        holder.tvDistributorName.setText(historyItems.get(position).getDisName());
        holder.tvRetailerName.setText(historyItems.get(position).getRetName());
        holder.tvOrderType.setText(historyItems.get(position).getOrderType());

        holder.tvOrderTakenDate.setVisibility(View.GONE);

        if (flag)
            holder.checkB.setVisibility(View.VISIBLE);

        final String finalDisName = historyItems.get(position).getDisName();
        final String finalRetName = historyItems.get(position).getRetName();
        final String finalRetPhone = historyItems.get(position).getRetPhone();
        final String finalRetAddress = historyItems.get(position).getRetAddress();

        holder.btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, PCOrderHistoryView.class);
                intent.putExtra("date", historyItems.get(position).getTakenDate());
                intent.putExtra("distributor", finalDisName);
                intent.putExtra("retailer", finalRetName);
                intent.putExtra("retailer_address", finalRetAddress);
                intent.putExtra("retailer_phone", finalRetPhone);
                intent.putExtra("orders", historyItems.get(position).getMyOrderHistoryList());
                mContext.startActivity(intent);
            }
        });

        holder.cvFullView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                holder.checkB.setChecked(true);
                holder.checkB.setVisibility(View.VISIBLE);
                //PCOrderHistory.chbSAll.setVisibility(View.VISIBLE);

                flag = true;
                notifyDataSetChanged();
                return false;
            }
        });

        if (flag) {

            holder.cvFullView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.checkB.setChecked(true);
                    holder.checkB.setVisibility(View.VISIBLE);
                    flag = true;
                    notifyDataSetChanged();
                }
            });
        }


        holder.checkB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    holder.checkB.setChecked(true);
                else
                    holder.checkB.setChecked(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public class MyViewholder extends RecyclerView.ViewHolder {

        TextView tvOrderTakenDate, tvDistributorName, tvRetailerName, tvOrderType;
        Button btnViewHistory;
        CardView cvFullView;
        CheckBox checkB;

        public MyViewholder(View itemView) {
            super(itemView);
            tvDistributorName = (TextView) itemView.findViewById(R.id.tvDistributorNameH);
            tvRetailerName = (TextView) itemView.findViewById(R.id.tvRetailerNameH);
            tvOrderType = (TextView) itemView.findViewById(R.id.tvOrderTypeH);
            tvOrderTakenDate = (TextView) itemView.findViewById(R.id.tvOrderTakenDate);
            btnViewHistory = (Button) itemView.findViewById(R.id.btnViewOrder);
            cvFullView = (CardView) itemView.findViewById(R.id.cvFullView);
            checkB = (CheckBox) itemView.findViewById(R.id.checkb);
        }
    }
}
