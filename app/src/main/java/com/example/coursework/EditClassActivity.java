package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditClassActivity extends AppCompatActivity {

    private int classId;
    private Button editTextDate;
    private EditText editTextTeacher;
    private EditText editTextComments;
    private Button buttonUpdateClass;
    Calendar selectedDate = Calendar.getInstance();
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        db = AppDatabase.getDatabase(this);
        classId = getIntent().getIntExtra("class_id", -1);

        editTextDate = findViewById(R.id.button_pick_day);
        editTextTeacher = findViewById(R.id.editTextTeacher);
        editTextComments = findViewById(R.id.editTextComments);
        buttonUpdateClass = findViewById(R.id.buttonUpdateClass);

        loadClassData();

        editTextDate.setOnClickListener(v -> showDatePicker());
        buttonUpdateClass.setOnClickListener(v -> {
            if (validateInputs()) {
                updateClass();
            }
        });
    }

    private void loadClassData() {
        new Thread(() -> {
            Class classItem = db.classDao().getClassById(classId);
            runOnUiThread(() -> {
                if (classItem != null) {
                    editTextDate.setText(classItem.date);
                    editTextTeacher.setText(classItem.teacher);
                    editTextComments.setText(classItem.comments);
                }
            });
        }).start();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate.set(Calendar.YEAR, year);
                        selectedDate.set(Calendar.MONTH, month);
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateButtonText();
                    }
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
        String dateString = sdf.format(selectedDate.getTime());
        editTextDate.setText(dateString);
    }
    private boolean validateInputs() {
        if (editTextDate.getText().toString().equals("Chọn ngày") ||
                TextUtils.isEmpty(editTextTeacher.getText())){

            showAlertDialog("Thông báo", "Vui lòng điền đầy đủ tất cả các trường");
            return false;
        }
        return true;
    }
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    private void updateClass() {
        String day = editTextDate.getText().toString();
        String teacher = editTextTeacher.getText().toString();
        String comments = editTextComments.getText().toString();

        new Thread(() -> {
            Class updatedClass = db.classDao().getClassById(classId);
            updatedClass.date = day;
            updatedClass.teacher = teacher;
            updatedClass.comments = comments;

            db.classDao().update(updatedClass);

            runOnUiThread(() -> {
                Toast.makeText(EditClassActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
