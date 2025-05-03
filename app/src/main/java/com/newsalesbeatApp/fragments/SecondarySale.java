package com.newsalesbeatApp.fragments;

import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.Animation;
import com.newsalesbeatApp.customview.Bar;
import com.newsalesbeatApp.customview.BarSet;
import com.newsalesbeatApp.customview.HorizontalStackBarChartView2;
import com.newsalesbeatApp.customview.Tooltip;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

//import com.db.chart.animation.Animation;
//import com.db.chart.model.Bar;
//import com.db.chart.model.BarSet;
//import com.db.chart.renderer.XRenderer;
//import com.db.chart.renderer.YRenderer;
//import com.db.chart.util.Tools;
//import com.db.chart.view.HorizontalStackBarChartView;

@SuppressLint("ValidFragment")
public class SecondarySale extends Fragment {

    private TextView tvSecondaryNoInternet, tvSecondarySaleT, tvSecondarySaleAch;

    private SalesBeatDb salesBeatDb;

    private LinearLayout llGraphContainerSecondarySale;

    private String saleAchievement = "0"/*, saleAchievementWeek2 = "0", saleAchievementWeek3 = "0",
            saleAchievementWeek4 = "0", saleAchievementWeek5 = "0", */, saleTarget = "0";

    private HorizontalStackBarChartView2 barChartSecondarySale;

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("SecondarySale", "onreceived");
            getSecondarySale();
            try {
                barChartSecondarySale.reset();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            initializeSaleGraph();
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.secondary_sale_graph, parent, false);
        //barChartSecondarySale = view.findViewById(R.id.barChartSecondarySale);
        tvSecondaryNoInternet = view.findViewById(R.id.tvSecondaryNoInternet);
        tvSecondarySaleT = view.findViewById(R.id.tvSecondarySaleT);
        tvSecondarySaleAch = view.findViewById(R.id.tvSecondarySaleAch);
        llGraphContainerSecondarySale = view.findViewById(R.id.llGraphContainerSecondarySale);

