package com.newsalesbeatApp.services;

import static com.newsalesbeatApp.utilityclass.UtilityClass.isValidBase64;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;
import com.newsalesbeatApp.netwotkcall.ServerCall;
import com.newsalesbeatApp.receivers.NetworkChangeInterface;
import com.newsalesbeatApp.receivers.NetworkChangeReceiver;
import com.newsalesbeatApp.sblocation.GPSLocation;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SBApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by abc on 9/29/18.
 */

public class DownloadAllListService extends IntentService implements NetworkChangeInterface {

    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_ERROR = 3;
    private final int DEFAULT_TIMEOUT = 60 * 1000;
    Bundle bundle;
    private ClientInterface apiIntentface;
    private String TAG = "DownloadAllListService";
    private SharedPreferences prefSFA;
    private SalesBeatDb salesBeatDb;
    private ServerCall serverCall;
    private ResultReceiver receiver;

    private Boolean fetchSku = false;
    private Boolean fetchData = false;
    private String town = "";


    public DownloadAllListService() {
        super(DownloadAllListService.class.getName());

        Context context = SBApplication.getInstance();
        prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        //salesBeatDb = new SalesBeatDb(context);
        salesBeatDb = SalesBeatDb.getHelper(context);
        bundle = new Bundle();
        serverCall = new ServerCall(context);

        NetworkChangeReceiver receiver = new NetworkChangeReceiver();
        receiver.InitNetworkListener(this);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        apiIntentface = RetrofitClient.getClient().create(ClientInterface.class);

        town = intent.getStringExtra("town");
        receiver = intent.getParcelableExtra("receiver");
        getSkuList();
        getAllDataList(town);
    }

