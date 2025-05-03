package com.newsalesbeatApp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.CustomCalendarAdapter;
import com.newsalesbeatApp.adapters.DailyAssesmentAdapter;
import com.newsalesbeatApp.adapters.SbMenuListAdapter;
import com.newsalesbeatApp.interfaces.VolleyCallback;
import com.newsalesbeatApp.pojo.MyCategory;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.GridDividerDecoration;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.sentry.ITransaction;
import io.sentry.Sentry;
import io.sentry.SpanStatus;
import io.sentry.TransactionOptions;

/*
 * Created by Dhirendra Thakur on 08-11-2017.
 */

public class MyMenuFragment extends Fragment {

    public static final int DOWNLOADED = 11;
    public static final int ERROR = 22;
    private final String TAG = "MyMenuFragment";
    private final String categoryOptions[] = {"ORDER BOOKING RETAILING",
            "OTHER ACTIVITY", "MONTHLY CLOSING", "SCHEMES ", "DOCUMENTS", "My EXPENSES",
            "My TRAINING CENTER", "KNOWLEDGE BASE ", "QUICK SUPPORT"
    };
    private final int backImage[] = {R.drawable.ic_edit_black_24dp, R.drawable.ic_joint_work_black_24dp,
            R.drawable.attendance_icon, R.drawable.scheme_icon, R.drawable.ic_collections_black_24dp,
            R.drawable.ic_attach_money_black_24dp, R.drawable.presentation, R.drawable.alphabetical, R.drawable.tv_icon
    };
    private final String dateTemplate = "MMMM yyyy";
    //Fragment fragment;
    RecyclerView menuList;
    NestedScrollView scMyMenu;
    /*Attendance view*/
    Calendar calendar;
    CustomCalendarAdapter adapter;
    String[] monthName = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};
    SharedPreferences prefSFA;
    GridView empAttendanceGridView;
    TextView monthTitle;
    UtilityClass utilityClass;
    int currentMonthVal = 0;
    int monthVal = 0;
    int counter = 0;
    TextView tvCurrentDate, tvCurrentMonth;
    int month, year;
    ImageView nextMonth, previousMonth;
    Map<String, String> userStatus = new HashMap<>();
    SalesBeatDb salesBeatDb;
    int flag = 0;//0 loading,1 downloaded,2 error
    private Calendar _calendar;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.menu_layout, container, false);
        menuList = view.findViewById(R.id.menuList);
        scMyMenu = view.findViewById(R.id.scMyMenu);
        prefSFA = requireActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
        empAttendanceGridView = view.findViewById(R.id.gridview);
        monthTitle = view.findViewById(R.id.txtvMonthName);
        nextMonth = view.findViewById(R.id.imgNextMonth);
        previousMonth = view.findViewById(R.id.imgBackMonth);

        utilityClass = new UtilityClass(requireContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());
        calendar = Calendar.getInstance();
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        tvCurrentDate.setText(String.valueOf(cDay));
        currentMonthVal = calendar.get(Calendar.MONTH);
        monthVal = currentMonthVal;
        month = monthVal + 1;
        year = calendar.get(Calendar.YEAR);
        tvCurrentMonth.setText(monthName[currentMonthVal]);

        intitializeUaerStatus();

        return view;
    }

    private void intitializeUaerStatus() {

        //Cursor cursor = salesBeatDb.getAllRecordFromUserAttendanceTable();
        Log.e(TAG, " Month: " + month + " year: " + year);
        Cursor cursor = null;
        if (month < 10)
            cursor = salesBeatDb.getAllRecordFromUserAttendanceTable2("0" + month, String.valueOf(year));
        else
            cursor = salesBeatDb.getAllRecordFromUserAttendanceTable2(String.valueOf(month), String.valueOf(year));


        // Log.e(TAG," Current month: "+cursor.getCount());

        try {

            userStatus.clear();

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    @SuppressLint("Range") String attendance = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_ATTENDANCE_STATUS));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DATE));

                    userStatus.put(date, attendance);

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        Log.e(TAG, " ====> " + userStatus.size());

        //return true;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        boolean flag = intitializeUaerStatus();
//        if (flag){

        boolean loaded = initializeCalendar();

        if (loaded) {
            //initialize menu list
            initializeMenuList();
        }
