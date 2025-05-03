package com.newsalesbeatApp.fragments;

import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.PrimarySaleHistory;
import com.newsalesbeatApp.customview.Animation;
import com.newsalesbeatApp.customview.Bar;
import com.newsalesbeatApp.customview.BarSet;
import com.newsalesbeatApp.customview.HorizontalStackBarChartView2;
import com.newsalesbeatApp.customview.Tooltip;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

@SuppressLint("ValidFragment")
public class PrimarySale extends Fragment {

    private static FirebaseAnalytics firebaseAnalytics;
    //HorizontalStackBarChartView barChartPrimarySale;
    TextView tvPrimaryNoInternet, tvPrimarySaleT, tvPrimarySaleAch;
    ImageView imgPrimaryData;
    SalesBeatDb salesBeatDb;
    UtilityClass utilityClass;
    LinearLayout llGraphContainerPrimarySale;
    String /*saleAchievementWeek1 = "0", saleAchievementWeek2 = "0", saleAchievementWeek3 = "0",
            saleAchievementWeek4 = "0",*/ saleAchievement = "0", saleTarget = "0";
    Button btnPrimaryData;
    HorizontalStackBarChartView2 barChartPrimarySale;
    SharedPreferences sfaPref;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("PrimarySale", "onreceived");
            getPrimarySale();
            try {
                barChartPrimarySale.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
            initializeSaleGraph();
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.primary_sale_graph, parent, false);
        //barChartPrimarySale = view.findViewById(R.id.barChartPrimarySale);
        sfaPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tvPrimaryNoInternet = view.findViewById(R.id.tvPrimaryNoInternet);
        tvPrimarySaleT = view.findViewById(R.id.tvPrimarySaleT);
        tvPrimarySaleAch = view.findViewById(R.id.tvPrimarySaleAch);
        imgPrimaryData = view.findViewById(R.id.imgPrimaryData);
        llGraphContainerPrimarySale = view.findViewById(R.id.llGraphContainerPrimarySale);
        btnPrimaryData = view.findViewById(R.id.btnPrimaryData);