    @Override
    public void connectionChange(boolean status) {

        if (fetchSku) {
            if (status) {
                getSkuList();
            } else {
                Toast.makeText(this, "Reconnecting", Toast.LENGTH_SHORT).show();
            }
        }

        if (fetchData) {
            if (status) {
                getAllDataList(town);
            } else {
                Toast.makeText(this, "Reconnecting", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void getAllDataList(final String town) {

        HashMap<String, Object> params = new HashMap<>();
        //RequestParams params = new RequestParams();
        params.put("town", town);

        Call<JsonObject> callGetAllData = apiIntentface.getAllDataList(prefSFA.getString("token", ""),
                params);

        // Retrofit
        callGetAllData.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                fetchData = false;
                if (response.isSuccessful()) {
                    // called when response HTTP status is "200 OK"
                    Log.e(TAG, "All List=====" + response.body());

                    try {
                        JSONObject JsonResponse = new JSONObject(response.body().toString());
                        String status = JsonResponse.getString("status");

                        /*-------------distributors array----------------*/
                        JSONArray distributors = JsonResponse.getJSONArray("distributors");

                        if (distributors.length() > 0) {

                            //clear previous data from DB
                            salesBeatDb.deleteAllDataFromDistributorListTable();
                            salesBeatDb.deleteDataFromBeatListTable();
                            salesBeatDb.deleteDataFromRetailerListTable();
                        }

                        for (int i = 0; i < distributors.length(); i++) {

                            JSONObject object = (JSONObject) distributors.get(i);
                            String did = object.getString("did");

                            String gstn = "";

                            if (!object.isNull("gstn") && object.has("gstn"))
                                gstn = object.getString("gstn");

                            salesBeatDb.insertDistributorList(object.getString("did"),
                                    object.getString("name"), town, object.getString("phone1"),
                                    object.getString("email1"), object.getString("type"),
                                    object.getString("address"), object.getString("district"),
                                    object.getString("zone"), object.getString("state"),
                                    object.getString("pincode"), object.getString("latitude"),
                                    object.getString("longitude"), gstn);

                            /*-------------sku array-------------*/
                            JSONArray products = object.getJSONArray("sku");
                            salesBeatDb.deleteAllDataFromSkuIdTable(did);

                            for (int p = 0; p < products.length(); p++) {

                                JSONObject pObj = (JSONObject) products.get(p);
                                salesBeatDb.insertSkuIdTable(pObj.getString("skuid"), did);
                            }

                            /*--------------beats array---------------*/
                            JSONArray beats = object.getJSONArray("beats");

                            for (int b = 0; b < beats.length(); b++) {

                                JSONObject object1 = (JSONObject) beats.get(b);
                                String beat_id = object1.getString("bid");

                                salesBeatDb.insertBeatList(object1.getString("bid"),
                                        object1.getString("name"), did,String.valueOf(object1.getBoolean("isMinimumCheckinRange")));

                                /*---------------retailers array--------------*/
                                JSONArray retailers = object1.getJSONArray("retailers");

                                for (int r = 0; r < retailers.length(); r++) {

                                    JSONObject object2 = (JSONObject) retailers.get(r);
                                    String rid = object2.getString("rid");
                                    JSONArray shopImages = object2.getJSONArray("shopImages");

                                    float distance = findDistance(object2.getString("latitude"),object2.getString("longitude"));

                                    String images = object2.getString("image");
                                    for (int s = 0; s < shopImages.length(); s++) {
                                        JSONObject imgObj = (JSONObject) shopImages.get(s);
                                        images = images.concat(",");
                                        images = images.concat(imgObj.getString("filename"));
                                    }
                                    Log.d("TAG", "calculate Distance : "+distance);
                                    salesBeatDb.insertRetailerList(rid,
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
                                            object2.getString("zone"),
                                            object2.getString("target"),
                                            object2.getString("outletChannel"),
                                            object2.getString("shopType"),
                                            object2.getString("grade"),
                                            images,
                                            beat_id);
                                }
                            }
                        }

                        if (status.equalsIgnoreCase("success")) {
                            receiver.send(DOWNLOAD_SUCCESS, bundle);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.e(TAG, "onFailure: " + response.code());
                    serverCall.handleError2(response.code(), TAG, "getBeats", response.message());
                    receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e(TAG, "onFailure: " + t.getMessage());
                if (!NetworkChangeReceiver.IS_CONNECTED)
                    fetchData = true;
            }
        });



        /*
        RequestParams params = new RequestParams();
        params.put("town", town);

        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("authorization", prefSFA.getString("token", ""));
        client.post(SbAppConstants.API_GET_BEAT_LIST, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        Log.e(TAG, "All List=====" + res);
                        try {
                            JSONObject JsonResponse = new JSONObject(res);
                            String status = JsonResponse.getString("status");

                             /*-------------distributors array----------------*/
/*                            JSONArray distributors = JsonResponse.getJSONArray("distributors");

                            if (distributors.length() > 0) {

                                //clear previous data from DB
                                salesBeatDb.deleteAllDataFromDistributorListTable();
                                salesBeatDb.deleteDataFromBeatListTable();
                                salesBeatDb.deleteDataFromRetailerListTable();
                            }

                            for (int i = 0; i < distributors.length(); i++) {

                                JSONObject object = (JSONObject) distributors.get(i);
                                String did = object.getString("did");

                                String gstn = "";

                                if (!object.isNull("gstn") && object.has("gstn"))
                                    gstn = object.getString("gstn");

                                salesBeatDb.insertDistributorList(object.getString("did"),
                                        object.getString("name"), town, object.getString("phone1"),
                                        object.getString("email1"), object.getString("type"),
                                        object.getString("address"), object.getString("district"),
                                        object.getString("zone"), object.getString("state"),
                                        object.getString("pincode"), object.getString("latitude"),
                                        object.getString("longitude"),gstn);

                            /*-------------sku array-------------*/
/*                                JSONArray products = object.getJSONArray("sku");
                                salesBeatDb.deleteAllDataFromSkuIdTable(did);

                                for (int p = 0; p < products.length(); p++) {

                                    JSONObject pObj = (JSONObject) products.get(p);
                                    salesBeatDb.insertSkuIdTable(pObj.getString("skuid"), did);
                                }

                            /*--------------beats array---------------*/
/*                                JSONArray beats = object.getJSONArray("beats");

                                for (int b = 0; b < beats.length(); b++) {

                                    JSONObject object1 = (JSONObject) beats.get(b);
                                    String beat_id = object1.getString("bid");

                                    salesBeatDb.insertBeatList(object1.getString("bid"),
                                            object1.getString("name"), did);

                                /*---------------retailers array--------------*/
/*                                    JSONArray retailers = object1.getJSONArray("retailers");

                                    for (int r = 0; r < retailers.length(); r++) {

                                        JSONObject object2 = (JSONObject) retailers.get(r);
                                        String rid = object2.getString("rid");
                                        JSONArray shopImages = object2.getJSONArray("shopImages");

                                        String images = object2.getString("image");
                                        for (int s = 0; s < shopImages.length(); s++) {
                                            JSONObject imgObj = (JSONObject) shopImages.get(s);
                                            images = images.concat(",");
                                            images = images.concat(imgObj.getString("filename"));
                                        }

                                        salesBeatDb.insertRetailerList(rid,
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
                                                object2.getString("gstin"),
                                                object2.getString("pin"),
                                                object2.getString("fssai"),
                                                object2.getString("district"),
                                                object2.getString("locality"),
                                                object2.getString("zone"),
                                                object2.getString("target"),
                                                object2.getString("outletChannel"),
                                                object2.getString("shopType"),
                                                object2.getString("grade"),
                                                images,
                                                beat_id);
                                    }
                                }
                            }

                            if (status.equalsIgnoreCase("success")) {
                                receiver.send(DOWNLOAD_SUCCESS, bundle);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.e(TAG,"onFailure: "+statusCode);
                        serverCall.handleError2(statusCode, TAG,"getBeats", res);
                        receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
                    }
                }
        );

 */
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

    private void getSkuList() {

        Call<JsonObject> callGetSku = apiIntentface.getSkuList(prefSFA.getString("token", ""));

        // Retrofit
        callGetSku.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                fetchSku = false;
                if (response.isSuccessful()) {

                    // called when response HTTP status is "200 OK"
                    Log.e(TAG, "Response Sku List=+=" + response.body().toString());

                    try {

                        JSONObject JsonResponse = new JSONObject(response.body().toString());
                        JSONArray products = JsonResponse.getJSONArray("sku");
                        Log.d(TAG, "onResponse SKU: "+new Gson().toJson(products));

                        String status = JsonResponse.getString("status");
                        if (status.equalsIgnoreCase("success")) {

                            //clearr previous data from DB
                            if (products.length() > 0) {
                                salesBeatDb.deleteAllDataFromSkuDetailsTable();
                            }


                            for (int i = 0; i < products.length(); i++) {

                                JSONObject object = (JSONObject) products.get(i);

                                try {

                                    Log.e(TAG, "Image Name: "+object.getString("image"));
                                    String imageBase = object.getString("image");
//                                    String imageBase = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEMAAAAYCAMAAACWXbB1AAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAABXUExURUdwTOA5NNo1MstFJtQ2L8tLINRAK8o4N8xFJeQxNtgvM7srK8ouMP/+/uZFRepgWOQ6POhQTexxZumGg/ro6MFPTfbU0+idm85mZcx/eui6uXileV21gLBfi1wAAAAJdFJOUwDcvUmaEW34Kn/heG0AAAI8SURBVDjLhZWLlqMgEERVSBAERBBfmf//zq0GH+jkzBaJAqGu3R3Fqqqq1+vVti1jb4hz3mTVD+VZ/M5pHWMMHjgrEq9reZM7ZJ09NVDblQZZxH5VrbrM1E7AYT7UU3tqsM6BcY9B3ginP8vnVggU11bsFoX8TbiuXtg/6wEZbMEoa5EqcRbgoFxBfH5+AAmZwar3PRNZVDND+isTEBKkjz+fK47MUM57WKT13rkheHyD9aEnhp/n0GOcBFKYMTFE7+lM9bDvikullDAmjGO/mNGpgMNqzGgwZ/sJJ7NZGkF9SJ0JqDQG/WBojXXGzGAotYKxYB0mJruZMcbVi3XdzLSGfjRbHIkxoxNnQjhecUJ0xizrrMHQGgyFjtCzmcSIWOzicUthNAwLDjZSb6VwAhDEaIBIjE6DYciaGE4EM2liINE+MSycYz+knlg3pIUquszousTodEQZ8MuoUxzEQC4BqSNiclpaMFOO1s9+mUzAfyhvjK5DDUbYKJ/MiFTTcXZOgOEsmFTtybkt1RYEKZuqJkQXY9ftZ0H9GLUWdOjiEoVCi3FwTmkaUg/zS8yP2sF4SFM7BcQup+jCTt3ubTDUN0hBUfRRlyQRSkpd4Rr/gSROxsijyRuDU6Baf4VkiirjkA+AdA3DTsi40ImjT5S+hUEIfSRyQ6jmnTdDqH3zZi+cVrpU4X/kUDecnYBdrwTa0/6lwo9HFPb26S9JDChs6kKUAFLa2/n7L/cddbwv8MagAzvfA18A/wCODEqJtiSYGgAAAABJRU5ErkJggg==";

                                    String base64Data = imageBase.substring(imageBase.indexOf(",") + 1);
                                    Log.d(TAG, "image base 64 Data-2: "+base64Data);

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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.e(TAG, "" + response.code());
                    serverCall.handleError2(response.code(), TAG, "getSku", response.message());

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "" + t.getMessage());

                if (!NetworkChangeReceiver.IS_CONNECTED)
                    fetchSku = true;

            }
        });

        /*
        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(DEFAULT_TIMEOUT);
        client.addHeader("authorization", prefSFA.getString("token", ""));
        client.post(SbAppConstants.API_GET_PRODUCT_LIST, null, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        // called when response HTTP status is "200 OK"
                        Log.e(TAG, "Response Sku List===" + res);

                        try {

                            JSONObject JsonResponse = new JSONObject(res);
                            JSONArray products = JsonResponse.getJSONArray("sku");

                            String status = JsonResponse.getString("status");
                            if (status.equalsIgnoreCase("success")) {

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
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.e(TAG,""+statusCode);
                        serverCall.handleError2(statusCode,TAG, "getSku", res);
                    }
                }
        );

         */
    }
}
