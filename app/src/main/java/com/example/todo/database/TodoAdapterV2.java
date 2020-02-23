package com.example.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.todo.helpers.GetDataHelper;

import java.util.ArrayList;

public class TodoAdapterV2 {
    private final static String TAG = "TodoAdapterV2";
    private static String nameForDB;
    // TODO: 2020-02-05 WE HAVE EMPTY ARRAY OF TODO
    private Context context;
    private DBHelper dbHelper;
    private String title;
    private ArrayList<String> dataTask;
    private String q_createTable;
    private String q_dropTable;
    private SQLiteDatabase database;

    public TodoAdapterV2(Context context) {
        this.context = context;
    }

    public TodoAdapterV2(Context context, String title) {
        this.context = context;


        title = title.replace(" ", "_");
        nameForDB = title + ".db";
        this.title = title;
    }

    public TodoAdapterV2(Context context, String title, ArrayList<String> data) {
        this.context = context;
        this.title = title;
        this.dataTask = data;

        createQueryNewTable();
        createQueryDropTable();
    }

    private void createQueryNewTable() {
        if (dataTask != null) {
            nameForDB = (title.replace(" ", "_")) + ".db";
            title = title.replace(" ", "_");
            q_createTable = String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT NOT NULL, tag INTEGER, done INTEGER);", title);

        } else
            q_createTable = "CREATE TABLE " + title;
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
            Log.d(TAG, "openDB: EXCEPTION: " + e.getMessage());
        }
    }

    public void closeDB() {
        dbHelper.close();
    }

    public void saveToDB() {
        int len = dataTask.size() - 1;
        title = title.replace(" ", "_");
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i <= len; i++) {
            contentValues.put("task", String.format("'%s'", dataTask.get(i)));
            contentValues.put("tag", "");
            contentValues.put("done", 0);

            database.insert(title, null, contentValues);
        }
    }


    public ArrayList<String> testLoadData(String title) {
        ArrayList<String> data = new ArrayList<>();
        title = title.replace(" ", "_");
        String q = String.format("SELECT * from %s;", title);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            data.add(cursor.getString(cursor.getColumnIndex("task")));
        }

        cursor.close();
        Log.d(TAG, "testLoadData: " + data);

        return data;
    }

    public ArrayList<GetDataHelper> loadAllData(String title) {
        ArrayList<GetDataHelper> data = new ArrayList<>();

        String q = String.format("SELECT task, done, tag from %s", title);
        Cursor cursor = database.rawQuery(q, null);
        GetDataHelper getDataHelper;

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String task = cursor.getString(cursor.getColumnIndex("task"));
            int done = cursor.getInt(cursor.getColumnIndex("done"));
            String tag = cursor.getString(cursor.getColumnIndex("tag"));
            getDataHelper = new GetDataHelper(task, done, tag);
            data.add(getDataHelper);
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