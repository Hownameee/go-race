package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.core.model.UserRoute;

import java.util.List;

public interface UserRouteRepository {

    /** Save a fully resolved route. Returns route ID. */
    LiveData<Result<Long>> saveRoute(UserRoute route);

    /** Get all saved routes (reactive). */
    LiveData<List<UserRoute>> getAllRoutes();

    /** Get a single route by ID (with waypoints). */
    LiveData<Result<UserRoute>> getRouteById(long id);

    /** Delete a route and its waypoints. */
    LiveData<Result<Void>> deleteRoute(long id);

    /** Update a route and its waypoints. */
    LiveData<Result<Void>> updateRoute(UserRoute route);

    /** Call Mapbox API to generate route from waypoints. */
    LiveData<Result<PlannedRoute>> generateRouteFromWaypoints(
            List<double[]> waypoints, String mode, boolean isCycle, String accessToken);
}
