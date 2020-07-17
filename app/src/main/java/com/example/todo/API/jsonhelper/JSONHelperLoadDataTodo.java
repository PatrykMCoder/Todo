package com.example.todo.API.jsonhelper;

import com.google.gson.annotations.SerializedName;

public class JSONHelperLoadDataTodo {
    @SerializedName("_id")
    public String _id;
    @SerializedName("task")
    public String task;
    @SerializedName("done")
    public boolean done;

    public JSONHelperLoadDataTodo(String _id, String task, boolean done) {
        this._id =  _id;
        this.task = task;
        this.done = done;
    }
}
