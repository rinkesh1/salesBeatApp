package com.newsalesbeatApp.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.OrderConfirmationDialogAdapter;
import com.newsalesbeatApp.adapters.SkuListAdapter;
import com.newsalesbeatApp.pojo.MyProduct;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


/*
 * Created by MTC on 03-08-2017.
 */

public class SkusFragment extends Fragment {

    ArrayList<MyProduct> myProductArrayList = new ArrayList<>();
    ArrayList<MyProduct> myProductArrayList2 = new ArrayList<>();
    ArrayList<String> skuIdArray = new ArrayList<>();
    ShimmerRecyclerView rvSkuList;
    UtilityClass utilityClass;
    SkuListAdapter adapter;
    SharedPreferences tempPref;
    LinearLayout llOrderConfirmation;
    TextView tvStc, tvSkuErrormsg;
    int count = 0;
    String from = "";
    List<String> brandList = new ArrayList<>();
    List<String> unitList = new ArrayList<>(); //@Umesh 20220930
    GPSLocation locationProvider;
    ImageView imgBack, imgFilterBy;
    LinearLayoutManager layoutManager;
    SalesBeatDb salesBeatDb;
    private String TAG = "SkusFragment";

//    public static Button  one, two, three, four, five, six, seven, eight, nine, next, del,zero;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle bundle) {
        View view = inflater.inflate(R.layout.sku_list, parent, false);
        //prefSFA = getContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = requireContext().getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        imgBack = view.findViewById(R.id.imgBack);
        imgFilterBy = view.findViewById(R.id.imgFilterBy);
        TextView tvPageTitle = view.findViewById(R.id.pageTitle);
        rvSkuList = view.findViewById(R.id.rvSkuList);
        llOrderConfirmation = view.findViewById(R.id.llOrderConfirmation);
        tvStc = view.findViewById(R.id.tvStc);
        tvSkuErrormsg = view.findViewById(R.id.tvSkuErrormsg);
//        one = view.findViewById(R.id.one);
//        two = view.findViewById(R.id.two);
//        three = view.findViewById(R.id.three);
//        four = view.findViewById(R.id.four);
//        five = view.findViewById(R.id.five);
//        six = view.findViewById(R.id.six);
//        seven = view.findViewById(R.id.seven);
//        eight = view.findViewById(R.id.eight);
//        nine = view.findViewById(R.id.nine);
//        next = view.findViewById(R.id.buttonnext);
//        del = view.findViewById(R.id.buttondel);
//        zero= view.findViewById(R.id.buttonzero);

        utilityClass = new UtilityClass(requireContext());
        //salesBeatDb = new SalesBeatDb(getContext());
        salesBeatDb = SalesBeatDb.getHelper(requireContext());
        locationProvider = new GPSLocation(requireContext());

        if (getArguments() != null)
            from = getArguments().getString("from");

        if (from != null && from.equalsIgnoreCase("stock")) {

            tvPageTitle.setText(getString(R.string.stockkeeping));
            tvStc.setText("Stock Confirmation");

        } else if (from != null && from.equalsIgnoreCase("order")) {

            tvPageTitle.setText("Distributor Order");
            tvStc.setText("Order Confirmation");

        } else if (from != null && from.equalsIgnoreCase("closing")) {

            tvPageTitle.setText("Monthly Closing");
            tvStc.setText("Closing Confirmation");
        }

