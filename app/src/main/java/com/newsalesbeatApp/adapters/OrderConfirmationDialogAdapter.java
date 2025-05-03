package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyProduct;

import java.util.ArrayList;

/*
 * Created by MTC on 11-09-2017.
 */

public class OrderConfirmationDialogAdapter extends RecyclerView.Adapter<OrderConfirmationDialogAdapter.ViewHolder> {

    private Context context;
    private ArrayList<MyProduct> myProductArrayList;

    public OrderConfirmationDialogAdapter(Context ctx, ArrayList<MyProduct> myProductArrayList) {
        this.context = ctx;
        this.myProductArrayList = myProductArrayList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public OrderConfirmationDialogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_confirmation_dialog_row, parent, false);
        return new OrderConfirmationDialogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderConfirmationDialogAdapter.ViewHolder holder, final int position) {
        holder.tvSkus.setText(myProductArrayList.get(position).getMySkus());
        holder.tvBrandName.setText(myProductArrayList.get(position).getBrand());
        holder.tvBrandPrice.setText(myProductArrayList.get(position).getPrice() + "/-");
        holder.tvUnit.setText("/ " + myProductArrayList.get(position).getUnit());
        holder.tvQuantity.setText(myProductArrayList.get(position).getQuantity());

    }

    @Override
    public int getItemCount() {
        return myProductArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSkus, tvBrandName, tvBrandPrice, tvUnit;
        TextView tvQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSkus = (TextView) itemView.findViewById(R.id.skuName);
            tvBrandName = (TextView) itemView.findViewById(R.id.brandName);
            tvBrandPrice = (TextView) itemView.findViewById(R.id.brandPrice);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvOrderQuantity);
            tvUnit = (TextView) itemView.findViewById(R.id.tvUnit);
        }
    }
}

