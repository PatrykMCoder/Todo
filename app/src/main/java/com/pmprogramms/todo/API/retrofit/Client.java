package com.pmprogramms.todo.API.retrofit;

import com.pmprogramms.todo.utils.device.CheckTypeApplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        String url = "";

        if (CheckTypeApplication.isDebugApp()) url = "http://10.0.2.2:4000";
        else url = "https://todo-note-api.herokuapp.com";

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
