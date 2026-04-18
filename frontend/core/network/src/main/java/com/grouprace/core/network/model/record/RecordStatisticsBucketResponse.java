package com.grouprace.core.network.model.record;

import com.google.gson.annotations.SerializedName;

public class RecordStatisticsBucketResponse {
    @SerializedName("total_activities")
    private double totalActivities;

    @SerializedName("total_distance_km")
    private double totalDistanceKm;

    @SerializedName("total_duration_seconds")
    private int totalDurationSeconds;

    @SerializedName("total_elevation_gain_m")
    private double totalElevationGainM;

    public double getTotalActivities() {
        return totalActivities;
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
