package com.example.todo.utils.reminders;

public class ReminderHelper {
    private static String title;

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        ReminderHelper.title = title;
    }
}
