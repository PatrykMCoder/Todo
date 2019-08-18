package com.example.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoAdapter{
    //db config
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "DB_TODO_NOTE.db";
    private static final String DB_TABLE_NAME = "TABLE_TODO_NOTE";

    //table
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMNT = 0;
    public static final String KEY_DESCRIPTION = "description";
    public static final String DESCRIPTION_OPTONS = "TEXT NOT NULL";
    public static final int DESCRIPTION_COLUMN = 1;
    public static final String KEY_COMPLETED = "completed";
    public static final String COMPLETED_OPTIONS = "INTEGER DEFAULT 0";
    public static final int COMPLETED_COLUMN = 2;

    //query
    private static final String DB_CREATE_TODO_TABLE = "CREATE TABLE " + DB_TABLE_NAME + "( " + KEY_ID + " " + ID_OPTIONS + ", " + KEY_DESCRIPTION + " " + DESCRIPTION_OPTONS + ", " + KEY_COMPLETED + " " + COMPLETED_OPTIONS + ");";
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
    }

    public void closeDB(){
        dbHelper.close();
    }

    public long insertTODODescription(String description){
        ContentValues contentValues = new ContentValues();
        contentValues.put("KEY_DESCRIPTION", description);
        return  database.insert(DB_TABLE_NAME, null, contentValues);
    }

    public Cursor getAllTodo(long id){
        String[] columns = {KEY_ID, KEY_DESCRIPTION, KEY_COMPLETED};
        return  database.query(DB_TABLE_NAME, columns, null, null, null, null, null);
    }

    public boolean removeTodo(long id){
        String where = KEY_ID  + "=" + id;
        return database.delete(DB_TABLE_NAME, where, null ) > 0;
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
