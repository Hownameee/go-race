package com.grouprace.feature.search.apdater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.core.model.UserSearchResult;
import com.grouprace.feature.search.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    private List<UserSearchResult> users;
    private boolean isClubTab = false;
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onActionClick(int userId, boolean isFollowing);
        void onItemClick(int userId);
    }

    public SearchAdapter(List<UserSearchResult> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    public void updateData(List<UserSearchResult> newData, boolean isClubTab) {
        this.users = newData;
        this.isClubTab = isClubTab;
        notifyDataSetChanged();
    }
    public void updateUserStatus(int userId, int status) {
        if (users == null) return;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == userId) {
                users.get(i).setFollowStatus(status);
                notifyItemChanged(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_user, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        UserSearchResult user = users.get(position);

        holder.tvUserName.setText(user.getFullname());
        holder.tvLocation.setText(user.getAddress());

        Glide.with(holder.ivAvatar.getContext())
                .load(user.getAvatarUrl())
                .placeholder(com.grouprace.core.system.R.drawable.ic_default_avt)
                .error(com.grouprace.core.system.R.drawable.ic_default_avt)
                .into(holder.ivAvatar);

        if (isClubTab) {
            holder.btnFollow.setVisibility(View.VISIBLE);
            if (user.getFollowStatus() == 1) {
                holder.btnFollow.setText("Joined");
            } else if (user.getFollowStatus() == 2) {
                holder.btnFollow.setText("Requested");
            } else {
                holder.btnFollow.setText("Join");
            }
        } else {
            holder.btnFollow.setVisibility(View.VISIBLE);
            if (user.getFollowStatus() == 1) {
                holder.btnFollow.setText("Following");
            } else {
                holder.btnFollow.setText("Follow");
            }
        }

        holder.btnFollow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(user.getUserId(), user.getFollowStatus() == 1);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(user.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUserName, tvLocation;
        Button btnFollow;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}