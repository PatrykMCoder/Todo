package com.example.todo.utils.objects;

public class TodoObject {
    private String title;
    private String description;
    private String dateCreate;
    private String dateReaming;
    private int done;

    public TodoObject(String title, String description, int done, String dateCreate, String dateReaming) {
        this.title = title;
        this.description = description;
        this.done = done;
        this.dateCreate = dateCreate;
        this.dateReaming = dateReaming;
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
