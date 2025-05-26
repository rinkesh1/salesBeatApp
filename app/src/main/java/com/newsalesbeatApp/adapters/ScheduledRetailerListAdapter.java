package com.newsalesbeatApp.adapters;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.activities.OrderBookingRetailing;
import com.newsalesbeatApp.activities.RetailerActivity;
import com.newsalesbeatApp.activities.RetailerOrderActivity;
import com.newsalesbeatApp.activities.RetailerVisitHistoryActivity;
import com.newsalesbeatApp.activities.RetailersFeedBack;
import com.newsalesbeatApp.activities.UpdateRetailersActivity;
import com.newsalesbeatApp.fragments.ScheduledRetailerList;
import com.newsalesbeatApp.pojo.RetailerItem;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.MockLocationChecker;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.Social;
import com.newsalesbeatApp.utilityclass.UtilityClass;
import com.newsalesbeatApp.utilityclass.ViewAnimation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * Created by MTC on 25-07-2017.
 */

public class ScheduledRetailerListAdapter extends RecyclerView.Adapter<ScheduledRetailerListAdapter.ViewHolder> {

    private static FirebaseAnalytics firebaseAnalytics;
    Context context;
    private ArrayList<RetailerItem> scheduledRetailerList;
    private SharedPreferences tempPref, prefSFA;
    private int tempPosition = -1;
    private String checkInTimeFormated, checkInTimeStamp;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private UtilityClass utilityClass;
    private GPSLocation locationProvider;
    private SalesBeatDb salesBeatDb;
    private boolean mIsPresent;
    ScheduledRetailerList checkContext;
    String formatDistance = "";
    private List<Social> items = new ArrayList<>();


    public ScheduledRetailerListAdapter(ScheduledRetailerList scheduledRetailerList, Context ctx, ArrayList<RetailerItem> retailerList, boolean isPresent) {

        this.checkContext = scheduledRetailerList;
        this.context = ctx;
        this.scheduledRetailerList = retailerList;
        this.items = items;
        this.mIsPresent = isPresent;
        tempPosition = -1;
        tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), MODE_PRIVATE);
        prefSFA = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), MODE_PRIVATE);
        firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);

        utilityClass = new UtilityClass(ctx);
        locationProvider = new GPSLocation(ctx);
