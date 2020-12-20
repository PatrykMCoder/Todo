package com.pmprogramms.todo.helpers.text;

import android.util.DisplayMetrics;

import com.pmprogramms.todo.MainActivity;

public class TextFormat {
    public String formatForTextLastEdit(MainActivity mainActivity, String data) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int pixelsWidth = displayMetrics.widthPixels;
        return pixelsWidth < 800 ? String.format("Edited:\n%s", data.split("T")[0]) : String.format("Edited: %s", data.split("T")[0]);
    }

    public String splitTextTag(String tag) {
        if (tag != null && tag.length() >= 7) {
            tag = tag.substring(0, Math.min(tag.length(), 7)) + "...";
        }
        return tag;
    }

    public String formatSelectedText(String text, TypeFormat typeFormat) {
        StringBuilder stringBuilder = new StringBuilder(text);
        switch (typeFormat) {
            case BOLD: {
                stringBuilder.insert(0, "<b>");
                stringBuilder.append("</b>");
                break;
            }
            case ITALIC: {
                stringBuilder.insert(0, "<i>");
                stringBuilder.append("</i>");
                break;
            }
            case UNDER_LINE: {
                stringBuilder.insert(0, "<u>");
                stringBuilder.append("</u>");
                break;
            }
            case STRIKETHROUGH: {
                stringBuilder.insert(0, "<strike>");
                stringBuilder.append("</strike>");
                break;
            }
        }

        return stringBuilder.toString();
    }
}
