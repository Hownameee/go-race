package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.ClubEvent;
import com.grouprace.feature.club.R;

public class ClubEventAdapter extends ListAdapter<ClubEvent, ClubEventAdapter.EventViewHolder> {

    public ClubEventAdapter() {
        super(new DiffUtil.ItemCallback<ClubEvent>() {
            @Override
            public boolean areItemsTheSame(@NonNull ClubEvent oldItem, @NonNull ClubEvent newItem) {
                return oldItem.getEventId().equals(newItem.getEventId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ClubEvent oldItem, @NonNull ClubEvent newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }
        });
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        ClubEvent event = getItem(position);
        holder.bind(event);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvTime, tvDesc;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.text_event_title);
            tvTime = itemView.findViewById(R.id.text_event_time);
            tvDesc = itemView.findViewById(R.id.text_event_description);
        }

        public void bind(ClubEvent event) {
            tvTitle.setText(event.getTitle());
            // Just displaying raw strings for mock
            tvTime.setText(event.getStartTime() + " - " + event.getEndTime());
            tvDesc.setText(event.getDescription());
        }
    }
}
