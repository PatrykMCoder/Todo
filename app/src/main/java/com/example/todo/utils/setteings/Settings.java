package com.example.todo.utils.setteings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class Settings {

    private Context context;
    private String msg;

    public Settings(Context context) {
        this.context = context;
    }

    public ArrayList<Integer> loadBackgroundColor() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSettings_colorPicker", 0);
        int alpha = (sharedPreferences.getInt("color_a", 255));
        int red = sharedPreferences.getInt("color_r", 243);
        int green = (sharedPreferences.getInt("color_g", 167));
        int blue = (sharedPreferences.getInt("color_b", 130));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(alpha);
        colors.add(red);
        colors.add(green);
        colors.add(blue);

        return colors;
    }

    public void saveBackgroundColor(int alpha, int red, int green, int blue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSettings_colorPicker", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (alpha  <= 100 || (red >= 200 && green >= 200 && blue >= 200)) {
            msg = "You can't change background color, color change to default color. Too bright color!";
            editor.putInt("color_a", 255);
            editor.putInt("color_r", 243);
            editor.putInt("color_g", 167);
            editor.putInt("color_b", 130);
            editor.apply();
        } else {
            editor.putInt("color_a", alpha);
            editor.putInt("color_r", red);
            editor.putInt("color_g", green);
            editor.putInt("color_b", blue);
            editor.apply();

            msg = "You change background color";
        }
    }

    public void saveSortTodo(String selectedSort){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSettings_sortTodo", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("sort_todo", selectedSort);
        editor.apply();
    }

    public String loadSortTodo(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSettings_sortTodo", 0);
        return sharedPreferences.getString("sort_todo", "Data Created");
    }

    public void setSecurityFingerprint(Object state){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSettings_security", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("secure_fingerprint", (Boolean) state);
        editor.apply();
    }

    public boolean getSecurityFingerprint(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserSettings_security", 0);
        return sharedPreferences.getBoolean("secure_fingerprint", false);
    }

    public String message(){
        return msg;
    }
}
