package com.example.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TodoAdapter{
    //db config
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "DB_TODO_NOTE.db";
    private static final String DB_TABLE_NAME = "TABLE_TODO_NOTE";

    //table
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMNT = 0;

    public static final String KEY_TITLE = "title";
    public static final String TITLE_OPTIONS = "TEXT NOT NULL";
    public static final int TITLE_COLUMN = 1;

    public static final String KEY_DESCRIPTION = "description";
    public static final String DESCRIPTION_OPTIONS = "TEXT NOT NULL";
    public static final int DESCRIPTION_COLUMN = 2;

    public static final String KEY_DATE_CREATE = "ce";
    public static final String DATE_CREATE_OPTIONS = "DATETIME DEFAULT CURRENT_TIMESTAMP";
    public static final int DATE_CREATE_COLUMN = 3;

    public static final String KEY_DATE_LIMIT = "time_to_finish";
    public static final String DATE_LIMIT_OPTIONS = "DATETIME DEFAULT CURRENT_TIMESTAMP";
    public static final int DATE_LIMIT_COLUMN = 4;

    public static final String KEY_COMPLETED = "completed";
    public static final String COMPLETED_OPTIONS = "INTEGER DEFAULT 0";
    public static final int COMPLETED_COLUMN = 5;

    public static final String KEY_ARCHIVE = "archive";
    public static final String ARCHIVE_OPTIONS = "INTEGER DEFAULT 0";
    public static final int ARCHIVE_COLUMN = 5;

    private String sort = "";

    private final String SORT_BY_DATE_CREATED = "Date created";
    private final String SORT_BY_ALPHABETICALLY = "Alphabetically";
    private final String SORT_BY_DATE_REAMING = "Date reaming";

    private final static String TAG = "TodoAdapter";
    //query
    private static final String DB_CREATE_TODO_TABLE = "CREATE TABLE " + DB_TABLE_NAME + "( " +
            KEY_ID + " " + ID_OPTIONS + ", " +
            KEY_TITLE + " " + TITLE_OPTIONS + ", " +
            KEY_DESCRIPTION + " " + DESCRIPTION_OPTIONS + ", " +
            KEY_DATE_CREATE + " " + DATE_CREATE_OPTIONS + ", " +
            KEY_DATE_LIMIT + " " + DATE_LIMIT_OPTIONS + ", " + KEY_COMPLETED + " " + COMPLETED_OPTIONS + ", " + KEY_ARCHIVE + " " + ARCHIVE_OPTIONS + " );";

    private static final String DROP_TODO_TABLE = "DROP TABLE IF EXISTS " + DB_TABLE_NAME;

    private SQLiteDatabase database;
    private Context context;
    private DBHelper dbHelper;

    public TodoAdapter(Context context){
        this.context = context;
    }

    public void openDB(){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        try{
            database = dbHelper.getWritableDatabase();
        }catch (SQLException e){
            database = dbHelper.getReadableDatabase();
            e.printStackTrace();
        }

        formatStingSort();
    }

    public void closeDB(){
        dbHelper.close();
    }

    public long insertDataTodo(String title, String description, String dateCreate, String dateLimit, int complete){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_DESCRIPTION, description);
        contentValues.put(KEY_DATE_CREATE, dateCreate);
        contentValues.put(KEY_DATE_LIMIT, dateLimit);
        contentValues.put(KEY_COMPLETED, complete);

        return database.insert(DB_TABLE_NAME, null, contentValues);
    }

    public ArrayList<String> getAllTodo(){
        String[] columns = {KEY_ID, KEY_DESCRIPTION, KEY_COMPLETED};
        ArrayList<String> data = new ArrayList<>();
        Cursor cursorData = database.rawQuery(String.format("SELECT * FROM %s", DB_TABLE_NAME), null);
        cursorData.moveToPosition(0);
        while(cursorData.moveToNext()){
            data.add(cursorData.getString(cursorData.getColumnIndex(KEY_TITLE)));
            data.add(cursorData.getString(cursorData.getColumnIndex(KEY_DESCRIPTION)));
            data.add(cursorData.getString(cursorData.getColumnIndex(KEY_COMPLETED)));
        }
        cursorData.close();
        return data;
    }

    public int getIdColumn(String title, String description){
        int id = 0;
        String q = String.format("SELECT _ID FROM %s where title='%s' and description='%s'",DB_TABLE_NAME, title, description);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToFirst();
        id = Integer.parseInt(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow((KEY_ID)))));
        Log.d(TAG, "getIdColumn: id: " + id);
        cursor.close();
        return id;
    }

    public ArrayList<String> getTitleTODO(){
        ArrayList<String> data = new ArrayList<>();
        String q = String.format("SELECT %s from TABLE_TODO_NOTE ORDER BY %s", KEY_TITLE, sort);
        Log.d(TAG, "getTitleTODO: Query SQL: " + q);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToPosition(0);
        while (cursor.moveToNext()){
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        }
        Log.d(TAG, "getTitleTODO: DATA_TITLE: " + data);
        return data;
    }

    public ArrayList<String> getDescriptionTODO(){
        ArrayList<String> data = new ArrayList<>();
        String q = String.format("SELECT %s from TABLE_TODO_NOTE ORDER BY %s", KEY_DESCRIPTION, sort);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToPosition(0);
        while (cursor.moveToNext()){
            data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        }
        Log.d(TAG, "getDescriptionTODO: DATA_DESCRIPTION: " + data);
        return data;
    }

    public ArrayList<Integer> getDoneTODO(){
        ArrayList<Integer> data = new ArrayList<>();
        String q = String.format("SELECT %s from TABLE_TODO_NOTE ORDER BY %s", KEY_COMPLETED, sort);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()){
            data.add(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COMPLETED)));
        }
        Log.d(TAG, "getDescriptionTODO: DATA_DESCRIPTION: " + data);
        return data;
    }

    public ArrayList<Integer> getArchiveTODO(){
        ArrayList<Integer> data = new ArrayList<>();
        String q = String.format(("SELECT %s from %s"), KEY_ARCHIVE, DB_TABLE_NAME);
        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToFirst();
        while (cursor.moveToNext())
            data.add(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ARCHIVE)));
        return data;
    }

    public ArrayList<String> getRowTODO(int id){
        String q = String.format("Select * from %s where %s = %s", DB_TABLE_NAME, KEY_ID, id);
        Log.d(TAG, "getRowTODO: id: " + id);

        ArrayList<String> data = new ArrayList<>();

        Cursor cursor = database.rawQuery(q, null);
        cursor.moveToFirst();

        data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
        data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
        data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COMPLETED)));
        data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_CREATE)));
        data.add(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_LIMIT)));
        return data;
    }

    public boolean editTODO(String title, String description, String dataReaming, int id){
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_DESCRIPTION, description);
        contentValues.put(KEY_DATE_LIMIT, dataReaming);

        return database.update(DB_TABLE_NAME, contentValues, KEY_ID + "=" + id, null) > 0;
    }

    public boolean changeStatusTODO(int done, int id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_COMPLETED, done);
        return database.update(DB_TABLE_NAME, contentValues, KEY_ID + "=" + id, null) > 0;
    }

    public boolean archiveTODO(int id, int archive){
        ContentValues contentValues = new ContentValues();
        contentValues.put("archive", archive);
        return database.update(DB_TABLE_NAME, contentValues, KEY_ID + "=" + id, null) > 0;
    }

    public boolean deleteTODO(int id){
        return database.delete(DB_TABLE_NAME, KEY_ID + "=" + id, null) > 0;
    }

    private String getSort(){
        return sort;
    }

    public void setSort(String sort){
        this.sort = sort;
    }

    private void formatStingSort(){
        String sort = getSort();
        switch (sort) {
            case SORT_BY_DATE_CREATED:
                this.sort = KEY_DATE_CREATE + " ASC";
                break;
            case SORT_BY_DATE_REAMING:
                this.sort = KEY_DATE_LIMIT + " ASC";
                break;
            case SORT_BY_ALPHABETICALLY:
                this.sort = KEY_TITLE + " ASC";
                break;
            default:
                this.sort = null;
                break;
        }
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TODO_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
            db.execSQL(DROP_TODO_TABLE);
            onCreate(db);
        }
    }
}