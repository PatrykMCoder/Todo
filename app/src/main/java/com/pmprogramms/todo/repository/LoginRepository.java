package com.pmprogramms.todo.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.pmprogramms.todo.API.retrofit.API;
import com.pmprogramms.todo.API.retrofit.Client;
import com.pmprogramms.todo.API.retrofit.login.JsonHelperLogin;
import com.pmprogramms.todo.API.retrofit.user.JsonHelperUser;

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
            public void onResponse(Call<JsonHelperLogin> call, Response<JsonHelperLogin> response) {
                if (response.isSuccessful()) {
                    loginResultLiveData.postValue(response.body());
                } else
                    loginResultLiveData.postValue(null);
            }

            @Override
            public void onFailure(Call<JsonHelperLogin> call, Throwable t) {
                loginResultLiveData.postValue(null);
            }
        });

        return loginResultLiveData;

    }
}
