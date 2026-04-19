package com.grouprace.core.network.model.record;

import com.google.gson.annotations.SerializedName;

public class RecordProfileStatisticsResponse {
    @SerializedName("activity_type")
    private String activityType;

    @SerializedName("weekly_average")
    private RecordStatisticsBucketResponse weeklyAverage;

    @SerializedName("year_to_date")
    private RecordStatisticsBucketResponse yearToDate;

    @SerializedName("all_time")
    private RecordStatisticsBucketResponse allTime;

    public String getActivityType() {
        return activityType;
    }

    public RecordStatisticsBucketResponse getWeeklyAverage() {
        return weeklyAverage;
    }

    public RecordStatisticsBucketResponse getYearToDate() {
        return yearToDate;
    }

    public RecordStatisticsBucketResponse getAllTime() {
        return allTime;
    }
}
