package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.ClubEvent;
import com.grouprace.feature.club.R;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    private List<ClubEvent> items = new ArrayList<>();
    private final OnEventClickListener listener;

    public interface OnEventClickListener {
        void onJoinClick(ClubEvent event);
        void onEventClick(ClubEvent event);
    }

    public EventAdapter(OnEventClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<ClubEvent> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClubEvent event = items.get(position);
        holder.bind(event, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCalMonth;
        private final TextView tvCalDay;
        private final TextView tvCalWeekday;
        private final TextView tvTitle;
        private final TextView tvCategory;
        private final TextView tvParticipants;
        private final TextView tvStatus;
        private final android.widget.Button btnJoin;

        ViewHolder(View view) {
            super(view);
            tvCalMonth = view.findViewById(R.id.tv_cal_month);
            tvCalDay = view.findViewById(R.id.tv_cal_day);
            tvCalWeekday = view.findViewById(R.id.tv_cal_weekday);
            tvTitle = view.findViewById(R.id.tv_event_title);
            tvCategory = view.findViewById(R.id.tv_event_category);
            tvParticipants = view.findViewById(R.id.tv_event_participants);
            tvStatus = view.findViewById(R.id.tv_event_status);
            btnJoin = view.findViewById(R.id.btn_event_join);
        }

        void bind(ClubEvent event, OnEventClickListener listener) {
            tvTitle.setText(event.getTitle());
            
            // Format dates for calendar view
            try {
                java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                java.util.Date date = isoFormat.parse(event.getStartTime());
                
                if (date != null) {
                    java.text.SimpleDateFormat monthFormat = new java.text.SimpleDateFormat("MMM", java.util.Locale.US);
                    java.text.SimpleDateFormat dayFormat = new java.text.SimpleDateFormat("dd", java.util.Locale.US);
                    java.text.SimpleDateFormat weekdayFormat = new java.text.SimpleDateFormat("EEE", java.util.Locale.US);
                    
                    tvCalMonth.setText(monthFormat.format(date).toUpperCase());
                    tvCalDay.setText(dayFormat.format(date));
                    tvCalWeekday.setText(weekdayFormat.format(date));
                }
            } catch (Exception e) {
                tvCalMonth.setText("---");
                tvCalDay.setText("??");
                tvCalWeekday.setText("---");
            }

            tvCategory.setText(event.getTargetDistance() > 0 ? "Running" : "Social");
            int count = event.getParticipantsCount();
            tvParticipants.setText(count == 0 ? "No participants yet" : count + (count == 1 ? " participant" : " participants"));

            // Status and Join Button Logic
            boolean isPast = false;
            try {
                if (event.getEndTime() != null) {
                    java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                    isoFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    java.util.Date endDate = isoFormat.parse(event.getEndTime());
                    if (endDate != null) {
                        isPast = endDate.before(new java.util.Date());
                    }
                }
            } catch (Exception ignored) {}

            double progress = 0;
            if (event.getTargetDistance() > 0) {
                progress = (event.getGlobalDistance() / event.getTargetDistance()) * 100;
            }
            boolean isCompleted = progress >= 100;

            // Hide Join button if already joined OR past OR completed
            boolean shouldHideJoin = event.isJoined() || isPast || isCompleted;
            btnJoin.setVisibility(shouldHideJoin ? View.GONE : View.VISIBLE);
            
            android.util.Log.d("EventAdapter", "Event: " + event.getTitle() + 
                " | Joined: " + event.isJoined() + 
                " | Past: " + isPast + 
                " | Completed: " + isCompleted + " (" + progress + "%)" +
                " | Final Visibility: " + (shouldHideJoin ? "GONE" : "VISIBLE"));

            // Display status if past or completed
            if (isPast || isCompleted) {
                tvStatus.setVisibility(View.VISIBLE);
                if (isCompleted) {
                    tvStatus.setText("COMPLETED");
                    tvStatus.setTextColor(android.graphics.Color.parseColor("#00BFA5")); // Cyan/Teal
                    tvStatus.setBackgroundResource(R.drawable.bg_badge_public); // Use existing badge bg
                } else {
                    tvStatus.setText("FAILED");
                    tvStatus.setTextColor(android.graphics.Color.RED);
                    tvStatus.setBackgroundResource(R.drawable.bg_badge_private); // Use existing badge bg
                }
            } else {
                tvStatus.setVisibility(View.GONE);
            }

            btnJoin.setOnClickListener(v -> {
                if (listener != null) listener.onJoinClick(event);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onEventClick(event);
            });
        }
    }
}
