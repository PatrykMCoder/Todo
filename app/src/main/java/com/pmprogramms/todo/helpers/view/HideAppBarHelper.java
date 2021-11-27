package com.pmprogramms.todo.helpers.view;

import android.view.View;

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;

public class HideAppBarHelper {
    private final View appBar;

    public HideAppBarHelper(MainActivity mainActivity) {
        appBar = mainActivity.findViewById(R.id.custom_app_bar);
    }

    public void hideBar() {
        appBar.setVisibility(View.GONE);
    }

    public void showBar() {
        appBar.setVisibility(View.VISIBLE);
    }
}
