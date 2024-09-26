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

    @Query("SELECT * FROM classes WHERE courseId = :courseId AND teacher LIKE :searchQuery")
    List<Class> searchClassesForCourse(int courseId, String searchQuery);

    @Query("SELECT * FROM classes WHERE courseId = :courseId AND date LIKE :dayOfWeek")
    List<Class> searchClassesByDayOfWeek(int courseId, String dayOfWeek);

    @Query("SELECT * FROM classes WHERE courseId = :courseId AND date = :date")
    List<Class> searchClassesByDate(int courseId, String date);
}