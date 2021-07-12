package com.pmprogramms.todo.repository;

import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.API.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.todo.todo.Todos;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoNoteRepository {
    private API apiTodoNote = Client.getInstance().create(API.class);

    private MutableLiveData<JSONHelperTodo> todosMutableLiveData;

    public MutableLiveData<JSONHelperTodo> getAllTodos(String token) {
        todosMutableLiveData = new MutableLiveData<>();
        apiTodoNote.getUserTodosTitle(token).enqueue(new Callback<JSONHelperTodo>() {
            @Override
            public void onResponse(Call<JSONHelperTodo> call, Response<JSONHelperTodo> response) {
                if (response.isSuccessful()) {
                    todosMutableLiveData.postValue(response.body());
                } else
                    todosMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<JSONHelperTodo> call, Throwable t) {
                todosMutableLiveData.postValue(null);
            }
        });
        return todosMutableLiveData;
    }

}