        //initialize classes
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());

        getSecondarySale();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeSaleGraph();
        //requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_secondary_sale"));

    }

    @Override
    public void onResume() {
        try {
            requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_secondary"));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onPause() {

        try {
            requireContext().unregisterReceiver(receiver);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    private void getSecondarySale() {

        Cursor cursor = null;

        try {

            cursor = salesBeatDb.getEmpSecondarySale();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

//                    saleAchievementWeek1 = cursor.getString(cursor.getColumnIndex("sale_week1"));
//                    saleAchievementWeek2 = cursor.getString(cursor.getColumnIndex("sale_week2"));
//                    saleAchievementWeek3 = cursor.getString(cursor.getColumnIndex("sale_week3"));
//                    saleAchievementWeek4 = cursor.getString(cursor.getColumnIndex("sale_week4"));
                    saleAchievement = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SALE_ACH));
                    saleTarget = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SALE_TARGET));

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null)
                cursor.close();
        }
    }

    private void initializeSaleGraph() {

        if (saleAchievement.equalsIgnoreCase("0")
                && saleTarget.equalsIgnoreCase("0")) {

            llGraphContainerSecondarySale.setVisibility(View.GONE);
            tvSecondaryNoInternet.setVisibility(View.VISIBLE);
            tvSecondaryNoInternet.setText("No data available");

        } else {

            tvSecondarySaleT.setText(saleTarget);
            tvSecondarySaleAch.setText(saleAchievement);


            String[] mLabels = {"T", "A"};
            float[] mValues = new float[2];
            mValues[1] = Float.parseFloat(saleAchievement);
            mValues[0] = Float.parseFloat(saleTarget);

            Bar bar1 = new Bar(mLabels[0], mValues[0]);
            bar1.setColor(Color.parseColor("#feb47b"));

            Bar bar2 = new Bar(mLabels[1], mValues[1]);
            bar2.setColor(Color.parseColor("#6cbf84"));

            BarSet barSet1 = new BarSet();
            barSet1.addBar(bar2);
            barSet1.addBar(bar1);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            barChartSecondarySale = new HorizontalStackBarChartView2(getContext());
            barChartSecondarySale.setLayoutParams(params);
            barChartSecondarySale.addData(barSet1);

            float maxVal = 0;

            if (mValues[0] > mValues[1])
                maxVal = mValues[0];
            else
                maxVal = mValues[1];

            float step = 0;
            if (maxVal != 0)
                step = maxVal / 3;

            if (maxVal != 0)
                barChartSecondarySale.setAxisBorderValues(0, maxVal, step);

            if (!getResources().getBoolean(R.bool.isTablet)) {

                barChartSecondarySale.setXAxis(true);
                barChartSecondarySale.setYAxis(false);
                barChartSecondarySale.setRoundCorners(20);
                barChartSecondarySale.setBarSpacing(60);
            }

            Tooltip tip1 = new Tooltip(getContext());
            tip1.setBackgroundColor(Color.parseColor("#CC7B1F"));

            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
            tip1.setEnterAnimation(alpha).setDuration(150);
            alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
            tip1.setExitAnimation(alpha).setDuration(150);

            barChartSecondarySale.setTooltips(tip1)
                    .show(new Animation().setInterpolator(new AccelerateDecelerateInterpolator()));

            llGraphContainerSecondarySale.addView(barChartSecondarySale);
            llGraphContainerSecondarySale.setVisibility(View.VISIBLE);
            tvSecondaryNoInternet.setVisibility(View.GONE);
            tvSecondaryNoInternet.setText("No data available");

//            //target bar
//            Bar targetBar = new Bar("T", Float.parseFloat(saleTarget));
//            targetBar.setColor(Color.parseColor("#feb47b"));
//
//            //achievement bar
//            Bar achievementBar = new Bar("A", Float.parseFloat(saleAchievementWeek1));
//            achievementBar.setColor(Color.parseColor("#145A32"));
//
//            //adding stack
//            BarSet stackBarSet1 = new BarSet();
//            stackBarSet1.addBar(achievementBar);
//            stackBarSet1.addBar(targetBar);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//
//            barChartSecondarySale = new HorizontalStackBarChartView2(getContext());
//            barChartSecondarySale.setLayoutParams(params);
//
//            barChartSecondarySale.addData(stackBarSet1);
//
//            String[] mLabels = {"A", "T"};
//
//            float[][] mValuesOne = {{Float.parseFloat(saleAchievementWeek2), 0},
//                    {Float.parseFloat(saleAchievementWeek3), 0},
//                    {Float.parseFloat(saleAchievementWeek4), 0},
//                    {Float.parseFloat(saleAchievementWeek5), 0}};
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[0]);
//            stackBarSet1.setColor(Color.parseColor("#196F3D"));
//            barChartSecondarySale.addData(stackBarSet1);
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[1]);
//            stackBarSet1.setColor(Color.parseColor("#196F3D"));
//            barChartSecondarySale.addData(stackBarSet1);
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[2]);
//            stackBarSet1.setColor(Color.parseColor("#229954"));
//            barChartSecondarySale.addData(stackBarSet1);
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[3]);
//            stackBarSet1.setColor(Color.parseColor("#1E8449"));
//            barChartSecondarySale.addData(stackBarSet1);
//
//            int step = 0;
//            float borderValue = 0;
//
//            if (Float.parseFloat(saleTarget) > (Float.parseFloat(saleAchievementWeek1) + Float.parseFloat(saleAchievementWeek2) +
//                    Float.parseFloat(saleAchievementWeek3) + Float.parseFloat(saleAchievementWeek4) + Float.parseFloat(saleAchievementWeek5))) {
//
//                step = (int) (Float.parseFloat(saleTarget) / 4);
//                borderValue = Float.parseFloat(saleTarget);
//
//            } else {
//
//                float total = Float.parseFloat(saleAchievementWeek1) + Float.parseFloat(saleAchievementWeek2) +
//                        Float.parseFloat(saleAchievementWeek3) + Float.parseFloat(saleAchievementWeek4) +
//                        Float.parseFloat(saleAchievementWeek5);
//
//                step = (int) (total / 4);
//                borderValue = total;
//
//            }
//
//            if (borderValue > 0 && step > 0) {
//
//                barChartSecondarySale.setAxisBorderValues(0, borderValue, step);
//                if (!getResources().getBoolean(R.bool.isTablet)){
//
//                    barChartSecondarySale.setXAxis(true);
//                    barChartSecondarySale.setYAxis(false);
//                    barChartSecondarySale.setRoundCorners(20);
//                    barChartSecondarySale.setBarSpacing(60);
//                }
//
//                Paint thresPaint = new Paint();
//                thresPaint.setColor(Color.parseColor("#dad8d6"));
//                thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
//                thresPaint.setStyle(Paint.Style.STROKE);
//                thresPaint.setAntiAlias(true);
//                thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
//
//                int[] order = {0, 1};
//
//                barChartSecondarySale.setXLabels(XRenderer.LabelPosition.OUTSIDE)
//                        .setYLabels(YRenderer.LabelPosition.OUTSIDE)
//                        .setValueThreshold(89.f, 89.f, thresPaint)
//                        .show(new Animation().inSequence(.5f, order));
//
//                llGraphContainerSecondarySale.addView(barChartSecondarySale);
//                llGraphContainerSecondarySale.setVisibility(View.VISIBLE);
//                tvSecondaryNoInternet.setVisibility(View.GONE);
//                tvSecondaryNoInternet.setText("No data available");
//
//            } else {
//
//                llGraphContainerSecondarySale.setVisibility(View.GONE);
//                tvSecondaryNoInternet.setVisibility(View.VISIBLE);
//                tvSecondaryNoInternet.setText("Not enough sale to plot");
//            }

        }
    }
}
