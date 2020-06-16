package com.example.todo.utils.loader;

import android.content.Context;
import android.util.Log;

import com.example.todo.utils.formats.StringFormater;

import java.io.File;
import java.util.ArrayList;

public class LoaderDatabases {
    private Context context;

    private File[] listFile;
    private File file;
    private String path;
    private int counter;

    public LoaderDatabases(Context context) {
        this.context = context;
    }

    private String getPath() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            path = context.getDataDir() + "/databases";
        } else {
            path = "data/data/" + context.getPackageName() + "/databases/";
        }
        return path;
    }

    public ArrayList<String> loadTitles() {
        file = new File(getPath());
        listFile = file.listFiles();

        ArrayList<String> titles = new ArrayList<>();
        counter = 0;
        if (listFile != null)
            for (File f : listFile) {
                String name = f.getName();
                String nameWithoutExtension;
                nameWithoutExtension = name.split("\\.")[0];
                if (name.endsWith(".db")) {
                    titles.add(nameWithoutExtension);
                    counter++;
                }

            }
        return titles;
    }

    public boolean checkFileExist(String title) {
        file = new File(getPath());
        listFile = file.listFiles();
        title = new StringFormater(title).formatTitle() + ".db";
        if (listFile != null) {
            for (File f : listFile) {
                String t = f.getName();
                if (t.equals(title))
                    return true;
            }
        }
        return false;
    }
}
