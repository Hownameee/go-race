package com.grouprace.feature.tracking.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouprace.core.model.PlannedRoute;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.feature.tracking.R;
import com.grouprace.feature.records.compare.ui.CompareRecordsFragment;
import com.grouprace.feature.records.list.ui.RecordsFragment;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.viewport.ViewportPlugin;
import com.mapbox.maps.plugin.viewport.ViewportStatus;
import com.mapbox.maps.plugin.viewport.ViewportStatusObserver;
import com.mapbox.maps.plugin.viewport.ViewportUtils;
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions;
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions;
import com.mapbox.maps.plugin.viewport.data.ViewportStatusChangeReason;
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState;

import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrackingFragment extends Fragment {

    private static final String ARG_PLANNED_ROUTE = "planned_route";

    /** Use this factory when launching with a pre-planned route (ghost overlay). */
    public static TrackingFragment newInstance(PlannedRoute route) {
        TrackingFragment fragment = new TrackingFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLANNED_ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    private MapView mapView;
    private Style mapStyle;
    private TrackingViewModel viewModel;
    private ViewportPlugin viewport;
    private FollowPuckViewportState followState;
    private boolean hasPlannedRoute;

    // Stats views
    private LinearLayout statsBar;
    private TextView tvDistance;
    private TextView tvTime;
    private TextView tvPace;

    // Buttons
    private Button btnStart;
    private Button btnPause;
    private Button btnResume;
    private Button btnFinish;
    private Button btnRecords;
    private Button btnCompare;

    private static final long SNAP_BACK_DELAY_MS = 1000;
    private final Handler snapBackHandler = new Handler(Looper.getMainLooper());
    private final Runnable snapBackRunnable = () -> {
        if (viewport != null && followState != null) {
            viewport.transitionTo(followState,
                    viewport.makeDefaultViewportTransition(
                            new DefaultViewportTransitionOptions.Builder()
                                    .maxDurationMs(1500)
                                    .build()),
                    null);
        }
    };

    // When user pans the map, viewport goes Idle — snap back after a short delay
    private final ViewportStatusObserver statusObserver = (from, to, reason) -> {
        if (to instanceof ViewportStatus.Idle
                && reason.equals(ViewportStatusChangeReason.USER_INTERACTION)
                && followState != null) {
            snapBackHandler.removeCallbacks(snapBackRunnable);
            snapBackHandler.postDelayed(snapBackRunnable, SNAP_BACK_DELAY_MS);
        }
    };

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean granted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (Boolean.TRUE.equals(granted)) {
                    enableLocationTracking();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);
        statsBar = view.findViewById(R.id.stats_bar);
        tvDistance = view.findViewById(R.id.tv_distance);
        tvTime = view.findViewById(R.id.tv_time);
        tvPace = view.findViewById(R.id.tv_pace);
        btnStart = view.findViewById(R.id.btn_start);
        btnPause = view.findViewById(R.id.btn_pause);
        btnResume = view.findViewById(R.id.btn_resume);
        btnFinish = view.findViewById(R.id.btn_finish);
        btnRecords = view.findViewById(R.id.btn_records);
        btnCompare = view.findViewById(R.id.btn_compare);

        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            mapStyle = style;
            // Planned ghost route drawn first (grey), live route on top (red)
            RouteMapHelper.setupPlannedRouteLayer(style);
            RouteMapHelper.setupRouteLayer(style);

            PlannedRoute plannedRoute = null;
            if (getArguments() != null) {
                plannedRoute = (PlannedRoute) getArguments().getSerializable(ARG_PLANNED_ROUTE);
            }
            hasPlannedRoute = plannedRoute != null;
            if (hasPlannedRoute) {
                RouteMapHelper.drawPlannedRoute(style, plannedRoute.coordinates);
            }

            requestLocationPermission();
        });

        viewModel = new ViewModelProvider(this).get(TrackingViewModel.class);

        observeViewModel();
        setupButtons();
    }

    // --- Setup ---

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            enableLocationTracking();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void enableLocationTracking() {
        // Enable the blue location puck
        LocationComponentPlugin locationPlugin =
                mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (locationPlugin != null) {
            locationPlugin.updateSettings(settings -> {
                settings.setEnabled(true);
                settings.setPuckBearingEnabled(true);
                return null;
            });
        }

        // Use Viewport API to follow location puck
        viewport = ViewportUtils.getViewport(mapView);

        followState = viewport.makeFollowPuckViewportState(
                new FollowPuckViewportStateOptions.Builder()
                        .zoom(15.0)
                        .pitch(0.0)
                        .bearing(null)
                        .build()
        );

        // Start following immediately
        viewport.transitionTo(followState, viewport.makeImmediateViewportTransition(), null);

        // Re-follow after user pans the map
        viewport.addStatusObserver(statusObserver);
    }

    // --- Observe ViewModel ---

    private void observeViewModel() {
        viewModel.getTrackingState().observe(getViewLifecycleOwner(), this::updateButtonVisibility);

        viewModel.getPolylinePoints().observe(getViewLifecycleOwner(), this::drawPolyline);

        viewModel.getDistanceKm().observe(getViewLifecycleOwner(), distance ->
                tvDistance.setText(String.format(Locale.US, "%.2f", distance)));

        viewModel.getElapsedTimeMs().observe(getViewLifecycleOwner(), this::updateTimeDisplay);

        viewModel.getPace().observe(getViewLifecycleOwner(), paceText ->
                tvPace.setText(paceText));

        viewModel.getFinishedActivityId().observe(getViewLifecycleOwner(), activityId -> {
            if (activityId != null) {
                navigateToSummary(activityId);
                viewModel.resetFinishedActivityId();
            }
        });
    }

    // --- Button handling ---

    private void setupButtons() {
        btnStart.setOnClickListener(v -> viewModel.startTracking());
        btnPause.setOnClickListener(v -> viewModel.pauseTracking());
        btnResume.setOnClickListener(v -> viewModel.resumeTracking());
        btnFinish.setOnClickListener(v -> viewModel.finishTracking());
        btnRecords.setOnClickListener(v -> navigateToRecords());
        btnCompare.setOnClickListener(v -> navigateToCompare());
    }

    private void updateButtonVisibility(TrackingViewModel.TrackingState state) {
        boolean idle = state == TrackingViewModel.TrackingState.IDLE;
        boolean tracking = state == TrackingViewModel.TrackingState.TRACKING;
        boolean paused = state == TrackingViewModel.TrackingState.PAUSED;

        btnStart.setVisibility(idle ? View.VISIBLE : View.GONE);
        btnRecords.setVisibility(idle ? View.VISIBLE : View.GONE);
        btnCompare.setVisibility(idle ? View.VISIBLE : View.GONE);
        btnPause.setVisibility(tracking ? View.VISIBLE : View.GONE);
        btnResume.setVisibility(paused ? View.VISIBLE : View.GONE);
        btnFinish.setVisibility(!idle ? View.VISIBLE : View.GONE);
        statsBar.setVisibility(!idle ? View.VISIBLE : View.GONE);
    }

    // --- Map updates ---

    private void drawPolyline(List<Point> points) {
        RouteMapHelper.drawRoute(mapStyle, points);
    }

    private void updateTimeDisplay(Long millis) {
        if (millis == null) return;
        long totalSeconds = millis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        tvTime.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    // --- Navigation ---

    private void navigateToSummary(long activityId) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), ActivitySummaryFragment.newInstance(activityId))
                .addToBackStack(null)
                .commit();
    }

    private void navigateToRecords() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), new RecordsFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToCompare() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), new CompareRecordsFragment())
                .addToBackStack(null)
                .commit();
    }

    private int getContainerId() {
        return ((ViewGroup) requireView().getParent()).getId();
    }

    // --- Lifecycle ---

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
        snapBackHandler.removeCallbacks(snapBackRunnable);
        if (viewport != null) {
            viewport.removeStatusObserver(statusObserver);
        }
        mapView.onDestroy();
        super.onDestroyView();
    }
}
