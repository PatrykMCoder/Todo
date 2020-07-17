package com.example.todo.helpers.todohelper.tags;

public class TagsHelper {
    private static String tag = "no tag";
    private static String title;

    public static String getTag() {
        return tag;
    }

    public static void setTag(String tag) {
        TagsHelper.tag = tag;
    }

    public static void setTitle(String title){
        TagsHelper.title = title;
    }

    public static String getTitle(){
        return title;
    }
}
