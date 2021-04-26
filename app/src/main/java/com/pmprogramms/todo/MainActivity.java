package com.pmprogramms.todo;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pmprogramms.todo.helpers.search.TitleSearchHandle;
import com.pmprogramms.todo.helpers.tools.Permissions;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.screen.NotificationBar;
import com.pmprogramms.todo.view.fragments.TagsFragment;
import com.pmprogramms.todo.view.fragments.TodoArchiveFragment;
import com.pmprogramms.todo.view.fragments.TodoDetailsFragment;
import com.pmprogramms.todo.view.fragments.TodoFragment;
import com.pmprogramms.todo.view.reminders.RemindersActivity;
import com.pmprogramms.todo.view.fragments.user.UserProfileFragment;
import com.pmprogramms.todo.view.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private View includeView;
    private EditText searchEditText;
    private ImageButton openSlideMenuButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new NotificationBar(getWindow()).updateColorNotificationBar();
        new Permissions(this).getAllPermissions();
        deleteOldData();
        String userToken = new UserData(this).getUserToken();
        initView();

        if (TitleSearchHandle.getTitle() != null) {
            initFragment(new TodoDetailsFragment(userToken, TitleSearchHandle.getId(), TitleSearchHandle.getTitle(), TitleSearchHandle.isArchive(), Color.parseColor(TitleSearchHandle.getColor())), false);
        } else
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
        int itemId = menuItem.getItemId();
        if (itemId == R.id.todo_item) {
            initFragment(new TodoFragment(), false);
            return true;
        } else if (itemId == R.id.user_profile) {
            initFragment(new UserProfileFragment(), true);
            return true;
        } else if (itemId == R.id.tags) {
            initFragment(new TagsFragment(), true);
            drawerLayout.closeDrawers();
            return true;
        } else if (itemId == R.id.note_item || itemId == R.id.settings || itemId == R.id.archive_note) {
            Toast.makeText(this, "Available in future :)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.archive_todo) {
            initFragment(new TodoArchiveFragment(), true);
            drawerLayout.closeDrawers();
            return true;
        } else if (itemId == R.id.reminders) {
            Intent intent = new Intent(this, RemindersActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.send_feedback) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:pmarciszewski774@gmail.com"));
            startActivity(emailIntent);
            return true;
        } else if (itemId == R.id.contact_with_me) {
            Uri uri = Uri.parse("https://www.instagram.com/patryk_programmer/");
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW);
            instagramIntent.setData(Uri.parse("com.instagram.android"));
            try {
                startActivity(instagramIntent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.instagram.com/patryk_programmer/")));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.search_label) {
            Intent searchActivity = new Intent(this, SearchActivity.class);
            searchActivity.setAction(Intent.ACTION_SEARCH);
            startActivity(searchActivity);
        } else if (id == R.id.open_slide_menu) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // 14.04.2021
    // This method is for remove user_id from devices (Version app: 2.1.12 to 2.2)
    // Todo -> Remove this method soon
    private void deleteOldData() {
        new UserData(getApplicationContext()).removeOldData();
    }
}
