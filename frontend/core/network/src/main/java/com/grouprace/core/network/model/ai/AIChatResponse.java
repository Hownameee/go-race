package com.grouprace.core.network.model.ai;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AIChatResponse {
    @SerializedName("explanation")
    private String explanation;
    @SerializedName("waypoints")
    private List<Waypoint> waypoints;
    public static class Waypoint {
        @SerializedName("name")
        private String name;
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;
        @SerializedName("description")
        private String description;
        
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getName() { return name; }
    }

    public String getExplanation() { return explanation; }
    public List<Waypoint> getWaypoints() { return waypoints; }
}
