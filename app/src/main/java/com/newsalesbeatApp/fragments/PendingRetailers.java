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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.AddNewRetailerActivity;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.activities.RetailerActivity;
import com.newsalesbeatApp.adapters.VisitedRetailerListAdapter;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PendingRetailers extends Fragment {

    ShimmerRecyclerView rcvRetailerList;

    Button btnChangeBeat;
    ImageView imgNoRecord;
    TextView tvTemp;

    SharedPreferences tempPref;
    VisitedRetailerListAdapter adapter;

    FloatingActionButton fabUpDown;
    LinearLayoutManager layoutManager;

    UtilityClass utilityClass;
    SalesBeatDb salesBeatDb;
    private MenuItem mSearchItem;
    private Toolbar mToolbar;
    private ArrayList<ArrayList<String>> orderPlacedForDistributorList;
    private ArrayList<RetailerItem> visitedRetailerList;
    private Handler handler;
    private Runnable runnable;

    private static int getThemeColor(Context context, int id) {
        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(new int[]{id});
        int result = a.getColor(0, 0);
        a.recycle();
        return result;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.pending_retailers_fragment, container, false);
        setHasOptionsMenu(true);
        //prefSFA = getContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = getContext().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        rcvRetailerList = view.findViewById(R.id.rcRetailerList);
        btnChangeBeat = view.findViewById(R.id.btnChangeBeat);
        fabUpDown = view.findViewById(R.id.fabUpDown);
        imgNoRecord = view.findViewById(R.id.imgNoRecord);
        tvTemp = view.findViewById(R.id.tvTemp);

        mToolbar = getActivity().findViewById(R.id.toolbar2);
        utilityClass = new UtilityClass(getContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());
        handler = new Handler();

        imgNoRecord.setVisibility(View.GONE);
        tvTemp.setVisibility(View.GONE);
        btnChangeBeat.setVisibility(View.GONE);

        return view;
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get visited retailer list
        visitedRetailerList = getVisitedRetailerList();

        Log.e("visitedRetailerList", " Length: " + visitedRetailerList.size());

        //calculate pc and sale
        calculateTcPCAndSale();

        //get distributor list
        orderPlacedForDistributorList = getOrderPlacedForDistributorList(visitedRetailerList);

        if (visitedRetailerList != null && visitedRetailerList.size() > 0) {

            //Collections.reverse(retailerItemList);
            adapter = new VisitedRetailerListAdapter(getContext(), visitedRetailerList, orderPlacedForDistributorList);
            layoutManager = new LinearLayoutManager(getContext());
            rcvRetailerList.setLayoutManager(layoutManager);
            //rcvRetailerList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
            rcvRetailerList.setAdapter(adapter);

            SharedPreferences.Editor editor = tempPref.edit();
            editor.putBoolean(getString(R.string.is_on_retailer_page), true);
            editor.apply();

        } else {

            fabUpDown.setVisibility(View.GONE);
            imgNoRecord.setVisibility(View.VISIBLE);
            tvTemp.setVisibility(View.VISIBLE);
            btnChangeBeat.setVisibility(View.VISIBLE);
            rcvRetailerList.setVisibility(View.GONE);

        }


        fabUpDown.setOnClickListener(view13 -> {

            if (layoutManager.findLastVisibleItemPosition() == visitedRetailerList.size() - 1) {
                rcvRetailerList.smoothScrollToPosition(0);
            } else {
                rcvRetailerList.smoothScrollToPosition(visitedRetailerList.size() - 1);
            }

        });


        try {

            rcvRetailerList.setOnScrollChangeListener((view12, i, i1, i2, i3) -> {

                try {
                    if (layoutManager.findLastVisibleItemPosition() == visitedRetailerList.size() - 1) {
                        fabUpDown.setImageResource(R.drawable.up_arrow48);
                    } else {
                        fabUpDown.setImageResource(R.drawable.down_arrow48);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (NoClassDefFoundError error) {
            error.printStackTrace();
        }


        btnChangeBeat.setOnClickListener(view1 -> {

            Intent intent = new Intent(getContext(), OrderBookingRetailing.class);
            intent.putExtra("change_beat", "yes");
            startActivity(intent);
            getActivity().finish();
            //getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

        });
    }

    private void calculateTcPCAndSale() {

        Cursor orederPlacedBy = null, orderPlacedByNew = null;
        int tc = 0, pc = 0;
        double sale = 0;

        List<String> retPc = new ArrayList<>();
        List<String> newRetPC = new ArrayList<>();
        List<String> retTc = new ArrayList<>();
        List<String> newRetTC = new ArrayList<>();

        try {

            orederPlacedBy = salesBeatDb.getRetailersFromOderPlacedByRetailersTable2("error");

            if (orederPlacedBy != null && orederPlacedBy.getCount() > 0 && orederPlacedBy.moveToFirst()) {

                do {


                    String rid = orederPlacedBy.getString(orederPlacedBy.getColumnIndex("rid"));
                    String did = orederPlacedBy.getString(orederPlacedBy.getColumnIndex("did"));
                    String orderType = orederPlacedBy.getString(orederPlacedBy.getColumnIndex("order_type"));

                    Log.e("VisitedRetailer", "===> rid: " + rid + " did: " + did + " order type: " + orderType);

                    if (orderType.equalsIgnoreCase("onShop")
                            || orderType.equalsIgnoreCase("telephonic")
                            || orderType.equalsIgnoreCase("revise order")) {

                        retPc.add(rid);
                    }

                    retTc.add(rid);

                    Cursor cursor = salesBeatDb.getSpecificDataFromOrderEntryListTable22(rid, did);

                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        do {

                            String strSale = cursor.getString(cursor.getColumnIndex("brand_qty"));
                            String cFactor = cursor.getString(cursor.getColumnIndex("conversion_factor"));

                            if (!orderType.equalsIgnoreCase("cancelled")) {

                                double saleVal = 0, conversionFactor = 0;
                                try {

                                    saleVal = Double.parseDouble(strSale);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                try {

                                    conversionFactor = Double.parseDouble(cFactor);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                double temp = (saleVal / conversionFactor);
                                sale = sale + temp;
                            }

                        } while (cursor.moveToNext());
                    }

                } while (orederPlacedBy.moveToNext());
            }

            orderPlacedByNew = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTablePend("error");
            if (orderPlacedByNew != null && orderPlacedByNew.getCount() > 0 && orderPlacedByNew.moveToFirst()) {

                do {

                    String nrid = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("nrid"));
                    String tempRid = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex(SalesBeatDb.KEY_NEW_RETAILER_TEMP_IDD));
                    String new_did = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_did"));
                    String orderType = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_order_comment"));
                    if (orderType.equalsIgnoreCase("new productive")) {
                        newRetPC.add(nrid);
                    }

                    newRetTC.add(nrid);

                    Cursor cursor = salesBeatDb.getSpecificDataFromNewOrderEntryListTable22(nrid, new_did);
                    Log.e("RRRR", "---->" + cursor.getCount() + " " + nrid + "  " + tempRid);

                    if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                        do {

                            String strSale = cursor.getString(cursor.getColumnIndex("new_brand_qty"));
                            String cFactor = cursor.getString(cursor.getColumnIndex("conversion_factor"));
                            double s = 0;
                            try {
                                s = Double.valueOf(strSale);
                            } catch (Exception e) {
                                e.getMessage();
                            }
                            sale = sale + s / Double.valueOf(cFactor);

                        } while (cursor.moveToNext());

                    }

                } while (orderPlacedByNew.moveToNext());

            }

            //to remove duplicate value from list
            Set<String> hs = new LinkedHashSet<>();
            hs.addAll(retPc);
            retPc.clear();
            retPc.addAll(hs);

            hs.addAll(retTc);
            retTc.clear();
            retTc.addAll(hs);

            Set<String> hs2 = new LinkedHashSet<>();
            hs2.addAll(newRetPC);
            newRetPC.clear();
            newRetPC.addAll(hs2);

            hs2.addAll(newRetTC);
            newRetTC.clear();
            newRetTC.addAll(hs2);

            RetailerActivity.tvNc.setText(String.valueOf(newRetTC.size()));

            pc = retPc.size() + newRetPC.size();
            tc = retTc.size() + newRetTC.size();
            RetailerActivity.tvPc.setText(String.valueOf(pc));
            RetailerActivity.tvSale.setText(new DecimalFormat("##.##").format(sale) + " " + getString(R.string.unitt));
            RetailerActivity.tvTc.setText(String.valueOf(tc));

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            if (orederPlacedBy != null)
                orederPlacedBy.close();
            if (orderPlacedByNew != null)
                orderPlacedByNew.close();
        }
    }

    private ArrayList<ArrayList<String>> getOrderPlacedForDistributorList(ArrayList<RetailerItem> visitedRetailerList) {

        ArrayList<ArrayList<String>> distributorList = new ArrayList<>();

//        for (int i = 0; i < visitedRetailerList.size(); i++) {
//
//            Cursor disPlacedBy = salesBeatDb.getDistributorFromOderPlacedByRetailersTable(visitedRetailerList.get(i).getRetailerId());
//            ArrayList<String> disPlacedByList = new ArrayList<>();
//
//            try {
//
//                if (disPlacedBy != null && disPlacedBy.getCount() > 0 && disPlacedBy.moveToFirst()) {
//
//                    do {
//                        disPlacedByList.add(disPlacedBy.getString(disPlacedBy.getColumnIndex("did")));
//                    } while (disPlacedBy.moveToNext());
//                }
//
//                distributorList.add(disPlacedByList);
//
//            } catch (Exception e) {
//                e.getMessage();
//            } finally {
//                if (disPlacedBy != null)
//                    disPlacedBy.close();
//            }
//        }

        return distributorList;
    }

    private ArrayList<RetailerItem> getVisitedRetailerList() {

        ArrayList<RetailerItem> visitedRetailerList = new ArrayList<>();
        ArrayList<String> orderPlacedByList = new ArrayList<>();

        Cursor orderPlacedByRetailer = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();
        orderPlacedByList.clear();

        try {

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

        //LIFO list
        Collections.reverse(orderPlacedByList);

        String bid = tempPref.getString(getString(R.string.beat_id_key), "");
        Cursor visitedRetailerCursor = null;

        try {

            if (orderPlacedByList.size() > 0) {

                for (int i = 0; i < orderPlacedByList.size(); i++) {

                    String rid = orderPlacedByList.get(i);
                    visitedRetailerCursor = salesBeatDb.getAllDataFromRetailerListTable2(bid, rid);

                    if (visitedRetailerCursor != null && visitedRetailerCursor.getCount() > 0
                            && visitedRetailerCursor.moveToFirst()) {

                        RetailerItem visitedRetailerItem = new RetailerItem();

                        visitedRetailerItem.setRetailerId(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_id")));
                        visitedRetailerItem.setRetailerbeat_unic_id(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("beat_id_r")));
                        visitedRetailerItem.setRetailer_unic_id(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("ruid_id")));
                        visitedRetailerItem.setRetailerName(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_name")));
                        visitedRetailerItem.setRetailerPhone(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("owner_phone")));
                        visitedRetailerItem.setRetailerAddress(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_address")));
                        visitedRetailerItem.setRetailer_state(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_state")));
                        visitedRetailerItem.setRetailer_email(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_email")));
                        visitedRetailerItem.setRetailer_owner_name(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("owner_name")));
                        visitedRetailerItem.setRetailer_gstin(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_gstin")));
                        visitedRetailerItem.setRetailer_fssai(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_fssai")));
                        visitedRetailerItem.setReatilerWhatsAppNo(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("whatsapp_no")));
                        visitedRetailerItem.setRetailer_city(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("zone")));
                        visitedRetailerItem.setReatialerTarget(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("target")));
                        visitedRetailerItem.setRetailer_grade(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_grade")));
                        visitedRetailerItem.setRetailerLocality(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("locality")));
                        visitedRetailerItem.setRetailer_image(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_image")));
                        visitedRetailerItem.setRetailer_pin(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_pin")));
                        visitedRetailerItem.setLatitude(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_latitude")));
                        visitedRetailerItem.setLongtitude(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_longtitude")));

                        Cursor orderType = salesBeatDb.getOrderTypeFromOderPlacedByRetailersTable(orderPlacedByList.get(i));
                        orderType.moveToFirst();
                        visitedRetailerItem.setOrderType(orderType.getString(orderType.getColumnIndex("order_type")));
                        visitedRetailerItem.setTimeStamp(orderType.getString(orderType.getColumnIndex("taken_at")));
                        visitedRetailerItem.setServerStatus(orderType.getString(orderType.getColumnIndex("order_status")));

                        visitedRetailerList.add(visitedRetailerItem);
                    }
                }
            }

        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (visitedRetailerCursor != null)
                visitedRetailerCursor.close();
        }

        //add new retailer list into visited retailer
        ArrayList<RetailerItem> newRetailerList = getNewRetailerList(bid);
        ArrayList<RetailerItem> preferredRetailerList = getPreferredRetailerList(bid);


        visitedRetailerList.addAll(newRetailerList);


        Collections.sort(visitedRetailerList, new Comparator<RetailerItem>() {
            @Override
            public int compare(RetailerItem r1, RetailerItem r2) {
                return r2.getTimeStamp().compareTo(r1.getTimeStamp());
            }
        });


        visitedRetailerList.addAll(preferredRetailerList);

        return visitedRetailerList;
    }

    private ArrayList<RetailerItem> getPreferredRetailerList(String bid) {

        Cursor orderPlacedByNew = null, cursor;
        ArrayList<RetailerItem> newRetailerList = new ArrayList<>();

        cursor = salesBeatDb.getAllDataFromNewPreferredRetailerListTable2(bid);
        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    RetailerItem retailerItem = new RetailerItem();

//                    String new_rid = cursor.getString(cursor.getColumnIndex("nrid"));
                    String new_rid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_ID));
                    Log.e("Visited", "....NRID:" + new_rid);
                    String shop_name = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String shop_address = "N/A";//cursor.getString(cursor.getColumnIndex("new_retailer_address"));
                    String owner_name = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_CONTACT_NAME1));
                    String owner_mobile_no = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME1));
                    String whatsappNo = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_DISTRIBUTOR_CONTACT_PERSON_NAME1));
                    String lat = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_LATITUDE));
                    String longt = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_LONGITUDE));
                    String state = "Bihar Hard code";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String zone = "Bihar Hard code";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String locality = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_BLOCK));
                    String pincode = "N/A";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String email_id = "N/A";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String gstin = "N/A";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String fssai_no = "N/A";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String grade = "N/A";//cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_PREFERRED_RETAILER_FIRM_NAME));
                    String serverStatus = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_PREFERRED_ORDER_STATUS));

                    retailerItem.setRetailerId(new_rid);
                    retailerItem.setRetailerbeat_unic_id(new_rid);
                    retailerItem.setRetailer_unic_id(new_rid);
                    retailerItem.setRetailerName(shop_name);
                    retailerItem.setRetailerPhone(owner_mobile_no);
                    retailerItem.setRetailerAddress(shop_address);
                    retailerItem.setRetailer_state(state);
                    retailerItem.setRetailer_email(email_id);
                    retailerItem.setRetailer_owner_name(owner_name);
                    retailerItem.setRetailer_gstin(gstin);
                    retailerItem.setRetailer_fssai(fssai_no);
                    retailerItem.setReatilerWhatsAppNo(whatsappNo);
                    retailerItem.setRetailer_city(zone);
                    retailerItem.setRetailer_grade(grade);
                    retailerItem.setRetailerLocality(locality);
                    retailerItem.setRetailer_image("");
                    retailerItem.setRetailer_pin(pincode);
                    retailerItem.setLatitude(lat);
                    retailerItem.setLongtitude(longt);

                    String orderType = "";
                    String orderTakenTime = "";
                    //String serverStatus = "";

                    orderPlacedByNew = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewPreferredRetailersTable(new_rid);
                    Log.e("Visited", "....Count:" + orderPlacedByNew.getCount());
                    if (orderPlacedByNew != null && orderPlacedByNew.getCount() > 0 && orderPlacedByNew.moveToFirst()) {
                        orderType = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex(SalesBeatDb.KEY_PREFERRED_ORDER_COMMENT));
                        orderTakenTime = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex(SalesBeatDb.KEY_PREFERRED_ORDER_PLACED_TIME));
                        //serverStatus = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex(SalesBeatDb.KEY_PREFERRED_ORDER_STATUS));
                        Log.e("Visited", "Pre orderType:" + serverStatus);
                    }

                    if (orderType.equalsIgnoreCase("p productive")) {
                        retailerItem.setOrderType("p productive");
                        retailerItem.setTimeStamp(orderTakenTime);
                    } else {
                        retailerItem.setOrderType("pre no order");
                        retailerItem.setTimeStamp(orderTakenTime);
                    }

                    retailerItem.setServerStatus(serverStatus);

                    newRetailerList.add(retailerItem);

                } while (cursor.moveToNext());
            }

            //to remove duplicate value from list
            Set<RetailerItem> hs2 = new LinkedHashSet<>();
            hs2.addAll(newRetailerList);
            newRetailerList.clear();
            newRetailerList.addAll(hs2);

        } catch (Exception e) {
            Log.e("VisRet", "===" + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            if (orderPlacedByNew != null)
                orderPlacedByNew.close();
        }

        //to remove duplicate value from list
        ArrayList<RetailerItem> newList = new ArrayList<>();
        ArrayList<String> newList2 = new ArrayList<>();

        for (int i = 0; i < newRetailerList.size(); i++) {
            String nrid = newRetailerList.get(i).getRetailerId();
            RetailerItem newRetailerItem = newRetailerList.get(i);
            if (!newList2.contains(nrid)) {

                newList.add(newRetailerItem);
                newList2.add(nrid);
            }
        }

        return newList;

    }

    private ArrayList<RetailerItem> getNewRetailerList(String bidd) {
        Cursor orderPlacedByNew = null, cursor;
        ArrayList<RetailerItem> newRetailerList = new ArrayList<>();

        cursor = salesBeatDb.getAllDataFromNewRetailerListTable2(bidd);
        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    RetailerItem retailerItem = new RetailerItem();

                    String new_rid = cursor.getString(cursor.getColumnIndex("nrid"));
                    // String new_rid = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_NEW_RETAILER_TEMP_IDD));
                    Log.e("Visited", "....NRID:" + new_rid);
                    String shop_name = cursor.getString(cursor.getColumnIndex("new_retailer_name"));
                    String shop_address = cursor.getString(cursor.getColumnIndex("new_retailer_address"));
                    String owner_name = cursor.getString(cursor.getColumnIndex("new_owner_name"));
                    String owner_mobile_no = cursor.getString(cursor.getColumnIndex("new_owner_phone"));
                    String whatsappNo = cursor.getString(cursor.getColumnIndex("new_whatsapp_no"));
                    String lat = cursor.getString(cursor.getColumnIndex("new_retailer_latitude"));
                    String longt = cursor.getString(cursor.getColumnIndex("new_retailer_longtitude"));
                    String state = cursor.getString(cursor.getColumnIndex("new_retailer_state"));
                    String zone = cursor.getString(cursor.getColumnIndex("new_zone"));
                    String locality = cursor.getString(cursor.getColumnIndex("new_locality"));
                    String pincode = cursor.getString(cursor.getColumnIndex("new_retailer_pin"));
                    String email_id = cursor.getString(cursor.getColumnIndex("new_retailer_email"));
                    String gstin = cursor.getString(cursor.getColumnIndex("new_retailer_gstin"));
                    String fssai_no = cursor.getString(cursor.getColumnIndex("new_retailer_fssai"));
                    String grade = cursor.getString(cursor.getColumnIndex("new_retailer_grade"));

                    retailerItem.setRetailerId(new_rid);
                    retailerItem.setRetailerbeat_unic_id(new_rid);
                    retailerItem.setRetailer_unic_id(new_rid);
                    retailerItem.setRetailerName(shop_name);
                    retailerItem.setRetailerPhone(owner_mobile_no);
                    retailerItem.setRetailerAddress(shop_address);
                    retailerItem.setRetailer_state(state);
                    retailerItem.setRetailer_email(email_id);
                    retailerItem.setRetailer_owner_name(owner_name);
                    retailerItem.setRetailer_gstin(gstin);
                    retailerItem.setRetailer_fssai(fssai_no);
                    retailerItem.setReatilerWhatsAppNo(whatsappNo);
                    retailerItem.setRetailer_city(zone);
                    retailerItem.setRetailer_grade(grade);
                    retailerItem.setRetailerLocality(locality);
                    retailerItem.setRetailer_image("");
                    retailerItem.setRetailer_pin(pincode);
                    retailerItem.setLatitude(lat);
                    retailerItem.setLongtitude(longt);

                    String orderType = "";
                    String orderTakenTime = "";
                    String serverStatus = "";

                    orderPlacedByNew = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable(new_rid);
                    Log.e("Visited", "....Count:" + orderPlacedByNew.getCount());
                    if (orderPlacedByNew != null && orderPlacedByNew.getCount() > 0 && orderPlacedByNew.moveToFirst()) {
                        orderType = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_order_comment"));
                        orderTakenTime = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("new_taken_at"));
                        serverStatus = orderPlacedByNew.getString(orderPlacedByNew.getColumnIndex("server_status"));
                        Log.e("Visited", "....Status:" + serverStatus);
                    }

                    if (orderType.equalsIgnoreCase("new productive")) {
                        retailerItem.setOrderType("new productive");
                        retailerItem.setTimeStamp(orderTakenTime);
                    } else {
                        retailerItem.setOrderType("no order new");
                        retailerItem.setTimeStamp(orderTakenTime);
                    }

                    retailerItem.setServerStatus(serverStatus);

                    newRetailerList.add(retailerItem);

                } while (cursor.moveToNext());
            }

            //to remove duplicate value from list
            Set<RetailerItem> hs2 = new LinkedHashSet<>();
            hs2.addAll(newRetailerList);
            newRetailerList.clear();
            newRetailerList.addAll(hs2);

        } catch (Exception e) {
            Log.e("VisRet", "===" + e.getMessage());
        } finally {
            if (cursor != null)
                cursor.close();
            if (orderPlacedByNew != null)
                orderPlacedByNew.close();
        }

        //to remove duplicate value from list
        ArrayList<RetailerItem> newList = new ArrayList<>();
        ArrayList<String> newList2 = new ArrayList<>();

        for (int i = 0; i < newRetailerList.size(); i++) {
            String nrid = newRetailerList.get(i).getRetailerId();
            RetailerItem newRetailerItem = newRetailerList.get(i);
            if (!newList2.contains(nrid)) {

                newList.add(newRetailerItem);
                newList2.add(nrid);
            }
        }

        return newList;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action) {
            View menuItemView = requireActivity().findViewById(R.id.action);
            showDialog(menuItemView);
            return true;
        }

        return super.onOptionsItemSelected(item);


