package com.grouprace.feature.tracking.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.feature.tracking.R;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Reusable fragment to display a completed activity's route on a map.
 * Teammates can use: ActivityDetailFragment.newInstance(activityId)
 */
@AndroidEntryPoint
public class ActivityDetailFragment extends Fragment {

    private static final String ARG_ACTIVITY_ID = "activityId";

    private MapView mapView;
    private ActivityDetailViewModel viewModel;
    private PolylineAnnotationManager polylineManager;

    public static ActivityDetailFragment newInstance(long activityId) {
        ActivityDetailFragment fragment = new ActivityDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ACTIVITY_ID, activityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);
        TextView tvDistance = view.findViewById(R.id.tv_distance);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvPace = view.findViewById(R.id.tv_pace);
 
        viewModel = new ViewModelProvider(this).get(ActivityDetailViewModel.class);
 
        mapView.getMapboxMap().loadStyle(Style.STANDARD, style -> {
            polylineManager = RouteMapHelper.createPolylineManager(mapView);
 
            viewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {
                RouteMapHelper.drawPolyline(polylineManager, points);
                RouteMapHelper.zoomToFitRoute(mapView, points);
            });
        });
 
        viewModel.getFormattedDistance().observe(getViewLifecycleOwner(), tvDistance::setText);
        viewModel.getFormattedTime().observe(getViewLifecycleOwner(), tvTime::setText);
        viewModel.getFormattedPace().observe(getViewLifecycleOwner(), tvPace::setText);
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }
}
