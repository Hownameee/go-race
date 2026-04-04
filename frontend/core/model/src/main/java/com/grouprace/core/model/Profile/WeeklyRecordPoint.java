package com.grouprace.core.model.Profile;

public class WeeklyRecordPoint {
    private final String weekStart;
    private final String weekEnd;
    private final double totalDistanceKm;
    private final int totalDurationSeconds;
    private final double totalElevationGainM;

    public WeeklyRecordPoint(String weekStart, String weekEnd, double totalDistanceKm,
                             int totalDurationSeconds, double totalElevationGainM) {
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalElevationGainM = totalElevationGainM;
    }

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
