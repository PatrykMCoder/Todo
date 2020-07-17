package com.example.todo.helpers.todohelper.tags;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;

import com.example.todo.R;

import java.util.ArrayList;
import java.util.Arrays;

public class LoadTagsHelper {

    private Context context;

    public LoadTagsHelper(Context context) {
        this.context = context;
    }

    public ArrayList<String> loadTags() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("custom_tags", Context.MODE_PRIVATE);
        ArrayList<String> dataTags = new ArrayList<>();

        dataTags.clear();
        dataTags.addAll(Arrays.asList(context.getResources().getStringArray(R.array.tags)));

        for (Object o : sharedPreferences.getAll().values()) {
            dataTags.add(o.toString());
        }

        return dataTags;
    }
}
