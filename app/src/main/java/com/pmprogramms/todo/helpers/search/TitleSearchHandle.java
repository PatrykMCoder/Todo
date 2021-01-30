package com.pmprogramms.todo.helpers.search;

public class TitleSearchHandle {
    private static String title;
    private static String id;
    private static String color;
    private static boolean archive;

    public static String getTitle() {
        return title;
    }

    public static void setTitle(String title) {
        TitleSearchHandle.title = title;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        TitleSearchHandle.id = id;
    }

    public static void setArchive(boolean archive) {
        TitleSearchHandle.archive = archive;
    }
    public static boolean isArchive() {
        return archive;
    }

    public static String getColor() { return color; }

    public static void setColor(String color) { TitleSearchHandle.color = color; }
}
