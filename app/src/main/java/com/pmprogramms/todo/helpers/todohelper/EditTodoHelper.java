package com.pmprogramms.todo.helpers.todohelper;

public class EditTodoHelper {
    private String task;
    private int done;
    private String tag;
    private String lastEdited;

    public EditTodoHelper(String task, int done, String tag, String lastEdited) {
        this.task = task;
        this.done = done;
        this.tag = tag;
        this.lastEdited = lastEdited;
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

    public String getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(String lastEdited) {
        this.lastEdited = lastEdited;
    }
}
