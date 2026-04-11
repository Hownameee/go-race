package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.Post;
import com.grouprace.feature.club.R;

public class ClubPostAdapter extends ListAdapter<Post, ClubPostAdapter.PostViewHolder> {

    public ClubPostAdapter() {
        super(new DiffUtil.ItemCallback<Post>() {
            @Override
            public boolean areItemsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return oldItem.getPostId() == newItem.getPostId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Post oldItem, @NonNull Post newItem) {
                return oldItem.getDescription().equals(newItem.getDescription());
            }
        });
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = getItem(position);
        holder.bind(post);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor, tvContent, tvTime;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.text_author_name);
            tvContent = itemView.findViewById(R.id.text_post_content);
            tvTime = itemView.findViewById(R.id.text_post_time);
        }

        public void bind(Post post) {
            tvAuthor.setText(post.getUsername()); // simple mock mapping
            tvContent.setText(post.getDescription());
            tvTime.setText(post.getCreatedAt()); 
        }
    }
}
