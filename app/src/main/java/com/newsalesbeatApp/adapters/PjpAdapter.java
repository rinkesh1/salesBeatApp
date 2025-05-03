package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.CreatePjp;
import com.newsalesbeatApp.pojo.MyPjp;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PjpAdapter extends RecyclerView.Adapter<PjpAdapter.MyViewholder> {

    private static final int DAY_OFFSET = 1;
    private final List<String> list;
    private final String[] months = {"Jan", "Feb", "March",
            "Apr", "May", "June", "July", "Aug", "Sep",
            "Oct", "Nov", "Dec"};
    private final String[] dayOfTheWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private final int[] daysOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30,
            31, 30, 31};
    public ArrayList<MyPjp> pjpList;
    int count = 0;
    private Context context;
    private SharedPreferences myPref;
    private int currentDayOfMonth;
    private int currentWeekDay;

    //------------------------------
    private RelativeLayout rlTownList, rlDistributorList, rlBeatList, rlEmpList;
    private LinearLayout llTcPc;
    private EditText edtDate, edtRemarks, edtTc, edtPc, edtSale;
    private ImageView imgDate, imgActivityList, imgTownList, imgDistributorList, imgBeatList, imgEmployeeList;
    private AutoCompleteTextView actvActivityList, actvTownList, actvDistributorList, actvBeatList, actvEmployeeList;

    private Button btnCreatePjp;

    private ArrayList<String> activityList = new ArrayList<>();
    private ArrayList<String> empList = new ArrayList<>();
    private ArrayList<String> empIdList = new ArrayList<>();
    private ArrayList<String> townItems = new ArrayList<>();
    private ArrayList<String> disList = new ArrayList<>();
    private ArrayList<String> didList = new ArrayList<>();
    private ArrayList<String> beatList = new ArrayList<>();
    private ArrayList<String> beaIDtList = new ArrayList<>();

    private SalesBeatDb salesBeatDb;
    //private UtilityClass utilityClass;
    private int pos = 0;


    private String did;
    private String bid;
    private String eid;

    public PjpAdapter(Context context, ArrayList<MyPjp> pjpList, int month, int year) {
        this.context = context;
        this.pjpList = pjpList;
        this.list = new ArrayList<>();
        salesBeatDb = SalesBeatDb.getHelper(context);
        //utilityClass = new UtilityClass(context);
        myPref = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
        setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

        // Print Month
        printMonth(month, year);
    }

    @SuppressLint("LongLogTag")
    private void printMonth(int mm, int yy) {

        int trailingSpaces = 0;
        int daysInPrevMonth = 0;
        int prevMonth = 0;
        int prevYear = 0;
        int nextMonth = 0;
        int nextYear = 0;

        int currentMonth = mm - 1;
        //String currentMonthName = getMonthAsString(currentMonth);
        int daysInMonth = getNumberOfDaysOfMonth(currentMonth);

        GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

        if (currentMonth == 11) {

            prevMonth = currentMonth - 1;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 0;
            prevYear = yy;
            nextYear = yy + 1;
        } else if (currentMonth == 0) {

            prevMonth = 11;
            prevYear = yy - 1;
            nextYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            nextMonth = 1;

        } else {

            prevMonth = currentMonth - 1;
            nextMonth = currentMonth + 1;
            nextYear = yy;
            prevYear = yy;
            daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);

        }

        currentWeekDay = cal.get(Calendar.DAY_OF_WEEK);
        Log.d("TAG****", "" + currentWeekDay);
        //trailingSpaces = currentWeekDay;

        if (cal.isLeapYear(cal.get(Calendar.YEAR)))
            if (mm == 2)
                ++daysInMonth;
            else if (mm == 3)
                ++daysInPrevMonth;

        // Trailing Month days
