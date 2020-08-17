package com.example.todo.service.jsonhelper;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;

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
