package com.grouprace.core.system.ui;

import android.view.View;
import android.widget.TextView;

import com.grouprace.core.system.R;

import java.util.Locale;

public class TodayStatsHelper {

    public static void bind(View statsRow, int activities, int totalDurationSeconds, float distanceKm) {
        if (statsRow == null) return;

        TextView tvActivities = statsRow.findViewById(R.id.tv_stat_activities);
        TextView tvTime = statsRow.findViewById(R.id.tv_stat_time);
        TextView tvDistance = statsRow.findViewById(R.id.tv_stat_distance);

        if (tvActivities != null) {
            tvActivities.setText(String.valueOf(activities));
        }

        if (tvTime != null) {
            int minutes = totalDurationSeconds / 60;
            tvTime.setText(minutes + "m");
        }

        if (tvDistance != null) {
            tvDistance.setText(String.format(Locale.getDefault(), "%.1fkm", distanceKm));
        }
    }
}
