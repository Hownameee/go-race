package com.grouprace.feature.club.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grouprace.core.model.ClubMember;
import com.grouprace.feature.club.R;

import java.util.ArrayList;
import java.util.List;

public class ClubMemberAdapter extends RecyclerView.Adapter<ClubMemberAdapter.ViewHolder> {

    private List<ClubMember> members = new ArrayList<>();
    private final OnMemberClickListener listener;

    public interface OnMemberClickListener {
        void onMemberClick(ClubMember member);
    }

    public ClubMemberAdapter(OnMemberClickListener listener) {
        this.listener = listener;
    }

    public void setMembers(List<ClubMember> newMembers) {
        this.members = newMembers;
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
        ClubMember member = members.get(position);
        holder.tvName.setText(member.getFullname());
        
        String roleText = member.getRole();
        if (member.isLeader()) {
            roleText = "Leader";
        } else if ("admin".equalsIgnoreCase(member.getRole())) {
            roleText = "Admin";
        } else {
            roleText = "Member";
        }
        
        holder.tvRole.setText(roleText);
        holder.tvRole.setVisibility(View.VISIBLE);

        if (member.getAvatarUrl() != null && !member.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(member.getAvatarUrl())
                    .circleCrop()
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(com.grouprace.core.system.R.drawable.ic_default_avt);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMemberClick(member);
            }
        });
    }

    @Override
    public int getItemCount() {
        return members.size();
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
