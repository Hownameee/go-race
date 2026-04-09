package com.grouprace.core.data.model;

import androidx.room.ColumnInfo;
import com.grouprace.core.model.TodaySummary;

public class TodaySummaryEntity {
    @ColumnInfo(name = "activityCount")
    public int activityCount;

    @ColumnInfo(name = "totalDurationSeconds")
    public int totalDurationSeconds;

    @ColumnInfo(name = "totalDistanceKm")
    public float totalDistanceKm;

    public TodaySummary asExternalModel() {
        return new TodaySummary(
                activityCount,
                totalDurationSeconds,
                totalDistanceKm
        );
    }
}
