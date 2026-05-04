package com.grouprace.feature.profile.ui.activities;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.grouprace.core.common.DateUtils;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Record;
import com.grouprace.feature.profile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileRecordAdapter extends RecyclerView.Adapter<ProfileRecordAdapter.RecordViewHolder> {
    public interface Listener {
        void onRecordClicked(Record record);
    }

    private final Listener listener;
    private final List<Record> records = new ArrayList<>();

    public ProfileRecordAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(@Nullable List<Record> items) {
        records.clear();
        if (items != null) {
            records.addAll(items);
        }
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return records.isEmpty();
    }

    public int getRecordCount() {
        return records.size();
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record currentRecord = records.get(position);

        holder.start.setText(DateUtils.formatStartTime(currentRecord.getStartTime()));
        holder.speed.setText(String.format(Locale.getDefault(), "%.1f km/h", currentRecord.getSpeed()));
        holder.distance.setText(String.format(Locale.getDefault(), "%.2f km", currentRecord.getDistance()));
        holder.duration.setText(TimeUtils.formatDuration(currentRecord.getDuration()));
        holder.activityType.setText(currentRecord.getTitle());

        Glide.with(holder.itemView.getContext()).clear(holder.ivIcon);
        holder.ivIcon.setImageDrawable(null);

        String imageUrl = currentRecord.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            holder.pbLoading.setVisibility(View.VISIBLE);

            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.pbLoading.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.pbLoading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(holder.ivIcon);
        } else {
            holder.pbLoading.setVisibility(View.GONE);
            holder.ivIcon.setImageDrawable(null);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecordClicked(currentRecord);
            }
        });
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class RecordViewHolder extends RecyclerView.ViewHolder {
        final TextView start;
        final TextView speed;
        final TextView distance;
        final TextView duration;
        final TextView activityType;
        final ImageView ivIcon;
        final ProgressBar pbLoading;

        RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.tv_start_time);
            speed = itemView.findViewById(R.id.tv_speed);
            distance = itemView.findViewById(R.id.distance);
            duration = itemView.findViewById(R.id.duration);
            activityType = itemView.findViewById(R.id.tv_activity_type);
            ivIcon = itemView.findViewById(R.id.iv_image_record);
            pbLoading = itemView.findViewById(R.id.pb_image_loading);
        }
    }
}
