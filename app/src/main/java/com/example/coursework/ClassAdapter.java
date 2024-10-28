package com.example.coursework;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private List<Class> classList;
    private OnClassClickListener listener;
    private OnClassDeleteListener deleteListener;

    public ClassAdapter(List<Class> classList, OnClassClickListener listener, OnClassDeleteListener deleteListener) {
        this.classList = classList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    public interface OnClassClickListener {
        void onClassClick(Class classItem);
    }

    public interface OnClassDeleteListener {
        void onClassDelete(Class classItem);
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
        holder.textViewDate.setText(classItem.date);
        holder.textViewTeacher.setText(classItem.teacher);
        holder.textViewComments.setText(classItem.comments);

        holder.buttonEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClassClick(classItem);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onClassDelete(classItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewDate;
        public TextView textViewTeacher;
        public TextView textViewComments;
        public MaterialButton buttonEdit;
        public MaterialButton buttonDelete;

        public ClassViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTeacher = itemView.findViewById(R.id.textViewTeacher);
            textViewComments = itemView.findViewById(R.id.textViewComments);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
