package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateRecordRequest {
    @SerializedName("activityType")
    private String activityType;

    @SerializedName("title")
    private String title;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("durationSeconds")
    private int duration;

    @SerializedName("distanceKm")
    private double distance;

    @SerializedName("caloriesBurned")
    private float calories;

    @SerializedName("heartRateAvg")
    private float heartRate;

    @SerializedName("speed")
    private double speed;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("routePoints")
    private List<NetworkRoutePoint> routePoints;

    public CreateRecordRequest(String activityType, String title, String startTime, String endTime,
                               int duration, double distance, float calories, float heartRate,
                               double speed, String imageUrl, List<NetworkRoutePoint> routePoints) {
        this.activityType = activityType;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.distance = distance;
        this.calories = calories;
        this.heartRate = heartRate;
        this.speed = speed;
        this.imageUrl = imageUrl;
        this.routePoints = routePoints;
    }

    // Getters
    public String getActivityType() { return activityType; }
    public String getTitle() { return title; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public int getDuration() { return duration; }
    public double getDistance() { return distance; }
    public float getCalories() { return calories; }
    public float getHeartRate() { return heartRate; }
    public double getSpeed() { return speed; }
    public String getImageUrl() { return imageUrl; }
    public List<NetworkRoutePoint> getRoutePoints() { return routePoints; }
}
