package com.grouprace.core.network.model.record;

import com.google.gson.annotations.SerializedName;

public class RecordWeeklyPointResponse {
    @SerializedName("week_start")
    private String weekStart;

    @SerializedName("week_end")
    private String weekEnd;

    @SerializedName("total_distance_km")
    private double totalDistanceKm;

    @SerializedName("total_duration_seconds")
    private int totalDurationSeconds;

    @SerializedName("total_elevation_gain_m")
    private double totalElevationGainM;

    public String getWeekStart() {
        return weekStart;
    }

    public String getWeekEnd() {
        return weekEnd;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public int getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public double getTotalElevationGainM() {
        return totalElevationGainM;
    }
}
