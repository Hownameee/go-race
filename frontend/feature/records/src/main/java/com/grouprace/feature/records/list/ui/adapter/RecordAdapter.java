package com.grouprace.feature.records.list.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.DateUtils;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.core.model.Record;
import com.grouprace.feature.records.R;

import java.util.List;
import java.util.Locale;

public class RecordAdapter extends ArrayAdapter<Record> {

    private static final String BASE_IMAGE_URL = "http://10.0.2.2:5000";

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
            ImageView ivIcon = convertView.findViewById(R.id.iv_icon);

            start.setText(DateUtils.formatStartTime(currentRecord.getStartTime()));
            speed.setText(String.format(Locale.getDefault(), "%.1f km/h", currentRecord.getSpeed()));
            distance.setText(String.format(Locale.getDefault(), "%.2f km", currentRecord.getDistance()));

            duration.setText(TimeUtils.formatDuration(currentRecord.getDuration()));

            activityType.setText(currentRecord.getActivityType());
            
            String routeUrl = currentRecord.getRouteUrl();
            if (routeUrl != null && !routeUrl.isEmpty()) {
                String fullUrl = BASE_IMAGE_URL + routeUrl;
                Glide.with(getContext())
                        .load(fullUrl)
                        .placeholder(R.color.icon_background)
                        .centerCrop()
                        .into(ivIcon);
            }
        }

        return convertView;
    }
}
