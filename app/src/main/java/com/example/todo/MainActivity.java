package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import android.Manifest;

import com.example.todo.database.TodoAdapter;

public class MainActivity extends AppCompatActivity {
    private TodoAdapter todoAdapter;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllPermission();
        initFragment(new HomePageFragment(getApplicationContext()), false);
    }

    private void getAllPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
        }
    }

    private void initFragment(Fragment fragment, boolean addToBackStage){
        Fragment f = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.containerFragment, f);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }
}
