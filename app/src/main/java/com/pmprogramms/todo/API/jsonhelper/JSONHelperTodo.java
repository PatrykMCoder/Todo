package com.pmprogramms.todo.API.jsonhelper;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class JSONHelperTodo {
    private String TAG = "jsonHelper";

    @SerializedName("title")
    public String title;

    @SerializedName("_id")
    public String id;

    @SerializedName("tag")
    public String tag;

    @SerializedName("archive")
    public boolean archive;

    @SerializedName("color")
    public String color;

    public JSONHelperTodo(String title, String id, String tag, boolean archive, String color){
        this.title = title;
        this.id = id;
        this.tag = tag;
        this.archive = archive;
        this.color = color;
    }
}
