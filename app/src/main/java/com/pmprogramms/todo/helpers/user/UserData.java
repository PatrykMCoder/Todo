package com.pmprogramms.todo.helpers.user;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {
    private final SharedPreferences sharedPreferences;
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
