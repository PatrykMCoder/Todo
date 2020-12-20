package com.pmprogramms.todo.API.jsonhelper.note;

import com.google.gson.annotations.SerializedName;

public class JSONHelperNote {
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

    public JSONHelperNote(String _id, String title, boolean archive, String contents, String updatedAt) {
        this._id = _id;
        this.title = title;
        this.archive = archive;
        this.contents = contents;
        this.updatedAt = updatedAt;
    }
}