//        locationProvider.unregisterReceiver();
        //salesBeatDb = new SalesBeatDb(ctx);
        salesBeatDb = SalesBeatDb.getHelper(ctx);

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ////Changes in UI
        //Intelegains
        View view = LayoutInflater.from(context).inflate(R.layout.scheduled_retailer_list_row, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final RetailerItem p = scheduledRetailerList.get(position);

        holder.tvCounter.setText(String.valueOf(position + 1));
        holder.tvRetailerName.setText(scheduledRetailerList.get(position).getRetailerName());
        holder.tvRetailerAddress.setText(scheduledRetailerList.get(position).getRetailerAddress());

        if(!scheduledRetailerList.get(position).getRetailerPhone().equalsIgnoreCase("null")){
            holder.tvRetailerMobile.setText(scheduledRetailerList.get(position).getRetailerPhone());
        }else {
            holder.tvRetailerMobile.setText("");
            holder.imgCall.setVisibility(View.GONE);
        }


        String[] images = scheduledRetailerList.get(position).getRetailer_image().split(",");

        holder.retailerIcon.setImageUrl(SbAppConstants.IMAGE_PREFIX_RETAILER_THUMB + images[0], mImageLoader);
        //holder.retailerIcon.setImageUrl("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS8Be76P_87OKOqgBI16dtcH7fL9IegFcZKEw&usqp=CAU", mImageLoader);

        //holder.retailerIcon.setImageUrl("https://rtpl-cdn.azureedge.net/sfaadmin/retailers/thumb/AZ3xDrkNRroa3XLlDYOebOIE34AChU.png", mImageLoader);

        //@Umesh
//        if(images[0].length()>0 && images[0]!="default.jpeg")
//        {
//            byte[] imageBytes = Base64.decode(images[0], Base64.DEFAULT);
//            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//            holder.retailerIcon.setBackground(null);
//            holder.retailerIcon.setDefaultImageBitmap(decodedImage);
//       }

        if (mIsPresent) {
            if (tempPosition == -1) {

                holder.tvCheckInOrder.setVisibility(View.VISIBLE);
                holder.tvTelephonicOrder.setVisibility(View.VISIBLE);
                holder.llCheckIn.setVisibility(View.GONE);
                holder.tvNoOrderToday.setVisibility(View.GONE);
                holder.tvOnShopOrder.setVisibility(View.GONE);
                holder.llCheckIn.setVisibility(View.GONE);
                holder.tvCheckInOrder.setEnabled(true);
                holder.tvTelephonicOrder.setEnabled(true);
                holder.tvNoOrderToday.setEnabled(true);
                holder.tvOnShopOrder.setEnabled(true);
                //holder.imgScheduled.setVisibility(View.GONE);

            } else if (tempPosition == position) {

                holder.tvCheckInOrder.setEnabled(true);
                holder.tvNoOrderToday.setVisibility(View.VISIBLE);
                holder.tvTelephonicOrder.setEnabled(true);
                holder.tvNoOrderToday.setEnabled(true);
                holder.tvOnShopOrder.setEnabled(true);

            } else {

                holder.tvCheckInOrder.setEnabled(false);
                holder.tvTelephonicOrder.setEnabled(false);
                holder.tvNoOrderToday.setEnabled(false);
                holder.tvOnShopOrder.setEnabled(false);
                //holder.imgScheduled.setVisibility(View.VISIBLE);

            }


            holder.tvCheckInOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Cursor cursor = null;
                    String getBeatId = "", isRage = "";
                    try {
                        cursor = salesBeatDb.getBeatIdFromRetailer(scheduledRetailerList.get(position).getRetailerId());
                        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                            getBeatId = cursor.getString(cursor.getColumnIndex("beat_id_r"));
//                            Log.d("TAG", "getBeatId: " + getBeatId);
                        }

                        cursor = salesBeatDb.getBeatIdFromBeat(getBeatId);
                        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                            isRage = cursor.getString(cursor.getColumnIndex("beat_range"));
//                            Log.d("TAG", "isRage: " + isRage);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }

                    Log.d("TAG", "check isRage: "+isRage);

                    MockLocationChecker mockLocationChecker = new MockLocationChecker(context);

                    if (mockLocationChecker.isMockLocationEnabled()) {
                        Log.d("TAG", "Check Mock-1");
                        checkContext.toastError(view, "Mock location is turned ON, you must turn it OFF to continue using this app.");
                        return;
                    }

                    if (mockLocationChecker.isAnyLocationMock()) {
                        Log.d("TAG", "Check Mock-2");
                        checkContext.toastError(view, "Mock location is turned ON, you must turn it OFF to continue using this app.");
                        return;
                    }

                    if (isRage.equalsIgnoreCase("true")) {
                        if(scheduledRetailerList.get(position).getLatitude().length() != 0 && scheduledRetailerList.get(position).getLongtitude().length() != 0){
                            boolean IsRadius = checkRadiusInMtr(scheduledRetailerList.get(position).getLatitude(), scheduledRetailerList.get(position).getLongtitude());
                            Log.d("TAG", "Check Radius :"+IsRadius);
//                            if (IsRadius) {
                                boolean show = toggleLayoutExpand(!p.expanded, view, holder.lyt_expand);
                                scheduledRetailerList.get(position).expanded = show;

                                Bundle params = new Bundle();
                                params.putString("Action", "Check-In Order");
                                params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                                firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                                tempPosition = position;

                                holder.tvCheckInOrder.setVisibility(View.GONE);
                                holder.tvTelephonicOrder.setVisibility(View.GONE);
                                holder.tvOnShopOrder.setVisibility(View.VISIBLE);
                                holder.llCheckIn.setVisibility(View.VISIBLE);
                                holder.tvNoOrderToday.setVisibility(View.VISIBLE);

                                DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.timeformat));
                                DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                checkInTimeStamp = dateFormat1.format(Calendar.getInstance().getTime());
                                checkInTimeFormated = dateFormat.format(Calendar.getInstance().getTime());
                                holder.tvInTime.setText(checkInTimeFormated);
                                holder.llCheckIn.setVisibility(View.VISIBLE);

                                // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                                String[] temp = checkInTime.split(" ");

                                utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                                        checkInTime, "Retailer Checked In for Order", temp[1], String.valueOf(locationProvider.getLatitude()),
                                        String.valueOf(locationProvider.getLongitude()));

//                                notifyItemChanged(tempPosition);
                                notifyDataSetChanged();
//                            } else {
//                                checkContext.showCustomToast(formatDistance, scheduledRetailerList.get(position).getLatitude()
//                                        , scheduledRetailerList.get(position).getLongtitude()
//                                        , scheduledRetailerList.get(position).getRetailerId());
//
//                            }
                        }else {
//                            Toast.makeText(context, "This shop has not location data.", Toast.LENGTH_LONG).show();

                            Bundle params = new Bundle();
                            params.putString("Action", "Check-In Order");
                            params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                            firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                            tempPosition = position;

                            holder.tvCheckInOrder.setVisibility(View.GONE);
                            holder.tvTelephonicOrder.setVisibility(View.GONE);
                            holder.tvOnShopOrder.setVisibility(View.VISIBLE);
                            holder.llCheckIn.setVisibility(View.VISIBLE);
                            holder.tvNoOrderToday.setVisibility(View.VISIBLE);

                            DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.timeformat));
                            DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            checkInTimeStamp = dateFormat1.format(Calendar.getInstance().getTime());
                            checkInTimeFormated = dateFormat.format(Calendar.getInstance().getTime());
                            holder.tvInTime.setText(checkInTimeFormated);
                            holder.llCheckIn.setVisibility(View.VISIBLE);

                            // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                            String[] temp = checkInTime.split(" ");

                            utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                                    checkInTime, "Retailer Checked In for Order", temp[1], String.valueOf(locationProvider.getLatitude()),
                                    String.valueOf(locationProvider.getLongitude()));

