package com.pmprogramms.todo.helpers.api;

import android.util.Log;

import com.google.gson.Gson;
import com.pmprogramms.todo.API.retrofit.error.ErrorBody;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class SessionHelper {
    public boolean checkSession(ResponseBody responseBody) {
//        todo -> fix that method
//        Gson gson = new Gson();
//        ErrorBody errorBody = gson.fromJson(responseBody.string(), ErrorBody.class);
//        return errorBody.errorData.auth;
        return true;
    }
}
