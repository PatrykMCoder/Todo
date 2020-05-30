package com.example.todo.utils.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.MainActivity;
import com.example.todo.R;

public class Notification {
    private Context context;

    private static final String TAG = "CustomNotificationClass";

    public Notification(Context context) {
        this.context = context;
    }

    public void createNotificationsChanel() {
        //reminder channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Reminders";
            String description = "Your reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NotificationChannels.REMINDER_CHANNEL_ID, name, importance);
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
            channel.setLightColor(android.app.Notification.DEFAULT_LIGHTS);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (context != null && notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    public void notifyReminder(String titleTodo){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannels.REMINDER_CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.app_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_icon));
        builder.setContentTitle(String.format("Reminder for: %s", titleTodo.replace("_", " ")));
        builder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        builder.setAutoCancel(true);
        //todo add buttons for action snooze

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(Integer.parseInt(NotificationChannels.REMINDER_CHANNEL_ID), builder.build());
    }

}
