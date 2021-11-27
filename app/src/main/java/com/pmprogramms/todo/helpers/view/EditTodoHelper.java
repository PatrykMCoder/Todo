package com.pmprogramms.todo.helpers.view;

import com.pmprogramms.todo.api.retrofit.todo.todo.Data;
import java.io.Serializable;

public class EditTodoHelper implements Serializable {
    private final Data dataTodo;

    public EditTodoHelper(Data dataTodo) {
        this.dataTodo = dataTodo;
    }

    public Data getDataTodo() {
        return dataTodo;
    }
}
