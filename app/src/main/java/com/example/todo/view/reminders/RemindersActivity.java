package com.example.todo.view.reminders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.MainActivity;
import com.example.todo.R;
import com.example.todo.helpers.reminders.ReadAboutRemindersFromSharedpreference;
import com.example.todo.utils.recyclerView.ReminderRecyclerAdapter;
import com.example.todo.utils.reminders.ReminderBroadcastReceiver;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;

public class RemindersActivity extends AppCompatActivity {

    private MainActivity mainActivity;
    private SharedPreferences remindersTitlePreference;
    private SharedPreferences remindersIdsPreference;
    private ArrayList<String> dataReminder;

    private RecyclerView reminderList;
    private RecyclerView.Adapter reminderAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ReadAboutRemindersFromSharedpreference readAboutRemindersFromSharedpreference;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list);

        readAboutRemindersFromSharedpreference = new ReadAboutRemindersFromSharedpreference(this);

        getData();

        toolbar = findViewById(R.id.custom_tool_bar);
        reminderList = findViewById(R.id.reminders_container);
        reminderAdapter = new ReminderRecyclerAdapter(dataReminder);

        layoutManager = new LinearLayoutManager(mainActivity, RecyclerView.VERTICAL, false);
        reminderList.setLayoutManager(layoutManager);

        reminderList.swapAdapter(reminderAdapter, true);
        setSupportActionBar(toolbar);
    }

    private void getData() {
        dataReminder = readAboutRemindersFromSharedpreference.readTitles();
    }
}
