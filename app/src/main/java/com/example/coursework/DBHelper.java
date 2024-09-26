package com.example.coursework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "YogaCourses.db";
    public static final String TABLE_NAME = "courses";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "DAY";
    public static final String COL_3 = "TIME";
    public static final String COL_4 = "CAPACITY";
    public static final String COL_5 = "DURATION";
    public static final String COL_6 = "PRICE";
    public static final String COL_7 = "TYPE";
    public static final String COL_8 = "DESCRIPTION";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, DAY TEXT, TIME TEXT, CAPACITY INTEGER, DURATION INTEGER, PRICE TEXT, TYPE TEXT, DESCRIPTION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertCourse(String day, String time, int capacity, int duration, String price, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, day);
        contentValues.put(COL_3, time);
        contentValues.put(COL_4, capacity);
        contentValues.put(COL_5, duration);
        contentValues.put(COL_6, price);
        contentValues.put(COL_7, type);
        contentValues.put(COL_8, description);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // return true if insertion succeeded
    }

    public Cursor getAllCourses() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
