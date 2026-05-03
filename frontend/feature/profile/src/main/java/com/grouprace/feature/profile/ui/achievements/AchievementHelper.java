package com.grouprace.feature.profile.ui.achievements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AchievementHelper {
    public static final int[] MILESTONES = {
            1, 3, 5, 10, 20, 30, 40, 50, 75, 100, 150, 200, 250, 300, 400, 500, 600, 700, 800, 900, 1000
    };

    private AchievementHelper() {
    }

    public static List<AchievementItem> buildAchievements(int totalActivities) {
        List<AchievementItem> achievements = new ArrayList<>();
        for (int milestone : MILESTONES) {
            achievements.add(new AchievementItem(milestone, totalActivities >= milestone));
        }
        return achievements;
    }

    public static int countUnlocked(List<AchievementItem> achievements) {
        int count = 0;
        for (AchievementItem item : achievements) {
            if (item.isUnlocked()) {
                count++;
            }
        }
        return count;
    }

    public static List<AchievementItem> buildPreviewAchievements(int totalActivities) {
        List<AchievementItem> all = buildAchievements(totalActivities);
        List<AchievementItem> preview = new ArrayList<>();
        for (int index = all.size() - 1; index >= 0; index--) {
            AchievementItem item = all.get(index);
            if (item.isUnlocked()) {
                preview.add(item);
            }
            if (preview.size() == 3) {
                break;
            }
        }

        if (preview.isEmpty()) {
            for (int index = 0; index < Math.min(3, all.size()); index++) {
                preview.add(all.get(index));
            }
        }

        Collections.reverse(preview);
        return preview;
    }

    public static String formatMilestoneLabel(int milestone) {
        return milestone == 1 ? "1 activity" : milestone + " activities";
    }
}
