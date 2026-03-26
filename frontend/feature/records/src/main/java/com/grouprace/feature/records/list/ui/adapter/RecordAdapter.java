package com.grouprace.feature.records.list.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

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

import java.util.List;
import java.util.Locale;

public class RecordAdapter extends ArrayAdapter<Record> {
    public RecordAdapter(Context context, List<Record> records) {
        super(context, R.layout.item_record, records);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record currentRecord = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_record, parent, false);
        }

        if (currentRecord != null) {
            TextView start = convertView.findViewById(R.id.tv_start_time);
            TextView speed = convertView.findViewById(R.id.tv_speed);
            TextView distance = convertView.findViewById(R.id.distance);
            TextView duration = convertView.findViewById(R.id.duration);
            TextView activityType = convertView.findViewById(R.id.tv_activity_type);
            ImageView ivIcon = convertView.findViewById(R.id.iv_image_record);
            ProgressBar pbLoading = convertView.findViewById(R.id.pb_image_loading);

            start.setText(DateUtils.formatStartTime(currentRecord.getStartTime()));
            speed.setText(String.format(Locale.getDefault(), "%.1f km/h", currentRecord.getSpeed()));
            distance.setText(String.format(Locale.getDefault(), "%.2f km", currentRecord.getDistance()));

            duration.setText(TimeUtils.formatDuration(currentRecord.getDuration()));

            activityType.setText(currentRecord.getActivityType());
            
            String imageUrl = currentRecord.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                pbLoading.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(imageUrl)
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
                        .into(ivIcon);
            } else {
                pbLoading.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
