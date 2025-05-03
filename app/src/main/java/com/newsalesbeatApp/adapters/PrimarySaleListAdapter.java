package com.newsalesbeatApp.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.pojo.Item;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PrimarySaleListAdapter extends RecyclerView.Adapter<PrimarySaleListAdapter.MyViewHolder>
        implements Filterable {

    Context context;
    private ArrayList<Item> primarySaleList;
    private ArrayList<Item> primarySaleList2;

    public PrimarySaleListAdapter(Context ctx, ArrayList<Item> primarySaleList) {

        this.context = ctx;
        this.primarySaleList = primarySaleList;
        this.primarySaleList2 = primarySaleList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.primary_salehistory_row, parent, false);
        return new MyViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        ArrayList<Item> primarySaleDateWiseList = primarySaleList.get(position).getItemList();

        PrimarySaleLisDateWisetAdapter saleLisDateWisetAdapter =
                new PrimarySaleLisDateWisetAdapter(context, primarySaleDateWiseList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.rvPrimarySaleListByDate.setLayoutManager(layoutManager);
        holder.rvPrimarySaleListByDate.setAdapter(saleLisDateWisetAdapter);



        holder.tvDistributorNameForPrimarySale.setText(primarySaleList.get(position).getItem1());
        holder.tvTownName.setText(primarySaleList.get(position).getItem4());
//        holder.tvPrimarySaleTargetD.setText(primarySaleList.get(position).getItem2() + context.getString(R.string.unitt));  //@Umesh 31-08-2022
        holder.tvPrimarySaleTargetD.setText(primarySaleList.get(position).getItem2());
//        holder.tvPrimarySaleAchTotal.setText(primarySaleList.get(position).getItem3() + context.getString(R.string.unitt));
        holder.tvPrimarySaleAchTotal.setText(primarySaleList.get(position).getItem3());
        holder.llThumbsUp.setVisibility(View.INVISIBLE);

        if (primarySaleList.get(position).getItem3().contains("-")) {

            holder.llThumbsUp.setVisibility(View.VISIBLE);
            holder.imgThumsUp.setImageResource(R.drawable.ic_thumb_down_black_24dp);
            holder.imgThumsUp.setColorFilter(Color.parseColor("#F0544D"), android.graphics.PorterDuff.Mode.MULTIPLY);
            holder.tvPrimarySaleAchTotal.setTextColor(Color.parseColor("#F0544D"));

        } else {

            //@Umesh 31-08-2022
//            int target = Integer.parseInt(primarySaleList.get(position).getItem2());
//            int ach = Integer.parseInt(primarySaleList.get(position).getItem3());
            double target = Double.parseDouble(primarySaleList.get(position).getItem2());
            double ach = Double.parseDouble(primarySaleList.get(position).getItem3());
            holder.llThumbsUp.setVisibility(View.VISIBLE);

            if (ach >= target) {

                holder.imgThumsUp.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                holder.imgThumsUp.setColorFilter(Color.parseColor("#5aac82"), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.tvPrimarySaleAchTotal.setTextColor(Color.parseColor("#5aac82"));

            } /*else if(ach != 0) {

                holder.llDropDown.setVisibility(View.VISIBLE);

            } */ else {

                holder.imgThumsUp.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                holder.imgThumsUp.setColorFilter(Color.parseColor("#F0544D"), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.tvPrimarySaleAchTotal.setTextColor(Color.parseColor("#ffffbb33"));
            }

            if (ach == 0)
                holder.llDropDown.setVisibility(View.INVISIBLE);
            else
                holder.llDropDown.setVisibility(View.VISIBLE);
        }

        //@Umesh
        collapse(holder.llHeaderPrimarySale);
        holder.llDropDown.setVisibility(View.INVISIBLE);


        holder.llDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.rvPrimarySaleListByDate.isShown()) {

                    collapse(holder.llHeaderPrimarySale);
                    rotateImage(180, 360, holder.imgDropDown);

                } else {

                    expand(holder.llHeaderPrimarySale);
                    rotateImage(0, 180, holder.imgDropDown);

                }


            }
        });
    }

    private void rotateImage(int init, int degree, ImageView imgDropDown) {

        RotateAnimation rotate =
                new RotateAnimation(init, degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setFillAfter(true);

        imgDropDown.startAnimation(rotate);

    }

    private void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int targetHeight = 0;
        if (Build.VERSION.SDK_INT < 21)
            targetHeight = v.getHeight();
        else
            targetHeight = v.getMeasuredHeight();
        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);

        final int finalTargetHeight = targetHeight;
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (finalTargetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    private void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(500);
        v.startAnimation(a);
    }

    @Override
    public int getItemCount() {
        return primarySaleList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    primarySaleList = primarySaleList2;
                } else {
                    ArrayList<Item> filteredList = new ArrayList<>();
                    for (Item row : primarySaleList2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getItem1().toLowerCase().contains(charString.toLowerCase())) {

                            filteredList.add(row);
                        }
                    }

                    primarySaleList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = primarySaleList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                primarySaleList = (ArrayList<Item>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvDistributorNameForPrimarySale, tvPrimarySaleTargetD, tvPrimarySaleAchTotal, tvTownName;
        RecyclerView rvPrimarySaleListByDate;
        LinearLayout llPrimarySaleHistory, llHeaderPrimarySale, llDropDown, llThumbsUp;
        ImageView imgDropDown, imgThumsUp;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDistributorNameForPrimarySale = itemView.findViewById(R.id.tvDistributorNameForPrimarySale);
            tvPrimarySaleTargetD = itemView.findViewById(R.id.tvPrimarySaleTargetD);
            tvPrimarySaleAchTotal = itemView.findViewById(R.id.tvPrimarySaleAchTotal);
            tvTownName = itemView.findViewById(R.id.tvTownPS);
            rvPrimarySaleListByDate = itemView.findViewById(R.id.rvPrimarySaleListByDate);
            llPrimarySaleHistory = itemView.findViewById(R.id.llPrimarySaleHistory);
            llHeaderPrimarySale = itemView.findViewById(R.id.llHeaderPrimarySale);
            llDropDown = itemView.findViewById(R.id.llDropDown);
            llThumbsUp = itemView.findViewById(R.id.llThumbsUp);
            imgDropDown = itemView.findViewById(R.id.imgDropDown);
            imgThumsUp = itemView.findViewById(R.id.imgThumsUp);
        }
    }

    private class PrimarySaleLisDateWisetAdapter extends RecyclerView.Adapter<PrimarySaleLisDateWisetAdapter.MyViewHolder2> {
        Context context;
        ArrayList<Item> primarySaleDateWiseList;

        public PrimarySaleLisDateWisetAdapter(Context context, ArrayList<Item> primarySaleDateWiseList) {
            this.context = context;
            this.primarySaleDateWiseList = primarySaleDateWiseList;
        }

        @Override
        public MyViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.primarysale_bydate, parent, false);
            return new MyViewHolder2(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder2 holder, int position) {

            DateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            DateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

            String formattedDate = "";
            try {

                String temp[] = primarySaleDateWiseList.get(position).getItem1().split(" ");
                Date date = simpleDateFormat2.parse(temp[0]);
                formattedDate = simpleDateFormat.format(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvPrimarySaleDate.setText(formattedDate);
            if (!primarySaleDateWiseList.get(position).getItem2().contains("-"))
                holder.tvPrimarySaleAchievement.setText(primarySaleDateWiseList.get(position).getItem2() + context.getString(R.string.unitt));
            else
                holder.tvSalesReturn.setText(primarySaleDateWiseList.get(position).getItem2() + context.getString(R.string.unitt));
        }

        @Override
        public int getItemCount() {
            return primarySaleDateWiseList.size();
        }

        class MyViewHolder2 extends RecyclerView.ViewHolder {

            TextView tvPrimarySaleDate, tvPrimarySaleAchievement, tvSalesReturn;

            public MyViewHolder2(View itemView) {
                super(itemView);
                tvPrimarySaleDate = itemView.findViewById(R.id.tvPrimarySaleDate);
                tvPrimarySaleAchievement = itemView.findViewById(R.id.tvPrimarySaleAchievement);
                tvSalesReturn = itemView.findViewById(R.id.tvSalesReturn);
            }
        }
    }
}

