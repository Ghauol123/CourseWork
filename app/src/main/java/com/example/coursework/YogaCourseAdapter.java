package com.example.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class YogaCourseAdapter extends RecyclerView.Adapter<YogaCourseAdapter.CourseViewHolder> {

    private List<YogaCourse> courses;

    public YogaCourseAdapter(List<YogaCourse> courses) {
        this.courses = courses;
    }

    public interface OnItemClickListener {
        void onItemClick(YogaCourse course);
        void onEditClick(YogaCourse course);
        void onDeleteClick(YogaCourse course);
        void onClassClick(YogaCourse course);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        YogaCourse course = courses.get(position);
        holder.textViewDay.setText("Day: " + course.day);
        holder.textViewTime.setText("Time: " + course.time);
        holder.textViewType.setText("Type: " + course.type);

        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(course);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(course);
            }
        });

        holder.buttonClass.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClassClick(course);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void updateCourses(List<YogaCourse> newCourses) {
        courses = newCourses;
        notifyDataSetChanged();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDay, textViewTime, textViewType;
        Button buttonEdit, buttonDelete, buttonClass;

        public CourseViewHolder(View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewType = itemView.findViewById(R.id.textViewType);
            buttonEdit = itemView.findViewById(R.id.button_edit);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonClass = itemView.findViewById(R.id.button_class);
        }
    }
}