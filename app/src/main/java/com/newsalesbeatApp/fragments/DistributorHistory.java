package com.newsalesbeatApp.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
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
import com.newsalesbeatApp.adapters.DistributorHistoryListAdapter;
import com.newsalesbeatApp.pojo.ClaimHistoryItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributorHistory extends Fragment {

    private RecyclerView rvDistributorHistory;

    private SharedPreferences myPref, tempPref;
    private String filePath = "";
    private int cMonth = 0;

    private TextView tvNoDataDisHis;
    private SalesBeatDb salesBeatDb;
    private ProgressBar pbDisHis;
    private JsonObjectRequest jsonObjectRequest;

    private SwipeRefreshLayout disHisRefresh;

    private String startDate, endDate,imgLatLong;

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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.distributor_history, parent, false);
        myPref = requireContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = requireContext().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        rvDistributorHistory = view.findViewById(R.id.rvDistributorHistory);
        tvNoDataDisHis = view.findViewById(R.id.tvNoDataDisHis);
        pbDisHis = view.findViewById(R.id.pbDisHis);
        disHisRefresh = view.findViewById(R.id.disHisRefresh);
        MaterialCalendarView calendarDistributorHistoryDay = view.findViewById(R.id.calendarDistributorHistoryDay);

        //UtilityClass utilityClass = new UtilityClass(getContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        rvDistributorHistory.setVisibility(View.GONE);
        tvNoDataDisHis.setVisibility(View.GONE);
        pbDisHis.setVisibility(View.VISIBLE);

        filePath = getActivity().getExternalFilesDir(null).getAbsolutePath() + "/recorded_audio.m4a";

        new PingServer(internet -> {
            /* do something with boolean response */
            if (!internet) {

                Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                disHisRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();

                initializeDistributorHistoryList();

            } else {

                getNewDitributorHistory(startDate, endDate);

                    /*    if (utilityClass.isInternetConnected())
                            getNewDitributorHistory(startDate, endDate);
                        else {

                            disHisRefresh.setRefreshing(false);
                            Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();

                            initializeDistributorHistoryList();
                        }*/
            }

        });

        disHisRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //current date string
