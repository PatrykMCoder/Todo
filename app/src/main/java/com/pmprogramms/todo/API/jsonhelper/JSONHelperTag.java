package com.pmprogramms.todo.API.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperTag {
    @SerializedName("tag")
    public String tag;

    public JSONHelperTag(String tag) {
        this.tag = tag;
    }
}
