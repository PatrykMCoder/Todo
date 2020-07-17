package com.pmprogramms.todo.helpers.tools;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.pmprogramms.todo.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Permissions {
    private final Context context;
    private final MainActivity mainActivity;

    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public Permissions(Context context) {
        this.context = context;
        this.mainActivity = (MainActivity) context;
    }

    public void getAllPermissions() {
        List<String> needPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                needPermissions.add(permission);
            }
        }
        for (String permission : needPermissions) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{permission}, 1234);
        }
    }
}
