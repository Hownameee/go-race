package com.grouprace.core.system.ui;

import android.view.View;
import androidx.annotation.DrawableRes;

public class TopAppBarConfig {
    private final String title;
    @DrawableRes
    private final int leftIconResId;
    @DrawableRes
    private final int rightIconResId;
    private final View.OnClickListener onLeftIconClick;
    private final View.OnClickListener onRightIconClick;

    private TopAppBarConfig(Builder builder) {
        this.title = builder.title;
        this.leftIconResId = builder.leftIconResId;
        this.rightIconResId = builder.rightIconResId;
        this.onLeftIconClick = builder.onLeftIconClick;
        this.onRightIconClick = builder.onRightIconClick;
    }

    public String getTitle() {
        return title;
    }

    @DrawableRes
    public int getLeftIconResId() {
        return leftIconResId;
    }

    @DrawableRes
    public int getRightIconResId() {
        return rightIconResId;
    }

    public View.OnClickListener getOnLeftIconClick() {
        return onLeftIconClick;
    }

    public View.OnClickListener getOnRightIconClick() {
        return onRightIconClick;
    }

    public static class Builder {
        private String title;
        @DrawableRes
        private int leftIconResId = 0; // 0 means hidden or default if not supported
        @DrawableRes
        private int rightIconResId = 0; // 0 means hidden
        private View.OnClickListener onLeftIconClick;
        private View.OnClickListener onRightIconClick;

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

        public Builder setRightIcon(@DrawableRes int iconResId) {
            this.rightIconResId = iconResId;
            return this;
        }

        public Builder setRightIcon(@DrawableRes int iconResId, View.OnClickListener onClickListener) {
            this.rightIconResId = iconResId;
            this.onRightIconClick = onClickListener;
            return this;
        }

        public Builder setOnRightIconClick(View.OnClickListener onClickListener) {
            this.onRightIconClick = onClickListener;
            return this;
        }

        public TopAppBarConfig build() {
            return new TopAppBarConfig(this);
        }
    }
}
