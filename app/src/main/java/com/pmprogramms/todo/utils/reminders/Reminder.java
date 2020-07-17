package com.pmprogramms.todo.utils.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Reminder {
    private Context context;
    private AlarmManager alarmManager;
    private long time;
    private ReminderRepeatType typeRepeatReminder;
    private ReminderDisplayType typeDisplayReminder;
    private String titleTodoForReminder;

    public Reminder(Context context) {
        this.context = context;
    }

    public Reminder(Context context, String titleTodoForReminder, long time, ReminderRepeatType typeRepeatReminder, ReminderDisplayType typeDisplayReminder) {
        this.context = context;
        this.titleTodoForReminder = titleTodoForReminder;
        this.time = time;
        this.typeRepeatReminder = typeRepeatReminder;
        this.typeDisplayReminder = typeDisplayReminder;
    }

    public void createReminder() {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        intent.putExtra("todoName", titleTodoForReminder);
        intent.putExtra("displayType", typeDisplayReminder.ordinal());
        intent.putExtra("repeatType", typeRepeatReminder.ordinal());
        long id = time;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (typeRepeatReminder == ReminderRepeatType.NONE)
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, calculateRepeatTime(), pendingIntent);
        }
    }

    public void removeReminder(int id){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.cancel(pendingIntent);
    }

    private long calculateRepeatTime() {
        if (typeRepeatReminder == ReminderRepeatType.FOUR_TIMES_PER_DAY) {
            return AlarmManager.INTERVAL_HOUR * 6;
        } else if (typeRepeatReminder == ReminderRepeatType.ONE_TIME_PER_DAY) {
            return AlarmManager.INTERVAL_DAY;
        }else if(typeRepeatReminder == ReminderRepeatType.NONE){
            return 0;
        }

        return 0;
    }
}
