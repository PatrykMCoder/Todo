package com.example.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.todo.helpers.CreateTodoHelper;
import com.example.todo.helpers.EditTodoHelper;
import com.example.todo.helpers.GetDataHelper;
import com.example.todo.utils.formats.StringFormater;

import java.util.ArrayList;
import java.util.List;

public class TodoAdapter {
    private final static String TAG = "TodoAdapter";
    private static String nameForDB;
    private Context context;
    private DBHelper dbHelper;
    private String title;

    private ArrayList<String> dataTask;
    private ArrayList<CreateTodoHelper> helper;

    private String q_createTable;
    private String q_dropTable;
    private SQLiteDatabase database;

    public TodoAdapter(Context context) {
        this.context = context;
    }

    public TodoAdapter(Context context, String title) {
        this.context = context;

        title = new StringFormater(title).formatTitle();
        nameForDB = title + ".db";
        this.title = title;
    }

    public TodoAdapter(Context context, String title, ArrayList<CreateTodoHelper> helper) {
        this.context = context;
        this.title = title;
        this.helper = helper;

        createQueryNewTable();
        createQueryDropTable();
    }

    private void createQueryNewTable() {
        if (helper != null || title != null) {
            nameForDB = new StringFormater(title).formatTitle() + ".db";
            title = new StringFormater(title).formatTitle();
            q_createTable = String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT NOT NULL, tag INTEGER, done INTEGER, last_edited DATE NOT NULL);", title);
        }
    }

    private void createQueryDropTable() {
        q_dropTable = "DROP TABLE IF EXISTS " + title;
    }

    private void openDB() {
        dbHelper = new DBHelper(context, nameForDB, null, 1);
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = dbHelper.getReadableDatabase();
        }
    }

    private void closeDB() {
        dbHelper.close();
    }

    public void saveToDB() {
        openDB();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < helper.size(); i++) {
            contentValues.put("task", String.format("%s", helper.get(i).getTask()));
            contentValues.put("tag", String.format("%s", helper.get(i).getTag()));
            contentValues.put("done", String.format("%s", helper.get(i).getDone()));
            contentValues.put("last_edited", String.format("%s", helper.get(i).getLastEdited()));

            database.insert(title, null, contentValues);
        }
        closeDB();
    }

    public ArrayList<GetDataHelper> loadAllData() {
        openDB();
        ArrayList<GetDataHelper> data = new ArrayList<>();

        String q = String.format("SELECT task, done, tag, last_edited from %s", title);
        Cursor cursor = database.rawQuery(q, null);
        GetDataHelper getDataHelper;

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String task = cursor.getString(cursor.getColumnIndex("task"));
            int done = cursor.getInt(cursor.getColumnIndex("done"));
            String tag = cursor.getString(cursor.getColumnIndex("tag"));
            String lastEdited = cursor.getString(cursor.getColumnIndex("last_edited"));
            getDataHelper = new GetDataHelper(task, done, tag, lastEdited);
            data.add(getDataHelper);
        }
        cursor.close();
        closeDB();
        return data;
    }

    public float getPercentDoneTask() {
        openDB();
        ArrayList<GetDataHelper> data = new ArrayList<>();
        float doneTask = 0;
        float allTask = 0;
        float percent = 0;
        String q = String.format("SELECT done from %s", title);
        GetDataHelper getDataHelper;

        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToPosition(-1);

        while (cursor.moveToNext()) {
            float done = cursor.getInt(cursor.getColumnIndex("done"));

            if (done == 1) doneTask += 1;

        }

        allTask = cursor.getCount();

        cursor.close();
        closeDB();
        return (doneTask / allTask) * 100;
    }

    public void deleteTodo(String title) {
        openDB();
        String q = String.format("DROP TABLE %s", title);
        database.delete(title, null, null);
        closeDB();
    }

    public void editTodo(String title, ArrayList<EditTodoHelper> dataToEdit) {
        openDB();
        database.execSQL("delete from " + title);

        ContentValues contentValues = new ContentValues();

        for (int i = 0; i < dataToEdit.size(); i++) {
            String task = dataToEdit.get(i).getTask();
            int done = dataToEdit.get(i).getDone();
            String tag = dataToEdit.get(i).getTag();
            String lastEdited = dataToEdit.get(i).getLastEdited();

            contentValues.put("task", String.format("%s", dataToEdit.get(i).getTask()));
            contentValues.put("tag", String.format("%s", dataToEdit.get(i).getTag()));
            contentValues.put("done", String.format("%s", dataToEdit.get(i).getDone()));
            contentValues.put("last_edited", String.format("%s", dataToEdit.get(i).getLastEdited()));

            database.insert(title, null, contentValues);
        }
        closeDB();
    }

    public void changeStatusTask(String title, String task, int status) {
        openDB();
        title = new StringFormater(title).formatTitle();
        String q = String.format("SELECT id FROM %s where task = '%s'", title, task);
        int id = 0;
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToFirst();
        id = cursor.getInt(cursor.getColumnIndex("id"));
        cursor.close();

        ContentValues contentValues = new ContentValues();
        contentValues.put("done", status);

        database.update(title, contentValues, String.format("id = %s", id), null);
        closeDB();
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            if (q_createTable != null)
                db.execSQL(q_createTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (q_createTable != null) {
                db.execSQL(q_dropTable);
                onCreate(db);
            }
        }
    }
}