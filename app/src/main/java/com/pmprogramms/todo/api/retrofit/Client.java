package com.pmprogramms.todo.api.retrofit;

import com.pmprogramms.todo.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        String url;
        if (BuildConfig.DEBUG) url = BuildConfig.LOCAL_API;
        else url = BuildConfig.PRODUCTION_API;

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
