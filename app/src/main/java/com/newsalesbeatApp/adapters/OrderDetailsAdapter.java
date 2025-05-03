package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.SkuItem;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyHolder> {

    Context context;
    private List<SkuItem> orderDetailList;

    public OrderDetailsAdapter(Context ctx, List<SkuItem> orderDetailList) {
        this.context = ctx;
        this.orderDetailList = orderDetailList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_details_row, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        holder.tvSku.setText(orderDetailList.get(position).getSku());
        holder.tvQty.setText(orderDetailList.get(position).getOpening());
        holder.tvPrice.setText(orderDetailList.get(position).getPrice());
        holder.tvPrice.setVisibility(View.INVISIBLE);
        //holder.tvUnit.setText("/Kg");//+orderDetailList.get(position).getUnit());
        //@Umesh 16-07-2022
        holder.tvUnit.setText(orderDetailList.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView tvSku, tvPrice, tvQty, tvUnit;

        public MyHolder(View itemView) {
            super(itemView);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvUnit = itemView.findViewById(R.id.tvUnitt);
        }
    }
}
