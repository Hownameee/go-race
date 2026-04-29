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
import com.grouprace.feature.profile.R;

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

    public boolean isEmpty() {
        return posts.isEmpty();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_post, parent, false);
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
        private final TextView author;
        private final TextView time;
        private final TextView title;
        private final TextView description;
        private final View stats;
        private final TextView distance;
        private final TextView duration;
        private final TextView speed;
        private final ImageView media;
        private final TextView likes;
        private final TextView comments;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.profile_post_author);
            time = itemView.findViewById(R.id.profile_post_time);
            title = itemView.findViewById(R.id.profile_post_title);
            description = itemView.findViewById(R.id.profile_post_description);
            stats = itemView.findViewById(R.id.profile_post_stats);
            distance = itemView.findViewById(R.id.profile_post_distance);
            duration = itemView.findViewById(R.id.profile_post_duration);
            speed = itemView.findViewById(R.id.profile_post_speed);
            media = itemView.findViewById(R.id.profile_post_media);
            likes = itemView.findViewById(R.id.profile_post_likes);
            comments = itemView.findViewById(R.id.profile_post_comments);
        }

        void bind(Post post) {
            author.setText(post.getFullName() != null ? post.getFullName() : "Unknown");
            time.setText(post.getCreatedAt() != null ? post.getCreatedAt() : "");
            title.setText(post.getTitle() != null ? post.getTitle() : "Untitled Activity");

            if (post.getDescription() != null && !post.getDescription().trim().isEmpty()) {
                description.setVisibility(View.VISIBLE);
                description.setText(post.getDescription());
            } else {
                description.setVisibility(View.GONE);
            }

            if (post.getRecordId() != null) {
                stats.setVisibility(View.VISIBLE);
                double distanceKm = post.getDistanceKm() != null ? post.getDistanceKm() : 0.0;
                int seconds = post.getDurationSeconds() != null ? post.getDurationSeconds() : 0;
                double speedValue = post.getSpeed() != null ? post.getSpeed() : 0.0;
                distance.setText(String.format(Locale.getDefault(), "%.2f km", distanceKm));
                duration.setText(TimeUtils.formatDuration(seconds));
                speed.setText(String.format(Locale.getDefault(), "%.1f km/h", speedValue));
            } else {
                stats.setVisibility(View.GONE);
            }

            String imageUrl = post.getRecordImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                media.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext()).load(imageUrl).centerCrop().into(media);
            } else {
                media.setVisibility(View.GONE);
            }

            likes.setText(post.getLikeCount() + " likes");
            comments.setText(post.getCommentCount() + " comments");
        }
    }
}
