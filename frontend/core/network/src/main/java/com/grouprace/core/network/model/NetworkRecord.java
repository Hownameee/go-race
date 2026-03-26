package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;

public class NetworkRecord {
    @SerializedName("activity_type")
    private String activityType;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("owner_id")
    private int ownerId;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("duration_seconds")
    private int duration;

    @SerializedName("distance_km")
    private float distance;

    @SerializedName("calories_burned")
    private float calories;

    @SerializedName("heart_rate_avg")
    private float heartRate;

    @SerializedName("speed")
    private float speed;

    @SerializedName("image_url")
    private String imageUrl;

    // --- Getters and Setters ---

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public com.grouprace.core.model.Record asExternalModel() {
        return new com.grouprace.core.model.Record(this.activityType, this.startTime, this.endTime, this.ownerId, this.duration, this.distance, this.calories, this.heartRate, this.speed, this.imageUrl);
    }
}