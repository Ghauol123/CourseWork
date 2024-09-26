package com.example.coursework;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {YogaCourse.class, Class.class}, version = 1) // Cập nhật phiên bản
public abstract class AppDatabase extends RoomDatabase {
    public abstract YogaCourseDao yogaCourseDao();
    public abstract ClassDao classDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "yoga_course_db")
                            // Điều này sẽ tạo lại cơ sở dữ liệu và ngăn ngừa lỗi do thay đổi schema
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
