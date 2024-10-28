package com.example.coursework;

import android.os.Bundle;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import android.util.Log;

public class HomeActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private CourseTypeAdapter adapter;
    private List<CourseType> courseTypes;
    // Thêm biến Firebase
    private static FirebaseDatabase database;
    private static DatabaseReference coursesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo Firebase ngay từ đầu
        initializeFirebase();

        recyclerView = findViewById(R.id.recyclerViewCourseTypes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initializeCourseTypes();
        
        adapter = new CourseTypeAdapter(this, courseTypes, this);
        adapter.setOnItemClickListener(new CourseTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CourseType courseType) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                // Thay đổi flag để cho phép quay lại HomeActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // Truyền yoga type qua intent
                intent.putExtra("selected_yoga_type", courseType.getName());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initializeFirebase() {
        try {
            if (database == null) {
                database = FirebaseDatabase.getInstance("https://yoga-course1-default-rtdb.asia-southeast1.firebasedatabase.app/");
                database.setPersistenceEnabled(true);
            }
            if (coursesRef == null) {
                coursesRef = database.getReference("Courses");
                coursesRef.keepSynced(true);
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "Error initializing Firebase: " + e.getMessage());
        }
    }

    // Thêm getter cho Firebase instances
    public static FirebaseDatabase getDatabase() {
        return database;
    }

    public static DatabaseReference getCoursesRef() {
        return coursesRef;
    }

    private void initializeCourseTypes() {
        courseTypes = new ArrayList<>();
        
        // Lấy mảng yoga_types từ resources
        String[] yogaTypes = getResources().getStringArray(R.array.yoga_types);
        
        // Thêm các loại yoga vào danh sách (bỏ qua "All" ở vị trí đầu tiên)
        for (int i = 1; i < yogaTypes.length; i++) {
            String name = yogaTypes[i];
            String description = getYogaDescription(name);
            int imageResource = getYogaImage(name);
            courseTypes.add(new CourseType(
                    name,
                    description,
                    imageResource
            ));
        }
    }

    private String getYogaDescription(String yogaType) {
        // Mô tả cho từng loại yoga
        switch (yogaType) {
            case "Hatha Yoga":
                return "A gentle, basic form of yoga that promotes physical and mental well-being";
            case "Vinyasa Yoga":
                return "A dynamic practice that connects breath with movement";
            case "Ashtanga Yoga":
                return "A rigorous style of yoga following a specific sequence of postures";
            case "Yin Yoga":
                return "A slow-paced style holding poses for longer periods";
            case "Restorative Yoga":
                return "A relaxing practice using props to support the body";
            default:
                return "Explore this unique style of yoga";
        }
    }
    private int getYogaImage(String yogaType) {
        switch (yogaType) {
            case "Hatha Yoga":
                return R.drawable.yoga_hatha; // Đổi tên file theo quy tắc
            case "Vinyasa Yoga":
                return R.drawable.yoga_vinyasa;
            case "Ashtanga Yoga":
                return R.drawable.yoga_ashtanga;
            case "Yin Yoga":
                return R.drawable.yoga_yin;
            case "Restorative Yoga":
                return R.drawable.yoga_restorative;
            default:
                return R.drawable.logo_yoga;
        }
    }
}