//                            notifyItemChanged(tempPosition);
                            notifyDataSetChanged();
                        }

                    } else {
                        boolean show = toggleLayoutExpand(!p.expanded, view, holder.lyt_expand);
                        scheduledRetailerList.get(position).expanded = show;

                        Bundle params = new Bundle();
                        params.putString("Action", "Check-In Order");
                        params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                        firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                        tempPosition = position;

                        holder.tvCheckInOrder.setVisibility(View.GONE);
                        holder.tvTelephonicOrder.setVisibility(View.GONE);
                        holder.tvOnShopOrder.setVisibility(View.VISIBLE);
                        holder.llCheckIn.setVisibility(View.VISIBLE);
                        holder.tvNoOrderToday.setVisibility(View.VISIBLE);

                        DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.timeformat));
                        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        checkInTimeStamp = dateFormat1.format(Calendar.getInstance().getTime());
                        checkInTimeFormated = dateFormat.format(Calendar.getInstance().getTime());
                        holder.tvInTime.setText(checkInTimeFormated);
                        holder.llCheckIn.setVisibility(View.VISIBLE);

                        // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                        String[] temp = checkInTime.split(" ");

                        utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                                checkInTime, "Retailer Checked In for Order", temp[1], String.valueOf(locationProvider.getLatitude()),
                                String.valueOf(locationProvider.getLongitude()));

//                        notifyItemChanged(tempPosition);
                        notifyDataSetChanged();

                    }
                }
            });

            /*holder.tvCheckInShop_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "onClick Arrow");
                    boolean show = toggleLayoutExpand(!p.expanded, v, holder.lyt_expand);
                    scheduledRetailerList.get(position).expanded = show;
                }
            });*/

            holder.tvCancelCheckIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "tvCancelCheckIn");
                    boolean show = toggleLayoutExpand(!p.expanded, view, holder.lyt_expand);
                    scheduledRetailerList.get(position).expanded = show;

                    Bundle params = new Bundle();
                    params.putString("Action", "Cancel Check-In");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    tempPosition = -1;

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer Checked In for Order Cancelled", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));


