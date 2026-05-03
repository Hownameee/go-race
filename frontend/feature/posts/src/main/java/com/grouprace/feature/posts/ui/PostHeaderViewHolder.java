package com.grouprace.feature.posts.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Post;
import com.grouprace.core.system.animation.InteractionAnimator;
import com.grouprace.feature.posts.R;
import com.grouprace.feature.posts.ui.adapter.PostMediaAdapter;

import java.util.Locale;

public class PostHeaderViewHolder {
    private final TextView tvUsername;
    private final TextView tvTime;
    private final TextView tvTitle;
    private final TextView tvDistance;
    private final TextView tvPace;
    private final TextView tvDuration;
    private final TextView tvDescription;
    private final View llStats;
    private final ImageView ivMedia;
    private final RecyclerView rvMedia;
    private final ImageView ivActivityType;
    final TextView tvLikes;
    final TextView tvComments;
    final ImageView ivLike;
    final ImageView ivComment;
    final ImageView ivShare;
    final ImageView ivMore;

    public PostHeaderViewHolder(@NonNull View itemView) {
        tvUsername = itemView.findViewById(R.id.tv_username);
        tvTime = itemView.findViewById(R.id.tv_time);
        tvTitle = itemView.findViewById(R.id.tv_title);
        tvDescription = itemView.findViewById(R.id.tv_description);
        tvDistance = itemView.findViewById(R.id.tv_distance);
        tvPace = itemView.findViewById(R.id.tv_pace);
        tvDuration = itemView.findViewById(R.id.tv_duration);
        llStats = itemView.findViewById(R.id.ll_stats);
        ivMedia = itemView.findViewById(R.id.iv_media);
        rvMedia = itemView.findViewById(R.id.rv_media);
        ivActivityType = itemView.findViewById(R.id.iv_activity_type);
        tvLikes = itemView.findViewById(R.id.tv_likes);
        tvComments = itemView.findViewById(R.id.tv_comments);
        ivLike = itemView.findViewById(R.id.iv_like);
        ivComment = itemView.findViewById(R.id.iv_comment);
        ivShare = itemView.findViewById(R.id.iv_share);
        ivMore = itemView.findViewById(R.id.iv_more);

        // Setup PagerSnapHelper for rvMedia
        new androidx.recyclerview.widget.PagerSnapHelper().attachToRecyclerView(rvMedia);
    }

    public void bind(Post post) {
        tvUsername.setText(post.getFullName() != null ? post.getFullName() : "Unknown");
        tvTitle.setText(post.getTitle() != null ? post.getTitle() : "Untitled Activity");
        tvTime.setText(post.getCreatedAt() != null ? post.getCreatedAt() : "");

        if (post.getDescription() != null && !post.getDescription().isEmpty()) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(post.getDescription());
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        if (post.getRecordId() != null) {
            llStats.setVisibility(View.VISIBLE);

            // Distance
            double distance = post.getDistanceKm() != null ? post.getDistanceKm() : 0.0;
            tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", distance));

            // Duration
            int seconds = post.getDurationSeconds() != null ? post.getDurationSeconds() : 0;
            tvDuration.setText(TimeUtils.formatDuration(seconds));

            // Pace (min/km)
            if (distance > 0 && seconds > 0) {
                double paceMinKm = (seconds / 60.0) / distance;
                int paceMin = (int) paceMinKm;
                int paceSec = (int) ((paceMinKm - paceMin) * 60);
                tvPace.setText(String.format(Locale.getDefault(), "%d:%02d /km", paceMin, paceSec));
            } else {
                tvPace.setText("--:--");
            }

            // Activity Icon
            if (post.getActivityType() != null) {
                ivActivityType.setVisibility(View.VISIBLE);
                if (post.getActivityType().equalsIgnoreCase("Running")) {
                    ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_run);
                } else if (post.getActivityType().equalsIgnoreCase("Walking")) {
                    ivActivityType.setImageResource(com.grouprace.core.system.R.drawable.ic_walk);
                } else {
                    ivActivityType.setVisibility(View.GONE);
                }
            } else {
                ivActivityType.setVisibility(View.GONE);
            }

            // TODO: Delete this ivMedia block entirely later
            ivMedia.setVisibility(View.GONE);
        } else {
            llStats.setVisibility(View.GONE);
            ivMedia.setVisibility(View.GONE);
            ivActivityType.setVisibility(View.GONE);
        }

        // Post Photos & Record Image
        java.util.List<String> combinedMedia = new java.util.ArrayList<>();
        if (post.getRecordImageUrl() != null && !post.getRecordImageUrl().isEmpty()) {
            combinedMedia.add(post.getRecordImageUrl());
        }
        if (post.getPhotoUrls() != null && !post.getPhotoUrls().isEmpty()) {
            combinedMedia.addAll(post.getPhotoUrls());
        }

        if (!combinedMedia.isEmpty()) {
            rvMedia.setVisibility(View.VISIBLE);
            PostMediaAdapter mediaAdapter = new PostMediaAdapter();
            rvMedia.setAdapter(mediaAdapter);
            mediaAdapter.submitList(combinedMedia);
        } else {
            rvMedia.setVisibility(View.GONE);
        }

        tvLikes.setText(String.valueOf(post.getLikeCount()));
        tvComments.setText(String.valueOf(post.getCommentCount()));

        if (post.isLiked()) {
            ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like_selected);
        } else {
            ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like);
        }
    }

    public void bindLikes(Post post) {
        tvLikes.setText(String.valueOf(post.getLikeCount()));
        if (post.isLiked()) {
            ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like_selected);
            InteractionAnimator.playPopAnimation(ivLike);
        } else {
            ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like);
        }
    }
}
