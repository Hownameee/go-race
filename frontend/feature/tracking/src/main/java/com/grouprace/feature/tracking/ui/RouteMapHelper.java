package com.grouprace.feature.tracking.ui;

import com.mapbox.bindgen.Expected;
import com.mapbox.bindgen.Value;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.viewport.ViewportPlugin;
import com.mapbox.maps.plugin.viewport.ViewportUtils;
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions;
import com.mapbox.maps.plugin.viewport.state.OverviewViewportState;

import java.util.List;

/**
 * Shared helper for drawing routes on a Mapbox map.
 * Uses GeoJSON source + LineLayer for smooth rounded lines and efficient updates.
 * Used by TrackingFragment, ActivitySummaryFragment, and ActivityDetailFragment.
 */
final class RouteMapHelper {

    static final String ROUTE_COLOR = "#FF5252";
    static final double ROUTE_WIDTH = 5.0;
    private static final String SOURCE_ID = "route-source";
    private static final String LAYER_ID = "route-layer";

    private RouteMapHelper() {}

    /** Call once inside the loadStyle callback to register source + layer. */
    static void setupRouteLayer(Style style) {
        Expected<String, Value> source = Value.fromJson(
            "{\"type\":\"geojson\",\"data\":{\"type\":\"LineString\",\"coordinates\":[]}}"
        );
        if (source.isValue()) {
            style.addStyleSource(SOURCE_ID, source.getValue());
        }

        Expected<String, Value> layer = Value.fromJson(
            "{\"id\":\"" + LAYER_ID + "\",\"type\":\"line\",\"source\":\"" + SOURCE_ID + "\"," +
            "\"layout\":{\"line-cap\":\"round\",\"line-join\":\"round\"}," +
            "\"paint\":{\"line-color\":\"" + ROUTE_COLOR + "\",\"line-width\":" + ROUTE_WIDTH + "}}"
        );
        if (layer.isValue()) {
            style.addStyleLayer(layer.getValue(), null);
        }
    }

    /** Update the route line — updates source data only, no delete/recreate. */
    static void drawRoute(Style style, List<Point> points) {
        if (style == null || points == null || points.size() < 2) return;
        Expected<String, Value> data = Value.fromJson(LineString.fromLngLats(points).toJson());
        if (data.isValue()) {
            style.setStyleSourceProperty(SOURCE_ID, "data", data.getValue());
        }
    }

    static void zoomToFitRoute(MapView mapView, List<Point> points) {
        if (points == null || points.size() < 2) return;
        ViewportPlugin viewport = ViewportUtils.getViewport(mapView);
        OverviewViewportState overviewState = viewport.makeOverviewViewportState(
            new OverviewViewportStateOptions.Builder()
                .geometry(LineString.fromLngLats(points))
                .padding(new EdgeInsets(80, 80, 80, 80))
                .build()
        );
        viewport.transitionTo(overviewState, viewport.makeImmediateViewportTransition(), null);
    }
}
