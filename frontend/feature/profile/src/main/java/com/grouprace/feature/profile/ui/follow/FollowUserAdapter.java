package com.grouprace.feature.profile.ui.follow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.model.Profile.FollowUser;
import com.grouprace.feature.profile.R;

import java.util.ArrayList;
import java.util.List;

public class FollowUserAdapter extends RecyclerView.Adapter<FollowUserAdapter.FollowUserViewHolder> {
    public interface OnUserClickListener {
        void onUserClick(FollowUser user);
    }

    private final List<FollowUser> items = new ArrayList<>();
    private final OnUserClickListener listener;

    public FollowUserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<FollowUser> users) {
        items.clear();
        if (users != null) {
            items.addAll(users);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FollowUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_user, parent, false);
        return new FollowUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowUserViewHolder holder, int position) {
        FollowUser user = items.get(position);
        holder.fullname.setText(user.getFullname() != null ? user.getFullname() : "Unknown athlete");
        String username = user.getUsername() != null && !user.getUsername().trim().isEmpty()
                ? "@" + user.getUsername()
                : "Profile";
        holder.username.setText(username);

        Glide.with(holder.avatar.getContext())
                .load(user.getAvatarUrl())
                .placeholder(com.grouprace.core.system.R.drawable.bg_avatar_placeholder)
                .error(com.grouprace.core.system.R.drawable.bg_avatar_placeholder)
                .into(holder.avatar);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class FollowUserViewHolder extends RecyclerView.ViewHolder {
        private final ImageView avatar;
        private final TextView fullname;
        private final TextView username;

        FollowUserViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.follow_user_avatar);
            fullname = itemView.findViewById(R.id.follow_user_fullname);
            username = itemView.findViewById(R.id.follow_user_username);
        }
    }
}
