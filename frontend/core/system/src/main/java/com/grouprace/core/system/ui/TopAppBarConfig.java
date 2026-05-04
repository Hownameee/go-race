package com.grouprace.core.system.ui;

import android.view.View;
import androidx.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;

public class TopAppBarConfig {
    public enum IconTag {
        SEARCH,
        NOTIFICATION
    }
    private final String title;
    @DrawableRes
    private final int leftIconResId;
    private final View.OnClickListener onLeftIconClick;

    private final List<ActionIcon> rightIcons;

    private TopAppBarConfig(Builder builder) {
        this.title = builder.title;
        this.leftIconResId = builder.leftIconResId;
        this.onLeftIconClick = builder.onLeftIconClick;
        this.rightIcons = builder.rightIcons;
    }

    public String getTitle() {
        return title;
    }

    @DrawableRes
    public int getLeftIconResId() {
        return leftIconResId;
    }


    public View.OnClickListener getOnLeftIconClick() {
        return onLeftIconClick;
    }

    public List<ActionIcon> getRightIcons() {
        return rightIcons;
    }

    public static class ActionIcon {
        @DrawableRes
        public final int iconResId;
        public final View.OnClickListener onClickListener;
        public int badgeCount; // -1 means no badge
        /** Enum key used by TopAppBarHelper.updateBadge(rootView, tag, count). */
        public final IconTag tag;

        public ActionIcon(@DrawableRes int iconResId, IconTag tag, View.OnClickListener onClickListener) {
            this.iconResId = iconResId;
            this.tag = tag;
            this.onClickListener = onClickListener;
            this.badgeCount = -1;
        }
    }
    public static class Builder {
        private String title;
        @DrawableRes
        private int leftIconResId = 0;
        private View.OnClickListener onLeftIconClick;
        private List<ActionIcon> rightIcons = new ArrayList<>();

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLeftIcon(@DrawableRes int iconResId) {
            this.leftIconResId = iconResId;
            return this;
        }

        public Builder setLeftIcon(@DrawableRes int iconResId, View.OnClickListener onClickListener) {
            this.leftIconResId = iconResId;
            this.onLeftIconClick = onClickListener;
            return this;
        }

        public Builder setOnLeftIconClick(View.OnClickListener onClickListener) {
            this.onLeftIconClick = onClickListener;
            return this;
        }

        public Builder addRightIcon(@DrawableRes int iconResId, View.OnClickListener onClickListener) {
            this.rightIcons.add(new ActionIcon(iconResId, null, onClickListener));
            return this;
        }

        public Builder addRightIcon(@DrawableRes int iconResId, IconTag tag, View.OnClickListener onClickListener) {
            this.rightIcons.add(new ActionIcon(iconResId, tag, onClickListener));
            return this;
        }
        public TopAppBarConfig build() {
            return new TopAppBarConfig(this);
        }
    }
}
