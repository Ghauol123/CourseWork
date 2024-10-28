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
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateClassActivity extends AppCompatActivity {

    private long courseId;

    private Button editTextDate;
    private EditText editTextTeacher;
    private EditText editTextComments;
    private Button buttonCreateClass;
    Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Initialize views
        editTextDate = findViewById(R.id.button_pick_day);
        editTextTeacher = findViewById(R.id.editText_teacherName);
        editTextComments = findViewById(R.id.editText_className);
        buttonCreateClass = findViewById(R.id.button_save_class);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
        buttonCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveCourse();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.alert_dialog_background))
                .setPositiveButton(getString(R.string.dialog_button_ok), (dialog, which) -> {
                    dialog.dismiss();
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.primary_color));
        });

        dialog.show();
    }
    private void saveCourse() {
        int courseId = getIntent().getIntExtra("course_id", 1);
        String day = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(selectedDate.getTime());
        String teacher = editTextTeacher.getText().toString();
        String comments = editTextComments.getText().toString();
        final Class newClass = new Class(courseId, day, teacher, comments);

        new Thread(new Runnable() {
            @Override
            public void run() {
                long result = AppDatabase.getDatabase(CreateClassActivity.this).classDao().insert(newClass);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != -1) {
                            Toast.makeText(CreateClassActivity.this, "Đã lưu lớp học", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            showAlertDialog("Lỗi", "Không thể lưu lớp học");
                        }
                    }
                });
            }
        }).start();
    }
}
