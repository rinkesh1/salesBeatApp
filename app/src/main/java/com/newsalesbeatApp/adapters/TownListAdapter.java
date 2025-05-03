package com.newsalesbeatApp.adapters;

import static com.newsalesbeatApp.utilityclass.UtilityClass.isValidBase64;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.fragments.DistributorList;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.pojo.BeatItem;
import com.newsalesbeatApp.pojo.TownItem;
import com.newsalesbeatApp.receivers.ConnectivityChangeReceiver;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.PingServer;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.TextHttpResponseHandler;
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.DefaultHttpClient;

/*
 * Created by MTC on 29-07-2017.
 */

public class TownListAdapter extends RecyclerView.Adapter<TownListAdapter.ViewHolder> implements NetworkChangeInterface {

    private String TAG = "TownListAdapter";
    private Context context;
    private SalesBeatDb salesBeatDb;
    private ArrayList<TownItem> townList;
    private SharedPreferences tempPref, myPref;
    private ClientInterface clientInterface;


    private UtilityClass utilityClassObj;
    private Dialog view;

    //private LinearLayout llDownloadStatus;
    private ImageView imgDisMappingDownloadDone, imgDisDownloadDone, imgBeatDownloadDone, imgRetDownloadDone;
    private TextView tvTryAgainDisMap, tvTryAgainDis, tvTryAgainBeat,
            tvTryAgainRet, tvDownloadingDisMap, tvDownloadingDis, tvDownloadingBeats, tvDownloadingRet;

    private GifImageView imgLoaderDisMap, imgLoaderDis, imgLoaderBeat, imgLoaderRet;

    private ArrayList<String> dids = new ArrayList<>();
    private ArrayList<String> didUpdatedAt = new ArrayList<>();
    private ArrayList<String> bids = new ArrayList<>();
    private ArrayList<String> bidUpdatedAt = new ArrayList<>();
    private ArrayList<String> retId = new ArrayList<>();
    private ArrayList<String> ridUpdatedAt = new ArrayList<>();
    //private ArrayList<String> beatIdList = new ArrayList<>();

    //@Umesh 20220916
    public ArrayList<BeatItem> BeatItemList = new ArrayList<>();

    private DownloadMappingDetails downloadTask;
    private DownloadDistributors downloadDis;
    private DownloadBeats downloadBeats;
    private DownloadRetailers downloadRetailers;
    private GoToFragment goToFragment;

    private boolean isCancelled = false;

    private boolean skuCall = false;
    private boolean connected = true;

    //@Umesh 20220908
    private boolean IsOther = false;

    public TownListAdapter(Context ctx, ArrayList<TownItem> tList) {

        try {

            this.context = ctx;
            this.townList = tList;
            tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
            myPref = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
            //requestQueue = Volley.newRequestQueue(context);

            //salesBeatDb = new SalesBeatDb(ctx);
            salesBeatDb = SalesBeatDb.getHelper(ctx);
            //serverCall = new ServerCall(ctx);
            utilityClassObj = new UtilityClass(ctx);

            String townName = tempPref.getString(ctx.getString(R.string.town_name_key), "");

            //@Umesh 20220908
            if (tempPref.getString("IsOther", "").equals("true"))
                IsOther = true;
            else
                IsOther = false;

            NetworkChangeReceiver receiver = new NetworkChangeReceiver();
            receiver.InitNetworkListener(this);

            if (!townName.isEmpty()) {
                initDistDialog(townName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.town_list_row, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.tvTownName.setText(townList.get(position).getTownName());
        if (townList.get(position).getTownName().length() > 0) //@Umesh 20220909
        {
            String letter = String.valueOf(townList.get(position).getTownName().charAt(0));
            holder.icon1.setText(letter);
        }
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.icon1.setTextColor(color);


        try {

            PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
            Drawable drawable = context.getDrawable(R.drawable.gray_rectangle);
            int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            drawable.setColorFilter(color2, mMode);

            Drawable drawable1 = context.getDrawable(R.drawable.ic_keyboard_arrow_right_black_48dp);
            drawable1.setColorFilter(Color.BLACK, mMode);

            holder.icon1.setBackground(drawable);
            holder.icon2.setBackground(drawable1);

        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }

        holder.rlTownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tName = townList.get(position).getTownName();
                initDistDialog(tName);

            }
        });
    }

