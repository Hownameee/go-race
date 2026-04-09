package com.grouprace.core.network.model.searchbox;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchBoxResponse {

    @SerializedName("features")
    public List<Feature> features;

    public static class Feature {
        @SerializedName("properties")
        public Properties properties;

        @SerializedName("geometry")
        public Geometry geometry;
    }

    public static class Properties {
        @SerializedName("name")
        public String name;

        @SerializedName("distance")
        public double distance; // meters from user location
    }

    public static class Geometry {
        @SerializedName("coordinates")
        public List<Double> coordinates; // [lng, lat]
    }
}
