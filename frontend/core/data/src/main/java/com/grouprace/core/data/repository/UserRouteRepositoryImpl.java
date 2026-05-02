package com.grouprace.core.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.model.UserRouteEntity;
import com.grouprace.core.data.model.UserRouteWaypointEntity;
import com.grouprace.core.data.model.UserRouteWithWaypoints;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.core.model.UserRoute;
import com.grouprace.core.network.source.UserRouteNetworkDataSource;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

import javax.inject.Inject;

public class UserRouteRepositoryImpl implements UserRouteRepository {

    private final UserRouteDao userRouteDao;
    private final UserRouteNetworkDataSource networkDataSource;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Inject
    public UserRouteRepositoryImpl(UserRouteDao userRouteDao,
                                   UserRouteNetworkDataSource networkDataSource) {
        this.userRouteDao = userRouteDao;
        this.networkDataSource = networkDataSource;
    }

    @Override
    public LiveData<Result<Long>> saveRoute(UserRoute route) {
        MutableLiveData<Result<Long>> result = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                Log.d("UserRouteRepositoryImpl", "Saving route: " + route.name);
                UserRouteEntity entity = new UserRouteEntity(
                        route.name,
                        route.routeMode,
                        route.isCycle,
                        route.distanceKm,
                        route.durationSeconds,
                        UserRouteEntity.coordinatesToJson(route.routeCoordinates),
                        new Date().getTime()
                );
                long routeId = userRouteDao.insertRoute(entity);

                List<UserRouteWaypointEntity> waypoints = new ArrayList<>();
                for (int i = 0; i < route.waypoints.size(); i++) {
                    double[] wp = route.waypoints.get(i);
                    waypoints.add(new UserRouteWaypointEntity(routeId, i, wp[0], wp[1]));
                }
                userRouteDao.insertWaypoints(waypoints);

                mainHandler.post(() -> result.setValue(new Result.Success<>(routeId)));
            } catch (Exception e) {
                Log.e("UserRouteRepositoryImpl", "Error saving route: " + e.getMessage());
                mainHandler.post(() -> result.setValue(new Result.Error<>(e, e.getMessage())));
            }
        });
        return result;
    }

    @Override
    public LiveData<List<UserRoute>> getAllRoutes() {
        return Transformations.map(userRouteDao.getAllRoutes(), relationList -> {
            List<UserRoute> routes = new ArrayList<>();
            for (UserRouteWithWaypoints wrapper : relationList) {
                routes.add(wrapper.route.toExternalModel(wrapper.waypoints));
            }
            return routes;
        });
    }

    @Override
    public LiveData<Result<UserRoute>> getRouteById(long id) {
        MutableLiveData<Result<UserRoute>> result = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                UserRouteWithWaypoints wrapper = userRouteDao.getRouteById(id);
                if (wrapper != null) {
                    UserRoute route = wrapper.route.toExternalModel(wrapper.waypoints);
                    mainHandler.post(() -> result.setValue(new Result.Success<>(route)));
                } else {
                    mainHandler.post(() -> result.setValue(new Result.Error<>(new Exception("Route not found"), "Route not found")));
                }
            } catch (Exception e) {
                mainHandler.post(() -> result.setValue(new Result.Error<>(e, e.getMessage())));
            }
        });
        return result;
    }

    @Override
    public LiveData<Result<Void>> deleteRoute(long id) {
        MutableLiveData<Result<Void>> result = new MutableLiveData<>();
        executorService.execute(() -> {
            try {
                userRouteDao.deleteWaypointsForRoute(id);
                userRouteDao.deleteRoute(id);
                mainHandler.post(() -> result.setValue(new Result.Success<>(null)));
            } catch (Exception e) {
                mainHandler.post(() -> result.setValue(new Result.Error<>(e, e.getMessage())));
            }
        });
        return result;
    }

    @Override
    public LiveData<Result<PlannedRoute>> generateRouteFromWaypoints(
            List<double[]> waypoints, String mode, boolean isCycle, String accessToken) {
        return networkDataSource.generateRouteFromWaypoints(waypoints, mode, isCycle, accessToken);
    }
}
