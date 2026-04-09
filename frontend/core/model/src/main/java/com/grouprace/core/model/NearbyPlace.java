package com.grouprace.core.model;

public class NearbyPlace {
    public final String name;
    public final double lng;
    public final double lat;
    public final double distanceMeters;

    public NearbyPlace(String name, double lng, double lat, double distanceMeters) {
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        this.distanceMeters = distanceMeters;
    }

    public String getFormattedDistance() {
        if (distanceMeters < 1000) return (int) distanceMeters + " m";
        return String.format("%.1f km", distanceMeters / 1000);
    }
}
