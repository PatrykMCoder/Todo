package com.example.todo.helpers.user;

import android.content.Context;
import android.content.SharedPreferences;

public class UserData {
    private SharedPreferences sharedPreferences;
    public UserData(Context context) {
        sharedPreferences = context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }

    public void setUserID(String userID) {
        sharedPreferences.edit().putString("user_id", userID).apply();
    }

    public String getUserID() {
        return sharedPreferences.getString("user_id", null);
    }

    public void removeUserID() {
        sharedPreferences.edit().putString("user_id", null).apply();
    }
}
