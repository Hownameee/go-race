package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.ClubStats;
import com.grouprace.feature.club.R;

public class ClubLeaderboardAdapter extends ListAdapter<ClubStats.LeaderboardEntry, ClubLeaderboardAdapter.LeaderboardViewHolder> {

    public ClubLeaderboardAdapter() {
        super(new DiffUtil.ItemCallback<ClubStats.LeaderboardEntry>() {
            @Override
            public boolean areItemsTheSame(@NonNull ClubStats.LeaderboardEntry oldItem, @NonNull ClubStats.LeaderboardEntry newItem) {
                return oldItem.getMemberId().equals(newItem.getMemberId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull ClubStats.LeaderboardEntry oldItem, @NonNull ClubStats.LeaderboardEntry newItem) {
                return oldItem.getDistance() == newItem.getDistance();
            }
        });
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        ClubStats.LeaderboardEntry entry = getItem(position);
        holder.bind(entry, position + 1);
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvDistance;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.text_rank);
            tvName = itemView.findViewById(R.id.text_member_name);
            tvDistance = itemView.findViewById(R.id.text_distance);
        }

        public void bind(ClubStats.LeaderboardEntry entry, int rank) {
            tvRank.setText(String.valueOf(rank));
            tvName.setText(entry.getMemberName());
            tvDistance.setText(String.format("%.1f km", entry.getDistance()));
        }
    }
}
