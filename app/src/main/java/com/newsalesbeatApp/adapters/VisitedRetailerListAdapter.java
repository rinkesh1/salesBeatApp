package com.newsalesbeatApp.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.RetailerActivity;
import com.newsalesbeatApp.activities.RetailerOrderActivity;
import com.newsalesbeatApp.activities.RetailerVisitHistoryActivity;
import com.newsalesbeatApp.activities.RetailersFeedBack;
import com.newsalesbeatApp.activities.UpdateRetailersActivity;
import com.newsalesbeatApp.interfaces.ApiIntentface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.SbLog;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

/*
 * Created by Dhirendra Thakur on 07-11-2017.
 */

public class VisitedRetailerListAdapter
        extends RecyclerView.Adapter<VisitedRetailerListAdapter.ViewHolder>
        implements Filterable {

    private static FirebaseAnalytics firebaseAnalytics;
    Context context;
    private String TAG = getClass().getName();
    private ArrayList<RetailerItem> visitedRetailerList;
    private ArrayList<RetailerItem> visitedRetailerList2;
    private ArrayList<ArrayList<String>> orderPlacedForDistributorList;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private UtilityClass utilityClass;
    private SharedPreferences prefSFA;
    private SalesBeatDb salesBeatDb;
    private GPSLocation locationProvider;
    private ApiIntentface apiIntentface;


    //Constructor
    public VisitedRetailerListAdapter(Context ctx, ArrayList<RetailerItem> retailerItemList,
                                      ArrayList<ArrayList<String>> orderPlacedForDistributorList) {

        this.context = ctx;
        this.visitedRetailerList = retailerItemList;
        this.visitedRetailerList2 = retailerItemList;
        this.orderPlacedForDistributorList = orderPlacedForDistributorList;
        utilityClass = new UtilityClass(ctx);
        locationProvider = new GPSLocation(ctx);
        prefSFA = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
        //salesBeatDb = new SalesBeatDb(ctx);
        salesBeatDb = SalesBeatDb.getHelper(ctx);
        firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
        apiIntentface = RetrofitClient.getClient().create(ApiIntentface.class);


        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(context);

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
                private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

                public void putBitmap(String url, Bitmap bitmap) {
                    mCache.put(url, bitmap);
                }

                public Bitmap getBitmap(String url) {
                    return mCache.get(url);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public VisitedRetailerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.visited_retailer_list_row, parent, false);
        return new VisitedRetailerListAdapter.ViewHolder(view);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final VisitedRetailerListAdapter.ViewHolder holder,
                                 @SuppressLint("RecyclerView") final int position) {

        holder.tvRetailerName.setText(visitedRetailerList.get(position).getRetailerName());
        holder.tvRetailerAddress.setText("Address:- " + visitedRetailerList.get(position).getRetailerAddress());
        holder.tvCounter.setText(String.valueOf((visitedRetailerList.size() - position)));

        holder.tvTimeStamp.setText("Visited At:- " + getTimeInAMPM(visitedRetailerList.get(position).getTimeStamp()));

        if (visitedRetailerList.get(position).getServerStatus().equalsIgnoreCase("success"))
            holder.llServerStatus.setBackgroundColor(Color.parseColor("#7FFF00"));
        else if (visitedRetailerList.get(position).getServerStatus().equalsIgnoreCase("error"))
            holder.llServerStatus.setBackgroundColor(Color.parseColor("#ff00bf"));
        else
            holder.llServerStatus.setBackgroundColor(Color.parseColor("#F0544D"));

        String[] images = visitedRetailerList.get(position).getRetailer_image().split(",");
        holder.retailerIcon.setImageUrl(SbAppConstants.IMAGE_PREFIX_RETAILER_THUMB + images[0],
                mImageLoader);

        //@Umesh
//        if(images[0].length()>0 && images[0]!="default.jpeg")
//        {
//            byte[] imageBytes = Base64.decode(images[0], Base64.DEFAULT);
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            holder.retailerIcon.setBackground(null);
//            holder.retailerIcon.setDefaultImageBitmap(decodedImage);
//        }

        Log.e(TAG, " -->" + visitedRetailerList.get(position).getOrderType());

        if (visitedRetailerList.get(position).getOrderType().equalsIgnoreCase("no order")) {

            holder.tvFrom.setText("no Order");
            holder.tvOrderType.setText("No Order");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#F0544D"));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.circle_back_red);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.tvCancelOrder.setVisibility(View.GONE);
            holder.tvNewRetailer.setVisibility(View.GONE);

        } else if (visitedRetailerList.get(position).getOrderType().equalsIgnoreCase("cancelled")) {

            holder.tvFrom.setText("Order cancelled");
            holder.tvOrderType.setText("Cancelled");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#F0544D"));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.cirle_back_yellow);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.tvCancelOrder.setVisibility(View.GONE);
            holder.tvNewRetailer.setVisibility(View.GONE);

        } else if (visitedRetailerList.get(position).getOrderType().equalsIgnoreCase("no order new")) {

            holder.tvFrom.setText(context.getString(R.string.reqNoOrdered));
            holder.tvOrderType.setText("No Order");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#F0544D"));//context.getColor(R.color.red_like));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.cirle_back_yellow);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.imgMore.setVisibility(View.VISIBLE);
            holder.tvCancelOrder.setVisibility(View.GONE);
            holder.tvNewRetailer.setVisibility(View.VISIBLE);

        } else if (visitedRetailerList.get(position).getOrderType().equalsIgnoreCase("new productive")) {

            holder.tvFrom.setText(context.getString(R.string.reqNOrdered));
            holder.tvOrderType.setText("Productive");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#ff99cc00"));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.cirle_back_yellow);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.imgMore.setVisibility(View.VISIBLE);
            holder.tvCancelOrder.setVisibility(View.GONE);
            holder.tvNewRetailer.setVisibility(View.VISIBLE);

        } else if (visitedRetailerList.get(position).getOrderType().equalsIgnoreCase("pre no order")) {

            holder.tvFrom.setText(context.getString(R.string.preferredRetNoOrder));
            holder.tvOrderType.setText("No Order");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#F0544D"));//context.getColor(R.color.red_like));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.cirle_back_yellow);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.imgMore.setVisibility(View.GONE);
            holder.tvCancelOrder.setVisibility(View.GONE);
            holder.tvNewRetailer.setVisibility(View.VISIBLE);
            holder.tvNewRetailer.setText("Preferred");

        } else if (visitedRetailerList.get(position).getOrderType().equalsIgnoreCase("p productive")) {

            holder.tvFrom.setText(context.getString(R.string.preferredRetOrder));
            holder.tvOrderType.setText("Productive");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#ff99cc00"));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.cirle_back_yellow);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.imgMore.setVisibility(View.GONE);
            holder.tvCancelOrder.setVisibility(View.GONE);
            holder.tvNewRetailer.setVisibility(View.VISIBLE);
            holder.tvNewRetailer.setText("Preferred");

        } else {

            holder.tvFrom.setText("Productive");
            holder.tvOrderType.setText("Productive");
            holder.tvOrderType.setBackgroundColor(Color.parseColor("#ff99cc00"));
            //holder.llOrderStamp.setBackgroundResource(R.drawable.circle_back_green);
            holder.tvRevisedOrder.setVisibility(View.VISIBLE);
            holder.tvCancelOrder.setVisibility(View.VISIBLE);
            holder.tvNewRetailer.setVisibility(View.GONE);
        }

        holder.tvCancelOrder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {


                try {

                    String rid = visitedRetailerList.get(position).getRetailerId();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            rid, checkInTime, "Order Cancelled", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    ArrayList<String> distributorList = orderPlacedForDistributorList.get(position);
                    showCanelConfirmationDialog(rid, distributorList);

                    Bundle params = new Bundle();
                    params.putString("Action", "Cancel Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("VisitedRetailerList", params);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        holder.tvRevisedOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "Revised Order");
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        visitedRetailerList.get(position).getRetailerId(), checkInTime, "Order Revised", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

//                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());
                Intent intent = new Intent(context, RetailerOrderActivity.class);
                intent.putExtra("retName", visitedRetailerList.get(position).getRetailerName());
                intent.putExtra("rid", visitedRetailerList.get(position).getRetailerId());
                intent.putExtra("tabPosition", 1);
                intent.putExtra("orderType", "revise order");
                intent.putExtra("checkInTime", checkInTime);
                intent.putExtra("if", holder.tvFrom.getText().toString());
                context.startActivity(intent);
                //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        holder.retailerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "Retailer Icon");
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        checkInTime, "Retailer and shop images clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                showImagePager(position);

            }
        });

        holder.callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "Call Button");
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                String phn = visitedRetailerList.get(position).getRetailerPhone();

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        checkInTime, "Retailer Phone Calling Clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                if (phn != null && !phn.isEmpty() && !phn.equalsIgnoreCase("null")) {

                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phn));

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(intent);
                    }

                } else {
                    Toast.makeText(context, "Number not available", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.locationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "Map Button");
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        checkInTime, "Retailer Location Clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                try {

                    double lat2 = Double.parseDouble(visitedRetailerList.get(position).getLatitude());
                    double longt2 = Double.parseDouble(visitedRetailerList.get(position).getLongtitude());

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
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        checkInTime, "Retailer Info Clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                Intent intent = new Intent(context, UpdateRetailersActivity.class);
                intent.putExtra("retailer", visitedRetailerList.get(position));
                context.startActivity(intent);
                ((Activity) context).finish();
                //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        holder.feedBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "Feedback Button");
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        checkInTime, "Retailer feedback clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                String rid = visitedRetailerList.get(position).getRetailerId();
                Intent intent = new Intent(context, RetailersFeedBack.class);
                intent.putExtra("id", rid);
                intent.putExtra("from", "retailer");
                context.startActivity(intent);

            }
        });

        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle params = new Bundle();
                params.putString("Action", "More Button");
                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                firebaseAnalytics.logEvent("VisitedRetailerList", params);

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                String[] temp = checkInTime.split(" ");

                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                        checkInTime, "Retailer more clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                        String.valueOf(locationProvider.getLongitude()));

                showDialog(view, holder.imgMore, visitedRetailerList.get(position).getRetailerId(), position);

            }
        });

    }

    private void showImagePager(int position) {

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Dialog dialog = new Dialog(context, R.style.DialogTheme);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.dimAmount = 0.75f;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(R.layout.outlet_image_dialog);
        final ViewPager roomPager = (ViewPager) dialog.findViewById(R.id.viewPagerShopImage);
        final TextView txtvCurrentRoom = (TextView) dialog.findViewById(R.id.tvCurrentImage);
        final TextView txtvTotalRoom = (TextView) dialog.findViewById(R.id.tvTotalImage);

        String[] r_img = visitedRetailerList.get(position).getRetailer_image().split(",");
        ShopImagePagerAdapter imagePagerAdapter = new ShopImagePagerAdapter(context, r_img);

        roomPager.setAdapter(imagePagerAdapter);
        txtvTotalRoom.setText(String.valueOf(r_img.length));
        txtvCurrentRoom.setText("1");

        roomPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    position++;
                    txtvCurrentRoom.setText(String.valueOf(position));

                } else {
                    position++;
                    txtvCurrentRoom.setText(String.valueOf(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dialog.show();
    }

    private String getTimeInAMPM(String timeStamp) {

        try {
            String[] temp = timeStamp.split(" ");
            final String time = temp[1];

            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            return new SimpleDateFormat("kk:mm a").format(dateObj);
        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        }

        return "";
    }

    private void showCanelConfirmationDialog(final String rid, final ArrayList<String> distributorList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Alret!");
        builder.setMessage("Do you really want to cancel order?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.e("SIZE DIS", "==" + distributorList.size());
                if (distributorList.size() == 1) {

                    cancelOrder(rid, distributorList.get(0));

                } else {

                    showDistributorListDailog(rid, distributorList);
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        Dialog dialog = builder.create();

        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDistributorListDailog(final String rid, ArrayList<String> distributorList) {

        final Dialog disListDialog = new Dialog(context);
        disListDialog.setContentView(R.layout.order_cancellation_dialog);
        disListDialog.setCancelable(false);
        disListDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout checkBoxContainer = (LinearLayout) disListDialog.findViewById(R.id.checkBoxContainer);
        LinearLayout llCancelOrder = (LinearLayout) disListDialog.findViewById(R.id.llCancelOrder);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        CheckBox chbDisList = null;
        TextView tvId = null;
        final ArrayList<String> idList = new ArrayList<>();
        final ArrayList<String> reasonList = new ArrayList<>();
        for (int i = 0; i < distributorList.size(); i++) {

            Cursor dis = salesBeatDb.getDistributorName(distributorList.get(i));
            dis.moveToFirst();
            String disName = dis.getString(dis.getColumnIndex("distributor_name"));
            chbDisList = new CheckBox(context);
            chbDisList.setTextAppearance(context, R.style.MyCheckBox);
            chbDisList.setText(disName);
            chbDisList.setLayoutParams(params);


            tvId = new TextView(context);
            tvId.setText(distributorList.get(i));
            tvId.setLayoutParams(params);
            tvId.setVisibility(View.GONE);

            final TextView finalTvId1 = tvId;
            chbDisList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {

                        idList.add(finalTvId1.getText().toString());
                        cancelOrder2(reasonList);

                    } else {
                        idList.remove(finalTvId1.getText().toString());
                    }
                }
            });

            checkBoxContainer.addView(chbDisList);
            checkBoxContainer.addView(tvId);
        }


        llCancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                for (int i = 0; i < idList.size(); i++) {

                    String transactionId = prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_"
                            + String.valueOf(Calendar.getInstance().getTimeInMillis());

                    salesBeatDb.updateInOderPlacedByRetailersTable(rid, idList.get(i), "", "cancelled", "",
                            "", "", reasonList.get(i), transactionId, date);

                }

                Intent intent = new Intent(context, RetailerActivity.class);
                intent.putExtra("tabPosition", 1);
                context.startActivity(intent);
                ((Activity) context).finish();

                disListDialog.dismiss();
            }
        });

        disListDialog.show();

    }

    public void showDialog(View view, ImageView imgMore, final String rid, final int position) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), imgMore);

        ((Activity) context).getMenuInflater().inflate(R.menu.visited_more_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.visitHistory) {

                new PingServer(internet -> {
                    /* do something with boolean response */
                    if (!internet) {
                        Toast.makeText(context, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                    } else {
                        if (utilityClass.isInternetConnected()) {

                            Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                            intent.putExtra("rid", rid);
                            intent.putExtra("name", visitedRetailerList.get(position).getRetailerName());
                            intent.putExtra("from", "retailer");
                            context.startActivity(intent);

                        } else {
                            Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                        }
                    }

                });


            } else if (item.getItemId() == R.id.getErrorInfo) {

                showErrorInfoDialog(
                        visitedRetailerList.get(position).getServerStatusCode(),
                        visitedRetailerList.get(position).getErrorMsg()
                );
            } else if (item.getItemId() == R.id.syncNow) {
                Log.e("Testing", "" + visitedRetailerList.get(position)
                        .getOrderType());
                if (utilityClass.isInternetConnected()) {
                    if (visitedRetailerList.get(position)
                            .getOrderType().equalsIgnoreCase("productive")) {
                        syncExistingRetailrsProductiveOrder(visitedRetailerList.get(position).getRetailerId(), position);
                    } else if (visitedRetailerList.get(position)
                            .getOrderType().equalsIgnoreCase("no order")) {
                        syncExistingRetailrsNonProductiveOrder(visitedRetailerList.get(position).getRetailerId(), position);
                    } else if (visitedRetailerList.get(position)
                            .getOrderType().equalsIgnoreCase("new productive")) {
                        syncNewRetailrs(visitedRetailerList.get(position).getRetailerId(),
                                position, "new productive");
                    } else if (visitedRetailerList.get(position)
                            .getOrderType().equalsIgnoreCase("no order new")) {
                        syncNewRetailrs(visitedRetailerList.get(position).getRetailerId(),
                                position, "no order new");
                    } else {
                        syncExistingRetailrsProductiveOrder(visitedRetailerList.get(position).getRetailerId(), position);
                    }
                } else {
                    Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }

            return false;
        });

        popupMenu.show();

    }


    private void syncNewRetailrs(String retailerId, int position, String type) {

        Cursor newRetailerCursor = null;

        try {

            //submit new  retailers to server
            newRetailerCursor = salesBeatDb.getSpecificDataFromNewRetailerListTable(retailerId);
            if (newRetailerCursor != null && newRetailerCursor.getCount() > 0 && newRetailerCursor.moveToFirst()) {

                String new_rid = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("nrid"));
                String did = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("distributor_id"));
                String shop_name = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_name"));
                String shop_address = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_address"));
                String shop_phone = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_phone"));
                String owner_name = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_owner_name"));
                String owner_mobile_no = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_owner_phone"));
                String whatsappNo = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_whatsapp_no"));
                String lat = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_latitude"));
                String longt = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_longtitude"));
                String state = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_state"));
                String zone = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_zone"));
                String locality = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_locality"));
                String district = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_district"));
                String pincode = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_pin"));
                String email_id = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_email"));
                String gstin = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_gstin"));
                String target = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_target"));
                String fssai_no = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_fssai"));
                String grade = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_retailer_grade"));
                String outlet_channel = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_outletchannel"));
                String shop_type = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_type"));
                String owner_image = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_owner_image"));
                String image_time_stamp = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("image_time_stamp"));
                String bid = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("beat_id"));
                String shop_image1 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image1"));
                String shop_image2 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image2"));
                String shop_image3 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image3"));
                String shop_image4 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image4"));
                //String shop_image5 = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("new_shop_image5"));
                String transactionId = newRetailerCursor.getString(newRetailerCursor.getColumnIndex("transactionId"));

                addNewRetailerToServer(new_rid, did, shop_name, shop_address, shop_phone, owner_name, owner_mobile_no,
                        whatsappNo, lat, longt, state, zone, locality, district, pincode, email_id, gstin, target, fssai_no,
                        grade, outlet_channel, shop_type, owner_image, image_time_stamp, shop_image1, shop_image2, shop_image3,
                        shop_image4, bid, transactionId, position, type);
            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (newRetailerCursor != null)
                newRetailerCursor.close();
        }

    }

    private void addNewRetailerToServer(String new_rid, String did, String shop_name,
                                        String shop_address, String shop_phone, String owner_name,
                                        String owner_mobile_no, String whatsappNo, String lat,
                                        String longt, String state, String zone, String locality,
                                        String district, String pincode, String email_id, String gstin,
                                        String target, String fssai_no, String grade, String outlet_channel,
                                        String shop_type, String owner_image, String image_time_stamp,
                                        String shop_image1, String shop_image2, String shop_image3,
                                        String shop_image4, String bid, String transactionId,
                                        int position, String type) {

        final Dialog loader = new Dialog(context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

//        final HashMap<String, Object> newRetailer = new HashMap<>();
//
//        try {
//
//            newRetailer.put("shop_name", shop_name);
//            newRetailer.put("shop_address", shop_address);
//
//            if (!shop_phone.isEmpty())
//                newRetailer.put("shop_phone", shop_phone);
//
//            newRetailer.put("owner_name", owner_name);
//
//            if (!owner_mobile_no.isEmpty())
//                newRetailer.put("owner_mobile_no", owner_mobile_no);
//
//            if (!whatsappNo.isEmpty())
//                newRetailer.put("whatsappNo", whatsappNo);
//
//            newRetailer.put("state", state);
//            newRetailer.put("zone", zone);
//            newRetailer.put("locality", locality);
//            newRetailer.put("district", district);
//            newRetailer.put("pincode", pincode);
//
//            if (!email_id.isEmpty())
//                newRetailer.put("email_id", email_id);
//
//            newRetailer.put("gstin", gstin);
//            newRetailer.put("target", target);
//            newRetailer.put("fssai_no", fssai_no);
//            newRetailer.put("grade", grade);
//            newRetailer.put("outlet_channel", outlet_channel);
//            newRetailer.put("shop_type", shop_type);
//            newRetailer.put("image_time_stamp", image_time_stamp);
//            newRetailer.put("latitude", lat);
//            newRetailer.put("longitude", longt);
//            newRetailer.put("bid", bid);
//            newRetailer.put("addedOn", image_time_stamp);
//            newRetailer.put("transactionId", transactionId);
//
//
//            Log.e(TAG, "New Retailer Json: " + newRetailer.toString());
//
//            if (owner_image != null && !owner_image.isEmpty() && compressImage(owner_image) != null)
//                newRetailer.put("image", getStringImage(compressImage(owner_image)));
//
//            if (shop_image1 != null && !shop_image1.isEmpty() && compressImage(shop_image1) != null)
//                newRetailer.put("shop_image[0]", getStringImage(compressImage(shop_image1)));
//
//            if (shop_image2 != null && !shop_image2.isEmpty() && compressImage(shop_image2) != null)
//                newRetailer.put("shop_image[1]", getStringImage(compressImage(shop_image2)));
//
//            if (shop_image3 != null && !shop_image3.isEmpty() && compressImage(shop_image3) != null)
//                newRetailer.put("shop_image[2]", getStringImage(compressImage(shop_image3)));
//
//            if (shop_image4 != null && !shop_image4.isEmpty() && compressImage(shop_image4) != null)
//                newRetailer.put("shop_image[3]", getStringImage(compressImage(shop_image4)));
//
//            Call newretailerCall = apiIntentface.addNewRetailerToServer(
//                    prefSFA.getString("token", ""),
//                    newRetailer);
//
//            // Retrofit
//            newretailerCall.enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
//
//                    loader.dismiss();
//                    Log.e(TAG, "Response add New Retailer: " + response.body());
//
//                    if (response.isSuccessful()) {
//                        try {
//
//                            assert response.body() != null;
//                            JSONObject jsonObject = new JSONObject(response.body().toString());
//
//                            String nrid = jsonObject.getString("rid");
//                            String status = jsonObject.getString("status");
//
//                            if (status.equalsIgnoreCase("success")) {
//
//                                boolean val1 = salesBeatDb.updateNewRetailerListTable(new_rid, nrid, "success");
//                                Log.e(TAG, "New Retailer: " + val1);
//                                if (val1) {
//                                    deleteLocalFile(owner_image, shop_image1, shop_image2,
//                                            shop_image3, shop_image4);
//
//                                    if (type.equalsIgnoreCase("new productive"))
//                                        syncNewRetailrsProductiveOrder(nrid, position);
//                                    else
//                                        syncNewRetailrsNonProductiveOrder(nrid, position);
//
//                                }
//
//                            } else {
//                                Toast.makeText(context, response.code() +
//                                        " " + response.message(), Toast.LENGTH_SHORT).show();
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Toast.makeText(context,
//                                    " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(context, response.code() +
//                                " " + response.message(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    loader.dismiss();
//                    Toast.makeText(context,
//                            " " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//        } catch (Exception e) {
//            loader.dismiss();
//            e.printStackTrace();
//            Toast.makeText(context,
//                    " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }

        //final HashMap<String, Object> newRetailer = new HashMap<>();

        JSONObject newRetailer = new JSONObject();

        try {

            newRetailer.put("shop_name", shop_name);
            //@Umesh
            newRetailer.put("name", shop_name);
            newRetailer.put("address", shop_address);

            if (!shop_phone.isEmpty())
                newRetailer.put("shopPhone", shop_phone);

            newRetailer.put("ownersName", owner_name);

            if (!owner_mobile_no.isEmpty())
                newRetailer.put("ownersPhone1", owner_mobile_no);

            if (!whatsappNo.isEmpty())
                newRetailer.put("whatsappNo", whatsappNo);

            newRetailer.put("state", state);
            // saveNewRetailerDetailsParams.put("zone", zone);
            newRetailer.put("locality", locality);
            newRetailer.put("district", district);
            newRetailer.put("pin", pincode);

            if (!email_id.isEmpty())
                newRetailer.put("email", email_id);

            newRetailer.put("gstin", gstin);
            newRetailer.put("target", target);
            newRetailer.put("fssai", fssai_no);
            newRetailer.put("grade", grade);
            newRetailer.put("outletChannel", outlet_channel);
            newRetailer.put("shopType", shop_type);
            //newRetailer.put("image_time_stamp", imageTimeStamp);
            newRetailer.put("latitude", lat);
            newRetailer.put("longitude", longt);
            newRetailer.put("bid", Integer.valueOf(bid));
//            newRetailer.put("addedOn", imageTimeStamp);
            // newRetailer.put("addedOnstr", imageTimeStamp);
            newRetailer.put("transactionId", transactionId);


            Log.e(TAG, "New Retailer Json: " + newRetailer.toString());
//@Umesh
            if (owner_image != null && !owner_image.isEmpty()) {
                File compressedImageFile = compressImage(owner_image);
                if (compressedImageFile != null) {
                    // Convert the File to a Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedImageFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    newRetailer.put("image", getStringImage(compressedBitmap));
                }
            }

            if (shop_image1 != null && !shop_image1.isEmpty()) {
                File compressedImageFile = compressImage(shop_image1);
                if (compressedImageFile != null) {
                    // Convert the File to a Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedImageFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    newRetailer.put("image", getStringImage(compressedBitmap));
                }
            }

            /*if (shop_image1 != null && !shop_image1.isEmpty() && compressImage(shop_image1) != null)
                newRetailer.put("ShopImage0", getStringImage(compressImage(shop_image1)));*/

            if (shop_image2 != null && !shop_image2.isEmpty()) {
                File compressedImageFile = compressImage(shop_image2);
                if (compressedImageFile != null) {
                    // Convert the File to a Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedImageFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    newRetailer.put("image", getStringImage(compressedBitmap));
                }
            }

            /*if (shop_image2 != null && !shop_image2.isEmpty() && compressImage(shop_image2) != null)
                newRetailer.put("ShopImage1", getStringImage(compressImage(shop_image2)));*/

            if (shop_image3 != null && !shop_image3.isEmpty()) {
                File compressedImageFile = compressImage(shop_image3);
                if (compressedImageFile != null) {
                    // Convert the File to a Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedImageFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    newRetailer.put("image", getStringImage(compressedBitmap));
                }
            }

           /* if (shop_image3 != null && !shop_image3.isEmpty() && compressImage(shop_image3) != null)
                newRetailer.put("ShopImage2", getStringImage(compressImage(shop_image3)));*/

            if (shop_image4 != null && !shop_image4.isEmpty()) {
                File compressedImageFile = compressImage(shop_image4);
                if (compressedImageFile != null) {
                    // Convert the File to a Bitmap
                    Bitmap compressedBitmap = BitmapFactory.decodeFile(compressedImageFile.getAbsolutePath());

                    // Now pass the Bitmap to getStringImage
                    newRetailer.put("image", getStringImage(compressedBitmap));
                }
            }

           /* if (shop_image4 != null && !shop_image4.isEmpty() && compressImage(shop_image4) != null)
                newRetailer.put("ShopImage3", getStringImage(compressImage(shop_image4)));*/


            JsonObjectRequest addNewRetailerReq = new JsonObjectRequest(Request.Method.POST,
                    SbAppConstants.ADD_NEW_RETAILER, newRetailer, response -> {

                loader.dismiss();
                Log.e(TAG, "No order From new Retailer Response: " + response.toString());
                try {
                //@Umesh 23-Feb-2022
                if(response.getInt("status")==1)
                {
                       JSONObject data = response.getJSONObject("data");
                        String nrid = data.getString("rid");
                        if(response.getInt("status")==1)
                        {
                            boolean val1 = salesBeatDb.updateNewRetailerListTable(new_rid, nrid, "success");
                            Log.e(TAG, "New Retailer: " + val1);
                            if (val1) {
                                deleteLocalFile(owner_image, shop_image1, shop_image2,
                                        shop_image3, shop_image4);

                                if (type.equalsIgnoreCase("new productive"))
                                    syncNewRetailrsProductiveOrder(nrid, position);
                                else
                                    syncNewRetailrsNonProductiveOrder(nrid, position);

                            }


                        } else {
                            Toast.makeText(context, "" + response.toString(), Toast.LENGTH_SHORT).show();
                            try {
                                SbLog.printError("Add New Retailer",
                                        "addRetailer", "Not available",
                                        "" + response.toString(), prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                loader.dismiss();
                error.printStackTrace();
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json");
                    headers.put("authorization", prefSFA.getString("token", ""));
                    return headers;
                }
            };

            addNewRetailerReq.setRetryPolicy(new DefaultRetryPolicy(
                            50000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                    )
            );

            Volley.newRequestQueue(context).add(addNewRetailerReq);

        } catch (Exception e) {
            loader.dismiss();
            e.printStackTrace();
            Toast.makeText(context,
                    " " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void syncNewRetailrsNonProductiveOrder(String nrid, int position) {
        Cursor noOrderC = null;
        try {

            noOrderC = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable22(nrid);

            if (noOrderC != null && noOrderC.getCount() > 0 && noOrderC.moveToFirst()) {

//                do {

                    final String rid = noOrderC.getString(noOrderC.getColumnIndex("nrid"));
                    String did = noOrderC.getString(noOrderC.getColumnIndex("distributor_id"));
                    String checkIn = noOrderC.getString(noOrderC.getColumnIndex("new_check_in_time"));
                    String checkOut = noOrderC.getString(noOrderC.getColumnIndex("new_check_in_time"));
                    String lat = noOrderC.getString(noOrderC.getColumnIndex("new_order_lat"));
                    String longt = noOrderC.getString(noOrderC.getColumnIndex("new_order_long"));
                    String reason = noOrderC.getString(noOrderC.getColumnIndex("new_order_comment"));
                    String transactionId = noOrderC.getString(noOrderC.getColumnIndex("transactionId"));


                    try {

                        JSONObject callDetails = new JSONObject();
                        callDetails.put("rid", rid);
                        callDetails.put("did", did);
                        //callDetails.put("checkIn", checkIn);
                        //callDetails.put("checkOut", checkOut);
                        callDetails.put("checkInstr",checkIn); //Umesh
                        callDetails.put("checkOutstr",checkOut); //Umesh
                        callDetails.put("latitude", lat);
                        callDetails.put("longitude", longt);
                        callDetails.put("comments", reason);

                        JSONArray retailerOutCalls = new JSONArray();
                        retailerOutCalls.put(callDetails);

                        JSONObject orders = new JSONObject();
                        orders.put("retailerCalls", retailerOutCalls);
                        orders.put("transactionId", transactionId);

                        JSONObject nonProductiveCallParams = new JSONObject();
                        nonProductiveCallParams.put("retailerCalls", retailerOutCalls);
                        nonProductiveCallParams.put("transactionId", transactionId);

                        Log.e(TAG, "No order From new Retailer json: " + orders.toString());

                        JsonObjectRequest nonProductiveCallRequest = new JsonObjectRequest(Request.Method.POST,
                                SbAppConstants.SUBMIT_ORDER, nonProductiveCallParams, response -> {

                            Log.e(TAG, "No order From new Retailer Response: " + response.toString());
                            try {
                            //@Umesh 23-Feb-2022
                            if(response.getInt("status")==1)
                            {
                                if(response.getInt("status")==1)
                                    {
                                        boolean val1 = salesBeatDb.updateNewRetailerListTable2(nrid,
                                                did, "success");
                                        if (val1) {
                                            Toast.makeText(context, "Synced successfully", Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();
                                            visitedRetailerList.get(position).setServerStatus("success");
                                        }

                            } else {
                                try {
//                                    SbLog.printError("Existing Retailer Non Productive Call",
//                                            "submitOrders", "Not available",
//                                            "" + response.toString(), prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                                    Toast.makeText(context, "Not available", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }, error -> {
//                            boolean val = salesBeatDb.updateInOderPlacedByRetailersTable22(rid, did,
//                                    "error", String.valueOf(error.networkResponse.statusCode),
//                                    "" + error.getMessage());
                        }) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Accept", "application/json");
                                headers.put("authorization", prefSFA.getString("token", ""));
                                return headers;
                            }
                        };

                        nonProductiveCallRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        50000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                                )
                        );

                        Volley.newRequestQueue(context).add(nonProductiveCallRequest);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

//                } while (noOrderC.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (noOrderC != null)
                noOrderC.close();
        }
    }

    private void syncNewRetailrsProductiveOrder(String nrid, int position) {
        Cursor orderPlacedBy = null;
        try {

            //submit new retailers order to server
            orderPlacedBy = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable222(nrid);
            if (orderPlacedBy != null && orderPlacedBy.getCount() > 0 && orderPlacedBy.moveToFirst()) {

//                do {

                // String nrid = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("nrid"));
                String did = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("distributor_id"));
                String orderTakenAt = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_taken_at"));
                String orderType = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_type"));
                String checkIn = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_check_in_time"));
                String checkOut = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_check_out_time"));
                String lat = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_lat"));
                String longt = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_long"));
                String cmnt = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("new_order_comment"));
                String transactionId = orderPlacedBy.getString(orderPlacedBy.getColumnIndex("transactionId"));

                if (cmnt.equalsIgnoreCase("new productive")) {

                    submitOrderFromNewRetailersToServer(nrid, did, orderTakenAt, orderType, checkIn,
                            checkOut, lat, longt, transactionId/*, prevNRid, prevNCheckInT*/, position);


                } else {

                    submitNoOrderFromNewRetailerToServer(nrid, did, transactionId, position);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (orderPlacedBy != null)
                orderPlacedBy.close();
        }
    }

    private void submitOrderFromNewRetailersToServer(final String nridSaved, final String did, String taken_at, String ordertype,
                                                     String checkInT, String checkOutT, String lat, String longt,
                                                     String transactionId, int position/*, String prevNRid, String prevNCheckInT*/) {

        final Dialog loader = new Dialog(context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        List<HashMap> catalogue = new ArrayList<>();
//        JSONArray catalogue = new JSONArray();

        Cursor cursorOrderList = salesBeatDb.getSpecificDataFromNewOrderEntryListTable(nridSaved, did
                /*,"success", "fail"*/);

        try {

            // Log.e("Here","u are R9 cur--->"+cursorOrderList.getCount()+"  "+nridSaved+"  "+did);

            if (cursorOrderList != null && cursorOrderList.getCount() > 0 && cursorOrderList.moveToFirst()) {

                do {

                    HashMap<String, Object> jObjectInput = new HashMap<>();
//                    JSONObject jObjectInput = new JSONObject();
                    jObjectInput.put("skuid", cursorOrderList.getString(cursorOrderList.getColumnIndex("new_sku_id")));
                    jObjectInput.put("qty", cursorOrderList.getString(cursorOrderList.getColumnIndex("new_brand_qty")));
                    catalogue.add(jObjectInput);

                } while (cursorOrderList.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            loader.dismiss();
        } finally {
            if (cursorOrderList != null)
                cursorOrderList.close();
        }

        if (catalogue.size() > 0) {

            final HashMap<String, Object> ordersData = new HashMap<>();
//            final JSONObject ordersData = new JSONObject();

            try {

                HashMap<String, Object> values1 = new HashMap<>();
//                JSONObject values1 = new JSONObject();
                values1.put("cid", prefSFA.getString(context.getString(R.string.cmny_id), ""));
                values1.put("rid", nridSaved);
                values1.put("orderType", ordertype);
                values1.put("eid", prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                values1.put("did", did);
                values1.put("takenAt", taken_at);
                values1.put("latitude", lat);
                values1.put("longitude", longt);
//                values1.put("catalogue", catalogue);
                values1.put("orderCatalogue", catalogue);

                List<HashMap> orders = new ArrayList<>();
//                JSONArray orders = new JSONArray();
                orders.add(values1);

                HashMap<String, Object> values2 = new HashMap<>();
//                JSONObject values2 = new JSONObject();
                values2.put("rid", nridSaved);
                values2.put("checkIn", checkInT);
                values2.put("checkOut", checkOutT);
                values2.put("latitude", lat);
                values2.put("longitude", longt);
                values2.put("comments", "");

                List<HashMap> retailerCalls = new ArrayList<>();
//                JSONArray retailerCalls = new JSONArray();
                retailerCalls.add(values2);

                ordersData.put("orders", orders);
                ordersData.put("retailerCalls", retailerCalls);

                ordersData.put("transactionId", transactionId + did);

            } catch (Exception e) {
                loader.dismiss();
                e.printStackTrace();
            }

            Log.e(TAG, "New retailer order json: " + ordersData.toString());

            try {

                Call<JsonObject> callSubmitOrder = apiIntentface.submitOrder(
                        prefSFA.getString("token", ""),
                        ordersData
                );

                // Retrofit
                callSubmitOrder.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                        Log.e(TAG, "Response new retailer order: " + response.body());
                        loader.dismiss();
                        if (response.isSuccessful()) {

                            try {

                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                //@Umesh
                                if(jsonObject.getInt("status")==1)
                                {

                                    boolean val1 = salesBeatDb.updateNewRetailerListTable2(nridSaved, did, "success");
                                    Log.e(TAG, "New Retailer Order status: " + val1);
                                    if (val1) {
                                        Toast.makeText(context, "Synced successfully", Toast.LENGTH_SHORT).show();
                                        notifyDataSetChanged();
                                        visitedRetailerList.get(position).setServerStatus("success");
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            Toast.makeText(context, Thread.currentThread().getStackTrace()[1].getLineNumber()+":"+response.code() + "" + response.message(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        loader.dismiss();
                        Log.e(TAG, "New Retailer Order error: " + t.getMessage());
                        Toast.makeText(context, "" + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                loader.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            loader.dismiss();
            Toast.makeText(context, "No data to sync", Toast.LENGTH_SHORT).show();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void submitNoOrderFromNewRetailerToServer(final String nrid, final String did,
                                                      String transactionId, int position) {

        final Dialog loader = new Dialog(context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();
        //Log.d("Submit no order", "start");
        Cursor noOrderC = null;
        try {

            noOrderC = salesBeatDb.getSpecificNewRetailersFromOderPlacedByNewRetailersTable22(nrid);

            if (noOrderC != null && noOrderC.getCount() > 0 && noOrderC.moveToFirst()) {

//                do {

                final String rid = noOrderC.getString(noOrderC.getColumnIndex("nrid"));
                String checkIn = noOrderC.getString(noOrderC.getColumnIndex("new_check_in_time"));
                String checkOut = noOrderC.getString(noOrderC.getColumnIndex("new_check_in_time"));
                String lat = noOrderC.getString(noOrderC.getColumnIndex("new_order_lat"));
                String longt = noOrderC.getString(noOrderC.getColumnIndex("new_order_long"));
                String reason = noOrderC.getString(noOrderC.getColumnIndex("new_order_comment"));

                HashMap<String, Object> jsonObject = new HashMap<>();
//                    JSONObject jsonObject = new JSONObject();
                jsonObject.put("rid", rid);
                jsonObject.put("checkIn", checkIn);
                jsonObject.put("checkOut", checkOut);
                jsonObject.put("latitude", lat);
                jsonObject.put("longitude", longt);
                jsonObject.put("comments", reason);

                List<HashMap> retailerOutCalls = new ArrayList<>();
//                    JSONArray retailerOutCalls = new JSONArray();
                retailerOutCalls.add(jsonObject);

//                    final JSONObject orders = new JSONObject();
                final HashMap<String, Object> orders = new HashMap<>();
                orders.put("retailerCalls", retailerOutCalls);
                orders.put("transactionId", transactionId + did);

                Log.e(TAG, "No order From new Retailer json: " + orders.toString());

                Call<JsonObject> callSubmitOrder = apiIntentface.submitOrder(
                        prefSFA.getString("token", ""),
                        orders
                );

                // Retrofit
                callSubmitOrder.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                        Log.e(TAG, "Response NO Order new retailer: " + response.body());
                        loader.dismiss();
                        if (response.isSuccessful()) {

                            try {

                                JSONObject jsonObject1 = new JSONObject(response.body().toString());

                                //String status = jsonObject1.getString("status");
                                //String msg = response.getString("statusMessage");
                                //@Umesh
                                if(jsonObject1.getInt("status")==1)
                                {

                                    boolean val1 = salesBeatDb.updateNewRetailerListTable2(nrid, did, "success");
                                    Log.e(TAG, "New Retailer No Order status: " + val1);
                                    if (val1) {
                                        Toast.makeText(context, "Synced successfully", Toast.LENGTH_SHORT).show();
                                        visitedRetailerList.get(position).setServerStatus("success");
                                        notifyDataSetChanged();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            Toast.makeText(context, Thread.currentThread().getStackTrace()[1].getLineNumber()+":"+response.code() + "" + response.message(),
                                    Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        loader.dismiss();
                        Log.e(TAG, "New Retailer No Order error: " + t.getMessage());
                        Toast.makeText(context, "" + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

//                } while (noOrderC.moveToNext());
            }

        } catch (Exception e) {
            loader.dismiss();
            Log.e(TAG, "==" + e.getMessage());
            Toast.makeText(context, "" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        } finally {
            if (noOrderC != null)
                noOrderC.close();
        }
    }


    /*private Bitmap compressImage(String filePath) {

        try {

            Log.e(TAG, "===>" + filePath);

            return new Compressor(context)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(75)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(new File(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

    public static File compressImage(String filePath) {
        try {
            // Load image dimensions without loading full bitmap into memory
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;

            // Compute the scale factor
            int inSampleSize = 1;
            while (originalWidth / inSampleSize > 640 || originalHeight / inSampleSize > 480) {
                inSampleSize *= 2;
            }

            // Load the scaled-down image
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            if (bitmap == null) return null;

            // Resize bitmap to exactly 640x480
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, true);

            // Create output file
            File compressedFile = new File(filePath.replace(".jpg", "_compressed.jpg"));
            FileOutputStream fileOutputStream = new FileOutputStream(compressedFile);

            // Compress image (JPEG, 75% quality)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

            // Recycle bitmaps to free memory
            bitmap.recycle();
            resizedBitmap.recycle();

            return compressedFile; // Return the compressed image file

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private String getStringImage(Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encodedImage;

    }

    private void deleteLocalFile(String ownerImage, String shopImage1, String shopImage2,
                                 String shopImage3, String shopImage4) {

        File file, file1, file2, file3, file4;
        boolean temp = false;

        try {

            if (!ownerImage.isEmpty()) {
                file = new File(ownerImage);
                if (file.exists())
                    temp = file.delete();
            }

            if (!shopImage1.isEmpty()) {
                file1 = new File(shopImage1);
                if (file1.exists())
                    temp = file1.delete();
            }

            if (!shopImage2.isEmpty()) {
                file2 = new File(shopImage2);
                if (file2.exists())
                    temp = file2.delete();
            }

            if (!shopImage3.isEmpty()) {
                file3 = new File(shopImage3);
                if (file3.exists())
                    temp = file3.delete();
            }

            if (!shopImage4.isEmpty()) {
                file4 = new File(shopImage4);
                if (file4.exists())
                    temp = file4.delete();
            }

            Log.e(TAG, " File deleted:" + temp);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //return temp;
    }

    private void syncExistingRetailrsNonProductiveOrder(String retailerId, int position) {
        Cursor noOrderC = null;

        try {

            //submit no orders from existing retailers to server
            noOrderC = salesBeatDb.getOrderTypeFromOderPlacedByRetailersTable(retailerId);
            if (noOrderC != null && noOrderC.getCount() > 0 && noOrderC.moveToFirst()) {

                String rid = noOrderC.getString(noOrderC.getColumnIndex("rid"));
                String did = noOrderC.getString(noOrderC.getColumnIndex("did"));
                String checkIn = noOrderC.getString(noOrderC.getColumnIndex("check_in_time"));
                String checkOut = noOrderC.getString(noOrderC.getColumnIndex("check_out_time"));
                String lat = noOrderC.getString(noOrderC.getColumnIndex("order_lat"));
                String longt = noOrderC.getString(noOrderC.getColumnIndex("order_long"));
                String reason = noOrderC.getString(noOrderC.getColumnIndex("order_comment"));
                String transactionId = noOrderC.getString(noOrderC.getColumnIndex("transactionId"));

                submitNoOrderToServer(rid, did, checkIn, checkOut, lat, longt,
                        reason, transactionId, position);

            }

        } catch (Exception e) {
            Log.e(TAG, "==" + e.getMessage());
        } finally {

            if (noOrderC != null)
                noOrderC.close();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void submitNoOrderToServer(final String rid, final String did, String checkIn,
                                       String checkOut, String lat, String longt, String reason,
                                       String transactionId, int position) {

        final Dialog loader = new Dialog(context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        HashMap<String, Object> orderHash = new HashMap<>();
        try {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("rid", Integer.parseInt(rid));
            hashMap.put("did", Integer.parseInt(did));
//            hashMap.put("checkIn", checkIn);
//            hashMap.put("checkOut", checkOut);
            hashMap.put("checkInstr", checkIn);//Umesh
            hashMap.put("checkOutstr", checkOut); //Umesh
            hashMap.put("latitude", lat);
            hashMap.put("longitude", longt);
            hashMap.put("comments", reason);

            List<HashMap> retailerOutCalls = new ArrayList<>();
            retailerOutCalls.add(hashMap);

            orderHash.put("orders", new ArrayList<>());
            orderHash.put("retailerCalls", retailerOutCalls);
            orderHash.put("transactionId", transactionId);

        } catch (Exception e) {
            loader.dismiss();
            e.printStackTrace();
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        Log.e(TAG, "No order from Existing retailer Json: " + orderHash.toString());


        Call<JsonObject> callNoOrder = apiIntentface.submitOrder(
                prefSFA.getString("token", ""),
                orderHash
        );

        // Retrofit
        callNoOrder.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                Log.e(TAG, "Response NO Order existing retailer: " + response.toString());
                loader.dismiss();
                if (response.isSuccessful())
                {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        try {
                            //@Umesh
                            if(jsonObject.getInt("status")==1)
                            {
                                boolean val = salesBeatDb.updateInOderPlacedByRetailersTable2(rid, "success", did);
                                if (val) {
                                    visitedRetailerList.get(position).setServerStatus("success");
                                    Toast.makeText(context, "Synced successfully", Toast.LENGTH_SHORT).show();
                                    notifyDataSetChanged();
                                }
                            }
                            else
                            {
                                Toast.makeText(context,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(context,jsonObject.getString("message") , Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                } else {
                    Toast.makeText(context, Thread.currentThread().getStackTrace()[1].getLineNumber()+":"+response.code() + "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                loader.dismiss();
                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void syncExistingRetailrsProductiveOrder(String retailerId, int position) {

        Cursor orderPlacedByRetailers = null;
        try {

            //submitting existing retailars  orders to server
            orderPlacedByRetailers =
                    salesBeatDb.getOrderTypeFromOderPlacedByRetailersTable(retailerId);

            if (orderPlacedByRetailers != null && orderPlacedByRetailers.getCount() > 0
                    && orderPlacedByRetailers.moveToFirst()) {

                String rid = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("rid"));
                String did = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("did"));
                String orderTakenAt = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("check_in_time"));
                String orderType = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_type"));
                String checkIn = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("check_in_time"));
                String checkOutTime = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("check_out_time"));
                String lat = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_lat"));
                String longt = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("order_long"));
                String transactionId = orderPlacedByRetailers.getString(orderPlacedByRetailers.getColumnIndex("transactionId"));

//                if (orderType.contains("cancelled")) {  //@Umesh
//
//                    orderType = "onShop";
//                }


                submitOrderFromRetailersToServer(rid, did, orderTakenAt, orderType, checkIn,
                        checkOutTime, lat, longt,/* prevRid, prevCheckInT, */transactionId, position);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (orderPlacedByRetailers != null)
                orderPlacedByRetailers.close();
        }
    }

    private void submitOrderFromRetailersToServer(String rid, String did, String orderTakenAt,
                                                  String orderType, String checkInTime, String checkOutTime,
                                                  String lat, String longt, String transactionId,
                                                  int position) {

        final Dialog loader = new Dialog(context, R.style.DialogActivityTheme);
        loader.setContentView(R.layout.loader);
        if (loader.getWindow() != null)
            loader.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loader.show();

        List<HashMap> catalog = new ArrayList<>();
        Cursor catalogList = null;

        try {
            catalogList = salesBeatDb.getSpecificDataFromOrderEntryListTable(rid, did/*, "fail"*/);
            if (catalogList != null && catalogList.getCount() > 0 && catalogList.moveToFirst()) {

                do {

                    HashMap<String, Object> item = new HashMap<>();

                    item.put("skuid", catalogList.getString(catalogList.getColumnIndex("sku_id")));
                    item.put("qty", catalogList.getString(catalogList.getColumnIndex("brand_qty")));
                    catalog.add(item);

                } while (catalogList.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            loader.dismiss();
        } finally {
            if (catalogList != null)
                catalogList.close();
        }

        if (catalog.size() > 0) {



           HashMap<String, Object> params = new HashMap<>();

            HashMap<String, Object> orderArrayItem = new HashMap<>();

            try {

                orderArrayItem.put("cid", 1);
                orderArrayItem.put("rid", Integer.valueOf(rid));
                orderArrayItem.put("orderType", orderType);
                orderArrayItem.put("eid", Integer.valueOf(prefSFA.getString(context.getString(R.string.emp_id_key), "")));
                orderArrayItem.put("did", Integer.valueOf(did));

//                orderArrayItem.put("takenAt",orderTakenAt);
                orderArrayItem.put("takenAtStr",orderTakenAt); //Umesh
                orderArrayItem.put("latitude", lat);
                orderArrayItem.put("longitude", longt);
//                orderArrayItem.put("catalogue", catalog);
                orderArrayItem.put("orderCatalogue", catalog); //Umesh

                List<HashMap> orders = new ArrayList<>();
                orders.add(orderArrayItem);

                HashMap<String, Object> retailerCallsArrayItem = new HashMap<>();
                retailerCallsArrayItem.put("rid", Integer.valueOf(rid));
                retailerCallsArrayItem.put("did", Integer.valueOf(did));
                retailerCallsArrayItem.put("eid", Integer.valueOf(prefSFA.getString(context.getString(R.string.emp_id_key), "")));
//                retailerCallsArrayItem.put("checkIn",checkInTime);
//                retailerCallsArrayItem.put("checkOut",checkOutTime);

                retailerCallsArrayItem.put("checkInstr",checkInTime); //Umesh
                retailerCallsArrayItem.put("checkOutstr",checkOutTime); //Umesh

                retailerCallsArrayItem.put("latitude", lat);
                retailerCallsArrayItem.put("longitude", longt);
                retailerCallsArrayItem.put("comments", "");

                List<HashMap> retailerCalls = new ArrayList<>();
                retailerCalls.add(retailerCallsArrayItem);

                params.put("orders", orders);
                params.put("transactionId", transactionId);
                params.put("retailerCalls", retailerCalls);


            } catch (Exception e) {
                e.printStackTrace();
                loader.dismiss();
            }

            Log.e(TAG, "Existing Retailer Order json: " +new Gson().toJson(params));

            try {

                Call<JsonObject> callSubmitOrder = apiIntentface.submitOrder(
                        prefSFA.getString("token", ""),
                        params);

                // Retrofit
                callSubmitOrder.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                        Log.e(TAG, "Response Existing retailer order: " + response.toString());
                        Log.e(TAG, "Response Existing isSuccessful: " + response.isSuccessful());
                        loader.dismiss();
                        if (response.isSuccessful())
                        {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().toString());
                                //@Umesh
                                if(jsonObject.getInt("status")==1)
                                {
                                    boolean val2 = salesBeatDb.updateInOderPlacedByRetailersTable2(rid,
                                            "success", did);
                                    if (val2) {
                                        Toast.makeText(context, "Synced successfully", Toast.LENGTH_SHORT).show();
                                        visitedRetailerList.get(position).setServerStatus("success");
                                        notifyDataSetChanged();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }



                        } else {
                            Toast.makeText(context, Thread.currentThread().getStackTrace()[0].getLineNumber()+":"+response.code() + "" + response.message(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        loader.dismiss();
                        Log.e(TAG, "onFailure");
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                loader.dismiss();
                e.printStackTrace();
                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            loader.dismiss();
            Toast.makeText(context, "No data to sync", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorInfoDialog(String serverStatusCode, String errorMsg) {
        String y = "Ok", n = "";
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Error");
        builder.setMessage("Status Code:" + serverStatusCode + "\n" + errorMsg);

        builder.setPositiveButton(y, (dialogInterface, i) -> dialogInterface.dismiss());

        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cancelOrder(final String rid, final String did) {

        final Dialog dialog = new Dialog(context, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.no_order_reason_dialog);

        if (dialog.getWindow() != null)
            dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final RadioButton stockAvailable = (RadioButton) dialog.findViewById(R.id.stockAvl);
        final RadioButton shopClosed = (RadioButton) dialog.findViewById(R.id.shopClosed);
        final RadioButton competitorPriceLow = (RadioButton) dialog.findViewById(R.id.competitorPriceLow);
        final RadioButton competitorSchemeBetter = dialog.findViewById(R.id.competitorSchemeBetter);
        final RadioButton delaySupply = (RadioButton) dialog.findViewById(R.id.delayIrregularSupply);
        final RadioButton directOrder = (RadioButton) dialog.findViewById(R.id.directOrderPlaced);
        final RadioButton keyPersonNotAvl = (RadioButton) dialog.findViewById(R.id.keyPersonNotAvailable);
        final RadioButton lowRetailerMargin = (RadioButton) dialog.findViewById(R.id.lowRetailerMargin);
        final RadioButton lowShelfOffTake = (RadioButton) dialog.findViewById(R.id.lowShelfOffTake);
        final RadioButton noCustomerA = (RadioButton) dialog.findViewById(R.id.noCustomerA);
        final RadioButton notInterestedIn = (RadioButton) dialog.findViewById(R.id.notInterestedIn);
        final RadioButton paymentIssue = (RadioButton) dialog.findViewById(R.id.paymentIssue);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                ArrayList<String> reason = new ArrayList<>();

                if (stockAvailable.isChecked())
                    reason.add(stockAvailable.getText().toString());
                if (shopClosed.isChecked())
                    reason.add(shopClosed.getText().toString());
                if (competitorPriceLow.isChecked())
                    reason.add(competitorPriceLow.getText().toString());
                if (delaySupply.isChecked())
                    reason.add(delaySupply.getText().toString());
                if (directOrder.isChecked())
                    reason.add(directOrder.getText().toString());
                if (keyPersonNotAvl.isChecked())
                    reason.add(keyPersonNotAvl.getText().toString());
                if (lowRetailerMargin.isChecked())
                    reason.add(lowRetailerMargin.getText().toString());
                if (lowShelfOffTake.isChecked())
                    reason.add(lowShelfOffTake.getText().toString());
                if (noCustomerA.isChecked())
                    reason.add(noCustomerA.getText().toString());
                if (notInterestedIn.isChecked())
                    reason.add(notInterestedIn.getText().toString());
                if (paymentIssue.isChecked())
                    reason.add(paymentIssue.getText().toString());
                if (competitorSchemeBetter.isChecked())
                    reason.add(competitorSchemeBetter.getText().toString());


                if (reason.size() > 0) {

                    String transactionId = prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_"
                            + String.valueOf(Calendar.getInstance().getTimeInMillis());

                    Log.e("RID", "===" + rid);
                    salesBeatDb.updateInOderPlacedByRetailersTable(rid, did, "", "cancelled", "",
                            "", "", reason.toString(), transactionId, date);

                    dialog.dismiss();

                    Intent intent = new Intent(context, RetailerActivity.class);
                    intent.putExtra("tabPosition", 1);
                    context.startActivity(intent);
                    ((Activity) context).finish();

                } else {
                    Toast.makeText(context, "Please provide reason", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
    }

    private void cancelOrder2(final ArrayList<String> reasonList) {

        final Dialog dialog = new Dialog(context, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.no_order_reason_dialog);

        if (dialog.getWindow() != null)
            dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.setCancelable(false);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final RadioButton stockAvailable = (RadioButton) dialog.findViewById(R.id.stockAvl);
        final RadioButton shopClosed = (RadioButton) dialog.findViewById(R.id.shopClosed);
        final RadioButton competitorPriceLow = (RadioButton) dialog.findViewById(R.id.competitorPriceLow);
        final RadioButton competitorSchemeBetter = dialog.findViewById(R.id.competitorSchemeBetter);
        final RadioButton delaySupply = (RadioButton) dialog.findViewById(R.id.delayIrregularSupply);
        final RadioButton directOrder = (RadioButton) dialog.findViewById(R.id.directOrderPlaced);
        final RadioButton keyPersonNotAvl = (RadioButton) dialog.findViewById(R.id.keyPersonNotAvailable);
        final RadioButton lowRetailerMargin = (RadioButton) dialog.findViewById(R.id.lowRetailerMargin);
        final RadioButton lowShelfOffTake = (RadioButton) dialog.findViewById(R.id.lowShelfOffTake);
        final RadioButton noCustomerA = (RadioButton) dialog.findViewById(R.id.noCustomerA);
        final RadioButton notInterestedIn = (RadioButton) dialog.findViewById(R.id.notInterestedIn);
        final RadioButton paymentIssue = (RadioButton) dialog.findViewById(R.id.paymentIssue);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ArrayList<String> reason = new ArrayList<>();

                if (stockAvailable.isChecked())
                    reason.add(stockAvailable.getText().toString());
                if (shopClosed.isChecked())
                    reason.add(shopClosed.getText().toString());
                if (competitorPriceLow.isChecked())
                    reason.add(competitorPriceLow.getText().toString());
                if (delaySupply.isChecked())
                    reason.add(delaySupply.getText().toString());
                if (directOrder.isChecked())
                    reason.add(directOrder.getText().toString());
                if (keyPersonNotAvl.isChecked())
                    reason.add(keyPersonNotAvl.getText().toString());
                if (lowRetailerMargin.isChecked())
                    reason.add(lowRetailerMargin.getText().toString());
                if (lowShelfOffTake.isChecked())
                    reason.add(lowShelfOffTake.getText().toString());
                if (noCustomerA.isChecked())
                    reason.add(noCustomerA.getText().toString());
                if (notInterestedIn.isChecked())
                    reason.add(notInterestedIn.getText().toString());
                if (paymentIssue.isChecked())
                    reason.add(paymentIssue.getText().toString());
                if (competitorSchemeBetter.isChecked())
                    reason.add(competitorSchemeBetter.getText().toString());

                reasonList.add(reason.toString());

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public int getItemCount() {
        return visitedRetailerList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    visitedRetailerList = visitedRetailerList2;
                } else {
                    ArrayList<RetailerItem> filteredList = new ArrayList<>();
                    for (RetailerItem row : visitedRetailerList2) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getRetailerName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    visitedRetailerList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = visitedRetailerList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                visitedRetailerList = (ArrayList<RetailerItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRetailerName, tvRetailerAddress, tvRevisedOrder, tvCancelOrder,
                tvOrderType, tvCounter, tvTimeStamp, tvNewRetailer, tvFrom;

        ImageView locationIcon, infoIcon, callIcon, feedBackIcon, imgMore;

        CardView mainLayout;

        LinearLayout /*llOrderStamp,*/ llServerStatus;

        NetworkImageView retailerIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            //Layouts Linear,Relative,CardView
            mainLayout = itemView.findViewById(R.id.retailerListMainLayout);
            //llOrderStamp = itemView.findViewById(R.id.llOrderStamp);
            llServerStatus = itemView.findViewById(R.id.llServerStatus);
            //TextView
            tvRetailerName = itemView.findViewById(R.id.retailerName);
            tvRetailerAddress = itemView.findViewById(R.id.tvRetailerAddress);
            tvOrderType = itemView.findViewById(R.id.tvOrderType);
            tvRevisedOrder = itemView.findViewById(R.id.tvRevisedOrder);
            tvCancelOrder = itemView.findViewById(R.id.tvCancelOrder);
            tvCounter = itemView.findViewById(R.id.tvCounter);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            tvNewRetailer = itemView.findViewById(R.id.tvNewRetailer);
            tvFrom = itemView.findViewById(R.id.tvFrom);
            //ImageView
            retailerIcon = itemView.findViewById(R.id.retailerIcon);
            callIcon = itemView.findViewById(R.id.callIcon);
            locationIcon = itemView.findViewById(R.id.locationIcon);
            infoIcon = itemView.findViewById(R.id.infoIcon);
            feedBackIcon = itemView.findViewById(R.id.feedBackIcon);
            imgMore = itemView.findViewById(R.id.imgMore);
        }
    }
}

