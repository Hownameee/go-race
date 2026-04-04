package com.grouprace.core.model;

public class RoutePoint {
    public final long activityId;
    public final double latitude;
    public final double longitude;
    public final double altitude;
    public final long timestamp;
    public final float accuracy;

    public RoutePoint(long activityId, double latitude, double longitude, double altitude, long timestamp, float accuracy) {
        this.activityId = activityId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }
}
