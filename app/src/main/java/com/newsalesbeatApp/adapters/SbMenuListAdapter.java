package com.newsalesbeatApp.adapters;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.ClosingActivity;
import com.newsalesbeatApp.activities.Documets;
import com.newsalesbeatApp.activities.MyClaimExpense;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.activities.OtherActivity;
import com.newsalesbeatApp.pojo.MyCategory;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/*
 * Created by MTC on 18-07-2017.
 */
public class SbMenuListAdapter extends RecyclerView.Adapter<SbMenuListAdapter.ViewHolder> {

    private static FirebaseAnalytics firebaseAnalytics;
    private Context context;
    private SharedPreferences tempPref, sfa;
    private ArrayList<MyCategory> android;
    private String attendance = "";
    private UtilityClass utilityClass;
    private GPSLocation locationProvider;

    public SbMenuListAdapter(Context context, ArrayList<MyCategory> android) {

        this.android = android;
        this.context = context;
        utilityClass = new UtilityClass(context);
        locationProvider = new GPSLocation(context);
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        tempPref = context.getSharedPreferences(context.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        sfa = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_menu_list_row, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        Log.d("TAG", "menu Adapter: "+android.get(position).getCategoryName());
        viewHolder.tvCategoryName.setText(android.get(position).getCategoryName());
        viewHolder.categoryIcon.setImageResource(android.get(position).getIcon());


        String currentDate = utilityClass.getDMYDateFormat().format(Calendar.getInstance().getTime());

        String[] date = currentDate.split("-");
        int day = Integer.parseInt(date[0]);
        int month = Integer.parseInt(date[1]);
        int year = Integer.parseInt(date[2]);

        int sday = 0, smonth = 0, syear = 0;
        if (!tempPref.getString(context.getString(R.string.closing_start_date_key), "").isEmpty()) {

            String[] sdate = tempPref.getString(context.getString(R.string.closing_start_date_key), "").split("-");
            sday = Integer.parseInt(sdate[2]);
            smonth = Integer.parseInt(sdate[1]);
            syear = Integer.parseInt(sdate[0]);
        }


        int eday = 0, emonth = 0, eyear = 0;
        if (!tempPref.getString(context.getString(R.string.closing_end_date_key), "").isEmpty()) {

            String[] edate = tempPref.getString(context.getString(R.string.closing_end_date_key), "").split("-");
            eday = Integer.parseInt(edate[2]);
            emonth = Integer.parseInt(edate[1]);
            eyear = Integer.parseInt(edate[0]);
        }

        viewHolder.tvMsg.setText("From " + getDMY(tempPref.getString(context.getString(R.string.closing_start_date_key), ""))
                + " to " + getDMY(tempPref.getString(context.getString(R.string.closing_end_date_key), "")));


        // viewHolder.tvMsg.setText("28-12-2019 to 08-01-2020 ");


        Log.d("TAG", "check attendance-1: "+attendance);
        if (!tempPref.getString(context.getString(R.string.attendance_key), "").isEmpty()) {

            attendance = tempPref.getString(context.getString(R.string.attendance_key), "");
        }
        Log.d("TAG", "check attendance: "+attendance);
        attendance = "present";
        if (attendance.equalsIgnoreCase("present")) {

            if (position == 0)
                viewHolder.llLock.setVisibility(View.GONE);
            else if (position == 1)
                viewHolder.llLock.setVisibility(View.GONE);

            else if (position == 2) {

                viewHolder.llLock.setVisibility(View.GONE);

                viewHolder.llmsg.setVisibility(View.GONE);

                if ((day >= sday || day <= eday) && (sday != 0 && eday != 0)) {
                    viewHolder.llmsg.setVisibility(View.GONE);
                } else {

                    viewHolder.llmsg.setVisibility(View.VISIBLE);

                    viewHolder.llmsg.setOnClickListener(view -> Toast.makeText(context, "Will open between "
                            + getDMY(tempPref.getString(context.getString(R.string.closing_start_date_key), ""))
                            + " to " + getDMY(tempPref.getString(context.getString(R.string.closing_end_date_key), "")), Toast.LENGTH_SHORT).show());
                }

            } else if (position == 4)
                viewHolder.llLock.setVisibility(View.GONE);
            else if (position == 5)
                viewHolder.llLock.setVisibility(View.GONE);
            else if (position == 8)
                viewHolder.llLock.setVisibility(View.GONE);

        } else if (attendance.equalsIgnoreCase("checkOut")) {

            if (position == 5)
                viewHolder.llLock.setVisibility(View.GONE);
            if (position == 4 || position == 8)
                viewHolder.llLock.setVisibility(View.GONE);

        } else {

            if (position == 4 || position == 8)
                viewHolder.llLock.setVisibility(View.GONE);
        }

        viewHolder.mainLayout.setOnClickListener(view -> {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

            String[] temp = checkInTime.split(" ");

            utilityClass.setEvent(sfa.getString(context.getString(R.string.emp_id_key), ""),
                    checkInTime, "Button BookOrder From Menu", temp[1], String.valueOf(locationProvider.getLatitude()),
                    String.valueOf(locationProvider.getLongitude()));

            Log.e("onClick", "attendance===" + attendance);
            Log.e("onClick", "position===" + position);
            if (position == 0 && attendance.equalsIgnoreCase("Present")) {

                String activity = tempPref.getString(context.getString(R.string.act_type_key), "");

                activity = activity.replaceAll("\\[", "");
                activity = activity.replaceAll("\\]", "");

                String[] act = activity.split(",");

                String activity1 = "", activity2 = "";

                if (act != null && act.length == 1) {
                    activity1 = act[0];
                } else if (act != null && act.length == 2) {
                    activity1 = act[0];
                    activity2 = act[1];
                }

                Log.e("SbMenuListAdapter", "====>" + activity1);

                if (activity1.contains("Retailing") ||
                        activity2.contains("Retailing")) {


                    Intent intent = new Intent(context, OrderBookingRetailing.class);
                    context.startActivity(intent);

                } else {


                    switch (activity1) {

                        case "joint working":
                            Toast.makeText(context, "Are you on joint working", Toast.LENGTH_SHORT).show();
                            break;
                        case "Meeting":
                            Toast.makeText(context, "You are in meeting", Toast.LENGTH_SHORT).show();
                            break;
                        case "New Distributor Search":
                            Toast.makeText(context, "You are on new distributor appointment", Toast.LENGTH_SHORT).show();
                            break;
                        case "Travelling":
                            Toast.makeText(context, "You are Travelling", Toast.LENGTH_SHORT).show();
                            break;
                        case "Payment Collection":
                            Toast.makeText(context, "You are in beat for payment collection", Toast.LENGTH_SHORT).show();
                            break;
                        case "Marketing/Promotion":
                            Toast.makeText(context, "You are in beat for marketing/promotion", Toast.LENGTH_SHORT).show();
                            break;
                        case "Van Sales":
                            Toast.makeText(context, "You are in beat for van sales", Toast.LENGTH_SHORT).show();
                            break;
                        case "Others":
                            Toast.makeText(context, "Order booking not allowed in Others", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Intent intent = new Intent(context, OrderBookingRetailing.class);
                            context.startActivity(intent);
                            break;

                    }

//                        if (actT.contains("joint working")) {
//                            Toast.makeText(requireContext(), "Are you on joint working", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("Meeting")) {
//                            Toast.makeText(requireContext(), "You are in meeting", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("New Distributor Appointment")) {
//                            Toast.makeText(requireContext(), "You are on new distributor appointment", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("Travelling")) {
//                            Toast.makeText(requireContext(), "You are Travelling", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("Payment Collection")) {
//                            Toast.makeText(requireContext(), "You are in beat for payment collection", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("Marketing/Promotion")) {
//                            Toast.makeText(requireContext(), "You are in beat for marketing/promotion", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("Van Sales")) {
//                            Toast.makeText(requireContext(), "You are in beat for van sales", Toast.LENGTH_SHORT).show();
//                        } else if (actT.contains("Others")) {
//                            Toast.makeText(requireContext(), "Unknown", Toast.LENGTH_SHORT).show();
//                        }
                }

//                    if ((tempPref.getString(context.getString(R.string.askfororder_key), "").equalsIgnoreCase("yes")
//                            || tempPref.getString(context.getString(R.string.askfororder_key), "").isEmpty())) {
//
//                        Intent intent = new Intent(context, OrderBookingRetailing.class);
//                        context.startActivity(intent);
//                        //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
//
//                    } else {
//                        Toast.makeText(context, "Are you on joint working", Toast.LENGTH_SHORT).show();
//                    }


            } else if (position == 1 && attendance.equalsIgnoreCase("Present")) {

                Intent intent = new Intent(context, OtherActivity.class);
                intent.putExtra("page_title", android.get(position).getCategoryName());
                context.startActivity(intent);


                //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (position == 2 && attendance.equalsIgnoreCase("Present")) {

                //Toast.makeText(context, "Added soon", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, ClosingActivity.class);
                intent.putExtra("page_title", android.get(position).getCategoryName());
                context.startActivity(intent);
                //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (position == 3 && attendance.equalsIgnoreCase("Present")) {

                Toast.makeText(context, "Added soon", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, Schemes.class);
//                    intent.putExtra("page_title", android.get(position).getCategoryName());
//                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (position == 4 /*&& attendance.equalsIgnoreCase("Present")*/) {

                Bundle params = new Bundle();
                params.putString("Action", "Product visit");
                params.putString("UserId", "" + sfa.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("SBMenuTest", params);

                Intent intent = new Intent(context, Documets.class);
                intent.putExtra("page_title", android.get(position).getCategoryName());
                Log.d("SBMenuTest", android.get(position).getCategoryName());
                context.startActivity(intent);
                //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if ((position == 5) && (attendance.equalsIgnoreCase("Present")
                    || attendance.equalsIgnoreCase("checkOut"))) {

                Bundle params = new Bundle();
                params.putString("Action", "Claim & Expense visit");
                params.putString("UserId", "" + sfa.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("SBMenuTest", params);

                Intent intent = new Intent(context, MyClaimExpense.class);
                intent.putExtra("page_title", android.get(position).getCategoryName());
                context.startActivity(intent);
                //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (position == 6 && attendance.equalsIgnoreCase("Present")) {

                Toast.makeText(context, "Added soon", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, MyTrainingCenter.class);
//                    intent.putExtra("page_title", android.get(position).getCategoryName());
//                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (position == 7 && attendance.equalsIgnoreCase("Present")) {

                Toast.makeText(context, "Added soon", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(context, KnowledgeBase.class);
//                    intent.putExtra("page_title", android.get(position).getCategoryName());
//                    context.startActivity(intent);
//                    ((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            } else if (position == 8 /*&& attendance.equalsIgnoreCase("Present")*/) {
                // Use package name which we want to check
                boolean isAppInstalled = appInstalledOrNot("com.teamviewer.quicksupport.market");

                if (isAppInstalled) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = context.getPackageManager()
                            .getLaunchIntentForPackage("com.teamviewer.quicksupport.market");
                    context.startActivity(LaunchIntent);

                } else {
                    showDialog();

                }

            } else {
                Toast.makeText(context, "Please mark present to enable this", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getDMY(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String sD = "";
        try {
            if(date.length()>0) //Umesh
            {
                Date oneWayTripDate = input.parse(date);                 // parse input
                assert oneWayTripDate != null;
                sD = output.format(oneWayTripDate);
            }// format output
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return sD;
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Alert!");
        builder.setCancelable(false);
        builder.setMessage("Quick Support not installed on your device.Please install?");

        builder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();

                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.teamviewer.quicksupport.market")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.teamviewer.quicksupport.market")));
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


            }
        });

        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return android.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout categoryLayout, llLock, llmsg;
        CardView mainLayout;
        private TextView tvCategoryName, tvMsg/*, tvSubCategory*/;
        private ImageView categoryIcon;

        public ViewHolder(View view) {
            super(view);
            categoryLayout = view.findViewById(R.id.categoryLayout);
            mainLayout = view.findViewById(R.id.mainLayout);
            llLock = view.findViewById(R.id.llLock);
            llmsg = view.findViewById(R.id.llmsg);
            tvCategoryName = view.findViewById(R.id.tvCategoryName);
            tvMsg = view.findViewById(R.id.tvMsg);
            //tvSubCategory = view.findViewById(R.id.tvSubCategoryName);
            categoryIcon = view.findViewById(R.id.imgCategoryIcon);

        }
    }
}

