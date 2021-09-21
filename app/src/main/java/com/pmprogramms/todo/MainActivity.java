package com.pmprogramms.todo;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.pmprogramms.todo.databinding.ActivityMainBinding;
import com.pmprogramms.todo.helpers.tools.Permissions;
import com.pmprogramms.todo.helpers.user.UserData;
import com.pmprogramms.todo.utils.screen.NotificationBar;
import com.pmprogramms.todo.view.fragments.TodoFragmentDirections;
import com.pmprogramms.todo.view.reminders.RemindersActivity;
import com.pmprogramms.todo.view.search.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        new NotificationBar(getWindow()).updateColorNotificationBar();
        new Permissions(this).getAllPermissions();

        activityMainBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
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
        activityMainBinding.customAppBar.searchLabel.setOnClickListener(this);
        activityMainBinding.customAppBar.openSlideMenu.setOnClickListener(this);
        activityMainBinding.navBar.setOnNavigationItemSelectedListener(this);
        activityMainBinding.navigationView.setNavigationItemSelectedListener(this);

        deleteOldData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (getIntent().getStringExtra("id") != null) {
            NavDirections directions = TodoFragmentDirections.actionTodoFragmentToTodoDetailsFragment(getIntent().getStringExtra("id"));
            Navigation.findNavController(activityMainBinding.fragmentContainerView).navigate(directions);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.todo_item) {
            Navigation.findNavController(activityMainBinding.fragmentContainerView).navigate(R.id.todoFragment);
            return true;
        } else if (itemId == R.id.user_profile) {
            Navigation.findNavController(activityMainBinding.fragmentContainerView).navigate(R.id.userProfileFragment);
            return true;
        } else if (itemId == R.id.tags) {
            Navigation.findNavController(activityMainBinding.fragmentContainerView).navigate(R.id.tagsFragment);
            activityMainBinding.drawerLayout.closeDrawers();
            return true;
        } else if (itemId == R.id.archive_todo) {
            Navigation.findNavController(activityMainBinding.fragmentContainerView).navigate(R.id.todoArchiveFragment);
            activityMainBinding.drawerLayout.closeDrawers();
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
            // FIXME: 21/09/2021 fix open instagram
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
        if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START))
            activityMainBinding.drawerLayout.closeDrawers();
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
            activityMainBinding.drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void deleteOldData() {
        new UserData(getApplicationContext()).removeOldData();
    }
}
