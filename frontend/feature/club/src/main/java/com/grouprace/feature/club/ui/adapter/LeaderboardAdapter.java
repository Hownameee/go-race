package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.ClubStats;
import com.grouprace.feature.club.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<ClubStats.LeaderboardEntry> items = new ArrayList<>();

    public void submitList(List<ClubStats.LeaderboardEntry> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClubStats.LeaderboardEntry item = items.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return Math.min(items.size(), 10);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvDistance;

        ViewHolder(View view) {
            super(view);
            ivAvatar = view.findViewById(R.id.iv_avatar);
            tvName = view.findViewById(R.id.tv_name);
            tvDistance = view.findViewById(R.id.tv_distance);
        }

        void bind(ClubStats.LeaderboardEntry item, int position) {
            tvName.setText(item.getMemberName());
            tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", item.getDistance()));
            
            if (item.getAvatarUrl() != null && !item.getAvatarUrl().isEmpty()) {
                com.bumptech.glide.Glide.with(itemView.getContext())
                        .load(item.getAvatarUrl())
                        .placeholder(com.grouprace.core.system.R.drawable.ic_default_avt)
                        .error(com.grouprace.core.system.R.drawable.ic_default_avt)
                        .circleCrop()
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(com.grouprace.core.system.R.drawable.ic_default_avt);
            }
        }
    }
}
