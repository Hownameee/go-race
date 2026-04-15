package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.feature.club.R;

import java.util.List;

public class ClubNavAdapter extends RecyclerView.Adapter<ClubNavAdapter.NavViewHolder> {
    private final List<NavItem> navItems;
    private final OnNavClickListener listener;

    public ClubNavAdapter(List<NavItem> navItems, OnNavClickListener listener) {
        this.navItems = navItems;
        this.listener = listener;
    }

    public void updateItems(List<NavItem> items) {
        this.navItems.clear();
        if (items != null) {
            this.navItems.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_nav_button, parent, false);
        return new NavViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NavViewHolder holder, int position) {
        NavItem item = navItems.get(position);
        holder.bind(item);

        holder.itemView.setOnClickListener(v -> {
            listener.onNavClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return navItems != null ? navItems.size() : 0;
    }

    public interface OnNavClickListener {
        void onNavClick(NavItem item);
    }

    static class NavViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        public NavViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ View từ file XML mới
            ivIcon = itemView.findViewById(R.id.iv_nav_icon);
            tvTitle = itemView.findViewById(R.id.tv_nav_title);
        }

        public void bind(NavItem item) {
            tvTitle.setText(item.getTitle());
            ivIcon.setImageResource(item.getIconResId());
        }
    }

    public static class NavItem {
        private final String id;
        private final String title;
        private final int iconResId; // THÊM TRƯỜNG NÀY ĐỂ LƯU ICON
        private final Fragment targetFragment;

        public NavItem(String id, String title, int iconResId, Fragment targetFragment) {
            this.id = id;
            this.title = title;
            this.iconResId = iconResId;
            this.targetFragment = targetFragment;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public int getIconResId() {
            return iconResId;
        }

        public Fragment getTargetFragment() {
            return targetFragment;
        }
    }
}