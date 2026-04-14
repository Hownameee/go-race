package com.grouprace.feature.club.ui.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.grouprace.core.model.Club;
import com.grouprace.feature.club.R;

public class ClubAdapter extends ListAdapter<Club, RecyclerView.ViewHolder> {

    private static final int TYPE_LINEAR = 0;
    private static final int TYPE_GRID = 1;

    private boolean isGridMode = false;
    private OnClubClickListener listener;

    public ClubAdapter() {
        super(new DiffUtil.ItemCallback<Club>() {
            @Override
            public boolean areItemsTheSame(@NonNull Club oldItem, @NonNull Club newItem) {
                return oldItem.getClubId() == newItem.getClubId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Club oldItem, @NonNull Club newItem) {
                return oldItem.getClubId() == newItem.getClubId() &&
                        (oldItem.getStatus() == null ? newItem.getStatus() == null : oldItem.getStatus().equals(newItem.getStatus())) &&
                        oldItem.getName().equals(newItem.getName()) &&
                        (oldItem.getAvatarUrl() == null ? newItem.getAvatarUrl() == null : oldItem.getAvatarUrl().equals(newItem.getAvatarUrl()));
            }
        });
    }

    public void setGridMode(boolean isGridMode) {
        this.isGridMode = isGridMode;
    }

    public void setListener(OnClubClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return isGridMode ? TYPE_GRID : TYPE_LINEAR;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_GRID) {
            View view = inflater.inflate(R.layout.item_club_list_grid, parent, false);
            return new GridViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_club_list_linear, parent, false);
            return new LinearViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Club club = getItem(position);
        if (holder instanceof GridViewHolder) {
            ((GridViewHolder) holder).bind(club, listener);
        } else if (holder instanceof LinearViewHolder) {
            ((LinearViewHolder) holder).bind(club, listener);
        }
    }

    public interface OnClubClickListener {
        void onClubClick(Club club);

        void onJoinClick(Club club);
    }

    static class LinearViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMembers, tvPosts, tvDesc, btnExpand;
        ImageView ivAvatar;
        ProgressBar pbLoading;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.text_club_name);
            tvMembers = itemView.findViewById(R.id.text_club_members);
            tvPosts = itemView.findViewById(R.id.text_club_posts);
            tvDesc = itemView.findViewById(R.id.text_club_description);
            btnExpand = itemView.findViewById(R.id.button_expand);
            ivAvatar = itemView.findViewById(R.id.image_club_avatar);
            pbLoading = itemView.findViewById(R.id.pb_image_loading);
        }

        public void bind(Club club, OnClubClickListener listener) {
            tvName.setText(club.getName());
            tvMembers.setText(club.getMemberCount() + " Members");
            tvPosts.setText(club.getPostCount() + " Posts");
            tvDesc.setText(club.getDescription());

            // Bind Avatar with Glide and Loading Indicator
            Glide.with(itemView.getContext()).clear(ivAvatar);
            ivAvatar.setImageDrawable(null);

            if (club.getAvatarUrl() != null && !club.getAvatarUrl().isEmpty()) {
                pbLoading.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(club.getAvatarUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                pbLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                pbLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .centerCrop()
                        .into(ivAvatar);
            } else {
                pbLoading.setVisibility(View.GONE);
            }

            tvDesc.setMaxLines(1);
            tvDesc.setEllipsize(TextUtils.TruncateAt.END);
            btnExpand.setVisibility(View.GONE);

            tvDesc.post(() -> {
                int lineCount = tvDesc.getLineCount();
                if (lineCount > 0 && tvDesc.getLayout() != null) {
                    if (tvDesc.getLayout().getEllipsisCount(lineCount - 1) > 0) {
                        btnExpand.setVisibility(View.VISIBLE);
                    } else {
                        btnExpand.setVisibility(View.GONE);
                    }
                }
            });

            btnExpand.setOnClickListener(v -> {
                TransitionManager.beginDelayedTransition((ViewGroup) itemView);
                tvDesc.setMaxLines(Integer.MAX_VALUE);
                tvDesc.setEllipsize(null);
                btnExpand.setVisibility(View.GONE);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClubClick(club);
            });
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMembers;
        View btnJoin;
        ImageView ivAvatar;
        ProgressBar pbLoading;


        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.text_club_name);
            tvMembers = itemView.findViewById(R.id.text_club_members);
            btnJoin = itemView.findViewById(R.id.button_join);
            ivAvatar = itemView.findViewById(R.id.image_club_avatar);
            pbLoading = itemView.findViewById(R.id.pb_image_loading);
        }

        public void bind(Club club, OnClubClickListener listener) {
            tvName.setText(club.getName());
            tvMembers.setText(club.getMemberCount() + " Members");

            Glide.with(itemView.getContext()).clear(ivAvatar);
            ivAvatar.setImageDrawable(null);

            if (club.getAvatarUrl() != null && !club.getAvatarUrl().isEmpty()) {
                pbLoading.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(club.getAvatarUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                pbLoading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                pbLoading.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .centerCrop()
                        .into(ivAvatar);
            } else {
                pbLoading.setVisibility(View.GONE);
            }

            if ("approved".equals(club.getStatus())) {
                ((TextView) btnJoin).setText("Joined");
                btnJoin.setEnabled(false);
                btnJoin.setVisibility(View.VISIBLE);
            } else if ("pending".equals(club.getStatus())) {
                ((TextView) btnJoin).setText("Request sent");
                btnJoin.setEnabled(false);
                btnJoin.setVisibility(View.VISIBLE);
            } else {
                ((TextView) btnJoin).setText("Join");
                btnJoin.setEnabled(true);
                btnJoin.setVisibility(View.VISIBLE);
            }

            btnJoin.setOnClickListener(v -> {
                if (listener != null) listener.onJoinClick(club);
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClubClick(club);
            });
        }
    }
}
