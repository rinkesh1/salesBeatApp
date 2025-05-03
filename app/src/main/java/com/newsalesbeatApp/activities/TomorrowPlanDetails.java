package com.newsalesbeatApp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.newsalesbeatApp.R;

public class TomorrowPlanDetails extends AppCompatActivity {

    TextView tvActivityType, tvWorkingTown, tvWorkingDistributor,
            tvEmployeeWorkingWith, tvMeetingType, tvComment, tvShareTomorrowPlan;

    SharedPreferences tempPref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomorrowdetails);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), MODE_PRIVATE);
        tvActivityType = findViewById(R.id.tvTomorrowActivityType);
        tvWorkingTown = findViewById(R.id.tvTomorrowWorkingTown);
        tvWorkingDistributor = findViewById(R.id.tvTomorrowWorkingDistributor);
        tvEmployeeWorkingWith = findViewById(R.id.tvTomorrowEmpWorkingW);
        tvMeetingType = findViewById(R.id.tvTomorrowMeetingType);
        tvComment = findViewById(R.id.tvTomorrowComment);
        tvShareTomorrowPlan = findViewById(R.id.tvShareTomorrowDetails);

        Toolbar mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        setSupportActionBar(mToolbar);

        tvPageTitle.setText("Tomorrow Plan Details");

        tvActivityType.setText(tempPref.getString(getString(R.string.tomActType), ""));
        tvWorkingTown.setText(tempPref.getString(getString(R.string.tomWorkinTown), ""));
        tvWorkingDistributor.setText(tempPref.getString(getString(R.string.tomWorkingDis), ""));
        tvEmployeeWorkingWith.setText(tempPref.getString(getString(R.string.tomEmpWith), ""));
        tvMeetingType.setText(tempPref.getString(getString(R.string.tomMeetingType), ""));
        tvComment.setText(tempPref.getString(getString(R.string.tomComment), ""));

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        tvShareTomorrowPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePlan();
            }
        });

    }

    private void sharePlan() {

        String text = "";
        text = text.concat("\n");
        text = text.concat("Activity type : " + tvActivityType.getText().toString());
        text = text.concat("\n");
        text = text.concat("Working town : " + tvWorkingTown.getText().toString());
        text = text.concat("\n");
        text = text.concat("Working distributor : " + tvWorkingDistributor.getText().toString());
        text = text.concat("\n");
        text = text.concat("Employee working with : " + tvEmployeeWorkingWith.getText().toString());
        text = text.concat("\n");
        text = text.concat("Meeting type  : " + tvMeetingType.getText().toString());
        text = text.concat("\n");
        text = text.concat("Comment  : " + tvComment.getText().toString());


        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Tomorrow plan details");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share summary"));
    }
}
