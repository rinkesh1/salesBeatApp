package com.newsalesbeatApp.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.WorkDetails;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.pojo.SkuItem;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by Dhirendra Thakur on 13-04-2018.
 */

public class DetailedListAdapter extends RecyclerView.Adapter<DetailedListAdapter.MyViewHolder> {

    private Context mContext;
    private String type;
    private List<SkuItem> skuItemList;
    private List<RetailerItem> retailerItemList;
    private List<Item> fullDayActivityItemList;
    private List<String> jointWorkingWith;
    private ArrayList<String> beatList;
    private UtilityClass utilityClass;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public DetailedListAdapter(Context ctx, List<SkuItem> list, String type) {
        this.mContext = ctx;
        this.skuItemList = list;
        this.type = type;
        utilityClass = new UtilityClass(ctx);
    }

    public DetailedListAdapter(WorkDetails ctx, List<RetailerItem> retailerItemList, String type) {
        this.mContext = ctx;
        this.retailerItemList = retailerItemList;
        this.type = type;
        utilityClass = new UtilityClass(ctx);

        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(ctx);

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }
    }

    public DetailedListAdapter(WorkDetails workDetails, ArrayList<String> beatList, String type) {
        this.mContext = workDetails;
        this.beatList = beatList;
        this.type = type;
        utilityClass = new UtilityClass(workDetails);
    }

    public DetailedListAdapter(WorkDetails workDetails, List<Item> fullDayActivityList, List<String> jointWorkingWith,
                               String type, String empty) {

        this.mContext = workDetails;
        this.fullDayActivityItemList = fullDayActivityList;
        this.jointWorkingWith = jointWorkingWith;
        this.type = type;
        utilityClass = new UtilityClass(workDetails);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (type.equalsIgnoreCase("L")) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sold_sku_row, parent, false);
            return new MyViewHolder(view);
        }

        if (type.equalsIgnoreCase("T")) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.tc_list_row, parent, false);
            return new MyViewHolder(view);
        }

        if (type.equalsIgnoreCase("BA") || type.equalsIgnoreCase("BV")) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.beat_v_list_row, parent, false);
            return new MyViewHolder(view);
        }

        if (type.equalsIgnoreCase("OA")) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.full_day_activity_row, parent, false);
            return new MyViewHolder(view);
        }

        if (type.equalsIgnoreCase("NC")) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.new_retailer_list_row, parent, false);
            return new MyViewHolder(view);
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (type.equalsIgnoreCase("L")) {

            holder.tvSkuName.setText(skuItemList.get(position).getSku());
            holder.tvSkuQty.setText(skuItemList.get(position).getOpening());
            holder.tvSkuUnit.setText(skuItemList.get(position).getClosing());
        }

        if (type.equalsIgnoreCase("T")) {

            //String temp[] = retailerItemList.get(position).getRetailer_pin().split(" ");
            //String chIn = utilityClass.get12Format(temp[1]);

            String chIn=retailerItemList.get(position).getRetailer_pin(); //@Umesh

//            String tempp[] = retailerItemList.get(position).getRetailerPhone().split(" ");
//            String chOt = utilityClass.get12Format(tempp[1]);

            String chOt=retailerItemList.get(position).getRetailerPhone(); //@Umesh

            if (retailerItemList.get(position).getOrderType() == null
                    || retailerItemList.get(position).getOrderType().equalsIgnoreCase("null")
                    || retailerItemList.get(position).getOrderType().isEmpty()) {

                holder.tvOrderTypeHis.setText("Productive");
                holder.tvOrderTypeHis.setBackgroundColor(Color.parseColor("#ff99cc00"));
                holder.llCmntHis.setVisibility(View.GONE);

            } else {

                holder.tvOrderTypeHis.setText("Non Prod.");
                holder.tvOrderTypeHis.setBackgroundColor(Color.parseColor("#F0544D"));
                String cmnt = "";
                try {

                    cmnt = retailerItemList.get(position).getOrderType().replaceAll("\\[", "");
                    cmnt = cmnt.replaceAll("\\]", "");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                holder.tvCommentHis.setText(cmnt);
                holder.llCmntHis.setVisibility(View.VISIBLE);
            }

            holder.tvRetName.setText(retailerItemList.get(position).getRetailerName());
            holder.tvChkIN.setText(chIn);
            holder.tvChkOUT.setText(chOt);
        }

        if (type.equalsIgnoreCase("BA") || type.equalsIgnoreCase("BV")) {

            holder.tvBeatNameByDate.setText(beatList.get(position));
        }

        if (type.equalsIgnoreCase("OA")) {

            holder.tvActivity.setText(fullDayActivityItemList.get(position).getItem1());
            if (fullDayActivityItemList.get(position).getItem2() == null
                    || fullDayActivityItemList.get(position).getItem2().equalsIgnoreCase("null")) {
                holder.tvRemarks.setText("NA");
            } else {
                holder.tvRemarks.setText(fullDayActivityItemList.get(position).getItem2());
            }

            if (jointWorkingWith.size() > 0) {

                holder.llJwWith.setVisibility(View.VISIBLE);
                holder.tvJWWith.setText(jointWorkingWith.toString());

            } else {
                holder.llJwWith.setVisibility(View.GONE);
            }
        }

        if (type.equalsIgnoreCase("NC")) {

            holder.tvNewRetName.setText(retailerItemList.get(position).getRetailerName());
            holder.tvRetOwnerName.setText(retailerItemList.get(position).getRetailer_owner_name());
            holder.tvRetOwnerPhn.setText(retailerItemList.get(position).getRetailerPhone());
            holder.tvRetAddress.setText(retailerItemList.get(position).getRetailerAddress());
            holder.tvRetState.setText(retailerItemList.get(position).getRetailer_state());
            holder.tvRetCity.setText(retailerItemList.get(position).getRetailer_city());
            holder.tvRetPin.setText(retailerItemList.get(position).getRetailer_pin());
            holder.tvRetLocality.setText(retailerItemList.get(position).getRetailerLocality());
            holder.tvRetOutletChannel.setText(retailerItemList.get(position).getTimeStamp());
            holder.tvRetGrade.setText(retailerItemList.get(position).getRetailer_grade());
            holder.tvRetShopType.setText(retailerItemList.get(position).getOrderType());

            holder.imgRetailer.setImageUrl(SbAppConstants.IMAGE_PREFIX_RETAILER_THUMB + retailerItemList.get(position).getRetailer_image(),
                    mImageLoader);
        }

    }

    @Override
    public int getItemCount() {

        if (type.equalsIgnoreCase("L"))
            return skuItemList.size();

        if (type.equalsIgnoreCase("T"))
            return retailerItemList.size();

        if (type.equalsIgnoreCase("BA") || type.equalsIgnoreCase("BV"))
            return beatList.size();

        if (type.equalsIgnoreCase("OA"))
            return fullDayActivityItemList.size();

        if (type.equalsIgnoreCase("NC"))
            return retailerItemList.size();

        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvSkuName, tvSkuQty, tvSkuUnit, tvBeatNameByDate,
                tvRetName, tvNewRetName, tvChkIN, tvChkOUT, tvActivity, tvRemarks, tvRetOwnerName,
                tvRetOwnerPhn, tvRetAddress, tvRetCity, tvRetState, tvRetPin, tvRetLocality, tvRetOutletChannel,
                tvRetGrade, tvRetShopType, tvOrderTypeHis, tvCommentHis, tvJWWith;

        NetworkImageView imgRetailer;
        LinearLayout llCmntHis, llJwWith;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvSkuName = itemView.findViewById(R.id.tvSkuName);
            tvSkuQty = itemView.findViewById(R.id.tvSkuQty);
            tvSkuUnit = itemView.findViewById(R.id.tvSkuUnit);
            tvBeatNameByDate = itemView.findViewById(R.id.tvBeatNameByDate);
            tvRetName = itemView.findViewById(R.id.tvRetName);
            tvChkIN = itemView.findViewById(R.id.tvChkIN);
            tvChkOUT = itemView.findViewById(R.id.tvChkOUT);
            tvOrderTypeHis = itemView.findViewById(R.id.tvOrderTypeHis);
            tvCommentHis = itemView.findViewById(R.id.tvCommentsHis);
            tvActivity = itemView.findViewById(R.id.tvActivity);
            tvRemarks = itemView.findViewById(R.id.tvRemarksO);
            tvJWWith = itemView.findViewById(R.id.tvJWWith);
            tvNewRetName = itemView.findViewById(R.id.tvNRetailerName);
            tvRetOwnerName = itemView.findViewById(R.id.tvNRetailerOwnerName);
            tvRetOwnerPhn = itemView.findViewById(R.id.tvNRetailerOwnerMobile);
            tvRetAddress = itemView.findViewById(R.id.tvNRetailerAddress);
            tvRetCity = itemView.findViewById(R.id.tvNRetailerDistrict);
            tvRetState = itemView.findViewById(R.id.tvNRetailerState);
            tvRetPin = itemView.findViewById(R.id.tvNRetailerPin);
            tvRetLocality = itemView.findViewById(R.id.tvNRetailerLocality);
            tvRetOutletChannel = itemView.findViewById(R.id.tvNRetailerOutletChannel);
            tvRetGrade = itemView.findViewById(R.id.tvNRetailerGrade);
            tvRetShopType = itemView.findViewById(R.id.tvNRetailerShopType);
            imgRetailer = itemView.findViewById(R.id.imgNRetailerImage);
            llCmntHis = itemView.findViewById(R.id.llCmntHis);
            llJwWith = itemView.findViewById(R.id.llJwWith);

        }
    }
}
