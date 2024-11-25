package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        buttonPickDay = findViewById(R.id.button_pick_day);
        buttonPickTime = findViewById(R.id.button_pick_time);
        editTextCapacity = findViewById(R.id.editText_capacity);
        editTextDuration = findViewById(R.id.editText_duration);
        editTextPrice = findViewById(R.id.editText_price);
        editTextDescription = findViewById(R.id.editText_description);
        buttonUpdateCourse = findViewById(R.id.button_update_course);
        spinnerType = findViewById(R.id.spinner_type);
        db = AppDatabase.getDatabase(this);
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
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
        String dayOfWeek = sdf.format(selectedDate.getTime());
        buttonPickDay.setText(dayOfWeek);
    }

    private void updateTimeButtonText() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeString = sdf.format(selectedTime.getTime());
        buttonPickTime.setText(timeString);
    }

    private boolean validateInputs() {
        if (buttonPickDay.getText().toString().equals("Pick Date") ||
                buttonPickTime.getText().toString().equals("Pick Time") ||
                TextUtils.isEmpty(editTextCapacity.getText()) ||
                TextUtils.isEmpty(editTextDuration.getText()) ||
                TextUtils.isEmpty(editTextPrice.getText()) ||
                TextUtils.isEmpty(editTextDescription.getText())) {

            showAlertDialog("Alert", "Please fill in all fields");
            return false;
        }
        return true;
    }

    private void updateCourse() {
        String day = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault()).format(selectedDate.getTime());

        course.day = day;
        course.time = buttonPickTime.getText().toString();
        course.capacity = Integer.parseInt(editTextCapacity.getText().toString());
        course.duration = Integer.parseInt(editTextDuration.getText().toString());
        course.price = editTextPrice.getText().toString();
        course.type = spinnerType.getSelectedItem().toString();
        course.description = editTextDescription.getText().toString();

        new Thread(() -> {
            int result = db.yogaCourseDao().update(course);
            runOnUiThread(() -> {
                if (result > 0) {
                    showSuccessDialog();
                } else {
                    showAlertDialog("Error", "Course could not be updated");
                }
            });
        }).start();
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 
                            WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(ContextCompat.getDrawable(this, 
                               R.drawable.alert_dialog_background));
        }

        ImageView imageViewSuccess = dialog.findViewById(R.id.imageView_success);
        TextView textViewTitle = dialog.findViewById(R.id.textView_title);
        TextView textViewMessage = dialog.findViewById(R.id.textView_message);
        Button buttonOk = dialog.findViewById(R.id.button_ok);

        textViewTitle.setText("Success");
        textViewMessage.setText("Course updated successfully");
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.success_animation);
        imageViewSuccess.startAnimation(animation);

        buttonOk.setOnClickListener(v -> {
            dialog.dismiss();
            setResult(RESULT_OK);
            finish();
        });

        dialog.show();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
