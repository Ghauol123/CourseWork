package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassActivity extends AppCompatActivity implements ClassAdapter.OnClassClickListener, ClassAdapter.OnClassDeleteListener {
    private TextView textViewCourseInfo;
    private EditText editTextSearch;
    private EditText editTextDayOfWeek;
    private Button buttonAddClass;
    private Button buttonPickDate;
    private RecyclerView recyclerViewClasses;
    private YogaCourse course;
    private AppDatabase db;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        db = AppDatabase.getDatabase(this);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewCourseInfo = findViewById(R.id.textViewCourseInfo);
        editTextSearch = findViewById(R.id.editTextSearch);
        editTextDayOfWeek = findViewById(R.id.editTextDayOfWeek);
//        buttonAddClass = findViewById(R.id.buttonAddClass);
        buttonPickDate = findViewById(R.id.buttonPickDate);
        recyclerViewClasses = findViewById(R.id.recyclerViewClasses);
        course = (YogaCourse) getIntent().getSerializableExtra("course");
        if (course != null) {
            updateCourseInfo();
            loadClassesForCourse(course.getId(), "", "", "");
        }
//        buttonAddClass.setOnClickListener(v -> {
//            Intent intent = new Intent(ClassActivity.this, CreateClassActivity.class);
//            intent.putExtra("course_id", course.getId());
//            startActivity(intent);
//        });
        recyclerViewClasses.setLayoutManager(new LinearLayoutManager(this));

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loadClassesForCourse(course.getId(), s.toString(), "", "");
            }
        });

        buttonPickDate.setOnClickListener(v -> showDatePicker());

        editTextDayOfWeek.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                loadClassesForCourse(course.getId(), "", s.toString(), "");
            }
        });

        FloatingActionButton fabAddClass = findViewById(R.id.fabAddClass);
        fabAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassActivity.this, CreateClassActivity.class);
                intent.putExtra("course_id", course.getId());
                startActivity(intent);
            }
        });
    }

    private void updateCourseInfo() {
        String courseInfo = String.format("%s - %s\n%s\nCapacity: %d",
                course.day, course.time, course.type, course.capacity);
        textViewCourseInfo.setText(courseInfo);
    }

    private void loadClassesForCourse(int courseId, String searchQuery, String dayOfWeek, String date) {
        new Thread(() -> {
            List<Class> classList;
            if (!searchQuery.isEmpty()) {
                classList = db.classDao().searchClassesForCourse(courseId, "%" + searchQuery + "%");
            } else if (!dayOfWeek.isEmpty()) {
                classList = db.classDao().searchClassesByDayOfWeek(courseId, "%" + dayOfWeek + "%");
            } else if (!date.isEmpty()) {
                classList = db.classDao().searchClassesByDate(courseId, date);
            } else {
                classList = db.classDao().getClassesForCourse(courseId);
            }
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
        loadClassesForCourse(course.getId(), editTextSearch.getText().toString(), editTextDayOfWeek.getText().toString(), "");
    }

    @Override
    public void onClassClick(Class classItem) {
        Intent intent = new Intent(ClassActivity.this, EditClassActivity.class);
        intent.putExtra("class_id", classItem.id);
        startActivity(intent);
    }

    @Override
    public void onClassDelete(Class classItem) {
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa lớp học này?")
                .setIcon(R.drawable.ic_warning)
                .setPositiveButton("Xóa", (dialog, which) -> deleteClass(classItem))
                .setNegativeButton("Hủy", null)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.alert_dialog_background))
                .show();
    }

    private void deleteClass(Class classItem) {
        new Thread(() -> {
            db.classDao().delete(classItem);
            runOnUiThread(() -> {
                Toast.makeText(ClassActivity.this, "Đã xóa lớp học", Toast.LENGTH_SHORT).show();
                loadClassesForCourse(course.getId(), "", "", "");
            });
        }).start();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String dateString = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(selectedDate.getTime());
                    loadClassesForCourse(course.getId(), "", "", dateString);
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // hoặc finish();
        return true;
    }
}
