package com.grouprace.core.network.model.directions;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {

    @SerializedName("routes")
    public List<Route> routes;

    public static class Route {
        @SerializedName("distance")
        public double distance; // meters

        @SerializedName("duration")
        public double duration; // seconds

        @SerializedName("geometry")
        public Geometry geometry;
    }

    public static class Geometry {
        @SerializedName("coordinates")
        public List<List<Double>> coordinates; // [lng, lat] pairs
    }
}
