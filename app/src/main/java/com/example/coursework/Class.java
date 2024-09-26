package com.example.coursework;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "classes",
        foreignKeys = @ForeignKey(entity = YogaCourse.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE))
public class Class implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int courseId;
    public String date;
    public String teacher;
    public String comments;

    public Class(int courseId, String date, String teacher, String comments) {
        this.courseId = courseId;
        this.date = date;
        this.teacher = teacher;
        this.comments = comments;
    }
}