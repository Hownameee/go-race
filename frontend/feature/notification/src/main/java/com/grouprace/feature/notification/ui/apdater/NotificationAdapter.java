package com.grouprace.feature.notification.ui.apdater;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.model.NotificationModel;
import com.grouprace.feature.notification.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> items = new ArrayList<>();
    private Integer latestNotificationId = null;

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private OnNotificationClickListener clickListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.clickListener = listener;
    }

    public void submitList(List<NotificationModel> newList) {
        this.items = newList;
        if (!newList.isEmpty()) {
            latestNotificationId = newList.get(0).getId();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvMessage.setText(item.getMessage());

        // --- BẮT ĐẦU: Xử lý load Avatar từ URL ---
        String avatarUrl = item.getAvtUrl(); // Giả định model có hàm getAvatarUrl()

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(avatarUrl)
                    .placeholder(com.grouprace.core.system.R.drawable.bg_avatar_placeholder) // Ảnh hiển thị trong lúc chờ tải
                    .error(com.grouprace.core.system.R.drawable.bg_avatar_placeholder)       // Ảnh hiển thị nếu link lỗi
                    .circleCrop()                                  // Bo tròn ảnh
                    .into(holder.ivAvatar);
        } else {
            // Nếu không có URL, set về ảnh mặc định
            holder.ivAvatar.setImageResource(com.grouprace.core.system.R.drawable.bg_avatar_placeholder);
        }
        // --- KẾT THÚC: Xử lý load Avatar ---

        // Xử lý thời gian
        String createdAtStr = item.getCreatedAt();
        try {
            long timeMillis = sdf.parse(createdAtStr).getTime();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    timeMillis,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS);
            holder.tvTime.setText(timeAgo);
        } catch (ParseException e) {
            holder.tvTime.setText(createdAtStr);
        }

        // Logic thay đổi màu nền dựa trên trạng thái đã đọc
        if (!item.isRead()) {
            holder.rootLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), com.grouprace.core.system.R.color.surface_medium));
        } else {
            holder.rootLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onNotificationClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        LinearLayout rootLayout;
        ImageView ivAvatar; // Khai báo ImageView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvMessage = itemView.findViewById(R.id.tv_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            rootLayout = itemView.findViewById(R.id.ll_root);

            // Ánh xạ id từ file XML
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
        }
    }
}