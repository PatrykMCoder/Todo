package com.pmprogramms.todo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pmprogramms.todo.api.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.api.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.api.retrofit.user.JsonHelperUser;
import com.pmprogramms.todo.repository.TodoNoteRepository;

import java.util.HashMap;

public class TodoNoteViewModel extends AndroidViewModel {
    private final TodoNoteRepository todoNoteRepository = new TodoNoteRepository();

    public TodoNoteViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<JSONHelperTodo> getAllTodos(boolean archive, String token) {
        return todoNoteRepository.getAllTodos(archive, token);
    }

    public LiveData<JSONHelperTodo> getSelectedTodo(String todoID, String userToken) {
        return todoNoteRepository.getSelectedTodo(todoID, userToken);
    }

    public LiveData<Integer> updateSelectedTodo(String todoID, HashMap<String, String> todoMap, String userToken) {
        return todoNoteRepository.updateSelectedTodo(todoID, todoMap, userToken);
    }

    public LiveData<Integer> archiveTodo(String todoID, HashMap<String, Boolean> archiveMap, String userToken) {
        return todoNoteRepository.archiveTodo(todoID, archiveMap, userToken);
    }

    public LiveData<Integer> createTodo(HashMap<String, String> data, String userToken) {
        return todoNoteRepository.createTodo(data, userToken);
    }

    public LiveData<Integer> editTodo(String todoID, HashMap<String, String> data, String userToken) {
        return todoNoteRepository.editTodoNote(todoID, data, userToken);
    }

    public LiveData<JsonHelperTag> getAllTags(String userToken) {
        return todoNoteRepository.getAllTags(userToken);
    }

    public LiveData<Integer> deleteTodo(String todoID, String userToken) {
        return todoNoteRepository.deleteTodo(todoID, userToken);
    }

    public void createCustomTag(HashMap<String, String> data, String userToken) {
        todoNoteRepository.createCustomTag(data, userToken);
    }

    public LiveData<JsonHelperUser> getUserData(String userToken) {
        return todoNoteRepository.getUserData(userToken);
    }
}
