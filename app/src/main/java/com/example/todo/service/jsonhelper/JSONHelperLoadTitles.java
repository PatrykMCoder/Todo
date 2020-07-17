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

    public JSONHelperLoadTitles(String title, String id){
        this.title = title;
        this.id = id;
    }
}
