package com.example.todo.utils.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.todo.MainActivity;
import com.example.todo.ReminderAlarmActivity;
import com.example.todo.utils.notifications.Notification;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderBCReceiver";
    private int index;
    private String todoTitle;
    private Context context;

    private Notification notification;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        notification = new Notification(context);
        index = intent.getIntExtra("displayType", -1);
        ReminderDisplayType reminderDisplayType = null;

        if (index > -1 && index < ReminderDisplayType.values().length) {
            reminderDisplayType = ReminderDisplayType.values()[index];
        } else {
            //handle error
        }

        todoTitle = intent.getStringExtra("todoName");
        Log.d(TAG, "onReceive: " + reminderDisplayType);
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
