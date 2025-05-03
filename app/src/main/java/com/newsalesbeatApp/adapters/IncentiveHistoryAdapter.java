package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.Item;

import java.util.ArrayList;

public class IncentiveHistoryAdapter extends RecyclerView.Adapter<IncentiveHistoryAdapter.MyViewHolder> {

    Context context;
    private ArrayList<Item> incentiveList;

    public IncentiveHistoryAdapter(Context context, ArrayList<Item> incentiveList) {
        this.context = context;
        this.incentiveList = incentiveList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.incentive_history_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvIncentiveDate.setText(incentiveList.get(position).getItem1());
        holder.tvIncentiveSale.setText(incentiveList.get(position).getItem2() + " " + context.getString(R.string.unitt));
        holder.tvIncentiveAmount.setText(context.getString(R.string.Rs) + " " + incentiveList.get(position).getItem3());
    }

    @Override
    public int getItemCount() {
        return incentiveList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvIncentiveDate, tvIncentiveSale, tvIncentiveAmount;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvIncentiveDate = itemView.findViewById(R.id.tvIncentiveDate);
            tvIncentiveSale = itemView.findViewById(R.id.tvIncentiveSale);
            tvIncentiveAmount = itemView.findViewById(R.id.tvIncentiveAmount);

        }
    }
}
