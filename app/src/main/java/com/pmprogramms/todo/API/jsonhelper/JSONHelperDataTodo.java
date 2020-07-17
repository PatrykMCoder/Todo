package com.pmprogramms.todo.API.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperDataTodo {
    @SerializedName("_id")
    public String _id;
    @SerializedName("task")
    public String task;
    @SerializedName("done")
    public boolean done;

    public JSONHelperDataTodo(String _id, String task, boolean done) {
        this._id =  _id;
        this.task = task;
        this.done = done;
    }
}
