package com.grouprace.feature.profile.ui.posts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder> {
    private final List<Post> posts = new ArrayList<>();

    public void submitList(@Nullable List<Post> items) {
        posts.clear();
        if (items != null) {
            posts.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(com.grouprace.feature.posts.R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;
        private final TextView tvTime;
        private final TextView tvTitle;
        private final TextView tvDistance;
        private final TextView tvPace;
        private final TextView tvDuration;
        private final View llStats;
        private final ImageView ivMedia;
        private final ImageView ivActivityType;
        private final TextView tvLikes;
        private final TextView tvComments;
        private final ImageView ivLike;
        private final ImageView ivComment;
        private final ImageView ivShare;
        private final ImageView ivMore;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_username);
            tvTime = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_time);
            tvTitle = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_title);
            tvDistance = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_distance);
            tvPace = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_pace);
            tvDuration = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_duration);
            llStats = itemView.findViewById(com.grouprace.feature.posts.R.id.ll_stats);
            ivMedia = itemView.findViewById(com.grouprace.feature.posts.R.id.iv_media);
            ivActivityType = itemView.findViewById(com.grouprace.feature.posts.R.id.iv_activity_type);
            tvLikes = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_likes);
            tvComments = itemView.findViewById(com.grouprace.feature.posts.R.id.tv_comments);
            ivLike = itemView.findViewById(com.grouprace.feature.posts.R.id.iv_like);
            ivComment = itemView.findViewById(com.grouprace.feature.posts.R.id.iv_comment);
            ivShare = itemView.findViewById(com.grouprace.feature.posts.R.id.iv_share);
            ivMore = itemView.findViewById(com.grouprace.feature.posts.R.id.iv_more);
        }

        void bind(Post post) {
            tvUsername.setText(post.getFullName() != null ? post.getFullName() : "Unknown");
            tvTitle.setText(post.getTitle() != null ? post.getTitle() : "Untitled Activity");
            tvTime.setText(post.getCreatedAt() != null ? post.getCreatedAt() : "");

            if (post.getRecordId() != null) {
                llStats.setVisibility(View.VISIBLE);

                double distance = post.getDistanceKm() != null ? post.getDistanceKm() : 0.0;
                tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance));

                int seconds = post.getDurationSeconds() != null ? post.getDurationSeconds() : 0;
                tvDuration.setText(TimeUtils.formatDuration(seconds));

                if (distance > 0 && seconds > 0) {
                    double paceMinKm = (seconds / 60.0) / distance;
                    int paceMin = (int) paceMinKm;
                    int paceSec = (int) ((paceMinKm - paceMin) * 60);
                    tvPace.setText(String.format(Locale.getDefault(), "%d:%02d /km", paceMin, paceSec));
                } else {
                    tvPace.setText("--:--");
                }

                if (post.getActivityType() != null) {
                    ivActivityType.setVisibility(View.VISIBLE);
                    if ("Running".equalsIgnoreCase(post.getActivityType())) {
                        ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_run);
                    } else if ("Walking".equalsIgnoreCase(post.getActivityType())) {
                        ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_walk);
                    } else {
                        ivActivityType.setVisibility(View.GONE);
                    }
                } else {
                    ivActivityType.setVisibility(View.GONE);
                }

                if (post.getRecordImageUrl() != null && !post.getRecordImageUrl().isEmpty()) {
                    ivMedia.setVisibility(View.VISIBLE);
                    Glide.with(itemView.getContext())
                            .load(post.getRecordImageUrl())
                            .centerCrop()
                            .into(ivMedia);
                } else {
                    ivMedia.setVisibility(View.GONE);
                }
            } else {
                llStats.setVisibility(View.GONE);
                ivMedia.setVisibility(View.GONE);
                ivActivityType.setVisibility(View.GONE);
            }

            tvLikes.setText(String.valueOf(post.getLikeCount()));
            tvComments.setText(String.valueOf(post.getCommentCount()));

            ivLike.setImageResource(
                    post.isLiked()
                            ? com.grouprace.core.system.R.drawable.ic_like_selected
                            : com.grouprace.core.system.R.drawable.ic_like
            );

            ivLike.setEnabled(false);
            ivComment.setEnabled(false);
            ivShare.setEnabled(false);
            ivMore.setEnabled(false);
        }
    }
}
