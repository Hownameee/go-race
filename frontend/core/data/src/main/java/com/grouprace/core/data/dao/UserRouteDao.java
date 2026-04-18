package com.grouprace.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.grouprace.core.data.model.UserRouteEntity;
import com.grouprace.core.data.model.UserRouteWaypointEntity;
import com.grouprace.core.data.model.UserRouteWithWaypoints;

import java.util.List;

@Dao
public interface UserRouteDao {

    @Insert
    long insertRoute(UserRouteEntity route);

    @Insert
    void insertWaypoints(List<UserRouteWaypointEntity> waypoints);

    @Query("SELECT * FROM user_routes ORDER BY createdAt DESC")
    LiveData<List<UserRouteWithWaypoints>> getAllRoutes();

    @Query("SELECT * FROM user_routes WHERE id = :id")
    UserRouteWithWaypoints getRouteById(long id);

    @Query("SELECT * FROM user_route_waypoints WHERE routeId = :routeId ORDER BY orderIndex ASC")
    List<UserRouteWaypointEntity> getWaypointsForRoute(long routeId);

    @Query("DELETE FROM user_route_waypoints WHERE routeId = :routeId")
    void deleteWaypointsForRoute(long routeId);

    @Query("DELETE FROM user_routes WHERE id = :id")
    void deleteRoute(long id);
}
