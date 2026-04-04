package com.grouprace.core.network.model.record;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecordWeeklySummaryResponse {
    @SerializedName("activity_type")
    private String activityType;

    @SerializedName("weeks")
    private int weeks;

    @SerializedName("points")
    private List<RecordWeeklyPointResponse> points;

    public String getActivityType() {
        return activityType;
    }

    public int getWeeks() {
        return weeks;
    }

    public List<RecordWeeklyPointResponse> getPoints() {
        return points;
    }
}
