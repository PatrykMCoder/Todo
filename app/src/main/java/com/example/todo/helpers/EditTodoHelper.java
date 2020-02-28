package com.example.todo.helpers;

public class EditTodoHelper {
    private String task;
    private int done;
    private String tag;

    public EditTodoHelper(String task, int done, String tag) {
        this.task = task;
        this.done = done;
        this.tag = tag;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
