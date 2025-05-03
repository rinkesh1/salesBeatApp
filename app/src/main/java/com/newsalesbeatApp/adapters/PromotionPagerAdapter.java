package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.PromotionActivity;

import java.io.File;
import java.util.ArrayList;

/*
 * Created by MTC on 29-12-2015.
 */
public class PromotionPagerAdapter extends PagerAdapter {

    private static FirebaseAnalytics firebaseAnalytics;
    SharedPreferences sfaPref;
    private Context mContext;
    private ArrayList<String> room_img_url;
    private ArrayList<String> content;
    private LayoutInflater mLayoutInflater;

    public PromotionPagerAdapter(Context context, ArrayList<String> r_img, ArrayList<String> content) {
        this.mContext = context;
        sfaPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        this.room_img_url = r_img;
        this.content = content;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);

    }

    @Override
    public int getCount() {
        return room_img_url.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        View itemView = mLayoutInflater.inflate(R.layout.promotion_image_pager_row, container, false);
        ImageView imageView1 = itemView.findViewById(R.id.imgHotelRoom);

        Glide.with(mContext)
                .load(new File(room_img_url.get(position)))
                .into(imageView1);

        Log.e("PromotionPagerAdapter", "===>" + room_img_url.get(position));

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SBApplication.getInstance().trackEvent("Campaign", "CampaignInfo",
//                        "CampaignInfo visit by:"+sfaPref.getString(mContext.getString(R.string.emp_id_key),""));


                Bundle params = new Bundle();
                params.putString("Action", "Campaign Infortion");
                params.putString("UserId", "" + sfaPref.getString(mContext.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("Campaign", params);

                try {

                    Intent intent = new Intent(mContext, PromotionActivity.class);
                    intent.putExtra("url", room_img_url.get(position));
                    intent.putExtra("content", content.get(position));

                    mContext.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                    Log.e("PromotionPger", "===" + e.getMessage());
                }

            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}
