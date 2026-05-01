package com.grouprace.feature.tracking.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.core.common.result.Result;
import com.grouprace.feature.tracking.R;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ActivitySummaryFragment extends Fragment {

    private static final String ARG_ACTIVITY_ID = "activityId";

    private MapView mapView;
    private ActivityDetailViewModel detailViewModel;
    private TrackingViewModel trackingViewModel;
    private long activityId;

    public static ActivitySummaryFragment newInstance(long activityId) {
        ActivitySummaryFragment fragment = new ActivitySummaryFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ACTIVITY_ID, activityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activityId = getArguments().getLong(ARG_ACTIVITY_ID);
        }
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
        CheckBox cbSaveAsRoute = view.findViewById(R.id.cb_save_as_route);
        Button btnSave = view.findViewById(R.id.btn_save);

        trackingViewModel = new ViewModelProvider(requireActivity()).get(TrackingViewModel.class);
        detailViewModel = new ViewModelProvider(this).get(ActivityDetailViewModel.class);

        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            RouteMapHelper.setupRouteLayer(style);

            if (activityId == -1) {
                // Fresh run: display points from trackingViewModel
                List<Point> points = trackingViewModel.getFinalPolylinePoints();
                RouteMapHelper.drawRoute(style, points);
                RouteMapHelper.zoomToFitRoute(mapView, points);
            } else {
                // Existing record: display points from detailViewModel
                detailViewModel.getRoutePoints().observe(getViewLifecycleOwner(), points -> {
                    RouteMapHelper.drawRoute(style, points);
                    RouteMapHelper.zoomToFitRoute(mapView, points);
                });
            }
        });

        if (activityId == -1) {
            // Fresh run: setup UI from tracking metrics
            tvDistance.setText(String.format(Locale.US, "%.2f", trackingViewModel.getFinalDistKm()));

            long elapsed = trackingViewModel.getFinalElapsed();
            long totalSeconds = elapsed / 1000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            tvTime.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));

            if (trackingViewModel.getFinalDistKm() > 0 && elapsed > 0) {
                double speedKmH = trackingViewModel.getFinalDistKm() / (elapsed / 3600000.0);
                tvPace.setText(String.format(Locale.US, "%.1f", speedKmH));
            } else {
                tvPace.setText("0.0");
            }

            etTitle.setText("New Activity");

            // Handle save result via trackingViewModel
            trackingViewModel.getFinishedActivityId().observe(getViewLifecycleOwner(), newId -> {
                if (newId != null) {
                    Toast.makeText(requireContext(), "Activity saved!", Toast.LENGTH_SHORT).show();
                    trackingViewModel.resetFinishedActivityId();
                    requireActivity().getSupportFragmentManager()
                            .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
            });
        } else {
            // Existing record: setup UI from detailViewModel
            detailViewModel.getFormattedDistance().observe(getViewLifecycleOwner(), tvDistance::setText);
            detailViewModel.getFormattedTime().observe(getViewLifecycleOwner(), tvTime::setText);
            detailViewModel.getFormattedPace().observe(getViewLifecycleOwner(), tvPace::setText);

            detailViewModel.getRecord().observe(getViewLifecycleOwner(), record -> {
                if (record != null && etTitle.getText().toString().isEmpty()) {
                    etTitle.setText(record.getTitle());
                }
            });

            detailViewModel.getRouteSavedMessage().observe(getViewLifecycleOwner(), msg -> {
                if (msg == null)
                    return;
                detailViewModel.clearRouteSavedMessage();
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
            });

            detailViewModel.getSaveResult().observe(getViewLifecycleOwner(), result -> {
                if (result instanceof Result.Loading) {
                    btnSave.setEnabled(false);
                    btnSave.setText("Saving...");
                } else if (result instanceof Result.Success) {
                    Toast.makeText(requireContext(), "Activity updated!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else if (result instanceof Result.Error) {
                    btnSave.setEnabled(true);
                    btnSave.setText("Save");
                    Toast.makeText(requireContext(), "Error: " + ((Result.Error<Void>) result).message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            if (title.isEmpty())
                title = "Activity";

            if (activityId == -1) {
                btnSave.setEnabled(false);
                btnSave.setText("Saving...");
                trackingViewModel.confirmSave(title, cbSaveAsRoute.isChecked());
            } else {
                if (cbSaveAsRoute.isChecked())
                    detailViewModel.saveAsRoute(title);
                detailViewModel.saveTitle(title);
            }
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
