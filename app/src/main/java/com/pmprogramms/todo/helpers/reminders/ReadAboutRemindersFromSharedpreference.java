package com.pmprogramms.todo.helpers.reminders;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class ReadAboutRemindersFromSharedpreference {
    private final Context context;

    public ReadAboutRemindersFromSharedpreference(Context context) {
        this.context = context;
    }

    public ArrayList<String> readTitles() {
        SharedPreferences remindersTitlePreference = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
        ArrayList<String> dataReminder = new ArrayList<>();

        for (Object data : remindersTitlePreference.getAll().values().toArray()) {
            dataReminder.add(data.toString());
        }

        return dataReminder;
    }

    public int getID(String title) {
        SharedPreferences remindersIdsPreference = context.getSharedPreferences("reminders_id", Context.MODE_PRIVATE);
        return remindersIdsPreference.getInt(title, 0);
    }
}
