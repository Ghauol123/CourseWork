package com.example.coursework;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    RecyclerView recyclerView;
    Button buttonCreateCourse;
    YogaCourseAdapter adapter;

    private static final int CREATE_COURSE_REQUEST = 1;
    private static final int EDIT_COURSE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this);
        recyclerView = findViewById(R.id.recyclerViewCourses);
        buttonCreateCourse = findViewById(R.id.button_create_course);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new YogaCourseAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new YogaCourseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(YogaCourse course) {
                // Handle item click if needed
            }

            @Override
            public void onEditClick(YogaCourse course) {
                Intent intent = new Intent(MainActivity.this, EditCourseActivity.class);
                intent.putExtra("course", course);
                startActivityForResult(intent, EDIT_COURSE_REQUEST);
            }

            @Override
            public void onDeleteClick(YogaCourse course) {
                showDeleteConfirmationDialog(course);
            }

            @Override
            public void onClassClick(YogaCourse course) {
                Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                intent.putExtra("course", course);
                startActivity(intent);
            }
        });

        buttonCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateCourseActivity.class);
                startActivityForResult(intent, CREATE_COURSE_REQUEST);
            }
        });
        loadCourses();
    }

    private void showDeleteConfirmationDialog(YogaCourse course) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Yes", (dialog, which) -> deleteCourse(course))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteCourse(YogaCourse course) {
        new Thread(() -> {
            int result = db.yogaCourseDao().delete(course);
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(MainActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    loadCourses();
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete course", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_COURSE_REQUEST && resultCode == RESULT_OK) {
            loadCourses();
        } else if (requestCode == EDIT_COURSE_REQUEST && resultCode == RESULT_OK) {
            loadCourses();
        }
    }

    private void loadCourses() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<YogaCourse> courses = db.yogaCourseDao().getAllCourses();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateCourses(courses);
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }
}