//        for (int i = 0; i < trailingSpaces; i++) {
//
//            list.add(String
//                    .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
//                            + i)
//                    + "-GREY"
//                    + "-"
//                    + getMonthAsString(prevMonth)
//                    + "-"
//                    + prevYear);
//        }

        // Current Month Days
        for (int i = 1; i <= daysInMonth; i++) {

            if (i == getCurrentDayOfMonth() && currentMonth == Calendar.getInstance().get(Calendar.MONTH)) {
                list.add(String.valueOf(i) + "-BLUE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);
            } else {
                list.add(String.valueOf(i) + "-WHITE" + "-"
                        + getMonthAsString(currentMonth) + "-" + yy);
            }
        }

        // Leading Month days
//        for (int i = 0; i < list.size() % 7; i++) {
//
//            list.add(String.valueOf(i + 1) + "-GREY" + "-"
//                    + getMonthAsString(nextMonth) + "-" + nextYear);
//        }
//
//        for (int j = 0;j < list.size();j++){
//
//            String [] day_color = list.get(j).split("-");
//            if (day_color[1].equalsIgnoreCase("GREY"))
//                list.remove(j);
//        }
    }

    private int getNumberOfDaysOfMonth(int i) {
        return daysOfMonth[i];
    }

    private String getMonthAsString(int i) {
        return months[i];
    }

    public int getCurrentDayOfMonth() {
        return currentDayOfMonth;
    }

    private void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.currentDayOfMonth = currentDayOfMonth;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }


    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.create_pjp_list_row, parent, false);
        return new MyViewholder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewholder holder, final int position) {

        String[] day_color = list.get(position).split("-");

        String theday = day_color[0];
        int themonth = 0;//Integer.parseInt(day_color[2]);
        for (int i = 0; i < months.length; i++) {

            if (months[i].equalsIgnoreCase(day_color[2])) {
                themonth = i + 1;
            }
        }

        int theyear = Integer.parseInt(day_color[3]);
        //checking for PJP
        if (pjpList != null && pjpList.size() > 0) {

            boolean chkPjp = checkForPJP(theday, themonth, theyear);

            if (chkPjp) {

                String activity = pjpList.get(pos).getActivity();
                String town = pjpList.get(pos).getTownName();
                String distributor = pjpList.get(pos).getDistributorName();
                String beat = pjpList.get(pos).getBeatName();
                String emp = pjpList.get(pos).getEmp();
                String remarks = pjpList.get(pos).getRemarks();
                String tc = pjpList.get(pos).getTc();
                String pc = pjpList.get(pos).getPc();
                String sale = pjpList.get(pos).getSale();

                // 1 Retailing
                // 2 Meeting
                // 3 Joint Working
                // 4 Distributor Search
                // 5 Leave
                // 6 Weak Off
                // 7 Market Survey
                // 8 Holiday
                // 9 Office Visit

                if (activity.equalsIgnoreCase("1")) {

                    holder.tvActivityPjp.setText("Retailing");
                    holder.tvTownPjp.setText(town);
                    holder.tvDisributorPjp.setText(distributor);
                    holder.tvBeatPjp.setText(beat);
                    holder.tvTCPJP.setText("Tc: " + tc);
                    holder.tvPCPjp.setText("Pc: " + pc);
                    holder.tvSALEPjp.setText(sale + "" + context.getString(R.string.unitt));

                    holder.llDisPjp.setVisibility(View.VISIBLE);
                    holder.llBeatPjp.setVisibility(View.VISIBLE);
                    holder.llTCPCSAle.setVisibility(View.VISIBLE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                } else if (activity.equalsIgnoreCase("2")) {

                    holder.tvActivityPjp.setText("Meeting");
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                } else if (activity.equalsIgnoreCase("3")) {

                    holder.tvActivityPjp.setText("Joint Working");
                    holder.tvTownPjp.setText(town);
                    holder.tvDisributorPjp.setText(distributor);
                    holder.tvEmployeePjp.setText(emp);


                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.VISIBLE);

                } else if (activity.equalsIgnoreCase("4")) {

                    holder.tvActivityPjp.setText("Distributor Search");
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                } else if (activity.equalsIgnoreCase("5")) {

                    holder.tvActivityPjp.setText("Leave");
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                } else if (activity.equalsIgnoreCase("6")) {

                    holder.tvActivityPjp.setText("Weak Off");
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                } else if (activity.equalsIgnoreCase("7")) {

                    holder.tvActivityPjp.setText("Market Survey");
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                } else if (activity.equalsIgnoreCase("8")) {

                    holder.tvActivityPjp.setText("Holiday");
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);
                } else if (activity.equalsIgnoreCase("9")) {

                    holder.tvActivityPjp.setText("Office Visit");
                    holder.tvTownPjp.setText("N/A");
                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);
                }

                holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);

            } else {

                holder.llCreatedPjpDetail.setVisibility(View.GONE);
            }

        }

        Cursor cursor = null;
        try {

            String date = "";
            if (themonth < 10) {
                if (Integer.parseInt(theday) < 10)
                    date = theyear + "-0" + themonth + "-0" + theday;
                else
                    date = theyear + "-0" + themonth + "-" + theday;
            } else {
                if (Integer.parseInt(theday) < 10)
                    date = theyear + "-" + themonth + "-0" + theday;
                else
                    date = theyear + "-" + themonth + "-" + theday;
            }

            cursor = salesBeatDb.getPjp(date);
            Log.e("TAGGG", date + "==> " + cursor.getCount() + "  Pos: " + position);

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                String activity = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_ACTIVITY));
                String town = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_TOWN));
                String did = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRIBUTOR_ID));
                String distributor = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_DISTRIBUTOR));
                String bid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_BEAT_ID));
                String beat = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_BEAT));
                String eid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_EMP_ID));
                String emp = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_EMP));
                String tc = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_TC_PJP));
                String pc = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_PC_PJP));
                String sale = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_SALE_PJP));

                MyPjp myPjp = new MyPjp();

                myPjp.setRemarks("");

                if (activity.equalsIgnoreCase("Retailing")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);
                    holder.tvDisributorPjp.setText(distributor);
                    holder.tvBeatPjp.setText(beat);
                    holder.tvTCPJP.setText(tc);
                    holder.tvPCPjp.setText(pc);
                    holder.tvSALEPjp.setText(sale);

                    holder.llDisPjp.setVisibility(View.VISIBLE);
                    holder.llBeatPjp.setVisibility(View.VISIBLE);
                    holder.llTCPCSAle.setVisibility(View.VISIBLE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setDistributor_id(did);
                    myPjp.setBeat_id(bid);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);
                    myPjp.setTc(tc);
                    myPjp.setPc(pc);
                    myPjp.setSale(sale);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Meeting")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Joint Working")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);
                    holder.tvDisributorPjp.setText(distributor);
                    holder.tvEmployeePjp.setText(emp);


                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.VISIBLE);

                    myPjp.setActivity(activity);
                    myPjp.setDistributor_id(did);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);
                    myPjp.setEmp(eid);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Distributor Search")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Leave")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Weak Off")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Market Survey")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Holiday")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                } else if (activity.equalsIgnoreCase("Office Visit")) {

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText("N/A");

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(date);

                    pjpList.add(myPjp);

                }

                holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                count++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        Cursor cursorPjp = salesBeatDb.getAllPjp();
        if (cursorPjp != null && cursorPjp.getCount() > 0) {
            CreatePjp.tvSavePjp.setClickable(true);
            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
        }

        // Set the Day GridCell
        holder.tvDatePjp.setText(day_color[0] + " " + day_color[2]);
        holder.tvDayOfWeekPjp.setText("(" + getDayOfTheWeek(day_color[0] + " " + day_color[2] + "," + day_color[3]) + ")");

        holder.imgEditPjp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] dateArr = list.get(position).split("-");
                String day = dateArr[0];
                String month = dateArr[2];
                String year = dateArr[3];
                int mth = 0;
                for (int i = 0; i < months.length; i++) {

                    if (months[i].equalsIgnoreCase(month))
                        mth = i;

                }

                int theday = Integer.parseInt(day);
                String date = "";
                if (mth < 10) {
                    if (theday < 10)
                        date = year + "-0" + (mth + 1) + "-0" + day;
                    else
                        date = year + "-0" + (mth + 1) + "-" + day;
                } else {
                    if (theday < 10)
                        date = year + "-" + (mth + 1) + "-0" + day;
                    else
                        date = year + "-" + (mth + 1) + "-" + day;
                }

                showDialog(date, holder, position);
            }
        });
    }

    private boolean checkForPJP(String theday, int themonth, int theyear) {

        for (int i = 0; i < pjpList.size(); i++) {

            try {

                String[] pjp = pjpList.get(i).getDate().split("-");
                int pjpDay = Integer.parseInt(pjp[2]);
                int pjpMonthh = Integer.parseInt(pjp[1]);
                int pjpYear = Integer.parseInt(pjp[0]);

                int fillDay = Integer.parseInt(theday);

                if ((fillDay == pjpDay) && (themonth == pjpMonthh) && (theyear == pjpYear)) {
                    pos = i;
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    private String getDayOfTheWeek(String input_date) {

        SimpleDateFormat format1 = new SimpleDateFormat("dd MMM,yyyy");
        Date dt1 = null;
        try {
            dt1 = format1.parse(input_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");
        String finalDay = format2.format(dt1);

        return finalDay;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void showDialog(String date, final MyViewholder holder, int position) {


        final Dialog createPjpDialog = new Dialog(context);
        createPjpDialog.setContentView(R.layout.create_pjp_dialog);
        rlTownList = createPjpDialog.findViewById(R.id.rlTownList);
        rlDistributorList = createPjpDialog.findViewById(R.id.rlDistributorList);
        rlBeatList = createPjpDialog.findViewById(R.id.rlBeatList);
        rlEmpList = createPjpDialog.findViewById(R.id.rlEmpList);
        llTcPc = createPjpDialog.findViewById(R.id.llTcPc);
        edtDate = createPjpDialog.findViewById(R.id.edtDate);
        edtRemarks = createPjpDialog.findViewById(R.id.edtRemarksPjp);
        edtTc = createPjpDialog.findViewById(R.id.edtTcPjp);
        edtPc = createPjpDialog.findViewById(R.id.edtPcPjp);
        edtSale = createPjpDialog.findViewById(R.id.edtSalePjp);
        //imgDate = createPjpDialog.findViewById(R.id.imgDate);
        imgActivityList = createPjpDialog.findViewById(R.id.imgActivityList);
        imgTownList = createPjpDialog.findViewById(R.id.imgTownList);
        imgDistributorList = createPjpDialog.findViewById(R.id.imgDistributorList);
        imgBeatList = createPjpDialog.findViewById(R.id.imgBeatList);
        imgEmployeeList = createPjpDialog.findViewById(R.id.imgEmployeeList);
        actvActivityList = createPjpDialog.findViewById(R.id.actvActivityList);
        actvTownList = createPjpDialog.findViewById(R.id.actvTownList);
        actvDistributorList = createPjpDialog.findViewById(R.id.actvDistributorList);
        actvBeatList = createPjpDialog.findViewById(R.id.actvBeatList);
        actvEmployeeList = createPjpDialog.findViewById(R.id.actvEmployeeList);
        btnCreatePjp = createPjpDialog.findViewById(R.id.btnCreatePjp);

        activityList.clear();
        activityList.add("Retailing");
        activityList.add("Joint Working");
        activityList.add("Distributor Search");
        activityList.add("Market Survey");
        activityList.add("Meeting");
        activityList.add("Office Visit");
        activityList.add("Leave");
        activityList.add("Weekly Off");
        activityList.add("Holiday");

        ArrayList<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");


        rlTownList.setVisibility(View.GONE);
        rlDistributorList.setVisibility(View.GONE);
        rlBeatList.setVisibility(View.GONE);
        rlEmpList.setVisibility(View.GONE);
        llTcPc.setVisibility(View.GONE);


        ArrayAdapter<String> activityAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, activityList);

        actvActivityList.setAdapter(activityAdapter);


        edtDate.setText(date);
//        String[] tD = date.split("-");
//        String day = tD[0];
//        String m = tD[1];
//        int month = months.indexOf(m);
//        month = month + 1;
//        String yr = tD[2];
//        if (month < 10)
//            edtDate.setText(day + "-0" + month + "-" + yr);
//        else
//            edtDate.setText(day + "-" + month + "-" + yr);

//        imgDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showDateDialog();
//            }
//        });


        actvActivityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                pos = position;
                if (position == 0) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.VISIBLE);
                    rlBeatList.setVisibility(View.VISIBLE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.VISIBLE);

                    getTownList();

                } else if (position == 1) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.VISIBLE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.VISIBLE);
                    llTcPc.setVisibility(View.GONE);

                    getTownList();

                    getEmpList();

                } else if (position == 2) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    getTownList();

                } else if (position == 3) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    getTownList();

                } else if (position == 4) {

                    rlTownList.setVisibility(View.VISIBLE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                    getTownList();

                } else if (position == 5) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                } else if (position == 6) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                } else if (position == 7) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);

                } else if (position == 8) {

                    rlTownList.setVisibility(View.GONE);
                    rlDistributorList.setVisibility(View.GONE);
                    rlBeatList.setVisibility(View.GONE);
                    rlEmpList.setVisibility(View.GONE);
                    llTcPc.setVisibility(View.GONE);
                }

                resetByActivity();

            }
        });

        actvActivityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvActivityList.showDropDown();
                actvActivityList.setError(null);
            }
        });

        imgActivityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvActivityList.showDropDown();
                actvActivityList.setError(null);
            }
        });


        actvTownList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvTownList.showDropDown();
                actvTownList.setError(null);
            }
        });

        imgTownList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvTownList.showDropDown();
                actvTownList.setError(null);
            }
        });

        actvTownList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (pos == 0 || pos == 1) {

                    String townName = townItems.get(position);

                    getDisList(townName);
                    resetByTown();

                }
            }
        });

        actvDistributorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvDistributorList.showDropDown();
                actvDistributorList.setError(null);
            }
        });

        actvDistributorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                did = didList.get(position);
                if (pos == 0)
                    getBeatList(did);

                resetByDistributor();

            }
        });

        imgDistributorList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvDistributorList.showDropDown();
                actvDistributorList.setError(null);
            }
        });

        actvBeatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvBeatList.showDropDown();
                actvBeatList.setError(null);
            }
        });

        actvBeatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bid = beaIDtList.get(position);
            }
        });

        imgBeatList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvBeatList.showDropDown();
                actvBeatList.setError(null);
            }
        });

        actvEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvEmployeeList.showDropDown();
                actvEmployeeList.setError(null);
            }
        });

        actvEmployeeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eid = empIdList.get(position);
            }
        });

        imgEmployeeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actvEmployeeList.showDropDown();
                actvEmployeeList.setError(null);
            }
        });


        btnCreatePjp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                String tDate = edtDate.getText().toString();
                String activity = actvActivityList.getText().toString();
                String town = actvTownList.getText().toString();
                String distributor = actvDistributorList.getText().toString();
                String beat = actvBeatList.getText().toString();
                String emp = actvEmployeeList.getText().toString();
                String remarks = edtRemarks.getText().toString();

                MyPjp myPjp = new MyPjp();

                if (activity.equalsIgnoreCase("Retailing")) {

                    if (!town.isEmpty() && !distributor.isEmpty() && !beat.isEmpty()
                            && !edtTc.getText().toString().isEmpty() && !edtPc.getText().toString().isEmpty()
                            && !edtSale.getText().toString().isEmpty()) {

                        int tcN = 0, pcN = 0;
                        try {

                            tcN = Integer.parseInt(edtTc.getText().toString());
                            pcN = Integer.parseInt(edtPc.getText().toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (tcN > pcN) {


                            btnCreatePjp.setText("Creating...");
                            btnCreatePjp.setClickable(false);

                            holder.tvActivityPjp.setText(activity);
                            holder.tvTownPjp.setText(town);
                            holder.tvDisributorPjp.setText(distributor);
                            holder.tvBeatPjp.setText(beat);
                            holder.tvTCPJP.setText("Tc: " + edtTc.getText().toString());
                            holder.tvPCPjp.setText("Pc: " + edtPc.getText().toString());
                            holder.tvSALEPjp.setText(edtSale.getText().toString() + context.getString(R.string.unitt));

                            holder.llDisPjp.setVisibility(View.VISIBLE);
                            holder.llBeatPjp.setVisibility(View.VISIBLE);
                            holder.llTCPCSAle.setVisibility(View.VISIBLE);
                            holder.llEmpPjp.setVisibility(View.GONE);

                            myPjp.setActivity(activity);
                            myPjp.setDistributor_id(did);
                            myPjp.setBeat_id(bid);
                            myPjp.setTownName(town);
                            myPjp.setDate(tDate);
                            myPjp.setTc(edtTc.getText().toString());
                            myPjp.setPc(edtPc.getText().toString());
                            myPjp.setSale(edtSale.getText().toString());
                            myPjp.setRemarks(remarks);

                            pjpList.add(myPjp);

                            salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                    edtTc.getText().toString(), edtPc.getText().toString(),
                                    edtSale.getText().toString(), tDate, remarks);

                            holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                            holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                            count++;

                            if (count > 0) {
                                CreatePjp.tvSavePjp.setClickable(true);
                                CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                                CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                            }

                            createPjpDialog.dismiss();

                        } else {

                            Toast.makeText(context, "PC can not greater then TC", Toast.LENGTH_SHORT).show();
                        }


                    } else if (town.isEmpty()) {

                        actvTownList.setError("Town can't be empty");
                        Toast.makeText(context, "Town can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (distributor.isEmpty()) {

                        actvDistributorList.setError("Distributor can't be empty");
                        Toast.makeText(context, "Distributor can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (beat.isEmpty()) {

                        actvBeatList.setError("Beat can't be empty");
                        Toast.makeText(context, "Beat can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (edtTc.getText().toString().isEmpty()) {

                        edtTc.setError("Field can't be empty");
                        Toast.makeText(context, "Field can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (edtPc.getText().toString().isEmpty()) {

                        edtPc.setError("Field can't be empty");
                        Toast.makeText(context, "Field can't be empty", Toast.LENGTH_SHORT).show();

                    } else if (edtSale.getText().toString().isEmpty()) {

                        edtTc.setError("Sale can't be empty");
                        Toast.makeText(context, "Sale can't be empty", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "Please provide all details", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Meeting")) {

                    if (!town.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);

                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.GONE);

                        myPjp.setActivity(activity);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);
                        myPjp.setRemarks(remarks);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (town.isEmpty()) {

                        actvTownList.setError("Town can't be empty");
                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Joint Working")) {

                    if (!town.isEmpty() && !distributor.isEmpty() && !emp.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);
                        holder.tvDisributorPjp.setText(distributor);
                        holder.tvEmployeePjp.setText(emp);


                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.VISIBLE);

                        myPjp.setActivity(activity);
                        myPjp.setDistributor_id(did);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);
                        myPjp.setEmp(eid);
                        myPjp.setRemarks(remarks);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (town.isEmpty()) {

                        actvTownList.setError("Town can't be empty");
                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    } else if (distributor.isEmpty()) {

                        actvDistributorList.setError("Distributor can't be empty");
                        Toast.makeText(context, "Please provide Distributor name", Toast.LENGTH_SHORT).show();

                    } else if (emp.isEmpty()) {

                        actvEmployeeList.setError("Employee can't be empty");
                        Toast.makeText(context, "Please provide Employee name", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "Please provide all details", Toast.LENGTH_SHORT).show();

                    }


                } else if (activity.equalsIgnoreCase("Distributor Search")) {

                    if (!town.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);

                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.GONE);

                        myPjp.setActivity(activity);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);
                        myPjp.setRemarks(remarks);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (town.isEmpty()) {

                        actvTownList.setError("Town can't be empty");
                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Leave")) {

                    if (!remarks.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);

                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.GONE);

                        myPjp.setActivity(activity);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);
                        myPjp.setRemarks(remarks);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (remarks.isEmpty()) {

                        edtRemarks.setError("Remarks can't be empty");

                    } else {

                        Toast.makeText(context, "Please provide remarks", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Weekly Off")) {

                    if (!remarks.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);

                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.GONE);

                        myPjp.setActivity(activity);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);
                        myPjp.setRemarks(remarks);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (remarks.isEmpty()) {

                        edtRemarks.setError("Remarks can't be empty");

                    } else {

                        Toast.makeText(context, "Please provide remarks", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Market Survey")) {

                    if (!town.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);

                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.GONE);

                        myPjp.setActivity(activity);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (town.isEmpty()) {

                        actvTownList.setError("Town can't be empty");
                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(context, "Please provide town name", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Holiday")) {

                    if (!remarks.isEmpty()) {

                        btnCreatePjp.setText("Creating...");
                        btnCreatePjp.setClickable(false);

                        holder.tvActivityPjp.setText(activity);
                        holder.tvTownPjp.setText(town);

                        holder.llDisPjp.setVisibility(View.GONE);
                        holder.llBeatPjp.setVisibility(View.GONE);
                        holder.llTCPCSAle.setVisibility(View.GONE);
                        holder.llEmpPjp.setVisibility(View.GONE);

                        myPjp.setActivity(activity);
                        myPjp.setTownName(town);
                        myPjp.setDate(tDate);
                        myPjp.setRemarks(remarks);

                        pjpList.add(myPjp);

                        salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                                edtTc.getText().toString(), edtPc.getText().toString(),
                                edtSale.getText().toString(), tDate, remarks);

                        holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                        holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                        count++;

                        if (count > 0) {
                            CreatePjp.tvSavePjp.setClickable(true);
                            CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                            CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                        }

                        createPjpDialog.dismiss();


                    } else if (remarks.isEmpty()) {

                        edtRemarks.setError("Please provide remarks");

                    } else {

                        Toast.makeText(context, "Please provide remarks", Toast.LENGTH_SHORT).show();

                    }

                } else if (activity.equalsIgnoreCase("Office Visit")) {

                    btnCreatePjp.setText("Creating...");
                    btnCreatePjp.setClickable(false);

                    holder.tvActivityPjp.setText(activity);
                    holder.tvTownPjp.setText(town);

                    holder.llDisPjp.setVisibility(View.GONE);
                    holder.llBeatPjp.setVisibility(View.GONE);
                    holder.llTCPCSAle.setVisibility(View.GONE);
                    holder.llEmpPjp.setVisibility(View.GONE);

                    myPjp.setActivity(activity);
                    myPjp.setTownName(town);
                    myPjp.setDate(tDate);

                    pjpList.add(myPjp);

                    salesBeatDb.insertPjp(activity, town, did, distributor, bid, beat, eid, emp,
                            edtTc.getText().toString(), edtPc.getText().toString(),
                            edtSale.getText().toString(), tDate, remarks);

                    holder.llCreatedPjpDetail.setVisibility(View.VISIBLE);
                    holder.llEditPjp.setBackgroundColor(Color.parseColor("#ff99cc00"));
                    count++;

                    if (count > 0) {
                        CreatePjp.tvSavePjp.setClickable(true);
                        CreatePjp.tvSavePjp.setBackgroundColor(Color.parseColor("#5aac82"));
                        CreatePjp.tvSavePjp.setTextColor(Color.parseColor("#ffffff"));
                    }

                    createPjpDialog.dismiss();


                } else if (activity.isEmpty()) {

                    actvActivityList.setError("Activity can't be empty");
                    Toast.makeText(context, "Activity can't be empty", Toast.LENGTH_SHORT).show();

                }
            }
        });


        createPjpDialog.show();
    }

    private void getEmpList() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_EMP_LIST + myPref.getString(context.getString(R.string.zone_id_key), "") + "/employees",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("CreatePJP", "onResponse JOINT WORKING EMP LIST===" + response);

                try {

                    empIdList.clear();
                    empList.clear();

                    JSONObject data = response.getJSONObject("data");
                    JSONArray emp = data.getJSONArray("employees");

                    for (int index = 0; index < emp.length(); index++) {
                        JSONObject list = (JSONObject) emp.get(index);

                        String eid = list.getString("eid");
                        if (!myPref.getString(context.getString(R.string.emp_id_key), "").equalsIgnoreCase(eid)) {

                            empIdList.add(eid);
                            empList.add(list.getString("name"));
//                            empPhone.add(list.getString("phone1"));
                        }

                    }

                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        ArrayAdapter empAdapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_dropdown_item, empList);

                        actvEmployeeList.setAdapter(empAdapter);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("CreatePjp", "Error===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);

    }

    private void getTownList() {

        new LoadTownList().execute();
    }

    private void getDisList(String townName) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_DISTRIBUTORS_2 + "?town=" + townName,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("CreatePJP", "onResponse Dis LIST===" + response);

                try {

                    //@Umesh 02-Feb-2022
                    if(response.getInt("status")==1)
                    {

                        didList.clear();
                        disList.clear();

                        JSONArray distributors = response.getJSONArray("distributors");
                        for (int i = 0; i < distributors.length(); i++) {


                            JSONObject obj = (JSONObject) distributors.get(i);

                            //JSONObject zoneObj = obj.getJSONObject("zone");

//                            DistrebutorItem item = new DistrebutorItem();
//                            item.setDistrebutorName();
//                            item.setDistrebutorId(obj.getString("did"));

                            didList.add(obj.getString("did"));
                            disList.add(obj.getString("name"));


                        }

                        ArrayAdapter disAdapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_dropdown_item, disList);

                        actvDistributorList.setAdapter(disAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("CreatePjp", "Error===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);

    }

    private void getBeatList(String did) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_BEATS_2 + "did=" + did,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("CreatePJP", "onResponse Dis LIST===" + response);

                try {

                    String status = response.getString("status");

                    if (status.equalsIgnoreCase("success")) {

                        beaIDtList.clear();
                        beatList.clear();

                        JSONArray beats = response.getJSONArray("beats");
                        for (int i = 0; i < beats.length(); i++) {

                            JSONObject obj = (JSONObject) beats.get(i);

                            beaIDtList.add(obj.getString("bid"));
                            beatList.add(obj.getString("name"));


                        }

                        ArrayAdapter beatAdapter = new ArrayAdapter<>(context,
                                android.R.layout.simple_spinner_dropdown_item, beatList);

                        actvBeatList.setAdapter(beatAdapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                try {
                    if (error.networkResponse.statusCode == 422) {
                        String responseBody = null;
                        try {

                            responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject object = new JSONObject(responseBody);
                            String message = object.getString("message");
                            JSONObject errorr = object.getJSONObject("errors");

                            Log.e("CreatePjp", "Error===" + message + "===" + errorr);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        Volley.newRequestQueue(context).add(jsonObjectRequest);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void resetByActivity() {

        actvTownList.setText("");
        resetByTown();

    }

    private void resetByTown() {

        actvDistributorList.setText("");
        resetByDistributor();

    }

    private void resetByDistributor() {

        actvEmployeeList.setText("");
        actvBeatList.setText("");
        edtTc.setText("");
        edtPc.setText("");
        edtRemarks.setText("");
        edtSale.setText("");

        actvTownList.setError(null);
        actvDistributorList.setError(null);
        actvBeatList.setError(null);
        actvEmployeeList.setError(null);
    }

    private class LoadTownList extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            Cursor cursor = null;

            try {
                cursor = salesBeatDb.getAllRecordFromTownListTable();
                if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                    do {
                        townItems.add(cursor.getString(cursor.getColumnIndex("town_name")));
                    } while (cursor.moveToNext());

                    Collections.sort(townItems, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                }

            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (cursor != null)
                    cursor.close();
            }


            return townItems;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            townItems.clear();
        }

        @Override
        protected void onPostExecute(ArrayList<String> townItems) {
            super.onPostExecute(townItems);

            if (townItems.size() > 0) {

                ArrayAdapter townAdapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_spinner_dropdown_item, townItems);

                actvTownList.setAdapter(townAdapter);

            }
        }
    }

    public class MyViewholder extends RecyclerView.ViewHolder {

        TextView tvDatePjp, tvActivityPjp, tvTownPjp, tvDisributorPjp,
                tvBeatPjp, tvEmployeePjp, tvTCPJP, tvPCPjp, tvSALEPjp, tvDayOfWeekPjp;

        ImageView imgEditPjp;

        LinearLayout llCreatedPjpDetail, llDisPjp, llBeatPjp, llEmpPjp,
                llTCPCSAle, llEditPjp;

        public MyViewholder(View itemView) {
            super(itemView);
            tvDatePjp = itemView.findViewById(R.id.tvDatePjp);
            tvActivityPjp = itemView.findViewById(R.id.tvActivityPjp);
            tvTownPjp = itemView.findViewById(R.id.tvTownPjp);
            tvDisributorPjp = itemView.findViewById(R.id.tvDisributorPjp);
            tvBeatPjp = itemView.findViewById(R.id.tvBeatPjp);
            tvEmployeePjp = itemView.findViewById(R.id.tvEmployeePjp);
            tvTCPJP = itemView.findViewById(R.id.tvTCPJP);
            tvPCPjp = itemView.findViewById(R.id.tvPCPjp);
            tvSALEPjp = itemView.findViewById(R.id.tvSALEPjp);
            tvDayOfWeekPjp = itemView.findViewById(R.id.tvDayOfWeekPjp);
            imgEditPjp = itemView.findViewById(R.id.imgEditPjp);
            llCreatedPjpDetail = itemView.findViewById(R.id.llCreatedPjpDetail);
            llDisPjp = itemView.findViewById(R.id.llDisPjp);
            llBeatPjp = itemView.findViewById(R.id.llBeatPjp);
            llEmpPjp = itemView.findViewById(R.id.llEmpPjp);
            llTCPCSAle = itemView.findViewById(R.id.llTCPCSAle);
            llEditPjp = itemView.findViewById(R.id.llEditPjp);

        }
    }
}
