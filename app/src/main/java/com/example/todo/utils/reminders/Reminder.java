package com.example.todo.utils.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

public class Reminder {
    private Context context;
    private AlarmManager alarmManager;
    private long time;
    private ReminderRepeatType typeRepeatReminder;
    private ReminderDisplayType typeDisplayReminder;
    private boolean repeatReminder;
    private String titleTodoForReminder;

    public Reminder(Context context, String titleTodoForReminder, long time, boolean repeatReminder, ReminderRepeatType typeRepeatReminder, ReminderDisplayType typeDisplayReminder) {
        this.context = context;
        this.titleTodoForReminder = titleTodoForReminder;
        this.time = time;
        this.repeatReminder = repeatReminder;
        this.typeRepeatReminder = typeRepeatReminder;
        this.typeDisplayReminder = typeDisplayReminder;
    }

    public void createReminder() {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("todoName", titleTodoForReminder);
        intent.putExtra("displayType", typeDisplayReminder.ordinal());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!repeatReminder)
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, calculateRepeatTime(), pendingIntent);
        }
    }

    private long calculateRepeatTime() {
        if (typeRepeatReminder == ReminderRepeatType.FOUR_TIMES_PER_DAY) {
            return AlarmManager.INTERVAL_HOUR * 6;
        } else if (typeRepeatReminder == ReminderRepeatType.N_HOUR_PER_DAY) {
            return AlarmManager.INTERVAL_HOUR;
        } else if (typeRepeatReminder == ReminderRepeatType.ONE_TIME_PER_DAY) {
            return AlarmManager.INTERVAL_DAY;
        }else if(typeRepeatReminder == ReminderRepeatType.NONE){
            return 0;
        }

        return 0;
    }
}
