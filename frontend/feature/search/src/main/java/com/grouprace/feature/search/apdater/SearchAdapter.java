package com.grouprace.feature.search.apdater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    public void updateUserStatus(int userId, boolean isFollowing) {
        if (users == null) return;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == userId) {
                users.get(i).setFollowing(isFollowing);
                notifyItemChanged(i); // Chỉ vẽ lại item này
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

        if (isClubTab) {
            holder.btnFollow.setVisibility(View.VISIBLE);
            if (user.isFollowing()) {
                holder.btnFollow.setText("Joined");
            } else {
                holder.btnFollow.setText("Join");
            }
        } else {
            holder.btnFollow.setVisibility(View.VISIBLE);
            if (user.isFollowing()) {
                holder.btnFollow.setText("Following");
            } else {
                holder.btnFollow.setText("Follow");
            }
        }

        holder.btnFollow.setOnClickListener(v -> {
            if (listener != null) {
                listener.onActionClick(user.getUserId(), user.isFollowing());
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