//        }

    }

    private boolean initializeCalendar() {

        // Initialised
        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);

        Log.e(TAG, " ---> " + userStatus.size());

        DailyAssesmentAdapter adapter = new DailyAssesmentAdapter(getContext(), month, year, userStatus, flag);

        adapter.notifyDataSetChanged();
        empAttendanceGridView.setAdapter(adapter);

        monthTitle.setText(DateFormat.format(dateTemplate, _calendar.getTime()));

        return true;
    }

    public void onResume() {
        super.onResume();

        previousMonth.setOnClickListener(v -> {

            if (month <= 1) {
                month = 12;
                year--;
            } else {
                month--;
            }

            Cursor cursor = null;
            if (month < 10)
                cursor = salesBeatDb.getAllRecordFromUserAttendanceTable2("0" + month, String.valueOf(year));
            else
                cursor = salesBeatDb.getAllRecordFromUserAttendanceTable2(String.valueOf(month), String.valueOf(year));

            if (cursor != null && cursor.getCount() > 0) {

                intitializeUaerStatus();

                setGridCellAdapterToDate(month, year);
            } else if (utilityClass.isInternetConnected()) {
                SharedPreferences prefSFA = getActivity().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
                Sentry.configureScope(scope -> {
                    scope.setTag("page_locale", "en_US");
                    scope.setExtra(prefSFA.getString("username", ""), prefSFA.getString("password", ""));
//                    scope.setExtra("user_id", "123456");
//            scope.setUser(new UserBuilder().setIpAddress("192.168.0.1").build());
                });

                TransactionOptions txOptions = new TransactionOptions();
                txOptions.setBindToScope(true);
                ITransaction transaction = Sentry.startTransaction("MyMenuFragment", "getEmpOutputByMonth",txOptions);
                try {
                    if (transaction == null) {
                        transaction = Sentry.startTransaction("processOrderBatch()", "task");
                    }
                    // span operation: task, span description: operation
//                    ISpan innerSpan = transaction.startChild("SalesBeat Check", "operation");
                    getMonthlyAttendance(String.valueOf(month), String.valueOf(year));

                } catch (Exception e) {
                    transaction.setThrowable(e);
                    transaction.setStatus(SpanStatus.INTERNAL_ERROR);
                    throw e;
                } finally {
                    transaction.finish();
                }


                intitializeUaerStatus();

                setGridCellAdapterToDate(month, year);
            }


        });

        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (month > 11) {
                    month = 1;
                    year++;
                } else {
                    month++;
                }

                Calendar calendar = Calendar.getInstance();
                int curMonth = calendar.get(Calendar.MONTH) + 1;
                int curYear = calendar.get(Calendar.YEAR);

                Log.e(TAG, curMonth + "  " + curYear + "----->" + month + "  " + year);

                intitializeUaerStatus();

                if (year < curYear) {

                    setGridCellAdapterToDate(month, year);

                } else if (year == curYear && month <= curMonth) {

                    setGridCellAdapterToDate(month, year);

                } else {

                    if (month <= 1) {
                        month = 12;
                        year--;
                    } else {
                        month--;
                    }

                    Toast.makeText(requireContext(), "No data", Toast.LENGTH_SHORT).show();
                }


            }
        });

        scMyMenu.fullScroll(View.FOCUS_UP);
        scMyMenu.pageScroll(View.FOCUS_UP);
    }

    private void initializeMenuList() {

        ArrayList<MyCategory> androidVersions = prepareData();

        final SbMenuListAdapter adapter = new SbMenuListAdapter(getContext(), androidVersions);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        menuList.setLayoutManager(layoutManager);
        menuList.setHasFixedSize(true);
        menuList.addItemDecoration(new GridDividerDecoration(getContext()));
        menuList.setAdapter(adapter);

    }

    private ArrayList<MyCategory> prepareData() {

        ArrayList<MyCategory> android_version = new ArrayList<>();
        for (int i = 0; i < categoryOptions.length; i++) {
            MyCategory myCategory = new MyCategory();

            myCategory.setCategoryName(categoryOptions[i]);
            myCategory.setIcon(backImage[i]);
            android_version.add(myCategory);
        }
        return android_version;
    }

    private void setGridCellAdapterToDate(int month, int year) {

        DailyAssesmentAdapter adapter = new DailyAssesmentAdapter(getContext(), month, year, userStatus, flag);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        monthTitle.setText(DateFormat.format(dateTemplate,
                _calendar.getTime()));
        adapter.notifyDataSetChanged();
        empAttendanceGridView.setAdapter(adapter);

    }

    @SuppressLint("RestrictedApi")
    private void getMonthlyAttendance(String month, String year) {

        Log.e(TAG, "Month--->" + month + " & year-->" + year);

        getEmployeeRecordByMonthAndYear(month, year, result -> {

            String checkInTime = "", checkOutTime = "", totalCall = "", productiveCall = "", lineSold = "",
                    attendance = "", date = "", totalWorkingTime = "", totalRetailingTime = "", reason = "";

            //Log.e(TAG,"Response: "+reason);

            try {
                //@Umesh 02-Feb-2022
                if(result.getInt("status")==1)
                {
                    JSONArray attendanceArr = result.getJSONArray("data");

                    for (int i = 0; i < attendanceArr.length(); i++)
                    {
                        JSONObject response = (JSONObject) attendanceArr.get(i);

                        if (response.has("attendance") && !response.isNull("attendance"))
                            attendance = response.getString("attendance");
                        else
                            attendance = "";

                        if (response.has("date") && !response.isNull("date"))
                            date = response.getString("date");
                        else
                            date = "";

                        if (response.has("checkIn") && !response.isNull("checkIn"))
                            checkInTime = response.getString("checkIn");
                        else
                            checkInTime = "";

                        if (response.has("checkOut") && !response.isNull("checkOut"))
                            checkOutTime = response.getString("checkOut");
                        else
                            checkOutTime = "";

                        if (response.has("totalCalls") && !response.isNull("totalCalls"))
                            totalCall = response.getString("totalCalls");
                        else
                            totalCall = "";

                        if (response.has("productiveCalls") && !response.isNull("productiveCalls"))
                            productiveCall = response.getString("productiveCalls");
                        else
                            productiveCall = "";

                        if (response.has("linesSold") && !response.isNull("linesSold"))
                            lineSold = response.getString("linesSold");
                        else
                            lineSold = "";

                        if (response.has("totalWorkingTime") && !response.isNull("totalWorkingTime"))
                            totalWorkingTime = response.getString("totalWorkingTime");
                        else
                            totalWorkingTime = "";

                        if (response.has("totalRetailingTime") && !response.isNull("totalRetailingTime"))
                            totalRetailingTime = response.getString("totalRetailingTime");
                        else
                            totalRetailingTime = "";

                        if (response.has("reason") && !response.isNull("reason"))
                            reason = response.getString("reason");
                        else
                            reason = "";

                        String tempDate = "";
                        String month1 = "";
                        String year1 = "";

                        if (!date.isEmpty()) {
                            String[] temp = date.split(" ");
                            tempDate = temp[0];
                            String[] tDate = tempDate.split("-");
                            month1 = tDate[1];
                            year1 = tDate[0];
                        }

                        Log.e(TAG, " day: " + tempDate + " month: " + month1 + " year: " + year1);
                        salesBeatDb.insertUserAttendance(attendance, checkInTime, checkOutTime, tempDate, totalCall,
                                productiveCall, lineSold, totalWorkingTime, totalRetailingTime, reason, month1, year1);
                    }
                    Loaded loaded = new Loaded(new Handler());
                    loaded.send(DOWNLOADED, new Bundle());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Loaded loaded = new Loaded(new Handler());
                loaded.send(ERROR, new Bundle());
            }

        });
    }

    private void getEmployeeRecordByMonthAndYear(final String month, final String year, final VolleyCallback volleyCallback) {

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_GET_EMP_RECORD_BY_MONTH
                ,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject responseObj) {
                Log.e(TAG, "Response EmployeeRecordByMonthAndYear: " + responseObj);

                volleyCallback.onSuccessResponse(responseObj);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {

                    SbLog.printError(TAG, "getEmpOutputByMonth",
                            String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                            prefSFA.getString(getString(R.string.emp_id_key), ""));

                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {
                            responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.e("ERRR", "===== " + responseBody);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    error.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }) {

            public byte[] getBody() {
                HashMap<String, String> params2 = new HashMap<>();
                params2.put("month", month);
                params2.put("year", year);
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

        objectRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(requireContext()).add(objectRequest);
    }

    @SuppressLint("RestrictedApi")
    private class Loaded extends ResultReceiver {

        private Loaded(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case DOWNLOADED:

                    flag = 1;
                    intitializeUaerStatus();

                    setGridCellAdapterToDate(month, year);

                    break;

                case ERROR:

                    flag = 2;

                    break;

            }
            super.onReceiveResult(resultCode, resultData);
        }

    }
}
