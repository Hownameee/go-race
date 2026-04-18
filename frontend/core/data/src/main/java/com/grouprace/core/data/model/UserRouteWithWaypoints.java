package com.grouprace.core.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

/**
 * POJO for Room to fetch UserRouteEntity with its associated UserRouteWaypointEntities.
 */
public class UserRouteWithWaypoints {
    @Embedded
    public UserRouteEntity route;

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId"
    )
    public List<UserRouteWaypointEntity> waypoints;
}
