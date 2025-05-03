package com.newsalesbeatApp.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.newsalesbeatApp.adapters.PrimarySaleListAdapter;
import com.newsalesbeatApp.pojo.Item;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.sentry.Sentry;

public class PrimarySaleHistory extends AppCompatActivity {

    SharedPreferences myPref;
    RequestQueue requestQueue;
    ArrayList<Item> primarySaleList = new ArrayList<>();
    RecyclerView rvPrimarySaleHistory;
    TextView tvNoDataPrimarySale, tvTotalTarget, tvTotalAchievement, tvPercentageOfTotal;
    MaterialCalendarView calendarPrimarySaleHistory;
    Toolbar mToolbar;
    PrimarySaleListAdapter primarySaleListAdapter;
    double totalTar = 0, totalAch = 0;
    double percentage = 0;
    private MenuItem mSearchItem;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.primary_sale_history);
        myPref = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        rvPrimarySaleHistory = findViewById(R.id.rvPrimarySaleHistory);
        tvNoDataPrimarySale = findViewById(R.id.tvNoDataPrimarySale);
        calendarPrimarySaleHistory = findViewById(R.id.calendarPrimarySaleHistory);
        tvTotalTarget = findViewById(R.id.tvTotalTarget);
        tvTotalAchievement = findViewById(R.id.tvTotalAch);
        tvPercentageOfTotal = findViewById(R.id.tvPercentageOfTotal);

        setUpToolBar();

        requestQueue = Volley.newRequestQueue(PrimarySaleHistory.this);
        //current date string
        final Calendar cc = java.util.Calendar.getInstance();
        SimpleDateFormat sdff = new SimpleDateFormat("MM/yyyy");
        String curentDateString = sdff.format(cc.getTime());

        getPrimarySaleHistory(curentDateString);

        calendarPrimarySaleHistory.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                //selected date string
                Calendar cc2 = date.getCalendar();
                SimpleDateFormat sdff = new SimpleDateFormat("MM/yyyy");
                String curentDateString = sdff.format(cc2.getTime());

                totalTar = 0;
                totalAch = 0;
                percentage = 0;

                //@Umesh 31-08-2022
               // tvTotalTarget.setText(String.valueOf(totalTar) + " kg");
                tvTotalTarget.setText(String.valueOf(totalTar));
               // tvTotalAchievement.setText(String.valueOf(totalAch) + " kg");
                tvTotalAchievement.setText(String.valueOf(totalAch));
                tvPercentageOfTotal.setText(String.valueOf(percentage) + "%");

                getPrimarySaleHistory(curentDateString);
            }
        });


        /*String currentMax = String.valueOf(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)) + "-" +
                String.valueOf(Calendar.getInstance().get(Calendar.MONTH) + 1) + "-" +
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        Log.d("PrimarySaleDate", currentMax);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");*/

        Calendar max = Calendar.getInstance();
        max.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));

        calendarPrimarySaleHistory.state().edit()
                .setMaximumDate(max)
                .commit();
    }

    @Override
    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private void setUpToolBar() {

        mToolbar = findViewById(R.id.toolbar3);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);
        setSupportActionBar(mToolbar);

        tvPageTitle.setText("Primary Sale History");

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PrimarySaleHistory.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    public void onBackPressed() {

        PrimarySaleHistory.this.finish();
        //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    private void getPrimarySaleHistory(String curentDateString) {

        final Dialog loader = new Dialog(this, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_PRIMARY_SALE_HISTORY +"?date="+ curentDateString,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("onResponse", "PrimarySaleHistory===>>" + response);
                loader.dismiss();
                primarySaleList.clear();
                try {
                    //@Umesh
                    if (response.getInt("status") == 1)
                    {
                        response= response.getJSONObject("data");
                    JSONArray saleTarget = response.getJSONArray("saleTarget");
                    String total_ach = response.getString("total_ach");
                    totalAch = Double.parseDouble(total_ach);
                    String total_tar = response.getString("total_tar");
                    totalTar = Double.parseDouble(total_tar);

                    for (int i = 0; i < saleTarget.length(); i++) {

                        Item item = new Item();
                        JSONObject obj = (JSONObject) saleTarget.get(i);
                        String target = obj.getString("target");

                        JSONObject distributor = obj.getJSONObject("distributors");
                        item.setItem1(distributor.getString("name"));
                        item.setItem2(target);
                        item.setItem4(distributor.getString("town"));

                        JSONArray sales = obj.getJSONArray("sales");
                        ArrayList<Item> saleList = new ArrayList<>();
                        Double ach = 0.0;
                        for (int j = 0; j < sales.length(); j++) {

                            Item item2 = new Item();
                            JSONObject obj2 = (JSONObject) sales.get(j);
                            item2.setItem1(obj2.getString("date"));
                            ach = ach + obj2.getDouble("sale");
                            item2.setItem2(String.valueOf(obj2.getDouble("sale")));

                            saleList.add(item2);
                        }

                        //item.setItem3(String.valueOf(ach));
                        //@Umesh
                        String acheive = obj.getString("achievment");
                        item.setItem3(acheive);
                        item.setItemList(saleList);

                        primarySaleList.add(item);

                    }

//                    JSONArray achievements = response.getJSONArray("achievements");
//                    for (int i = 0; i < achievements.length(); i++) {
//
//                        Item item = new Item();
//                        JSONObject obj = (JSONObject) achievements.get(i);
//                        //String target = obj.getString("target");
//                        //JSONObject distributor = obj.getJSONObject("distributor");
//                        item.setItem1(obj.getString("name"));
//                        item.setItem2("0");
//                        item.setItem4(obj.getString("town"));
//
//                        JSONArray sales = obj.getJSONArray("sales");
//                        ArrayList<Item> saleList = new ArrayList<>();
//                        int ach = 0;
//                        for (int j = 0; j < sales.length(); j++) {
//
//                            Item item2 = new Item();
//                            JSONObject obj2 = (JSONObject) sales.get(j);
//                            item2.setItem1(obj2.getString("date"));
//                            ach = ach + obj2.getInt("sale");
//                            item2.setItem2(String.valueOf(obj2.getInt("sale")));
//                            saleList.add(item2);
//                        }
//
//                        item.setItem3(String.valueOf(ach));
//                        item.setItemList(saleList);
//
//                        primarySaleList.add(item);
//                    }

                    //String status = response.getString("status");
                    //if (status.equalsIgnoreCase("success"))
                    {

                        if (primarySaleList.size() > 0) {

                            try {
                                if (totalTar == 0)
                                    percentage = 100;
                                else {

                                    percentage = ((totalAch * 100) /totalTar);
                                }
                            } catch (Exception e) {
                                //e.printStackTrace();
                                percentage = 0;
                            }

                            //@Umesh 31-08-2022
                            //tvTotalTarget.setText(String.valueOf(totalTar) + " kg");
                            tvTotalTarget.setText(String.valueOf(totalTar));
                            //tvTotalAchievement.setText(String.valueOf(totalAch) + " kg");
                            tvTotalAchievement.setText(String.valueOf(totalAch));
                            tvPercentageOfTotal.setText(new DecimalFormat("##.##").format(percentage) + "%");


                            Collections.sort(primarySaleList, new Comparator<Item>() {
                                @Override
                                public int compare(Item o1, Item o2) {
                                    return o1.getItem1().compareTo(o2.getItem1());
                                }
                            });

                            primarySaleListAdapter =
                                    new PrimarySaleListAdapter(PrimarySaleHistory.this, primarySaleList);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(PrimarySaleHistory.this);
                            rvPrimarySaleHistory.setLayoutManager(layoutManager);
                            rvPrimarySaleHistory.setAdapter(primarySaleListAdapter);

                            tvNoDataPrimarySale.setVisibility(View.GONE);
                            rvPrimarySaleHistory.setVisibility(View.VISIBLE);

                        } else {
                            tvNoDataPrimarySale.setVisibility(View.VISIBLE);
                            rvPrimarySaleHistory.setVisibility(View.GONE);
                        }

                    }
                }
                else
                {
                    Log.e("PrimarySaleHistory",response.getString("message"));
                }

                } catch (Exception e) {

                    tvNoDataPrimarySale.setVisibility(View.VISIBLE);
                    rvPrimarySaleHistory.setVisibility(View.GONE);
                    tvNoDataPrimarySale.setText("Error in data:"+e.getMessage());
                    e.printStackTrace();
                    Sentry.captureMessage(e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loader.dismiss();
                error.printStackTrace();
                tvNoDataPrimarySale.setVisibility(View.VISIBLE);
                tvNoDataPrimarySale.setText("Server error");
                rvPrimarySaleHistory.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        MenuItem item = menu.findItem(R.id.action);
        item.setVisible(false);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(mSearchItem);

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (primarySaleListAdapter != null)
                    primarySaleListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (primarySaleListAdapter != null)
                    primarySaleListAdapter.getFilter().filter(newText);
                return false;
            }
        });

        // Get the search close button image view
        ImageView closeButton = searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (primarySaleListAdapter != null)
                    primarySaleListAdapter.getFilter().filter("");
                //Find EditText view
                EditText et = (EditText) searchViewAndroidActionBar.findViewById(R.id.search_src_text);

                //Clear the text from EditText view
                et.setText("");

                //Clear query
                searchViewAndroidActionBar.setQuery("", false);
                //Collapse the action view
                searchViewAndroidActionBar.onActionViewCollapsed();
            }
        });

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Called when SearchView is collapsing
                if (mSearchItem.isActionViewExpanded()) {
                    animateSearchToolbar(1, false, false);
                }

                if (primarySaleListAdapter != null)
                    primarySaleListAdapter.getFilter().filter("");

                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                return true;
            }
        });

        return true;
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));

        if (show) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, 0.0f, (float) width);
                createCircularReveal.setDuration(250);
                createCircularReveal.start();
            } else {
                TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, (float) (-mToolbar.getHeight()), 0.0f);
                translateAnimation.setDuration(220);
                mToolbar.clearAnimation();
                mToolbar.startAnimation(translateAnimation);
            }

        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int width = mToolbar.getWidth() -
                        (containsOverflow ? getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material) : 0) -
                        ((getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) * numberOfMenuIcon) / 2);
                Animator createCircularReveal = ViewAnimationUtils.createCircularReveal(mToolbar,
                        isRtl(getResources()) ? mToolbar.getWidth() - width : width, mToolbar.getHeight() / 2, (float) width, 0.0f);
                createCircularReveal.setDuration(250);
                createCircularReveal.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mToolbar.setBackgroundColor(getThemeColor(PrimarySaleHistory.this, android.R.attr.colorPrimary));
                    }
                });
                createCircularReveal.start();
            } else {
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                Animation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, (float) (-mToolbar.getHeight()));
                AnimationSet animationSet = new AnimationSet(true);
                animationSet.addAnimation(alphaAnimation);
                animationSet.addAnimation(translateAnimation);
                animationSet.setDuration(220);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mToolbar.setBackgroundColor(getThemeColor(PrimarySaleHistory.this, android.R.attr.colorPrimary));
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                mToolbar.startAnimation(animationSet);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
}
