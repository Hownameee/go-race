package com.grouprace.feature.posts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Post;
import com.grouprace.feature.posts.R;
import com.grouprace.core.system.animation.InteractionAnimator;

import java.util.Locale;

public class PostAdapter extends ListAdapter<Post, PostAdapter.PostViewHolder> {

    public static final String PAYLOAD_LIKE = "PAYLOAD_LIKE";

    public interface OnPostActionListener {
        void onLikeClicked(Post post, int position);

        void onCommentClicked(Post post);

        void onShareClicked(Post post);
        void onReportClicked(Post post);
    }

    private OnPostActionListener listener;

    private static final DiffUtil.ItemCallback<Post> DIFF_CALLBACK = new DiffUtil.ItemCallback<Post>() {
        @Override
        public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getPostId() == newItem.getPostId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
            return oldItem.getPostId() == newItem.getPostId()
                    && oldItem.getLikeCount() == newItem.getLikeCount()
                    && oldItem.getCommentCount() == newItem.getCommentCount()
                    && oldItem.isLiked() == newItem.isLiked();
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull Post oldItem, @NonNull Post newItem) {
            if (oldItem.isLiked() != newItem.isLiked() || oldItem.getLikeCount() != newItem.getLikeCount()) {
                return PAYLOAD_LIKE;
            }
            return super.getChangePayload(oldItem, newItem);
        }
    };

    public PostAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnPostActionListener(OnPostActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position,
            @NonNull java.util.List<Object> payloads) {
        if (!payloads.isEmpty()) {
            for (Object payload : payloads) {
                if (PAYLOAD_LIKE.equals(payload)) {
                    holder.bindLikes(getItem(position));
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = getItem(position);
        holder.bind(post);

        InteractionAnimator.setupSquishAnimation(holder.ivLike);
        holder.ivLike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeClicked(post, position);
            }
        });

        View.OnClickListener commentClickListener = v -> {
            if (listener != null) {
                listener.onCommentClicked(post);
            }
        };
        InteractionAnimator.setupSquishAnimation(holder.ivComment);
        holder.ivComment.setOnClickListener(commentClickListener);
        holder.tvComments.setOnClickListener(commentClickListener);

        InteractionAnimator.setupSquishAnimation(holder.ivShare);
        holder.ivShare.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareClicked(post);
            }
        });

        holder.ivMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.menu_post_more, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_report) {
                    if (listener != null) {
                        listener.onReportClicked(post);
                    }
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    public String getLastPostCreatedAt() {
        if (getItemCount() > 0) {
            return getItem(getItemCount() - 1).getCreatedAt();
        }
        return null;
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
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

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
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

                // Record Image
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

            // Post Photos
            if (post.getPhotoUrls() != null && !post.getPhotoUrls().isEmpty()) {
                rvMedia.setVisibility(View.VISIBLE);
                PostMediaAdapter mediaAdapter = new PostMediaAdapter();
                rvMedia.setAdapter(mediaAdapter);
                mediaAdapter.submitList(post.getPhotoUrls());
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
}
