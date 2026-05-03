package com.grouprace.feature.profile.ui.main.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.feature.profile.R;
import com.grouprace.feature.profile.ui.achievements.AchievementHelper;
import com.grouprace.feature.profile.ui.achievements.AchievementItem;

import java.util.List;

public class ProfileAchievementsPreviewComponent {
    private final TextView count;
    private final LinearLayout container;

    public ProfileAchievementsPreviewComponent(View root) {
        count = root.findViewById(R.id.profile_achievements_count);
        container = root.findViewById(R.id.profile_achievements_preview_container);
    }

    public void bind(@Nullable RecordProfileStatisticsResponse response) {
        int totalActivities = 0;
        if (response != null && response.getAllTime() != null) {
            totalActivities = (int) Math.floor(response.getAllTime().getTotalActivities());
        }

        List<AchievementItem> allAchievements = AchievementHelper.buildAchievements(totalActivities);
        List<AchievementItem> previewAchievements = AchievementHelper.buildPreviewAchievements(totalActivities);
        count.setText(AchievementHelper.countUnlocked(allAchievements) + " / " + AchievementHelper.MILESTONES.length);
        populate(previewAchievements);
    }

    private void populate(List<AchievementItem> achievements) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(container.getContext());

        for (AchievementItem item : achievements) {
            View itemView = inflater.inflate(R.layout.item_profile_achievement, container, false);
            itemView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            View badgeContainer = itemView.findViewById(R.id.achievement_badge_container);
            TextView milestoneValue = itemView.findViewById(R.id.achievement_milestone_value);
            TextView milestoneLabel = itemView.findViewById(R.id.achievement_milestone_label);

            badgeContainer.setBackgroundResource(item.isUnlocked()
                    ? R.drawable.bg_achievement_hexagon_unlocked
                    : R.drawable.bg_achievement_hexagon_locked);
            milestoneValue.setText(String.valueOf(item.getMilestone()));
            milestoneLabel.setText(AchievementHelper.formatMilestoneLabel(item.getMilestone()));
            milestoneValue.setAlpha(item.isUnlocked() ? 1f : 0.55f);
            milestoneLabel.setAlpha(item.isUnlocked() ? 1f : 0.55f);

            container.addView(itemView);
        }
    }
}
