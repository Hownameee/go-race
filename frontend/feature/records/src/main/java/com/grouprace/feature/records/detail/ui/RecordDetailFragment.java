package com.grouprace.feature.records.detail.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.grouprace.core.common.DateUtils;
import com.grouprace.core.common.TimeUtils;
import com.grouprace.feature.records.R;

import java.util.Locale;

public class RecordDetailFragment extends Fragment {

    private static final String ARG_ACTIVITY_TYPE = "arg_activity_type";
    private static final String ARG_START_TIME = "arg_start_time";
    private static final String ARG_DISTANCE = "arg_distance";
    private static final String ARG_AVG_SPEED = "arg_avg_speed";
    private static final String ARG_HEART_RATE = "arg_heart_rate";
    private static final String ARG_CALORIES = "arg_calories";
    private static final String ARG_DURATION = "arg_duration";
    private static final String ARG_ROUTE_URL = "arg_route_url";
    
    private static final String BASE_IMAGE_URL = "http://10.0.2.2:5000";

    private ImageView ivRoutePreview;
    private TextView tvActivityTitle;
    private TextView tvStartTime;
    private TextView tvDistance;
    private TextView tvAvgSpeed;
    private TextView tvHeartRate;
    private TextView tvCalories;
    private TextView tvDuration;

    public RecordDetailFragment() {
        super(R.layout.fragment_detail_record);
    }

    public static RecordDetailFragment newInstance(String activityType, String startTime, String distance, String avgSpeed, String heartRate, String calories, int duration, String routeUrl) {

        RecordDetailFragment fragment = new RecordDetailFragment();
        Bundle args = new Bundle();

        args.putString(ARG_ACTIVITY_TYPE, activityType);
        args.putString(ARG_START_TIME, DateUtils.formatStartTime(startTime));
        args.putString(ARG_DISTANCE, distance);
        args.putString(ARG_AVG_SPEED, avgSpeed);
        args.putString(ARG_HEART_RATE, heartRate);
        args.putString(ARG_CALORIES, calories);
        args.putString(ARG_ROUTE_URL, routeUrl);

        args.putString(ARG_DURATION, TimeUtils.formatDuration(duration));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivRoutePreview = view.findViewById(R.id.iv_route_preview);
        tvActivityTitle = view.findViewById(R.id.tv_activity_title);
        tvStartTime = view.findViewById(R.id.tv_start_time);
        tvDistance = view.findViewById(R.id.tv_distance);
        tvAvgSpeed = view.findViewById(R.id.tv_avg_speed);
        tvHeartRate = view.findViewById(R.id.tv_heart_rate);
        tvCalories = view.findViewById(R.id.tv_calories);
        tvDuration = view.findViewById(R.id.tv_duration);

        if (getArguments() != null) {
            tvActivityTitle.setText(getArguments().getString(ARG_ACTIVITY_TYPE));
            tvStartTime.setText(getArguments().getString(ARG_START_TIME));
            tvDistance.setText(getArguments().getString(ARG_DISTANCE));
            tvAvgSpeed.setText(getArguments().getString(ARG_AVG_SPEED));
            tvHeartRate.setText(getArguments().getString(ARG_HEART_RATE));
            tvCalories.setText(getArguments().getString(ARG_CALORIES));
            tvDuration.setText(getArguments().getString(ARG_DURATION));
            
            String routeUrl = getArguments().getString(ARG_ROUTE_URL);
            if (routeUrl != null && !routeUrl.isEmpty()) {
                String fullUrl = BASE_IMAGE_URL + routeUrl;
                Glide.with(this)
                        .load(fullUrl)
                        .placeholder(R.color.icon_background)
                        .into(ivRoutePreview);
            }
        }
    }
}
