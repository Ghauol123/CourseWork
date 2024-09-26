package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditCourseActivity extends AppCompatActivity {
    Button buttonPickDay, buttonPickTime, buttonUpdateCourse;
    EditText editTextCapacity, editTextDuration, editTextPrice, editTextDescription;
    Calendar selectedDate = Calendar.getInstance();
    Calendar selectedTime = Calendar.getInstance();
    Spinner spinnerType;
    YogaCourse course;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        buttonPickDay = findViewById(R.id.button_pick_day);
        buttonPickTime = findViewById(R.id.button_pick_time);
        editTextCapacity = findViewById(R.id.editText_capacity);
        editTextDuration = findViewById(R.id.editText_duration);
        editTextPrice = findViewById(R.id.editText_price);
        editTextDescription = findViewById(R.id.editText_description);
        buttonUpdateCourse = findViewById(R.id.button_update_course);
        spinnerType = findViewById(R.id.spinner_type);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.yoga_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        course = (YogaCourse) getIntent().getSerializableExtra("course");
        if (course != null) {
            populateFields();
        }

        buttonPickDay.setOnClickListener(v -> showDatePicker());
        buttonPickTime.setOnClickListener(v -> showTimePicker());
        buttonUpdateCourse.setOnClickListener(v -> {
            if (validateInputs()) {
                updateCourse();
            }
        });
    }

    private void populateFields() {
        // Chuyển đổi tên thứ thành ngày trong tuần
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
            Date date = sdf.parse(course.day);
            selectedDate.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        updateDateButtonText();
        buttonPickTime.setText(course.time);
        editTextCapacity.setText(String.valueOf(course.capacity));
        editTextDuration.setText(String.valueOf(course.duration));
        editTextPrice.setText(course.price);
        editTextDescription.setText(course.description);
        spinnerType.setSelection(((ArrayAdapter<String>)spinnerType.getAdapter()).getPosition(course.type));
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = sdf.format(selectedDate.getTime());
        buttonPickDay.setText(dayOfWeek);
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

    private void updateCourse() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = sdf.format(selectedDate.getTime());

        course.day = dayOfWeek;
        course.time = buttonPickTime.getText().toString();
        course.capacity = Integer.parseInt(editTextCapacity.getText().toString());
        course.duration = Integer.parseInt(editTextDuration.getText().toString());
        course.price = editTextPrice.getText().toString();
        course.type = spinnerType.getSelectedItem().toString();
        course.description = editTextDescription.getText().toString();

        new Thread(() -> {
            int result = AppDatabase.getDatabase(EditCourseActivity.this).yogaCourseDao().update(course);
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(EditCourseActivity.this, "Đã cập nhật khóa học", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showAlertDialog("Lỗi", "Không thể cập nhật khóa học");
                }
            });
        }).start();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}