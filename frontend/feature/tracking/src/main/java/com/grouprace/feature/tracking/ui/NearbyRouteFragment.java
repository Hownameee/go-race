package com.grouprace.feature.tracking.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.grouprace.core.model.NearbyPlace;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.feature.records.compare.ui.CompareRecordsFragment;
import com.grouprace.feature.records.list.ui.RecordsFragment;
import com.grouprace.feature.tracking.R;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.viewport.ViewportPlugin;
import com.mapbox.maps.plugin.viewport.ViewportUtils;
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NearbyRouteFragment extends Fragment {

    private MapView mapView;
    private Style mapStyle;
    private NearbyRouteViewModel viewModel;

    private LinearLayout rowLoading;
    private LinearLayout rowIdle;
    private LinearLayout rowRouteReady;
    private LinearLayout tvRouteInfoCard;
    private TextView tvRouteInfo;
    private TextView btnCancel;
    private MaterialButton btnRecords;
    private MaterialButton btnCompare;
    private MaterialButton btnFreeRun;
    private MaterialButton btnFindNearby;
    private MaterialButton btnReplan;
    private MaterialButton btnStartRun;

    private boolean locationCaptured = false;

    private final OnIndicatorPositionChangedListener positionListener = point -> {
        if (!locationCaptured) {
            locationCaptured = true;
            viewModel.setUserLocation(point.longitude(), point.latitude());
            // Location captured — unregister to stop firing on every GPS tick
            LocationComponentPlugin plugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
            if (plugin != null) plugin.removeOnIndicatorPositionChangedListener(this.positionListener);
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
        return inflater.inflate(R.layout.fragment_nearby_route, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = view.findViewById(R.id.map_view);
        rowLoading = view.findViewById(R.id.row_loading);
        rowIdle = view.findViewById(R.id.row_idle);
        rowRouteReady = view.findViewById(R.id.row_route_ready);
        tvRouteInfoCard = view.findViewById(R.id.tv_route_info_card);
        tvRouteInfo = view.findViewById(R.id.tv_route_info);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnRecords = view.findViewById(R.id.btn_records);
        btnCompare = view.findViewById(R.id.btn_compare);
        btnFreeRun = view.findViewById(R.id.btn_free_run);
        btnFindNearby = view.findViewById(R.id.btn_find_nearby);
        btnReplan = view.findViewById(R.id.btn_replan);
        btnStartRun = view.findViewById(R.id.btn_start_run);

        viewModel = new ViewModelProvider(this).get(NearbyRouteViewModel.class);

        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            mapStyle = style;
            RouteMapHelper.setupPlannedRouteLayer(style);
            requestLocationPermission();
        });

        observeViewModel();
        setupButtons();
        setupBackPress();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocationTracking();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void enableLocationTracking() {
        LocationComponentPlugin locationPlugin =
                mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (locationPlugin != null) {
            locationPlugin.updateSettings(settings -> {
                settings.setEnabled(true);
                settings.setPuckBearingEnabled(true);
                return null;
            });
            locationPlugin.addOnIndicatorPositionChangedListener(positionListener);
        }

        // Follow puck
        ViewportPlugin viewport = ViewportUtils.getViewport(mapView);
        viewport.transitionTo(
                viewport.makeFollowPuckViewportState(
                        new FollowPuckViewportStateOptions.Builder()
                                .zoom(14.0).pitch(0.0).bearing(null).build()),
                viewport.makeImmediateViewportTransition(), null);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            boolean loading = state == NearbyRouteViewModel.UiState.LOADING;
            boolean routeReady = state == NearbyRouteViewModel.UiState.ROUTE_READY;

            rowLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
            rowIdle.setVisibility(!loading && !routeReady ? View.VISIBLE : View.GONE);
            rowRouteReady.setVisibility(routeReady ? View.VISIBLE : View.GONE);
        });

        viewModel.getNearbyPlaces().observe(getViewLifecycleOwner(), places -> {
            if (places == null) return;
            viewModel.clearNearbyPlaces(); // consume so it doesn't re-fire on rotation
            if (!places.isEmpty()) {
                showPlacePickerDialog(places);
            } else {
                Toast.makeText(requireContext(), "No places found nearby", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getPlannedRoute().observe(getViewLifecycleOwner(), route -> {
            if (route != null) {
                mapView.getMapboxMap().getStyle(style ->
                        RouteMapHelper.drawPlannedRoute(style, route.coordinates));
                tvRouteInfo.setText(route.getFormattedDistance()
                        + "  ·  " + route.getFormattedDuration()
                        + "  ·  " + route.getDifficulty());
                tvRouteInfoCard.setVisibility(View.VISIBLE);
                RouteMapHelper.zoomToFitCoords(mapView, route.coordinates);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg == null || msg.isEmpty()) return;
            viewModel.clearError(); // consume so it doesn't re-fire on rotation
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupButtons() {
        btnFreeRun.setOnClickListener(v -> navigateToTracking(null));

        btnFindNearby.setOnClickListener(v -> {
            if (!locationCaptured) {
                Toast.makeText(requireContext(),
                        "Waiting for location...", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.findNearbyPlaces();
        });

        btnCancel.setOnClickListener(v -> viewModel.cancelLoading());

        btnRecords.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getContainerId(), new RecordsFragment())
                        .addToBackStack(null)
                        .commit());

        btnCompare.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getContainerId(), new CompareRecordsFragment())
                        .addToBackStack(null)
                        .commit());

        btnReplan.setOnClickListener(v -> {
            viewModel.cancelLoading();
            RouteMapHelper.clearPlannedRoute(mapStyle);
            tvRouteInfoCard.setVisibility(View.GONE);
            // Show cached places list if available — skip re-fetching
            List<NearbyPlace> cached = viewModel.getLastFetchedPlaces();
            if (cached != null && !cached.isEmpty()) {
                showPlacePickerDialog(cached);
            }
        });

        btnStartRun.setOnClickListener(v -> {
            PlannedRoute route = viewModel.getPlannedRoute().getValue();
            navigateToTracking(route);
        });
    }

    private void showPlacePickerDialog(List<NearbyPlace> places) {
        String[] names = new String[places.size()];
        for (int i = 0; i < places.size(); i++) {
            names[i] = places.get(i).name + "  (" + places.get(i).getFormattedDistance() + ")";
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose a destination")
                .setItems(names, (dialog, which) -> {
                    viewModel.generateRoute(places.get(which));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToTracking(PlannedRoute route) {
        viewModel.reset(); // full reset — clears places cache too
        RouteMapHelper.clearPlannedRoute(mapStyle);
        tvRouteInfoCard.setVisibility(View.GONE);

        Fragment fragment = route != null
                ? TrackingFragment.newInstance(route)
                : new TrackingFragment();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        NearbyRouteViewModel.UiState state = viewModel.getUiState().getValue();
                        if (state == NearbyRouteViewModel.UiState.LOADING) {
                            viewModel.cancelLoading();
                        } else if (state == NearbyRouteViewModel.UiState.ROUTE_READY) {
                            viewModel.reset();
                            RouteMapHelper.clearPlannedRoute(mapStyle);
                            tvRouteInfoCard.setVisibility(View.GONE);
                        } else {
                            // IDLE — let system handle (exit / pop back stack)
                            setEnabled(false);
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });
    }

    private int getContainerId() {
        return ((ViewGroup) requireView().getParent()).getId();
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
        LocationComponentPlugin locationPlugin =
                mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (locationPlugin != null) {
            locationPlugin.removeOnIndicatorPositionChangedListener(positionListener);
        }
        mapView.onDestroy();
        super.onDestroyView();
    }
}
