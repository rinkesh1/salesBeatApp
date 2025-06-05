package com.newsalesbeatApp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.ExpandableMenuAdapter;
import com.newsalesbeatApp.network.ApiClient;
import com.newsalesbeatApp.network.ApiService;
import com.newsalesbeatApp.pojo.MenuItem;
import com.newsalesbeatApp.pojo.MenuResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomiseMenuActivity extends AppCompatActivity {

    private SharedPreferences myPref,prefSFA;
    ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customise_menu);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);

            final View rootView = findViewById(R.id.main_layout);
            rootView.setOnApplyWindowInsetsListener((v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsets.Type.systemBars());
                // Optionally use insets.top or bottom padding if needed
                v.setPadding(0, systemBars.top, 0, systemBars.bottom); // or (0,0,0,0) if full overlay
                return insets;
            });
        } else {
            // For Android below R
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
                v.setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
                return insets.consumeSystemWindowInsets();
            });

            // Also request layout flags to allow drawing under system bars
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        expandableListView = findViewById(R.id.expandableListView);
        ImageView imgBack = findViewById(R.id.imgBack);
        TextView tvPageTitle = findViewById(R.id.pageTitle);

        tvPageTitle.setText("Reports");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //locationProvider.unregisterReceiver();
                CustomiseMenuActivity.this.finish();
            }
        });

        getMenuAPIcall();

    }

    private void getMenuAPIcall() {
        expandableListView.setGroupIndicator(null);
        String stCode = prefSFA.getString(getString(R.string.state_key), "");
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);

        String strToken = myPref.getString("token", "");
        Log.d("TAG", "get State Code: " + stCode);
        Log.d("TAG", "Token Token: " + strToken);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<MenuResponse> call = apiService.getCustomiseMenu(strToken);
        call.enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MenuResponse res = response.body();

                    Log.d("API", "Success: " + response.body());

                    Map<String, List<MenuItem>> groupedData = new LinkedHashMap<>();
                    for (MenuItem item : res.data) {
                        String group = item.menuGroup != null ? item.menuGroup : "Others";
                        if (!groupedData.containsKey(group)) {
                            groupedData.put(group, new ArrayList<>());
                        }
                        groupedData.get(group).add(item);
                    }

                    /*for (MenuItem item : res.data) {
                        String group = item.menuGroup != null ? item.menuGroup : "Others";
                        if (!groupedData.containsKey(group)) {
                            groupedData.put(group, new ArrayList<>());
                        }
                        groupedData.get(group).add(item);
                    }*/

                    // Set adapter here
                    ExpandableMenuAdapter adapter = new ExpandableMenuAdapter(CustomiseMenuActivity.this, groupedData);
                    expandableListView.setAdapter(adapter);

                    expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
                        MenuItem clicked = groupedData
                                .get(adapter.getGroup(groupPosition))
                                .get(childPosition);

//                        Toast.makeText(getApplication(), "Clicked: " + clicked.menuName, Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getApplication(), "Clicked URL: " + clicked.url, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CustomiseMenuActivity.this, ReportDetails.class);
                        intent.putExtra("mName", clicked.menuName);
                        intent.putExtra("apUrl", clicked.url);
                        startActivity(intent);

                        return true;
                    });
                } else {
                    Log.e("API", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                Log.e("API", "Failure: " + t.getMessage());
            }
        });


    }
}