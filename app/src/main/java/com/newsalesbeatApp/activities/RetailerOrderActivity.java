package com.newsalesbeatApp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.adapters.RetailerFinalOrderListAdapter;
import com.newsalesbeatApp.adapters.RetailerOrderListAdapter;
import com.newsalesbeatApp.fragments.RetailerOrderFragment;
import com.newsalesbeatApp.pojo.MyProduct;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
 * Created by MTC on 16-08-2017.
 */

public class RetailerOrderActivity extends AppCompatActivity {

    public static String orderType = "";
    public int brandQty = 0;
    public int brandQtyPC = 0;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    String TAG = "RetailerOrderActivity";
    UtilityClass utilityClass;
    SharedPreferences prefSFA, tempPref;
    TabLayout tabDistributor;
    String rid = "", nrid = "", checkInTime = "", from = "", retailerName = "";
    private final List<String> mFragmentTitleList = new ArrayList<>();
    int tabPosition;
    LinearLayout llOrderConfirmation;
    Bundle newRet;
    GPSLocation locationProvider;
    SalesBeatDb salesBeatDb;

    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_confirmation);
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        tempPref = getSharedPreferences(getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
        tabDistributor = findViewById(R.id.tabDistributor);
        llOrderConfirmation = findViewById(R.id.llOrderConfirmation);

        Toolbar mToolbar = findViewById(R.id.toolbar2);
        ImageView imgBack = mToolbar.findViewById(R.id.imgBack);
        final TextView userImage = mToolbar.findViewById(R.id.userPic);
        TextView tvPageTitle = mToolbar.findViewById(R.id.pageTitle);

        utilityClass = new UtilityClass(this);
        locationProvider = new GPSLocation(this);
        //salesBeatDb = new SalesBeatDb(this);
        salesBeatDb = SalesBeatDb.getHelper(this);

        //check gps status if on/off
        locationProvider.checkGpsStatus();

        rid = getIntent().getStringExtra("rid");
        nrid = getIntent().getStringExtra("nrid");
        from = getIntent().getStringExtra("if");
        orderType = getIntent().getStringExtra("orderType");
        checkInTime = getIntent().getStringExtra("checkInTime");
        tabPosition = getIntent().getIntExtra("tabPosition", 0);
        retailerName = getIntent().getStringExtra("retName");

        tvPageTitle.setText(retailerName);
        userImage.setText(retailerName);


        try {

            newRet = getIntent().getBundleExtra("newRet");

            //assert newRet != null;
            String[] prm = new String[0];
            if (newRet != null) {
                prm = newRet.getStringArray("params");
            }
            tvPageTitle.setText(prm[0]);
            userImage.setText(prm[0]);

        } catch (Exception e) {
            e.getMessage();
        }


        //initilize viewpager with tab
        tabDistributor.setSelectedTabIndicatorColor(Color.WHITE);
        tabDistributor.setSelectedTabIndicatorHeight(0);
        final ViewPager distributorViewPager = (ViewPager) findViewById(R.id.distributorPager);
        setupViewPager(distributorViewPager);
        tabDistributor.setupWithViewPager(distributorViewPager);


        imgBack.setOnClickListener(view -> {

            Intent intent = null;
            //locationProvider.unregisterReceiver();

            if (orderType.equalsIgnoreCase("onNewShop")) {

                intent = new Intent(RetailerOrderActivity.this, AddNewRetailerActivity.class);
                intent.putExtra("from", "order");
                intent.putExtra("newRet", newRet);
                startActivity(intent);
                RetailerOrderActivity.this.finish();
                //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            } else {
                RetailerOrderActivity.this.finish();
            }


        });

        llOrderConfirmation.setOnClickListener(view -> showFinalOrderList());

    }


    public void onDestroy() {
        System.gc();
        super.onDestroy();
    }

    private void showFinalOrderList() {
        Log.d(TAG, "showFinalOrderList");
        final ArrayList<String> distributorIdList = new ArrayList<>();
        final ArrayList<String> distributorNameList = new ArrayList<>();
        final ArrayList<ArrayList<MyProduct>> finalOrderList = new ArrayList<>();

        for (int i = 0; i < mFragmentList.size(); i++) {

            final RetailerOrderFragment retailerOrderFragment = (RetailerOrderFragment) mFragmentList.get(i);

            ArrayList<MyProduct> myProducts = new ArrayList<>();

            if (retailerOrderFragment != null && retailerOrderFragment.rvOrderList.getAdapter()
                    != null && retailerOrderFragment.rvOrderList.getAdapter().getItemCount() > 0) {

                for (int position = 0; position < retailerOrderFragment.orderList.size(); position++)
                {


                    RetailerOrderListAdapter retailerOrderListAdapter = (RetailerOrderListAdapter)
                            retailerOrderFragment.rvOrderList.getAdapter();

                    myProducts = retailerOrderListAdapter.myProductArrayList;

                }
            }

            if (retailerOrderFragment != null /*&& myProducts.size() > 0*/) {

                ArrayList<MyProduct> filteredProductList = new ArrayList<>();
                for (int j = 0; j < myProducts.size(); j++) {

                    if (myProducts.get(j).getQuantity() != null && !myProducts.get(j).getQuantity().isEmpty())
                        filteredProductList.add(myProducts.get(j));

                }

                if (filteredProductList.size() > 0) {

                    finalOrderList.add(filteredProductList);
                    distributorIdList.add(retailerOrderFragment.did);
                    distributorNameList.add(mFragmentTitleList.get(i));
                }

            }
        }

        if (finalOrderList.size() > 0)
            saveFinalOderListToDatabase(distributorNameList, distributorIdList, finalOrderList);
    }

    private void saveFinalOderListToDatabase(ArrayList<String> distributorNameList,
                                             final ArrayList<String> distributorIdList,
                                             final ArrayList<ArrayList<MyProduct>> finalOrderList) {

        //initialize final order list dialog
        final Dialog dialog = new Dialog(RetailerOrderActivity.this, R.style.DialogActivityTheme);

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
        final LinearLayout llConfirm = dialog.findViewById(R.id.llConfirm);
        final RetailerFinalOrderListAdapter retailerFinalOrderListAdapter;
        retailerFinalOrderListAdapter = new RetailerFinalOrderListAdapter(RetailerOrderActivity.this,
                distributorNameList, finalOrderList);
        if(orderConfirmRV != null){
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RetailerOrderActivity.this);
            orderConfirmRV.setLayoutManager(layoutManager);
            orderConfirmRV.setAdapter(retailerFinalOrderListAdapter);
        }


        int kg = 0, box = 0, pcs = 0, total = 0;

        for (int i = 0; i < finalOrderList.size(); i++) {

            ArrayList<MyProduct> myProducts = finalOrderList.get(i);
            for (int ind = 0; ind < myProducts.size(); ind++) {

                try {
                    //@Umesh LowerCase
                    if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("kg")) {

                        kg = kg + Integer.parseInt(myProducts.get(ind).getQuantity());

                    } else if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("box")) {

                        box = box + Integer.parseInt(myProducts.get(ind).getQuantity());

                    } else if (myProducts.get(ind).getUnit().toLowerCase(Locale.ROOT).contains("pc")) {

                        pcs = pcs + Integer.parseInt(myProducts.get(ind).getQuantity());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        //checking list have data or not
        if (retailerFinalOrderListAdapter.getItemCount() > 0) {

            llConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    llConfirm.setEnabled(false);
                    new MyAsynchTask(distributorIdList, finalOrderList, dialog)
                            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });

            dialog.show();

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                    distributorIdList.clear();
                    finalOrderList.clear();
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {

                    distributorIdList.clear();
                    finalOrderList.clear();
                }
            });

        } else {

            Toast.makeText(RetailerOrderActivity.this, "No data", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        SbAppConstants.STOP_SYNC = true;
        //check gps status if on/off
        locationProvider.checkGpsStatus();
    }

    private void setupViewPager(ViewPager myViewPager) {

        ArrayList<String> distributorName = new ArrayList<>();
        ArrayList<String> distributorId = new ArrayList<>();
        Cursor cursorBeat = null, cursorDis = null;

        try {

            //cursorBeat = salesBeatDb.getAllDataFromBeatListTable2(tempPref.getString(getString(R.string.beat_id_key), ""));
            cursorBeat = salesBeatDb.getDisBeatMap2(tempPref.getString(getString(R.string.beat_id_key), ""));

            if (cursorBeat != null && cursorBeat.getCount() > 0 && cursorBeat.moveToFirst()) {
                do {

                    String did = cursorBeat.getString(cursorBeat.getColumnIndex(SalesBeatDb.KEY_DID));
                    cursorDis = salesBeatDb.getDistributorName(did);

                    if (cursorDis != null && cursorDis.getCount() > 0 && cursorDis.moveToFirst()) {

                        String distributor_id = cursorDis.getString(cursorDis.getColumnIndex("distributor_id"));
                        String distributor_name = cursorDis.getString(cursorDis.getColumnIndex("distributor_name"));

                        distributorId.add(distributor_id);
                        distributorName.add(distributor_name);
                    }

                } while (cursorBeat.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursorBeat != null)
                cursorBeat.close();

            if (cursorDis != null)
                cursorDis.close();
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //To move selected distributor to first position
        for (int i = 0; i < distributorName.size(); i++) {

            if (distributorId.get(i).equalsIgnoreCase(tempPref.getString(getString(R.string.dis_id_key), ""))
                    && distributorName.get(i).equalsIgnoreCase(tempPref.getString(getString(R.string.dis_name_key), ""))) {

                String disNameMatched = distributorName.get(i);
                String didMatched = distributorId.get(i);

                String disName = distributorName.get(0);
                String did = distributorId.get(0);

                distributorId.set(0, didMatched);
                distributorName.set(0, disNameMatched);

                distributorId.set(i, did);
                distributorName.set(i, disName);

            }

        }

        for (int i = 0; i < distributorName.size(); i++) {

            adapter.addFragment(new RetailerOrderFragment(), distributorName.get(i), distributorId.get(i));

        }

        myViewPager.setAdapter(adapter);
        myViewPager.setOffscreenPageLimit(adapter.getCount());

    }

    @Override
    public void onBackPressed() {

        Intent intent = null;
        //locationProvider.unregisterReceiver();
        if (orderType.equalsIgnoreCase("onNewShop")) {

            intent = new Intent(RetailerOrderActivity.this, AddNewRetailerActivity.class);
            intent.putExtra("from", "order");
            intent.putExtra("newRet", newRet);
            startActivity(intent);
            RetailerOrderActivity.this.finish();
            //overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

        } else {
            RetailerOrderActivity.this.finish();
        }

    }

    private void deleteItems(String did) {

        try {

            //deleting for existing retailer order if exist
            if (from.equalsIgnoreCase("productive")
                    || from.equalsIgnoreCase("no order")
                    || from.equalsIgnoreCase("Order cancelled")) {

                salesBeatDb.deleteSpecificRecordFromOrderEntryListTable(rid, did);

            }

        } catch (Exception e) {
            //Log.e("RetailerOrderActivity", "===" + e.getMessage());
        }

    }

    private void saveData(String did, ArrayList<MyProduct> myProduct, String transactionId) {

        Log.e(TAG, " Order type: " + orderType);

        Calendar cal = Calendar.getInstance();
        String date = utilityClass.getYMDDateFormat().format(cal.getTime());
        String time2 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss ",
                Locale.ENGLISH).format(cal.getTime());

        //check for new retailer order
        if (orderType.equalsIgnoreCase("onNewShop")) {

            String[] params = newRet.getStringArray("params");
            String imageTimeStamp = newRet.getString("imageTime");
            String ownerImagePath = newRet.getString("ownerImage");
            String shopImagePath1 = newRet.getString("shopImage1");
            String shopImagePath2 = newRet.getString("shopImage2");
            String shopImagePath3 = newRet.getString("shopImage3");
            String shopImagePath4 = newRet.getString("shopImage4");

            //adding new retailer to db
            salesBeatDb.insertNewRetailerList(nrid, params[0], params[1], params[2],
                    params[3], params[4], params[5], locationProvider.getLatitudeStr(),
                    locationProvider.getLongitudeStr(), params[6], params[7], params[8], params[9],
                    params[10], params[11], params[12], params[13], params[14], params[15], params[16],
                    params[17], ownerImagePath, imageTimeStamp, shopImagePath1,
                    shopImagePath2, shopImagePath3, shopImagePath4,
                    "", did, tempPref.getString(getString(R.string.beat_id_key), ""), date, transactionId, date);

            String transactionId2 = prefSFA.getString(getString(R.string.emp_id_key), "") + "_" +
                    Calendar.getInstance().getTimeInMillis() + did;
            //entry for new retailer order
            salesBeatDb.entryInOderPlacedByNewRetailersTable(nrid, did, time2, orderType, checkInTime,
                    locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(),
                    "new productive", date, transactionId2, date);

            //entry for new retailer order skus
            for (int i = 0; i < myProduct.size(); i++) {
                Log.d("TAG", "upLoadOrder-1");
                salesBeatDb.insertNewOrderEntryListTable(myProduct.get(i).getProductId(),
                        myProduct.get(i).getMySkus(), myProduct.get(i).getBrand(),
                        myProduct.get(i).getPrice(), myProduct.get(i).getQuantity(),
                        myProduct.get(i).getUnit(), myProduct.get(i).getConversion(), nrid, did,
                        date, transactionId2, date);

            }

        }
        //check if order from newly added preferred retailer
        else if (orderType.equalsIgnoreCase(getString(R.string.pretailer))) {


//            String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
//
//
//            String transactionId = prefSFA.getString(getString(R.string.emp_id_key), "") + "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());

            String[] params = newRet.getStringArray("params");
            String shopImagePath1 = newRet.getString("shopImage1");

            salesBeatDb.insertNewPreferredRetailer(nrid, params[0], params[1], params[2], params[3], params[4],
                    params[5], params[6], params[7], params[8], params[9], params[10], params[11], params[12], params[13],
                    params[14], params[15], params[16], params[17], params[18], params[19], params[20], params[21], params[22], params[23],
                    params[24], params[25], params[26], params[27], params[28], params[29], params[30], params[31],
                    tempPref.getString(getString(R.string.dis_id_key), ""), tempPref.getString(getString(R.string.beat_id_key), ""),
                    date, transactionId, date);

            //entry for new retailer order
            salesBeatDb.entryInOderPlacedByPreferredRetailersTable(nrid, did, time2, orderType, checkInTime,
                    locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(),
                    "p productive", date, transactionId, date);

            //entry for new retailer order skus
            for (int i = 0; i < myProduct.size(); i++) {
                Log.d("TAG", "upLoadOrder-2");
                salesBeatDb.insertPreferredOrderEntryListTable(myProduct.get(i).getProductId(),
                        myProduct.get(i).getMySkus(), myProduct.get(i).getBrand(),
                        myProduct.get(i).getPrice(), myProduct.get(i).getQuantity(),
                        myProduct.get(i).getUnit(), myProduct.get(i).getConversion(), nrid, did,
                        date, transactionId, date);

            }

        }
        //checking if order is revised
        else if (orderType.equalsIgnoreCase(getString(R.string.reviseOrder))) {

            //if revised by new retailer
            if (from.equalsIgnoreCase(getString(R.string.reqNOrdered))
                    || from.equalsIgnoreCase(getString(R.string.reqNoOrdered)))
            {

                salesBeatDb.updateOderPlacedByNewRetailersTable(rid, did, time2, orderType, checkInTime,
                        locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(),
                        "new productive", date, transactionId, date);

                if(myProduct.size()>0) //@Umesh 20221122
                {
                    salesBeatDb.deleteSpecificDataFromNewOrderEntryListTable5(rid,did);
                }
            }
            //if revised by preferred retailer
            else if (from.equalsIgnoreCase(getString(R.string.preferredRetOrder))
                    || from.equalsIgnoreCase(getString(R.string.preferredRetNoOrder))) {

                salesBeatDb.updateOderPlacedByPreferredRetailersTable(rid, did, time2, orderType, checkInTime,
                        locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(),
                        "p productive", date, transactionId, date);

            }
            //if revised by existing retailer
            else {

                salesBeatDb.updateInOderPlacedByRetailersTable(rid, did, time2, orderType, checkInTime,
                        locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(), "",
                        transactionId, date);
            }




            //entry order skus
            for (int i = 0; i < myProduct.size(); i++) {

                //if revised by existing retailer
                if (from.equalsIgnoreCase("productive")
                        || from.equalsIgnoreCase("no order")
                        || from.equalsIgnoreCase("Order cancelled")) {
                    Log.d("TAG", "upLoadOrder-3");

                    salesBeatDb.insertOrderEntryListTable(myProduct.get(i).getProductId(),
                            myProduct.get(i).getMySkus(), myProduct.get(i).getBrand(),
                            myProduct.get(i).getPrice(), myProduct.get(i).getQuantity(),
                            myProduct.get(i).getUnit(), myProduct.get(i).getConversion(), rid, did,
                            date, transactionId, date);

                }
                //if revised by new retailer
                else if (from.equalsIgnoreCase(getString(R.string.reqNOrdered))
                        || from.equalsIgnoreCase(getString(R.string.reqNoOrdered))) {

                    Log.d("TAG", "upLoadOrder-4");
                    salesBeatDb.insertNewOrderEntryListTable2(myProduct.get(i).getProductId(),
                            myProduct.get(i).getMySkus(), myProduct.get(i).getBrand(),
                            myProduct.get(i).getPrice(), myProduct.get(i).getQuantity(),
                            myProduct.get(i).getUnit(), myProduct.get(i).getConversion(), rid,
                            did, date, transactionId, date);

                }
                //if revised by preferred retailer
                else if (from.equalsIgnoreCase(getString(R.string.preferredRetOrder))
                        || from.equalsIgnoreCase(getString(R.string.preferredRetNoOrder))) {
                    Log.d("TAG", "upLoadOrder-5");
                    salesBeatDb.insertPreferredOrderEntryListTable2(myProduct.get(i).getProductId(),
                            myProduct.get(i).getMySkus(), myProduct.get(i).getBrand(),
                            myProduct.get(i).getPrice(), myProduct.get(i).getQuantity(),
                            myProduct.get(i).getUnit(), myProduct.get(i).getConversion(), rid,
                            did, date, transactionId, date);

                }
            }

        }
        //check for existing retailer order
        else {
            Log.d("TAG", "upLoadOrder");
            //entry for existing retailer order skus
            salesBeatDb.entryInOderPlacedByRetailersTable(rid, did, time2, orderType, checkInTime,
                    locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(), "",
                    transactionId, date,String.valueOf(brandQty),String.valueOf(brandQtyPC));

            for (int i = 0; i < myProduct.size(); i++) {
                if(myProduct.get(i).getUnit().equalsIgnoreCase("KG")){
                    int BDbrandQty = Integer.parseInt(myProduct.get(i).getQuantity());
                    brandQty += BDbrandQty;
                }else if(myProduct.get(i).getUnit().equalsIgnoreCase("PC")){
                    int BDbrandQtyPC = Integer.parseInt(myProduct.get(i).getQuantity());
                    brandQtyPC += BDbrandQtyPC;
                }

                Log.d("TAG", "Count upLoadOrder :"+brandQty);
                Log.d("TAG", "Count upLoadOrder PC :"+brandQtyPC);
                salesBeatDb.insertOrderEntryListTable(myProduct.get(i).getProductId(),
                        myProduct.get(i).getMySkus(), myProduct.get(i).getBrand(),
                        myProduct.get(i).getPrice(), myProduct.get(i).getQuantity(),
                        myProduct.get(i).getUnit(), myProduct.get(i).getConversion(), rid,
                        did, date, transactionId, date);
            }

            String kg = brandQty+"_KG";
            String pc = brandQtyPC+"_PC";
            Log.d("TAG", "Update Qty kg :"+kg);
            Log.d("TAG", "Update Qty pc :"+pc);

            salesBeatDb.updateBrandQtyTable(rid, did,transactionId, date,kg,pc);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                //String result=data.getStringExtra("result");

                //GPSLocation.builder1 = null;
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //GPSLocation.builder1 = null;
                RetailerOrderActivity.this.finishAffinity();
            }
        }
    }

    private class MyAsynchTask extends AsyncTask<Void, Void, Void> {

        ArrayList<ArrayList<MyProduct>> finalOrderList;
        ArrayList<String> distributorIdList;
        Dialog dialog;

        public MyAsynchTask(ArrayList<String> distributorIdList, ArrayList<ArrayList<MyProduct>> finalOrderList,
                            Dialog dialog) {

            this.distributorIdList = distributorIdList;
            this.finalOrderList = finalOrderList;
            this.dialog = dialog;

            for (int index = 0; index < distributorIdList.size(); index++) {

                String did = distributorIdList.get(index);

                deleteItems(did);

            }
        }

        protected void onPreExecute() {


            for (int index = 0; index < distributorIdList.size(); index++) {

                ArrayList<MyProduct> myProduct = finalOrderList.get(index);
                String did = distributorIdList.get(index);

                String transactionId = prefSFA.getString(getString(R.string.emp_id_key), "") + "_" +
                        Calendar.getInstance().getTimeInMillis() + did;

                if (did != null && !did.isEmpty())
                    saveData(did, myProduct, transactionId);
            }

        }


        @Override
        protected Void doInBackground(Void... voids) {


            return null;
        }

        protected void onPostExecute(Void v) {

            dialog.dismiss();

            Intent intent = new Intent(RetailerOrderActivity.this, ConfirmationAnimationActivity.class);
            intent.putExtra("tabPosition", tabPosition);
            startActivity(intent);
            finish();
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public int getItemPosition(Object object) {
            return FragmentPagerAdapter.POSITION_UNCHANGED;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, String id) {

            Bundle bundle = new Bundle();
            bundle.putString("did", id);
            bundle.putString("orderType", orderType);
            bundle.putString("rid", rid);
            bundle.putString("from", from);
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}