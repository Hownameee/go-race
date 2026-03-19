package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "route_points")
public class RoutePoint {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public double latitude;
    public double longitude;
    public double altitude;
    public long timestamp;
    public float accuracy;

    public RoutePoint() {}

    public RoutePoint(double latitude, double longitude, double altitude,
                      long timestamp, float accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
    }
}
