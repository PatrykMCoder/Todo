package com.pmprogramms.todo.utils.screen;

import android.graphics.Color;
import android.view.View;
import android.view.Window;

public class NotificationBar {
    private final Window window;

    public NotificationBar(Window window) {
        this.window = window;
    }

    public void updateColorNotificationBar() {
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.WHITE);
    }
}
