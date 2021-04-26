package com.pmprogramms.todo.API.retrofit.todo.todo.edit;

import com.google.gson.annotations.SerializedName;

public class JSONHelperEditTodo {
    @SerializedName("task")
    public String task;

    @SerializedName("done")
    public boolean done;

    public JSONHelperEditTodo(String task, boolean done) {
        this.task = task;
        this.done = done;
    }
}