        //hide keyboard if open
        closeKeyboard(getActivity(), tvPageTitle.getWindowToken());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (from.equalsIgnoreCase("closing")){
//
//
//        } else {
//
//
//
//        }

        new MyAsynchTask2().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (from.equalsIgnoreCase("closing")) {

//                    SharedPreferences.Editor editor = tempPref.edit();
//                    editor.putString()

                    FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
                    //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                    Fragment fragment = new DistributorList2();
                    fragTransaction.replace(R.id.frmClosing, fragment);
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();

                } else {

                    FragmentManager fragManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragTransaction = fragManager.beginTransaction();
                    //fragTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slide_to_right);
                    Fragment fragment = new DistributorList();
                    fragTransaction.replace(R.id.flContainer, fragment);
                    fragTransaction.addToBackStack(null);
                    fragTransaction.commit();
                }


            }
        });


        rvSkuList.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                initializeList();
            }
        });


        llOrderConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adapter.getItemCount() > 0) {

                    final Dialog dialog = new Dialog(requireContext(), R.style.DialogActivityTheme);

                    if (dialog.getWindow() != null)
                        dialog.getWindow().setGravity(Gravity.BOTTOM);

                    dialog.setContentView(R.layout.order_confirmation_dialog);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    final RecyclerView orderConfirmRV = dialog.findViewById(R.id.rvSkuListDialog);
                    TextView tvTotalWeightInKg = dialog.findViewById(R.id.tvTotalWieghtInKg);
                    TextView tvTotalBox = dialog.findViewById(R.id.tvTotalBox);
                    TextView tvTotalPcs = dialog.findViewById(R.id.tvTotalPcs);
                    TextView tvTotalUnit = dialog.findViewById(R.id.tvTotalUnit);
                    LinearLayout llConfirm = dialog.findViewById(R.id.llConfirm);

                    for (int position = 0; position < myProductArrayList.size(); position++) {

                        RecyclerView.ViewHolder holder = rvSkuList.findViewHolderForAdapterPosition(position);

                        if (holder != null) {

                            EditText edtQuantity = holder.itemView.findViewById(R.id.edtQuantity);
                            Log.e(TAG, "===>>" + position + "===" + edtQuantity.getText());

                            if (!edtQuantity.getText().toString().isEmpty()) {

                                MyProduct myProduct = new MyProduct();
                                myProduct.setProductId(myProductArrayList.get(position).getProductId());
                                myProduct.setMySkus(myProductArrayList.get(position).getMySkus());
                                myProduct.setBrand(myProductArrayList.get(position).getBrand());
                                myProduct.setPrice(myProductArrayList.get(position).getPrice());
                                myProduct.setUnit(myProductArrayList.get(position).getUnit());
                                myProduct.setQuantity(edtQuantity.getText().toString());

                                myProductArrayList2.set(position, myProduct);

                            } else {

                                myProductArrayList2.set(position, null);

                            }
                        }
                    }

                    final ArrayList<MyProduct> myProducts = new ArrayList<>();

                    for (int pos = 0; pos < myProductArrayList2.size(); pos++) {

                        if (myProductArrayList2.get(pos) != null)
                            myProducts.add(myProductArrayList2.get(pos));

                    }

                    final OrderConfirmationDialogAdapter adapter2 = new OrderConfirmationDialogAdapter(getContext(), myProducts);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    orderConfirmRV.setLayoutManager(layoutManager);
                    //orderConfirmRV.addItemDecoration(new SimpleDividerItemDecoration(OrderConfirmation.this));
                    orderConfirmRV.setAdapter(adapter2);

                    int kg = 0, box = 0, pcs = 0, total = 0;

                    //@Umesh 30-05-2022 LowerCase
                    for (int ind = 0; ind < myProducts.size(); ind++) {

                        if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("kg")) {

                            kg = kg + Integer.parseInt(myProducts.get(ind).getQuantity());

                        } else if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("box")) {

                            box = box + Integer.parseInt(myProducts.get(ind).getQuantity());

                        } else if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("pc")) {

                            pcs = pcs + Integer.parseInt(myProducts.get(ind).getQuantity());

                        }

                    }

                    if (kg != 0)
                        tvTotalWeightInKg.setText(String.valueOf(kg) + "Kg");
                    if (box != 0)
                        tvTotalBox.setText(String.valueOf(box) + "Bag");
                    if (pcs != 0)
                        tvTotalPcs.setText(String.valueOf(pcs) + "Pcs");

                    total = kg + box + pcs;

                    if (total != 0)
                        tvTotalUnit.setText(String.valueOf(total) + " Unit");


                    if (adapter2.getItemCount() > 0) {

                        llConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                new MyAsynchTask(orderConfirmRV, myProducts).execute();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                    } else {

                        Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        imgFilterBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showFilterDailog();
            }
        });

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

