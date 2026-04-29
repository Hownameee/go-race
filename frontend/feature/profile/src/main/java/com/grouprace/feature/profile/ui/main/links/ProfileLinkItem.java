package com.grouprace.feature.profile.ui.main.links;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public class ProfileLinkItem {
    public enum Action {
        ACTIVITIES,
        STATISTICS,
        ROUTES,
        POSTS,
        CLUBS
    }

    private final Action action;
    private final int iconResId;
    private final String title;
    private final String subtitle;

    public ProfileLinkItem(Action action, @DrawableRes int iconResId, String title, @Nullable String subtitle) {
        this.action = action;
        this.iconResId = iconResId;
        this.title = title;
        this.subtitle = subtitle;
    }

    public Action getAction() {
        return action;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getTitle() {
        return title;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }
}
