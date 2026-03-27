package com.grouprace.feature.tracking.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.grouprace.feature.tracking.R;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;

public class TrackingFragment extends Fragment {

    private MapView mapView;
    private TrackingViewModel viewModel;
    private ServiceLocationProvider locationProvider;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean granted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                if (Boolean.TRUE.equals(granted)) {
                    viewModel.startTracking();
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

        locationProvider = new ServiceLocationProvider();

        mapView.getMapboxMap().loadStyle(Style.MAPBOX_STREETS, style -> {
            LocationComponentPlugin locationPlugin =
                    mapView.getPlugin(Plugin.MAPBOX_LOCATION_COMPONENT_PLUGIN_ID);
            if (locationPlugin != null) {
                locationPlugin.setLocationProvider(locationProvider);
                locationPlugin.updateSettings(settings -> {
                    settings.setEnabled(true);
                    return null;
                });
            }
        });

        Button btnStart = view.findViewById(R.id.btn_start);
        Button btnStop = view.findViewById(R.id.btn_stop);

        viewModel = new ViewModelProvider(this).get(TrackingViewModel.class);

        viewModel.getIsTracking().observe(getViewLifecycleOwner(), tracking -> {
            btnStart.setVisibility(Boolean.TRUE.equals(tracking) ? View.GONE : View.VISIBLE);
            btnStop.setVisibility(Boolean.TRUE.equals(tracking) ? View.VISIBLE : View.GONE);
        });

        viewModel.getCurrentLocation().observe(getViewLifecycleOwner(), this::moveCamera);

        btnStart.setOnClickListener(v -> requestLocationAndStart());
        btnStop.setOnClickListener(v -> viewModel.stopTracking());
    }

    private void requestLocationAndStart() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            viewModel.startTracking();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void moveCamera(Location location) {
        if (location == null) return;

        locationProvider.updateLocation(location.getLongitude(), location.getLatitude());

        mapView.getMapboxMap().setCamera(
                new CameraOptions.Builder()
                        .center(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
                        .zoom(15.0)
                        .build()
        );
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