//                    List<String> unitList = new ArrayList<>();
//                    unitList.add("Bag");
//                    unitList.add("Kg");
//                    unitList.add("Pcs");

                    //@Umesh 20220930
                    //to remove duplicate value from list
                    Set<String> hs = new LinkedHashSet<>();
                    hs.addAll(unitList);
                    unitList.clear();
                    unitList.addAll(hs);


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
                else //@Umesh 20220930
                {
                    Cursor cursor = salesBeatDb.getAllDataFromSkuDetailsTable();
                    initializeSkuList(cursor);
                }

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void initializeList() {

        for (int position = 0; position < myProductArrayList.size(); position++) {

            RecyclerView.ViewHolder holder = rvSkuList.findViewHolderForAdapterPosition(position);

            if (holder != null) {

                EditText edtQuantity = holder.itemView.findViewById(R.id.edtQuantity);

                if (!edtQuantity.getText().toString().isEmpty()) {


                    MyProduct myProduct = new MyProduct();
                    myProduct.setProductId(myProductArrayList.get(position).getProductId());
                    myProduct.setMySkus(myProductArrayList.get(position).getMySkus());
                    myProduct.setBrand(myProductArrayList.get(position).getBrand());
                    myProduct.setPrice(myProductArrayList.get(position).getPrice());
                    myProduct.setUnit(myProductArrayList.get(position).getUnit());
                    myProduct.setQuantity(edtQuantity.getText().toString());

                    myProductArrayList2.set(position, myProduct);

                } else {

                    myProductArrayList2.set(position, null);

                }
            }
        }
    }

    public void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr != null)
            mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    private void initializeSkuList(Cursor cursor) {

        myProductArrayList.clear();

        try {

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    String sku_id = cursor.getString(cursor.getColumnIndex("sku_id"));
                    MyProduct myProduct = new MyProduct();

                    for (int i = 0; i < skuIdArray.size(); i++) {

                        if (skuIdArray.get(i).equalsIgnoreCase(sku_id)) {

                            myProduct.setProductId(cursor.getString(cursor.getColumnIndex("sku_id")));
                            myProduct.setMySkus(cursor.getString(cursor.getColumnIndex("sku_name")));
                            myProduct.setBrand(cursor.getString(cursor.getColumnIndex("brand_name")));
                            myProduct.setPrice(cursor.getString(cursor.getColumnIndex("brand_price")));
                            myProduct.setUnit(cursor.getString(cursor.getColumnIndex("brand_unit")));
                            brandList.add(cursor.getString(cursor.getColumnIndex("brand_name")));
                            unitList.add(cursor.getString(cursor.getColumnIndex("brand_unit")));//@Umesh 20220930
                            myProductArrayList.add(myProduct);
                        }


                    }

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        if (myProductArrayList.size() > 0) {
            Log.d(TAG, "initializeSkuList lenght: "+myProductArrayList.size());
            adapter = new SkuListAdapter(requireContext(), myProductArrayList, from);
            layoutManager = new LinearLayoutManager(getContext());
            rvSkuList.setLayoutManager(layoutManager);
            int resId = R.anim.layout_animation_fall_down;
            LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
            rvSkuList.setLayoutAnimation(animation);
            rvSkuList.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            for (int temp = 0; temp < myProductArrayList.size(); temp++) {
                myProductArrayList2.add(temp, null);
            }

        } else {

            tvSkuErrormsg.setText(tempPref.getString(getString(R.string.skuErrorKey), ""));
            tvSkuErrormsg.setVisibility(View.VISIBLE);
            rvSkuList.setVisibility(View.GONE);
        }


    }

    @SuppressLint("StaticFieldLeak")
    private class MyAsynchTask extends AsyncTask<Void, Void, Void> {

        RecyclerView myRecycleView;
        ArrayList<MyProduct> myProducts;
        String takenTime = "";
        String takenDate = "";
        String type = "";

        private MyAsynchTask(RecyclerView rvSkuList, ArrayList<MyProduct> myProducts) {

            myRecycleView = rvSkuList;
            this.myProducts = myProducts;
        }

        protected void onPreExecute() {

            try {

                Calendar cal = Calendar.getInstance();
                takenTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss ").format(cal.getTime());
                takenDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                if (from != null && from.equalsIgnoreCase("stock")) {

                    //to check stock exist or not
                    Cursor stockAvailable = salesBeatDb.getAllDataFromSkuEntryListTable2
                            (tempPref.getString(getString(R.string.dis_id_key), ""));

                    if (stockAvailable != null && stockAvailable.getCount() > 0)
                        type = "revise";
                    else
                        type = "stock";

                } else if (from != null && from.equalsIgnoreCase("closing")) {

                    //to check stock exist or not
                    Cursor closingAvailable = salesBeatDb.getAllDataFromClosingEntryListTable2
                            (tempPref.getString(getString(R.string.dis_id_key), ""));

                    if (closingAvailable != null && closingAvailable.getCount() > 0)
                        type = "revise";
                    else
                        type = "stock";


                } else if (from != null && from.equalsIgnoreCase("order")) {

                    //to check stock exist or not
                    Cursor disOrderAvailable = salesBeatDb.getSpecificDataFromDisOrderEntryListTable2
                            (tempPref.getString(getString(R.string.dis_id_key), ""));

                    if (disOrderAvailable != null && disOrderAvailable.getCount() > 0)
                        type = "revise";
                    else
                        type = "order";

                }

                salesBeatDb.deleteAllFromDistributorOrderTable(tempPref.getString(getString(R.string.dis_id_key), ""), from);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.e(TAG, " Date: " + takenDate);

            salesBeatDb.insertInDistributorOrderTable(tempPref.getString(getString(R.string.dis_id_key), ""),
                    takenTime, "fail", takenDate, from, locationProvider.getLatitudeStr(),
                    locationProvider.getLongitudeStr());

            for (int position = 0; position < myProducts.size(); position++) {

                salesBeatDb.insertSkuEntryListTable(myProducts.get(position).getProductId(),
                        myProducts.get(position).getMySkus(), myProducts.get(position).getBrand(),
                        myProducts.get(position).getPrice(), myProducts.get(position).getQuantity(),
                        myProducts.get(position).getUnit(), takenTime, tempPref.getString(getString(R.string.dis_id_key), ""),
                        takenDate, "fail", from, locationProvider.getLatitudeStr(),
                        locationProvider.getLongitudeStr(), type);

            }

            return null;
        }

        protected void onPostExecute(Void v) {

            Bundle bundle1 = new Bundle();
            if (from.equalsIgnoreCase("stock"))
                bundle1.putInt("value", 1);
            else if (from.equalsIgnoreCase("order"))
                bundle1.putInt("value", 3);
            else if (from.equalsIgnoreCase("closing"))
                bundle1.putInt("value", 4);

            FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            Fragment fragment = new ConfirmationAnimationFragment();
            fragment.setArguments(bundle1);
            if (from.equalsIgnoreCase("closing"))
                ft.replace(R.id.frmClosing, fragment);
            else
                ft.replace(R.id.flContainer, fragment);
            ft.commit();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyAsynchTask2 extends AsyncTask<Void, Void, Void> {

        Cursor cursor1 = null, cursor = null;

        protected void onPreExecute() {

            skuIdArray.clear();

            try {

                //cursor1 = salesBeatDb.getAllDataFromSkuIdTable(tempPref.getString(getString(R.string.dis_id_key), ""));
                cursor1 = salesBeatDb.getDisSkuMap(tempPref.getString(getString(R.string.dis_id_key), ""));
                Log.e("DIS MApping", "--->" + cursor1.getCount());
                if (cursor1 != null && cursor1.getCount() > 0 && cursor1.moveToFirst()) {
                    do {
                        //skuIdArray.add(cursor1.getString(cursor1.getColumnIndex("sku_id")));
                        skuIdArray.add(cursor1.getString(cursor1.getColumnIndex(SalesBeatDb.KEY_SID)));
                    } while (cursor1.moveToNext());
                }

            } catch (Exception e) {
                e.getMessage();
            } finally {
                if (cursor1 != null)
                    cursor1.close();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        protected void onPostExecute(Void v) {

            cursor = salesBeatDb.getAllDataFromSkuDetailsTable();
            Log.e("DIS DATA ", "--->" + cursor1.getCount());
            initializeSkuList(cursor);
        }
    }

}
