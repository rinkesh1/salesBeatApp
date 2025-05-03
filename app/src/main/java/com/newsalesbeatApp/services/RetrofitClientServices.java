package com.newsalesbeatApp.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.newsalesbeatApp.utilityclass.SbAppConstants.API_GET_CMNY_INFO;

public class RetrofitClientServices {

    private static Retrofit retrofit = null;
    private static String BASE_URL = API_GET_CMNY_INFO;

    public static Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

    }
}
