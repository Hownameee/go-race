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

import java.util.ArrayList;
import java.util.List;

/**
 * Shared helper for drawing routes on a Mapbox map.
 * Uses GeoJSON source + LineLayer for smooth rounded lines and efficient updates.
 * Used by TrackingFragment, ActivitySummaryFragment, and ActivityDetailFragment.
 */
final class RouteMapHelper {

    static final String ROUTE_COLOR = "#FF5252";
    private static final String SOURCE_ID = "route-source";
    private static final String SOURCE_PLANNED_ID = "planned-route-source";

    private RouteMapHelper() {}

    static void setupRouteLayer(Style style) {
        addLineLayer(style, SOURCE_ID, "route-layer", ROUTE_COLOR, 5.0, false);
    }

    static void setupPlannedRouteLayer(Style style) {
        addLineLayer(style, SOURCE_PLANNED_ID, "planned-route-layer", "#888888", 4.0, true);
    }

    /** Update the live route line. */
    static void drawRoute(Style style, List<Point> points) {
        updateSource(style, SOURCE_ID, points);
    }

    /** Update the ghost (planned) route line from plain double[] coordinate pairs. */
    static void drawPlannedRoute(Style style, List<double[]> coords) {
        if (style == null || coords == null || coords.size() < 2) return;
        updateSource(style, SOURCE_PLANNED_ID, toPoints(coords));
    }

    /** Erase the ghost route from the map. */
    static void clearPlannedRoute(Style style) {
        if (style == null) return;
        Expected<String, Value> empty = Value.fromJson(
            "{\"type\":\"LineString\",\"coordinates\":[]}"
        );
        if (empty.isValue()) style.setStyleSourceProperty(SOURCE_PLANNED_ID, "data", empty.getValue());
    }

    static void zoomToFitRoute(MapView mapView, List<Point> points) {
        if (points == null || points.size() < 2) return;
        ViewportPlugin viewport = ViewportUtils.getViewport(mapView);
        viewport.transitionTo(
            viewport.makeOverviewViewportState(
                new OverviewViewportStateOptions.Builder()
                    .geometry(LineString.fromLngLats(points))
                    .padding(new EdgeInsets(80, 80, 80, 80))
                    .build()),
            viewport.makeImmediateViewportTransition(), null);
    }

    /** Variant accepting raw [lng, lat] pairs — used by planned route callers. */
    static void zoomToFitCoords(MapView mapView, List<double[]> coords) {
        if (coords == null || coords.size() < 2) return;
        zoomToFitRoute(mapView, toPoints(coords));
    }

    private static List<Point> toPoints(List<double[]> coords) {
        List<Point> points = new ArrayList<>(coords.size());
        for (double[] c : coords) points.add(Point.fromLngLat(c[0], c[1]));
        return points;
    }

    // --- private helpers ---

    private static void addLineLayer(Style style, String sourceId, String layerId,
                                     String color, double width, boolean dashed) {
        Expected<String, Value> source = Value.fromJson(
            "{\"type\":\"geojson\",\"data\":{\"type\":\"LineString\",\"coordinates\":[]}}"
        );
        if (source.isValue()) style.addStyleSource(sourceId, source.getValue());

        String dashPart = dashed ? ",\"line-dasharray\":[4,4]" : "";
        Expected<String, Value> layer = Value.fromJson(
            "{\"id\":\"" + layerId + "\",\"type\":\"line\",\"source\":\"" + sourceId + "\"," +
            "\"layout\":{\"line-cap\":\"round\",\"line-join\":\"round\"}," +
            "\"paint\":{\"line-color\":\"" + color + "\",\"line-width\":" + width + dashPart + "}}"
        );
        if (layer.isValue()) style.addStyleLayer(layer.getValue(), null);
    }

    private static void updateSource(Style style, String sourceId, List<Point> points) {
        if (style == null || points == null || points.size() < 2) return;
        Expected<String, Value> data = Value.fromJson(LineString.fromLngLats(points).toJson());
        if (data.isValue()) style.setStyleSourceProperty(sourceId, "data", data.getValue());
    }
}
