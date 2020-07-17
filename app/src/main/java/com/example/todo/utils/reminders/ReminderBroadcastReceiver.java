package com.example.todo.utils.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.todo.ReminderAlarmActivity;
import com.example.todo.utils.formats.StringFormater;
import com.example.todo.utils.notifications.Notification;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderBCReceiver";
    private int indexDisplay;
    private int indexRepeat;
    private String todoTitle;
    private Context context;

    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        notification = new Notification(context);
        indexDisplay = intent.getIntExtra("displayType", -1);
        indexRepeat = intent.getIntExtra("repeatType", -1);
        ReminderDisplayType reminderDisplayType = null;
        ReminderRepeatType reminderRepeatType = null;

        if (indexDisplay > -1 && indexDisplay < ReminderDisplayType.values().length) {
            reminderDisplayType = ReminderDisplayType.values()[indexDisplay];
        } else {
            //handle error
        }

        if (indexRepeat > -1 && indexRepeat < ReminderDisplayType.values().length) {
            reminderRepeatType = ReminderRepeatType.values()[indexRepeat];
        } else {
            //handle error
        }

        todoTitle = intent.getStringExtra("todoName");

        if (reminderDisplayType != null)
            switch (reminderDisplayType) {
                case ONLY_NOTIFICATION:
                    showNotification();
                    break;
                case NOTIFICATION_AND_OPEN_ACTIVITY:
                    showNotification();
                    openActivity();
                    break;
                case ONLY_OPEN_ACTIVITY:
                    openActivity();
                    break;
                default:
                    break;
            }
        else{
            //handle error
        }

        if(reminderRepeatType != null){
            switch (reminderRepeatType){
                case NONE: {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("reminders_title", Context.MODE_PRIVATE);
                    SharedPreferences.Editor deleter = sharedPreferences.edit();
                    deleter.remove(todoTitle);
                    deleter.apply();
                }
                default: break;
            }
        }else{
            //handle error
        }

    }

    private void showNotification(){
        notification.notifyReminder(todoTitle);
    }

    private void openActivity(){
        Intent intent = new Intent(context, ReminderAlarmActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("todoName", todoTitle);
        context.startActivity(intent);
    }
}