//        switch (item.getItemId()) {
//            case R.id.action:
//                View menuItemView = requireActivity().findViewById(R.id.action);
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

                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                RetailerActivity.retailerTab.setVisibility(View.GONE);
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Get the search close button image view
        ImageView closeButton = (ImageView) searchViewAndroidActionBar.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                adapter.getFilter().filter("");

                EditText et = searchViewAndroidActionBar.findViewById(R.id.search_src_text);

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

                adapter.getFilter().filter("");

                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Called when SearchView is expanding
                animateSearchToolbar(1, true, true);
                RetailerActivity.retailerTab.setVisibility(View.GONE);
                RetailerActivity.headerTCPC.setVisibility(View.GONE);
                RetailerActivity.retailerViewPager.setPagingEnabled(false);
                return true;
            }
        });
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

                } /*else if (item.getItemId() == R.id.addPreferredRetailer) {

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

    private void searchTerm(String newText) {

        ArrayList<RetailerItem> visitedRetailerList = new ArrayList<>();
        ArrayList<String> orderPlacedByList = new ArrayList<>();

        Cursor orderPlacedByRetailer = salesBeatDb.getRetailersFromOderPlacedByRetailersTable();
        orderPlacedByList.clear();

        try {

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

        //LIFO list
        Collections.reverse(orderPlacedByList);

        Cursor visitedRetailerCursor = null;
        try {

            if (orderPlacedByList.size() > 0) {

                visitedRetailerCursor = salesBeatDb.searchIntoRetailerListTable(newText, tempPref.getString(getString(R.string.beat_id_key), ""));

                if (visitedRetailerCursor != null && visitedRetailerCursor.getCount() > 0
                        && visitedRetailerCursor.moveToFirst()) {

                    do {

                        RetailerItem visitedRetailerItem = new RetailerItem();
                        String rid = visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_id"));
                        visitedRetailerItem.setRetailerId(rid);
                        visitedRetailerItem.setRetailerbeat_unic_id(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("beat_id_r")));
                        visitedRetailerItem.setRetailer_unic_id(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("ruid_id")));
                        visitedRetailerItem.setRetailerName(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_name")));
                        visitedRetailerItem.setRetailerPhone(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("owner_phone")));
                        visitedRetailerItem.setRetailerAddress(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_address")));
                        visitedRetailerItem.setRetailer_state(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_state")));
                        visitedRetailerItem.setRetailer_email(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_email")));
                        visitedRetailerItem.setRetailer_owner_name(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("owner_name")));
                        visitedRetailerItem.setRetailer_gstin(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_gstin")));
                        visitedRetailerItem.setRetailer_fssai(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_fssai")));
                        visitedRetailerItem.setReatilerWhatsAppNo(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("whatsapp_no")));
                        visitedRetailerItem.setRetailer_city(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("zone")));
                        visitedRetailerItem.setRetailer_grade(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_grade")));
                        visitedRetailerItem.setRetailerLocality(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("locality")));
                        visitedRetailerItem.setRetailer_image(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_image")));
                        visitedRetailerItem.setRetailer_pin(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_pin")));
                        visitedRetailerItem.setLatitude(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_latitude")));
                        visitedRetailerItem.setLongtitude(visitedRetailerCursor.getString(visitedRetailerCursor.getColumnIndex("retailer_longtitude")));

                        for (int i = 0; i < orderPlacedByList.size(); i++) {

                            if (orderPlacedByList.get(i).equalsIgnoreCase(rid)) {

                                Cursor orderType = salesBeatDb.getOrderTypeFromOderPlacedByRetailersTable(orderPlacedByList.get(i));
                                orderType.moveToFirst();
                                visitedRetailerItem.setOrderType(orderType.getString(orderType.getColumnIndex("order_type")));
                                visitedRetailerItem.setTimeStamp(orderType.getString(orderType.getColumnIndex("taken_at")));
                                visitedRetailerItem.setServerStatus(orderType.getString(orderType.getColumnIndex("order_status")));
                                visitedRetailerList.add(visitedRetailerItem);
                            }
                        }

                    } while (visitedRetailerCursor.moveToNext());
                }

                //to remove duplicate value from list
                Set<RetailerItem> hs2 = new LinkedHashSet<>();
                hs2.addAll(visitedRetailerList);
                visitedRetailerList.clear();
                visitedRetailerList.addAll(hs2);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (visitedRetailerCursor != null)
                visitedRetailerCursor.close();
        }

        adapter = new VisitedRetailerListAdapter(getContext(), visitedRetailerList, orderPlacedForDistributorList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rcvRetailerList.setLayoutManager(layoutManager);
        //rcvRetailerList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        rcvRetailerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void animateSearchToolbar(int numberOfMenuIcon, boolean containsOverflow, boolean show) {

        mToolbar.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
        //mDrawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.quantum_grey_600));

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
                        //mDrawerLayout.setStatusBarBackgroundColor(getThemeColor(OrderBookingRetailing.this, R.attr.colorPrimaryDark));
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
            // mDrawerLayout.setStatusBarBackgroundColor(ThemeUtils.getThemeColor(OrderBookingRetailing.this, R.attr.colorPrimaryDark));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private boolean isRtl(Resources resources) {
        return resources.getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
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
            //progressDialog.dismiss();
            Intent retIntent = new Intent(getContext(), RetailerActivity.class);
            startActivity(retIntent);
            getActivity().finish();
        }
    }
}


