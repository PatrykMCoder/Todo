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

    @SerializedName("archive")
    public boolean archive;

    public JSONHelperLoadTitles(String title, String id, String tag, boolean archive){
        this.title = title;
        this.id = id;
        this.tag = tag;
        this.archive = archive;
    }
}
