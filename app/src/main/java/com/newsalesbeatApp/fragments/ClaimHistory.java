package com.newsalesbeatApp.fragments;

import android.content.Context;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.ClaimHistoryAdapter;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by Dhirendra Thakur on 02-12-2017.
 */

public class ClaimHistory extends Fragment {

    private RecyclerView rvClaimHistoryList;

    private SharedPreferences myPref;
    private RequestQueue requestQueue;

    private ArrayList<ClaimHistoryItem> claimHistories = new ArrayList<>();
    private MaterialCalendarView calendarClaimHistoryMonth;
    private UtilityClass utilityClass;

    private int cMonth = 0;
    private int cYear = 0;

    private TextView tvNoDataClaim;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressBar pbClaimHis;

    private SwipeRefreshLayout claimHistoryRefresh;
    private String startDate, endDate;

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    //private boolean compareDate(String selectedDate) {

    // try {

            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = sdf.format(Calendar.getInstance().getTime());
            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(selectedDate);

            System.out.println("date1 : " + sdf.format(date1));
            System.out.println("date2 : " + sdf.format(date2));

//            if (date1.compareTo(date2) > 0) {
//                System.out.println("Date1 is after Date2");
//            } else if (date1.compareTo(date2) < 0) {
//                System.out.println("Date1 is before Date2");
//            } else if (date1.compareTo(date2) == 0) {
//                System.out.println("Date1 is equal to Date2");
//            } else {
//                System.out.println("How to get here?");
//            }

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

    }*/

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
        View view = inflater.inflate(R.layout.claim_history_layout, parent, false);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        rvClaimHistoryList = view.findViewById(R.id.rvClaimHistry);
        tvNoDataClaim = view.findViewById(R.id.tvNoDataClaim);
        pbClaimHis = view.findViewById(R.id.pbClaimHis);
        claimHistoryRefresh = view.findViewById(R.id.claimHistoryRefresh);
        calendarClaimHistoryMonth = view.findViewById(R.id.calendarClaimHistoryMonth);

        utilityClass = new UtilityClass(getContext());
        requestQueue = Volley.newRequestQueue(getContext());

        //current date string
        Calendar cc = java.util.Calendar.getInstance();
        cMonth = cc.get(Calendar.MONTH);
        cYear = cc.get(Calendar.YEAR);
        final SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdff.format(cc.getTime());

        Pair pair = getDateRange(0, 0);
        startDate = (String) pair.first;

        rvClaimHistoryList.setVisibility(View.GONE);
        tvNoDataClaim.setVisibility(View.GONE);
        pbClaimHis.setVisibility(View.VISIBLE);

        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {
                Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
            } else {
                if (utilityClass.isInternetConnected())
                    getClaimHistoryList(startDate, endDate);
                else {

                    Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                    rvClaimHistoryList.setVisibility(View.GONE);
                    tvNoDataClaim.setVisibility(View.VISIBLE);
                    pbClaimHis.setVisibility(View.GONE);
                }
            }

        });


        claimHistoryRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                rvClaimHistoryList.setVisibility(View.GONE);
                tvNoDataClaim.setVisibility(View.GONE);
                pbClaimHis.setVisibility(View.VISIBLE);

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (utilityClass.isInternetConnected())
                            getClaimHistoryList(startDate, endDate);
                        else {
                            claimHistoryRefresh.setRefreshing(false);
                            rvClaimHistoryList.setVisibility(View.GONE);
                            tvNoDataClaim.setVisibility(View.VISIBLE);
                            pbClaimHis.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();
                        }
                    }

                });


            }
        });

        calendarClaimHistoryMonth.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                int val = date.getMonth() - cMonth;
                int val2 = date.getYear() - cYear;
                Pair pair = getDateRange(val, val2);
                startDate = (String) pair.first;
                endDate = (String) pair.second;
                Log.e("DATE", "<--->" + startDate + "<--->" + endDate);

                rvClaimHistoryList.setVisibility(View.GONE);
                tvNoDataClaim.setVisibility(View.GONE);
                pbClaimHis.setVisibility(View.VISIBLE);

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (jsonObjectRequest != null)
                            jsonObjectRequest.cancel();
                        if (utilityClass.isInternetConnected())
                            getClaimHistoryList(startDate, endDate);
                    }

                });


            }
        });


        Calendar max = Calendar.getInstance();
        max.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));

        calendarClaimHistoryMonth.state().edit()
                .setMaximumDate(max)
                .commit();

        return view;
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

    private void getClaimHistoryList(final String startDate, final String endDate) {

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, SbAppConstants.API_GET_CLAIM_HISTORY + "?fromDate=" + startDate + "&toDate=" + endDate,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                claimHistoryRefresh.setRefreshing(false);
                Log.e("onResponse", "Claim History===" + response);
                try {
                    //@Umesh 13-March-2022
                    if(response.getInt("status")==1)
                    {
                        JSONObject obj = response.getJSONObject("data");
                        JSONArray claims = obj.getJSONArray("claims");
                        claimHistories.clear();
                        for (int i = 0; i < claims.length(); i++) {
                            ClaimHistoryItem claimHistory = new ClaimHistoryItem();
                            JSONObject object = (JSONObject) claims.get(i);
                            claimHistory.setClaimType(object.getString("claimType"));
                            claimHistory.setSettled(object.getString("stid"));
                            claimHistory.setDaType(object.getString("daType"));
                            claimHistory.setExpense(object.getString("expense"));
                            claimHistory.setApprovedExpense(object.getString("approvedExpense"));
                            claimHistory.setOrigin(object.getString("origin"));
                            claimHistory.setDestination(object.getString("destination"));
                            claimHistory.setKmsTravel(object.getString("kmsTravel"));
                            claimHistory.setRemarks(object.getString("remarks"));
                            claimHistory.setApproved(object.getString("approved"));
                            claimHistory.setDate(object.getString("created_at"));

                            claimHistories.add(claimHistory);

                        }

                        if(response.getInt("status")==1)
                        {

                            if (claimHistories.size() > 0) {

                                Collections.reverse(claimHistories);

                                ClaimHistoryAdapter claimHistoryAdapter = new ClaimHistoryAdapter(getContext(), claimHistories);

                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                rvClaimHistoryList.setLayoutManager(layoutManager);
                                //rvTownList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
                                rvClaimHistoryList.setAdapter(claimHistoryAdapter);
                                rvClaimHistoryList.setVisibility(View.VISIBLE);
                                tvNoDataClaim.setVisibility(View.GONE);
                                pbClaimHis.setVisibility(View.GONE);

                            } else {

                                rvClaimHistoryList.setVisibility(View.GONE);
                                pbClaimHis.setVisibility(View.GONE);
                                tvNoDataClaim.setVisibility(View.VISIBLE);

                            }


                        } else {

                            rvClaimHistoryList.setVisibility(View.GONE);
                            pbClaimHis.setVisibility(View.GONE);
                            tvNoDataClaim.setVisibility(View.VISIBLE);
                        }
                    }else
                    {
                        Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    rvClaimHistoryList.setVisibility(View.GONE);
                    pbClaimHis.setVisibility(View.GONE);
                    tvNoDataClaim.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                claimHistoryRefresh.setRefreshing(false);
                rvClaimHistoryList.setVisibility(View.GONE);
                pbClaimHis.setVisibility(View.GONE);
                tvNoDataClaim.setVisibility(View.VISIBLE);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPref.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonObjectRequest);
    }
}
