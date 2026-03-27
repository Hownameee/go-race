package com.grouprace.feature.tracking.ui;

import com.mapbox.geojson.Point;
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer;
import com.mapbox.maps.plugin.locationcomponent.LocationProvider;

import java.util.HashSet;
import java.util.Set;

public class ServiceLocationProvider implements LocationProvider {

    private final Set<LocationConsumer> consumers = new HashSet<>();

    public void updateLocation(double longitude, double latitude) {
        Point point = Point.fromLngLat(longitude, latitude);
        for (LocationConsumer consumer : consumers) {
            consumer.onLocationUpdated(new Point[]{point}, null);
        }
    }

    @Override
    public void registerLocationConsumer(@androidx.annotation.NonNull LocationConsumer consumer) {
        consumers.add(consumer);
    }

    @Override
    public void unRegisterLocationConsumer(@androidx.annotation.NonNull LocationConsumer consumer) {
        consumers.remove(consumer);
    }
}
