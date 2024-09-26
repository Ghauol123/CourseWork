//package com.example.coursework;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
//
//    private List<Class> classes;
//
//    public ClassAdapter(List<Class> classes) {
//        this.classes = classes;
//    }
//
//    @NonNull
//    @Override
//    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_item, parent, false);
//        return new ClassViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
//        Class classItem = classes.get(position);
//        holder.textViewDate.setText("Date: " + classItem.date);
//        holder.textViewTeacher.setText("Teacher: " + classItem.teacher);
//        holder.textViewComments.setText("Comments: " + classItem.comments);
//    }
//
//    @Override
//    public int getItemCount() {
//        return classes.size();
//    }
//
//    static class ClassViewHolder extends RecyclerView.ViewHolder {
//        TextView textViewDate, textViewTeacher, textViewComments;
//
//        public ClassViewHolder(View itemView) {
//            super(itemView);
//            textViewDate = itemView.findViewById(R.id.textViewDate);
//            textViewTeacher = itemView.findViewById(R.id.textViewTeacher);
//            textViewComments = itemView.findViewById(R.id.textViewComments);
//        }
//    }
//}
package com.example.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<Class> classList;

    public ClassAdapter(List<Class> classList) {
        this.classList = classList;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        Class classItem = classList.get(position);
        holder.textViewClassInfo.setText(classItem.date + " - " + classItem.teacher);
        holder.textViewComments.setText(classItem.comments);
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewClassInfo;
        public TextView textViewComments;

        public ClassViewHolder(View itemView) {
            super(itemView);
            textViewClassInfo = itemView.findViewById(R.id.textViewTeacher);
            textViewComments = itemView.findViewById(R.id.textViewDate);
        }
    }
}

