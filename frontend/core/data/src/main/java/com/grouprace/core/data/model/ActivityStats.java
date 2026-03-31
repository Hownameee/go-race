package com.grouprace.core.data.model;

import java.util.List;

public class ActivityStats {

    public final double distanceKm;
    public final long elapsedTimeMs;
    public final double paceMinPerKm;

    public ActivityStats(double distanceKm, long elapsedTimeMs, double paceMinPerKm) {
        this.distanceKm = distanceKm;
        this.elapsedTimeMs = elapsedTimeMs;
        this.paceMinPerKm = paceMinPerKm;
    }

    public static ActivityStats fromPoints(List<RoutePoint> points) {
        if (points == null || points.size() < 2) {
            return new ActivityStats(0, 0, 0);
        }

        double totalMeters = 0;
        for (int i = 1; i < points.size(); i++) {
            totalMeters += haversine(
                    points.get(i - 1).latitude, points.get(i - 1).longitude,
                    points.get(i).latitude, points.get(i).longitude
            );
        }

        long elapsedMs = points.get(points.size() - 1).timestamp - points.get(0).timestamp;
        double distanceKm = totalMeters / 1000.0;
        double paceMinPerKm = distanceKm > 0 ? (elapsedMs / 60000.0) / distanceKm : 0;

        return new ActivityStats(distanceKm, elapsedMs, paceMinPerKm);
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000; // Earth radius in meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
