package com.pmprogramms.todo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.customTags.JsonHelperTag;
import com.pmprogramms.todo.API.retrofit.todo.todo.Data;
import com.pmprogramms.todo.API.retrofit.todo.todo.JSONHelperTodo;
import com.pmprogramms.todo.API.retrofit.user.JsonHelperUser;
import com.pmprogramms.todo.API.retrofit.user.UserData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TodoNoteRepository {
    private final API apiTodoNote = Client.getInstance().create(API.class);

    private MutableLiveData<JSONHelperTodo> todosMutableLiveData;
    private MutableLiveData<JSONHelperTodo> selectedTodoMutableLiveData;
    private MutableLiveData<Integer> returnCodeAfterUpdateTodo;
    private MutableLiveData<Integer> returnCodeAfterArchiveTodo;
    private MutableLiveData<Integer> returnCodeCreateTodo;
    private MutableLiveData<Integer> returnCodeEditTodo;
    private MutableLiveData<JsonHelperTag> tagsMutableLiveData;
    private MutableLiveData<Integer> returnCodeDeleteTodo;
    private MutableLiveData<Integer> returnCodeCreateTag;
    private MutableLiveData<JsonHelperUser> userDataMutableLiveData;

    public LiveData<JSONHelperTodo> getAllTodos(boolean archive, String token) {
        if (todosMutableLiveData == null)
            todosMutableLiveData = new MutableLiveData<>();

        apiTodoNote.getUserTodosTitle(archive, token).enqueue(new Callback<JSONHelperTodo>() {
            @Override
            public void onResponse(Call<JSONHelperTodo> call, Response<JSONHelperTodo> response) {
                if (response.isSuccessful() && (response.code() == 200 || response.code() == 201)) {
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

    public LiveData<JSONHelperTodo> getSelectedTodo(String todoID, String userToken) {
        if (selectedTodoMutableLiveData == null)
            selectedTodoMutableLiveData = new MutableLiveData<>();
        apiTodoNote.getUserTodoData(todoID, userToken).enqueue(new Callback<JSONHelperTodo>() {
            @Override
            public void onResponse(Call<JSONHelperTodo> call, Response<JSONHelperTodo> response) {
                if (response.isSuccessful() && (response.code() == 200 || response.code() == 201))
                    selectedTodoMutableLiveData.postValue(response.body());
                else
                    selectedTodoMutableLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<JSONHelperTodo> call, Throwable t) {
                selectedTodoMutableLiveData.postValue(null);
            }
        });
        return selectedTodoMutableLiveData;
    }

    public LiveData<Integer> updateSelectedTodo(String todoID, HashMap<String, String> todoMap, String userToken) {
        if (returnCodeAfterUpdateTodo == null)
            returnCodeAfterUpdateTodo = new MutableLiveData<>();

        Call<Data> call = apiTodoNote.updateTodoStatus(todoID, todoMap, userToken);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                returnCodeAfterUpdateTodo.postValue(response.code());
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                returnCodeAfterUpdateTodo.postValue(500);
            }
        });
        return returnCodeAfterUpdateTodo;
    }

    public LiveData<Integer> archiveTodo(String todoID, HashMap<String, Boolean> archiveMap, String userToken) {
        if (returnCodeAfterArchiveTodo == null)
            returnCodeAfterArchiveTodo = new MutableLiveData<>();

        Call<Void> call = apiTodoNote.archiveAction(todoID, archiveMap, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                returnCodeAfterArchiveTodo.postValue(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                returnCodeAfterArchiveTodo.postValue(500);
            }
        });
        return returnCodeAfterArchiveTodo;
    }

    public LiveData<Integer> createTodo(HashMap<String, String> data, String userToken) {
        if (returnCodeCreateTodo == null)
            returnCodeCreateTodo = new MutableLiveData<>();

        Call<Void> call = apiTodoNote.saveTodo(data, userToken);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                returnCodeCreateTodo.postValue(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                returnCodeCreateTodo.postValue(500);
            }
        });
        return returnCodeCreateTodo;
    }

    public LiveData<Integer> editTodoNote(String todoID, HashMap<String, String> data, String userToken) {
        if (returnCodeEditTodo == null)
            returnCodeEditTodo = new MutableLiveData<>();

        apiTodoNote.editTodo(todoID, data, userToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                returnCodeEditTodo.postValue(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                returnCodeEditTodo.postValue(500);
            }
        });

        return returnCodeEditTodo;
    }

    public LiveData<JsonHelperTag> getAllTags(String userToken) {
        if (tagsMutableLiveData == null)
            tagsMutableLiveData = new MutableLiveData<>();

        apiTodoNote.getUserTagData(userToken).enqueue(new Callback<JsonHelperTag>() {
            @Override
            public void onResponse(Call<JsonHelperTag> call, Response<JsonHelperTag> response) {
                if (response.isSuccessful() && (response.code() == 200 || response.code() == 201)) {
                    tagsMutableLiveData.postValue(response.body());
                } else {
                    tagsMutableLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<JsonHelperTag> call, Throwable t) {
                tagsMutableLiveData.postValue(null);
            }
        });

        return tagsMutableLiveData;
    }

    public LiveData<Integer> deleteTodo(String todoID, String userToken) {
        if (returnCodeDeleteTodo == null) {
            returnCodeDeleteTodo = new MutableLiveData<>();
        }

        apiTodoNote.deleteTodo(todoID, userToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                returnCodeDeleteTodo.postValue(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                returnCodeDeleteTodo.postValue(500);
            }
        });

        return returnCodeDeleteTodo;
    }

    public void createCustomTag(HashMap<String, String> data, String userToken) {
        if (returnCodeCreateTag == null)
            returnCodeCreateTag = new MutableLiveData<>();

        apiTodoNote.createCustomTag(data, userToken).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                returnCodeCreateTag.postValue(response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                returnCodeCreateTag.postValue(500);
            }
        });
    }

    public LiveData<JsonHelperUser> getUserData(String userToken) {
        if (userDataMutableLiveData == null) {
            userDataMutableLiveData = new MutableLiveData<>();
        }

        apiTodoNote.getUserData(userToken).enqueue(new Callback<JsonHelperUser>() {
            @Override
            public void onResponse(Call<JsonHelperUser> call, Response<JsonHelperUser> response) {
                if (response.isSuccessful() && (response.code() == 200 || response.code() == 201)) {
                    userDataMutableLiveData.postValue(response.body());
                } else {
                    userDataMutableLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<JsonHelperUser> call, Throwable t) {
                userDataMutableLiveData.postValue(null);
            }
        });

        return userDataMutableLiveData;
    }
}
