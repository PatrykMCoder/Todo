package com.pmprogramms.todo.helpers.view;

import android.view.View;

import com.pmprogramms.todo.MainActivity;
import com.pmprogramms.todo.R;

public class HideAppBarHelper {
    private MainActivity mainActivity;
    private View appBar;

    public HideAppBarHelper(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        appBar = mainActivity.findViewById(R.id.custom_app_bar);

    }

    public void hideBar() {
        appBar.setVisibility(View.GONE);
    }

    public void showBar() {
        appBar.setVisibility(View.VISIBLE);
    }
}
