package com.example.coursework;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    long insert(Class classItem);

    @Update
    void update(Class classItem);

    @Delete
    void delete(Class classItem);

    @Query("SELECT * FROM classes WHERE id = :classId")
    Class getClassById(int classId);

    @Query("SELECT * FROM classes WHERE courseId = :courseId")
    List<Class> getClassesForCourse(int courseId);
}