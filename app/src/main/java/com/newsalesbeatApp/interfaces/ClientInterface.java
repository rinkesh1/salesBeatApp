package com.newsalesbeatApp.interfaces;

import com.google.gson.JsonObject;
import com.newsalesbeatApp.utilityclass.SbAppConstants;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ClientInterface {

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.GET_CMNY_INFO)
    Call<JsonObject> getCompanyInfo(
            @Body HashMap jsonObject
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.GET_CMNY_INFO)
    Call<JsonObject> checkNetConnection(
            @Body HashMap requestParams
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.GET_BEAT_LIST)
    Call<JsonObject> getAllDataList(
            @Header("authorization") String auth,
            @Body HashMap requestParams
    );

    @Headers("Content-Type: application/json")
    @POST(SbAppConstants.GET_PRODUCT_LIST)
    Call<JsonObject> getSkuList(
            @Header("authorization") String auth
    );

//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_PRIMARY_SALE_HISTORY + "/{date_given}/")
//    Call<JsonObject> getPrimarySaleHistory(
//            @Header("authorization") String auth,
//            @Path(value = "date_given") String date_given);
//
//    @Headers("Content-Type: application/json")
//    @POST(SbAppConstants.GET_TOWN_LIST)
//    Call<JsonObject> getTownListFromServer(
//            @Header("authorization") String auth,
//            @Body HashMap requestParams
//    );

    @Headers("Content-Type: application/json")
    @GET(SbAppConstants.GET_PRODUCT_LIST)
    Call<JsonObject> getProductList(
            @Header("authorization") String auth
    );

    @Headers("Content-Type: application/json")
    @GET(SbAppConstants.GET_NEWDISTRIBUTORFORMJSON)
    Call<JsonObject> getDisJsonForm(
            @Header("authorization") String auth
    );

//    @Headers("Content-Type: application/json")
//    @POST(SbAppConstants.GET_EMP_RECORD_BY_MONTH)
//    Call<JsonObject> getEmployeeRecordByMonthAndYear(
//            @Header("authorization") String auth,
//            @Body HashMap requestParams
//    );
//
//    @Headers("Content-Type: application/json")
//    @POST(SbAppConstants.GET_EMP_LEADER_BOARD)
//    Call<JsonObject> getLeaderboardData(
//            @Header("authorization") String auth,
//            @Body HashMap requestParams
//    );

//    @Headers("Content-Type: application/json")
//    @POST(SbAppConstants.GET_EMP_KRA_BY_DATE)
//    Call<JsonObject> getEmpKraByDate(
//            @Header("authorization") String auth,
//            @Body HashMap requestParams
//    );
//
//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_EMP_RECORD_BY_DATE + "{date_given}")
//    Call<JsonObject> getEmployeeRecordByDate(
//            @Header("authorization") String auth,
//            @Path(value = "date_given") String date_given
//            //@Query("date") String date
//            //@Body HashMap requestParams
//    );

//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_DISTRIBUTOR_TAR_ACH + "/{date_given}/")
//    Call<JsonObject> getDistributorTargetAchievement(
//            @Header("authorization") String auth,
//            @Query("date_given") String date_given
//    );
//
//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_PRIMARY_SALE_BY_DATE + "/{date_given}/")
//    Call<JsonObject> getEmpPrimarySaleByDate(
//            @Header("authorization") String auth,
//            @Query("date_given") String date_given
//    );
//
//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_SECONDARY_SALE_BY_DATE + "/{date}/{date}/")
//    Call<JsonObject> getEmpSecondarySaleByDate(
//            @Header("authorization") String auth,
//            @Query("date") String date
//    );

//    @Headers("Content-Type: application/json")
//    @POST(SbAppConstants.GET_PROMOTION)
//    Call<JsonObject> getPromotion(
//            @Header("authorization") String auth
//    );
//
//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_INCENTIVE + "/{date}/")
//    Call<JsonObject> getIncenties(
//            @Header("authorization") String auth,
//            @Query("date") String date
//    );

//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.DISTRIBUTORS_2)
//    Call<HttpEntity> getDistributors2(
//            @Header("authorization") String auth,
//            @Query("town") String town
//    );
//
//    @Headers("Content-Type: application/json")
//    @GET(SbAppConstants.GET_RETAILERS_FEEDBACK)
//    Call<HttpEntity> getRetailersFeedback(
//            @Header("authorization") String auth
//    );

}
