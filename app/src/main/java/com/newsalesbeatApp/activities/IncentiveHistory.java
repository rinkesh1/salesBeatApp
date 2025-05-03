package com.newsalesbeatApp.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.IncentiveHistoryAdapter;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IncentiveHistory extends AppCompatActivity implements NetworkChangeInterface {

    MaterialCalendarView calendarIncentiveHistoryMonth;
    RecyclerView rvIncentiveHistoryList;
    SharedPreferences prefSFA;
    UtilityClass utilityClass;

    // Network Error
    private Boolean incentiveFailure = false;
    private String incentiveDate = "";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.incentive_info_layout);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);
        calendarIncentiveHistoryMonth = findViewById(R.id.calendarIncentiveHistoryMonth);
        rvIncentiveHistoryList = findViewById(R.id.rvIncentiveHistoryList);

        tvPageTitle.setText("Incentive History");

        utilityClass = new UtilityClass(this);

        final String date = utilityClass.getMYDateFormat().format(Calendar.getInstance().getTime());

        getIncentiveHistory(date);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IncentiveHistory.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        calendarIncentiveHistoryMonth.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String inputDate = utilityClass.getMYDateFormat().format(date.getCalendar().getTime());
                getIncentiveHistory(inputDate);
            }
        });

    }

    private void getIncentiveHistory(String date) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        loader.show();

        //getMonthlySecondaryKraByDate/{start_month}/{start_year}/{end_month}/{end_year}
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_INCENTIVE_HISTORY + "/" + date,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                incentiveFailure = false;

                Log.e("onResponse", "Incentive History===" + response);

                loader.dismiss();
                try {

                    ArrayList<Item> incentiveList = new ArrayList<>();
                    JSONArray incentive = response.getJSONArray("incentives");
                    for (int i = 0; i < incentive.length(); i++) {

                        Item item = new Item();
                        JSONObject obj = (JSONObject) incentive.get(i);

                        item.setItem1(obj.getString("date"));
                        item.setItem2(obj.getString("sale"));
                        item.setItem3(obj.getString("amount"));

                        incentiveList.add(item);

                    }

                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        IncentiveHistoryAdapter incentiveHistoryAdapter =
                                new IncentiveHistoryAdapter(IncentiveHistory.this, incentiveList);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(IncentiveHistory.this);
                        rvIncentiveHistoryList.setLayoutManager(layoutManager);
                        int resId = R.anim.layout_animation_fall_down;
                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(IncentiveHistory.this, resId);
                        rvIncentiveHistoryList.setLayoutAnimation(animation);
                        rvIncentiveHistoryList.setAdapter(incentiveHistoryAdapter);
                    }

                } catch (Exception e) {

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (!utilityClass.isInternetConnected()) {

                    incentiveDate = date;
                    incentiveFailure = true;

                } else {
                    incentiveFailure = false;
                }

                loader.dismiss();
            }
        }) {


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(IncentiveHistory.this).add(jsonObjectRequest);

    }

    @Override
    public void connectionChange(boolean status) {

        if (status) {

            if (incentiveFailure) {
                getIncentiveHistory(incentiveDate);
            }

        }

    }
}
