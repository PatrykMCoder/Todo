package com.pmprogramms.todo.helpers.user;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.pmprogramms.todo.utils.text.Messages;

public class UserData {
    private SharedPreferences sharedPreferences;
    public UserData(Context context) {
        sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }

    public void setUserToken(String userToken) {
        sharedPreferences.edit().putString("token", userToken).apply();
    }

    public String getUserToken() {
        return sharedPreferences.getString("token", null);
    }

    public void removeUserToken() {
        sharedPreferences.edit().putString("token", null).apply();
    }

    public void removeOldData() {
        if (sharedPreferences.getString("user_id", null) != null) {
            sharedPreferences.edit().putString("user_id", null).apply();
        }
    }
}
