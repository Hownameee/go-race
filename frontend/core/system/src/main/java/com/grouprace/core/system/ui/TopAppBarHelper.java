package com.grouprace.core.system.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
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
        if (topBar == null) return; // Top App Bar not found

        TextView tvTitle = topBar.findViewById(R.id.tv_app_title);
        ImageView ivLeftIcon = topBar.findViewById(R.id.iv_app_logo); // Maps to left icon
        LinearLayout llRightIconsContainer = topBar.findViewById(R.id.ll_right_icons_container);// Maps to right icon

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

        // --- 3. XỬ LÝ DANH SÁCH ICON PHẢI (LOGIC MỚI) ---
        if (llRightIconsContainer != null) {
            /// Xóa toàn bộ icon cũ để tránh bị nhân bản khi Fragment vẽ lại UI
            llRightIconsContainer.removeAllViews();

            List<TopAppBarConfig.ActionIcon> rightIcons = config.getRightIcons();

            if (rightIcons != null && !rightIcons.isEmpty()) {
                llRightIconsContainer.setVisibility(View.VISIBLE);
                Context context = rootView.getContext();

                // Quy đổi kích thước từ dp sang px
                int iconSizePx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 28, context.getResources().getDisplayMetrics());
                int marginPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 16, context.getResources().getDisplayMetrics());

                // Duyệt qua danh sách icon được cấu hình và vẽ lên màn hình
                int whiteColor = ContextCompat.getColor(context, R.color.white);
                for (int i = 0; i < rightIcons.size(); i++) {
                    TopAppBarConfig.ActionIcon actionIcon = rightIcons.get(i);

                    // Khởi tạo ImageView động
                    ImageView iconView = new ImageView(context);
                    iconView.setImageResource(actionIcon.iconResId);

                    // Nhuộm trắng icon (tương đương app:tint="@color/white" trong XML)
                    iconView.setColorFilter(whiteColor, PorterDuff.Mode.SRC_IN);

                    // Thiết lập kích thước (28dp x 28dp)
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSizePx, iconSizePx);

                    // Thêm khoảng cách (margin) bên trái cho các icon (trừ icon đầu tiên)
                    if (i > 0) {
                        params.setMarginStart(marginPx);
                    }
                    iconView.setLayoutParams(params);

                    // Thêm hiệu ứng gợn sóng (ripple) chuẩn Material Design khi bấm
                    TypedValue outValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
                    iconView.setBackgroundResource(outValue.resourceId);

                    // Gắn sự kiện click
                    if (actionIcon.onClickListener != null) {
                        iconView.setOnClickListener(actionIcon.onClickListener);
                    }

                    // Nhét ImageView vừa tạo vào vùng chứa
                    llRightIconsContainer.addView(iconView);
                }
            } else {
                // Nếu không có icon nào bên phải thì ẩn luôn vùng chứa
                llRightIconsContainer.setVisibility(View.GONE);
            }
        }
    }
}
