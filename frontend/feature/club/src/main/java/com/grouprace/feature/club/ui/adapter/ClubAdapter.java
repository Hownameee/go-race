package com.grouprace.feature.club.ui.adapter;

import android.text.TextUtils;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

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
                return oldItem.getName().equals(newItem.getName());
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
        boolean isExpanded = false;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.text_club_name);
            tvMembers = itemView.findViewById(R.id.text_club_members);
            tvPosts = itemView.findViewById(R.id.text_club_posts);
            tvDesc = itemView.findViewById(R.id.text_club_description);
            btnExpand = itemView.findViewById(R.id.button_expand);
        }

        public void bind(Club club, OnClubClickListener listener) {
            tvName.setText(club.getName());
            tvMembers.setText(club.getMemberCount() + " Members");
            tvPosts.setText(club.getPostCount() + " Posts");
            tvDesc.setText(club.getDescription());

            isExpanded = false;
            tvDesc.setMaxLines(1);
            tvDesc.setEllipsize(TextUtils.TruncateAt.END);
            btnExpand.setRotation(0f);
            btnExpand.setVisibility(View.GONE);


            tvDesc.post(() -> {
                if (!isExpanded) {
                    int lineCount = tvDesc.getLineCount();
                    if (lineCount > 0 && tvDesc.getLayout() != null) {
                        if (tvDesc.getLayout().getEllipsisCount(lineCount - 1) > 0) {
                            btnExpand.setVisibility(View.VISIBLE);
                        } else {
                            btnExpand.setVisibility(View.GONE);
                        }
                    } else {
                        btnExpand.setVisibility(View.GONE);
                    }
                }
            });

            btnExpand.setOnClickListener(v -> {
                TransitionManager.beginDelayedTransition((ViewGroup) itemView);
                isExpanded = !isExpanded;
                if (isExpanded) {
                    tvDesc.setMaxLines(Integer.MAX_VALUE);
                    tvDesc.setEllipsize(null);
                    btnExpand.animate().rotation(180f).setDuration(350).start();
                } else {
                    tvDesc.setMaxLines(1);
                    tvDesc.setEllipsize(TextUtils.TruncateAt.END);
                    btnExpand.animate().rotation(0f).setDuration(350).start();
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onClubClick(club);
            });
        }
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMembers;
        View btnJoin;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.text_club_name);
            tvMembers = itemView.findViewById(R.id.text_club_members);
            btnJoin = itemView.findViewById(R.id.button_join);
        }

        public void bind(Club club, OnClubClickListener listener) {
            tvName.setText(club.getName());
            tvMembers.setText(club.getMemberCount() + " Members");
            if (club.isJoined()) {
                btnJoin.setVisibility(View.GONE);
            } else {
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
