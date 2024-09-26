package com.example.coursework;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "courses")
public class YogaCourse implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String day;
    public String time;
    public int capacity;
    public int duration;
    public String price;
    public String type;
    public String description;

    public YogaCourse(String day, String time, int capacity, int duration, String price, String type, String description) {
        this.day = day;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
    }
    public int getId() {
        return id;
    }
}