        //initialize of classes
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(requireContext());
        utilityClass = new UtilityClass(requireContext());
        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());

        imgPrimaryData.setImageResource(R.drawable.info_icon);
        imgPrimaryData.setPadding(0, 5, 0, 0);

        getPrimarySale();

        imgPrimaryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                SBApplication.getInstance().trackEvent("PrimarySale", "PrimarySaleInfo",
//                        "Primary Sale visit by:"+sfaPref.getString(getString(R.string.emp_id_key),""));


                Bundle params = new Bundle();
                params.putString("Action", "Primary Sale History Visit");
                params.putString("UserId", "" + sfaPref.getString(getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("PrimarySale", params);

                if (utilityClass.isInternetConnected()) {

                    Intent intent = new Intent(getContext(), PrimarySaleHistory.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrimaryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (utilityClass.isInternetConnected()) {

                    Intent intent = new Intent(getContext(), PrimarySaleHistory.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeSaleGraph();
        //requireContext().registerReceiver(receiver, new IntentFilter("salesbeat_primary_sale"));

    }

    @Override
    public void onResume() {
        try {
//            requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_primary"));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_primary"), Context.RECEIVER_NOT_EXPORTED);
            } else {
                requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_primary"), Context.RECEIVER_NOT_EXPORTED);
            }

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

    private void getPrimarySale() {

        Cursor cursor = null;

        try {

            cursor = salesBeatDb.getEmpPrimarySale();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

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

        Log.e("PrimarySale", "  : " + saleTarget);
        if (saleAchievement.equalsIgnoreCase("0")
                && saleTarget.equalsIgnoreCase("0")) {

            llGraphContainerPrimarySale.setVisibility(View.GONE);
            tvPrimaryNoInternet.setVisibility(View.VISIBLE);
            tvPrimaryNoInternet.setText("No data available");

        } else {

            tvPrimarySaleT.setText(saleTarget);
            tvPrimarySaleAch.setText(saleAchievement);

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

            barChartPrimarySale = new HorizontalStackBarChartView2(getContext());
            barChartPrimarySale.setLayoutParams(params);
            barChartPrimarySale.addData(barSet1);

            float maxVal = 0;

            if (mValues[0] > mValues[1])
                maxVal = mValues[0];
            else
                maxVal = mValues[1];

            float step = 0;
            if (maxVal != 0)
                step = maxVal / 3;

            if (maxVal != 0)
                barChartPrimarySale.setAxisBorderValues(0, maxVal, step);

            if (!getResources().getBoolean(R.bool.isTablet)) {

                barChartPrimarySale.setXAxis(true);
                barChartPrimarySale.setYAxis(false);
                barChartPrimarySale.setRoundCorners(20);
                barChartPrimarySale.setBarSpacing(60);
            }

            Tooltip tip1 = new Tooltip(getContext());
            tip1.setBackgroundColor(Color.parseColor("#CC7B1F"));

            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
            tip1.setEnterAnimation(alpha).setDuration(150);
            alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
            tip1.setExitAnimation(alpha).setDuration(150);

            //@Umesh 01-09-2022
            try {
                barChartPrimarySale.setTooltips(tip1)
                        .show(new Animation().setInterpolator(new AccelerateDecelerateInterpolator()));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            llGraphContainerPrimarySale.addView(barChartPrimarySale);
            llGraphContainerPrimarySale.setVisibility(View.VISIBLE);
            tvPrimaryNoInternet.setVisibility(View.GONE);
            tvPrimaryNoInternet.setText("No data available");


//            //target bar
//            Bar targetBar = new Bar("T", Float.parseFloat(saleTarget));
//            targetBar.setColor(Color.parseColor("#feb47b"));
//
//
//            //achievement bar
//            Bar achievementBar = new Bar("A", Float.parseFloat(saleAchievementWeek1));
//            achievementBar.setColor(Color.parseColor("#145A32"));
//
//
//            //adding stack
//            BarSet stackBarSet1 = new BarSet();
//            stackBarSet1.addBar(achievementBar);
//            stackBarSet1.addBar(targetBar);
//
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT);
//
//            barChartPrimarySale = new HorizontalStackBarChartView2(getContext());
//            barChartPrimarySale.setLayoutParams(params);
//
//            barChartPrimarySale.addData(stackBarSet1);
//
//            String[] mLabels = {"A", "T"};
//
//            float[][] mValuesOne = {{Float.parseFloat(saleAchievementWeek2), 0},
//                    {Float.parseFloat(saleAchievementWeek3), 0},
//                    {Float.parseFloat(saleAchievementWeek4), 0},
//                    {Float.parseFloat(saleAchievementWeek5), 0}};
//
////            float[][] mValuesOne = {{Float.parseFloat(saleAchievementWeek1), 0},
////                    {Float.parseFloat(saleAchievementWeek2), 0},
////                    {Float.parseFloat(saleAchievementWeek3), 0},
////                    {Float.parseFloat(saleAchievementWeek4), 0},
////                    {Float.parseFloat(saleAchievementWeek5), 0}};
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[0]);
//            stackBarSet1.setColor(Color.parseColor("#196F3D"));
//            barChartPrimarySale.addData(stackBarSet1);
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[1]);
//            stackBarSet1.setColor(Color.parseColor("#196F3D"));
//            barChartPrimarySale.addData(stackBarSet1);
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[2]);
//            stackBarSet1.setColor(Color.parseColor("#229954"));
//            barChartPrimarySale.addData(stackBarSet1);
//
//            stackBarSet1 = new BarSet(mLabels, mValuesOne[3]);
//            stackBarSet1.setColor(Color.parseColor("#1E8449"));
//            barChartPrimarySale.addData(stackBarSet1);
//
////            stackBarSet1 = new BarSet(mLabels, mValuesOne[4]);
////            stackBarSet1.setColor(Color.parseColor("#36B367"));
////            barChartPrimarySale.addData(stackBarSet1);
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
//                barChartPrimarySale.setAxisBorderValues(0, borderValue, step);
//                if (!getResources().getBoolean(R.bool.isTablet)){
//
//                    barChartPrimarySale.setXAxis(true);
//                    barChartPrimarySale.setYAxis(false);
//                    barChartPrimarySale.setRoundCorners(20);
//                    barChartPrimarySale.setBarSpacing(60);
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
//                barChartPrimarySale.setXLabels(XRenderer.LabelPosition.OUTSIDE)
//                        .setYLabels(YRenderer.LabelPosition.OUTSIDE)
//                        .setValueThreshold(89.f, 89.f, thresPaint)
//                        .show(new Animation().inSequence(.5f, order));
//
//
//                llGraphContainerPrimarySale.addView(barChartPrimarySale);
//                llGraphContainerPrimarySale.setVisibility(View.VISIBLE);
//                tvPrimaryNoInternet.setVisibility(View.GONE);
//                tvPrimaryNoInternet.setText("No data available");


//            } else {
//
//                llGraphContainerPrimarySale.setVisibility(View.GONE);
//                tvPrimaryNoInternet.setVisibility(View.VISIBLE);
//                tvPrimaryNoInternet.setText("Not enough sale to plot");
//            }

        }
    }
}
