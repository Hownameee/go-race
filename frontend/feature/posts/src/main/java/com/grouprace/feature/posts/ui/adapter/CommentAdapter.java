package com.grouprace.feature.posts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.Comment;
import com.grouprace.feature.posts.R;
import com.grouprace.core.system.animation.InteractionAnimator;

public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public static final String PAYLOAD_LIKE = "PAYLOAD_LIKE";

    public interface OnCommentActionListener {
        void onLikeClicked(Comment comment, int position);

        void onReplyClicked(Comment comment);

        void onViewRepliesClicked(Comment comment);
    }

    private final OnCommentActionListener listener;

    private static final DiffUtil.ItemCallback<Comment> DIFF_CALLBACK = new DiffUtil.ItemCallback<Comment>() {
        @Override
        public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
            return oldItem.getCommentId() == newItem.getCommentId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
            return oldItem.getContent().equals(newItem.getContent()) &&
                    oldItem.getCreatedAt().equals(newItem.getCreatedAt()) &&
                    oldItem.isLiked() == newItem.isLiked() &&
                    oldItem.getLikeCount() == newItem.getLikeCount();
        }

        @Nullable
        @Override
        public Object getChangePayload(@NonNull Comment oldItem, @NonNull Comment newItem) {
            if (oldItem.isLiked() != newItem.isLiked() || oldItem.getLikeCount() != newItem.getLikeCount()) {
                return PAYLOAD_LIKE;
            }
            return super.getChangePayload(oldItem, newItem);
        }
    };

    public CommentAdapter(OnCommentActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull java.util.List<Object> payloads) {
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
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = getItem(position);
        holder.bind(comment);

        InteractionAnimator.setupSquishAnimation(holder.ivLike);
        holder.ivLike.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (listener != null && currentPos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                listener.onLikeClicked(getItem(currentPos), currentPos);
            }
        });

        holder.tvReply.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (listener != null && currentPos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                listener.onReplyClicked(getItem(currentPos));
            }
        });

        holder.tvViewReplies.setOnClickListener(v -> {
            int currentPos = holder.getBindingAdapterPosition();
            if (listener != null && currentPos != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                listener.onViewRepliesClicked(getItem(currentPos));
            }
        });
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvUsername;
        private final TextView tvTime;
        private final TextView tvContent;
        private final TextView tvLikeCount;
        private final TextView tvReply;
        private final TextView tvViewReplies;
        private final ImageView ivAvatar;
        private final ImageView ivLike;
        private final OnCommentActionListener listener;

        public CommentViewHolder(@NonNull View itemView, OnCommentActionListener listener) {
            super(itemView);
            this.listener = listener;
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            tvReply = itemView.findViewById(R.id.tv_reply);
            tvViewReplies = itemView.findViewById(R.id.tv_view_replies);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivLike = itemView.findViewById(R.id.iv_like);
        }

        public void bind(Comment comment) {
            tvUsername.setText(comment.getFullName() != null ? comment.getFullName() : comment.getUsername());
            tvContent.setText(comment.getContent());
            tvTime.setText(comment.getCreatedAt());

            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            if (comment.isLiked()) {
                ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like_selected);
            } else {
                ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like);
            }

            if (comment.getReplyCount() > 0) {
                tvViewReplies.setVisibility(View.VISIBLE);
                if (comment.isRepliesExpanded()) {
                    tvViewReplies.setText("Hide replies");
                } else {
                    tvViewReplies.setText(
                            "View " + comment.getReplyCount() + " " + (comment.getReplyCount() == 1 ? "reply" : "replies"));
                }
            } else {
                tvViewReplies.setVisibility(View.GONE);
            }

            // Indentation for replies
            int marginStart = comment.getParentId() != null
                    ? (int) (48 * itemView.getContext().getResources().getDisplayMetrics().density)
                    : 0;
            itemView.setPadding(marginStart, itemView.getPaddingTop(), itemView.getPaddingRight(),
                    itemView.getPaddingBottom());
        }

        public void bindLikes(Comment comment) {
            tvLikeCount.setText(String.valueOf(comment.getLikeCount()));
            if (comment.isLiked()) {
                ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like_selected);
                InteractionAnimator.playPopAnimation(ivLike);
            } else {
                ivLike.setImageResource(com.grouprace.core.system.R.drawable.ic_like);
            }
        }
    }
}
