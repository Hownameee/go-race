package com.grouprace.core.model;

public class Record {
    private final String activityType;
    private final String startTime;
    private final String endTime;
    private final int ownerId;
    private final int duration;
    private final double distance;
    private final float calories;
    private final float heartRate;
    private final float speed;
    private final String routeUrl;

    public Record(String activityType, String startTime, String endTime, int ownerId, int duration, double distance, float calories, float heartRate, float speed, String routeUrl) {
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ownerId = ownerId;
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.heartRate = heartRate;
        this.speed = speed;
        this.routeUrl = routeUrl;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public float getCalories() {
        return calories;
    }

    public float getHeartRate() {
        return heartRate;
    }

    public double getSpeed() {
        return speed;
    }

    public String getRouteUrl() {
        return routeUrl;
    }
}
