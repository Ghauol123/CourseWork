package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassActivity extends AppCompatActivity implements ClassAdapter.OnClassClickListener, ClassAdapter.OnClassDeleteListener {
    private TextView textViewCourseInfo;
    private Button buttonAddClass;
    private RecyclerView recyclerViewClasses;
    private YogaCourse course;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        db = AppDatabase.getDatabase(this);

        textViewCourseInfo = findViewById(R.id.textViewCourseInfo);
        buttonAddClass = findViewById(R.id.buttonAddClass);
        recyclerViewClasses = findViewById(R.id.recyclerViewClasses);
        course = (YogaCourse) getIntent().getSerializableExtra("course");
        if (course != null) {
            updateCourseInfo();
            loadClassesForCourse(course.getId());
        }
        buttonAddClass.setOnClickListener(v -> {
            Intent intent = new Intent(ClassActivity.this, CreateClassActivity.class);
            intent.putExtra("course_id", course.getId());
            startActivity(intent);
        });
        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));
        
//        // Add item decoration for better spacing
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(getDrawable(android.R.drawable.divider_horizontal_bright));
//        recyclerViewClasses.addItemDecoration(itemDecoration);
    }

    private void updateCourseInfo() {
        String courseInfo = String.format("%s - %s\n%s\nCapacity: %d",
                course.day, course.time, course.type, course.capacity);
        textViewCourseInfo.setText(courseInfo);
    }
    private void loadClassesForCourse(int courseId) {
        new Thread(() -> {
            List<Class> classList = db.classDao().getClassesForCourse(courseId);
            runOnUiThread(() -> {
                if (classList != null && !classList.isEmpty()) {
                    setupRecyclerView(classList);
                } else {
                    Toast.makeText(ClassActivity.this, "Không có lớp học nào", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();

    }
    private void setupRecyclerView(List<Class> classList) {
        ClassAdapter classAdapter = new ClassAdapter(classList, this, this);
        recyclerViewClasses.setAdapter(classAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClassesForCourse(course.getId());
    }

    @Override
    public void onClassClick(Class classItem) {
        Intent intent = new Intent(ClassActivity.this, EditClassActivity.class);
        intent.putExtra("class_id", classItem.id);
        startActivity(intent);
    }

    @Override
    public void onClassDelete(Class classItem) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lớp học này?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteClass(classItem))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteClass(Class classItem) {
        new Thread(() -> {
            db.classDao().delete(classItem);
            runOnUiThread(() -> {
                Toast.makeText(ClassActivity.this, "Đã xóa lớp học", Toast.LENGTH_SHORT).show();
                loadClassesForCourse(course.getId());
            });
        }).start();
    }
}