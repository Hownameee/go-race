package com.grouprace.core.system.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.grouprace.core.system.R;

public class TopAppBarHelper {

    public static void setupTopAppBar(View rootView, TopAppBarConfig config) {
        if (config == null || rootView == null) return;

        View topBar = rootView.findViewById(R.id.top_bar);
        if (topBar == null) return; // Top App Bar not found

        TextView tvTitle = topBar.findViewById(R.id.tv_app_title);
        ImageView ivLeftIcon = topBar.findViewById(R.id.iv_app_logo); // Maps to left icon
        ImageView ivRightIcon = topBar.findViewById(R.id.iv_notification_bell); // Maps to right icon

        if (tvTitle != null) {
            if (config.getTitle() != null) {
                tvTitle.setText(config.getTitle());
                tvTitle.setVisibility(View.VISIBLE);
            }
        }

        if (ivLeftIcon != null) {
            if (config.getLeftIconResId() != 0) {
                ivLeftIcon.setImageResource(config.getLeftIconResId());
                ivLeftIcon.setVisibility(View.VISIBLE);
                
                if (config.getOnLeftIconClick() != null) {
                    ivLeftIcon.setOnClickListener(config.getOnLeftIconClick());
                } else {
                    ivLeftIcon.setOnClickListener(null);
                }
            } else {
                ivLeftIcon.setVisibility(View.GONE);
            }
        }

        if (ivRightIcon != null) {
            if (config.getRightIconResId() != 0) {
                ivRightIcon.setImageResource(config.getRightIconResId());
                ivRightIcon.setVisibility(View.VISIBLE);

                if (config.getOnRightIconClick() != null) {
                    ivRightIcon.setOnClickListener(config.getOnRightIconClick());
                } else {
                    ivRightIcon.setOnClickListener(null);
                }
            } else {
                ivRightIcon.setVisibility(View.GONE);
            }
        }
    }
}
