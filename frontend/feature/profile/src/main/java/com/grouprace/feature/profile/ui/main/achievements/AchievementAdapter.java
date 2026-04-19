package com.grouprace.feature.profile.ui.main.achievements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.feature.profile.R;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {
    private final List<AchievementItem> items = new ArrayList<>();

    public void submitList(List<AchievementItem> achievements) {
        items.clear();
        if (achievements != null) {
            items.addAll(achievements);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        AchievementItem item = items.get(position);
        holder.milestoneValue.setText(String.valueOf(item.getMilestone()));
        holder.milestoneLabel.setText(AchievementHelper.formatMilestoneLabel(item.getMilestone()));
        holder.badgeContainer.setBackgroundResource(
                item.isUnlocked()
                        ? R.drawable.bg_achievement_hexagon_unlocked
                        : R.drawable.bg_achievement_hexagon_locked
        );
        holder.milestoneValue.setAlpha(item.isUnlocked() ? 1f : 0.55f);
        holder.milestoneLabel.setAlpha(item.isUnlocked() ? 1f : 0.55f);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        final FrameLayout badgeContainer;
        final TextView milestoneValue;
        final TextView milestoneLabel;

        AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            badgeContainer = itemView.findViewById(R.id.achievement_badge_container);
            milestoneValue = itemView.findViewById(R.id.achievement_milestone_value);
            milestoneLabel = itemView.findViewById(R.id.achievement_milestone_label);
        }
    }
}
