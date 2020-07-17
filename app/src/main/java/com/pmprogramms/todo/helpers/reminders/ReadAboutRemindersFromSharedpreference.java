package com.pmprogramms.todo.helpers.reminders;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.pmprogramms.todo.utils.reminders.ReminderBroadcastReceiver;

import java.util.ArrayList;

public class ReadAboutRemindersFromSharedpreference {
    private Context context;

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

//    public ArrayList<PendingIntent> readPendings() {
//        SharedPreferences remindersIdsPreference = context.getSharedPreferences("reminders_id", Context.MODE_PRIVATE);
//        ArrayList<PendingIntent> pendingIntents = new ArrayList<>();
//        PendingIntent pendingIntent;
//        for (String dataTitle : readTitles()) {
//            int id = remindersIdsPreference.getInt(dataTitle, 0);
//            Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
//            pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
//        }
//        return pendingIntents;
//    }

    public int getID(String title) {
        SharedPreferences remindersIdsPreference = context.getSharedPreferences("reminders_id", Context.MODE_PRIVATE);
        return remindersIdsPreference.getInt(title, 0);
    }
}
