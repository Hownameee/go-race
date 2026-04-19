package com.grouprace.core.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_route_waypoints")
public class UserRouteWaypointEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long routeId;
    public int orderIndex;
    public double longitude;
    public double latitude;

    public UserRouteWaypointEntity() {}

    @Ignore
    public UserRouteWaypointEntity(long routeId, int orderIndex,
                                   double longitude, double latitude) {
        this.routeId = routeId;
        this.orderIndex = orderIndex;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
