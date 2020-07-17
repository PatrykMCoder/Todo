package com.pmprogramms.todo.utils.text;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Messages {
    private Context context;

    public Messages(Context context) {
        this.context = context;
    }

    public void showMessage(String msg) {
        Handler handler = new Handler();
        handler.post(() -> {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
