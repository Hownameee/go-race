package com.grouprace.feature.posts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.Comment;
import com.grouprace.feature.posts.R;

public class CommentAdapter extends ListAdapter<Comment, CommentAdapter.CommentViewHolder> {

    public interface OnCommentActionListener {
        void onLikeClick(Comment comment);
        void onReplyClick(Comment comment);
        void onViewRepliesClick(Comment comment);
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
                   oldItem.getCreatedAt().equals(newItem.getCreatedAt());
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
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(getItem(position));
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
                tvViewReplies.setText("View " + comment.getReplyCount() + " " + (comment.getReplyCount() == 1 ? "reply" : "replies"));
            } else {
                tvViewReplies.setVisibility(View.GONE);
            }

            // Indentation for replies
            int marginStart = comment.getParentId() != null ? 
                (int) (48 * itemView.getContext().getResources().getDisplayMetrics().density) : 0;
            itemView.setPadding(marginStart, itemView.getPaddingTop(), itemView.getPaddingRight(), itemView.getPaddingBottom());

            ivLike.setOnClickListener(v -> listener.onLikeClick(comment));
            tvReply.setOnClickListener(v -> listener.onReplyClick(comment));
            tvViewReplies.setOnClickListener(v -> listener.onViewRepliesClick(comment));
        }
    }
}
