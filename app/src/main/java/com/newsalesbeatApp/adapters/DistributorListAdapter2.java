package com.newsalesbeatApp.adapters;

import static com.newsalesbeatApp.utilityclass.UtilityClass.isValidBase64;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.newsalesbeatApp.activities.RetailerVisitHistoryActivity;
import com.newsalesbeatApp.customview.AnimCheckBox;
import com.newsalesbeatApp.fragments.SkusFragment;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.pojo.DistrebutorItem;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SbAppConstants;
import com.newsalesbeatApp.utilityclass.UtilityClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;

public class DistributorListAdapter2 extends RecyclerView.Adapter<DistributorListAdapter2.ViewHolder>
        implements Filterable, NetworkChangeInterface {

    String TAG = "DistributorListAdapter";
    //
//    private Paint thresPaint;
//    Snackbar snackbar;
//    RecyclerView tempV;
    String tName;
    private ArrayList<DistrebutorItem> distrebutorItemList = new ArrayList<>();
    private ArrayList<DistrebutorItem> distrebutorItemList2 = new ArrayList<>();
    private Context context;
    private SharedPreferences tempPref, myPerf;
    private ClientInterface apiInterface;
    private Boolean fetchSku = false;
    //    private static FirebaseAnalytics firebaseAnalytics;
//
    private UtilityClass utilityClass;
    //
    private SalesBeatDb salesBeatDb;
    private ArrayList<String> dids = new ArrayList<>();
    private ArrayList<String> didUpdatedAt = new ArrayList<>();
    private ArrayList<String> bids = new ArrayList<>();
    private ArrayList<String> bidUpdatedAt = new ArrayList<>();
    private ArrayList<String> retId = new ArrayList<>();
    private ArrayList<String> ridUpdatedAt = new ArrayList<>();
    //private ArrayList<String> beatIdList = new ArrayList<>();

    public DistributorListAdapter2(Context ctx, ArrayList<DistrebutorItem> items, String townName) {

        try {

            this.context = ctx;
            this.distrebutorItemList = items;
            this.distrebutorItemList2 = items;
            this.tName = townName;
            tempPref = ctx.getSharedPreferences(ctx.getString(R.string.temp_pref_name), Context.MODE_PRIVATE);
            myPerf = ctx.getSharedPreferences(ctx.getString(R.string.pref_name), Context.MODE_PRIVATE);
            utilityClass = new UtilityClass(context);
            //salesBeatDb = new SalesBeatDb(context);
            salesBeatDb = SalesBeatDb.getHelper(ctx);

            NetworkChangeReceiver receiver = new NetworkChangeReceiver();
            receiver.InitNetworkListener(this);
//            firebaseAnalytics = FirebaseAnalytics.getInstance(ctx);
//            this.tempV = rcvDistrebutorList;
//
//            thresPaint = new Paint();
//            thresPaint.setColor(Color.parseColor("#dad8d6"));
//            thresPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
//            thresPaint.setStyle(Paint.Style.STROKE);
//            thresPaint.setAntiAlias(true);
//            thresPaint.setStrokeWidth(Tools.fromDpToPx(.75f));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.distributor_list_row2, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.disIcon.setText(distrebutorItemList.get(position).getDistrebutorName());
        holder.tvDistName.setText(distrebutorItemList.get(position).getDistrebutorName());
        //holder.tvDistrebuterAddressClosing.setText(distrebutorItemList.get(position).getDistrebutor_address());

        Cursor cursor = SalesBeatDb.getHelper(context)
                .getAllDataFromClosingEntryListTable2(distrebutorItemList.get(position).getDistrebutorId());

        if (cursor != null && cursor.getCount() > 0)
            holder.chbIsClosingTaken.setVisibility(View.VISIBLE);
        else
            holder.chbIsClosingTaken.setVisibility(View.GONE);

        holder.tvClosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getSkuList(position);

                new DownloadMappingDetails(tName, position).execute();

            }
        });

        holder.tvClosingHis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (utilityClass.isInternetConnected()) {

                    Intent intent = new Intent(context, RetailerVisitHistoryActivity.class);
                    intent.putExtra("did", distrebutorItemList.get(position).getDistrebutorId());
                    intent.putExtra("name", distrebutorItemList.get(position).getDistrebutorName());
                    intent.putExtra("from", "dis_closing");
                    context.startActivity(intent);

                } else {
                    Toast.makeText(context, "Not connected to internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getSkuList() {


        apiInterface = RetrofitClient.getClient().create(ClientInterface.class);

        Call<JsonObject> jsonObjectCall = apiInterface.getProductList(myPerf.getString("token", ""));
        jsonObjectCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                fetchSku = false;
                if (response.isSuccessful()) {
                    // called when response HTTP status is "200 OK"
                    Log.e(TAG, "Response Sku List=-=" + response);

                    try {

                        JSONObject JsonResponse = new JSONObject(response.body().toString());
                        JSONArray products = JsonResponse.getJSONArray("sku");
                        Log.e(TAG, "Response Sku products =" + new Gson().toJson(products));
                        //clearr previous data from DB
                        if (products.length() > 0) {
                            salesBeatDb.deleteAllDataFromSkuDetailsTable();
                        }


                        for (int i = 0; i < products.length(); i++) {

                            JSONObject object = (JSONObject) products.get(i);

                            try {
                                Log.e(TAG, "Image Name: "+object.getString("image"));
                                String imageBase = object.getString("image");
//                                String imageBase = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEMAAAAYCAMAAACWXbB1AAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABXUExURUdwTOA5NNo1MstFJtQ2L8tLINRAK8o4N8xFJeQxNtgvM7srK8ouMP/+/uZFRepgWOQ6POhQTexxZumGg/ro6MFPTfbU0+idm85mZcx/eui6uXileV21gLBfi1wAAAAJdFJOUwDcvUmaEW34Kn/heG0AAAI8SURBVDjLhZWLlqMgEERVSBAERBBfmf//zq0GH+jkzBaJAqGu3R3Fqqqq1+vVti1jb4hz3mTVD+VZ/M5pHWMMHjgrEq9reZM7ZJ09NVDblQZZxH5VrbrM1E7AYT7UU3tqsM6BcY9B3ginP8vnVggU11bsFoX8TbiuXtg/6wEZbMEoa5EqcRbgoFxBfH5+AAmZwar3PRNZVDND+isTEBKkjz+fK47MUM57WKT13rkheHyD9aEnhp/n0GOcBFKYMTFE7+lM9bDvikullDAmjGO/mNGpgMNqzGgwZ/sJJ7NZGkF9SJ0JqDQG/WBojXXGzGAotYKxYB0mJruZMcbVi3XdzLSGfjRbHIkxoxNnQjhecUJ0xizrrMHQGgyFjtCzmcSIWOzicUthNAwLDjZSb6VwAhDEaIBIjE6DYciaGE4EM2liINE+MSycYz+knlg3pIUquszousTodEQZ8MuoUxzEQC4BqSNiclpaMFOO1s9+mUzAfyhvjK5DDUbYKJ/MiFTTcXZOgOEsmFTtybkt1RYEKZuqJkQXY9ftZ0H9GLUWdOjiEoVCi3FwTmkaUg/zS8yP2sF4SFM7BcQup+jCTt3ubTDUN0hBUfRRlyQRSkpd4Rr/gSROxsijyRuDU6Baf4VkiirjkA+AdA3DTsi40ImjT5S+hUEIfSRyQ6jmnTdDqH3zZi+cVrpU4X/kUDecnYBdrwTa0/6lwo9HFPb26S9JDChs6kKUAFLa2/n7L/cddbwv8MagAzvfA18A/wCODEqJtiSYGgAAAABJRU5ErkJggg==";

                                String base64Data = imageBase.substring(imageBase.indexOf(",") + 1);
                                Log.d(TAG, "image base 64 Data: "+base64Data);

                                if (imageBase.contains("data:image/png;base64,")) {
                                    salesBeatDb.insertSkuDetailsTable(
                                            object.getString("skuid"),
                                            object.getString("sku"),
                                            object.getString("brandName"),
                                            object.getString("price"),
                                            object.getString("unit"),
                                            object.getString("conversionFactor"),
                                            base64Data
                                    );
                                } else {
                                    salesBeatDb.insertSkuDetailsTable(
                                            object.getString("skuid"),
                                            object.getString("sku"),
                                            object.getString("brandName"),
                                            object.getString("price"),
                                            object.getString("unit"),
                                            object.getString("conversionFactor"),
                                            "" // Insert a blank string
                                    );
                                }

                                /*salesBeatDb.insertSkuDetailsTable(object.getString("skuid"), object.getString("sku"),
                                        object.getString("brand"), object.getString("price"), object.getString("unit"),
                                        object.getString("conversionFactor"));
*/
                            } catch (Exception sqle) {

                                sqle.printStackTrace();
                            }
                        }

                        String status = JsonResponse.getString("status");
                        if (status.equalsIgnoreCase("success")) {


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.e(TAG, "" + response.code());
                    Toast.makeText(context, "" + response.message() + " : " + response.code(), Toast.LENGTH_SHORT).show();
                    //serverCall.handleError2(statusCode,TAG, "getSku", res);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Get Product List Fialure " + t.getMessage());
                if (!NetworkChangeReceiver.IS_CONNECTED)
                    fetchSku = true;
            }
        });

/*

            final int DEFAULT_TIMEOUT = 60 * 1000;
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(DEFAULT_TIMEOUT);
            client.addHeader("authorization", myPerf.getString("token", ""));
            client.post(SbAppConstants.API_GET_PRODUCT_LIST, null, new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String res) {
                            // called when response HTTP status is "200 OK"
                            Log.e(TAG, "Response Sku List===" + res);

                            try {

                                JSONObject JsonResponse = new JSONObject(res);
                                JSONArray products = JsonResponse.getJSONArray("sku");

                                //clearr previous data from DB
                                if (products.length() > 0) {
                                    salesBeatDb.deleteAllDataFromSkuDetailsTable();
                                }


                                for (int i = 0; i < products.length(); i++) {

                                    JSONObject object = (JSONObject) products.get(i);

                                    try {

                                        salesBeatDb.insertSkuDetailsTable(object.getString("skuid"), object.getString("sku"),
                                                object.getString("brand"), object.getString("price"), object.getString("unit"),
                                                object.getString("conversionFactor"));

                                    } catch (Exception sqle) {

                                        sqle.printStackTrace();
                                    }
                                }

                                String status = JsonResponse.getString("status");
                                if (status.equalsIgnoreCase("success")) {


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.e(TAG, "" + statusCode);
                            Toast.makeText(context, "" + res + " : " + statusCode, Toast.LENGTH_SHORT).show();
                            //serverCall.handleError2(statusCode,TAG, "getSku", res);
                        }
                    }
            );

 */
    }

    @Override
    public void connectionChange(boolean status) {
//        Toast.makeText(context, "Distributor List "+status, Toast.LENGTH_SHORT).show();

        if (fetchSku) {
            if (status) {
                getSkuList();
            } else {
                Toast.makeText(context, "Reconnecting", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goForStock(int position) {

        Log.e("HEEy", "======????");

        SharedPreferences.Editor editor = tempPref.edit();
        editor.putString(context.getString(R.string.dis_id_key), distrebutorItemList.get(position).getDistrebutorId());
        editor.putString(context.getString(R.string.dis_name_key), distrebutorItemList.get(position).getDistrebutorName());
        editor.apply();

        Bundle bundle = new Bundle();
        bundle.putString("from", "closing");

        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        //ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        Fragment fragment = new SkusFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.frmClosing, fragment);
        ft.commit();
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

    private class DownloadMappingDetails extends AsyncTask<Void, String, String> {

        String townName;
        int position;

        public DownloadMappingDetails(String townName, int position) {
            this.townName = townName;
            this.position = position;
        }

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvDisMappingDownloadProgress.setVisibility(View.VISIBLE);
//            tvDownloadingDisMap.setTextColor(Color.parseColor("#424242"));
//            imgLoaderDisMap.setImageResource(R.drawable.loader_gif);
//            imgLoaderDisMap.setVisibility(View.VISIBLE);
//            imgDisMappingDownloadDone.setVisibility(View.GONE);
            salesBeatDb.deleteDisBeatMap();
            salesBeatDb.deleteDisSkuMap();

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

//                HttpClient httpClient = new DefaultHttpClient();
//                String url = SbAppConstants.API_GET_MAPPING_DETAILS + "town=" + townName;
//                HttpGet getRequest = new HttpGet(url);
//                getRequest.addHeader("authorization", myPerf.getString("token", ""));
//
//                try {
//
//
//                    HttpResponse response = httpClient.execute(getRequest);
//
//                    StatusLine statusLine = response.getStatusLine();
//                    int statusCode = statusLine.getStatusCode();
//                    Log.e("DistributorList", "Dis Map Response Status code-->" + statusCode);
//                    if (statusCode == 200) {
//
//                        HttpEntity entity = response.getEntity();
//                        InputStream content = entity.getContent();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
//                        String res = reader.readLine();
//
//                        Log.e("Mapping Response is", "::" + res);
//
//                        JSONObject object = new JSONObject(res);
//
//                        String status = object.getString("status");
//
//                        if (status.equalsIgnoreCase("success")) {
//
//                            dids.clear();
//                            didUpdatedAt.clear();
//                            bids.clear();
//                            bidUpdatedAt.clear();
//                            retId.clear();
//                            ridUpdatedAt.clear();
//
//                            JSONArray distributors = object.getJSONArray("distributors");
//                            for (int i = 0; i < distributors.length(); i++) {
//
//                                JSONObject disObj = (JSONObject) distributors.get(i);
//                                dids.add(String.valueOf(disObj.getInt("did")));
//                                didUpdatedAt.add(disObj.getString("updated_at"));
//
//                            }
//
//                            JSONArray beats = object.getJSONArray("beats");
//                            for (int i = 0; i < beats.length(); i++) {
//
//                                JSONObject beatObj = (JSONObject) beats.get(i);
//                                bids.add(String.valueOf(beatObj.getInt("bid")));
//                                bidUpdatedAt.add(beatObj.getString("updated_at"));
//                            }
//
//                            JSONArray retailers = object.getJSONArray("retailersBeatMap");
//                            for (int i = 0; i < retailers.length(); i++) {
//
//                                JSONObject retObj = (JSONObject) retailers.get(i);
//                                retId.add(String.valueOf(retObj.getInt("rid")));
//                                ridUpdatedAt.add(retObj.getString("updated_at"));
//                            }
//
//                            JSONArray disBeatMapArr = object.getJSONArray("distBeatMap");
//                            for (int i = 0; i < disBeatMapArr.length(); i++) {
//                                JSONObject obj = (JSONObject) disBeatMapArr.get(i);
//                                String did = obj.getString("did");
//                                String bid = obj.getString("bid");
//                                salesBeatDb.insertIntoDisBeatMap(did, bid);
//                            }
//
//                            JSONArray disSkuMapArr = object.getJSONArray("skuDistMap");
//                            for (int i = 0; i < disSkuMapArr.length(); i++) {
//                                JSONObject obj = (JSONObject) disSkuMapArr.get(i);
//                                String skuid = obj.getString("skuid");
//                                String did = obj.getString("did");
//
//                                salesBeatDb.insertIntoDisSkuMap(did, skuid);
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
//
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                    return "error";
//                }

            }

            return null;
        }
        //@Umesh 17-08-2022
        public void  getTownDistributorsAndBeats()
        {
            JsonObjectRequest getBeatDetailsReq = new JsonObjectRequest(Request.Method.GET,
                    SbAppConstants.API_GET_MAPPING_DistributorsAndBeats + "town=" + townName,null,
                    response -> {
                        try {
                            Integer Status =response.getInt("status");
                            if(response.getInt("status")==1)
                            {
                                response= response.getJSONObject("data");
                                dids.clear();
                                didUpdatedAt.clear();
                                bids.clear();
                                bidUpdatedAt.clear();
//                                retId.clear();
//                                ridUpdatedAt.clear();

                                JSONArray distributors = response.getJSONArray("distributors");
                                Log.d(TAG, "getTownDistributorsAndBeats Adapter :"+new Gson().toJson(distributors));
                                if(distributors!=null)
                                {
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
                                for (int i = 0; i < beats.length(); i++) {

                                    JSONObject beatObj = (JSONObject) beats.get(i);
                                    bids.add(String.valueOf(beatObj.getInt("bid")));
                                    bidUpdatedAt.add(beatObj.getString("updated_at"));
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
                            else{
                                goForStock(position);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();

                            e.printStackTrace();

                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();

                    Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    //tvDisMappingDownloadProgress.setVisibility(View.GONE);

                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", myPerf.getString("token", ""));
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
        public void  getTownRetailerAndSkus()
        {
            Log.d("TAG", "Town Name: "+townName);
            JsonObjectRequest getBeatDetailsReq = new JsonObjectRequest(Request.Method.GET,
                    SbAppConstants.API_GET_MAPPING_RetailerAndSkus + "town=" + townName,null,
                    response -> {
                        try {
                            Integer Status =response.getInt("status");
                            if(response.getInt("status")==1)
                            {
                                response= response.getJSONObject("data");
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
                                Log.d("TAG", "disSkuMapArr :"+disSkuMapArr.length());
                                for (int i = 0; i < disSkuMapArr.length(); i++)
                                {
                                    JSONObject obj = (JSONObject) disSkuMapArr.get(i);
                                    String skuid = obj.getString("skuid");
                                    String did = obj.getString("did");
                                    Log.d("TAG", "disSkuMapArr");
                                    salesBeatDb.insertIntoDisSkuMap(did, skuid);
                                }
                            }
                           else{
                                goForStock(position);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                            Toast.makeText(context, new Throwable().getStackTrace()[0].getLineNumber()+":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            //tvDisMappingDownloadProgress.setVisibility(View.GONE);
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();

                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("authorization", myPerf.getString("token", ""));
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

            if (!isCancelled()) {

                getTownDistributorsAndBeats();
                getTownRetailerAndSkus();

                //@Umesh Commented 17-08-2022
                /*StringRequest getDistBeatDataReq = new StringRequest(Request.Method.GET,
                        SbAppConstants.API_GET_MAPPING_DETAILS + "town=" + townName,
                        new com.android.volley.Response.Listener<String>() {
                            @Override
                            public void onResponse(String res) {

                                try {

                                    JSONObject object = new JSONObject(res);

                                    String status = object.getString("status");

                                    if (status.equalsIgnoreCase("success")) {

                                        dids.clear();
                                        didUpdatedAt.clear();
                                        bids.clear();
                                        bidUpdatedAt.clear();
                                        retId.clear();
                                        ridUpdatedAt.clear();

                                        JSONArray distributors = object.getJSONArray("distributors");
                                        if(distributors!=null)
                                        {
                                            for (int i = 0; i < distributors.length(); i++) {

                                                JSONObject disObj = (JSONObject) distributors.get(i);
                                                dids.add(String.valueOf(disObj.getInt("did")));
                                                didUpdatedAt.add(disObj.getString("updated_at"));

                                            }
                                        }

                                        JSONArray beats = object.getJSONArray("beats");
                                        for (int i = 0; i < beats.length(); i++) {

                                            JSONObject beatObj = (JSONObject) beats.get(i);
                                            bids.add(String.valueOf(beatObj.getInt("bid")));
                                            bidUpdatedAt.add(beatObj.getString("updated_at"));
                                        }

                                        JSONArray retailers = object.getJSONArray("retailersBeatMap");
                                        for (int i = 0; i < retailers.length(); i++) {

                                            JSONObject retObj = (JSONObject) retailers.get(i);
                                            retId.add(String.valueOf(retObj.getInt("rid")));
                                            ridUpdatedAt.add(retObj.getString("updated_at"));
                                        }

                                        JSONArray disBeatMapArr = object.getJSONArray("distBeatMap");
                                        for (int i = 0; i < disBeatMapArr.length(); i++) {
                                            JSONObject obj = (JSONObject) disBeatMapArr.get(i);
                                            String did = obj.getString("did");
                                            String bid = obj.getString("bid");
                                            salesBeatDb.insertIntoDisBeatMap(did, bid);
                                        }

                                        JSONArray disSkuMapArr = object.getJSONArray("skuDistMap");
                                        for (int i = 0; i < disSkuMapArr.length(); i++) {
                                            JSONObject obj = (JSONObject) disSkuMapArr.get(i);
                                            String skuid = obj.getString("skuid");
                                            String did = obj.getString("did");

                                            salesBeatDb.insertIntoDisSkuMap(did, skuid);
                                        }

                                    }

                                    if (status.equalsIgnoreCase("Success")) {
                                        goForStock(position);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("authorization", myPerf.getString("token", ""));
                        return headers;
                    }

//                    @Override
//                    public byte[] getBody() {
//                        HashMap<String, String> params2 = new HashMap<String, String>();
//                        params2.put("data", updatedAtArr.toString());
//                        return new JSONObject(params2).toString().getBytes();
//                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                Volley.newRequestQueue(context).add(getDistBeatDataReq);
                */

            }

        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDistName, disIcon, tvClosing, tvClosingHis, tvDistrebuterAddressClosing;
        AnimCheckBox chbIsClosingTaken;

        public ViewHolder(View itemView) {
            super(itemView);
            //TextViews
            tvDistName = itemView.findViewById(R.id.tvDistName);

            disIcon = itemView.findViewById(R.id.disIcon);
            tvClosing = itemView.findViewById(R.id.tvClosing);
            tvClosingHis = itemView.findViewById(R.id.tvClosingHis);
            tvDistrebuterAddressClosing = itemView.findViewById(R.id.tvDistrebuterAddressClosing);

            chbIsClosingTaken = itemView.findViewById(R.id.chbIsClosingTaken);

        }
    }

}

