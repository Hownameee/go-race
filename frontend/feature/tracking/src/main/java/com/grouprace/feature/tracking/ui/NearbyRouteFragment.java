package com.grouprace.feature.tracking.ui;

import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
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
    private MaterialCardView searchCard;
    private View dividerResults;
    private View touchDismissOverlay;
    private LinearLayout resultsContainer;
    private EditText etSearchDestination;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private boolean locationCaptured = false;

    private final OnIndicatorPositionChangedListener positionListener = point -> {
        if (!locationCaptured) {
            locationCaptured = true;
            viewModel.setUserLocation(point.longitude(), point.latitude());
            // Captured — stop firing on every GPS tick
            LocationComponentPlugin plugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
            if (plugin != null) plugin.removeOnIndicatorPositionChangedListener(this.positionListener);
        }
    };

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean granted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (Boolean.TRUE.equals(granted)) enableLocationTracking();
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
        bindViews(view);
        viewModel = new ViewModelProvider(this).get(NearbyRouteViewModel.class);

        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            mapStyle = style;
            RouteMapHelper.setupPlannedRouteLayer(style);
            requestLocationPermission();
        });

        observeViewModel();
        setupSearch();
        setupButtons();
        setupBackPress();
    }

    private void bindViews(View view) {
        mapView             = view.findViewById(R.id.map_view);
        rowLoading          = view.findViewById(R.id.row_loading);
        rowIdle             = view.findViewById(R.id.row_idle);
        rowRouteReady       = view.findViewById(R.id.row_route_ready);
        tvRouteInfoCard     = view.findViewById(R.id.tv_route_info_card);
        tvRouteInfo         = view.findViewById(R.id.tv_route_info);
        btnCancel           = view.findViewById(R.id.btn_cancel);
        btnRecords          = view.findViewById(R.id.btn_records);
        btnCompare          = view.findViewById(R.id.btn_compare);
        btnFreeRun          = view.findViewById(R.id.btn_free_run);
        btnFindNearby       = view.findViewById(R.id.btn_find_nearby);
        btnReplan           = view.findViewById(R.id.btn_replan);
        btnStartRun         = view.findViewById(R.id.btn_start_run);
        searchCard           = view.findViewById(R.id.search_card);
        dividerResults       = view.findViewById(R.id.divider_results);
        touchDismissOverlay  = view.findViewById(R.id.touch_dismiss_overlay);
        resultsContainer     = view.findViewById(R.id.results_container);
        etSearchDestination  = view.findViewById(R.id.et_search_destination);
    }

    // --- location ---

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
        ViewportPlugin viewport = ViewportUtils.getViewport(mapView);
        viewport.transitionTo(
                viewport.makeFollowPuckViewportState(
                        new FollowPuckViewportStateOptions.Builder()
                                .zoom(14.0).pitch(0.0).bearing(null).build()),
                viewport.makeImmediateViewportTransition(), null);
    }

    // --- observers ---

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            boolean loading   = state == NearbyRouteViewModel.UiState.LOADING;
            boolean routeReady = state == NearbyRouteViewModel.UiState.ROUTE_READY;
            boolean idle      = !loading && !routeReady;

            rowLoading.setVisibility(loading    ? View.VISIBLE : View.GONE);
            rowIdle.setVisibility(idle          ? View.VISIBLE : View.GONE);
            rowRouteReady.setVisibility(routeReady  ? View.VISIBLE : View.GONE);
            searchCard.setVisibility(routeReady ? View.GONE    : View.VISIBLE);
            tvRouteInfoCard.setVisibility(routeReady ? View.VISIBLE : View.GONE);

            if (loading)    clearInlineResults();
            if (routeReady) hideKeyboard();
        });

        viewModel.getNearbyPlaces().observe(getViewLifecycleOwner(), places -> {
            if (places == null) return;
            viewModel.clearNearbyPlaces(); // prevent re-show on rotation
            if (places.isEmpty()) {
                Toast.makeText(requireContext(), R.string.msg_no_places_found, Toast.LENGTH_SHORT).show();
            } else {
                showInlineResults(places);
            }
        });

        viewModel.getPlannedRoute().observe(getViewLifecycleOwner(), route -> {
            if (route == null) return;
            mapView.getMapboxMap().getStyle(style ->
                    RouteMapHelper.drawPlannedRoute(style, route.coordinates));
            tvRouteInfo.setText(route.getFormattedDistance()
                    + "  ·  " + route.getFormattedDuration()
                    + "  ·  " + route.getDifficulty());
            RouteMapHelper.zoomToFitCoords(mapView, route.coordinates);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg == null || msg.isEmpty()) return;
            viewModel.clearError(); // prevent re-show on rotation
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        });
    }

    // --- search ---

    private void setupSearch() {
        searchRunnable = () -> {
            String query = etSearchDestination.getText().toString().trim();
            if (!query.isEmpty()) viewModel.searchByQuery(query);
        };

        etSearchDestination.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                searchHandler.removeCallbacks(searchRunnable);
                if (s.length() < 2) {
                    clearInlineResults(); // clear stale results once query is too short
                } else if (locationCaptured) {
                    searchHandler.postDelayed(searchRunnable, 300);
                }
            }
        });

        // Return/Search key: dismiss if results are open, otherwise fire search immediately
        etSearchDestination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHandler.removeCallbacks(searchRunnable);
                if (resultsContainer.getVisibility() == View.VISIBLE) {
                    clearInlineResults();
                    etSearchDestination.clearFocus();
                    hideKeyboard();
                } else {
                    String query = etSearchDestination.getText().toString().trim();
                    if (query.length() >= 2 && locationCaptured) viewModel.searchByQuery(query);
                }
                return true;
            }
            return false;
        });

        // Tap outside the search card → dismiss results and keyboard
        touchDismissOverlay.setOnClickListener(v -> {
            clearInlineResults();
            etSearchDestination.clearFocus();
            hideKeyboard();
        });
    }

    // --- buttons ---

    private void setupButtons() {
        btnFreeRun.setOnClickListener(v -> navigateToTracking(null));

        btnFindNearby.setOnClickListener(v -> {
            if (!locationCaptured) {
                Toast.makeText(requireContext(), R.string.msg_waiting_for_location, Toast.LENGTH_SHORT).show();
                return;
            }
            etSearchDestination.setText(""); // clear typed query — we're switching to nearby discovery
            viewModel.findNearbyPlaces();
        });

        btnCancel.setOnClickListener(v -> viewModel.cancelLoading());

        btnRecords.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getContainerId(), new RecordsFragment())
                        .addToBackStack(null).commit());

        btnCompare.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(getContainerId(), new CompareRecordsFragment())
                        .addToBackStack(null).commit());

        btnReplan.setOnClickListener(v -> replan());

        btnStartRun.setOnClickListener(v ->
                navigateToTracking(viewModel.getPlannedRoute().getValue()));
    }

    // --- helpers ---

    /** Reset to IDLE and re-show the last result list so the user can pick a different place. */
    private void replan() {
        List<NearbyPlace> cached = viewModel.getLastFetchedPlaces();
        viewModel.reset(); // drives UI state via observer (hides route card, shows searchCard + rowIdle)
        RouteMapHelper.clearPlannedRoute(mapStyle);
        etSearchDestination.setText("");
        if (cached != null && !cached.isEmpty()) showInlineResults(cached);
    }

    private void showInlineResults(List<NearbyPlace> places) {
        resultsContainer.removeAllViews();

        int px12 = (int) (12 * getResources().getDisplayMetrics().density);
        TypedValue ripple = new TypedValue();
        requireContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, ripple, true);

        for (NearbyPlace place : places) {
            TextView item = new TextView(requireContext());
            item.setText(place.name + "  ·  " + place.getFormattedDistance());
            item.setTextColor(android.graphics.Color.WHITE);
            item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            item.setPadding(px12, px12, px12, px12);
            item.setBackgroundResource(ripple.resourceId);
            item.setOnClickListener(v -> {
                clearInlineResults();
                viewModel.generateRoute(place);
            });
            resultsContainer.addView(item);
        }

        dividerResults.setVisibility(View.VISIBLE);
        resultsContainer.setVisibility(View.VISIBLE);
        touchDismissOverlay.setVisibility(View.VISIBLE);
    }

    private void clearInlineResults() {
        resultsContainer.removeAllViews();
        resultsContainer.setVisibility(View.GONE);
        dividerResults.setVisibility(View.GONE);
        touchDismissOverlay.setVisibility(View.GONE);
    }

    private void navigateToTracking(PlannedRoute route) {
        searchHandler.removeCallbacks(searchRunnable);
        etSearchDestination.setText("");
        viewModel.reset();
        RouteMapHelper.clearPlannedRoute(mapStyle);

        Fragment fragment = route != null
                ? TrackingFragment.newInstance(route)
                : new TrackingFragment();

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(getContainerId(), fragment)
                .addToBackStack(null).commit();
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
                            replan();
                        } else {
                            // IDLE — let system handle (pop back stack / exit)
                            setEnabled(false);
                            requireActivity().getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(etSearchDestination.getWindowToken(), 0);
    }

    private int getContainerId() {
        return ((ViewGroup) requireView().getParent()).getId();
    }

    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onStop()  { super.onStop();  mapView.onStop();  }

    @Override
    public void onDestroyView() {
        searchHandler.removeCallbacks(searchRunnable);
        LocationComponentPlugin locationPlugin =
                mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (locationPlugin != null) {
            locationPlugin.removeOnIndicatorPositionChangedListener(positionListener);
        }
        mapView.onDestroy();
        super.onDestroyView();
    }
}
