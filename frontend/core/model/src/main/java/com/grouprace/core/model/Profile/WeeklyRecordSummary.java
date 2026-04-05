package com.grouprace.core.model.Profile;

import java.util.List;

public class WeeklyRecordSummary {
    private final String activityType;
    private final int weeks;
    private final List<WeeklyRecordPoint> points;

    public WeeklyRecordSummary(String activityType, int weeks, List<WeeklyRecordPoint> points) {
        this.activityType = activityType;
        this.weeks = weeks;
        this.points = points;
    }

    public String getActivityType() {
        return activityType;
    }

    public int getWeeks() {
        return weeks;
    }

    public List<WeeklyRecordPoint> getPoints() {
        return points;
    }
}
