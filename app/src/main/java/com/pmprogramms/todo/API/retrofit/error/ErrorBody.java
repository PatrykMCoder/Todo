package com.pmprogramms.todo.API.retrofit.error;

import com.google.gson.annotations.SerializedName;

public class ErrorBody {

    @SerializedName("message")
    public ErrorData errorData;

    public class  ErrorData {
        public boolean auth;
        public String message;
    }
}
