package com.grouprace.feature.profile.ui.achievements;

public class AchievementItem {
    private final int milestone;
    private final boolean unlocked;

    public AchievementItem(int milestone, boolean unlocked) {
        this.milestone = milestone;
        this.unlocked = unlocked;
    }

    public int getMilestone() {
        return milestone;
    }

    public boolean isUnlocked() {
        return unlocked;
    }
}
