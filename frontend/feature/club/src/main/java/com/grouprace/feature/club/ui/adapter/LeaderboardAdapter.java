package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.model.ClubStats;
import com.grouprace.feature.club.R;

import java.util.ArrayList;
import java.util.List;

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
        holder.bind(item, position + 1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRank;
        private final ImageView ivAvatar;
        private final TextView tvName;
        private final TextView tvDistance;

        ViewHolder(View view) {
            super(view);
            tvRank = view.findViewById(R.id.tv_rank);
            ivAvatar = view.findViewById(R.id.iv_avatar);
            tvName = view.findViewById(R.id.tv_name);
            tvDistance = view.findViewById(R.id.tv_distance);
        }

        void bind(ClubStats.LeaderboardEntry item, int rank) {
            tvRank.setText(String.valueOf(rank));
            tvName.setText(item.getMemberName());
            tvDistance.setText(String.format("%.2f km", item.getDistance()));

            if (item.getAvatarUrl() != null && !item.getAvatarUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(item.getAvatarUrl())
                        .circleCrop()
                        .placeholder(com.grouprace.core.system.R.drawable.bg_avatar_placeholder)
                        .error(com.grouprace.core.system.R.drawable.bg_avatar_placeholder)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(com.grouprace.core.system.R.drawable.bg_avatar_placeholder);
            }
        }
    }
}
