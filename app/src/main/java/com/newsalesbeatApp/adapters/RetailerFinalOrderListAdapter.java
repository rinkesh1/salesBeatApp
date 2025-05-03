package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyProduct;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Dhirendra Thakur on 18-01-2018.
 */

public class RetailerFinalOrderListAdapter extends RecyclerView.Adapter<RetailerFinalOrderListAdapter.MyViewHolder> {

    Context context;
    private List<String> distributorNameList;
    private ArrayList<ArrayList<MyProduct>> finalOrderList;

    public RetailerFinalOrderListAdapter(Context ctx, List<String> distributorNameList,
                                         ArrayList<ArrayList<MyProduct>> finalOrderList) {
        this.context = ctx;
        this.distributorNameList = distributorNameList;
        this.finalOrderList = finalOrderList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.final_order_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvDistributorName.setText(distributorNameList.get(position));
        OrderConfirmationDialogAdapter orderConfirmationDialogAdapter = new OrderConfirmationDialogAdapter(context, finalOrderList.get(position));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        holder.rvFinalOrderList.setLayoutManager(layoutManager);
        holder.rvFinalOrderList.setAdapter(orderConfirmationDialogAdapter);

    }

    @Override
    public int getItemCount() {
        return distributorNameList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RecyclerView rvFinalOrderList;
        TextView tvDistributorName;

        public MyViewHolder(View itemView) {
            super(itemView);
            rvFinalOrderList = (RecyclerView) itemView.findViewById(R.id.rvFinalOrderList);
            tvDistributorName = (TextView) itemView.findViewById(R.id.tvDistributorNameFl);
        }
    }
}
