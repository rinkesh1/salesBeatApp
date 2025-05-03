package com.newsalesbeatApp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MeetingActivity extends AppCompatActivity {

    RadioButton rdHOVisit, rdNewDisSearch, rdOthers;
    EditText edtRemarks;
    Button btnSubmit;

    String activity, remarks;

    RequestQueue requestQueue;
    SharedPreferences/* myPref,*/ tempPref;
    UtilityClass utilityClass;
    GPSLocation locationProvider;
    SalesBeatDb salesBeatDb;
    boolean remarksNeeded = true;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.meeting_activity);
        //myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        rdHOVisit = findViewById(R.id.rdHOVisit);
        //rdTraining = findViewById(R.id.rdTraining);
        rdNewDisSearch = findViewById(R.id.rdNewDisSearch);
        rdOthers = findViewById(R.id.rdOthers);
        edtRemarks = findViewById(R.id.edtRemarksMeeting);
        btnSubmit = findViewById(R.id.btnSubmitMeeting);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        requestQueue = Volley.newRequestQueue(this);
        utilityClass = new UtilityClass(MeetingActivity.this);
        //salesBeatDb = new SalesBeatDb(this);
        salesBeatDb = SalesBeatDb.getHelper(this);
        locationProvider = new GPSLocation(this);
        //check gps status if on/off
        locationProvider.checkGpsStatus();


        tvPageTitle.setText("Meeting");

        if (tempPref.getBoolean(getString(R.string.fda_key), false))
            showMeesageDialog();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //locationProvider.unregisterReceiver();
                MeetingActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        rdHOVisit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    activity = rdHOVisit.getText().toString();
                    remarksNeeded = false;
                }
            }
        });


        rdNewDisSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    activity = rdNewDisSearch.getText().toString();
                    remarksNeeded = false;
                }
            }
        });


        rdOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    activity = rdOthers.getText().toString();
                    remarksNeeded = true;
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remarks = edtRemarks.getText().toString();

                if (activity != null && !activity.isEmpty()) {

                    if (remarks.isEmpty() && remarksNeeded) {
                        Toast.makeText(MeetingActivity.this, "Please give remarks.", Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String date2 = utilityClass.getYMDDateFormat().format(Calendar.getInstance().getTime());
                        String date = sdf.format(Calendar.getInstance().getTime());
                        salesBeatDb.insertOtherActivity(activity, remarks, date, locationProvider.getLatitudeStr(),
                                locationProvider.getLongitudeStr(), date2);

                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putBoolean(getString(R.string.fda_key), true);
                        editor.putString(getString(R.string.fda_type_key), activity);
                        editor.putString(getString(R.string.fda_remarks_key), remarks);

                        editor.apply();

                        Toast.makeText(MeetingActivity.this, "Noted successfully", Toast.LENGTH_SHORT).show();

                        showMeesageDialog();
                    }

                } else
                    Toast.makeText(MeetingActivity.this, "Please select option", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showMeesageDialog() {

        final Dialog mDialog = new Dialog(MeetingActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        mDialog.setContentView(R.layout.dialog_message);
        mDialog.setCancelable(false);
        TextView tvActivity = mDialog.findViewById(R.id.tvActivity);
        TextView tvRemarksM = mDialog.findViewById(R.id.tvRemarksM);
        Button btnOk = mDialog.findViewById(R.id.btnOkMeeting);
        RelativeLayout rlMessage = mDialog.findViewById(R.id.rlMessage);
        LinearLayout llDataList = mDialog.findViewById(R.id.llDataList);
        CardView cvJointW = mDialog.findViewById(R.id.cvJointW);

        rlMessage.setVisibility(View.VISIBLE);
        llDataList.setVisibility(View.GONE);
        cvJointW.setVisibility(View.GONE);

        tvActivity.setText(tempPref.getString(getString(R.string.fda_type_key), ""));
        tvRemarksM.setText(tempPref.getString(getString(R.string.fda_remarks_key), ""));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                MeetingActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        mDialog.show();
    }

    public void onBackPressed() {
        //locationProvider.unregisterReceiver();
        MeetingActivity.this.finish();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                MeetingActivity.this.finishAffinity();
            }
        }
    }
}
