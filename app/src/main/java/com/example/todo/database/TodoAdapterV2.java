package com.example.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.todo.MainActivity;

import java.util.ArrayList;

public class TodoAdapterV2 {
    // TODO: 2020-02-05 WE HAVE EMPTY ARRAY OF TODO 
    private Context context;
    private DBHelper dbHelper;

    private String title;
    private static String nameForDB;
    private ArrayList<String> dataTask;
    private String q_createTable;
    private String q_dropTable;

    private SQLiteDatabase database;

    private final static String TAG = "TodoAdapterV2";

    public TodoAdapterV2(Context context) {
        this.context = context;
    }

    public TodoAdapterV2(Context context, String title, ArrayList<String> data) {
        this.context = context;
        this.title = title;
        this.dataTask = data;

        createQueryNewTable();
        createQueryDropTable();
    }

    private void createQueryNewTable() {
        int len = dataTask.size();
        nameForDB = (title.replace(" ", "_")) + ".db";
        title = title.replace(" ", "_");

        q_createTable = "CREATE TABLE " + title + "(id INTEGER PRIMARY KEY AUTOINCREMENT";

        for (int i = 0; i <= len; i++) {
            if (i != len) {
                q_createTable += ", task_" + (i + 1) + " TEXT";
            } else {
                q_createTable += ", task_" + (i + 1) + " TEXT);";
            }
        }
    }

    private void createQueryDropTable() {
        q_dropTable = "DROP TABLE IF EXISTS " + title;
    }

    public void openDB() {
        dbHelper = new DBHelper(context, nameForDB, null, 1);
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = dbHelper.getReadableDatabase();
            e.printStackTrace();
        }
    }

    public void closeDB() {
        dbHelper.close();
    }

    public void saveToDB() {
        int len = dataTask.size();
        title = title.replace(" ", "_");
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < len; i++) {
            contentValues.put("task_" + (i + 1), String.format("'%s'", dataTask.get(i)));
        }

        database.insert(title, null, contentValues);
    }


    public ArrayList<String> testLoadData(String title) {
        ArrayList<String> data = new ArrayList<>();
        title = title.replace(" ", "_");
        String q = String.format("SELECT * from %s;", title);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToFirst();
        int i = 0;
        String t = "task_";
        while (cursor.moveToNext()) {
            if (i == 0)
                data.add(cursor.getString(cursor.getColumnIndex("id")));
            else
                data.add(cursor.getString(cursor.getColumnIndex("task_" + i)));
            i++;
        }

        cursor.close();

        return data;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(q_createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(q_dropTable);
            onCreate(db);
        }
    }
}


/*
if (getDataTask() != null)
            for (int i = 0; i < dataTask.size(); i++) {
                q_createTable += ", task_" + (i + 1) + " TEXT NOT NULL";

                if (i == dataTask.size() - 1)
                    q_createTable += ");";
            }
 */