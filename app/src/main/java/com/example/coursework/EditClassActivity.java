package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        db = AppDatabase.getDatabase(this);
        classId = getIntent().getIntExtra("class_id", -1);

        editTextDate = findViewById(R.id.button_pick_day);
        editTextTeacher = findViewById(R.id.editText_teacherName);
        editTextComments = findViewById(R.id.editText_className);
        buttonUpdateClass = findViewById(R.id.button_update_class);

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
    private void updateClass() {
        String day = editTextDate.getText().toString();
        String teacher = editTextTeacher.getText().toString();
        String comments = editTextComments.getText().toString();

        new Thread(() -> {
            try {
                Class updatedClass = db.classDao().getClassById(classId);
                if (updatedClass != null) {
                    updatedClass.date = day;
                    updatedClass.teacher = teacher;
                    updatedClass.comments = comments;

                    int result = db.classDao().update(updatedClass);
                    runOnUiThread(() -> {
                        if (result > 0) {
                            showSuccessDialog();
                        } else {
                            showAlertDialog("Lỗi", "Không thể cập nhật lớp học");
                        }
                    });
                } else {
                    runOnUiThread(() -> {
                        showAlertDialog("Lỗi", "Không tìm thấy lớp học");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    showAlertDialog("Lỗi", "Đã xảy ra lỗi khi cập nhật: " + e.getMessage());
                });
            }
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

        textViewTitle.setText("Thành công");
        textViewMessage.setText("Đã cập nhật lớp học thành công");

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.success_animation);
        imageViewSuccess.startAnimation(animation);

        buttonOk.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    }
