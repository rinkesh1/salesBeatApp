package com.newsalesbeatApp.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.RetailerVisitHistoryActivity;
import com.newsalesbeatApp.customview.Animation;
import com.newsalesbeatApp.customview.Bar;
import com.newsalesbeatApp.customview.BarSet;
import com.newsalesbeatApp.customview.HorizontalStackBarChartView2;
import com.newsalesbeatApp.customview.Tools;
import com.newsalesbeatApp.customview.XRenderer;
import com.newsalesbeatApp.customview.YRenderer;
import com.newsalesbeatApp.fragments.BeatList;
import com.newsalesbeatApp.fragments.DistributorList;
import com.newsalesbeatApp.fragments.SkusFragment;
import com.newsalesbeatApp.pojo.DistrebutorItem;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import io.sentry.Sentry;

//import com.db.chart.animation.Animation;
//import com.db.chart.model.Bar;
//import com.db.chart.model.BarSet;
//import com.db.chart.tooltip.Tooltip;
//import com.db.chart.view.HorizontalBarChartView;

/*
 * Created by MTC on 26-07-2017.
 */

public class DistributorListAdapter extends RecyclerView.Adapter<DistributorListAdapter.ViewHolder>
        implements Filterable {

    private static FirebaseAnalytics firebaseAnalytics;
    String TAG = "DistributorListAdapter";
    int MY_SOCKET_TIMEOUT_MS = 50000;
    DistributorList distributorList;
    Snackbar snackbar;
    RecyclerView tempV;
    private ArrayList<DistrebutorItem> distrebutorItemList = new ArrayList<>();
    private ArrayList<DistrebutorItem> distrebutorItemList2 = new ArrayList<>();
    private Context context;
    private SharedPreferences tempPref, myPerf;
    private UtilityClass utilityClass;
    private SalesBeatDb salesBeatDb;
    private Paint thresPaint;
    public  String PhoneNo,Email; //@Umesh 20221208

    public DistributorListAdapter(Context ctx, ArrayList<DistrebutorItem> items,
                                  ShimmerRecyclerView rcvDistrebutorList) {

        try {

            this.context = ctx;
            this.distrebutorItemList = items;
            this.distrebutorItemList2 = items;
            tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
            myPerf = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
            utilityClass = new UtilityClass(context);
            //salesBeatDb = new SalesBeatDb(context);
            salesBeatDb = SalesBeatDb.getHelper(ctx);
            firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
            this.tempV = rcvDistrebutorList;
            distributorList = new DistributorList();

            thresPaint = new Paint();
            thresPaint.setColor(Color.parseColor("#dad8d6"));
            thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
            thresPaint.setStyle(Paint.Style.STROKE);
            thresPaint.setAntiAlias(true);
            thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.distrebutor_list_row, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        initializeSaleGraph(holder, distrebutorItemList.get(position).getDistributorSaleTarget(),
                distrebutorItemList.get(position).getDistributorSaleAch());

        snackbar = Snackbar
                .make(tempV, "Please wait...", Snackbar.LENGTH_INDEFINITE);

        holder.tvDistrebutorName.setText(distrebutorItemList.get(position).getDistrebutorName());
        holder.tvDistrebuterAddress.setText(distrebutorItemList.get(position).getDistrebutor_address());
        String letter = String.valueOf(distrebutorItemList.get(position).getDistrebutorName().charAt(0));
        holder.distrebutorIcon.setText(letter);
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.distrebutorIcon.setTextColor(color);

        Cursor checkStockAvailability = salesBeatDb.
                getSpecificDataFromSkuEntryListTable2(distrebutorItemList.get(position).getDistrebutorId());

        if (checkStockAvailability.getCount() > 0) {
            //holder.imgTaken.setVisibility(View.VISIBLE);
            holder.tvReviseStock.setVisibility(View.VISIBLE);
            holder.tvStockCaptured.setVisibility(View.GONE);
        } else {
            //holder.imgTaken.setVisibility(View.GONE);
            holder.tvReviseStock.setVisibility(View.GONE);
            holder.tvStockCaptured.setVisibility(View.VISIBLE);
        }


        final Cursor checkOrderAvailability = salesBeatDb.
                getSpecificDataFromDisOrderEntryListTable2(distrebutorItemList.get(position).getDistrebutorId());

        if (checkOrderAvailability.getCount() > 0) {
            //holder.imgTaken.setVisibility(View.VISIBLE);
            holder.tvDisReviseOrder.setVisibility(View.VISIBLE);
            holder.tvDisBookOrder.setVisibility(View.GONE);
        } else {
            //holder.imgTaken.setVisibility(View.GONE);
            holder.tvDisReviseOrder.setVisibility(View.GONE);
            holder.tvDisBookOrder.setVisibility(View.VISIBLE);
        }


        holder.tvDistrebutorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(context.getString(R.string.dis_id_key), distrebutorItemList.get(position).getDistrebutorId());
                editor.putString(context.getString(R.string.dis_name_key), distrebutorItemList.get(position).getDistrebutorName());
                editor.apply();

                addSkusList(distrebutorItemList.get(position).getDistrebutorId());

            }
        });

        holder.tvOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("DistributorList", "OrderHistory",
//                        "Dist. Order History by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));

                Bundle params = new Bundle();
                params.putString("Action", "Order History");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                if (utilityClass.isInternetConnected()) {

                    Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                    intent.putExtra("did", distrebutorItemList.get(position).getDistrebutorId());
                    intent.putExtra("name", distrebutorItemList.get(position).getDistrebutorName());
                    intent.putExtra("from", "distributor");
                    context.startActivity(intent);

                } else {
                    Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.tvStockHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("DistributorList", "StockHistory",
//                        "Dist. Stock History by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));


                Bundle params = new Bundle();
                params.putString("Action", "Stock History");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                if (utilityClass.isInternetConnected()) {
                    Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                    intent.putExtra("did", distrebutorItemList.get(position).getDistrebutorId());
                    intent.putExtra("name", distrebutorItemList.get(position).getDistrebutorName());
                    intent.putExtra("from", "distributor_s");
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "adapter onClick");
                SharedPreferences.Editor editor = tempPref.edit();
                editor.putString(context.getString(R.string.dis_id_key), distrebutorItemList.get(position).getDistrebutorId());
                editor.putString(context.getString(R.string.dis_name_key), distrebutorItemList.get(position).getDistrebutorName());
                editor.apply();
                addSkusList(distrebutorItemList.get(position).getDistrebutorId());
            }
        });


        holder.tvStockCaptured.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                SBApplication.getInstance().trackEvent("DistributorList", "StockCapture",
//                        "Dist. Stock Capture by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));

                Bundle params = new Bundle();
                params.putString("Action", "Stock Capture");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                goForStock(position);


            }
        });

        holder.tvReviseStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("DistributorList", "ReviseCapture",
//                        "Dist. Revise Capture by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));

                Bundle params = new Bundle();
                params.putString("Action", "Revise Stock History");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                goForStock(position);

            }
        });

        holder.tvDisBookOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("DistributorList", "DistributoeOrder",
//                        "Dist. Order by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));


                Bundle params = new Bundle();
                params.putString("Action", "Distributor Order");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                goForOrder(position);

            }
        });


        holder.tvDisReviseOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("DistributorList", "DistributoeOrder",
//                        "Dist. Order Revise by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));


                Bundle params = new Bundle();
                params.putString("Action", "Revise Distributor Order");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                goForOrder(position);
            }
        });


        holder.tvPartyOutstanding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SBApplication.getInstance().trackEvent("DistributorList", "PartyOutstanding",
//                        "Party Outstanding visit by:"+myPerf.getString(context.getString(R.string.emp_id_key),""));


                Bundle params = new Bundle();
                params.putString("Action", "Party Outstanding visit");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                if (utilityClass.isInternetConnected()) {
                    snackbar.show();
                    showOutstandingDialog(position);
                } else {
                    Toast.makeText(context, "You are not connected to internet", Toast.LENGTH_LONG).show();
                }

            }
        });


        holder.callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "Call Button");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                String phn = distrebutorItemList.get(position).getDistrebutor_phone();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phn));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    context.startActivity(intent);
                }

            }
        });


        holder.locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Bundle params = new Bundle();
                    params.putString("Action", "Map Button");
                    params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("DistributorList", params);

                    double lat2 = Double.parseDouble(distrebutorItemList.get(position).getLat());
                    double longt2 = Double.parseDouble(distrebutorItemList.get(position).getLongt());

                    String uri = "http://maps.google.com/?daddr=" + lat2 + "," + longt2;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    context.startActivity(intent);

                } catch (Exception e) {
                    Toast.makeText(context, "Missing data", Toast.LENGTH_SHORT).show();
                }
            }
        });


        holder.infoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle params = new Bundle();
                params.putString("Action", "Info Button");
                params.putString("UserId", "" + myPerf.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("DistributorList", params);

                showDistributorInfoDialog(position);

            }
        });


