package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.Record;
import com.grouprace.feature.club.R;

public class ClubActivityAdapter extends ListAdapter<Record, ClubActivityAdapter.ActivityViewHolder> {

    public ClubActivityAdapter() {
        super(new DiffUtil.ItemCallback<Record>() {
            @Override
            public boolean areItemsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
                return oldItem.getRecordId() == newItem.getRecordId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Record oldItem, @NonNull Record newItem) {
                return oldItem.getDistance() == newItem.getDistance();
            }
        });
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Record record = getItem(position);
        holder.bind(record);
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDistance, tvPace, tvDuration;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_activity_title);
            tvDistance = itemView.findViewById(R.id.text_activity_distance);
            tvPace = itemView.findViewById(R.id.text_activity_pace);
            tvDuration = itemView.findViewById(R.id.text_activity_duration);
        }

        public void bind(Record record) {
            tvTitle.setText(record.getTitle() != null ? record.getTitle() : "Workout");
            tvDistance.setText(String.format("%.2f km", record.getDistance()));
            tvPace.setText(String.format("%.1f km/h", record.getSpeed()));
            int seconds = record.getDuration();
            tvDuration.setText(String.format("%02d:%02d", seconds/60, seconds%60));
        }
    }
}
