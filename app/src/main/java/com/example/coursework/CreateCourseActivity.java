package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateCourseActivity extends AppCompatActivity {
    Button buttonPickDay, buttonPickTime, buttonSaveCourse;
    EditText editTextCapacity, editTextDuration, editTextPrice, editTextDescription;
    Calendar selectedDate = Calendar.getInstance();
    Calendar selectedTime = Calendar.getInstance();
    Spinner spinnerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        DBHelper dbHelper = new DBHelper(this);

        buttonPickDay = findViewById(R.id.button_pick_day);
        buttonPickTime = findViewById(R.id.button_pick_time);
        editTextCapacity = findViewById(R.id.editText_capacity);
        editTextDuration = findViewById(R.id.editText_duration);
        editTextPrice = findViewById(R.id.editText_price);
        editTextDescription = findViewById(R.id.editText_description);
        buttonSaveCourse = findViewById(R.id.button_save_course);
        spinnerType = findViewById(R.id.spinner_type);

        // Sử dụng ArrayAdapter với mảng chuỗi từ resources
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.yoga_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        buttonPickDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        buttonPickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        buttonSaveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    saveCourse();
                }
            }
        });
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

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);
                        updateTimeButtonText();
                    }
                },
                selectedTime.get(Calendar.HOUR_OF_DAY),
                selectedTime.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
        String dateString = sdf.format(selectedDate.getTime());
        buttonPickDay.setText(dateString);
    }

    private void updateTimeButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeString = sdf.format(selectedTime.getTime());
        buttonPickTime.setText(timeString);
    }

    private boolean validateInputs() {
        if (buttonPickDay.getText().toString().equals("Chọn ngày") ||
                buttonPickTime.getText().toString().equals("Chọn giờ") ||
                TextUtils.isEmpty(editTextCapacity.getText()) ||
                TextUtils.isEmpty(editTextDuration.getText()) ||
                TextUtils.isEmpty(editTextPrice.getText()) ||
                TextUtils.isEmpty(editTextDescription.getText())) {

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

    private void saveCourse() {
        String day = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(selectedDate.getTime());
        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.getTime());
        int capacity = Integer.parseInt(editTextCapacity.getText().toString());
        int duration = Integer.parseInt(editTextDuration.getText().toString());
        String price = editTextPrice.getText().toString();
        String type = spinnerType.getSelectedItem().toString();
        String description = editTextDescription.getText().toString();

        final YogaCourse newCourse = new YogaCourse(day, time, capacity, duration, price, type, description);

        new Thread(new Runnable() {
            @Override
            public void run() {
                long result = AppDatabase.getDatabase(CreateCourseActivity.this).yogaCourseDao().insert(newCourse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != -1) {
                            Toast.makeText(CreateCourseActivity.this, "Đã lưu khóa học", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            showAlertDialog("Lỗi", "Không thể lưu khóa học");
                        }
                    }
                });
            }
        }).start();
    }
}