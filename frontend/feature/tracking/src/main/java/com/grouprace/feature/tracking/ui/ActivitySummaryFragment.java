package com.grouprace.feature.tracking.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.feature.tracking.R;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivitySummaryFragment extends Fragment {

    private static final String ARG_ACTIVITY_ID = "activityId";

    private MapView mapView;
    private ActivityDetailViewModel viewModel;

    public static ActivitySummaryFragment newInstance(long activityId) {
        ActivitySummaryFragment fragment = new ActivitySummaryFragment();
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
        return inflater.inflate(R.layout.fragment_activity_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);
        TextView tvDistance = view.findViewById(R.id.tv_distance);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvPace = view.findViewById(R.id.tv_pace);
        EditText etTitle = view.findViewById(R.id.et_title);
        Button btnSave = view.findViewById(R.id.btn_save);

        viewModel = new ViewModelProvider(this).get(ActivityDetailViewModel.class);

        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            RouteMapHelper.setupRouteLayer(style);

            viewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {
                RouteMapHelper.drawRoute(style, points);
                RouteMapHelper.zoomToFitRoute(mapView, points);
            });
        });

        viewModel.getFormattedDistance().observe(getViewLifecycleOwner(), tvDistance::setText);
        viewModel.getFormattedTime().observe(getViewLifecycleOwner(), tvTime::setText);
        viewModel.getFormattedPace().observe(getViewLifecycleOwner(), tvPace::setText);

        viewModel.getRecord().observe(getViewLifecycleOwner(), record -> {
            if (record != null && etTitle.getText().toString().isEmpty()) {
                etTitle.setText(record.getTitle());
            }
        });

        viewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Loading) {
                btnSave.setEnabled(false);
                btnSave.setText("Saving...");
            } else if (result instanceof Result.Success) {
                Toast.makeText(requireContext(), "Activity saved!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else if (result instanceof Result.Error) {
                btnSave.setEnabled(true);
                btnSave.setText("Save");
                String error = ((Result.Error<Void>) result).message;
                Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_LONG).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty()) {
                title = "Activity";
            }
            viewModel.saveTitle(title);
        });
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
