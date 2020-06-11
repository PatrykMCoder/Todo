package com.example.todo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todo.view.fragments.TodoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllPermission();
        initView();
        initFragment(new TodoFragment(), false);
    }

    @SuppressLint("ResourceType")
    private void initView() {
        bottomNavigationView = findViewById(R.id.nav_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public void initFragment(final Fragment fragment, boolean addToBackStage) {
        this.fragment = fragment;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.containerFragment, fragment);

        if (addToBackStage)
            fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();
    }

    public void closeFragment(Fragment oldFragment, Fragment newFragment) {
        assert fragment.getFragmentManager() != null;
        fragment.getFragmentManager().beginTransaction().remove(oldFragment).replace(R.id.containerFragment, newFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.todo_item:
                initFragment(new TodoFragment(), true);
                return true;
            case R.id.note_item:
//                initFragment(new NoteFragment(), true);
                Toast.makeText(getApplicationContext(), "Available in future :)", Toast.LENGTH_LONG).show();
                return false;
            case R.id.settings_item:
                Toast.makeText(getApplicationContext(), "Available in future :)", Toast.LENGTH_LONG).show();
                return false;
            default:
                break;

        }
        return false;
    }

    private void getAllPermission() {
        int i = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> needPermission = new ArrayList<>();
            for (String s : permissions) {
                if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                    needPermission.add(s);
                }
            }
            for (String s : needPermission) {
                if (!needPermission.isEmpty()) {
                    ActivityCompat.requestPermissions(this, new String[]{needPermission.get(i)}, 1234);
                }
                i++;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
