package com.newsalesbeatApp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.SaleHistory;
import com.newsalesbeatApp.activities.SalesAddActivity;
import com.newsalesbeatApp.adapters.SaleHistoryListAdapter;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Dhirendra Thakur on 30-03-2018.
 */

public class SaleHistoryMonth extends Fragment {

    SharedPreferences prefSFA;
    ArrayList<Item> listEmp = new ArrayList<>();
    MaterialCalendarView calendarSaleHistoryDay;
    RecyclerView rvSaleHistory;
    JsonObjectRequest jsonObjectRequest;
    int cMonth = 0;
    int cYear = 0;
    ProgressBar pbSaleHis;
    TextView tvNodataSaleHis;
    SwipeRefreshLayout saleHisRefresh;
    FloatingActionButton fabAdd;

    //UtilityClass utilityClass;
    String startDate, endDate;

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.sale_history_month, parent, false);
        prefSFA = getActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        calendarSaleHistoryDay = view.findViewById(R.id.calendarSaleHistoryDay);
        rvSaleHistory = view.findViewById(R.id.rvSaleHistoryDay);
        pbSaleHis = view.findViewById(R.id.pbSaleHis);
        tvNodataSaleHis = view.findViewById(R.id.tvNoDataSaleHis);
        saleHisRefresh = view.findViewById(R.id.saleHisRefresh);
        fabAdd = view.findViewById(R.id.fabAdd);

        //utilityClass = new UtilityClass(getContext());
        //current date string
        java.util.Calendar cc = java.util.Calendar.getInstance();
        cMonth = cc.get(Calendar.MONTH);
        cYear = cc.get(Calendar.YEAR);
        final SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdff.format(cc.getTime());

        Pair pair = getDateRange(0, 0);
        startDate = (String) pair.first;

        rvSaleHistory.setVisibility(View.GONE);
        tvNodataSaleHis.setVisibility(View.GONE);
        pbSaleHis.setVisibility(View.VISIBLE);

        if (SaleHistory.utilityClass.isInternetConnected())
            getEmpLeaderBoard(startDate, endDate);
        else {

            rvSaleHistory.setVisibility(View.GONE);
            tvNodataSaleHis.setVisibility(View.VISIBLE);
            pbSaleHis.setVisibility(View.GONE);
            Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();

        }


        saleHisRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                rvSaleHistory.setVisibility(View.GONE);
                tvNodataSaleHis.setVisibility(View.GONE);
                pbSaleHis.setVisibility(View.VISIBLE);

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (SaleHistory.utilityClass.isInternetConnected())
                            getEmpLeaderBoard(startDate, endDate);
                        else {
                            saleHisRefresh.setRefreshing(false);
                            rvSaleHistory.setVisibility(View.GONE);
                            tvNodataSaleHis.setVisibility(View.VISIBLE);
                            pbSaleHis.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                        }
                    }

                });


            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SalesAddActivity.class);
                startActivity(intent);
            }
        });

        calendarSaleHistoryDay.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                int val = date.getMonth() - cMonth;
                int val2 = date.getYear() - cYear;
                Pair pair = getDateRange(val, val2);
                startDate = (String) pair.first;
                endDate = (String) pair.second;
                Log.e("DATE", "<--->" + startDate + "<--->" + endDate);

                rvSaleHistory.setVisibility(View.GONE);
                tvNodataSaleHis.setVisibility(View.GONE);
                pbSaleHis.setVisibility(View.VISIBLE);

                if (jsonObjectRequest != null)
                    jsonObjectRequest.cancel();
                getEmpLeaderBoard(startDate, endDate);
            }
        });


        Calendar max = Calendar.getInstance();
        max.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));

        calendarSaleHistoryDay.state().edit()
                .setMaximumDate(max)
                .commit();

        return view;
    }

    private void getEmpLeaderBoard(final String startDate, final String endDate) {
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_GET_EMP_LEADER_BOARD,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "LEADER_BOARD===" + response);
                saleHisRefresh.setRefreshing(false);
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
                        Toast.makeText(SaleHistoryMonth.super.getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {

                    e.printStackTrace();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                saleHisRefresh.setRefreshing(false);
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
                params2.put("fromDate", startDate);
                params2.put("toDate", endDate);
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

        jsonObjectRequest.setRetryPolicy(
                new DefaultRetryPolicy(50000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);
    }

    public Pair<String, String> getDateRange(int val, int val2) {
        String begining, end;
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, val);
            calendar.add(Calendar.YEAR, val2);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);

            begining = sdff.format(calendar.getTime());
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, val);
            calendar.add(Calendar.YEAR, val2);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = sdff.format(calendar.getTime());
        }

        return Pair.create(begining, end);
    }

}
