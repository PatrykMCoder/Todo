package com.pmprogramms.todo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationRepository {
    private final API apiTodoNote =  Client.getInstance().create(API.class);

    private MutableLiveData<Integer> resultCreateUser;

    public LiveData<Integer> createUser(HashMap<String, Object> userData) {
        if (resultCreateUser == null)
            resultCreateUser = new MutableLiveData<>();

        apiTodoNote.createUser(userData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful())
                    resultCreateUser.postValue(201);
                else
                    resultCreateUser.postValue(500);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                resultCreateUser.postValue(500);
            }
        });

        return resultCreateUser;
    }
}
