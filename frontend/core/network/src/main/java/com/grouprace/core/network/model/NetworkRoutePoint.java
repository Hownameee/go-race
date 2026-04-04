package com.grouprace.core.network.model;

import com.google.gson.annotations.SerializedName;

public class NetworkRoutePoint {
    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("altitude")
    private double altitude;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("accuracy")
    private float accuracy;

    public NetworkRoutePoint(double latitude, double longitude, double altitude, long timestamp, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }
}
