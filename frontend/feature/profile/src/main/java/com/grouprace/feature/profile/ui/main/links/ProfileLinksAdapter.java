package com.grouprace.feature.profile.ui.main.links;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.grouprace.feature.profile.R;

import java.util.ArrayList;
import java.util.List;

public class ProfileLinksAdapter extends RecyclerView.Adapter<ProfileLinksAdapter.LinkViewHolder> {
    public interface Listener {
        void onLinkClicked(ProfileLinkItem item);
    }

    private final Listener listener;
    private final List<ProfileLinkItem> items = new ArrayList<>();

    public ProfileLinksAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<ProfileLinkItem> nextItems) {
        items.clear();
        if (nextItems != null) {
            items.addAll(nextItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        holder.bind(items.get(position), position == items.size() - 1, listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        private final View row;
        private final ImageView icon;
        private final TextView title;
        private final TextView subtitle;
        private final View divider;

        LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            row = itemView.findViewById(R.id.profile_link_row);
            icon = itemView.findViewById(R.id.profile_link_icon);
            title = itemView.findViewById(R.id.profile_link_title);
            subtitle = itemView.findViewById(R.id.profile_link_subtitle);
            divider = itemView.findViewById(R.id.profile_link_divider);
        }

        void bind(ProfileLinkItem item, boolean isLast, Listener listener) {
            icon.setImageResource(item.getIconResId());
            title.setText(item.getTitle());
            if (item.getSubtitle() == null || item.getSubtitle().trim().isEmpty()) {
                subtitle.setVisibility(View.GONE);
            } else {
                subtitle.setVisibility(View.VISIBLE);
                subtitle.setText(item.getSubtitle());
            }
            divider.setVisibility(isLast ? View.GONE : View.VISIBLE);
            row.setOnClickListener(v -> listener.onLinkClicked(item));
        }
    }
}
