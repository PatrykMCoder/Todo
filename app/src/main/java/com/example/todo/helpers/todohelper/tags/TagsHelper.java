package com.example.todo.helpers.todohelper.tags;

public class TagsHelper {
    private static String tag = "no tag";

    public static String getTag() { return tag; }

    public static void setTag(String tag) {
        TagsHelper.tag = tag;
    }

}
