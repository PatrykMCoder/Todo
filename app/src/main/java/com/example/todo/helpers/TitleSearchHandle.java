package com.example.todo.helpers;

import com.example.todo.MainActivity;

public class TitleSearchHandle {
    private static String title;

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        TitleSearchHandle.title = title;
    }
}
