package com.grouprace.core.model;

import java.io.Serializable;
import java.util.List;

/** Round-trip route to a nearby place. Serializable for Bundle passing. */
public class PlannedRoute implements Serializable {

    /** Each entry is [longitude, latitude]. */
    public final List<double[]> coordinates;
    public final double distanceKm;
    public final int durationSeconds;

    public PlannedRoute(List<double[]> coordinates, double distanceKm, int durationSeconds) {
        this.coordinates = coordinates;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
    }

    public List<double[]> getCoordinates() { return coordinates; }
    public double getDistanceKm() { return distanceKm; }
    public int getDurationSeconds() { return durationSeconds; }

    public String getFormattedDistance() {
        return String.format("%.1f km", distanceKm);
    }

    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        if (minutes < 60) return "~" + minutes + " min";
        return "~" + (minutes / 60) + "h " + (minutes % 60) + "min";
    }

    public String getDifficulty() {
        if (distanceKm < 3) return "Easy";
        if (distanceKm < 7) return "Moderate";
        return "Hard";
    }
}
