package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.MyProduct;

import java.util.ArrayList;
import java.util.Locale;

public class RetailerOrderListAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private ArrayList<MyProduct> myProductArrayList;
    private String headerText;

   /* public RetailerOrderListAdapterNew(Context context, ArrayList<MyProduct> myProductArrayList, String headerText) {
        this.context = context;
        this.myProductArrayList = myProductArrayList;
        this.headerText = headerText;
    }
*/
    public RetailerOrderListAdapterNew(Context ctx, String did, String rid,
                                    ArrayList<MyProduct> myProductArrayList, String from, String headerText) {
        this.context = ctx;
        this.myProductArrayList = myProductArrayList;
        this.headerText = headerText;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(context).inflate(R.layout.sticky_header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.order_confirmation_row, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Log.e("Item",myProductArrayList.get(position).getBrand());
//        Log.e("Item",position+"");
//        Log.e("Item",myProductArrayList.get(position).getMySkus());
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ((HeaderViewHolder) holder).bind(myProductArrayList.get(position).getBrand());
        } else {
            ((ItemViewHolder) holder).bind(myProductArrayList.get(position));
//            ((ItemViewHolder) holder).edtQuantity

            ((ItemViewHolder) holder).edtQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (((ItemViewHolder) holder).edtQuantity.getText().length() > 0
                            && ((ItemViewHolder) holder).edtQuantity.getText().toString().matches("[0]+")) {

                        ((ItemViewHolder) holder).edtQuantity.getText().clear();

                    } else {

                        int val = 0;
                        if (!((ItemViewHolder) holder).edtQuantity.getText().toString().isEmpty())
                            val = Integer.parseInt(((ItemViewHolder) holder).edtQuantity.getText().toString());
                        Log.e("OrderAdapter", " value:" + val);

                        //@Umesh LowerCase
                        if ((((ItemViewHolder) holder).tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("kg"))
                                && (val <= 1000)) {
                            myProductArrayList.get(position).setQuantity(((ItemViewHolder) holder).edtQuantity.getText().toString());
                        } else if ((((ItemViewHolder) holder).tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("pc"))
                                && (val <= 100000)) {
                            myProductArrayList.get(position).setQuantity(((ItemViewHolder) holder).edtQuantity.getText().toString());
                        } else if ((((ItemViewHolder) holder).tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("bdl"))
                                && (val <= 1000)) {
                            myProductArrayList.get(position).setQuantity(((ItemViewHolder) holder).edtQuantity.getText().toString());
                        } else
                        {
                            if (((ItemViewHolder) holder).tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("kg")) {
                                Toast.makeText(context, "You can not enter value more than 1000 Kg", Toast.LENGTH_SHORT).show();
                                ((ItemViewHolder) holder).edtQuantity.getText().clear();
                            } else if (((ItemViewHolder) holder).tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("pc")) {
                                Toast.makeText(context, "You can not enter value more than 100000 Pcs", Toast.LENGTH_SHORT).show();
                                ((ItemViewHolder) holder).edtQuantity.getText().clear();
                            } else if (((ItemViewHolder) holder).tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("bdl")) {
                                Toast.makeText(context, "You can not enter value more than 1000 Bdl", Toast.LENGTH_SHORT).show();
                                ((ItemViewHolder) holder).edtQuantity.getText().clear();
                            }
                        }

                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return myProductArrayList.size(); // +1 for the header
    }

    @Override
    public int getItemViewType(int position) {

        if(position==0){
            return VIEW_TYPE_HEADER;
        }else{
            if(isPositionHeader(position)){
                Log.e("Item here","scrolled here");
                return VIEW_TYPE_HEADER;
            }else{
                return VIEW_TYPE_ITEM;
            }
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTextView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTextView = itemView.findViewById(R.id.headerTextView);
        }

        public void bind(String headerText) {
            headerTextView.setText(headerText);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvSkus, tvBrandName, tvBrandPrice, tvUnit;
        EditText edtQuantity;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSkus = itemView.findViewById(R.id.skuName);
            tvBrandName = itemView.findViewById(R.id.brandName);
            tvBrandPrice = itemView.findViewById(R.id.brandPrice);
            edtQuantity = itemView.findViewById(R.id.edtOrderQuantity);
            tvUnit = itemView.findViewById(R.id.tvUnit);
        }

        public void bind(MyProduct myProduct) {
            tvSkus.setText(myProduct.getMySkus());
            tvBrandName.setText(myProduct.getBrand());
            tvBrandPrice.setText(myProduct.getPrice() + "/-");
            tvUnit.setText("/ " + myProduct.getUnit());
            edtQuantity.setText(myProduct.getQuantity());
            /*edtQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (edtQuantity.getText().length() > 0
                            && edtQuantity.getText().toString().matches("[0]+")) {

                        edtQuantity.getText().clear();

                    } else {

                        int val = 0;
                        if (!edtQuantity.getText().toString().isEmpty())
                            val = Integer.parseInt(edtQuantity.getText().toString());
                        Log.e("OrderAdapter", " value:" + val);

                        //@Umesh LowerCase
                        if ((tvUnit.getText().toString().toLowerCase(Locale.ROOT).contains("kg"))
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
            });*/
        }
    }

    private boolean isPositionHeader(int position) {
        return !myProductArrayList.get(position-1).getBrand().equals(myProductArrayList.get(position).getBrand());
    }


}
