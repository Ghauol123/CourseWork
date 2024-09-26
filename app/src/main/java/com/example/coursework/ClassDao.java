package com.example.coursework;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    long insert(Class classItem);

    @Query("SELECT * FROM classes WHERE courseId = :courseId")
    List<Class> getClassesForCourse(int courseId);
}