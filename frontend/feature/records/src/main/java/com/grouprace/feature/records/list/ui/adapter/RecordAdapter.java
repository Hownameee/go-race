package com.grouprace.feature.records.list.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.grouprace.core.common.DateUtils;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Record;
import com.grouprace.feature.records.R;
import com.grouprace.feature.records.list.ui.RecordsViewModel;

import java.util.List;
import java.util.Locale;

public class RecordAdapter extends ArrayAdapter<Record> {

    private final RecordsViewModel viewModel;

    public RecordAdapter(Context context, List<Record> records, RecordsViewModel viewModel) {
        super(context, R.layout.item_record, records);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        Record currentRecord = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
            holder = new ViewHolder();
            holder.start = convertView.findViewById(R.id.tv_start_time);
            holder.speed = convertView.findViewById(R.id.tv_speed);
            holder.distance = convertView.findViewById(R.id.distance);
            holder.duration = convertView.findViewById(R.id.duration);
            holder.activityType = convertView.findViewById(R.id.tv_activity_type);
            holder.ivIcon = convertView.findViewById(R.id.iv_image_record);
            holder.pbLoading = convertView.findViewById(R.id.pb_image_loading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (currentRecord != null) {
            holder.start.setText(DateUtils.formatStartTime(currentRecord.getStartTime()));
            holder.speed.setText(String.format(Locale.getDefault(), "%.1f km/h", currentRecord.getSpeed()));
            holder.distance.setText(String.format(Locale.getDefault(), "%.2f km", currentRecord.getDistance()));
            holder.duration.setText(TimeUtils.formatDuration(currentRecord.getDuration()));
            holder.activityType.setText(currentRecord.getTitle());

            Glide.with(getContext()).clear(holder.ivIcon);
            holder.ivIcon.setImageDrawable(null);

            String imageUrl = currentRecord.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                holder.pbLoading.setVisibility(View.VISIBLE);

                Glide.with(getContext()).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("adapter", "Load failed: " + currentRecord.getRecordId());
                        viewModel.updateRecord(currentRecord.getRecordId());
                        holder.pbLoading.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.pbLoading.setVisibility(View.GONE);
                        return false;
                    }
                }).centerCrop().into(holder.ivIcon);
            } else {
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivIcon.setImageDrawable(null);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView start;
        TextView speed;
        TextView distance;
        TextView duration;
        TextView activityType;
        ImageView ivIcon;
        ProgressBar pbLoading;
    }
}