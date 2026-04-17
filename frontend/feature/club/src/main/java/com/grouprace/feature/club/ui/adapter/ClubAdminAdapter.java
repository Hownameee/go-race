package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.model.ClubAdmin;
import com.grouprace.feature.club.R;

import java.util.ArrayList;
import java.util.List;

public class ClubAdminAdapter extends RecyclerView.Adapter<ClubAdminAdapter.ViewHolder> {

    private List<ClubAdmin> admins = new ArrayList<>();

    public void setAdmins(List<ClubAdmin> newAdmins) {
        this.admins = newAdmins;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClubAdmin admin = admins.get(position);
        holder.tvName.setText(admin.getFullname());
        
        if (admin.isLeader()) {
            holder.tvRole.setText("Leader");
            holder.tvRole.setVisibility(View.VISIBLE);
        } else {
            holder.tvRole.setText("Admin");
            holder.tvRole.setVisibility(View.VISIBLE);
        }

        if (admin.getAvatarUrl() != null && !admin.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(admin.getAvatarUrl())
                    .circleCrop()
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() {
        return admins.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;
        TextView tvRole;

        ViewHolder(View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_admin_avatar);
            tvName = itemView.findViewById(R.id.tv_admin_name);
            tvRole = itemView.findViewById(R.id.tv_admin_role_badge);
        }
    }
}
