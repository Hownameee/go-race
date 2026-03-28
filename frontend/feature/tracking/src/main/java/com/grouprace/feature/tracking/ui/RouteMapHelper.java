package com.grouprace.feature.tracking.ui;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationsUtils;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions;
import com.mapbox.maps.plugin.viewport.ViewportPlugin;
import com.mapbox.maps.plugin.viewport.ViewportUtils;
import com.mapbox.maps.plugin.viewport.data.OverviewViewportStateOptions;
import com.mapbox.maps.plugin.viewport.state.OverviewViewportState;

import java.util.List;

/**
 * Shared helper for drawing routes on a Mapbox map.
 * Used by TrackingFragment, ActivitySummaryFragment, and ActivityDetailFragment.
 */
final class RouteMapHelper {

    static final String ROUTE_COLOR = "#FF4444";
    static final double ROUTE_WIDTH = 5.0;

    private RouteMapHelper() {}

    static PolylineAnnotationManager createPolylineManager(MapView mapView) {
        AnnotationPlugin annotationPlugin = AnnotationsUtils.getAnnotations(mapView);
        return PolylineAnnotationManagerKt.createPolylineAnnotationManager(annotationPlugin, null);
    }

    static void drawPolyline(PolylineAnnotationManager manager, List<Point> points) {
        if (manager == null || points == null || points.size() < 2) return;

        manager.deleteAll();
        PolylineAnnotationOptions options = new PolylineAnnotationOptions()
                .withPoints(points)
                .withLineColor(ROUTE_COLOR)
                .withLineWidth(ROUTE_WIDTH);
        manager.create(options);
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
