package com.example.todo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todo.utils.notifications.Notification;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Notification notification = new Notification(getApplicationContext());
        notification.createNotificationsChanel();
        lunchActivity();
    }

    private String getUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        return sharedPreferences.getString("user_id", null);
    }

    private void lunchActivity() {
        Intent intent;

        if (getUserID() != null)
            intent = new Intent(this, MainActivity.class);
        else
            intent = new Intent(this, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
