package com.pmprogramms.todo.helpers.input;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class HideKeyboard {
    private View view;
    private Activity activity;
    public HideKeyboard(View v, Activity activity) {
        view = v;
        this.activity = activity;
    }

    public void hide() {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
