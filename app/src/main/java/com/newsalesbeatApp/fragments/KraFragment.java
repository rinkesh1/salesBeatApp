package com.newsalesbeatApp.fragments;

import android.animation.PropertyValuesHolder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.customview.Animation;
import com.newsalesbeatApp.customview.Bar;
import com.newsalesbeatApp.customview.BarSet;
import com.newsalesbeatApp.customview.HorizontalStackBarChartView2;
import com.newsalesbeatApp.customview.Tooltip;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

/*
 * Created by Dhirendra Thakur on 03-01-2018.
 */

public class KraFragment extends Fragment {

    SalesBeatDb salesBeatDb;
    TextView tvNoInternet;
    TextView tvVal1;
    TextView tvVal2;
    TextView tvVal3;
    TextView tvVal4;
    String tcAchievement = "";
    String pcAchievement = "";
    String tcTarget = "";
    String pcTarget = "";
    LinearLayout llkraChartContainer;
    HorizontalStackBarChartView2 barChartKra;
    private Tooltip tip1;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("KraFragment", "onreceived");
            initializeKraGraph();
            try {
                barChartKra.reset();
            } catch (Exception e) {
                //e.printStackTrace();
            }
            initializeBar(tcAchievement, pcAchievement, tcTarget, pcTarget);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.kra_layout, parent, false);

        //barChartKra = view.findViewById(R.id.barChartKra);
        tvNoInternet = view.findViewById(R.id.tvNoInternet);
        tvVal1 = view.findViewById(R.id.tvVal1);
        tvVal2 = view.findViewById(R.id.tvVal2);
        tvVal3 = view.findViewById(R.id.tvVal3);
        tvVal4 = view.findViewById(R.id.tvVal4);
        llkraChartContainer = view.findViewById(R.id.llkraChartContainer);

        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());

        initializeKraGraph();

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);

        initializeBar(tcAchievement, pcAchievement, tcTarget, pcTarget);
        //requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_kra"));

    }

    @Override
    public void onResume() {
        try {
            requireContext().registerReceiver(receiver, new IntentFilter("com.salesbeat_kra"));

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

    private void initializeKraGraph() {

        Cursor cursor = null;

        try {

            cursor = salesBeatDb.getEmpKraDetails();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {

                    tcAchievement = cursor.getString(cursor.getColumnIndex("tcAchievement"));
                    pcAchievement = cursor.getString(cursor.getColumnIndex("pcAchievement"));
                    tcTarget = cursor.getString(cursor.getColumnIndex("tcTarget"));
                    pcTarget = cursor.getString(cursor.getColumnIndex("pcTarget"));

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void initializeBar(String tcAchievement, String pcAchievement,
                               String tcTarget, String pcTarget) {
        try {

            float[] mValues = new float[4];
            String[] mLabels1 = new String[4];

            mValues[1] = Float.parseFloat(tcAchievement);
            mValues[0] = Float.parseFloat(tcTarget);
            mValues[3] = Float.parseFloat(pcAchievement);
            mValues[2] = Float.parseFloat(pcTarget);

            mLabels1[1] = "Tc(A)";
            mLabels1[0] = "Tc(T)";
            mLabels1[3] = "Pc(A)";
            mLabels1[2] = "Pc(T)";

            tvVal1.setText(String.valueOf(mValues[0]));
            tvVal1.setTextColor(Color.parseColor("#feb47b"));
            tvVal2.setText(String.valueOf(mValues[1]));
            tvVal2.setTextColor(Color.parseColor("#6cbf84"));
            tvVal3.setText(String.valueOf(mValues[2]));
            tvVal3.setTextColor(Color.parseColor("#feb47b"));
            tvVal4.setText(String.valueOf(mValues[3]));
            tvVal4.setTextColor(Color.parseColor("#6cbf84"));

            Bar bar1 = new Bar(mLabels1[0], mValues[0]);
            bar1.setColor(Color.parseColor("#feb47b"));

            Bar bar2 = new Bar(mLabels1[1], mValues[1]);
            bar2.setColor(Color.parseColor("#6cbf84"));


            Bar bar3 = new Bar(mLabels1[2], mValues[2]);
            bar3.setColor(Color.parseColor("#feb47b"));

            Bar bar4 = new Bar(mLabels1[3], mValues[3]);
            bar4.setColor(Color.parseColor("#6cbf84"));

            BarSet barSet1 = new BarSet();
            barSet1.addBar(bar4);
            barSet1.addBar(bar3);
            barSet1.addBar(bar2);
            barSet1.addBar(bar1);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            barChartKra = new HorizontalStackBarChartView2(getContext());
            barChartKra.setLayoutParams(params);
            barChartKra.addData(barSet1);

            float maxVal = 0;

            if (mValues[0] > mValues[2])
                maxVal = mValues[0];
            else
                maxVal = mValues[2];

            if (maxVal == 0)
                maxVal = mValues[3] * 10;

            float step = 0;
            if (maxVal != 0)
                step = maxVal / 5;

            if (maxVal != 0)
                barChartKra.setAxisBorderValues(0, maxVal, step);

            if (!getResources().getBoolean(R.bool.isTablet)) {

                barChartKra.setXAxis(true);
                barChartKra.setYAxis(false);
                barChartKra.setRoundCorners(20);
                barChartKra.setBarSpacing(30);
            }

            tip1 = new Tooltip(getContext());
            tip1.setBackgroundColor(Color.parseColor("#CC7B1F"));

            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1);
            tip1.setEnterAnimation(alpha).setDuration(150);
            alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0);
            tip1.setExitAnimation(alpha).setDuration(150);

            barChartKra.setTooltips(tip1)
                    .show(new Animation().setInterpolator(new AccelerateDecelerateInterpolator()));

            llkraChartContainer.addView(barChartKra);
            tvNoInternet.setVisibility(View.GONE);

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
