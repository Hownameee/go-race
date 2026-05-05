package com.grouprace.core.model;

import java.util.List;

public class AIChatContext {
    private final String prompt;
    private final List<ChatMessage> history;
    private final double latitude;
    private final double longitude;
    private final List<double[]> currentWaypoints;

    public AIChatContext(String prompt, List<ChatMessage> history, double latitude, double longitude, List<double[]> currentWaypoints) {
        this.prompt = prompt;
        this.history = history;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentWaypoints = currentWaypoints;
    }

    public String getPrompt() { return prompt; }
    public List<ChatMessage> getHistory() { return history; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public List<double[]> getCurrentWaypoints() { return currentWaypoints; }
}
