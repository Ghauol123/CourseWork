package com.example.coursework;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface YogaCourseDao {
    @Insert
    long insert(YogaCourse course);

    @Query("SELECT * FROM courses")
    List<YogaCourse> getAllCourses();

    @Update
    int update(YogaCourse course);

    @Delete
    int delete(YogaCourse course);
}