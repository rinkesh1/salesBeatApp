package com.newsalesbeatApp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.AddNewRetailerActivity;
import com.newsalesbeatApp.activities.MapsActivity;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.activities.RetailerActivity;
import com.newsalesbeatApp.adapters.ScheduledRetailerListAdapter;
import com.newsalesbeatApp.pojo.BeatItem;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Created by MTC on 25-07-2017.
 */

public class ScheduledRetailerList extends Fragment {

    ArrayList<BeatItem> beatList = new ArrayList<>();
    boolean isPresentInBeat;
    SalesBeatDb salesBeatDb;
    private ShimmerRecyclerView rcvRetailerList;
    private Button btnChangeBeat;
    private ImageView imgNoRecord;
    private TextView tvTemp;
    private SharedPreferences tempPref;
    private ScheduledRetailerListAdapter adapter;
    private FloatingActionButton fabUpDown;
    private LinearLayoutManager layoutManager;
    private UtilityClass utilityClass;
    private MenuItem mSearchItem;
    private Toolbar mToolbar;
    private View parent_view;
    private ArrayList<RetailerItem> scheduledRetailerList;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.scheduled_retailer_list, container, false);
        setHasOptionsMenu(true);
        tempPref = requireActivity().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        rcvRetailerList = view.findViewById(R.id.rcRetailerList);
        btnChangeBeat = view.findViewById(R.id.btnChangeBeat);
        fabUpDown = view.findViewById(R.id.fabUpDown);
        imgNoRecord = view.findViewById(R.id.imgNoRecord);
        tvTemp = view.findViewById(R.id.tvTemp);

        utilityClass = new UtilityClass(getContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());

        mToolbar = requireActivity().findViewById(R.id.toolbar2);

        imgNoRecord.setVisibility(View.GONE);
        tvTemp.setVisibility(View.GONE);
        btnChangeBeat.setVisibility(View.GONE);
        //Intele
        getDistributerListFromBeatMap();

        return view;
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getting scheduled retailer list
        scheduledRetailerList = getSheduledRetailerList(0);

        if (scheduledRetailerList != null && scheduledRetailerList.size() > 0) {
            isPresentInBeat = true;
            Collections.sort(scheduledRetailerList, new Comparator<RetailerItem>() {
                @Override
                public int compare(RetailerItem o1, RetailerItem o2) {
                    return o1.getRetailerName().compareTo(o2.getRetailerName());
                }
            });

            adapter = new ScheduledRetailerListAdapter(ScheduledRetailerList.this,requireContext(), scheduledRetailerList, isPresentInBeat);
            layoutManager = new LinearLayoutManager(getContext());
            rcvRetailerList.setLayoutManager(layoutManager);
            int resId = R.anim.layout_animation_fall_down;
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
            rcvRetailerList.setLayoutAnimation(animation);
            rcvRetailerList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            RetailerActivity.imgFilterBy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick filter");
                    scheduledRetailerList.clear();
                    scheduledRetailerList = getSheduledRetailerList(1);
                    Log.d("TAG", "scheduledRetailerList lenght:"+scheduledRetailerList.size());
                    adapter = new ScheduledRetailerListAdapter(ScheduledRetailerList.this,requireContext(), scheduledRetailerList, isPresentInBeat);
                    layoutManager = new LinearLayoutManager(getContext());
                    rcvRetailerList.setLayoutManager(layoutManager);
                    int resId = R.anim.layout_animation_fall_down;
                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                    rcvRetailerList.setLayoutAnimation(animation);
                    rcvRetailerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });

            Log.d("TAG", "scheduledRetailerList: "+new Gson().toJson(scheduledRetailerList));



            SharedPreferences.Editor editor = tempPref.edit();
            editor.putBoolean(getString(R.string.is_on_retailer_page), true);
            editor.apply();

        } else {

            fabUpDown.setVisibility(View.GONE);
            imgNoRecord.setVisibility(View.VISIBLE);
            tvTemp.setVisibility(View.VISIBLE);

            tvTemp.setText(tempPref.getString(getString(R.string.retErrorKey), ""));

            btnChangeBeat.setVisibility(View.VISIBLE);
            rcvRetailerList.setVisibility(View.GONE);

        }

        fabUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutManager.findLastVisibleItemPosition() == scheduledRetailerList.size() - 1) {
                    rcvRetailerList.smoothScrollToPosition(0);
                } else {
                    rcvRetailerList.smoothScrollToPosition(scheduledRetailerList.size() - 1);
                }

            }
        });


        try {

            rcvRetailerList.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {

                    try {
                        if (layoutManager.findLastVisibleItemPosition() == scheduledRetailerList.size() - 1) {
                            fabUpDown.setImageResource(R.drawable.up_arrow48);
                        } else {
                            fabUpDown.setImageResource(R.drawable.down_arrow48);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (NoClassDefFoundError error) {
            //error.printStackTrace();
        }


        btnChangeBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), OrderBookingRetailing.class);
                intent.putExtra("change_beat", "yes");
                startActivity(intent);
                getActivity().finish();
                //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });


        if (tempPref.getBoolean(getString(R.string.is_on_retailer_page), false)) {
            RetailerActivity.imgBack.setImageResource(R.drawable.ic_home_white_24dp);
        }

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action) {
            View menuItemView = getActivity().findViewById(R.id.action);
            showDialog(menuItemView);
            return true;
        }

        return super.onOptionsItemSelected(item);


//        switch (item.getItemId()) {
//            case R.id.action:
//                View menuItemView = getActivity().findViewById(R.id.action);
//                showDialog(menuItemView);
//                break;
//            default:
//                return super.onOptionsItemSelected(item);
//        }


//        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
        mSearchItem = menu.findItem(R.id.m_search);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(mSearchItem);

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 2) {
                    searchTerm(newText);
                }
                return false;
            }
        });

        // Get the search close button image view
        ImageView closeButton = searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (scheduledRetailerList != null && scheduledRetailerList.size() > 0) {
                    isPresentInBeat = true;
                    ScheduledRetailerListAdapter adapter = new ScheduledRetailerListAdapter(ScheduledRetailerList.this,getContext(), scheduledRetailerList, isPresentInBeat);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    rcvRetailerList.setLayoutManager(layoutManager);
                    int resId = R.anim.layout_animation_fall_down;
                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                    rcvRetailerList.setLayoutAnimation(animation);
                    rcvRetailerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

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

                RetailerActivity.retailerTab.setVisibility(View.VISIBLE);
                RetailerActivity.headerTCPC.setVisibility(View.VISIBLE);
                RetailerActivity.retailerViewPager.setPagingEnabled(true);

                if (scheduledRetailerList != null && scheduledRetailerList.size() > 0) {
                    isPresentInBeat = true;
                    ScheduledRetailerListAdapter adapter = new ScheduledRetailerListAdapter(ScheduledRetailerList.this,getContext(), scheduledRetailerList, isPresentInBeat);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    rcvRetailerList.setLayoutManager(layoutManager);
                    int resId = R.anim.layout_animation_fall_down;
                    //LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                    //rcvRetailerList.setLayoutAnimation(animation);
                    rcvRetailerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                RetailerActivity.retailerTab.setVisibility(View.GONE);
                RetailerActivity.headerTCPC.setVisibility(View.GONE);
                RetailerActivity.retailerViewPager.setPagingEnabled(false);
               /* RetailerActivity.imgFilterBy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });*/


                return true;
            }
        });


    }

    public void getDistributerListFromBeatMap() {
        try {
            Cursor cursor = null;
            Cursor cursor2 = salesBeatDb.getDisBeatMap(tempPref.getString(getString(R.string.dis_id_key), ""));
            if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {

                do {

                    String bid = cursor2.getString(cursor2.getColumnIndex(SalesBeatDb.KEY_BID));

                    cursor = salesBeatDb.getAllDataFromBeatListTable2(bid);
                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        do {

                            BeatItem beatItem = new BeatItem();
                            beatItem.setBeatId(cursor.getString(cursor.getColumnIndex("beat_id")));
                            beatItem.setBeatName(cursor.getString(cursor.getColumnIndex("beat_name")));
                            beatList.add(beatItem);

                        } while (cursor.moveToNext());

                    }

                } while (cursor2.moveToNext());

            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    public void showDialog(View menuItem) {

        final PopupMenu popupMenu = new PopupMenu(requireContext(), menuItem);

        requireActivity().getMenuInflater().inflate(R.menu.retialer_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                popupMenu.dismiss();

                if (item.getItemId() == R.id.changeBeat) {

                    Intent intent = new Intent(getActivity(), OrderBookingRetailing.class);
                    intent.putExtra("change_beat", "yes");
                    startActivity(intent);
                    getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                } else if (item.getItemId() == R.id.changeDistributor) {

                    Intent intent = new Intent(getActivity(), OrderBookingRetailing.class);
                    intent.putExtra("change_distributor_pjp", "yes");
                    startActivity(intent);
                    getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                } else if (item.getItemId() == R.id.addNewRetailer) {

                    Intent intent = new Intent(getActivity(), AddNewRetailerActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                }  else if (item.getItemId() == R.id.changeDistance) {
                    Log.d("TAG", "check click");
                    scheduledRetailerList.clear();
                    scheduledRetailerList = getSheduledRetailerList(1);
                    Log.d("TAG", "scheduledRetailerList lenght:"+scheduledRetailerList.size());
                    adapter = new ScheduledRetailerListAdapter(ScheduledRetailerList.this,requireContext(), scheduledRetailerList, isPresentInBeat);
                    layoutManager = new LinearLayoutManager(getContext());
                    rcvRetailerList.setLayoutManager(layoutManager);
                    int resId = R.anim.layout_animation_fall_down;
                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
                    rcvRetailerList.setLayoutAnimation(animation);
                    rcvRetailerList.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

//                    getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                }/*else if (item.getItemId() == R.id.addPreferredRetailer) {

                    Intent intent = new Intent(getActivity(), AddPreferredRetailerActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

                }*/ else if (item.getItemId() == R.id.refreshLocal) {

                    //utilityClass.refreshDataBase();

                    new RefreshTask().execute();

                }
                return false;
            }
        });

        popupMenu.show();
    }

    public ArrayList<RetailerItem> getSheduledRetailerList(int check) {

        ArrayList<RetailerItem> retailerList = new ArrayList<>();
        ArrayList<String> orderPlacedByRetailersList = new ArrayList<>();

        Cursor orderPlacedBy = null, cursor = null;

        try {
            orderPlacedBy = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();

            if (orderPlacedBy != null && orderPlacedBy.getCount() > 0 && orderPlacedBy.moveToFirst()) {

                do {

                    orderPlacedByRetailersList.add(orderPlacedBy.getString(orderPlacedBy.getColumnIndex("rid")));

                } while (orderPlacedBy.moveToNext());

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (orderPlacedBy != null)
                orderPlacedBy.close();
        }

        try {
            if(check == 0){
                Log.d("TAG", "Update Sort Data-1");
                cursor = salesBeatDb.getAllDataFromRetailerListTable(tempPref.getString(getString(R.string.beat_id_key), ""));
            }else if(check == 1){
                Log.d("TAG", "Update Sort Data");
                cursor = salesBeatDb.getAllDataFromRetailerListTableNew(tempPref.getString(getString(R.string.beat_id_key), ""));
            }

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    RetailerItem retailerItem = new RetailerItem();

                    retailerItem.setRetailerId(cursor.getString(cursor.getColumnIndex("retailer_id")));
                    retailerItem.setRetailerbeat_unic_id(cursor.getString(cursor.getColumnIndex("beat_id_r")));
                    retailerItem.setRetailer_unic_id(cursor.getString(cursor.getColumnIndex("ruid_id")));
                    retailerItem.setRetailerName(cursor.getString(cursor.getColumnIndex("retailer_name")));
                    retailerItem.setRetailerPhone(cursor.getString(cursor.getColumnIndex("owner_phone")));
                    retailerItem.setRetailerAddress(cursor.getString(cursor.getColumnIndex("retailer_address")));
                    retailerItem.setRetailer_state(cursor.getString(cursor.getColumnIndex("retailer_state")));
                    retailerItem.setRetailer_email(cursor.getString(cursor.getColumnIndex("retailer_email")));
                    retailerItem.setRetailer_owner_name(cursor.getString(cursor.getColumnIndex("owner_name")));
                    retailerItem.setRetailer_gstin(cursor.getString(cursor.getColumnIndex("retailer_gstin")));
                    retailerItem.setRetailer_fssai(cursor.getString(cursor.getColumnIndex("retailer_fssai")));
                    retailerItem.setReatilerWhatsAppNo(cursor.getString(cursor.getColumnIndex("whatsapp_no")));
                    retailerItem.setRetailer_city(cursor.getString(cursor.getColumnIndex("zone")));
                    retailerItem.setReatialerTarget(cursor.getString(cursor.getColumnIndex("target")));
                    retailerItem.setRetailer_grade(cursor.getString(cursor.getColumnIndex("retailer_grade")));
                    retailerItem.setRetailerLocality(cursor.getString(cursor.getColumnIndex("locality")));
                    retailerItem.setRetailer_image(cursor.getString(cursor.getColumnIndex("retailer_image")));
                    retailerItem.setRetailer_pin(cursor.getString(cursor.getColumnIndex("retailer_pin")));
                    retailerItem.setLatitude(cursor.getString(cursor.getColumnIndex("retailer_latitude")));
                    retailerItem.setLongtitude(cursor.getString(cursor.getColumnIndex("retailer_longtitude")));

                    retailerList.add(retailerItem);

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            Log.e("SREtailer", "===" + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
        }

        for (int index = 0; index < orderPlacedByRetailersList.size(); index++) {

            String retailer_id = orderPlacedByRetailersList.get(index);

            for (int i = 0; i < retailerList.size(); i++) {

                if (retailerList.get(i).getRetailerId().equalsIgnoreCase(retailer_id)) {
                    retailerList.remove(i);
                }
            }
        }

        return retailerList;
    }

    private void searchTerm(String newText) {

        ArrayList<RetailerItem> retailerList = new ArrayList<>();

        ArrayList<String> orderPlacedByList = new ArrayList<>();

        Cursor orderPlacedByRetailer = null, cursor = null, cursor3 = null;
        ;

        try {

            orderPlacedByRetailer = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();
            orderPlacedByList.clear();

            if (orderPlacedByRetailer != null && orderPlacedByRetailer.getCount() > 0 && orderPlacedByRetailer.moveToFirst()) {

                do {

                    String rid = orderPlacedByRetailer.getString(orderPlacedByRetailer.getColumnIndex("rid"));
                    orderPlacedByList.add(rid);

                } while (orderPlacedByRetailer.moveToNext());
            }

        } catch (Exception e) {
            Log.e("Visted ret", "===" + e.getMessage());
        } finally {
            if (orderPlacedByRetailer != null)
                orderPlacedByRetailer.close();
        }


        //to remove duplicate value from list
        Set<String> hs = new LinkedHashSet<>();
        hs.addAll(orderPlacedByList);
        orderPlacedByList.clear();
        orderPlacedByList.addAll(hs);

        try {
            cursor = salesBeatDb.searchIntoRetailerListTable(newText, tempPref.getString(getString(R.string.beat_id_key), ""));
            retailerList.clear();
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {

                    isPresentInBeat = true;

                    RetailerItem retailerItem = new RetailerItem();
                    String rid = cursor.getString(cursor.getColumnIndex("retailer_id"));
                    retailerItem.setRetailerId(rid);
                    retailerItem.setRetailerbeat_unic_id(cursor.getString(cursor.getColumnIndex("beat_id_r")));
                    retailerItem.setRetailer_unic_id(cursor.getString(cursor.getColumnIndex("ruid_id")));
                    retailerItem.setRetailerName(cursor.getString(cursor.getColumnIndex("retailer_name")));
                    retailerItem.setRetailerPhone(cursor.getString(cursor.getColumnIndex("owner_phone")));
                    retailerItem.setRetailerAddress(cursor.getString(cursor.getColumnIndex("retailer_address")));
                    retailerItem.setRetailer_state(cursor.getString(cursor.getColumnIndex("retailer_state")));
                    retailerItem.setRetailer_email(cursor.getString(cursor.getColumnIndex("retailer_email")));
                    retailerItem.setRetailer_owner_name(cursor.getString(cursor.getColumnIndex("owner_name")));
                    retailerItem.setRetailer_gstin(cursor.getString(cursor.getColumnIndex("retailer_gstin")));
                    retailerItem.setRetailer_fssai(cursor.getString(cursor.getColumnIndex("retailer_fssai")));
                    retailerItem.setReatilerWhatsAppNo(cursor.getString(cursor.getColumnIndex("whatsapp_no")));
                    retailerItem.setRetailer_city(cursor.getString(cursor.getColumnIndex("zone")));
                    retailerItem.setRetailer_grade(cursor.getString(cursor.getColumnIndex("retailer_grade")));
                    retailerItem.setRetailerLocality(cursor.getString(cursor.getColumnIndex("locality")));
                    retailerItem.setRetailer_image(cursor.getString(cursor.getColumnIndex("retailer_image")));
                    retailerItem.setRetailer_pin(cursor.getString(cursor.getColumnIndex("retailer_pin")));
                    retailerItem.setLatitude(cursor.getString(cursor.getColumnIndex("retailer_latitude")));
                    retailerItem.setLongtitude(cursor.getString(cursor.getColumnIndex("retailer_longtitude")));

                    if (orderPlacedByList.size() > 0) {

                        boolean flag = true;
                        for (int i = 0; i < orderPlacedByList.size(); i++) {

                            if (orderPlacedByList.get(i).equalsIgnoreCase(rid)) {

                                flag = false;
                            }
                        }

                        if (flag) {
                            retailerList.add(retailerItem);
                        }

                    } else {
                        retailerList.add(retailerItem);
                    }

                } while (cursor.moveToNext());

            } else {
                //Intelegains
                if (beatList != null) {

                    String exitsBID = tempPref.getString(getString(R.string.beat_id_key), "");
                    String retailerName, BeatID;
                    retailerList.clear();
                    for (int i = 0; i < beatList.size(); i++) {
                        if (!beatList.get(i).getBeatId().equalsIgnoreCase(exitsBID)) {
                            cursor3 = salesBeatDb.searchIntoRetailerListTable(newText, beatList.get(i).getBeatId().trim());
                            if (cursor3 != null && cursor3.getCount() > 0 && cursor3.moveToFirst()) {
                                do {
                                    isPresentInBeat = false;
                                    //Changes in Model Class
                                    //Intelegains
                                    RetailerItem retailerItem = retailerItem = new RetailerItem();
                                    retailerName = cursor3.getString(cursor3.getColumnIndex("retailer_name"));
                                    BeatID = cursor3.getString(cursor3.getColumnIndex("beat_id_r"));
                                    try {
                                        cursor = salesBeatDb.getAllDataFromBeatListTable2(BeatID);
                                        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                                            do {
                                                retailerItem.setBeatName(cursor.getString(cursor.getColumnIndex("beat_name")));

                                            } while (cursor.moveToNext());

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    retailerItem.setRetailerbeat_unic_id(cursor3.getString(cursor3.getColumnIndex("beat_id_r")));
                                    retailerItem.setRetailer_unic_id(cursor3.getString(cursor3.getColumnIndex("ruid_id")));
                                    retailerItem.setRetailerName(cursor3.getString(cursor3.getColumnIndex("retailer_name")));
                                    retailerItem.setRetailerPhone(cursor3.getString(cursor3.getColumnIndex("owner_phone")));
                                    retailerItem.setRetailerAddress(cursor3.getString(cursor3.getColumnIndex("retailer_address")));
                                    retailerItem.setRetailer_state(cursor3.getString(cursor3.getColumnIndex("retailer_state")));
                                    retailerItem.setRetailer_email(cursor3.getString(cursor3.getColumnIndex("retailer_email")));
                                    retailerItem.setRetailer_owner_name(cursor3.getString(cursor3.getColumnIndex("owner_name")));
                                    retailerItem.setRetailer_gstin(cursor3.getString(cursor3.getColumnIndex("retailer_gstin")));
                                    retailerItem.setRetailer_fssai(cursor3.getString(cursor3.getColumnIndex("retailer_fssai")));
                                    retailerItem.setReatilerWhatsAppNo(cursor3.getString(cursor3.getColumnIndex("whatsapp_no")));
                                    retailerItem.setRetailer_city(cursor3.getString(cursor3.getColumnIndex("zone")));
                                    retailerItem.setRetailer_grade(cursor3.getString(cursor3.getColumnIndex("retailer_grade")));
                                    retailerItem.setRetailerLocality(cursor3.getString(cursor3.getColumnIndex("locality")));
                                    retailerItem.setRetailer_image(cursor3.getString(cursor3.getColumnIndex("retailer_image")));
                                    retailerItem.setRetailer_pin(cursor3.getString(cursor3.getColumnIndex("retailer_pin")));
                                    retailerItem.setLatitude(cursor3.getString(cursor3.getColumnIndex("retailer_latitude")));
                                    retailerItem.setLongtitude(cursor3.getString(cursor3.getColumnIndex("retailer_longtitude")));
                                    /*if (retailerName != null) {
                                       // if (retailerName.equalsIgnoreCase(newText)) {
                                            Toast.makeText(getContext(), retailerName + "It is Present in =" + BeatID + " Beat", Toast.LENGTH_LONG).show();
                                        //}isPresent

                                    }*/
                                    retailerList.add(retailerItem);

                                } while (cursor3.moveToNext());
                            }

                        }

                    }
                }

            }

            //to remove duplicate value from list
            Set<RetailerItem> hs2 = new LinkedHashSet<>();
            hs2.addAll(retailerList);
            retailerList.clear();
            retailerList.addAll(hs2);

            if (isPresentInBeat) {
                Collections.sort(retailerList, new Comparator<RetailerItem>() {
                    @Override
                    public int compare(RetailerItem o1, RetailerItem o2) {
                        return o1.getRetailerName().compareTo(o2.getRetailerName());
                    }
                });
            } else {
                Collections.sort(retailerList, new Comparator<RetailerItem>() {
                    @Override
                    public int compare(RetailerItem o1, RetailerItem o2) {
                        return o1.getBeatName().compareTo(o2.getBeatName());
                    }
                });
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
            if (cursor3 != null)
                cursor3.close();
        }

        ScheduledRetailerListAdapter adapter = new ScheduledRetailerListAdapter(ScheduledRetailerList.this,getContext(), retailerList, isPresentInBeat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvRetailerList.setLayoutManager(layoutManager);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rcvRetailerList.setLayoutAnimation(animation);
        rcvRetailerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));

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
                        mToolbar.setBackgroundColor(getThemeColor(getContext(), android.R.attr.colorPrimary));
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
                        mToolbar.setBackgroundColor(getThemeColor(getContext(), android.R.attr.colorPrimary));
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

    public void showCustomToast(String formatDistance,String lat,String log,String rId) {

        String[] parts = formatDistance.split("\\.");
        String beforeDecimal = parts[0];
        Log.d("TAG", "Check afterDecimal-1: " + beforeDecimal.length());


        final Snackbar snackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_toast_floating, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);
//        TextView txtRange = ((TextView) custom_view.findViewById(R.id.txtRange));
        Log.d("TAG", "Check afterDecimal check: " + parts[0].length());
        ((TextView) custom_view.findViewById(R.id.txtRange)).setText("you are "+formatDistance+" far");

        (custom_view.findViewById(R.id.icon_refresh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
//                adapter.checkRadiusInMtr(lat,log);
                Intent i = new Intent(getContext(), MapsActivity.class);
                i.putExtra("retailerId",rId);
                startActivity(i);
//                Toast.makeText(getContext(), "Refresh Location..!", Toast.LENGTH_SHORT).show();
            }
        });

        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    public void toastError(View view,String msg) {
        final Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }


    private class RefreshTask extends AsyncTask<Void, Void, Void> {

        //ProgressDialog progressDialog;

        @Override
        protected Void doInBackground(Void... voids) {
            utilityClass.refreshDataBase();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getContext());
//            progressDialog.setMessage("Refreshing...");
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // progressDialog.dismiss();
            Intent retIntent = new Intent(getContext(), RetailerActivity.class);
            startActivity(retIntent);
            getActivity().finish();
        }
    }
}
