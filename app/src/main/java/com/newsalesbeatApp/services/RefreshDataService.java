package com.newsalesbeatApp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.sqldatabase.SalesBeatDb;
import com.newsalesbeatApp.utilityclass.SBApplication;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by abc on 11/5/18.
 */

public class RefreshDataService extends IntentService {

    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_ERROR = 3;
    private final int DEFAULT_TIMEOUT = 60 * 1000;
    Bundle bundle;
    DownloadFile2 downloadFile = null;
    private String TAG = "RefreshDataService";
    private int count = 0;
    private SharedPreferences prefSFA;
    private SalesBeatDb salesBeatDb;
    private ResultReceiver receiver;

    public RefreshDataService() {
        super(RefreshDataService.class.getName());

        Context context = SBApplication.getInstance();
        prefSFA = context.getSharedPreferences(context.getString(R.string.pref_name), Context.MODE_PRIVATE);
        //salesBeatDb = new SalesBeatDb(context);
        salesBeatDb = SalesBeatDb.getHelper(context);
        bundle = new Bundle();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String api = intent.getStringExtra("api");
        receiver = intent.getParcelableExtra("receiver");

        if (api.equalsIgnoreCase("getCatalog")) {
            getCatalogFromServer();
        }
    }

    private void getCatalogFromServer() {

        Cursor cursor = null;
        JSONObject param = new JSONObject();
        JSONArray updatedAtArr = new JSONArray();

        try {
            cursor = salesBeatDb.gettDocs();

            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {

                do {

                    JSONObject obj = new JSONObject();

                    String id = cursor.getString(cursor.getColumnIndex("docs_id"));
                    String updatedAt = cursor.getString(cursor.getColumnIndex("last_updated"));

                    obj.put("id", id);
                    obj.put("updated_at", updatedAt);

                    updatedAtArr.put(obj);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
            try {
                param.put("updatedAt", updatedAtArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e(TAG, "Catalog Json-->" + param.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SbAppConstants.API_TO_GET_CATALOG,
                param, new Response.Listener<JSONObject>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "Catalog response: " + response.toString());
                Log.d(TAG, "onResponse: ");

                try {
                    Log.d(TAG, "onResponse: try response");
                    String basePath = response.getString("basepath");
                    //String baseThumb = response.getString("basepathThumb");
                    JSONArray catalogue = response.getJSONArray("catalogues");
                    String status = response.getString("status");
                    //String statusMessage = response.getString("statusMessage");

                    for (int i = 0; i < catalogue.length(); i++) {

                        JSONObject obj = (JSONObject) catalogue.get(i);

                        String id = "", catalogImage = "", desc = "", updatedAt = "", catStatus = "";
                        if (!obj.isNull("id") && obj.has("id")) {
                            id = obj.getString("id");
                        }

                        if (!obj.isNull("catalogueImg") && obj.has("catalogueImg")) {
                            catalogImage = basePath + obj.getString("catalogueImg");
                        }

                        if (!obj.isNull("desc") && obj.has("desc")) {
                            desc = obj.getString("desc");
                        }

                        if (!obj.isNull("updated_at") && obj.has("updated_at")) {
                            updatedAt = obj.getString("updated_at");
                        }

                        if (!obj.isNull("status") && obj.has("status")) {
                            catStatus = obj.getString("status");
                        }

                        Log.e(TAG, "onResponse: cat status " + catStatus + " i: " + i);

                        if (catStatus.equalsIgnoreCase("new")) {
                            //insert query
                            downloadFile = new DownloadFile2(id, catalogImage, desc, updatedAt, catStatus, status, catalogue.length());
                            downloadFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        } else if (catStatus.equalsIgnoreCase("old")) {
                            //update query
                            downloadFile = new DownloadFile2(id, catalogImage, desc, updatedAt, catStatus, status, catalogue.length());
                            downloadFile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                        }

                    }

                    if (!response.isNull("deleteData") && response.has("deleteData")) {

                        JSONArray deleteDataArr = response.getJSONArray("deleteData");
                        for (int i = 0; i < deleteDataArr.length(); i++) {

                            String id = (String) deleteDataArr.get(i);
                            //delete query
                            salesBeatDb.deletetSpecificDocs(id);
                        }
                    }

//                    if (status.equalsIgnoreCase("success")) {
//                        receiver.send(DOWNLOAD_SUCCESS, bundle);
//                    }

                } catch (Exception e) {
                    receiver.send(DOWNLOAD_ERROR, bundle);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                receiver.send(DOWNLOAD_ERROR, bundle);
                error.printStackTrace();
            }
        }) {

            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", prefSFA.getString("token", ""));
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private class DownloadFile2 extends AsyncTask<Void, Void, Boolean> {

        String id, catalogImage, desc, updatedAt, catStatus, requestStatus;
        int catalogueSize;

        public DownloadFile2(String id, String catalogImage, String desc, String updatedAt, String catStatus, String requestStatus, int catalogueSize) {
            this.id = id;
            this.catalogImage = catalogImage;
            this.desc = desc;
            this.updatedAt = updatedAt;
            this.catStatus = catStatus;
            this.requestStatus = requestStatus;
            this.catalogueSize = catalogueSize;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            String imageName = String.valueOf(Calendar.getInstance().getTimeInMillis());
            Log.e("Documents Download", "downloading");
            int count;
            try {

                URL url = new URL(catalogImage);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                String PATH = Environment.getExternalStorageDirectory() + "/SalesBeat/";
                File folder = new File(PATH);

                if (!folder.exists()) {
                    folder.mkdir();
                }

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(PATH + imageName);
                byte data[] = new byte[1024];

                while ((count = input.read(data)) != -1) {

                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                String filePath = folder.getPath() + "/" + imageName;

                if (catStatus.equalsIgnoreCase("new"))
                    salesBeatDb.insertDocsDetail(id, filePath, desc, updatedAt);
                else if (catStatus.equalsIgnoreCase("old")) {
                    Log.d(TAG, "doInBackground: update exisiting documents");
                    salesBeatDb.updateDocsDetail(id, filePath, desc, updatedAt);
                }


            } catch (Exception e) {
                Log.d(TAG, "doInBackground: error " + e.getLocalizedMessage());
                e.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(TAG, "onPostExecute: downloaded");
            count++;
            Log.d(TAG, "onPostExecute: count" + count);
            if (count == catalogueSize) {
                Log.d(TAG, "onPostExecute: count matched");
                if (requestStatus.equalsIgnoreCase("success")) {
                    receiver.send(DOWNLOAD_SUCCESS, bundle);
                }
            }
        }
    }
}

