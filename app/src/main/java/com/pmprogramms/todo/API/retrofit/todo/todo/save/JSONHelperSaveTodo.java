package com.pmprogramms.todo.API.retrofit.todo.todo.save;

import com.google.gson.annotations.SerializedName;

public class JSONHelperSaveTodo {
    @SerializedName("task")
    public String task;

    @SerializedName("done")
    public boolean done;

    public JSONHelperSaveTodo(String task, boolean done) {
        this.task = task;
        this.done = done;
    }
}
