package com.pmprogramms.todo.api.retrofit.todo.todo;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
    public String color;
    public boolean archive;
    public String _id;
    public String title;
    public String user_id;
    public String tag;
    public String updatedAt;
    public ArrayList<Todos> todos;
}
