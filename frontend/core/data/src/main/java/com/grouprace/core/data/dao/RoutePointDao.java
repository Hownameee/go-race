package com.grouprace.core.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.grouprace.core.data.model.RoutePoint;

import java.util.List;

@Dao
public interface RoutePointDao {

    @Insert
    void insert(RoutePoint point);

    @Query("SELECT * FROM route_points ORDER BY timestamp ASC")
    List<RoutePoint> getAll();

    @Query("SELECT * FROM route_points WHERE activityId = :activityId ORDER BY timestamp ASC")
    List<RoutePoint> getByActivityId(long activityId);

    @Query("UPDATE route_points SET activityId = :activityId WHERE activityId = 0")
    void assignUnassignedPointsToActivity(long activityId);

    @Query("DELETE FROM route_points WHERE activityId = 0")
    void deleteUnassignedPoints();
}
