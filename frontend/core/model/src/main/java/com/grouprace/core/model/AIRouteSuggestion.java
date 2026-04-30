package com.grouprace.core.model;

import java.util.List;

public class AIRouteSuggestion {
    private final String explanation;
    private final List<double[]> waypoints; // [longitude, latitude]

    public AIRouteSuggestion(String explanation, List<double[]> waypoints) {
        this.explanation = explanation;
        this.waypoints = waypoints;
    }

    public String getExplanation() { return explanation; }
    public List<double[]> getWaypoints() { return waypoints; }
}
