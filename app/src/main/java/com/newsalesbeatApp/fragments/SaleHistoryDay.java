package com.newsalesbeatApp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.SaleHistory;
import com.newsalesbeatApp.adapters.SaleHistoryListAdapter;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * Created by Dhirendra Thakur on 29-03-2018.
 */

public class SaleHistoryDay extends Fragment {

    private SharedPreferences prefSFA;
    private ArrayList<Item> listEmp = new ArrayList<>();
    private MaterialCalendarView calendarSaleHistoryDay;
    private RecyclerView rvSaleHistory;
    private ProgressBar pbSaleHis;
    private TextView tvNodataSaleHis;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.sale_history_daywise, parent, false);
        calendarSaleHistoryDay = view.findViewById(R.id.calendarSaleHistoryDay);
        rvSaleHistory = view.findViewById(R.id.rvSaleHistoryDay);
        pbSaleHis = view.findViewById(R.id.pbSaleHis1);
        tvNodataSaleHis = view.findViewById(R.id.tvNoDataSaleHis1);
        prefSFA = requireActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);

        //current date string
        java.util.Calendar cc = java.util.Calendar.getInstance();
        final SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String curentDateString = sdff.format(cc.getTime());


        rvSaleHistory.setVisibility(View.GONE);
        tvNodataSaleHis.setVisibility(View.GONE);
        pbSaleHis.setVisibility(View.VISIBLE);

        if (SaleHistory.utilityClass.isInternetConnected())
            getEmpLeaderBoard(curentDateString);


        try {
            calendarSaleHistoryDay.setCurrentDate(sdff.parse(curentDateString));
            calendarSaleHistoryDay.setSelectedDate(sdff.parse(curentDateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendarSaleHistoryDay.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                if (SaleHistory.utilityClass.isInternetConnected()) {

                    boolean flag = compareDate(sdff.format(date.getDate()));
                    if (flag) {

                        rvSaleHistory.setVisibility(View.GONE);
                        tvNodataSaleHis.setVisibility(View.GONE);
                        pbSaleHis.setVisibility(View.VISIBLE);

                        getEmpLeaderBoard(sdff.format(date.getDate()));

                    } else {

                        Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getContext(), "Not connected to internet", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Calendar max = Calendar.getInstance();

        calendarSaleHistoryDay.state().edit()
                .setMaximumDate(CalendarDay.from(
                        max.get(Calendar.YEAR),
                        max.get(Calendar.MONTH),
                        max.getActualMaximum(Calendar.DAY_OF_MONTH)
                ))
                .commit();

        return view;
    }

    private boolean compareDate(String selectedDate) {

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(selectedDate);

            System.out.println("date1 : " + sdf.format(date1));
            System.out.println("date2 : " + sdf.format(date2));

            if (date1.compareTo(date2) > 0) {
                return true;
            } else if (date1.compareTo(date2) < 0) {
                return false;
            } else if (date1.compareTo(date2) == 0) {
                return true;
            } else {
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;

    }

    private void getEmpLeaderBoard(final String curentDateString) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                SbAppConstants.API_GET_EMP_LEADER_BOARD, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("onResponse", "LEADER_BOARD===" + response);

                        try {
                            listEmp.clear();
                            //@Umesh 02-Feb-2022
                            if(response.getInt("status")==1)
                            {
                                JSONArray topEmpArr = response.getJSONArray("data");
                                for (int i = 0; i < topEmpArr.length(); i++)
                                {
                                    JSONObject object = (JSONObject) topEmpArr.get(i);
                                    Item item = new Item();
                                    item.setItem1(object.getString("eid"));
                                    item.setItem2(object.getString("name"));
                                    item.setItem3(object.getString("profilePic"));
                                    item.setItem4(object.getString("totalCalls"));
                                    item.setItem5(object.getString("productiveCalls"));
                                    item.setItem6(object.getString("totalWeight"));
                                    listEmp.add(item);
                                }
                                if (listEmp.size() > 0)
                                {

                                    SaleHistoryListAdapter saleHistoryListAdapter = new SaleHistoryListAdapter(getContext(), listEmp);
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    rvSaleHistory.setLayoutManager(layoutManager);
                                    rvSaleHistory.setAdapter(saleHistoryListAdapter);

                                    rvSaleHistory.setVisibility(View.VISIBLE);
                                    tvNodataSaleHis.setVisibility(View.GONE);
                                    pbSaleHis.setVisibility(View.GONE);

                                } else {

                                    rvSaleHistory.setVisibility(View.GONE);
                                    tvNodataSaleHis.setVisibility(View.VISIBLE);
                                    pbSaleHis.setVisibility(View.GONE);
                                }
                            }
                            else
                            {
                                Toast.makeText(SaleHistoryDay.super.getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                rvSaleHistory.setVisibility(View.GONE);
                tvNodataSaleHis.setVisibility(View.VISIBLE);
                pbSaleHis.setVisibility(View.GONE);

                error.printStackTrace();
            }
        }) {

            @Override
            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("filter", "SALES");
                params2.put("fromDate", curentDateString);
                params2.put("toDate", curentDateString);
                return new JSONObject(params2).toString().getBytes();
            }


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

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }
}
