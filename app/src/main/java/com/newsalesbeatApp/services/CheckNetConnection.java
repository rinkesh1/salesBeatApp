package com.newsalesbeatApp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.JsonObject;
import com.newsalesbeatApp.R;
import com.newsalesbeatApp.interfaces.ClientInterface;
import com.newsalesbeatApp.netwotkcall.RetrofitClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.loopj.android.http.RequestParams;
//import org.apache.http.Header;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;

/*
 * Created by abc on 11/16/18.
 */

public class CheckNetConnection extends IntentService {

    public static final int DOWNLOAD_SUCCESS = 2;
    public static final int DOWNLOAD_ERROR = 3;
    SharedPreferences prefSFA;
    private ResultReceiver receiver;
    private ClientInterface apiIntentface;

    public CheckNetConnection() {

        super(CheckNetConnection.class.getName());

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleIntent(Intent intent) {
        receiver = intent.getParcelableExtra("receiver");
        prefSFA = getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        getCmny();
        //new MyAsynTask2().execute();
    }

//    private class MyAsynTask2 extends AsyncTask<Void, Void, String> {
//
//
//        @Override
//        protected String doInBackground(Void... voids) {
//
//
//            JSONObject initDetails = new JSONObject();
//
//            try {
//
//                initDetails.put("auth", getString(R.string.apikey));
//                initDetails.put("cid", getString(R.string.cmny_id));
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            // set the connection timeout value to 2 minutes
//            final HttpParams httpParams = new BasicHttpParams();
//            HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
//            HttpClient httpClient = new DefaultHttpClient(httpParams);
//            HttpPost httpPost = new HttpPost(SbAppConstants.API_GET_CMNY_INFO);
//
//            try {
//
//                StringEntity se = new StringEntity(initDetails.toString());
//                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                httpPost.setEntity(se);
//
//                HttpResponse httpResponse = httpClient.execute(httpPost);
//                int statusCode = httpResponse.getStatusLine().getStatusCode();
//                if (statusCode != HttpStatus.SC_OK) {
//                    //throw new Exception("HTTP status code: " + statusCode + " != " + HttpStatus.SC_OK);
//                    receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
//                } else {
//                    receiver.send(DOWNLOAD_SUCCESS, Bundle.EMPTY);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return "";
//        }
//    }

    private void getCmny() {


        apiIntentface = RetrofitClient.getClient().create(ClientInterface.class);
        HashMap<String, Object> params = new HashMap<>();
//        RequestParams params = new RequestParams();

        params.put("auth", getString(R.string.apikey));
        params.put("cid", getString(R.string.cmny_id));


        Call<JsonObject> callCheckNetConnection = apiIntentface.checkNetConnection(params);
        callCheckNetConnection.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {
                    Log.e("CheckNetConnection", "---> Success");
                    receiver.send(DOWNLOAD_SUCCESS, Bundle.EMPTY);
                } else {
                    Log.e("CheckNetConnection", "---> Failure");
                    receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.e("CheckNetConnection", "---> Failure");
                Log.e("CheckNetConnection", t.getMessage());
                receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);

            }
        });

        /*
        RequestParams params = new RequestParams();

        params.put("auth", getString(R.string.apikey));
        params.put("cid", getString(R.string.cmny_id));

        SyncHttpClient client = new SyncHttpClient();
        client.post(SbAppConstants.API_GET_CMNY_INFO, params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String res) {
                        Log.e("CheckNetConnection", "---> Success");
                        receiver.send(DOWNLOAD_SUCCESS, Bundle.EMPTY);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                        Log.e("CheckNetConnection", "---> Failure");
                        receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
                    }
                }
        );

         */
    }
}