    private void initDistDialog(String tName) {

        try {

            SharedPreferences.Editor editor = tempPref.edit();
            editor.putString(context.getString(R.string.town_name_key), tName);
            editor.apply();

            new PingServer(internet -> {
                /* do something with boolean response */
                if (!internet) {
                    Toast.makeText(context, "No internet. You are offline", Toast.LENGTH_SHORT).show();
                } else {

                    isCancelled = false;
                    if (context != null && !((Activity) context).isFinishing())
                        showDownloadDialog(tName);


                }

            });


            //new MyAsynchTask(townList.get(position).getTownName()).execute();

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void showDownloadDialog(final String townName) {

        view = new Dialog(context, R.style.DialogActivityTheme);
        view.requestWindowFeature(Window.FEATURE_NO_TITLE);
        view.setContentView(R.layout.download_progress_dialog);


        if (view.getWindow() != null) {

            view.getWindow().setGravity(Gravity.BOTTOM);
        }


        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(view.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 70%
        int dialogWindowWidth = (int) (displayWidth * 0.99f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.75f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        //layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
        view.getWindow().setAttributes(layoutParams);

        //activityListDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        view.setCancelable(false);
        //view.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        imgDisMappingDownloadDone = view.findViewById(R.id.imgDisMappingDownloadDone);
        imgDisDownloadDone = view.findViewById(R.id.imgDisDownloadDone);
        imgBeatDownloadDone = view.findViewById(R.id.imgBeatDownloadDone);
        imgRetDownloadDone = view.findViewById(R.id.imgRetDownloadDone);
        imgLoaderDisMap = view.findViewById(R.id.imgLoaderDisMap);
        imgLoaderDis = view.findViewById(R.id.imgLoaderDis);
        imgLoaderBeat = view.findViewById(R.id.imgLoaderBeat);
        imgLoaderRet = view.findViewById(R.id.imgLoaderRet);
        tvTryAgainDisMap = view.findViewById(R.id.tvTryAgainDisMap);
        tvTryAgainDis = view.findViewById(R.id.tvTryAgainDis);
        tvTryAgainBeat = view.findViewById(R.id.tvTryAgainBeat);
        tvTryAgainRet = view.findViewById(R.id.tvTryAgainRet);
        tvDownloadingDisMap = view.findViewById(R.id.tvDownloadingDisMap);
        tvDownloadingDis = view.findViewById(R.id.tvDownloadingDis);
        tvDownloadingBeats = view.findViewById(R.id.tvDownloadingBeats);
        tvDownloadingRet = view.findViewById(R.id.tvDownloadingRet);
        ImageView imgClose = view.findViewById(R.id.closeDownloadDialog);

        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
        imgDisDownloadDone.setVisibility(View.VISIBLE);
        imgDisDownloadDone.setImageResource(R.drawable.refresh);
        imgBeatDownloadDone.setVisibility(View.VISIBLE);
        imgBeatDownloadDone.setImageResource(R.drawable.refresh);
        imgRetDownloadDone.setVisibility(View.VISIBLE);
        imgRetDownloadDone.setImageResource(R.drawable.refresh);
        tvTryAgainDisMap.setVisibility(View.GONE);
        tvTryAgainDis.setVisibility(View.GONE);
        tvTryAgainBeat.setVisibility(View.GONE);
        tvTryAgainRet.setVisibility(View.GONE);
        imgLoaderDisMap.setVisibility(View.GONE);
        imgLoaderDis.setVisibility(View.GONE);
        imgLoaderBeat.setVisibility(View.GONE);
        imgLoaderRet.setVisibility(View.GONE);


        new DownloadMappingDetails(townName, IsOther).execute();

        tvTryAgainDisMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTryAgainDisMap.setVisibility(View.GONE);

                if (downloadTask == null) {
                    downloadTask = new DownloadMappingDetails(townName, IsOther);
                    downloadTask.execute();
                } else {
                    downloadTask.cancel(true);
                    downloadTask = null;
                    downloadTask = new DownloadMappingDetails(townName, IsOther);
                    downloadTask.execute();

                }
            }
        });

        tvTryAgainDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTryAgainDis.setVisibility(View.GONE);
                if (downloadDis == null) {
                    downloadDis = new DownloadDistributors(townName);
                    downloadDis.execute();
                } else {
                    downloadDis.cancel(true);
                    downloadDis = null;
                    downloadDis = new DownloadDistributors(townName);
                    downloadDis.execute();

                }
            }
        });

        tvTryAgainBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTryAgainBeat.setVisibility(View.GONE);
                if (downloadBeats == null) {

                    downloadBeats = new DownloadBeats(townName);
                    downloadBeats.execute();
                } else {
                    downloadBeats.cancel(true);
                    downloadBeats = null;
                    downloadBeats = new DownloadBeats(townName);
                    downloadBeats.execute();

                }

            }
        });

        tvTryAgainRet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvTryAgainRet.setVisibility(View.GONE);
                if (downloadRetailers == null) {

                    downloadRetailers = new DownloadRetailers();
                    downloadRetailers.execute();
                } else {
                    downloadRetailers.cancel(true);
                    downloadRetailers = null;
                    downloadRetailers = new DownloadRetailers();
                    downloadRetailers.execute();

                }

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isCancelled = true;

                if (downloadTask != null /*&& downloadTask.getStatus() == AsyncTask.Status.RUNNING*/) {

                    downloadTask.cancel(true);
                }

                if (downloadDis != null /*&& downloadDis.getStatus() == AsyncTask.Status.RUNNING*/) {

                    downloadDis.cancel(true);
                }

                if (downloadBeats != null /*&& downloadBeats.getStatus() == AsyncTask.Status.RUNNING*/) {

                    downloadBeats.cancel(true);
                }

                if (downloadRetailers != null /*&& downloadRetailers.getStatus() == AsyncTask.Status.RUNNING*/) {

                    downloadRetailers.cancel(true);
                }

                if (goToFragment != null /*&& goToFragment.getStatus() == AsyncTask.Status.RUNNING*/) {

                    goToFragment.cancel(true);
                }

                view.dismiss();
            }
        });

        view.show();
    }

    private void getSkuList() {

        clientInterface = RetrofitClient.getClient().create(ClientInterface.class);
        String strToken = myPref.getString("token", "");
        Log.d(TAG, "getSkuList Token: " + strToken);
        Call<JsonObject> jsonObjectCall = clientInterface.getProductList(myPref.getString("token", ""));

        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                skuCall = false;
                if (response.isSuccessful()) {
                    // called when response HTTP status is "200 OK"
                    Log.e(TAG, "Response Sku List==:" + response);

                    try {

                        JSONObject JsonResponse = new JSONObject(response.body().toString());
                        JSONObject data = JsonResponse.getJSONObject("data");
                        JSONArray products = data.getJSONArray("sku");

                        Log.e(TAG, "Response Sku product:" + new Gson().toJson(products));
                        Log.e(TAG, "Response Sku List check==:" + new Gson().toJson(JsonResponse));
                        //String status = JsonResponse.getString("status");
                        //String msg = JsonResponse.getString("statusMessage");
                        if (JsonResponse.getInt("status") == 1) {

                            //clearr previous data from DB
                            if (products.length() > 0) {
                                salesBeatDb.deleteAllDataFromSkuDetailsTable();
                            }


                            for (int i = 0; i < products.length(); i++) {

                                JSONObject object = (JSONObject) products.get(i);
                                JSONObject brand = object.getJSONObject("skuBrands");
//                                Log.e(TAG, "skuBrands: "+new Gson().toJson(brand));
                                try {
                                    Log.e(TAG, "Image Name: " + object.getString("image"));
                                    String imageBase = object.getString("image");
//                                    String imageBase = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEMAAAAYCAMAAACWXbB1AAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABXUExURUdwTOA5NNo1MstFJtQ2L8tLINRAK8o4N8xFJeQxNtgvM7srK8ouMP/+/uZFRepgWOQ6POhQTexxZumGg/ro6MFPTfbU0+idm85mZcx/eui6uXileV21gLBfi1wAAAAJdFJOUwDcvUmaEW34Kn/heG0AAAI8SURBVDjLhZWLlqMgEERVSBAERBBfmf//zq0GH+jkzBaJAqGu3R3Fqqqq1+vVti1jb4hz3mTVD+VZ/M5pHWMMHjgrEq9reZM7ZJ09NVDblQZZxH5VrbrM1E7AYT7UU3tqsM6BcY9B3ginP8vnVggU11bsFoX8TbiuXtg/6wEZbMEoa5EqcRbgoFxBfH5+AAmZwar3PRNZVDND+isTEBKkjz+fK47MUM57WKT13rkheHyD9aEnhp/n0GOcBFKYMTFE7+lM9bDvikullDAmjGO/mNGpgMNqzGgwZ/sJJ7NZGkF9SJ0JqDQG/WBojXXGzGAotYKxYB0mJruZMcbVi3XdzLSGfjRbHIkxoxNnQjhecUJ0xizrrMHQGgyFjtCzmcSIWOzicUthNAwLDjZSb6VwAhDEaIBIjE6DYciaGE4EM2liINE+MSycYz+knlg3pIUquszousTodEQZ8MuoUxzEQC4BqSNiclpaMFOO1s9+mUzAfyhvjK5DDUbYKJ/MiFTTcXZOgOEsmFTtybkt1RYEKZuqJkQXY9ftZ0H9GLUWdOjiEoVCi3FwTmkaUg/zS8yP2sF4SFM7BcQup+jCTt3ubTDUN0hBUfRRlyQRSkpd4Rr/gSROxsijyRuDU6Baf4VkiirjkA+AdA3DTsi40ImjT5S+hUEIfSRyQ6jmnTdDqH3zZi+cVrpU4X/kUDecnYBdrwTa0/6lwo9HFPb26S9JDChs6kKUAFLa2/n7L/cddbwv8MagAzvfA18A/wCODEqJtiSYGgAAAABJRU5ErkJggg==";

                                    String base64Data = imageBase.substring(imageBase.indexOf(",") + 1);
                                    Log.d(TAG, "image base 64 Data-1: " + base64Data);

                                    /*salesBeatDb.insertSkuDetailsTable(object.getString("skuid"), object.getString("sku"),
                                            brand.getString("brandName"), object.getString("price"), object.getString("unit"),
                                            object.getString("conversionFactor"),imageBase);*/
                                    if (imageBase.contains("data:image/png;base64,")) {
                                        salesBeatDb.insertSkuDetailsTable(
                                                object.getString("skuid"),
                                                object.getString("sku"),
                                                brand.getString("brandName"),
                                                object.getString("price"),
                                                object.getString("unit"),
                                                object.getString("conversionFactor"),
                                                base64Data
                                        );
                                    } else {
                                        salesBeatDb.insertSkuDetailsTable(
                                                object.getString("skuid"),
                                                object.getString("sku"),
                                                brand.getString("brandName"),
                                                object.getString("price"),
                                                object.getString("unit"),
                                                object.getString("conversionFactor"),
                                                "" // Insert a blank string
                                        );
                                    }

                                } catch (Exception sqle) {
                                    sqle.printStackTrace();
                                }
                            }

                            if (products.length() == 0) {
                                SharedPreferences.Editor editor = tempPref.edit();
                                editor.putString(context.getString(R.string.skuErrorKey), new Throwable().getStackTrace()[0].getLineNumber() + "No data: skus");
                                editor.apply();
                            }
                        } else {
                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + JsonResponse.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                        SharedPreferences.Editor editor = tempPref.edit();
                        editor.putString(context.getString(R.string.skuErrorKey), "Exception: " + e.getMessage());
                        editor.apply();
                    }
                } else {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.e(TAG, "" + response.code());

                    SharedPreferences.Editor editor = tempPref.edit();
                    editor.putString(context.getString(R.string.skuErrorKey), response.code() + ": " + response.message());
                    editor.apply();
                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + response.message() + " : " + response.code(), Toast.LENGTH_SHORT).show();
                    //serverCall.handleError2(statusCode,TAG, "getSku", res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Failed Get Town List " + t.getMessage());


                if (!ConnectivityChangeReceiver.isConnectedToInternet(context)) {
                    //TODO call Retrofit on Network Available
                    skuCall = true;
                }

            }
        });
    }


    @Override
    public void connectionChange(boolean status) {
        Toast.makeText(context, "Town List " + status, Toast.LENGTH_SHORT).show();

        if (skuCall) {
            if (status) {
                getSkuList();
            } else {
                Toast.makeText(context, "Reconnect to internet", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public int getItemCount() {
        return townList.size();
    }

    private class DownloadMappingDetails extends AsyncTask<Void, String, String> {

        String townName;
        boolean IsOther;

        public DownloadMappingDetails(String townName, boolean IsOther) {
            this.townName = townName;
            this.IsOther = IsOther;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvDisMappingDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadingDisMap.setTextColor(Color.parseColor("#424242"));
            imgLoaderDisMap.setImageResource(R.drawable.loader_gif);
            imgLoaderDisMap.setVisibility(View.VISIBLE);
            imgDisMappingDownloadDone.setVisibility(View.GONE);
            salesBeatDb.deleteDisBeatMap();
            salesBeatDb.deleteDisSkuMap();
            Log.d("TAG", "getSkuList");
            getSkuList();
        }

        @Override
        protected String doInBackground(Void... voids) {

            if (!isCancelled()) {

                try {
                    townName = URLEncoder.encode(townName, "utf-8");
                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                }

            }

            return null;
        }


        public void getTownDistributorsAndBeats() {
            JsonObjectRequest getBeatDetailsReq = new JsonObjectRequest(Request.Method.GET,
                    SbAppConstants.API_GET_MAPPING_DistributorsAndBeats + "town=" + townName + "&IsOther=" + this.IsOther, null,
                    response -> {
                        try {
                            Integer Status = response.getInt("status");
                            if (response.getInt("status") == 1) {
                                response = response.getJSONObject("data");
                                dids.clear();
                                didUpdatedAt.clear();
                                bids.clear();
                                bidUpdatedAt.clear();
//                                retId.clear();
//                                ridUpdatedAt.clear();

                                JSONArray distributors = response.getJSONArray("distributors");
                                Log.d(TAG, "getTownDistributorsAndBeats Town: " + new Gson().toJson(distributors));
                                if (distributors != null) {
                                    for (int i = 0; i < distributors.length(); i++) {

                                        JSONObject disObj = (JSONObject) distributors.get(i);
                                        dids.add(String.valueOf(disObj.getInt("did")));
                                        didUpdatedAt.add(disObj.getString("updated_at"));
                                    }
                                }
                                JSONArray disBeatMapArr = response.getJSONArray("distBeatMap");
                                for (int i = 0; i < disBeatMapArr.length(); i++) {
                                    JSONObject obj = (JSONObject) disBeatMapArr.get(i);
                                    String did = obj.getString("did");
                                    String bid = obj.getString("bid");
                                    salesBeatDb.insertIntoDisBeatMap(did, bid);
                                }

                                JSONArray beats = response.getJSONArray("beats");
                                BeatItemList.clear();
                                for (int i = 0; i < beats.length(); i++) {

                                    JSONObject beatObj = (JSONObject) beats.get(i);

                                    //@Umesh 20220916 cause of duplicate bid in beats table..

//                                    bids.add(String.valueOf(beatObj.getInt("bid")));
//                                    bidUpdatedAt.add(beatObj.getString("updated_at"));

                                    BeatItem bt = new BeatItem();
                                    bt.setBeatId(String.valueOf(beatObj.getInt("bid")));
                                    bt.setBeatName(beatObj.getString("name"));
                                    bt.setBeatUpdatedAt(beatObj.getString("updated_at"));
                                    bt.setMinimumCheckinRange(beatObj.getBoolean("isMinimumCheckinRange"));
                                    BeatItemList.add(bt);

                                    Log.d(TAG, "getTownDistributorsAndBeats model: " + new Gson().toJson(BeatItemList));
                                }


//                                JSONArray retailers = response.getJSONArray("retailersBeatMap");
//                                for (int i = 0; i < retailers.length(); i++) {
//
//                                    JSONObject retObj = (JSONObject) retailers.get(i);
//                                    retId.add(String.valueOf(retObj.getInt("rid")));
//                                    ridUpdatedAt.add(retObj.getString("updated_at"));
//                                }
//                                JSONArray disSkuMapArr = response.getJSONArray("skuDistMap");
//                                for (int i = 0; i < disSkuMapArr.length(); i++)
//                                {
//                                    JSONObject obj = (JSONObject) disSkuMapArr.get(i);
//                                    String skuid = obj.getString("skuid");
//                                    String did = obj.getString("did");
//
//                                    salesBeatDb.insertIntoDisSkuMap(did, skuid);
//                                }
                            }


                            if (!isCancelled) {

                                try {

                                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                    imgLoaderDisMap.setVisibility(View.GONE);
                                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                                    if (Status == 1) {

                                        if (downloadDis == null) {
                                            downloadDis = new DownloadDistributors(townName);
                                            downloadDis.execute();
                                        } else {
                                            downloadDis.cancel(true);
                                            downloadDis = null;
                                            downloadDis = new DownloadDistributors(townName);
                                            downloadDis.execute();

                                        }

                                    } else {

                                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                        imgLoaderDisMap.setVisibility(View.GONE);
                                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();

                                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                    imgLoaderDisMap.setVisibility(View.GONE);
                                    tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            e.printStackTrace();

                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                            imgLoaderDisMap.setVisibility(View.GONE);
                            tvTryAgainDisMap.setVisibility(View.VISIBLE);
                            imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                            imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();

                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                    imgLoaderDisMap.setVisibility(View.GONE);
                    tvTryAgainDisMap.setVisibility(View.VISIBLE);
                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                    imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);

                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", myPref.getString("token", ""));
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            getBeatDetailsReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(context).add(getBeatDetailsReq);
        }

        public void getTownRetailerAndSkus() {
            Log.d("TAG", "Town Name check: " + townName);
            Log.d("TAG", "Town Name Devices: " + this.IsOther);

            JsonObjectRequest getBeatDetailsReq = new JsonObjectRequest(Request.Method.GET,
                    SbAppConstants.API_GET_MAPPING_RetailerAndSkus + "town=" + townName + "&IsOther=" + this.IsOther, null,
                    response -> {
                        try {
                            Integer Status = response.getInt("status");
                            if (response.getInt("status") == 1) {
                                response = response.getJSONObject("data");
//                                dids.clear();
//                                didUpdatedAt.clear();
//                                bids.clear();
//                                bidUpdatedAt.clear();
                                retId.clear();
                                ridUpdatedAt.clear();

//                                JSONArray distributors = response.getJSONArray("distributors");
//                                if(distributors!=null)
//                                {
//                                    for (int i = 0; i < distributors.length(); i++) {
//
//                                        JSONObject disObj = (JSONObject) distributors.get(i);
//                                        dids.add(String.valueOf(disObj.getInt("did")));
//                                        didUpdatedAt.add(disObj.getString("updated_at"));
//                                    }
//                                }
//                                JSONArray disBeatMapArr = response.getJSONArray("distBeatMap");
//                                for (int i = 0; i < disBeatMapArr.length(); i++) {
//                                    JSONObject obj = (JSONObject) disBeatMapArr.get(i);
//                                    String did = obj.getString("did");
//                                    String bid = obj.getString("bid");
//                                    salesBeatDb.insertIntoDisBeatMap(did, bid);
//                                }
//                                JSONArray beats = response.getJSONArray("beats");
//                                for (int i = 0; i < beats.length(); i++) {
//
//                                    JSONObject beatObj = (JSONObject) beats.get(i);
//                                    bids.add(String.valueOf(beatObj.getInt("bid")));
//                                    bidUpdatedAt.add(beatObj.getString("updated_at"));
//                                }


                                JSONArray retailers = response.getJSONArray("retailersBeatMap");
                                for (int i = 0; i < retailers.length(); i++) {

                                    JSONObject retObj = (JSONObject) retailers.get(i);
                                    retId.add(String.valueOf(retObj.getInt("rid")));
                                    ridUpdatedAt.add(retObj.getString("updated_at"));
                                }
                                JSONArray disSkuMapArr = response.getJSONArray("skuDistMap");
                                Log.d("TAG", "Town Retailer And Skus: " + disSkuMapArr.length());
                                Log.d("TAG", "Town Retailer And Skus-1: " + new Gson().toJson(disSkuMapArr));
                                for (int i = 0; i < disSkuMapArr.length(); i++) {
                                    JSONObject obj = (JSONObject) disSkuMapArr.get(i);
                                    String skuid = obj.getString("skuid");
                                    String did = obj.getString("did");

                                    salesBeatDb.insertIntoDisSkuMap(did, skuid);
                                }
                            }


                            if (!isCancelled) {

                                try {

                                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                                    imgLoaderDisMap.setVisibility(View.GONE);
//                                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                                    imgDisMappingDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                                    if (Status == 1) {

//                                        if (downloadDis == null) {
//                                            downloadDis = new DownloadDistributors(townName);
//                                            downloadDis.execute();
//                                        } else {
//                                            downloadDis.cancel(true);
//                                            downloadDis = null;
//                                            downloadDis = new DownloadDistributors(townName);
//                                            downloadDis.execute();
//
//                                        }

                                    } else {

                                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                                        imgLoaderDisMap.setVisibility(View.GONE);
//                                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
//                                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                    }


                                } catch (Exception e) {
                                    e.printStackTrace();

                                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                    imgLoaderDisMap.setVisibility(View.GONE);
                                    tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            e.printStackTrace();

                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                            imgLoaderDisMap.setVisibility(View.GONE);
                            tvTryAgainDisMap.setVisibility(View.VISIBLE);
                            imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                            imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();

                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                    imgLoaderDisMap.setVisibility(View.GONE);
                    tvTryAgainDisMap.setVisibility(View.VISIBLE);
                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                    imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);

                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", myPref.getString("token", ""));
                    return headers;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            getBeatDetailsReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            Volley.newRequestQueue(context).add(getBeatDetailsReq);
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            if (!isCancelled) {

                getTownDistributorsAndBeats();
                getTownRetailerAndSkus();

                //@Umesh Commented 17-08-2022
                /*JsonObjectRequest getBeatDetailsReq = new JsonObjectRequest(Request.Method.GET,
                        SbAppConstants.API_GET_MAPPING_DETAILS + "town=" + townName,null,
                        response -> {
                            try {
                                //@Umesh 10-March-2022
                                Integer Status =response.getInt("status");
                                if(response.getInt("status")==1)
                                {
                                    response= response.getJSONObject("data");
                                    dids.clear();
                                    didUpdatedAt.clear();
                                    bids.clear();
                                    bidUpdatedAt.clear();
                                    retId.clear();
                                    ridUpdatedAt.clear();

                                    JSONArray distributors = response.getJSONArray("distributors");
                                    if(distributors!=null)
                                    {
                                        for (int i = 0; i < distributors.length(); i++) {

                                            JSONObject disObj = (JSONObject) distributors.get(i);
                                            dids.add(String.valueOf(disObj.getInt("did")));
                                            didUpdatedAt.add(disObj.getString("updated_at"));
                                        }
                                    }

                                    JSONArray beats = response.getJSONArray("beats");
                                    for (int i = 0; i < beats.length(); i++) {

                                        JSONObject beatObj = (JSONObject) beats.get(i);
                                        bids.add(String.valueOf(beatObj.getInt("bid")));
                                        bidUpdatedAt.add(beatObj.getString("updated_at"));
                                    }

                                    JSONArray retailers = response.getJSONArray("retailersBeatMap");
                                    for (int i = 0; i < retailers.length(); i++) {

                                        JSONObject retObj = (JSONObject) retailers.get(i);
                                        retId.add(String.valueOf(retObj.getInt("rid")));
                                        ridUpdatedAt.add(retObj.getString("updated_at"));
                                    }

                                    JSONArray disBeatMapArr = response.getJSONArray("distBeatMap");
                                    for (int i = 0; i < disBeatMapArr.length(); i++) {
                                        JSONObject obj = (JSONObject) disBeatMapArr.get(i);
                                        String did = obj.getString("did");
                                        String bid = obj.getString("bid");
                                        salesBeatDb.insertIntoDisBeatMap(did, bid);
                                    }

                                    JSONArray disSkuMapArr = response.getJSONArray("skuDistMap");
                                    for (int i = 0; i < disSkuMapArr.length(); i++)
                                    {
                                        JSONObject obj = (JSONObject) disSkuMapArr.get(i);
                                        String skuid = obj.getString("skuid");
                                        String did = obj.getString("did");

                                        salesBeatDb.insertIntoDisSkuMap(did, skuid);
                                    }
                                }


                                if (!isCancelled) {

                                    try {

                                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                        imgLoaderDisMap.setVisibility(View.GONE);
                                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                        imgDisMappingDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                                        if(Status==1)
                                        {

                                            if (downloadDis == null) {
                                                downloadDis = new DownloadDistributors(townName);
                                                downloadDis.execute();
                                            } else {
                                                downloadDis.cancel(true);
                                                downloadDis = null;
                                                downloadDis = new DownloadDistributors(townName);
                                                downloadDis.execute();

                                            }

                                        } else {

                                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                            imgLoaderDisMap.setVisibility(View.GONE);
                                            tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                            imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                            imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();

                                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                        imgLoaderDisMap.setVisibility(View.GONE);
                                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                    }

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();

                                e.printStackTrace();

                                Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                imgLoaderDisMap.setVisibility(View.GONE);
                                tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                        imgLoaderDisMap.setVisibility(View.GONE);
                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("authorization", myPref.getString("token", ""));
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                getBeatDetailsReq.setRetryPolicy(new DefaultRetryPolicy(60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(getBeatDetailsReq);
                */


//                StringRequest getBeatDetailsReq = new StringRequest(Request.Method.GET,
//                        SbAppConstants.API_GET_MAPPING_DETAILS2+"zoneid="+myPref.getString(context.getString(R.string.zone_id_key),""),
//                        res -> {
//                            try {
//
//                                Log.e(TAG," Mapping Zone id: "+myPref.getString(context.getString(R.string.zone_id_key),""));
//                                JSONObject response = new JSONObject(res);
//
//                                Log.e(TAG," Mapping res: "+response.toString());
//
//                                String status1 = response.getString("status");
//
//                                if (status1.equalsIgnoreCase("success")) {
//
//                                    dids.clear();
//                                    didUpdatedAt.clear();
//                                    bids.clear();
//                                    bidUpdatedAt.clear();
//                                    retId.clear();
//                                    ridUpdatedAt.clear();
//
//                                    JSONArray distributors = response.getJSONArray("distributors");
//                                    for (int i = 0; i < distributors.length(); i++) {
//
//                                        JSONObject disObj = (JSONObject) distributors.get(i);
//                                        dids.add(String.valueOf(disObj.getInt("did")));
//                                        didUpdatedAt.add(disObj.getString("updated_at"));
//
//                                    }
//
//                                    JSONArray beats = response.getJSONArray("beats");
//                                    for (int i = 0; i < beats.length(); i++) {
//
//                                        JSONObject beatObj = (JSONObject) beats.get(i);
//                                        bids.add(String.valueOf(beatObj.getInt("bid")));
//                                        bidUpdatedAt.add(beatObj.getString("updated_at"));
//                                    }
//
//                                    JSONArray retailers = response.getJSONArray("retailersBeatMap");
//                                    for (int i = 0; i < retailers.length(); i++) {
//
//                                        JSONObject retObj = (JSONObject) retailers.get(i);
//                                        retId.add(String.valueOf(retObj.getInt("rid")));
//                                        ridUpdatedAt.add(retObj.getString("updated_at"));
//                                    }
//
//                                    JSONArray disBeatMapArr = response.getJSONArray("distBeatMap");
//                                    for (int i = 0; i < disBeatMapArr.length(); i++) {
//                                        JSONObject obj = (JSONObject) disBeatMapArr.get(i);
//                                        String did = obj.getString("did");
//                                        String bid = obj.getString("bid");
//                                        salesBeatDb.insertIntoDisBeatMap(did, bid);
//                                    }
//
//                                    JSONArray disSkuMapArr = response.getJSONArray("skuDistMap");
//                                    for (int i = 0; i < disSkuMapArr.length(); i++) {
//                                        JSONObject obj = (JSONObject) disSkuMapArr.get(i);
//                                        String skuid = obj.getString("skuid");
//                                        String did = obj.getString("did");
//
//                                        salesBeatDb.insertIntoDisSkuMap(did, skuid);
//                                    }
//
//                                }
//
//
//                                if (!isCancelled) {
//
//                                    try {
//
//                                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                                        imgLoaderDisMap.setVisibility(View.GONE);
//                                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                                        imgDisMappingDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);
//
//                                        if (status1.contains("success")) {
//
//                                            if (downloadDis == null) {
//                                                downloadDis = new DownloadDistributors(townName);
//                                                downloadDis.execute();
//                                            } else {
//                                                downloadDis.cancel(true);
//                                                downloadDis = null;
//                                                downloadDis = new DownloadDistributors(townName);
//                                                downloadDis.execute();
//
//                                            }
//
//                                        } else {
//
//                                            Toast.makeText(context, "" + status1, Toast.LENGTH_SHORT).show();
//                                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                                            imgLoaderDisMap.setVisibility(View.GONE);
//                                            tvTryAgainDisMap.setVisibility(View.VISIBLE);
//                                            imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                                            imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
//                                        }
//
//
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//
//                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                                        imgLoaderDisMap.setVisibility(View.GONE);
//                                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
//                                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
//                                    }
//
//                                }
//
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//
//                                e.printStackTrace();
//
//                                Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                                imgLoaderDisMap.setVisibility(View.GONE);
//                                tvTryAgainDisMap.setVisibility(View.VISIBLE);
//                                imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                                imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
//                            }
//                        }, new com.android.volley.Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        error.printStackTrace();
//                        Log.e(TAG,""+error.networkResponse.statusCode);
//
//                        Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
//                        imgLoaderDisMap.setVisibility(View.GONE);
//                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
//                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
//                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
//
//                    }
//                }) {
//
////                    @Override
////                    public byte[] getBody() {
////                        HashMap<String, String> params2 = new HashMap<String, String>();
////                        params2.put("zone_id", myPref.getString(context.getString(R.string.zone_id_key),""));
////                        return new JSONObject(params2).toString().getBytes();
////                    }
//
//                    @Override
//                    public Map<String, String> getHeaders() {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("Content-Type", "application/json; charset=utf-8");
//                        headers.put("authorization", myPref.getString("token", ""));
//                        return headers;
//                    }
//
//                    @Override
//                    public String getBodyContentType() {
//                        return "application/json";
//                    }
//                };
//
//                Volley.newRequestQueue(context).add(getBeatDetailsReq);
            }


        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);

        }
    }

    private class DownloadDistributors extends AsyncTask<Void, String, String> {

        String townName;
        JSONArray updatedAtArr = new JSONArray();

        public DownloadDistributors(String townName) {
            this.townName = townName;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvDisDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadingDis.setTextColor(Color.parseColor("#424242"));
            imgLoaderDis.setImageResource(R.drawable.loader_gif);
            imgLoaderDis.setVisibility(View.VISIBLE);
            imgDisDownloadDone.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            if (!isCancelled()) {

                for (int i = 0; i < dids.size(); i++) {

                    String did = dids.get(i);
                    String updatedAt = didUpdatedAt.get(i);

                    Cursor cursor = salesBeatDb.getDistributor(did);

                    updatedAtArr.put(Integer.parseInt(did));

                    try {

                        //JSONObject obj = new JSONObject();
//                        String updatedAtStored = "";

//                        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//
//                            updatedAtStored = cursor.getString(cursor.getColumnIndex(SalesBeatDb.KEY_LAST_UPDATED_AT));
//
//                            Log.e(TAG," didlist if:"+did);
//                            if (!updatedAt.contains(updatedAtStored)) {
//
//                                updatedAtArr.put(Integer.parseInt(did));
//
//                                salesBeatDb.deleteDistributor(did);
//                            }
//
//                        } else {
//
//                            updatedAtArr.put(Integer.parseInt(did));
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        return "error";
                    } finally {
                        if (cursor != null)
                            cursor.close();

                    }

                }

                Log.e("DistributorList", "Distributor Json-->" + updatedAtArr.toString());

            }


            return null;
        }


        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            if (!isCancelled) {

                JSONObject obj = new JSONObject();

                try {
                    obj.put("data", updatedAtArr);
                } catch (Exception ex) {

                }
                Log.e(TAG, " obj: " + obj.toString());
                JsonObjectRequest getDistListReq = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_GET_DISTRIBUTORS, obj,
                        new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {
                                    //@Umesh 12-March-2022
                                    if (response.getInt("status") == 1) {
                                        JSONObject object = response.getJSONObject("data");

                                        salesBeatDb.deleteAllDataFromDistributorListTable();

                                        JSONArray distributors = object.getJSONArray("distributors");
                                        if (distributors != null) {
                                            for (int i = 0; i < distributors.length(); i++) {
                                                JSONObject obj = (JSONObject) distributors.get(i);

                                                JSONObject zoneObj = obj.getJSONObject("zones");

                                                String gstn = "";

                                                if (!obj.isNull("gstin") && obj.has("gstin"))
                                                    gstn = obj.getString("gstin");

                                                salesBeatDb.insertDistributorList2(obj.getString("did"),
                                                        obj.getString("name"), obj.getString("town"),
                                                        obj.getString("phone1"), obj.getString("email1"),
                                                        obj.getString("type"), obj.getString("address"),
                                                        obj.getString("district"), zoneObj.getString("zone"),
                                                        obj.getString("state"), obj.getString("pincode"),
                                                        obj.getString("latitude"), obj.getString("longitude"),
                                                        gstn, obj.getString("updated_at"));

                                            }
                                        }

                                        if (!object.isNull("deletedData") && object.has("deletedData")) {

                                            JSONArray deleteDistributors = object.getJSONArray("deletedData");

                                            for (int i = 0; i < deleteDistributors.length(); i++) {
                                                String did = (String) deleteDistributors.get(i);
                                                salesBeatDb.deleteDistributor(did);
                                            }
                                        }


                                        if (!isCancelled) {

                                            try {

                                                //tvDisDownloadProgress.setVisibility(View.GONE);
                                                imgLoaderDis.setVisibility(View.GONE);
                                                imgDisDownloadDone.setVisibility(View.VISIBLE);
                                                imgDisDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                                                if (response.getInt("status") == 1) {

                                                    if (downloadBeats == null) {
                                                        downloadBeats = new DownloadBeats(townName);
                                                        downloadBeats.execute();
                                                    } else {
                                                        downloadBeats.cancel(true);
                                                        downloadBeats = null;
                                                        downloadBeats = new DownloadBeats(townName);
                                                        downloadBeats.execute();
                                                    }

                                                } else {

                                                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                                    //tvDisDownloadProgress.setVisibility(View.GONE);
                                                    imgLoaderDis.setVisibility(View.GONE);
                                                    imgDisDownloadDone.setVisibility(View.VISIBLE);
                                                    tvTryAgainDis.setVisibility(View.VISIBLE);
                                                    imgDisDownloadDone.setImageResource(R.drawable.refresh);
                                                }

                                            } catch (Exception e) {

                                                Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                //tvDisDownloadProgress.setVisibility(View.GONE);
                                                imgLoaderDis.setVisibility(View.GONE);
                                                imgDisDownloadDone.setVisibility(View.VISIBLE);
                                                tvTryAgainDis.setVisibility(View.VISIBLE);
                                                imgDisDownloadDone.setImageResource(R.drawable.refresh);
                                                e.printStackTrace();
                                            }
                                        }

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                    imgLoaderDisMap.setVisibility(View.GONE);
                                    tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                }
                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                        imgLoaderDisMap.setVisibility(View.GONE);
                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("authorization", myPref.getString("token", ""));
                        return headers;
                    }
                };

                getDistListReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(getDistListReq);


            }
        }
    }

    //@Umesh 20220916
    private class DownloadBeatsOld extends AsyncTask<Void, String, String> {

        JSONArray updatedAtArr = new JSONArray();

        String townName;

        public DownloadBeatsOld(String townName) {
            this.townName = townName;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvBeatDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadingBeats.setTextColor(Color.parseColor("#424242"));
            imgLoaderBeat.setImageResource(R.drawable.loader_gif);
            imgLoaderBeat.setVisibility(View.VISIBLE);
            imgBeatDownloadDone.setVisibility(View.GONE);

            //beatIdList.clear();
        }

        @Override
        protected String doInBackground(Void... voids) {

            for (int i = 0; i < bids.size(); i++) {

                String beatId = bids.get(i);
                String updatedAt = bidUpdatedAt.get(i);

                Cursor cursor1 = salesBeatDb.getAllDataFromBeatListTable2(beatId);
                try {

                    String updatedAtStored = "";
                    //JSONObject obj = new JSONObject();
                    if (cursor1 != null && cursor1.getCount() > 0 && cursor1.moveToFirst()) {

                        updatedAtStored = cursor1.getString(cursor1.getColumnIndex(SalesBeatDb.KEY_LAST_UPDATED_AT));
                        if (!updatedAt.contains(updatedAtStored)) {

                            updatedAtArr.put(Integer.parseInt(beatId));

                            salesBeatDb.deleteDataFromBeatListTable(beatId);
                        }

                    } else {

                        updatedAtArr.put(Integer.parseInt(beatId));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                } finally {

                    if (cursor1 != null)
                        cursor1.close();
                }

            }

            Log.e("DistributorList", "Beat Json-->" + updatedAtArr.toString());

            return null;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            if (!isCancelled) {

                JSONObject obj = new JSONObject();

                try {
                    obj.put("data", updatedAtArr);
                } catch (Exception ex) {

                }
                JsonObjectRequest getBeatListReq =
                        new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_GET_BEATS, obj,
                                new com.android.volley.Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        try {

                                            //@Umesh 12-March-2022
                                            if (response.getInt("status") == 1) {
                                                JSONObject object = response.getJSONObject("data");
                                                JSONArray beats = object.getJSONArray("beats");
                                                for (int i = 0; i < beats.length(); i++) {

                                                    JSONObject obj = (JSONObject) beats.get(i);
                                                    salesBeatDb.insertBeatList2(obj.getString("bid"),
                                                            obj.getString("name"), "", obj.getString("updated_at")
                                                            , String.valueOf(obj.getBoolean("isMinimumCheckinRange")));

                                                }

                                                if (!object.isNull("deletedData") && object.has("deletedData")) {

                                                    JSONArray deleteDistributors = object.getJSONArray("deletedData");

                                                    for (int i = 0; i < deleteDistributors.length(); i++) {

                                                        String bid = (String) deleteDistributors.get(i);
                                                        salesBeatDb.deleteDataFromBeatListTable(bid);
                                                    }
                                                }

                                                if (beats.length() == 0) {

                                                    SharedPreferences.Editor editor = tempPref.edit();
                                                    editor.putString(context.getString(R.string.beatErrorKey), "No data: beats");
                                                    editor.apply();
                                                }

                                                if (!isCancelled) {

                                                    try {

                                                        //tvBeatDownloadProgress.setVisibility(View.GONE);
                                                        imgLoaderBeat.setVisibility(View.GONE);
                                                        imgBeatDownloadDone.setVisibility(View.VISIBLE);
                                                        imgBeatDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                                                        if (response.getInt("status") == 1) {

                                                            if (downloadRetailers == null) {

                                                                downloadRetailers = new DownloadRetailers();
                                                                downloadRetailers.execute();

                                                            } else {

                                                                downloadRetailers.cancel(true);
                                                                downloadRetailers = null;
                                                                downloadRetailers = new DownloadRetailers();
                                                                downloadRetailers.execute();

                                                            }

                                                        } else {

                                                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + response.getString("message"), Toast.LENGTH_SHORT).show();
                                                            //tvBeatDownloadProgress.setVisibility(View.GONE);
                                                            imgLoaderBeat.setVisibility(View.GONE);
                                                            imgBeatDownloadDone.setVisibility(View.VISIBLE);
                                                            tvTryAgainBeat.setVisibility(View.VISIBLE);
                                                            imgBeatDownloadDone.setImageResource(R.drawable.refresh);
                                                        }

                                                    } catch (Exception e) {

                                                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        //tvBeatDownloadProgress.setVisibility(View.GONE);
                                                        imgLoaderBeat.setVisibility(View.GONE);
                                                        imgBeatDownloadDone.setVisibility(View.VISIBLE);
                                                        tvTryAgainBeat.setVisibility(View.VISIBLE);
                                                        imgBeatDownloadDone.setImageResource(R.drawable.refresh);
                                                        e.printStackTrace();

                                                    }
                                                }

                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();

                                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                            imgLoaderDisMap.setVisibility(View.GONE);
                                            tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                            imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                            imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                        }
                                    }

                                }, new com.android.volley.Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                error.printStackTrace();

                                Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                imgLoaderDisMap.setVisibility(View.GONE);
                                tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                            }
                        }) {

                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("authorization", myPref.getString("token", ""));
                                return headers;
                            }
                        };

                getBeatListReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(getBeatListReq);

            }

        }
    }

    private class DownloadBeats extends AsyncTask<Void, String, String> {

        JSONArray updatedAtArr = new JSONArray();

        String townName;

        public DownloadBeats(String townName) {
            this.townName = townName;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvBeatDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadingBeats.setTextColor(Color.parseColor("#424242"));
            imgLoaderBeat.setImageResource(R.drawable.loader_gif);
            imgLoaderBeat.setVisibility(View.VISIBLE);
            imgBeatDownloadDone.setVisibility(View.GONE);

            //beatIdList.clear();
        }

        @Override
        protected String doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            try {

                //@Umesh 12-March-2022
                salesBeatDb.deleteDataFromBeatListTable();

                for (int i = 0; i < BeatItemList.size(); i++) {
                    salesBeatDb.insertBeatList2(BeatItemList.get(i).getBeatId(),
                            BeatItemList.get(i).getBeatName(), "", BeatItemList.get(i).getBeatUpdatedAt()
                            , String.valueOf(BeatItemList.get(i).isMinimumCheckinRange()));
                }
                if (BeatItemList.size() == 0) {

                    SharedPreferences.Editor editor = tempPref.edit();
                    editor.putString(context.getString(R.string.beatErrorKey), "No data: beats");
                    editor.apply();
                }
                if (!isCancelled) {

                    try {

                        //tvBeatDownloadProgress.setVisibility(View.GONE);
                        imgLoaderBeat.setVisibility(View.GONE);
                        imgBeatDownloadDone.setVisibility(View.VISIBLE);
                        imgBeatDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                        if (BeatItemList.size() > 0) {
                            if (downloadRetailers == null) {

                                downloadRetailers = new DownloadRetailers();
                                downloadRetailers.execute();

                            } else {

                                downloadRetailers.cancel(true);
                                downloadRetailers = null;
                                downloadRetailers = new DownloadRetailers();
                                downloadRetailers.execute();

                            }

                        } else {

                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + " No Beats Found!!", Toast.LENGTH_SHORT).show();
                            //tvBeatDownloadProgress.setVisibility(View.GONE);
                            imgLoaderBeat.setVisibility(View.GONE);
                            imgBeatDownloadDone.setVisibility(View.GONE);
                            tvTryAgainBeat.setVisibility(View.VISIBLE);
                            imgBeatDownloadDone.setImageResource(R.drawable.refresh);
                        }

                    } catch (Exception e) {

                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        //tvBeatDownloadProgress.setVisibility(View.GONE);
                        imgLoaderBeat.setVisibility(View.GONE);
                        imgBeatDownloadDone.setVisibility(View.VISIBLE);
                        tvTryAgainBeat.setVisibility(View.VISIBLE);
                        imgBeatDownloadDone.setImageResource(R.drawable.refresh);
                        e.printStackTrace();

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                imgLoaderDisMap.setVisibility(View.GONE);
                tvTryAgainDisMap.setVisibility(View.VISIBLE);
                imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
            }
        }
    }

    private class DownloadRetailers extends AsyncTask<Void, String, String> {

        JSONArray updatedAtArr = new JSONArray();

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvRetDownloadProgress.setVisibility(View.VISIBLE);
            tvDownloadingRet.setTextColor(Color.parseColor("#424242"));
            imgLoaderRet.setImageResource(R.drawable.loader_gif);
            imgLoaderRet.setVisibility(View.VISIBLE);
            imgRetDownloadDone.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            if (!isCancelled()) {

//                HttpClient httpClient = new DefaultHttpClient();
//                HttpPost postRequest = new HttpPost(SbAppConstants.API_GET_RETAILERS);
//                postRequest.addHeader("authorization", myPref.getString("token", ""));
//                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                try {

                    for (int i = 0; i < retId.size(); i++) {

                        String rid = retId.get(i);
                        String updatedAt = ridUpdatedAt.get(i);

                        Cursor retCur = salesBeatDb.getRetailer(rid);

                        try {

                            String updatedAtStored = "";
                            //JSONObject obj = new JSONObject();
                            if (retCur != null && retCur.getCount() > 0 && retCur.moveToFirst()) {

                                updatedAtStored = retCur.getString(retCur.getColumnIndex(SalesBeatDb.KEY_LAST_UPDATED_AT));

                                if (!updatedAt.contains(updatedAtStored)) {

                                    updatedAtArr.put(Integer.parseInt(rid));

                                    salesBeatDb.deleteDataFromRetailerListTable(rid);
                                }
                            } else {

                                updatedAtArr.put(Integer.parseInt(rid));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            return "error";
                        } finally {
                            if (retCur != null)
                                retCur.close();
                        }

                    }

                    Log.e("DistributorList", "Retailer Json-->" + updatedAtArr.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    return "error";
                }

//                try {
//
//                    reqEntity.addPart("data", new StringBody(updatedAtArr.toString()));
//                    postRequest.setEntity(reqEntity);
//
//                    HttpResponse response = httpClient.execute(postRequest);
//
//                    StatusLine statusLine = response.getStatusLine();
//                    int statusCode = statusLine.getStatusCode();
//                    Log.e("DistributorList", "Retailer Response Status code-->" + statusCode);
//
//                    if (statusCode == 200) {
//
//                        HttpEntity entity = response.getEntity();
//                        InputStream content = entity.getContent();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                        String res = reader.readLine();
//
//                        Log.e("Retailers Response is", "::" + res);
//
//                        JSONObject object = new JSONObject(res);
//
//                        String status = object.getString("status");
//                        String msg = object.getString("statusMessage");
//
//                        if (status.equalsIgnoreCase("success")) {
//
//                            JSONArray retArr = object.getJSONArray("retailers");
//                            for (int i = 0; i < retArr.length(); i++) {
//
//                                JSONObject object2 = (JSONObject) retArr.get(i);
//
//                                JSONObject zoneObj = object2.getJSONObject("zone");
//
//                                salesBeatDb.insertRetailerList2(object2.getString("rid"),
//                                        object2.getString("bid"),
//                                        object2.getString("ruid"),
//                                        object2.getString("name"),
//                                        object2.getString("address"),
//                                        object2.getString("state"),
//                                        object2.getString("email"),
//                                        object2.getString("shopPhone"),
//                                        object2.getString("ownersName"),
//                                        object2.getString("ownersPhone1"),
//                                        object2.getString("whatsappNo"),
//                                        object2.getString("latitude"),
//                                        object2.getString("longitude"),
//                                        object2.getString("gstin"),
//                                        object2.getString("pin"),
//                                        object2.getString("fssai"),
//                                        object2.getString("district"),
//                                        object2.getString("locality"),
//                                        zoneObj.getString("zone"),
//                                        object2.getString("target"),
//                                        object2.getString("outletChannel"),
//                                        object2.getString("shopType"),
//                                        object2.getString("grade"),
//                                        object2.getString("image"),
//                                        "",
//                                        object2.getString("updated_at"));
//
//                            }
//
//                            if (!object.isNull("deletedData") && object.has("deletedData")) {
//
//                                JSONArray deleteDistributors = object.getJSONArray("deletedData");
//
//                                for (int i = 0; i < deleteDistributors.length(); i++) {
//
//                                    String rid = (String) deleteDistributors.get(i);
//                                    salesBeatDb.deleteDataFromRetailerListTable(rid);
//                                }
//
//                            }
//
//                            if (retArr.length() == 0){
//
//                                SharedPreferences.Editor editor = tempPref.edit();
//                                editor.putString(context.getString(R.string.retErrorKey),"No data: "+msg);
//                                editor.apply();
//                            }
//
//                        }
//
//                        return status;
//
//                    } else {
//                        //Log.e("Error....", "Failed to download file");
//                        return "error";
//                    }
//
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//
//                    SharedPreferences.Editor editor = tempPref.edit();
//                    editor.putString(context.getString(R.string.retErrorKey),"Exception: "+e1.getMessage());
//                    editor.apply();
//
//                    return "error";
//                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            if (!isCancelled) {
                JSONObject obj = new JSONObject();

                try {
                    obj.put("data", updatedAtArr);
                } catch (Exception ex) {

                }
                Log.d(TAG, "Retailer Value: " + new Gson().toJson(obj));
                JsonObjectRequest getRetailersReq = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_GET_RETAILERS, obj,
                        new com.android.volley.Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject res) {

                                try {
                                    //@Umesh 12-03-2022
                                    if (res.getInt("status") == 1) {
                                        JSONObject object = res.getJSONObject("data");
                                        JSONArray retArr = object.getJSONArray("retailers");
                                        for (int i = 0; i < retArr.length(); i++) {

                                            JSONObject object2 = (JSONObject) retArr.get(i);

                                            float distance = findDistance(object2.getString("latitude"), object2.getString("longitude"));
//                                            Log.d("TAG", "Adapter Distance: "+distance);
                                            JSONObject zoneObj = object2.getJSONObject("zones");

                                            salesBeatDb.insertRetailerList2(object2.getString("rid"),
                                                    object2.getString("bid"),
                                                    object2.getString("ruid"),
                                                    object2.getString("name"),
                                                    object2.getString("address"),
                                                    object2.getString("state"),
                                                    object2.getString("email"),
                                                    object2.getString("shopPhone"),
                                                    object2.getString("ownersName"),
                                                    object2.getString("ownersPhone1"),
                                                    object2.getString("whatsappNo"),
                                                    object2.getString("latitude"),
                                                    object2.getString("longitude"),
                                                    distance,
                                                    object2.getString("gstin"),
                                                    object2.getString("pin"),
                                                    object2.getString("fssai"),
                                                    object2.getString("district"),
                                                    object2.getString("locality"),
                                                    zoneObj.getString("zone"),
                                                    object2.getString("target"),
                                                    object2.getString("outletChannel"),
                                                    object2.getString("shopType"),
                                                    object2.getString("grade"),
                                                    object2.getString("image"),
                                                    "",
                                                    object2.getString("updated_at"));

                                        }

                                        if (!object.isNull("deletedData") && object.has("deletedData")) {

                                            JSONArray deleteDistributors = object.getJSONArray("deletedData");

                                            for (int i = 0; i < deleteDistributors.length(); i++) {

                                                String rid = (String) deleteDistributors.get(i);
                                                salesBeatDb.deleteDataFromRetailerListTable(rid);
                                            }

                                        }

                                        if (retArr.length() == 0) {

                                            SharedPreferences.Editor editor = tempPref.edit();
                                            editor.putString(context.getString(R.string.retErrorKey), "No data: Retailers");
                                            editor.apply();
                                        }

                                        if (!isCancelled) {

                                            try {

                                                //tvRetDownloadProgress.setVisibility(View.GONE);
                                                imgLoaderRet.setVisibility(View.GONE);
                                                imgRetDownloadDone.setVisibility(View.VISIBLE);
                                                imgRetDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);

                                                if (res.getInt("status") == 1) {

                                                    if (goToFragment == null) {

                                                        goToFragment = new GoToFragment();
                                                        goToFragment.execute();

                                                    } else {

                                                        goToFragment.cancel(true);
                                                        goToFragment = null;
                                                        goToFragment = new GoToFragment();
                                                        goToFragment.execute();
                                                    }

                                                } else {

                                                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + status, Toast.LENGTH_SHORT).show();
                                                    //tvBeatDownloadProgress.setVisibility(View.GONE);
                                                    imgLoaderRet.setVisibility(View.GONE);
                                                    imgRetDownloadDone.setVisibility(View.VISIBLE);
                                                    tvTryAgainRet.setVisibility(View.VISIBLE);
                                                    imgRetDownloadDone.setImageResource(R.drawable.refresh);
                                                }


                                            } catch (Exception e) {

                                                imgLoaderRet.setVisibility(View.GONE);
                                                imgRetDownloadDone.setVisibility(View.VISIBLE);
                                                tvTryAgainRet.setVisibility(View.VISIBLE);
                                                imgRetDownloadDone.setImageResource(R.drawable.refresh);
                                                Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();

                                            }
                                        }


                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                                    imgLoaderDisMap.setVisibility(View.GONE);
                                    tvTryAgainDisMap.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                                    imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                                }

                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();

                        Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber() + ":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                        imgLoaderDisMap.setVisibility(View.GONE);
                        tvTryAgainDisMap.setVisibility(View.VISIBLE);
                        imgDisMappingDownloadDone.setVisibility(View.VISIBLE);
                        imgDisMappingDownloadDone.setImageResource(R.drawable.refresh);
                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("authorization", myPref.getString("token", ""));
                        return headers;
                    }
                };

                getRetailersReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                Volley.newRequestQueue(context).add(getRetailersReq);

            }

//            if (!isCancelled) {
//
//                try {
//
//                    //tvRetDownloadProgress.setVisibility(View.GONE);
//                    imgLoaderRet.setVisibility(View.GONE);
//                    imgRetDownloadDone.setVisibility(View.VISIBLE);
//                    imgRetDownloadDone.setImageResource(R.drawable.ic_done_white_36dp);
//
//                    if (status.contains("success")) {
//
//                        if (goToFragment == null) {
//
//                            goToFragment = new GoToFragment();
//                            goToFragment.execute();
//
//                        } else {
//
//                            goToFragment.cancel(true);
//                            goToFragment = null;
//                            goToFragment = new GoToFragment();
//                            goToFragment.execute();
//                        }
//
//                    } else {
//
//                        Toast.makeText(context, "" + status, Toast.LENGTH_SHORT).show();
//                        //tvBeatDownloadProgress.setVisibility(View.GONE);
//                        imgLoaderRet.setVisibility(View.GONE);
//                        imgRetDownloadDone.setVisibility(View.VISIBLE);
//                        tvTryAgainRet.setVisibility(View.VISIBLE);
//                        imgRetDownloadDone.setImageResource(R.drawable.refresh);
//                    }
//
//
//                } catch (Exception e) {
//
//                    imgLoaderRet.setVisibility(View.GONE);
//                    imgRetDownloadDone.setVisibility(View.VISIBLE);
//                    tvTryAgainRet.setVisibility(View.VISIBLE);
//                    imgRetDownloadDone.setImageResource(R.drawable.refresh);
//                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//
//                }
//            }

        }
    }

    public static float findDistance(String latitudeB, String longitudeB) {
        GPSLocation locationProvider = new GPSLocation();

        double latA = locationProvider.getLatitude();
        double lonB = locationProvider.getLongitude();

        Location locationA = new Location("point A");
        locationA.setLatitude(latA);
        locationA.setLongitude(lonB);

        Location locationB = new Location("point B");
        if (latitudeB != null && longitudeB != null) {
            try {
                locationB.setLatitude(Double.parseDouble(latitudeB));
                locationB.setLongitude(Double.parseDouble(longitudeB));
                // Use locationB as needed
            } catch (NumberFormatException e) {
                // Handle the exception if the strings are not valid doubles
                System.err.println("Invalid latitude or longitude value: " + e.getMessage());
            }
        } else {
            // Handle the case where latitudeB or longitudeB is null
            System.err.println("Latitude or longitude is null");
        }

        // Calculate the distance in meters
//        float distance = locationA.distanceTo(locationB);

        return locationA.distanceTo(locationB);
    }

    private class GoToFragment extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {

            if (!isCancelled()) {

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Boolean flag) {
            super.onPostExecute(flag);

            if (flag && !isCancelled) {

                if (view != null && view.isShowing())
                    view.dismiss();

                Bundle bundle = new Bundle();
                bundle.putString("from", "town");
                FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
                //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
                Fragment fragment = new DistributorList();
                fragment.setArguments(bundle);
                ft.replace(R.id.flContainer, fragment);
                ft.commitAllowingStateLoss();

            }

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTownName;
        RelativeLayout rlTownLayout;
        TextView icon1;
        ImageView icon2;

        public ViewHolder(View itemView) {
            super(itemView);
            //Layouts Linear,Relative
            rlTownLayout = itemView.findViewById(R.id.rlTownLayout);
            //TextViews
            tvTownName = itemView.findViewById(R.id.tvTownName);
            icon1 = itemView.findViewById(R.id.townIcon);
            //ImageViews
            icon2 = itemView.findViewById(R.id.nextIcon);

        }
    }

}
