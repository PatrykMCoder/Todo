package com.pmprogramms.todo.API.retrofit.note;

import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("_id")
    public String _id;

    @SerializedName("title")
    public String title;

    @SerializedName("archive")
    public boolean archive;

    @SerializedName("contents")
    public String contents;

    @SerializedName("updatedAt")
    public String updatedAt;
}
