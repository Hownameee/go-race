package com.grouprace.feature.search.apdater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.imageview.ShapeableImageView;
import com.grouprace.core.model.User;
import com.grouprace.feature.search.R;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.UserViewHolder> {

    private List<User> userList;

    public SearchAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggested_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvLocation.setText(user.getLocation());
        
        // Logic hiển thị viền cam (stroke) cho Avatar nếu có Badge
        if (user.isHasBadge()) {
            holder.ivAvatar.setStrokeWidth(4f); // Chỉnh độ dày viền
        } else {
            holder.ivAvatar.setStrokeWidth(0f);
        }

        holder.btnFollow.setOnClickListener(v -> {
            holder.btnFollow.setText("Following");
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAvatar;
        TextView tvName, tvLocation;
        Button btnFollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }
}