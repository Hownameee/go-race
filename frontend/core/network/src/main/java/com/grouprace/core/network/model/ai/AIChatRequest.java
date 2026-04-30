package com.grouprace.core.network.model.ai;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AIChatRequest {
    @SerializedName("prompt")
    private String prompt;
    @SerializedName("history")
    private List<ChatMessage> history;
    @SerializedName("location")
    private Location location;
    @SerializedName("currentWaypoints")
    private List<Waypoint> currentWaypoints;

    public AIChatRequest(String prompt, List<ChatMessage> history, Location location, List<Waypoint> currentWaypoints) {
        this.prompt = prompt;
        this.history = history;
        this.location = location;
        this.currentWaypoints = currentWaypoints;
    }

    public static class ChatMessage {
        @SerializedName("role")
        private String role;
        @SerializedName("content")
        private String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class Waypoint {
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;

        public Waypoint(double lat, double lng) {
            this.latitude = lat;
            this.longitude = lng;
        }
    }

    public static class Location {
        @SerializedName("lat")
        private double lat;
        @SerializedName("lng")
        private double lng;

        public Location(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
}