//                Calendar cc = java.util.Calendar.getInstance();
//                cMonth = cc.get(Calendar.MONTH);
//                SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
//                String curentDateString = sdff.format(cc.getTime());
//
//                Pair pair = getDateRange(0);
//                String startDate = (String) pair.first;


                rvDistributorHistory.setVisibility(View.GONE);
                tvNoDataDisHis.setVisibility(View.GONE);
                pbDisHis.setVisibility(View.VISIBLE);

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {

                        Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                        disHisRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();

                        initializeDistributorHistoryList();

                    } else {

                        getNewDitributorHistory(startDate, endDate);

                    /*    if (utilityClass.isInternetConnected())
                            getNewDitributorHistory(startDate, endDate);
                        else {

                            disHisRefresh.setRefreshing(false);
                            Toast.makeText(getContext(), "You are not connected to internet", Toast.LENGTH_SHORT).show();

                            initializeDistributorHistoryList();
                        }*/
                    }

                });

            }
        });


        calendarDistributorHistoryDay.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                int val = date.getMonth() - cMonth;
                Pair pair = getDateRange(val);
                startDate = (String) pair.first;
                endDate = (String) pair.second;
                Log.e("DATE", "<--->" + startDate + "<--->" + endDate);

                rvDistributorHistory.setVisibility(View.GONE);
                tvNoDataDisHis.setVisibility(View.GONE);
                pbDisHis.setVisibility(View.VISIBLE);

                if (jsonObjectRequest != null)
                    jsonObjectRequest.cancel();

               /* if (utilityClass.isInternetConnected())
                    getNewDitributorHistory(startDate, endDate);
                else
                    initializeDistributorHistoryList();*/

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        initializeDistributorHistoryList();
                        Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        getNewDitributorHistory(startDate, endDate);
                    }

                });


            }
        });


        Calendar max = Calendar.getInstance();
        max.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));

        calendarDistributorHistoryDay.state().edit()
                .setMaximumDate(max)
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

    public void onResume() {
        super.onResume();

        if (!tempPref.getBoolean("Flag", false)) {

            //current date string
            Calendar cc = java.util.Calendar.getInstance();
            cMonth = cc.get(Calendar.MONTH);
            SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
            endDate = sdff.format(cc.getTime());

            Pair pair = getDateRange(0);
            startDate = (String) pair.first;


            rvDistributorHistory.setVisibility(View.GONE);
            tvNoDataDisHis.setVisibility(View.GONE);
            pbDisHis.setVisibility(View.VISIBLE);


            new PingServer(internet -> {
                /* do something with boolean response */
                if (!internet) {
                    Toast.makeText(getActivity(), "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    initializeDistributorHistoryList();
                } else {
                    getNewDitributorHistory(startDate, endDate);
                }

            });

            /*if (utilityClass.isInternetConnected())
                getNewDitributorHistory(startDate, endDate);
            else {

                initializeDistributorHistoryList();
            }*/
        }

        SharedPreferences.Editor editor = tempPref.edit();
        editor.remove("Flag");
        editor.apply();
    }

    public Pair<String, String> getDateRange(int val) {
        String begining, end;
        SimpleDateFormat sdff = new SimpleDateFormat("yyyy-MM-dd");
        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, val);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            setTimeToBeginningOfDay(calendar);

            begining = sdff.format(calendar.getTime());
        }

        {
            Calendar calendar = getCalendarForNow();
            calendar.add(Calendar.MONTH, val);
            calendar.set(Calendar.DAY_OF_MONTH,
                    calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            setTimeToEndofDay(calendar);
            end = sdff.format(calendar.getTime());
        }

        return Pair.create(begining, end);
    }

    private void getNewDitributorHistory(final String startDate, final String endDate) {
        Log.d("TAG", "getNewDitributorHistory startDate: "+startDate);
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_NEW_DISTRIBUTOR_HISTORY + "?EmployeeId=" + myPref.getString(getString(R.string.emp_id_key), "") + "&fromDate=" + startDate + "&toDate=" + endDate,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                disHisRefresh.setRefreshing(false);
                Log.d("TAG", "New Distributor history===: "+response);
                //loader.dismiss();
                try {
                    pbDisHis.setVisibility(View.GONE);
                    if(response.getInt("status")==1)
                    {
                    salesBeatDb.deleteAllDataFromNewDistributorTable();

                    JSONObject data = response.getJSONObject("data");
                    JSONArray distributors = data.getJSONArray("distributors");
                    if(distributors.length() == 0){
                        tvNoDataDisHis.setVisibility(View.VISIBLE);
                        return;
                    }
                    for (int i = 0; i < distributors.length(); i++)
                    {

//                        JSONObject object = (JSONObject) distributors.get(i);

                        JSONObject distributorWrapper = distributors.getJSONObject(i);
                        JSONObject object = distributorWrapper.getJSONObject("distributor");

                        ArrayList<String> newDistributorDetails = new ArrayList<>();
                        String tempDid = object.getString("ndid");
                        newDistributorDetails.add(object.getString("firmName"));
                        newDistributorDetails.add(object.getString("firmAddress"));
                        newDistributorDetails.add(object.getString("pin"));
                        newDistributorDetails.add(object.getString("city"));
                        newDistributorDetails.add(object.getString("state"));
                        newDistributorDetails.add(object.getString("ownerName"));
                        newDistributorDetails.add(object.getString("ownerMobile1"));
                        newDistributorDetails.add(object.getString("email"));
                        newDistributorDetails.add(object.getString("ownerMobile2"));
                        newDistributorDetails.add(object.getString("gstin"));
                        newDistributorDetails.add(object.getString("fsi"));
                        newDistributorDetails.add(object.getString("pan"));
                        newDistributorDetails.add(object.getString("monthlyTurnover"));
                        newDistributorDetails.add(object.getString("ownerImage"));

/*
                        if(!object.get("ownerImageTimeStamp").equals("null"))
                        {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                            String dt="";
                            Date date = null;
                            try {
                                String TokenValidTo = UtilityClass.UTCToLocal("yyyy-MM-dd'T'HH:mm:ss'Z'","yyyy-MM-dd HH:mm:ss",object.getString("ownerImageTimeStamp"));;
                                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date TokenTime = sdf.parse(TokenValidTo);

                                date = simpleDateFormat.parse(object.getString("ownerImageTimeStamp"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            if (date == null) {
                                dt="";
                            }

                            SimpleDateFormat convetDateFormat = new SimpleDateFormat("hh:mm a");

                            dt= convetDateFormat.format(date);
                        }
*/

                        newDistributorDetails.add(object.getString("ownerImageTimeStampStr")); //@Umesh
                        //newDistributorDetails.add(""); //@Umesh
                        newDistributorDetails.add(object.getString("beatShopCount"));
                        newDistributorDetails.add(object.getString("investmentPlan"));
                        newDistributorDetails.add(object.getString("workingSince"));
                        newDistributorDetails.add(object.getString("otherContactPersonNames"));
                        newDistributorDetails.add(object.getString("otherContactPersonPhones"));
                        newDistributorDetails.add(object.getString("firmImage"));
//                        newDistributorDetails.add(object.getString("firmImageTimeStamp")); //@Umesh
                        newDistributorDetails.add(""); //@Umesh
                        newDistributorDetails.add(object.getString("opinion"));
                        newDistributorDetails.add(object.getString("comment"));
                        newDistributorDetails.add(object.getString("other_brand"));

                        List<String> listProductDivision = new ArrayList<>();
                        listProductDivision.add(object.getString("productDivision"));
                        List<String> listWorkingBrand = new ArrayList<>();
                        listWorkingBrand.add(object.getString("workingBrand"));
                        List<String> listBeatName = new ArrayList<>();
                        listBeatName.add(object.getString("beatName"));
                        List<Double> ownerImageLatLong = new ArrayList<>();
                        Log.d("TAG", "ownerImageLatLong: "+object.getString("ownerImageLatLong"));

                        if (object.getString("ownerImageLatLong") != null && !object.getString("ownerImageLatLong").isEmpty()) {
                            imgLatLong = object.getString("ownerImageLatLong");
                        } else {
                            imgLatLong = "";
                        }

                        ownerImageLatLong.add(0.0);//Double.valueOf(object.getString("ownerImageLatLong"))
                        List<Double> firmImageLatLong = new ArrayList<>();
                        firmImageLatLong.add(0.0);//Double.valueOf(object.getString("firmImageLatLong"))

                        salesBeatDb.insertInNewDistributorTable2(tempDid, newDistributorDetails,
                                listBeatName, listProductDivision, ownerImageLatLong, firmImageLatLong);

                        initializeDistributorHistoryList();
                      }
                    }
                    else {
                        Log.e("Distibutor History",response.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TAG", "Distri error: "+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "Distri onErrorResponse: "+error.toString());
                disHisRefresh.setRefreshing(false);
                rvDistributorHistory.setVisibility(View.GONE);
                pbDisHis.setVisibility(View.GONE);
                tvNoDataDisHis.setVisibility(View.VISIBLE);
                error.printStackTrace();
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

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);

    }

    private String convertFileToBase64(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytesArray = new byte[(int) file.length()];
            fileInputStream.read(bytesArray);
            String base64String = Base64.encodeToString(bytesArray, Base64.DEFAULT);
            fileInputStream.close();
            return "data:audio/mpeg;base64," + base64String;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void initializeDistributorHistoryList() {

        ArrayList<ClaimHistoryItem> disHistory = new ArrayList<>();
        Cursor distributorHistory = null;

        try {

            disHistory.clear();
            distributorHistory = salesBeatDb.getAllDataFromNewDistributorTable2();
            if (distributorHistory != null
                    && distributorHistory.getCount() > 0
                    && distributorHistory.moveToFirst()) {

                do {

                    ClaimHistoryItem disH = new ClaimHistoryItem();
                    disH.setFirmName(distributorHistory.getString(distributorHistory.getColumnIndex("name_of_firm")));
                    disH.setFirmAddress(distributorHistory.getString(distributorHistory.getColumnIndex("firm_address")));
                    disH.setPin(distributorHistory.getString(distributorHistory.getColumnIndex("pincode")));
                    disH.setCity(distributorHistory.getString(distributorHistory.getColumnIndex("city")));
                    disH.setState(distributorHistory.getString(distributorHistory.getColumnIndex("state")));
                    disH.setOwnerName(distributorHistory.getString(distributorHistory.getColumnIndex("owner_name")));
                    disH.setMobile1(distributorHistory.getString(distributorHistory.getColumnIndex("owner_mobile_no1")));
                    disH.setMobile2(distributorHistory.getString(distributorHistory.getColumnIndex("owner_mobile_no2")));
                    disH.setOwnerEmail(distributorHistory.getString(distributorHistory.getColumnIndex("email_id")));
                    disH.setGstin(distributorHistory.getString(distributorHistory.getColumnIndex("gstin")));
                    disH.setFssai(distributorHistory.getString(distributorHistory.getColumnIndex("fssai_no")));
                    disH.setPan(distributorHistory.getString(distributorHistory.getColumnIndex("pan_no")));
                    disH.setMonthlyTurnOver(distributorHistory.getString(distributorHistory.getColumnIndex("monthly_turnover")));
                    disH.setBeat1(distributorHistory.getString(distributorHistory.getColumnIndex("beat_name")));
                    disH.setBeat2(distributorHistory.getString(distributorHistory.getColumnIndex("beat_name")));
                    disH.setNoOfShop(distributorHistory.getString(distributorHistory.getColumnIndex("no_of_shop_in_beat")));
                    disH.setInvestmentPlan(distributorHistory.getString(distributorHistory.getColumnIndex("investment_plan")));
                    disH.setProduct(distributorHistory.getString(distributorHistory.getColumnIndex("product_division")));
                    disH.setWorkingSince(distributorHistory.getString(distributorHistory.getColumnIndex("working_since")));
                    disH.setOtherPerson(distributorHistory.getString(distributorHistory.getColumnIndex("other_contact_person_name")));
                    disH.setOtherPersonMob(distributorHistory.getString(distributorHistory.getColumnIndex("other_contact_person_phn")));
                    disH.setOpDis(distributorHistory.getString(distributorHistory.getColumnIndex("opinion_about_distributor")));
                    disH.setAddedDate(distributorHistory.getString(distributorHistory.getColumnIndex("owner_image_time_stamp")));
                    disH.setRemarks(distributorHistory.getString(distributorHistory.getColumnIndex("comment")));
                    disH.setOtherBrands(distributorHistory.getString(distributorHistory.getColumnIndex("other_brand")));

                    disHistory.add(disH);

                } while (distributorHistory.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (distributorHistory != null)
                distributorHistory.close();
        }

        //LIFO list
        Collections.reverse(disHistory);

        if (disHistory.size() > 0) {

            DistributorHistoryListAdapter historyListAdapter = new DistributorHistoryListAdapter(getContext(), disHistory,imgLatLong);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            rvDistributorHistory.setLayoutManager(layoutManager);
            rvDistributorHistory.setAdapter(historyListAdapter);
            rvDistributorHistory.setVisibility(View.VISIBLE);
            tvNoDataDisHis.setVisibility(View.GONE);
            pbDisHis.setVisibility(View.GONE);

        } else {

            rvDistributorHistory.setVisibility(View.GONE);
            pbDisHis.setVisibility(View.GONE);
            tvNoDataDisHis.setVisibility(View.VISIBLE);
        }
    }

}
