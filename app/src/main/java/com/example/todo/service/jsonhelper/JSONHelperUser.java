package com.example.todo.service.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperUser {
    @SerializedName("email")
    public String email;

    @SerializedName("username")
    public String username;

    public JSONHelperUser(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
