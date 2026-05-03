package com.grouprace.core.system.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.grouprace.core.system.R;

import java.util.List;

public class TopAppBarHelper {

    public static void setupTopAppBar(View rootView, TopAppBarConfig config) {
        if (config == null || rootView == null) return;

        View topBar = rootView.findViewById(R.id.top_bar);
        if (topBar == null) return;

        TextView tvTitle = topBar.findViewById(R.id.tv_app_title);
        if (tvTitle != null) {
            String title = config.getTitle();
            if (title != null) {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            }
        }

        ImageView ivLeftIcon = topBar.findViewById(R.id.iv_app_logo);
        if (ivLeftIcon != null) {
            if (config.getLeftIconResId() != 0) {
                ivLeftIcon.setImageResource(config.getLeftIconResId());
                ivLeftIcon.setVisibility(View.VISIBLE);
                ivLeftIcon.setOnClickListener(config.getOnLeftIconClick()); // null is fine → clears listener
            } else {
                ivLeftIcon.setVisibility(View.GONE);
            }
        }

        LinearLayout container = topBar.findViewById(R.id.ll_right_icons_container);
        if (container == null) return;

        List<TopAppBarConfig.ActionIcon> icons = config.getRightIcons();

        if (icons == null || icons.isEmpty()) {
            container.setVisibility(View.GONE);
            return;
        }

        container.setVisibility(View.VISIBLE);
        Context context = rootView.getContext();
        int marginPx = context.getResources().getDimensionPixelSize(R.dimen.toolbar_icon_spacing);

        // Inflate missing wrappers (only runs on first call or when icon count grows).
        int existing = container.getChildCount();
        for (int i = existing; i < icons.size(); i++) {
            View wrapper = LayoutInflater.from(context)
                    .inflate(R.layout.item_top_app_bar_icon, container, false);

            // Spacing: all icons except the first get a start margin.
            if (i > 0) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) wrapper.getLayoutParams();
                lp.setMarginStart(marginPx);
                wrapper.setLayoutParams(lp);
            }

            // Ripple on the icon itself (borderless).
            ImageView icon = wrapper.findViewById(R.id.iv_toolbar_icon);
            TypedValue ripple = new TypedValue();
            context.getTheme().resolveAttribute(
                    android.R.attr.selectableItemBackgroundBorderless, ripple, true);
            icon.setBackgroundResource(ripple.resourceId);

            container.addView(wrapper);
        }

        // Remove surplus wrappers (icon count shrank — unusual but safe).
        while (container.getChildCount() > icons.size()) {
            container.removeViewAt(container.getChildCount() - 1);
        }

        // Bind / refresh every wrapper with the latest config data.
        int whiteColor = ContextCompat.getColor(context, R.color.white);
        for (int i = 0; i < icons.size(); i++) {
            TopAppBarConfig.ActionIcon actionIcon = icons.get(i);
            View wrapper = container.getChildAt(i);

            // Enum key stored on wrapper for type-safe updateBadge() lookups.
            if (actionIcon.tag != null) {
                wrapper.setTag(R.id.tag_icon_key, actionIcon.tag);
            }

            ImageView iconView = wrapper.findViewById(R.id.iv_toolbar_icon);
            iconView.setImageResource(actionIcon.iconResId);
            iconView.setColorFilter(whiteColor, PorterDuff.Mode.SRC_IN);
            iconView.setOnClickListener(actionIcon.onClickListener);

            TextView badgeView = wrapper.findViewById(R.id.tv_toolbar_badge);
            applyBadgeCount(badgeView, actionIcon.badgeCount);
        }
    }

    public static void updateBadge(View rootView, TopAppBarConfig.IconTag iconTag, int count) {
        if (rootView == null || iconTag == null) return;
        View topBar = rootView.findViewById(R.id.top_bar);
        if (topBar == null) return;

        LinearLayout container = topBar.findViewById(R.id.ll_right_icons_container);
        if (container == null) return;

        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (iconTag == child.getTag(R.id.tag_icon_key)) {
                TextView badge = child.findViewById(R.id.tv_toolbar_badge);
                if (badge != null) applyBadgeCount(badge, count);
                return;
            }
        }
    }



    private static void applyBadgeCount(TextView badge, int count) {
        if (count <= 0) {
            badge.setVisibility(View.GONE);
        } else {
            badge.setVisibility(View.VISIBLE);
            badge.setText(count > 9 ? "9+" : String.valueOf(count));
        }
    }
}
