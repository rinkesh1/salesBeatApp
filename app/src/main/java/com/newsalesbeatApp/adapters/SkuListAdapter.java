package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyProduct;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.util.ArrayList;

/*
 * Created by MTC on 03-08-2017.
 */

public class SkuListAdapter extends RecyclerView.Adapter<SkuListAdapter.ViewHolder> {

    SalesBeatDb salesBeatDb;
    private Context context;
    private ArrayList<MyProduct> myProductArrayList;
    private SharedPreferences tempPref;
    private String from;
    private boolean flag;

    public SkuListAdapter(Context ctx, ArrayList<MyProduct> myProductArrayList, String from) {

        this.context = ctx;
        this.myProductArrayList = myProductArrayList;
        this.from = from;
        this.flag = true;
        tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        //salesBeatDb = new SalesBeatDb(ctx);
        salesBeatDb = SalesBeatDb.getHelper(ctx);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sku_item_row, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.tvSkus.setText(myProductArrayList.get(position).getMySkus());
        holder.tvBrandName.setText(myProductArrayList.get(position).getBrand());
        holder.tvBrandPrice.setText(myProductArrayList.get(position).getPrice() + "/-");
        holder.tvUnit.setText("/ " + myProductArrayList.get(position).getUnit());

        Cursor cursor = null;

        try {

            if (flag) {

                cursor = salesBeatDb
                        .getAllDataFromSkuEntryListTable1(tempPref.getString(context.getString(R.string.dis_id_key), ""),
                                myProductArrayList.get(position).getProductId(), from);


                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    if (from.equalsIgnoreCase("stock")) {

                        String stockVal = cursor.getString(cursor.getColumnIndex("brand_qty"));
                        if (stockVal != null && !stockVal.isEmpty())
                            holder.edtQuantity.setText(stockVal);

                    } else if (from.equalsIgnoreCase("closing")) {

                        String closingVal = cursor.getString(cursor.getColumnIndex("brand_qty"));
                        if (closingVal != null && !closingVal.isEmpty())
                            holder.edtQuantity.setText(closingVal);

                    } else {

                        String disOrderVal = cursor.getString(cursor.getColumnIndex("distributor_order"));
                        if (disOrderVal != null && !disOrderVal.isEmpty())
                            holder.edtQuantity.setText(disOrderVal);
                    }
                }

            }
        } catch (Exception e) {
            Log.e("SkuListADApter", "==" + e.getMessage());
        } finally {

            if (cursor != null)
                cursor.close();
        }

        if (position == myProductArrayList.size() - 1)
            flag = false;

//        holder.edtQuantity.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//                if (holder.edtQuantity.getText().length() == 1
//                        && holder.edtQuantity.getText().toString().equalsIgnoreCase("0")) {
//                    holder.edtQuantity.getText().clear();
//                } else {
//
//                    int val = 0;
//                    if (!holder.edtQuantity.getText().toString().isEmpty())
//                        val = Integer.parseInt(holder.edtQuantity.getText().toString());
//                    if (holder.tvUnit.getText().toString().contains("Kg")
//                            && val <= 1000) {
//                        myProductArrayList.get(position).setQuantity(holder.edtQuantity.getText().toString());
//                    } else if (holder.tvUnit.getText().toString().contains("Pcs")
//                            && val <= 100000) {
//                        myProductArrayList.get(position).setQuantity(holder.edtQuantity.getText().toString());
//                    } else {
//
//                        holder.edtQuantity.getText().clear();
//                        if (holder.tvUnit.getText().toString().contains("Kg"))
//                            Toast.makeText(context, "You can not enter value more than 1000 Kg", Toast.LENGTH_SHORT).show();
//                        if (holder.tvUnit.getText().toString().contains("Pcs"))
//                            Toast.makeText(context, "You can not enter value more than 100000 Pcs", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return myProductArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSkus, tvBrandName, tvBrandPrice, tvUnit;
        EditText edtQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSkus = itemView.findViewById(R.id.skuName);
            tvBrandName = itemView.findViewById(R.id.brandName);
            tvBrandPrice = itemView.findViewById(R.id.brandPrice);
            edtQuantity = itemView.findViewById(R.id.edtQuantity);
            tvUnit = itemView.findViewById(R.id.tvUnit);
        }
    }
}
