package com.pmprogramms.todo.helpers.view;

import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.todo.todo.Todos;

import java.io.Serializable;
import java.util.ArrayList;

public class EditTodoHelper implements Serializable {
    private Data dataTodo;

    public EditTodoHelper(Data dataTodo) {
        this.dataTodo = dataTodo;
    }

    public Data getDataTodo() {
        return dataTodo;
    }
}
