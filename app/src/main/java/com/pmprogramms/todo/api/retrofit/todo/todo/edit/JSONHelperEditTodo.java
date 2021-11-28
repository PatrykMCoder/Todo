package com.pmprogramms.todo.api.retrofit.todo.todo.edit;

import com.google.gson.annotations.SerializedName;

public class JSONHelperEditTodo {
    @SerializedName("task")
    public final String task;

    @SerializedName("done")
    public final boolean done;

    public JSONHelperEditTodo(String task, boolean done) {
        this.task = task;
        this.done = done;
    }
}
