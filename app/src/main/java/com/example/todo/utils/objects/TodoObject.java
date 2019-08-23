package com.example.todo.utils.objects;

public class TodoObject {
    private String title;
    private String description;
    private String dateCreate;
    private String dateReaming;
    private int done;

    public TodoObject(String title, String description, String dateCreate, String dateReaming, int done) {
        this.title = title;
        this.description = description;
        this.dateCreate = dateCreate;
        this.dateReaming = dateReaming;
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateReaming() {
        return dateReaming;
    }

    public void setDateReaming(String dateReaming) {
        this.dateReaming = dateReaming;
    }

    public int getDone() {
        return done;
    }

    public void setDone(int done) {
        this.done = done;
    }
}
