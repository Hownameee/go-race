package com.grouprace.core.model;

public class TodaySummary {
    public int activityCount;
    public int totalDurationSeconds;
    public float totalDistanceKm;
    
    public TodaySummary(int activityCount, int totalDurationSeconds, float totalDistanceKm) {
        this.activityCount = activityCount;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalDistanceKm = totalDistanceKm;
    }
}
