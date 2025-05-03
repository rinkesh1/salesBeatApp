package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyProduct;

import java.util.ArrayList;
import java.util.Locale;

/*
 * Created by MTC on 16-08-2017.
 */

public class RetailerOrderListAdapter extends RecyclerView.Adapter<RetailerOrderListAdapter.ViewHolder> {

    //private SalesBeatDb salesBeatDb;
    public ArrayList<MyProduct> myProductArrayList = new ArrayList<>();
    //String TAG = getClass().getName();
    private Context context;

    //private String did, rid, from;

    public RetailerOrderListAdapter(Context ctx, String did, String rid,
                                    ArrayList<MyProduct> myProductArrayList, String from) {
        this.context = ctx;
        this.myProductArrayList = myProductArrayList;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_confirmation_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tvSkus.setText(myProductArrayList.get(position).getMySkus());
        holder.tvBrandName.setText(myProductArrayList.get(position).getBrand());
        holder.tvBrandPrice.setText(myProductArrayList.get(position).getPrice() + "/-");
        holder.tvUnit.setText("/ " + myProductArrayList.get(position).getUnit());
        holder.edtQuantity.setText(myProductArrayList.get(position).getQuantity());

//        Log.d("TAG", "Tv BrandName: "+myProductArrayList.get(position).getBrand());
        Log.d("TAG", "Tv BrandName: "+myProductArrayList.get(position).getImageStr());

        setImageFromBase64(myProductArrayList.get(position).getImageStr(), holder.skuImage);

        holder.edtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (holder.edtQuantity.getText().length() > 0
                        && holder.edtQuantity.getText().toString().matches("[0]+")) {

                    holder.edtQuantity.getText().clear();

                } else {

                    int val = 0;
                    if (!holder.edtQuantity.getText().toString().isEmpty())
                        val = Integer.parseInt(holder.edtQuantity.getText().toString());
                    Log.e("OrderAdapter", " value:" + val);

                    //@Umesh LowerCase
                    if ((holder.tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("kg"))
                            && (val <= 1000)) {
                        myProductArrayList.get(position).setQuantity(holder.edtQuantity.getText().toString());
                    } else if ((holder.tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("pc"))
                            && (val <= 100000)) {
                        myProductArrayList.get(position).setQuantity(holder.edtQuantity.getText().toString());
                    } else if ((holder.tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("bdl"))
                            && (val <= 1000)) {
                        myProductArrayList.get(position).setQuantity(holder.edtQuantity.getText().toString());
                    } else
                    {
                        if (holder.tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("kg")) {
                            Toast.makeText(context, "You can not enter value more than 1000 Kg", Toast.LENGTH_SHORT).show();
                            holder.edtQuantity.getText().clear();
                        } else if (holder.tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("pc")) {
                            Toast.makeText(context, "You can not enter value more than 100000 Pcs", Toast.LENGTH_SHORT).show();
                            holder.edtQuantity.getText().clear();
                        } else if (holder.tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("bdl")) {
                            Toast.makeText(context, "You can not enter value more than 1000 Bdl", Toast.LENGTH_SHORT).show();
                            holder.edtQuantity.getText().clear();
                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public void setImageFromBase64(String base64String, ImageView imageView) {
        try {
            // Check if the Base64 string is null or blank
            if (base64String.equalsIgnoreCase("null") || base64String.trim().isEmpty()) {
                // Set a placeholder image for null or blank strings
                imageView.setImageResource(R.drawable.ic_noimage); // Use your placeholder image
                return;
            }

            // Remove Base64 prefix if it exists (e.g., "data:image/png;base64,")
            if (base64String.contains(",")) {
                base64String = base64String.substring(base64String.indexOf(",") + 1);
            }

            // Decode the Base64 string to bytes
            byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);

            // Convert the byte array to a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Set the Bitmap to the ImageView
            imageView.setImageBitmap(bitmap);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.ic_noimage);
        }
    }

    @Override
    public int getItemCount() {
        return myProductArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSkus, tvBrandName, tvBrandPrice, tvUnit;
        EditText edtQuantity;
        ImageView skuImage;

        public ViewHolder(View itemView) {
            super(itemView);

            tvSkus = itemView.findViewById(R.id.skuName);
            tvBrandName = itemView.findViewById(R.id.brandName);
            tvBrandPrice = itemView.findViewById(R.id.brandPrice);
            edtQuantity = itemView.findViewById(R.id.edtOrderQuantity);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            skuImage = itemView.findViewById(R.id.imgSkuIcon);
        }
    }
}
