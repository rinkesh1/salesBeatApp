package com.newsalesbeatApp.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.RetailerOrderActivity;
import com.newsalesbeatApp.adapters.RecyclerSectionItemDecoration;
import com.newsalesbeatApp.adapters.RetailerOrderListAdapter;
import com.newsalesbeatApp.pojo.MyProduct;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * Created by MTC on 15-10-2017.
 */

public class RetailerOrderFragment extends Fragment{

    //private ArrayList<MyProduct> myProductArrayList2 = new ArrayList<>();
    public ArrayList<MyProduct> orderList;
    public ShimmerRecyclerView rvOrderList;
    public String did;
    SalesBeatDb salesBeatDb;
    //SharedPreferences prefSFA/*, tempPref*/;
    private RetailerOrderListAdapter adapter;
    private Cursor cursorSkuIdTable, cursorSkuIdDetails;
    private String rid, orderType, from;

    private int count = 0;
    private List<String> brandList = new ArrayList<>();

    private FloatingActionButton filterSku;

    @SuppressLint("RestrictedApi")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.order_fragment, parent, false);
        rvOrderList = view.findViewById(R.id.rvOrderList);
        filterSku = view.findViewById(R.id.filterSku);

        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(getContext());

        did = getArguments().getString("did");
        rid = getArguments().getString("rid");
        orderType = getArguments().getString("orderType");
        from = getArguments().getString("from");

        filterSku.setVisibility(View.GONE);

        filterSku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDailog();
            }
        });

        RecyclerSectionItemDecoration sectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.recycler_section_header_height),
                        true, // true for sticky, false for not
                        new RecyclerSectionItemDecoration.SectionCallback() {
                            @Override
                            public boolean isSection(int position) {
                                return position == 0
                                        || !orderList.get(position-1).getBrand().equals(orderList.get(position).getBrand());
                            }

                            @Override
                            public CharSequence getSectionHeader(int position) {
                                return orderList.get(position).getBrand();
                            }
                        });
        rvOrderList.addItemDecoration(sectionItemDecoration);

        return view;
    }


    private void showFilterDailog() {

        final Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.sku_filter_layout);

        final AutoCompleteTextView actvFilterBy = dialog.findViewById(R.id.actvFilterBy);
        final AutoCompleteTextView actvFilterWith = dialog.findViewById(R.id.actvFilterWith);
        ImageView imgDropDownFilter = dialog.findViewById(R.id.imgDropDownFilter);
        ImageView imgDropDownFilterWith = dialog.findViewById(R.id.imgDropDownFilterWith);
        Button btnApply = dialog.findViewById(R.id.btnApply);

        List<String> filterByList = new ArrayList<>();
        filterByList.add("Price");
        filterByList.add("Brand");
        filterByList.add("Unit");

        ArrayAdapter adapter = new ArrayAdapter(getContext(),
                android.R.layout.simple_spinner_dropdown_item, filterByList);
        actvFilterBy.setAdapter(adapter);

        actvFilterBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFilterBy.showDropDown();
            }
        });

        actvFilterBy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                count = i;
                if (i == 0) {

                    List<String> priceList = new ArrayList<>();
                    priceList.add("< 500");
                    priceList.add("> 500");
                    priceList.add("= 500");


                    ArrayAdapter adapter2 = new ArrayAdapter(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, priceList);

                    actvFilterWith.setAdapter(adapter2);

                } else if (i == 1) {

                    //to remove duplicate value from list
                    Set<String> hs = new LinkedHashSet<>();
                    hs.addAll(brandList);
                    brandList.clear();
                    brandList.addAll(hs);

                    ArrayAdapter adapter2 = new ArrayAdapter(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, brandList);

                    actvFilterWith.setAdapter(adapter2);

                } else if (i == 2) {

                    List<String> unitList = new ArrayList<>();
                    unitList.add("Bag");
                    unitList.add("Kg");
                    unitList.add("Pcs");

                    ArrayAdapter adapter2 = new ArrayAdapter(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, unitList);

                    actvFilterWith.setAdapter(adapter2);
                }
            }
        });

        imgDropDownFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFilterBy.showDropDown();
            }
        });


        actvFilterWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFilterWith.showDropDown();
            }
        });

        imgDropDownFilterWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actvFilterWith.showDropDown();
            }
        });


        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filetrWith = actvFilterWith.getText().toString();
                if (!filetrWith.isEmpty()) {
                    if (count == 0) {

                        String[] temp = filetrWith.split(" ");
                        String op = temp[0];
                        String fltBy = temp[1];

                        Cursor cursor = salesBeatDb.getDataPriceFromSkuDetailsTable(op, fltBy);
                        initializeSkuList(cursor);

                    } else if (count == 1) {

                        Cursor cursor = salesBeatDb.getDataBrandFromSkuDetailsTable(filetrWith);
                        initializeSkuList(cursor);

                    } else if (count == 2) {

                        Cursor cursor = salesBeatDb.getDataByUnitFromSkuDetailsTable(filetrWith);
                        initializeSkuList(cursor);
                    }

                }

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void initializeSkuList(Cursor paramCursor) {

        ArrayList<MyProduct> myProductArrayList = new ArrayList<>();
        ArrayList<String> skuIdArray = new ArrayList<>();

        //cursorSkuIdTable = salesBeatDb.getAllDataFromSkuIdTable(did);

        cursorSkuIdTable = salesBeatDb.getDisSkuMap(did);
        try {

            if (cursorSkuIdTable != null && cursorSkuIdTable.getCount() > 0 && cursorSkuIdTable.moveToFirst()) {

                skuIdArray.clear();
                do {
                    //skuIdArray.add(cursorSkuIdTable.getString(cursorSkuIdTable.getColumnIndex("sku_id")));
                    skuIdArray.add(cursorSkuIdTable.getString(cursorSkuIdTable.getColumnIndex(SalesBeatDb.KEY_SID)));
                } while (cursorSkuIdTable.moveToNext());
            }

        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (cursorSkuIdTable != null)
                cursorSkuIdTable.close();
        }

        cursorSkuIdDetails = paramCursor;//salesBeatDb.getAllDataFromSkuDetailsTable();

        try {

            if (cursorSkuIdDetails != null && cursorSkuIdDetails.getCount() > 0 && cursorSkuIdDetails.moveToFirst()) {
                myProductArrayList.clear();
                do {

                    String sku_id = cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_id"));
                    MyProduct myProduct = new MyProduct();

                    for (int i = 0; i < skuIdArray.size(); i++) {

                        if (skuIdArray.get(i).equalsIgnoreCase(sku_id)) {

                            String productId = cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_id"));
                            myProduct.setProductId(productId);

                            Cursor cursor = null, cursor2 = null;

                            try {
                                Log.d("TAG", "initializeSkuList: "+productId);
                                Log.e("New Testing", "###### orderType:"
                                        + RetailerOrderActivity.orderType + " From:"
                                        + from + " Product id:" + productId + " Rid:" + rid + " DID:" + did);

                                if (!RetailerOrderActivity.orderType.equalsIgnoreCase("onNewShop")) {

                                    if (from.equalsIgnoreCase(getString(R.string.reqNOrdered))
                                            || from.equalsIgnoreCase(getString(R.string.reqNoOrdered))) {

                                        cursor2 = salesBeatDb.getSpecificDataFromNewOrderEntryListTable2(rid, did, productId);

                                        Log.e("New Testing1", "  --->" + cursor2.getCount());

                                        if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {

                                            myProduct.setQuantity(cursor2.getString(cursor2.getColumnIndex("new_brand_qty")));
                                        }

                                    } else if (from.equalsIgnoreCase(getString(R.string.preferredRetOrder))
                                            || from.equalsIgnoreCase(getString(R.string.preferredRetNoOrder))) {

                                        cursor2 = salesBeatDb.getSpecificDataFromPreferredOrderEntryListTable2(rid, did, productId);

                                        Log.e("New Testing2", "  --->" + cursor2.getCount());

                                        if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {

                                            myProduct.setQuantity(cursor2.getString(cursor2.getColumnIndex("new_brand_qty")));
                                        }

                                    } else {

                                        cursor = salesBeatDb.getSpecificDataFromOrderEntryListTable2(did, rid, productId);

                                        Log.e("New Testing3", "  --->" + cursor.getCount());

                                        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                                            myProduct.setQuantity(cursor.getString(cursor.getColumnIndex("brand_qty")));
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("RetOrderGrag", " : " + e.getMessage());
                            } finally {

                                if (cursor != null)
                                    cursor.close();
                            }

                            myProduct.setMySkus(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_name")));
                            myProduct.setBrand(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_name")));
                            brandList.add(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_name")));
                            myProduct.setPrice(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_price")));
                            myProduct.setWeight(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_weight")));
                            myProduct.setUnit(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_unit")));
                            myProduct.setConversion(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("conversion_factor")));
                            myProduct.setImageStr(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_image")));
                            myProductArrayList.add(myProduct);
                        }
                    }

                } while (cursorSkuIdDetails.moveToNext());

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursorSkuIdDetails != null)
                cursorSkuIdDetails.close();
        }

        //myProductArrayList;

        adapter = new RetailerOrderListAdapter(getContext(), did, rid, myProductArrayList, from);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvOrderList.setLayoutManager(layoutManager);
        //rvOrderList.addItemDecoration(new SimpleDividerItemDecoration(this));
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvOrderList.setLayoutAnimation(animation);
        rvOrderList.setAdapter(adapter);
        rvOrderList.setItemViewCacheSize(adapter.getItemCount());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderList = getOrderList();
        Log.d("TAG", "check Oder List: "+new Gson().toJson(orderList));
        Log.d("TAG", "check Oder List: "+orderList.size());
        adapter = new RetailerOrderListAdapter(getContext(), did, rid, orderList, from);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvOrderList.setLayoutManager(layoutManager);
        //rvOrderList.addItemDecoration(new SimpleDividerItemDecoration(this));
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        rvOrderList.setLayoutAnimation(animation);
        rvOrderList.setAdapter(adapter);
        rvOrderList.setItemViewCacheSize(adapter.getItemCount());

//        for (int temp = 0; temp < orderList.size(); temp++) {
//
//            myProductArrayList2.add(temp, null);
//        }

//        rvOrderList.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                initializeFinalOrderList(orderList);
//            }
//        });

        //new ScrollTask().execute();
    }

//    private void initializeFinalOrderList(ArrayList<MyProduct> orderList) {
//
//        for (int position = 0; position < orderList.size(); position++) {
//
//            RecyclerView.ViewHolder holder = rvOrderList.findViewHolderForAdapterPosition(position);
//            if (holder != null) {
//
//                EditText actvQuantity = holder.itemView.findViewById(R.id.edtOrderQuantity);
//                if (!actvQuantity.getText().toString().isEmpty()) {
//
//                    MyProduct myProduct = new MyProduct();
//                    myProduct.setProductId(orderList.get(position).getProductId());
//                    myProduct.setMySkus(orderList.get(position).getMySkus());
//                    myProduct.setBrand(orderList.get(position).getBrand());
//                    myProduct.setPrice(orderList.get(position).getPrice());
//                    myProduct.setUnit(orderList.get(position).getUnit());
//                    myProduct.setQuantity(actvQuantity.getText().toString());
//                    myProduct.setConversion(orderList.get(position).getConversion());
//
//                    myProductArrayList2.set(position, myProduct);
//
//                } else {
//
//                    myProductArrayList2.set(position, null);
//
//                }
//            }
//        }
//
//    }

    private ArrayList<MyProduct> getOrderList() {

        ArrayList<MyProduct> myProductArrayList = new ArrayList<>();
        ArrayList<String> skuIdArray = new ArrayList<>();

        //cursorSkuIdTable = salesBeatDb.getAllDataFromSkuIdTable(did);

        cursorSkuIdTable = salesBeatDb.getDisSkuMap(did);
        try {

            if (cursorSkuIdTable != null && cursorSkuIdTable.getCount() > 0 && cursorSkuIdTable.moveToFirst()) {

                skuIdArray.clear();
                do {
                    //skuIdArray.add(cursorSkuIdTable.getString(cursorSkuIdTable.getColumnIndex("sku_id")));
                    skuIdArray.add(cursorSkuIdTable.getString(cursorSkuIdTable.getColumnIndex(SalesBeatDb.KEY_SID)));
                } while (cursorSkuIdTable.moveToNext());
            }

        } catch (Exception e) {
            e.getMessage();
        } finally {

            if (cursorSkuIdTable != null)
                cursorSkuIdTable.close();
        }

        cursorSkuIdDetails = salesBeatDb.getAllDataFromSkuDetailsTable();

        try {

            if (cursorSkuIdDetails != null && cursorSkuIdDetails.getCount() > 0 && cursorSkuIdDetails.moveToFirst()) {
                myProductArrayList.clear();
                do {

                    String sku_id = cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_id"));
                    MyProduct myProduct = new MyProduct();

                    for (int i = 0; i < skuIdArray.size(); i++) {

                        if (skuIdArray.get(i).equalsIgnoreCase(sku_id)) {

                            String productId = cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_id"));
                            myProduct.setProductId(productId);

                            Cursor cursor = null, cursor2 = null;

                            try {
                                Log.d("TAG", "initializeSkuList-1: "+productId);
                                Log.e("New Testing", "###### orderType:"
                                        + RetailerOrderActivity.orderType + " From:"
                                        + from + " Product id:" + productId + " Rid:" + rid + " DID:" + did);

                                if (!RetailerOrderActivity.orderType.equalsIgnoreCase("onNewShop")) {

                                    if (from.equalsIgnoreCase("Requested and Ordered")
                                            || from.equalsIgnoreCase("Requested without order")) {

                                        cursor2 = salesBeatDb.getSpecificDataFromNewOrderEntryListTable2(rid, did, productId);

                                        Log.e("New Testing1", "  --->" + cursor2.getCount());

                                        if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {

                                            myProduct.setQuantity(cursor2.getString(cursor2.getColumnIndex("new_brand_qty")));
                                        }

                                    } else if (from.equalsIgnoreCase(getString(R.string.preferredRetOrder))
                                            || from.equalsIgnoreCase(getString(R.string.preferredRetNoOrder))) {

                                        cursor2 = salesBeatDb.getSpecificDataFromPreferredOrderEntryListTable2(rid, did, productId);

                                        Log.e("New Testing2", "  --->" + cursor2.getCount());

                                        if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {

                                            myProduct.setQuantity(cursor2.getString(cursor2.getColumnIndex("new_brand_qty")));
                                        }

                                    } else {

                                        cursor = salesBeatDb.getSpecificDataFromOrderEntryListTable2(did, rid, productId);

                                        Log.e("New Testing3", "  --->" + cursor.getCount());

                                        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                                            myProduct.setQuantity(cursor.getString(cursor.getColumnIndex("brand_qty")));
                                        }
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("RetOrderGrag", " : " + e.getMessage());
                            } finally {

                                if (cursor != null)
                                    cursor.close();
                            }

                            myProduct.setMySkus(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_name")));
                            myProduct.setBrand(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_name")));
                            brandList.add(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_name")));
                            myProduct.setPrice(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_price")));
                            myProduct.setWeight(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_weight")));
                            myProduct.setUnit(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("brand_unit")));
                            myProduct.setConversion(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("conversion_factor")));
                            myProduct.setImageStr(cursorSkuIdDetails.getString(cursorSkuIdDetails.getColumnIndex("sku_image")));
                            myProductArrayList.add(myProduct);
                        }
                    }

                } while (cursorSkuIdDetails.moveToNext());

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursorSkuIdDetails != null)
                cursorSkuIdDetails.close();
        }

        return myProductArrayList;
    }
}
