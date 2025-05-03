package com.newsalesbeatApp.interfaces;

import com.google.gson.JsonObject;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiIntentface {

//    @Header("Content-Type: application/json")
//    @POST(SbAppConstants.FULL_DAY_ACTIVITY)
//

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.SUBMIT_ERROR)
    Call<JSONObject> submitError();

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.USER_LOG_IN)
    Call<JSONObject> refreshUserDetail(
            @Query("auth") String auth,
            @Query("cid") String cid,
            @Query("username") String username,
            @Query("password") String password,
            @Query("token") String token
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.SEND_PATH)
    Call<JsonObject> syncLocation(
            @Header("authorization") String auth,
            @Body List<HashMap> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.EMP_BEAT_VISIT)
    Call<JsonObject> markEmployeeVisitedBeat(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.ADD_NEW_RETAILER2)
    Call<JsonObject> addNewRetailerToServer(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );


    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.ADD_NEW_PREFERRED_RETAILER)
    Call<JsonObject> addNewPreferredRetailerToServer(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.ADD_NEW_PREFERRED_RETAILER_SHOP)
    Call<JsonObject> addNewPreferredRetailerShopToServer(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );


    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.SUBMIT_ORDER2)
    Call<JsonObject> submitOrder(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.ADD_NEW_DISTRIBUTOR)
    Call<JsonObject> newDistributorToServer(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.SUBMIT_DISTRIBUTOR_ORDER)
    Call<JsonObject> submitDistributorOrder(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.CANCEL_DISTRIBUTOR_ORDER)
    Call<JsonObject> cancelDistributorOrder(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.FULL_DAY_ACTIVITY)
    Call<JsonObject> submitActivity(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.SUBMIT_DISTRIBUTOR_CLOSING)
    Call<JsonObject> submitDistributorClosing(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.SUBMIT_DISTRIBUTOR_STOCK_INFO)
    Call<JsonObject> submitDistributorStockInfo(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.CANCEL_ORDER)
    Call<JsonObject> cancelOrder(
            @Header("authorization") String auth,
            @Body HashMap<String, Object> map
    );

}
