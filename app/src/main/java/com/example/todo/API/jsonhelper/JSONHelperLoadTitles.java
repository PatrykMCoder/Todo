package com.example.todo.API.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperLoadTitles {
    private String TAG = "jsonHelper";

    @SerializedName("title")
    public String title;

    @SerializedName("_id")
    public String id;

    @SerializedName("tag")
    public String tag;

    public JSONHelperLoadTitles(String title, String id, String tag){
        this.title = title;
        this.id = id;
        this.tag = tag;
    }
}
