package com.pmprogramms.todo.API.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperLastEdit {
    @SerializedName("updatedAt")
    public String updatedAt;

    public JSONHelperLastEdit(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
