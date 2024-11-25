package com.example.coursework;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseTypeAdapter extends RecyclerView.Adapter<CourseTypeAdapter.CourseTypeViewHolder> {
    private static List<CourseType> courseTypes;
    private static OnItemClickListener listener;

    public CourseTypeAdapter(List<CourseType> courseTypes) {
        this.courseTypes = courseTypes;
    }

    public interface OnItemClickListener {
        void onItemClick(CourseType courseType);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_type, parent, false);
        return new CourseTypeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseTypeViewHolder holder, int position) {
        CourseType courseType = courseTypes.get(position);
        holder.textYogaType.setText(courseType.getName());
        holder.textYogaDescription.setText(courseType.getDescription());
        holder.imageYogaType.setImageResource(courseType.getImageResourceId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(courseType);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseTypes.size();
    }

    static class CourseTypeViewHolder extends RecyclerView.ViewHolder {
        ImageView imageYogaType;
        TextView textYogaType;
        TextView textYogaDescription;

        public CourseTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageYogaType = itemView.findViewById(R.id.imageYogaType);
            textYogaType = itemView.findViewById(R.id.textYogaType);
            textYogaDescription = itemView.findViewById(R.id.textYogaDescription);

//            // Thêm click listener cho itemView
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
//                        listener.onItemClick(courseTypes.get(getAdapterPosition()));
//                    }
//                }
//            });
        }
    }
}
