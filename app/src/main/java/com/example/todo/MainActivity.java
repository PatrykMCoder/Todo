package com.example.todo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.todo.helpers.TitleSearchHandle;
import com.example.todo.view.fragments.TodoDetailsFragment;
import com.example.todo.view.fragments.TodoFragment;
import com.example.todo.view.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    private View includeView;
    private EditText searchEditText;
    private ImageButton openSlideMenuButton;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ArrayList<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllPermission();
        initView();

        if (TitleSearchHandle.getTitle() != null)
            initFragment(new TodoDetailsFragment(TitleSearchHandle.getTitle()), false);
        else
            initFragment(new TodoFragment(), false);
    }

    @SuppressLint("ResourceType")
    private void initView() {
        bottomNavigationView = findViewById(R.id.nav_bar);
        includeView = findViewById(R.id.custom_app_bar);
        searchEditText = includeView.findViewById(R.id.search_label);
        openSlideMenuButton = includeView.findViewById(R.id.open_slide_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        searchEditText.setOnClickListener(this);
        openSlideMenuButton.setOnClickListener(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
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
                initFragment(new TodoFragment(), false);
                return true;
            case R.id.note_item:
//                initFragment(new NoteFragment(), true);
                Toast.makeText(getApplicationContext(), "Available in future :)", Toast.LENGTH_LONG).show();
                return false;
            case R.id.settings_item:
                Toast.makeText(getApplicationContext(), "Available in future :)", Toast.LENGTH_LONG).show();
                return false;

            case R.id.archive_todo: {
                Toast.makeText(this, "Available in future :)", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.archive_note: {
                Toast.makeText(this, "Available in future :)", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.reminders: {
                Toast.makeText(this, "Available in future :)", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.send_feedback: {
                Toast.makeText(this, "Available in future :)", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.contact_with_me: {
                Toast.makeText(this, "Available in future :)", Toast.LENGTH_SHORT).show();
                return true;
            }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_label: {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                startActivity(intent);
                break;
            }
            case R.id.open_slide_menu: {
                drawerLayout.openDrawer(Gravity.START);
                break;
            }
        }
    }
}
