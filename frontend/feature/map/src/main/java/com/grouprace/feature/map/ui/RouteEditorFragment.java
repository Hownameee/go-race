package com.grouprace.feature.map.ui;

import com.mapbox.geojson.Point;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.grouprace.core.common.GpxExporter;
import com.grouprace.core.model.UserRoute;
import java.io.File;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grouprace.feature.map.R;
import com.mapbox.bindgen.Expected;
import com.mapbox.bindgen.Value;
import com.mapbox.common.MapboxOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RouteEditorFragment extends Fragment {

    private DrawRouteViewModel viewModel;
    private DrawRouteMapHelper mapHelper;
    private Style mapStyle;

    private static final String SOURCE_MARKERS = "draw-markers-source";
    private static final String LAYER_MARKERS  = "draw-markers-layer";

    private MapView mapView;
    private TextView tvWaypoints, tvRouteStats;
    private LinearLayout layoutRouteInfo;
    private SwitchMaterial switchCycle;
    private ImageButton btnBack;
    private Button btnUndo, btnClear, btnSave;
    private ProgressBar progressBar;
    private View btnAiChat;
    private Point currentPoint;

    private View cardControls, cardPreviewActions;
    private TextView tvPreviewName, tvPreviewStats;
    private Button btnEditRoute, btnUseRoute;
    private ImageButton btnMore;

    private boolean locationCaptured = false;
    private final OnIndicatorPositionChangedListener positionListener = point -> {
        currentPoint = point;
        if (!locationCaptured) {
            locationCaptured = true;
            mapView.getMapboxMap().setCamera(new com.mapbox.maps.CameraOptions.Builder()
                    .center(point).zoom(14.0).build());
        }
    };

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (Boolean.TRUE.equals(result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false))) {
                    enableLocationTracking();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(DrawRouteViewModel.class);
        mapHelper = new DrawRouteMapHelper();

        bindViews(view);
        setupMap();
        observeViewModel();
        setupListeners();
    }

    private void bindViews(View view) {
        mapView = view.findViewById(R.id.mapView);
        tvWaypoints = view.findViewById(R.id.tv_waypoints);
        tvRouteStats = view.findViewById(R.id.tv_route_stats);
        layoutRouteInfo = view.findViewById(R.id.layout_route_info);
        switchCycle = view.findViewById(R.id.switch_cycle);
        btnBack = view.findViewById(R.id.btn_back);
        btnUndo = view.findViewById(R.id.btn_undo);
        btnClear = view.findViewById(R.id.btn_clear);
        btnSave = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progressBar);

        cardControls = view.findViewById(R.id.card_controls);
        cardPreviewActions = view.findViewById(R.id.card_preview_actions);
        tvPreviewName = view.findViewById(R.id.tv_preview_name);
        tvPreviewStats = view.findViewById(R.id.tv_preview_stats);
        btnEditRoute = view.findViewById(R.id.btn_edit_route);
        btnUseRoute = view.findViewById(R.id.btn_use_route);
        btnMore = view.findViewById(R.id.btn_more);
        btnAiChat = view.findViewById(R.id.btn_ai_chat);
    }

    private void setupMap() {
        mapView.getMapboxMap().loadStyle(Style.DARK, style -> {
            mapStyle = style;
            mapHelper.setupLayers(style);
            setupMarkerLayer(style);
            
            String token = MapboxOptions.INSTANCE.getAccessToken();
            viewModel.setAccessToken(token);
            
            GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(mapView);
            gesturesPlugin.addOnMapClickListener(point -> {
                if (viewModel.screenMode.getValue() == DrawRouteViewModel.ScreenMode.DRAWING) {
                    viewModel.addWaypoint(point.longitude(), point.latitude());
                    return true;
                }
                return false;
            });

            requestLocationPermission();

            // If a route was already set before the style loaded, draw it now
            UserRoute pending = viewModel.previewedRoute.getValue();
            if (pending != null) {
                mapHelper.drawResolvedRoute(style, pending.routeCoordinates);
                mapHelper.zoomToFit(mapView, pending.waypoints);
            }
        });
    }

    private void setupMarkerLayer(Style style) {
        Expected<String, Value> source = Value.fromJson(
                "{\"type\":\"geojson\",\"data\":{\"type\":\"FeatureCollection\",\"features\":[]}}"
        );
        if (source.isValue()) style.addStyleSource(SOURCE_MARKERS, source.getValue());

        Expected<String, Value> layer = Value.fromJson(
                "{\"id\":\"" + LAYER_MARKERS + "\",\"type\":\"circle\",\"source\":\"" + SOURCE_MARKERS + "\"," +
                "\"paint\":{\"circle-radius\":10,\"circle-color\":\"#FF5252\",\"circle-stroke-width\":2,\"circle-stroke-color\":\"#FFFFFF\"}}"
        );
        if (layer.isValue()) style.addStyleLayer(layer.getValue(), null);

        Expected<String, Value> textLayer = Value.fromJson(
                "{\"id\":\"" + LAYER_MARKERS + "-text\",\"type\":\"symbol\",\"source\":\"" + SOURCE_MARKERS + "\"," +
                "\"layout\":{\"text-field\":[\"get\",\"index\"],\"text-size\":11,\"text-allow-overlap\":true}," +
                "\"paint\":{\"text-color\":\"#FFFFFF\"}}"
        );
        if (textLayer.isValue()) style.addStyleLayer(textLayer.getValue(), null);
    }

    private void observeViewModel() {
        viewModel.screenMode.observe(getViewLifecycleOwner(), mode -> {
            cardControls.setVisibility(mode == DrawRouteViewModel.ScreenMode.DRAWING ? View.VISIBLE : View.GONE);
            cardPreviewActions.setVisibility(mode == DrawRouteViewModel.ScreenMode.PREVIEW ? View.VISIBLE : View.GONE);
            tvWaypoints.setVisibility(mode == DrawRouteViewModel.ScreenMode.DRAWING ? View.VISIBLE : View.GONE);
        });

        viewModel.waypoints.observe(getViewLifecycleOwner(), waypoints -> {
            tvWaypoints.setText(getString(R.string.waypoint_count, waypoints.size()));
            if (viewModel.screenMode.getValue() == DrawRouteViewModel.ScreenMode.DRAWING) {
                updateMarkers(waypoints);
            }
        });

        viewModel.routeResult.observe(getViewLifecycleOwner(), result -> {
            viewModel.handleRouteResult(result);
        });

        viewModel.drawingState.observe(getViewLifecycleOwner(), state -> {
            progressBar.setVisibility(state == DrawRouteViewModel.DrawingState.LOADING ? View.VISIBLE : View.GONE);
            btnSave.setVisibility(state == DrawRouteViewModel.DrawingState.ROUTE_READY ? View.VISIBLE : View.GONE);
            layoutRouteInfo.setVisibility(state == DrawRouteViewModel.DrawingState.ROUTE_READY ? View.VISIBLE : View.GONE);
        });

        viewModel.generatedRoute.observe(getViewLifecycleOwner(), route -> {
            if (route != null) {
                if (mapStyle != null) mapHelper.drawResolvedRoute(mapStyle, route.getCoordinates());
                tvRouteStats.setText(route.getFormattedDistance() + "  ·  " + route.getFormattedDuration());
            } else if (mapStyle != null) {
                mapHelper.drawResolvedRoute(mapStyle, null);
            }
        });

        viewModel.previewedRoute.observe(getViewLifecycleOwner(), route -> {
            if (route != null) {
                tvPreviewName.setText(route.name);
                tvPreviewStats.setText(String.format("%.1f km  ·  ~%d min", route.distanceKm, route.durationSeconds / 60));
                
                if (mapStyle != null) {
                    mapHelper.drawResolvedRoute(mapStyle, route.routeCoordinates);
                    mapHelper.zoomToFit(mapView, route.waypoints);
                }
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> viewModel.exitDrawingMode());
        btnUndo.setOnClickListener(v -> viewModel.undoLastWaypoint());
        btnClear.setOnClickListener(v -> {
            viewModel.clearAll();
            if (mapStyle != null) mapHelper.clearAll(mapStyle);
        });
        btnSave.setOnClickListener(v -> showSaveDialog());
        switchCycle.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setCycle(isChecked));
        btnEditRoute.setOnClickListener(v -> viewModel.editPreviewedRoute());
        btnUseRoute.setOnClickListener(v -> Toast.makeText(requireContext(), "Use Route feature coming soon", Toast.LENGTH_SHORT).show());
        btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), btnMore);
            popup.getMenuInflater().inflate(R.menu.menu_route_preview, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_share_gpx) {
                    shareRouteGpx();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        btnAiChat.setOnClickListener(v -> {
            double lat = currentPoint != null ? currentPoint.latitude() : 0.0;
            double lng = currentPoint != null ? currentPoint.longitude() : 0.0;
            
            String token = MapboxOptions.INSTANCE.getAccessToken();
            List<double[]> waypoints = viewModel.waypoints.getValue();
            AIChatBottomSheet chatSheet = AIChatBottomSheet.newInstance(lat, lng, token, waypoints);
            chatSheet.setOnRouteGeneratedListener(aiResult -> {
                viewModel.handleAIRoutingResult(aiResult);
            });
            chatSheet.show(getChildFragmentManager(), "AI_CHAT");
        });
    }

    private void shareRouteGpx() {
        UserRoute route = viewModel.previewedRoute.getValue();
        if (route == null) return;

        File gpxFile = GpxExporter.export(requireContext(), route.name, route.routeCoordinates);
        if (gpxFile != null) {
            Uri contentUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", gpxFile);

            Intent viewIntent = new Intent(Intent.ACTION_VIEW);
            viewIntent.setDataAndType(contentUri, "application/gpx+xml");
            viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(viewIntent);
        } else {
            Toast.makeText(requireContext(), R.string.msg_export_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMarkers(List<double[]> waypoints) {
        if (mapStyle == null) return;
        try {
            JSONArray features = new JSONArray();
            for (int i = 0; i < waypoints.size(); i++) {
                double[] wp = waypoints.get(i);
                JSONObject feature = new JSONObject();
                feature.put("type", "Feature");
                JSONObject geometry = new JSONObject();
                geometry.put("type", "Point");
                geometry.put("coordinates", new JSONArray(new double[]{wp[0], wp[1]}));
                feature.put("geometry", geometry);
                JSONObject properties = new JSONObject();
                properties.put("index", String.valueOf(i + 1));
                feature.put("properties", properties);
                features.put(feature);
            }
            JSONObject collection = new JSONObject();
            collection.put("type", "FeatureCollection");
            collection.put("features", features);
            Expected<String, Value> data = Value.fromJson(collection.toString());
            if (data.isValue()) {
                mapStyle.setStyleSourceProperty(SOURCE_MARKERS, "data", data.getValue());
            }
        } catch (Exception ignored) {}
    }

    private void showSaveDialog() {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.dialog_route_name_hint);
        new AlertDialog.Builder(requireContext(), com.google.android.material.R.style.Theme_MaterialComponents_Dialog_Alert)
                .setTitle(R.string.dialog_route_name_title)
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (name.isEmpty()) name = "New Route";
                    viewModel.saveRoute(name);
                    Toast.makeText(requireContext(), R.string.msg_route_saved, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocationTracking();
        } else {
            locationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    private void enableLocationTracking() {
        LocationComponentPlugin locationPlugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (locationPlugin != null) {
            locationPlugin.updateSettings(settings -> {
                settings.setEnabled(true);
                return null;
            });
            locationPlugin.addOnIndicatorPositionChangedListener(positionListener);
        }
    }

    @Override public void onStart() { super.onStart(); mapView.onStart(); }
    @Override public void onStop() { super.onStop(); mapView.onStop(); }
    @Override public void onDestroyView() {
        LocationComponentPlugin locationPlugin = mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
        if (locationPlugin != null) locationPlugin.removeOnIndicatorPositionChangedListener(positionListener);
        mapView.onDestroy();
        super.onDestroyView();
    }
}
