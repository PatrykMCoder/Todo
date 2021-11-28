package com.pmprogramms.todo.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.api.retrofit.API;
import com.pmprogramms.todo.api.retrofit.Client;
import com.pmprogramms.todo.api.retrofit.login.JsonHelperLogin;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private final API apiTodoNote = Client.getInstance().create(API.class);

    private MutableLiveData<JsonHelperLogin> loginResultLiveData;

    public LiveData<JsonHelperLogin> loginUser(HashMap<String, String > authData) {
        if (loginResultLiveData == null)
            loginResultLiveData = new MutableLiveData<>();

        apiTodoNote.loginUser(authData).enqueue(new Callback<JsonHelperLogin>() {
            @Override
            public void onResponse(@NonNull Call<JsonHelperLogin> call, @NonNull Response<JsonHelperLogin> response) {
                if (response.isSuccessful()) {
                    loginResultLiveData.postValue(response.body());
                } else
                    loginResultLiveData.postValue(null);
            }

            @Override
            public void onFailure(@NonNull Call<JsonHelperLogin> call, @NonNull Throwable t) {
                loginResultLiveData.postValue(null);
            }
        });

        return loginResultLiveData;

    }
}