//                    notifyItemChanged(position);
                    notifyDataSetChanged();
                }
            });

            holder.retailerIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle params = new Bundle();
                    params.putString("Action", "Retailer Icon");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer and shop images", temp[1],
                            String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    showImagePager(scheduledRetailerList.get(position).getRetailer_image());
                }
            });

            holder.callIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle params = new Bundle();
                    params.putString("Action", "Call Button");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    String phn = scheduledRetailerList.get(position).getRetailerPhone();

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

            holder.imgCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle params = new Bundle();
                    params.putString("Action", "Call Button");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    String phn = scheduledRetailerList.get(position).getRetailerPhone();

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

            holder.imgLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle params = new Bundle();
                    params.putString("Action", "Map Button");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer Location Clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));


                    try {

                        double lat2 = Double.parseDouble(scheduledRetailerList.get(position).getLatitude());
                        double longt2 = Double.parseDouble(scheduledRetailerList.get(position).getLongtitude());

                        String uri = "http://maps.google.com/?daddr=" + lat2 + "," + longt2;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(context, "Missing data", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            holder.locationIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle params = new Bundle();
                    params.putString("Action", "Map Button");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer Location Clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));


                    try {

                        double lat2 = Double.parseDouble(scheduledRetailerList.get(position).getLatitude());
                        double longt2 = Double.parseDouble(scheduledRetailerList.get(position).getLongtitude());

                        String uri = "http://maps.google.com/?daddr=" + lat2 + "," + longt2;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);

                    } catch (Exception e) {
                        Toast.makeText(context, "Missing data", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            holder.tvTelephonicOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    Bundle params = new Bundle();
                    params.putString("Action", "Telephonic Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            scheduledRetailerList.get(position).getRetailerId(), checkInTime, "telephonic", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    Intent intent = new Intent(context, RetailerOrderActivity.class);
                    intent.putExtra("retName", scheduledRetailerList.get(position).getRetailerName());
                    intent.putExtra("tabPosition", 0);
                    intent.putExtra("rid", scheduledRetailerList.get(position).getRetailerId());
                    intent.putExtra("orderType", "telephonic");
                    intent.putExtra("checkInTime", checkInTime);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                }
            });

            holder.btnTelephonicOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    Bundle params = new Bundle();
                    params.putString("Action", "Telephonic Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            scheduledRetailerList.get(position).getRetailerId(), checkInTime, "telephonic", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    Intent intent = new Intent(context, RetailerOrderActivity.class);
                    intent.putExtra("retName", scheduledRetailerList.get(position).getRetailerName());
                    intent.putExtra("tabPosition", 0);
                    intent.putExtra("rid", scheduledRetailerList.get(position).getRetailerId());
                    intent.putExtra("orderType", "telephonic");
                    intent.putExtra("checkInTime", checkInTime);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });


            holder.tvOnShopOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle params = new Bundle();
                    params.putString("Action", "OnShop Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    String[] temp = checkInTimeStamp.split(" ");

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            scheduledRetailerList.get(position).getRetailerId(), checkInTimeStamp, "onShop", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    Intent intent = new Intent(context, RetailerOrderActivity.class);
                    intent.putExtra("tabPosition", 0);
                    intent.putExtra("rid", scheduledRetailerList.get(position).getRetailerId());
                    intent.putExtra("retName", scheduledRetailerList.get(position).getRetailerName());
                    intent.putExtra("orderType", "onShop");
                    intent.putExtra("checkInTime", checkInTimeStamp);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


                }
            });

            holder.btnOnShopOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle params = new Bundle();
                    params.putString("Action", "OnShop Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    String[] temp = checkInTimeStamp.split(" ");

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            scheduledRetailerList.get(position).getRetailerId(), checkInTimeStamp, "onShop", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    Intent intent = new Intent(context, RetailerOrderActivity.class);
                    intent.putExtra("tabPosition", 0);
                    intent.putExtra("rid", scheduledRetailerList.get(position).getRetailerId());
                    intent.putExtra("retName", scheduledRetailerList.get(position).getRetailerName());
                    intent.putExtra("orderType", "onShop");
                    intent.putExtra("checkInTime", checkInTimeStamp);
                    context.startActivity(intent);
                    //((Activity) context).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });

            holder.btnNoOrderToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle params = new Bundle();
                    params.putString("Action", "No Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            scheduledRetailerList.get(position).getRetailerId(), checkInTime, "NoOrder", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    noOrderReason(position);
                }
            });

            holder.tvNoOrderToday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Bundle params = new Bundle();
                    params.putString("Action", "No Order");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setOrderEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            scheduledRetailerList.get(position).getRetailerId(), checkInTime, "NoOrder", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    noOrderReason(position);
                }
            });


            holder.infoIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle params = new Bundle();
                    params.putString("Action", "Info Button");
                    params.putString("UserId", "" + prefSFA.getString(context.getString(R.string.emp_id_key), ""));
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);


                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer Info Clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    /*Intent intent = new Intent(context, UpdateRetailersActivityUpdate.class);
                    intent.putExtra("retailer", scheduledRetailerList.get(position));
                    context.startActivity(intent);
                    ((Activity) context).finish();*/
                    Log.d("TAG", "onClick Update check");
                    Intent intent = new Intent(context, UpdateRetailersActivity.class);
                    intent.putExtra("retailer", scheduledRetailerList.get(position));
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
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);


                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer feedback clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    String rid = scheduledRetailerList.get(position).getRetailerId();
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
                    firebaseAnalytics.logEvent("ScheduledRetailerList", params);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String checkInTime = dateFormat.format(Calendar.getInstance().getTime());

                    String[] temp = checkInTime.split(" ");

                    utilityClass.setEvent(prefSFA.getString(context.getString(R.string.emp_id_key), ""),
                            checkInTime, "Retailer more clicked", temp[1], String.valueOf(locationProvider.getLatitude()),
                            String.valueOf(locationProvider.getLongitude()));

                    showDialog(view, holder.imgMore, scheduledRetailerList.get(position).getRetailerId(), position);

                }
            });
        } else {
            //intelegains
            // holder.tvCheckInOrder.setVisibility(View.GONE);
            holder.tvTelephonicOrder.setVisibility(View.GONE);
            //holder.llCheckIn.setVisibility(View.GONE);
            holder.tvNoOrderToday.setVisibility(View.GONE);
            holder.tvOnShopOrder.setVisibility(View.GONE);
            holder.llCheckIn.setVisibility(View.GONE);
            holder.callIcon.setVisibility(View.GONE);
            holder.locationIcon.setVisibility(View.GONE);
            holder.imgMore.setVisibility(View.GONE);
            holder.infoIcon.setVisibility(View.GONE);
            holder.linfooter.setVisibility(View.GONE);
            holder.tvRetailerBeat.setVisibility(View.VISIBLE);
            if (position == 0) {
                holder.linShowTitle.setVisibility(View.VISIBLE);
                holder.tvBeatName.setVisibility(View.VISIBLE);
                holder.tvBeatName.setText("Beat: " + scheduledRetailerList.get(position).getBeatName());
                holder.tvCheckInOrder.setText("Change Beat");
            } else {
                if (scheduledRetailerList.get(position).getBeatName().equalsIgnoreCase(scheduledRetailerList.get(position - 1).getBeatName())) {
                    holder.tvBeatName.setVisibility(View.GONE);
                    holder.tvCheckInOrder.setText("Change Beat");
                    // holder.tvBeatName.setText(scheduledRetailerList.get(position).getBeatName());
                } else {
                    holder.tvBeatName.setVisibility(View.VISIBLE);
                    holder.tvCheckInOrder.setText("Change Beat");
                    holder.tvBeatName.setText("Beat: " + scheduledRetailerList.get(position).getBeatName());
                }
            }


            holder.tvRetailerBeat.setText("Present In Beat = " + scheduledRetailerList.get(position).getRetailerbeat_unic_id() + " , Please Change A Beat.");
            holder.tvRetailerBeat.setTypeface(Typeface.DEFAULT_BOLD);
            holder.tvCheckInOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    tempPosition = position;
                    Intent intent = new Intent(context, OrderBookingRetailing.class);
                    intent.putExtra("change_beat", "yes");
                    intent.putExtra("beat_id", scheduledRetailerList.get(position).getRetailerbeat_unic_id());
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });
        }
    }


    private boolean toggleLayoutExpand(boolean show, View view, View lyt_expand) {
//        Tools.toggleArrow(show, view);
        if (show) {
            ViewAnimation.expand(lyt_expand);
        } else {
            ViewAnimation.collapse(lyt_expand);
        }
        return show;
    }

    /*public boolean checkRadiusInMtr(String latitude, String longtitude) {
        Log.d("TAG", "check latitude: " + latitude);
        if (latitude != null && !latitude.trim().isEmpty() && longtitude != null && !longtitude.trim().isEmpty()) {
            String curLat = String.valueOf(locationProvider.getLatitude());
            String curLong = String.valueOf(locationProvider.getLongitude());
            Log.d("TAG", "checkRadiusInMtr curLat: " + curLat);
            Log.d("TAG", "checkRadiusInMtr curLong: " + curLong);

            Log.d("TAG", "checkRadiusInMtr latitude: " + latitude);
            Location startLocation = new Location("start");
            startLocation.setLatitude(Double.parseDouble(latitude));
            startLocation.setLongitude(Double.parseDouble(longtitude));

            Location endLocation = new Location("end");
            endLocation.setLatitude(Double.parseDouble(curLat));
            endLocation.setLongitude(Double.parseDouble(curLong));

            double distance = startLocation.distanceTo(endLocation);
            double kmMeter = distance / 1000.0;
            Log.d("TAG", "Check Distance meter: " + distance);
            Log.d("TAG", "Check Distance Km: " + kmMeter);
            formatDistance = String.format("%.1f", distance);

            String[] parts = formatDistance.split("\\.");
            String beforeDecimal = parts[0];
            Log.d("TAG", "Check afterDecimal-1: " + beforeDecimal.length());
            if (parts[0].length() > 3) {
                formatDistance = String.format("%.1f", kmMeter) + " km";
            } else {
                formatDistance = String.format("%.1f", distance) + " meters";
            }
            SharedPreferences pref = context.getSharedPreferences("pincode", MODE_PRIVATE);
            int getRadius = Integer.parseInt(pref.getString("sp_radius", ""));
            Log.d("TAG", "get getRadius :" + getRadius);

            if (distance <= getRadius) {
                Log.d("TAG", "True");
                return true;
            }
        }
        return false;
    }*/

    public boolean checkRadiusInMtr(String latitude, String longitude) {
        Log.d("TAG", "checkRadiusInMtr - latitude: " + latitude + ", longitude: " + longitude);

        // Check if latitude and longitude are not null or blank
        if (latitude != null && !latitude.trim().isEmpty() && longitude != null && !longitude.trim().isEmpty()) {
            try {
                String curLat = String.valueOf(locationProvider.getLatitude());
                String curLong = String.valueOf(locationProvider.getLongitude());
                Log.d("TAG", "checkRadiusInMtr - Current Latitude: " + curLat + ", Current Longitude: " + curLong);

                Location startLocation = new Location("start");
                startLocation.setLatitude(Double.parseDouble(latitude));
                startLocation.setLongitude(Double.parseDouble(longitude));

                Location endLocation = new Location("end");
                endLocation.setLatitude(Double.parseDouble(curLat));
                endLocation.setLongitude(Double.parseDouble(curLong));

                double distance = startLocation.distanceTo(endLocation);
                double kmMeter = distance / 1000.0;
                Log.d("TAG", "checkRadiusInMtr - Distance in meters: " + distance);
                Log.d("TAG", "checkRadiusInMtr - Distance in km: " + kmMeter);
                formatDistance = String.format("%.1f", distance);

                String[] parts = formatDistance.split("\\.");
                String beforeDecimal = parts[0];
                Log.d("TAG", "checkRadiusInMtr - Length before decimal: " + beforeDecimal.length());

                if (beforeDecimal.length() > 3) {
                    formatDistance = String.format("%.1f", kmMeter) + " km";
                } else {
                    formatDistance = String.format("%.1f", distance) + " meters";
                }

                SharedPreferences pref = context.getSharedPreferences("pincode", MODE_PRIVATE);
                String radiusValue = pref.getString("sp_radius", "");
                Log.d("TAG", "checkRadiusInMtr - Radius from SharedPreferences: " + radiusValue);

                int getRadius = Integer.parseInt(radiusValue);

                if (distance <= getRadius) {
                    Log.d("TAG", "checkRadiusInMtr - Within radius: true");
                    return true;
                } else {
                    Log.d("TAG", "checkRadiusInMtr - Outside radius: false");
                    return false;
                }
            } catch (NumberFormatException e) {
                Log.e("TAG", "checkRadiusInMtr - Invalid latitude or longitude format: " + e.getMessage());
            } catch (Exception e) {
                Log.e("TAG", "checkRadiusInMtr - Error: " + e.getMessage());
            }
        } else {
            Log.d("TAG", "checkRadiusInMtr - Latitude or Longitude is null or blank");
        }
        return false;
    }


    private void showImagePager(String images) {

        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        Dialog dialog = new Dialog(context, R.style.DialogTheme);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null)
            lp.copyFrom(dialog.getWindow().getAttributes());
        lp.dimAmount = 0.75f;
        lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.width = width;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        dialog.setContentView(R.layout.outlet_image_dialog);
        final ViewPager roomPager = dialog.findViewById(R.id.viewPagerShopImage);
        final TextView txtvCurrentRoom = dialog.findViewById(R.id.tvCurrentImage);
        final TextView txtvTotalRoom = dialog.findViewById(R.id.tvTotalImage);

        String[] shopImages = images.split(",");

        ShopImagePagerAdapter imagePagerAdapter = new ShopImagePagerAdapter(context, shopImages);
        roomPager.setAdapter(imagePagerAdapter);
        txtvTotalRoom.setText(String.valueOf(shopImages.length));
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


    private void noOrderReason(final int position) {
        final Dialog dialog = new Dialog(context, R.style.DialogActivityTheme);
        dialog.setContentView(R.layout.no_order_reason_dialog);

        if (dialog.getWindow() != null)
            dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final RadioButton stockAvailable = dialog.findViewById(R.id.stockAvl);
        final RadioButton shopClosed = dialog.findViewById(R.id.shopClosed);
        final RadioButton competitorPriceLow = dialog.findViewById(R.id.competitorPriceLow);
        final RadioButton competitorSchemeBetter = dialog.findViewById(R.id.competitorSchemeBetter);
        final RadioButton delaySupply = dialog.findViewById(R.id.delayIrregularSupply);
        final RadioButton directOrder = dialog.findViewById(R.id.directOrderPlaced);
        final RadioButton keyPersonNotAvl = dialog.findViewById(R.id.keyPersonNotAvailable);
        final RadioButton lowRetailerMargin = dialog.findViewById(R.id.lowRetailerMargin);
        final RadioButton lowShelfOffTake = dialog.findViewById(R.id.lowShelfOffTake);
        final RadioButton noCustomerA = dialog.findViewById(R.id.noCustomerA);
        final RadioButton notInterestedIn = dialog.findViewById(R.id.notInterestedIn);
        final RadioButton paymentIssue = dialog.findViewById(R.id.paymentIssue);
        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ArrayList<String> reason = new ArrayList<>();
                reason.clear();

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

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar cal = Calendar.getInstance();
                    String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                    String time2 = dateFormat.format(Calendar.getInstance().getTime());

                    String transactionId = prefSFA.getString(context.getString(R.string.emp_id_key), "") + "_" + String.valueOf(Calendar.getInstance().getTimeInMillis());


                    salesBeatDb.entryInOderPlacedByRetailersTable(scheduledRetailerList.get(position).getRetailerId(),
                            tempPref.getString(context.getString(R.string.dis_id_key), ""), time2, "no order", checkInTimeStamp,
                            locationProvider.getLatitudeStr(), locationProvider.getLongitudeStr(), reason.toString(), transactionId, date, "", "");

                    tempPosition = -1;

                    dialog.dismiss();

                    Intent intent = new Intent(context, RetailerActivity.class);
                    intent.putExtra("tabPosition", 1);
                    context.startActivity(intent);
                    ((Activity) context).finish();

                } else {

                    Toast.makeText(context, "No reason selected", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();

    }


    private void showDialog(View view, ImageView imgMore, final String rid, final int pos) {

        PopupMenu popupMenu = new PopupMenu(view.getContext(), imgMore);

        ((Activity) context).getMenuInflater().inflate(R.menu.more_item, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.visitHistory) {

                    if (utilityClass.isInternetConnected()) {

                        Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                        intent.putExtra("rid", rid);
                        intent.putExtra("from", "retailer");
                        intent.putExtra("name", scheduledRetailerList.get(pos).getRetailerName());
                        context.startActivity(intent);

                    } else {
                        Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                    }

                }/* else if (item.getItemId() == R.id.setReminder) {

                    setReminder();
                }*/

                return false;
            }
        });

        popupMenu.show();

    }

    @Override
    public int getItemCount() {
        return scheduledRetailerList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRetailerName, tvRetailerAddress,tvRetailerMobile, tvCheckInOrder, tvCounter, tvInTime,
                tvTelephonicOrder, tvOnShopOrder, tvNoOrderToday, tvCancelCheckIn, tvRetailerBeat, tvBeatName;

        ImageView locationIcon, infoIcon, callIcon, feedBackIcon, imgMore,imgCall,imgLocation/*, imgScheduled*/;

        Button btnOnShopOrder, btnNoOrderToday, btnTelephonicOrder;

        NetworkImageView retailerIcon;
        ImageButton bt_expand;

        CardView mainLayout;

        LinearLayout llScheduled, llCheckIn, linfooter, linShowTitle, lyt_expand;

        public ViewHolder(View itemView) {
            super(itemView);
            //Layouts Linear,Relative,CardView
            imgLocation = itemView.findViewById(R.id.imgLocation);
            imgCall = itemView.findViewById(R.id.imgCall);
            bt_expand = itemView.findViewById(R.id.bt_expand);
            lyt_expand = itemView.findViewById(R.id.lyt_expand);
            btnTelephonicOrder = itemView.findViewById(R.id.btnTelephonicOrder);
            btnNoOrderToday = itemView.findViewById(R.id.btnNoOrderToday);
            btnOnShopOrder = itemView.findViewById(R.id.btnOnShopOrder);

            mainLayout = itemView.findViewById(R.id.retailerListMainLayout);
            llScheduled = itemView.findViewById(R.id.llScheduled);
            llCheckIn = itemView.findViewById(R.id.llCheckIn);
            //TextView
            tvRetailerName = itemView.findViewById(R.id.retailerName);
            tvRetailerAddress = itemView.findViewById(R.id.tvRetailerAddress);
            tvRetailerMobile = itemView.findViewById(R.id.tvRetailerMobile);
            tvCheckInOrder = itemView.findViewById(R.id.tvCheckInShop);
            tvCounter = itemView.findViewById(R.id.tvCounter);
            tvInTime = itemView.findViewById(R.id.tvInTime);
            tvTelephonicOrder = itemView.findViewById(R.id.tvTelephonicOrder);
            tvOnShopOrder = itemView.findViewById(R.id.tvOnShopOrder);
            tvNoOrderToday = itemView.findViewById(R.id.tvNoOrderToday);
            tvCancelCheckIn = itemView.findViewById(R.id.tvCancelCheckIn);
            linfooter = itemView.findViewById(R.id.linfooter);
            linShowTitle = itemView.findViewById(R.id.linShowTitle);
            //ImageView
            retailerIcon = itemView.findViewById(R.id.retailerIcon);
            callIcon = itemView.findViewById(R.id.callIcon);
            locationIcon = itemView.findViewById(R.id.locationIcon);
            infoIcon = itemView.findViewById(R.id.infoIcon);
            feedBackIcon = itemView.findViewById(R.id.feedBackIcon);
            imgMore = itemView.findViewById(R.id.imgMore);
            tvBeatName = itemView.findViewById(R.id.tvBeatName);
            //imgScheduled = itemView.findViewById(R.id.imgScheduled);
            tvRetailerBeat = itemView.findViewById(R.id.tvRetailerBeat);

        }

    }

}
