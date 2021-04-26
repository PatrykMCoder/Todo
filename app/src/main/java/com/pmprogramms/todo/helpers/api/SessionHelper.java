package com.pmprogramms.todo.helpers.api;

import com.google.gson.Gson;
import com.pmprogramms.todo.API.retrofit.error.ErrorBody;

public class SessionHelper {
    public boolean checkSession(String responseBody) {
        Gson gson = new Gson();
        ErrorBody errorBody = gson.fromJson(responseBody, ErrorBody.class);
        return errorBody.errorData.auth;
    }
}
