package com.grouprace.feature.profile.ui.main.components;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.feature.profile.R;

import android.view.View;

public class ProfileStreakComponent {
    private final TextView status;
    private final ImageView fireIcon;
    private final TextView currentStreak;
    private final TextView longestStreak;
    private final TextView totalActiveDays;

    public ProfileStreakComponent(View root) {
        status = root.findViewById(R.id.profile_streak_status);
        fireIcon = root.findViewById(R.id.profile_streak_fire_icon);
        currentStreak = root.findViewById(R.id.profile_current_streak);
        longestStreak = root.findViewById(R.id.profile_longest_streak);
        totalActiveDays = root.findViewById(R.id.profile_total_active_days);
    }

    public void bind(@Nullable RecordStreakResponse streak, boolean self) {
        if (streak == null) {
            return;
        }

        currentStreak.setText(String.valueOf(streak.getCurrentStreakDays()));
        longestStreak.setText(String.valueOf(streak.getLongestStreakDays()));
        totalActiveDays.setText(String.valueOf(streak.getTotalActiveDays()));
        fireIcon.setImageResource(streak.isTodayHasRecord()
                ? R.drawable.ic_streak_fire_active
                : R.drawable.ic_streak_fire_inactive);

        if (streak.getCurrentStreakDays() <= 0) {
            status.setText("No active streak right now.");
        } else if (streak.isTodayHasRecord()) {
            status.setText(self ? "Great job. Your streak is active today." : "This athlete has an active streak today.");
        } else {
            status.setText(self ? "You still have time to extend your streak today." : "Their streak is still alive if they record today.");
        }
    }
}
