package com.grouprace.feature.map.ui;

import com.grouprace.core.model.PlannedRoute;
import java.util.List;

public class AIRoutingResult {
    private final PlannedRoute route;
    private final List<double[]> waypoints;

    public AIRoutingResult(PlannedRoute route, List<double[]> waypoints) {
        this.route = route;
        this.waypoints = waypoints;
    }

    public PlannedRoute getRoute() { return route; }
    public List<double[]> getWaypoints() { return waypoints; }
}
