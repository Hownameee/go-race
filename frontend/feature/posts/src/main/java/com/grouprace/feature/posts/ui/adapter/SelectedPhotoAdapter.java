package com.grouprace.feature.posts.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.grouprace.feature.posts.R;

public class SelectedPhotoAdapter extends ListAdapter<String, SelectedPhotoAdapter.ViewHolder> {

    private final OnPhotoRemoveListener listener;

    public interface OnPhotoRemoveListener {
        void onRemove(String uri);
    }

    public SelectedPhotoAdapter(OnPhotoRemoveListener listener) {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uri = getItem(position);
        Glide.with(holder.itemView.getContext())
                .load(uri)
                .into(holder.ivPhoto);
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(uri));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        View btnRemove;

        ViewHolder(View view) {
            super(view);
            ivPhoto = view.findViewById(R.id.iv_photo);
            btnRemove = view.findViewById(R.id.btn_remove);
        }
    }
}
