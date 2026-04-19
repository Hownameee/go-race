package com.grouprace.feature.map.ui;

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

final class DrawRouteMapHelper {

    private static final String SOURCE_RESOLVED  = "draw-resolved-source";
    private static final String LAYER_RESOLVED   = "draw-resolved-layer";

    void setupLayers(Style style) {
        addLineLayer(style, SOURCE_RESOLVED, LAYER_RESOLVED, "#FF5252", 5.0, false);
    }

    void drawResolvedRoute(Style style, List<double[]> coordinates) {
        if (style == null) return;
        if (coordinates == null || coordinates.size() < 2) {
            clearSource(style, SOURCE_RESOLVED);
            return;
        }
        updateSource(style, SOURCE_RESOLVED, toPoints(coordinates));
    }

    void clearAll(Style style) {
        if (style == null) return;
        clearSource(style, SOURCE_RESOLVED);
    }

    void zoomToFit(MapView mapView, List<double[]> waypoints) {
        if (waypoints == null || waypoints.size() < 2) return;
        List<Point> points = toPoints(waypoints);
        ViewportPlugin viewport = ViewportUtils.getViewport(mapView);
        viewport.transitionTo(
                viewport.makeOverviewViewportState(
                        new OverviewViewportStateOptions.Builder()
                                .geometry(LineString.fromLngLats(points))
                                .padding(new EdgeInsets(100, 100, 100, 100))
                                .build()),
                viewport.makeImmediateViewportTransition(), null);
    }

    // --- private helpers ---

    private static List<Point> toPoints(List<double[]> coords) {
        List<Point> points = new ArrayList<>(coords.size());
        for (double[] c : coords) points.add(Point.fromLngLat(c[0], c[1]));
        return points;
    }

    private static void addLineLayer(Style style, String sourceId, String layerId,
                                     String color, double width, boolean dashed) {
        Expected<String, Value> source = Value.fromJson(
                "{\"type\":\"geojson\",\"data\":{\"type\":\"LineString\",\"coordinates\":[]}}"
        );
        if (source.isValue()) style.addStyleSource(sourceId, source.getValue());

        String dashPart = dashed ? ",\"line-dasharray\":[2,2]" : "";
        Expected<String, Value> layer = Value.fromJson(
                "{\"id\":\"" + layerId + "\",\"type\":\"line\",\"source\":\"" + sourceId + "\"," +
                "\"layout\":{\"line-cap\":\"round\",\"line-join\":\"round\"}," +
                "\"paint\":{\"line-color\":\"" + color + "\",\"line-width\":" + width + dashPart + "}}"
        );
        if (layer.isValue()) style.addStyleLayer(layer.getValue(), null);
    }

    private static void updateSource(Style style, String sourceId, List<Point> points) {
        if (points == null || points.size() < 2) return;
        Expected<String, Value> data = Value.fromJson(LineString.fromLngLats(points).toJson());
        if (data.isValue()) style.setStyleSourceProperty(sourceId, "data", data.getValue());
    }

    private static void clearSource(Style style, String sourceId) {
        Expected<String, Value> empty = Value.fromJson(
                "{\"type\":\"LineString\",\"coordinates\":[]}"
        );
        if (empty.isValue()) style.setStyleSourceProperty(sourceId, "data", empty.getValue());
    }
}
