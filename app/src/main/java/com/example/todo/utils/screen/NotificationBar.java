package com.example.todo.utils.screen;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;

import androidx.core.content.ContextCompat;

import com.example.todo.R;
import com.example.todo.utils.notifications.Notification;

public class NotificationBar {
    private Window window;

    public NotificationBar(Window window){
        this.window = window;
    }

    public void updateColorNotificationBar(){
        if (Build.VERSION.SDK_INT >= 23) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(Color.WHITE);
        }
    }
}
