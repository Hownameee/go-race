package com.grouprace.core.model;

import java.io.Serializable;
import java.util.List;

/**
 * Domain model for a user-created route drawn on the map.
 * Always created online, so routeCoordinates / distance / duration are populated.
 */
public class UserRoute implements Serializable {

    public final long id;
    public final String name;

    /** Raw marker positions — each entry is [longitude, latitude]. */
    public final List<double[]> waypoints;

    /** Road-snapped route geometry — each entry is [longitude, latitude]. */
    public final List<double[]> routeCoordinates;

    public final double distanceKm;
    public final int durationSeconds;

    /** "normal" (Directions API) or "fast" (Optimization API). */
    public final String routeMode;

    public final boolean isCycle;
    public final long createdAt;

    public UserRoute(long id, String name, List<double[]> waypoints,
                     List<double[]> routeCoordinates, double distanceKm,
                     int durationSeconds, String routeMode, boolean isCycle,
                     long createdAt) {
        this.id = id;
        this.name = name;
        this.waypoints = waypoints;
        this.routeCoordinates = routeCoordinates;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
        this.routeMode = routeMode;
        this.isCycle = isCycle;
        this.createdAt = createdAt;
    }

    public String getFormattedDistance() {
        return String.format("%.1f km", distanceKm);
    }

    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        if (minutes < 60) return "~" + minutes + " min";
        return "~" + (minutes / 60) + "h " + (minutes % 60) + "min";
    }
}
