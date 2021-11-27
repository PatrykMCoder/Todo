package com.pmprogramms.todo.view.reminders;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pmprogramms.todo.R;
import com.pmprogramms.todo.helpers.reminders.ReadAboutRemindersFromSharedpreference;
import com.pmprogramms.todo.utils.recyclerView.ReminderRecyclerAdapter;

import java.util.ArrayList;

public class RemindersActivity extends AppCompatActivity {
    private ArrayList<String> dataReminder;
    private ReadAboutRemindersFromSharedpreference readAboutRemindersFromSharedpreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list);

        readAboutRemindersFromSharedpreference = new ReadAboutRemindersFromSharedpreference(this);

        getData();

        Toolbar toolbar = findViewById(R.id.custom_tool_bar);
        RecyclerView reminderList = findViewById(R.id.reminders_container);
        ReminderRecyclerAdapter reminderAdapter = new ReminderRecyclerAdapter(dataReminder);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        reminderList.setLayoutManager(layoutManager);

        reminderList.swapAdapter(reminderAdapter, true);
        setSupportActionBar(toolbar);
    }

    private void getData() {
        dataReminder = readAboutRemindersFromSharedpreference.readTitles();
    }
}
