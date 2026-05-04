package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grouprace.core.model.UserRoute;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "user_routes")
public class UserRouteEntity {

    @PrimaryKey
    public long id;

    public String name;
    public String routeMode;       // "normal" or "fast"
    public boolean isCycle;
    public double distanceKm;
    public int durationSeconds;
    public String routeCoordinatesJson;  // JSON [[lng,lat], ...]
    public long createdAt;

    public UserRouteEntity() {}

    @Ignore
    public UserRouteEntity(String name, String routeMode, boolean isCycle,
                           double distanceKm, int durationSeconds,
                           String routeCoordinatesJson, long createdAt) {
        this.name = name;
        this.routeMode = routeMode;
        this.isCycle = isCycle;
        this.distanceKm = distanceKm;
        this.durationSeconds = durationSeconds;
        this.routeCoordinatesJson = routeCoordinatesJson;
        this.createdAt = createdAt;
    }

    /**
     * Convert entity + its waypoint entities into the domain model.
     */
    public UserRoute toExternalModel(List<UserRouteWaypointEntity> waypointEntities) {
        // Parse waypoints
        List<double[]> waypoints = new ArrayList<>();
        if (waypointEntities != null) {
            for (UserRouteWaypointEntity wp : waypointEntities) {
                waypoints.add(new double[]{wp.longitude, wp.latitude});
            }
        }

        // Parse route coordinates from JSON
        List<double[]> routeCoordinates = new ArrayList<>();
        if (routeCoordinatesJson != null && !routeCoordinatesJson.isEmpty()) {
            try {
                Type type = new TypeToken<List<List<Double>>>() {}.getType();
                List<List<Double>> parsed = new Gson().fromJson(routeCoordinatesJson, type);
                if (parsed != null) {
                    for (List<Double> coord : parsed) {
                        if (coord.size() >= 2) {
                            routeCoordinates.add(new double[]{coord.get(0), coord.get(1)});
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return new UserRoute(id, name, waypoints, routeCoordinates,
                distanceKm, durationSeconds, routeMode, isCycle, createdAt);
    }

    /**
     * Serialize a list of [lng, lat] pairs to JSON for Room storage.
     */
    @Ignore
    public static String coordinatesToJson(List<double[]> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) return "[]";
        List<List<Double>> list = new ArrayList<>();
        for (double[] coord : coordinates) {
            List<Double> pair = new ArrayList<>();
            pair.add(coord[0]);
            pair.add(coord[1]);
            list.add(pair);
        }
        return new Gson().toJson(list);
    }
}
