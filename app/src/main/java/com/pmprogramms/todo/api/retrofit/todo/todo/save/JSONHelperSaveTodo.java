package com.pmprogramms.todo.api.retrofit.todo.todo.save;

import com.google.gson.annotations.SerializedName;

public class JSONHelperSaveTodo {
    @SerializedName("task")
    public final String task;

    @SerializedName("done")
    public final boolean done;

    public JSONHelperSaveTodo(String task, boolean done) {
        this.task = task;
        this.done = done;
    }
}
