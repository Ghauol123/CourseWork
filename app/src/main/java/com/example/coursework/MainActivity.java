package com.example.coursework;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.widget.AutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

public class MainActivity extends AppCompatActivity {
    AppDatabase db;
    RecyclerView recyclerView;
    Button buttonCreateCourse;
    Button buttonUpdateFirebase;
    AutoCompleteTextView autoCompleteTextViewYogaType;
    FloatingActionButton fabAddCourse;
    YogaCourseAdapter adapter;
    private boolean databaseChanged = false;
    private static final int CREATE_COURSE_REQUEST = 1;
    private static final int EDIT_COURSE_REQUEST = 2;
    private static final String CHANNEL_ID = "DatabaseChangesChannel";
    private static final int NOTIFICATION_ID = 1;
    private FirebaseDatabase database;
    private DatabaseReference coursesRef;
    // Thêm biến để lưu trữ type hiện tại
    private String currentYogaType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = AppDatabase.getDatabase(this);

        recyclerView = findViewById(R.id.recyclerViewCourses);
//        buttonCreateCourse = findViewById(R.id.button_create_course);
        buttonUpdateFirebase = findViewById(R.id.button_update_firebase);
        autoCompleteTextViewYogaType = findViewById(R.id.spinnerYogaType);
        fabAddCourse = findViewById(R.id.fab_add_course);

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
        buttonUpdateFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Update Firebase button clicked");
                updateFirebaseWithData();
            }
        });

        fabAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateCourseActivity.class);
                startActivityForResult(intent, CREATE_COURSE_REQUEST);
            }
        });

        setupYogaTypeAutoComplete();
        loadCourses();

        // Lấy Firebase instances từ HomeActivity
        database = HomeActivity.getDatabase();
        coursesRef = HomeActivity.getCoursesRef();

        // Thêm đoạn code này sau khi setupYogaTypeAutoComplete()
        // Kiểm tra xem có yoga type được truyền từ HomeActivity không
        String selectedYogaType = getIntent().getStringExtra("selected_yoga_type");
        if (selectedYogaType != null && !selectedYogaType.isEmpty()) {
            currentYogaType = selectedYogaType; // Lưu type hiện tại
            autoCompleteTextViewYogaType.setText(selectedYogaType, false);
            loadCoursesByType(selectedYogaType);
        } else {
            currentYogaType = null;
            loadCourses();
        }
    }

    private void setupYogaTypeAutoComplete() {
        String[] yogaTypes = getResources().getStringArray(R.array.yoga_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, yogaTypes);
        autoCompleteTextViewYogaType.setAdapter(adapter);

        autoCompleteTextViewYogaType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                if (!selectedType.equals("All")) {
                    currentYogaType = selectedType; // Cập nhật type hiện tại
                    loadCoursesByType(selectedType);
                } else {
                    currentYogaType = null; // Reset type khi chọn All
                    loadCourses();
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(YogaCourse course) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_course_title))
            .setMessage(getString(R.string.delete_course_message))
            .setIcon(R.drawable.ic_warning)
            .setBackground(ContextCompat.getDrawable(this, R.drawable.alert_dialog_background))
            .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                deleteCourse(course);
            })
            .setNegativeButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.delete_red));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        });
        
        dialog.show();
    }

    private void deleteCourse(YogaCourse course) {
        new Thread(() -> {
            int result = db.yogaCourseDao().delete(course);
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(MainActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    databaseChanged = true;
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
        if ((requestCode == CREATE_COURSE_REQUEST || requestCode == EDIT_COURSE_REQUEST) 
            && resultCode == RESULT_OK) {
            // Kiểm tra type hiện tại để load đúng danh sách
            if (currentYogaType != null && !currentYogaType.isEmpty()) {
                loadCoursesByType(currentYogaType);
            } else {
                loadCourses();
            }
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

    private void loadCoursesByType(String type) {
        Log.d("MainActivity", "Loading courses for type: " + type);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<YogaCourse> courses = db.yogaCourseDao().getCoursesByType(type);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateCourses(courses);
                        if (courses.isEmpty()) {
                            Toast.makeText(MainActivity.this, 
                                "No courses found for " + type, 
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra type hiện tại để load đúng danh sách
        if (currentYogaType != null && !currentYogaType.isEmpty()) {
            loadCoursesByType(currentYogaType);
        } else {
            loadCourses();
        }
    }

    private void updateFirebaseWithData() {
        // Kiểm tra kết nối trước khi thực hiện cập nhật
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet connection. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<YogaCourse> courses = db.yogaCourseDao().getAllCourses();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateCoursesInFirebase(courses);
                    }
                });
            }
        }).start();
    }

    private void updateCoursesInFirebase(final List<YogaCourse> localCourses) {
        coursesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    for (YogaCourse course : localCourses) {
                        final DatabaseReference courseRef = coursesRef.child(String.valueOf(course.getId()));
                        courseRef.setValue(course).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("MainActivity", "Course updated successfully: " + course.getId());
                                    // Sau khi cập nhật khóa học, cập nhật các lớp của khóa học đó
                                    updateClassesForCourse(course.getId(), courseRef);
                                } else {
                                    Log.e("MainActivity", "Failed to update course: " + course.getId(), task.getException());
                                }
                            }
                        });
                    }
                    Toast.makeText(MainActivity.this, "Courses synced with Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "Failed to clear Firebase data", task.getException());
                    Toast.makeText(MainActivity.this, "Failed to sync courses with Firebase. Please check your connection and try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateClassesForCourse(final int courseId, final DatabaseReference courseRef) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Class> classes = db.classDao().getClassesForCourse(courseId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference classesRef = courseRef.child("classes");
                        for (Class classItem : classes) {
                            DatabaseReference classRef = classesRef.child(String.valueOf(classItem.GetId()));
                            classRef.setValue(classItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("MainActivity", "Class updated successfully: " + classItem.GetId());
                                    } else {
                                        Log.e("MainActivity", "Failed to update class: " + classItem.GetId(), task.getException());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    // Thêm phương thức kiểm tra kết nối mạng
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không cần đóng kết nối Firebase ở đây nữa vì được quản lý bởi HomeActivity
    }
}
