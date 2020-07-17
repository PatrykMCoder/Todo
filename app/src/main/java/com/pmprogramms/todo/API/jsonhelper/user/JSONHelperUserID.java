package com.pmprogramms.todo.API.jsonhelper.user;

import com.google.gson.annotations.SerializedName;

public class JSONHelperUserID {
    @SerializedName("user_id")
    public String user_id;

    public JSONHelperUserID(String user_id) {
        this.user_id = user_id;
    }
}
