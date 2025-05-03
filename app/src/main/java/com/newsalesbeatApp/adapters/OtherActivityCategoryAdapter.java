package com.newsalesbeatApp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.AddDistributor;

/*
 * Created by MTC on 23-08-2017.
 */

public class OtherActivityCategoryAdapter extends RecyclerView.Adapter<OtherActivityCategoryAdapter.ViewHolder> {

    private static FirebaseAnalytics firebaseAnalytics;
    private Context context;
    private String[] otherCategoryItem_e;
    private int[] backImage;
    private SharedPreferences sfa;


    public OtherActivityCategoryAdapter(Context ctx, String[] otherCategoryItem_e, int[] backImage) {
        this.context = ctx;
        this.otherCategoryItem_e = otherCategoryItem_e;
        this.backImage = backImage;

        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        sfa = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.other_activity_category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.imgIconBack.setImageResource(backImage[position]);
        holder.tvCatName.setText(otherCategoryItem_e[position]);

        holder.llOtherCatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position == 0) {


                    Bundle params = new Bundle();
                    params.putString("Action", "Add New Distributor visit");
                    params.putString("UserId", "" + sfa.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("OtherActivity", params);

                    Intent intent = new Intent(context, AddDistributor.class);
                    intent.putExtra("page_title", otherCategoryItem_e[position]);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } /*else if (position == 1){

                    Bundle params = new Bundle();
                    params.putString("Action", "Joint Working Visit");
                    params.putString("UserId", ""+sfa.getString(context.getString(R.string.emp_id_key),""));
                    firebaseAnalytics.logEvent("OtherActivity", params);

                    Intent intent = new Intent(context, JointWorking.class);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                }else if (position == 1){

                    Bundle params = new Bundle();
                    params.putString("Action", "Meeting Activity visit");
                    params.putString("UserId", ""+sfa.getString(context.getString(R.string.emp_id_key),""));
                    firebaseAnalytics.logEvent("OtherActivity", params);

                    Intent intent = new Intent(context, MeetingActivity.class);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }*/

            }
        });
    }

    @Override
    public int getItemCount() {
        return otherCategoryItem_e.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIconBack;
        TextView tvCatName;
        LinearLayout llOtherCatLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imgIconBack = (ImageView) itemView.findViewById(R.id.otherCategoryIcon);
            tvCatName = (TextView) itemView.findViewById(R.id.otherCategoryName);
            llOtherCatLayout = (LinearLayout) itemView.findViewById(R.id.otherCategoryLayout);
        }

    }
}
