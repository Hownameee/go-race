package com.grouprace.core.network.model.route;

import com.google.gson.annotations.SerializedName;

public class NetworkUserRoute {

    @SerializedName("route_id")
    private long id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("route_mode")
    private String routeMode;

    @SerializedName("is_cycle")
    private int isCycle;

    @SerializedName("distance_km")
    private double distanceKm;

    @SerializedName("duration_seconds")
    private int durationSeconds;

    @SerializedName("route_coordinates_json")
    private String routeCoordinatesJson;

    @SerializedName("waypoints_json")
    private String waypointsJson;

    @SerializedName("created_at")
    private String createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRouteMode() { return routeMode; }
    public void setRouteMode(String routeMode) { this.routeMode = routeMode; }

    public int getIsCycle() { return isCycle; }
    public void setIsCycle(int isCycle) { this.isCycle = isCycle; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getRouteCoordinatesJson() { return routeCoordinatesJson; }
    public void setRouteCoordinatesJson(String routeCoordinatesJson) { this.routeCoordinatesJson = routeCoordinatesJson; }

    public String getWaypointsJson() { return waypointsJson; }
    public void setWaypointsJson(String waypointsJson) { this.waypointsJson = waypointsJson; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
