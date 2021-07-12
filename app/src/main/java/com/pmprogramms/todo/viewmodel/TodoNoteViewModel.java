package com.pmprogramms.todo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.API.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.repository.TodoNoteRepository;

public class TodoNoteViewModel extends AndroidViewModel {
    private TodoNoteRepository todoNoteRepository = new TodoNoteRepository();

    public TodoNoteViewModel(@NonNull Application application) {
        super(application);
    }

    public boolean isValidLoginForm(String email, String password) {
        //todo make validation
        return false;
    }

    public boolean isValidRegisterForm(String username, String email, String password, String accepted) {
        //todo make validation
        return false;
    }

    public LiveData<JSONHelperTodo> getAllTodos(String token) {
        return todoNoteRepository.getAllTodos(token);
    }
}
