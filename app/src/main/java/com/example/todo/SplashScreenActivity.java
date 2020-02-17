package com.example.todo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.todo.utils.setteings.Settings;

public class SplashScreenActivity extends AppCompatActivity {
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        settings = new Settings(getApplicationContext());

        removeBar();
        getAllPermission();

        lunchMainActivity();
    }

    private void removeBar(){
        if(Build.VERSION.SDK_INT >= 10)
            getSupportActionBar().hide();
        else
            getActionBar().hide();
    }

    private void lunchMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        }
    }
}
