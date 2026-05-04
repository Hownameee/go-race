package com.grouprace.feature.posts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.grouprace.feature.posts.R;
import java.util.ArrayList;
import java.util.List;

public class PostMediaAdapter extends RecyclerView.Adapter<PostMediaAdapter.ViewHolder> {

    private List<String> urls = new ArrayList<>();

    public void submitList(List<String> newUrls) {
        this.urls = newUrls;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_media, parent, false);
        // Ensure item takes full width of the RecyclerView
        view.setLayoutParams(new ViewGroup.LayoutParams(parent.getWidth(), parent.getHeight()));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(urls.get(position))
                .centerCrop()
                .into(holder.ivMedia);
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMedia;

        ViewHolder(View view) {
            super(view);
            ivMedia = view.findViewById(R.id.iv_media);
        }
    }
}
