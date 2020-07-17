package com.example.todo.API.jsonhelper;

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