//        holder.feedBackIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String did = distrebutorItemList.get(position).getDistrebutorId();
//                Intent intent = new Intent(context, RetailersFeedBack.class);
//                intent.putExtra("id", did);
//                intent.putExtra("from", "distributor");
//                context.startActivity(intent);
//            }
//        });

    }

    private void goForOrder(int position) {

        SharedPreferences.Editor editor = tempPref.edit();
        editor.putString(context.getString(R.string.dis_id_key), distrebutorItemList.get(position).getDistrebutorId());
        editor.putString(context.getString(R.string.dis_name_key), distrebutorItemList.get(position).getDistrebutorName());
        editor.apply();

        Bundle bundle = new Bundle();
        bundle.putString("from", "order");

        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        Fragment fragment = new SkusFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }

    private void goForStock(int position) {

        SharedPreferences.Editor editor = tempPref.edit();
        editor.putString(context.getString(R.string.dis_id_key), distrebutorItemList.get(position).getDistrebutorId());
        editor.putString(context.getString(R.string.dis_name_key), distrebutorItemList.get(position).getDistrebutorName());
        editor.apply();

        Bundle bundle = new Bundle();
        bundle.putString("from", "stock");

        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        Fragment fragment = new SkusFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.flContainer, fragment);
        ft.commit();
    }

    private void showOutstandingDialog(final int pos) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                SbAppConstants.API_GET_PARTY_OUTSTANDING + "?did=" + distrebutorItemList.get(pos).getDistrebutorId(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("Response", " Party Outstanding: " + response.toString());
                snackbar.dismiss();

                try {
                    //@Umesh
                    if(response.getInt("status")==1)
                    {
                        JSONArray dataarr = response.getJSONArray("data");
                        if(dataarr.length()>0)
                        {
                            JSONObject data = dataarr.getJSONObject(0);
                            String credit_days = data.getString("credit_days_limit");
                            String credit_limit = data.getString("credit_limit");
                            String bill_date = data.getString("bill_date");
                            String bill_number = data.getString("bill_number");
                            String bill_amount = data.getString("bill_amount");
                            String due_days = data.getString("due_days");
                            String pending_amount = data.getString("pending_amount");
                            String updated_at = data.getString("updated_at");

                            final Dialog partyOutstandingDialog = new Dialog(context);
                            partyOutstandingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            partyOutstandingDialog.setContentView(R.layout.party_outstanding_dialog);
                            partyOutstandingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            TextView tvPartyName = partyOutstandingDialog.findViewById(R.id.tvPartyName);
                            TextView tvCreditDays = partyOutstandingDialog.findViewById(R.id.tvCreditDays);
                            TextView tvCreditLimit = partyOutstandingDialog.findViewById(R.id.tvCreditLimit);
                            TextView tvBillDate = partyOutstandingDialog.findViewById(R.id.tvBillDate);
                            TextView tvBillNumber = partyOutstandingDialog.findViewById(R.id.tvBillNumber);
                            TextView tvBillAmt = partyOutstandingDialog.findViewById(R.id.tvBillAmount);
                            TextView tvDueDays = partyOutstandingDialog.findViewById(R.id.tvDueDays);
                            TextView tvPendingAmt = partyOutstandingDialog.findViewById(R.id.tvPendingAmt);
                            TextView tvLastUpdated = partyOutstandingDialog.findViewById(R.id.tvLastUpdated);
                            LinearLayout llCall = partyOutstandingDialog.findViewById(R.id.llCall);
                            LinearLayout llCancel = partyOutstandingDialog.findViewById(R.id.llCancelDialog);

                            tvPartyName.setText(distrebutorItemList.get(pos).getDistrebutorName());
                            tvCreditDays.setText(credit_days);
                            tvCreditLimit.setText(credit_limit);
                            tvBillDate.setText(bill_date);
                            tvBillNumber.setText(bill_number);
                            tvBillAmt.setText(bill_amount);
                            tvDueDays.setText(due_days + " days");
                            tvPendingAmt.setText(pending_amount);
                            tvLastUpdated.setText(updated_at);

                            llCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String phn = distrebutorItemList.get(pos).getDistrebutor_phone();
                                    Intent intent = new Intent(Intent.ACTION_CALL);
                                    intent.setData(Uri.parse("tel:" + phn));
                                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                        context.startActivity(intent);
                                    }
                                }
                            });

                            llCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    partyOutstandingDialog.dismiss();
                                }
                            });

                            partyOutstandingDialog.show();
                        }
                        else
                        {
                            Toast.makeText(context, "No Bills!!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, error -> {

            Toast.makeText(context, "status code: " + error.networkResponse.statusCode + " " + error.getMessage(), Toast.LENGTH_SHORT).show();

            snackbar.dismiss();
            SbLog.printError(TAG, "getOutstanding", String.valueOf(error.networkResponse.statusCode), error.getMessage(),
                    myPerf.getString(context.getString(R.string.emp_id_key), ""));
            error.printStackTrace();
        }) {

            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", myPerf.getString("token", ""));
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsonObjectRequest);

    }

    private void addSkusList(String did) {

        //Cursor cursor = salesBeatDb.getAllDataFromBeatListTable(did);
        ArrayList<String> beatIdList = new ArrayList<>();
        Cursor cursor = salesBeatDb.getDisBeatMap(did);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

            beatIdList.add(cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_BID)));
        }

        Log.d(TAG, "addSkusList: "+beatIdList.size());
        if (beatIdList.size() > 0) {

            FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
            Fragment fragment = new BeatList();
            ft.replace(R.id.flContainer, fragment);
            ft.commit();

        } else {

            Toast.makeText(context, "There is no beat in this distributor", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeSaleGraph(ViewHolder holder, String distributorSaleTarget, String distributorSaleAch) {

        try {

            if (distributorSaleTarget != null && !distributorSaleTarget.isEmpty()
                    && !distributorSaleTarget.equalsIgnoreCase("null"))
                //holder.tvVal1.setText(distributorSaleTarget + context.getString(R.string.unitt));
                //@Umesh 20220903
                holder.tvVal1.setText(distributorSaleTarget );

            if (distributorSaleAch != null && !distributorSaleAch.isEmpty()
                    && !distributorSaleAch.equalsIgnoreCase("null"))
               // holder.tvVal2.setText(distributorSaleAch + context.getString(R.string.unitt));
                //@Umesh 20220903
                holder.tvVal2.setText(distributorSaleAch);

            float[] mValues = new float[2];
            String[] mLabels1 = new String[2];

            mValues[0] = Float.parseFloat(distributorSaleTarget);
            mValues[1] = Float.parseFloat(distributorSaleAch);

            mLabels1[0] = "";
            mLabels1[1] = "";

            Bar bar1 = new Bar(mLabels1[0], mValues[0]);
            bar1.setColor(Color.parseColor("#feb47b"));

            Bar bar2 = new Bar(mLabels1[1], mValues[1]);
            bar2.setColor(Color.parseColor("#6cbf84"));


            BarSet barSet1 = new BarSet();
            barSet1.addBar(bar2);
            barSet1.addBar(bar1);

            holder.barChartSale.addData(barSet1);
            holder.barChartSale.setXAxis(false);
            holder.barChartSale.setYAxis(false);

//            Tooltip tip1 = new Tooltip(context);
//            tip1.setBackgroundColor(Color.parseColor("#CC7B1F"));

//            holder.barChartSale.setTooltips(tip1)
//                    .show(new Animation().setInterpolator(new AccelerateDecelerateInterpolator()));

            if (!context.getResources().getBoolean(R.bool.isTablet)) {

                holder.barChartSale.setRoundCorners(20);
                holder.barChartSale.setBarSpacing(30);
            }

            int[] order = {0, 1};

            holder.barChartSale.setXLabels(XRenderer.LabelPosition.OUTSIDE)
                    .setYLabels(YRenderer.LabelPosition.OUTSIDE)
                    .setValueThreshold(89.f, 89.f, thresPaint)
                    .show(new Animation().inSequence(.5f, order));


        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void showDialog(View view, ImageView imgMore, final int pos, Cursor checkOrderAvailability) {

        final PopupMenu popupMenu = new PopupMenu(view.getContext(), imgMore);

        if (checkOrderAvailability.getCount() > 0)
            ((Activity) context).getMenuInflater().inflate(R.menu.distributor_menu2, popupMenu.getMenu());
        else
            ((Activity) context).getMenuInflater().inflate(R.menu.distributor_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                /*if (item.getItemId() == R.id.takeDistributorOrder) {


                    Bundle bundle = new Bundle();
                    bundle.putString("from", "order");

                    FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                    Fragment fragment = new SkusFragment();
                    fragment.setArguments(bundle);
                    ft.replace(R.id.flContainer, fragment);
                    ft.commit();

                } else*/
                if (item.getItemId() == R.id.cancelDistributorOrder) {

                    cancelDistributorOrder(tempPref.getString(context.getString(R.string.dis_id_key), ""));

                } /*else if (item.getItemId() == R.id.partyOutstanding){

                    if (utilityClass.isInternetConnected()) {
                        Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                        showOutstandingDialog(pos);
                    } else {
                        Toast.makeText(context, "You are not connected to internet", Toast.LENGTH_LONG).show();
                    }

                }*/ else if (item.getItemId() == R.id.orderHistory) {

                    if (utilityClass.isInternetConnected()) {

                        Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                        intent.putExtra("did", distrebutorItemList.get(pos).getDistrebutorId());
                        intent.putExtra("name", distrebutorItemList.get(pos).getDistrebutorName());
                        intent.putExtra("from", "distributor");
                        context.startActivity(intent);

                    } else {
                        Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }


                } else if (item.getItemId() == R.id.stockCaptured) {

                    if (utilityClass.isInternetConnected()) {
                        Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                        intent.putExtra("did", distrebutorItemList.get(pos).getDistrebutorId());
                        intent.putExtra("name", distrebutorItemList.get(pos).getDistrebutorName());
                        intent.putExtra("from", "distributor_s");
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }

                }

                return false;
            }
        });

        popupMenu.show();

    }

    private void cancelDistributorOrder(final String distrebutorId) {

        Log.e("DistributorList", "Did===" + distrebutorId);

        Cursor cursor = null;
        try {

            cursor = salesBeatDb.getSpecificFromDistributorOrderTable(distrebutorId);
            if (cursor != null && cursor.getCount() > 0) {
                int val = salesBeatDb.updateInDistributorOrderTable2(distrebutorId);
                if (val == 1) {
                    Toast.makeText(context, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(context, "No order exist", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }


    public void showDistributorInfoDialog(int position) {

        final Dialog dialog = new Dialog(context, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.distributor_info_dialog);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.BOTTOM);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }

        ImageView imgDisClose = dialog.findViewById(R.id.imgDisClose);

        final TextView tvDisName = dialog.findViewById(R.id.tvDisName);
        final TextView tvDisAddress = dialog.findViewById(R.id.tvDisAddress);
        final TextView tvDisDistrict = dialog.findViewById(R.id.tvDisDistrict);
        final TextView tvDisZone = dialog.findViewById(R.id.tvDisZone);
        final TextView tvDisState = dialog.findViewById(R.id.tvDisState);
        final TextView tvDisEmail = dialog.findViewById(R.id.tvDisEmail);
        final TextView tvDisPhoneNo = dialog.findViewById(R.id.tvDisPhoneNo);
        final TextView tvDisType = dialog.findViewById(R.id.tvDisType);
        final TextView tvDisPinNo = dialog.findViewById(R.id.tvDisPinNo);
        final TextView tvDisLatitude = dialog.findViewById(R.id.tvDisLatitude);
        final TextView tvDisLongtitude = dialog.findViewById(R.id.tvDisLongtitude);
        final TextView tvDisGSTIN = dialog.findViewById(R.id.tvDisGSTIN);
        LinearLayout llUpdateDistMob = dialog.findViewById(R.id.llUpdateDistMob);


        if (distrebutorItemList.get(position).getDistrebutorName() == null
                || distrebutorItemList.get(position).getDistrebutorName().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutorName().isEmpty()) {
            tvDisName.setText("NA");
        } else {
            tvDisName.setText(distrebutorItemList.get(position).getDistrebutorName());
        }

        if (distrebutorItemList.get(position).getDistrebutor_address() == null
                || distrebutorItemList.get(position).getDistrebutor_address().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_address().isEmpty()) {
            tvDisAddress.setText("NA");
        } else {
            tvDisAddress.setText(distrebutorItemList.get(position).getDistrebutor_address());
        }

        if (distrebutorItemList.get(position).getDistrebutor_district() == null
                || distrebutorItemList.get(position).getDistrebutor_district().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_district().isEmpty()) {
            tvDisDistrict.setText("NA");
        } else {
            tvDisDistrict.setText(distrebutorItemList.get(position).getDistrebutor_district());
        }

        if (distrebutorItemList.get(position).getDistrebutor_zone() == null
                || distrebutorItemList.get(position).getDistrebutor_zone().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_zone().isEmpty()) {
            tvDisZone.setText("NA");
        } else {
            tvDisZone.setText(distrebutorItemList.get(position).getDistrebutor_zone());
        }

        if (distrebutorItemList.get(position).getDistrebutor_state() == null
                || distrebutorItemList.get(position).getDistrebutor_state().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_state().isEmpty()) {
            tvDisState.setText("NA");
        } else {
            tvDisState.setText(distrebutorItemList.get(position).getDistrebutor_state());
        }

        if (distrebutorItemList.get(position).getDistrebutor_email() == null
                || distrebutorItemList.get(position).getDistrebutor_email().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_email().isEmpty()) {
            tvDisEmail.setText("NA");
        } else {
            tvDisEmail.setText(distrebutorItemList.get(position).getDistrebutor_email());
            Email = tvDisEmail.getText().toString();
        }

        if (distrebutorItemList.get(position).getDistrebutor_phone() == null
                || distrebutorItemList.get(position).getDistrebutor_phone().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_phone().isEmpty()) {
            tvDisPhoneNo.setText("NA");
        } else {
            tvDisPhoneNo.setText(distrebutorItemList.get(position).getDistrebutor_phone());
            PhoneNo = tvDisPhoneNo.getText().toString();
        }

        if (distrebutorItemList.get(position).getDistrebutor_type() == null
                || distrebutorItemList.get(position).getDistrebutor_type().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_type().isEmpty()) {
            tvDisType.setText("NA");
        } else {
            tvDisType.setText(distrebutorItemList.get(position).getDistrebutor_type());
        }

        if (distrebutorItemList.get(position).getDistrebutor_pincode() == null
                || distrebutorItemList.get(position).getDistrebutor_pincode().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDistrebutor_pincode().isEmpty()) {
            tvDisPinNo.setText("NA");
        } else {
            tvDisPinNo.setText(distrebutorItemList.get(position).getDistrebutor_pincode());
        }

        if (distrebutorItemList.get(position).getDis_gstn() == null
                || distrebutorItemList.get(position).getDis_gstn().equalsIgnoreCase("null")
                || distrebutorItemList.get(position).getDis_gstn().isEmpty()) {
            tvDisGSTIN.setText("NA");
        } else {
            tvDisPinNo.setText(distrebutorItemList.get(position).getDis_gstn());
        }

        tvDisLatitude.setText("NA");
        tvDisLongtitude.setText("NA");

        imgDisClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        llUpdateDistMob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showMobUpdateDialog(dialog, distrebutorItemList.get(position).getDistrebutorId(), context);
            }
        });

        dialog.show();

    }

    private void showMobUpdateDialog(Dialog infodialog, String did, Context context) {

        final Dialog dialog = new Dialog(context, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.dist_mob_number_update);
        EditText edtNobileNumber = dialog.findViewById(R.id.edtNobileNumber);
        EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        LinearLayout llSubmitDistMob = dialog.findViewById(R.id.llSubmitDistMob);
        edtNobileNumber.setText(PhoneNo);
        edtEmail.setText(Email);

        llSubmitDistMob.setOnClickListener(view -> {
            String mob = edtNobileNumber.getText().toString();
            String email = edtEmail.getText().toString();
            Log.d(TAG, "email lenght: "+email.length());
            if (!mob.isEmpty()) {
                if (mob.length() == 10) {
                    if(isValidMobileNumber(mob)){
                        if(email.length() == 0){
                            updateNumber(did, mob,email,dialog, infodialog);
                        }else if(isValidEmail(email)){
                            updateNumber(did, mob,email,dialog, infodialog);
                        }else {
                            Toast.makeText(context, "Please Enter valid Email address", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d("TAG", "showMobUpdateDialog update");
                    }else {
                        Toast.makeText(context, "Please Enter valid Mobile number", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(context, "Mobile number is not valid", Toast.LENGTH_SHORT).show();
                }
            } else {

                Toast.makeText(context, "Mobile number is empty", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();

    }

    public static boolean isValidMobileNumber(String mobileNumber) {
        Pattern pattern = Pattern.compile("^[6-9]\\d{9}$");
        return pattern.matcher(mobileNumber).matches();
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void updateNumber(String did, String mob,String email, Dialog dialog, Dialog infodialog) {

        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating...");
        progressDialog.show();

        JSONObject orderrrr = new JSONObject();
        try {
            orderrrr.put("did", did);
            orderrrr.put("phone1", mob);
            orderrrr.put("email1", email);

            //@Umesh 20221207
            GPSLocation locationProvider = new GPSLocation(context);
            locationProvider.checkGpsStatus();//check gps status if on/off
            orderrrr.put("latitude",  String.valueOf(locationProvider.getLatitude()));
            orderrrr.put("longitude", String.valueOf(locationProvider.getLongitude()));

            Log.d("Login json", " " + orderrrr.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_DISTRIBUTOR_MOBILE_NUMBER_UPDATE,
                orderrrr, response -> {

            //loader.dismiss();
            Log.e(TAG, "Response Distributor mobile number update: " + response);
            progressDialog.dismiss();
            //{"status":"success","statusMessage":"Success"}
            try {
                //@Umesh
                if (response.getInt("status")==1)
                {
                    dialog.dismiss();
                    infodialog.dismiss();

                    boolean flag = salesBeatDb.updateDistributorTable(did, mob);
                    if (flag) {
                        //to initialize list with updated data
                        Bundle bundle = new Bundle();
                        bundle.putString("from", "town");
                        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                        Fragment fragment = new DistributorList();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.flContainer, fragment);
                        ft.commitAllowingStateLoss();
                    }
                    Toast.makeText(context, "" + "Updated!!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "" + response.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Sentry.captureMessage(e.getMessage());
            }


        }, error -> {
            dialog.dismiss();
            progressDialog.dismiss();
            error.printStackTrace();
            Sentry.captureMessage(error.getMessage());
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", myPerf.getString("token", ""));
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return distrebutorItemList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    distrebutorItemList = distrebutorItemList2;
                } else {
                    ArrayList<DistrebutorItem> filteredList = new ArrayList<>();
                    for (DistrebutorItem row : distrebutorItemList2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDistrebutorName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    distrebutorItemList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = distrebutorItemList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                distrebutorItemList = (ArrayList<DistrebutorItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDistrebutorName, tvDistrebuterAddress, tvStockCaptured, tvReviseStock, tvPartyOutstanding,
                tvDisBookOrder, tvDisReviseOrder, tvOrderHistory, tvStockHistory;
        ImageView callIcon, infoIcon, locationIcon, feedBackIcon, nextIcon;
        TextView distrebutorIcon, tvVal1, tvVal2;

        HorizontalStackBarChartView2 barChartSale;

        public ViewHolder(View itemView) {
            super(itemView);
            //TextViews
            tvOrderHistory = itemView.findViewById(R.id.tvOrderHistory);
            tvStockHistory = itemView.findViewById(R.id.tvStockHistory);
            tvDistrebutorName = itemView.findViewById(R.id.tvDistrebuterName);
            tvStockCaptured = itemView.findViewById(R.id.tvStockCapture);
            tvReviseStock = itemView.findViewById(R.id.tvReviseStock);
            tvDisBookOrder = itemView.findViewById(R.id.tvDisBookOrder);
            tvDisReviseOrder = itemView.findViewById(R.id.tvDisReviseOrder);
            tvPartyOutstanding = itemView.findViewById(R.id.tvPartyOutstanding);
            tvDistrebuterAddress = itemView.findViewById(R.id.tvDistrebuterAddress);
            distrebutorIcon = itemView.findViewById(R.id.distrebutorIcon);
            tvVal1 = itemView.findViewById(R.id.tvVal1);
            tvVal2 = itemView.findViewById(R.id.tvVal2);
            //ImageView
            callIcon = itemView.findViewById(R.id.callIcon);
            locationIcon = itemView.findViewById(R.id.locationIcon);
            infoIcon = itemView.findViewById(R.id.infoIcon);
            feedBackIcon = itemView.findViewById(R.id.feedBackIcon);
            nextIcon = itemView.findViewById(R.id.nextIcon);

            barChartSale = itemView.findViewById(R.id.barChartSale);

        }
    }

}
