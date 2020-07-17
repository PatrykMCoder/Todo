package com.example.todo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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

    private boolean connected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private String getUserID() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        return sharedPreferences.getString("user_id", null);
    }

    private void lunchActivity() {
        Intent intent;
        if (connected()) {
            if (getUserID() != null)
                intent = new Intent(this, MainActivity.class);
            else
                intent = new Intent(this, LoginActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            showAlertDialog();
        }

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Needed internet connection")
                .setMessage("Please, check your internet connection")
                .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lunchActivity();
                    }
                })
                .setIcon(R.drawable.ic_baseline_warning_24);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
