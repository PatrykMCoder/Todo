package com.pmprogramms.todo.API.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperLoadCustomTags {
    @SerializedName("_id")
    public String _id;
    @SerializedName("tag_name")
    public String tag_name;

    public JSONHelperLoadCustomTags(String _id, String tag_name) {
        this._id = _id;
        this.tag_name = tag_name;
    }
}
