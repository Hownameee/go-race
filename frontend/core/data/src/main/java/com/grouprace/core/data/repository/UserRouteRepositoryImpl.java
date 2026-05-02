package com.grouprace.core.data.repository;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grouprace.core.common.result.Result;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.model.UserRouteEntity;
import com.grouprace.core.data.model.UserRouteWaypointEntity;
import com.grouprace.core.data.model.UserRouteWithWaypoints;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.core.model.UserRoute;
import com.grouprace.core.network.model.route.NetworkUserRoute;
import com.grouprace.core.network.source.UserRouteNetworkDataSource;

import java.lang.reflect.Type;
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

    private UserRoute toDomainModel(NetworkUserRoute netRoute) {
        List<double[]> waypoints = new ArrayList<>();
        if (netRoute.getWaypointsJson() != null && !netRoute.getWaypointsJson().isEmpty()) {
            try {
                Type type = new TypeToken<List<List<Double>>>() {}.getType();
                List<List<Double>> parsed = new Gson().fromJson(netRoute.getWaypointsJson(), type);
                if (parsed != null) {
                    for (List<Double> coord : parsed) {
                        if (coord.size() >= 2) {
                            waypoints.add(new double[]{coord.get(0), coord.get(1)});
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        List<double[]> routeCoordinates = new ArrayList<>();
        if (netRoute.getRouteCoordinatesJson() != null && !netRoute.getRouteCoordinatesJson().isEmpty()) {
            try {
                Type type = new TypeToken<List<List<Double>>>() {}.getType();
                List<List<Double>> parsed = new Gson().fromJson(netRoute.getRouteCoordinatesJson(), type);
                if (parsed != null) {
                    for (List<Double> coord : parsed) {
                        if (coord.size() >= 2) {
                            routeCoordinates.add(new double[]{coord.get(0), coord.get(1)});
                        }
                    }
                }
            } catch (Exception ignored) {}
        }

        return new UserRoute(
                netRoute.getId(),
                netRoute.getName(),
                waypoints,
                routeCoordinates,
                netRoute.getDistanceKm(),
                netRoute.getDurationSeconds(),
                netRoute.getRouteMode(),
                netRoute.getIsCycle() == 1,
                System.currentTimeMillis()
        );
    }

    private UserRouteEntity toEntity(UserRoute route) {
        UserRouteEntity entity = new UserRouteEntity();
        entity.id = route.id;
        entity.name = route.name;
        entity.routeMode = route.routeMode;
        entity.isCycle = route.isCycle;
        entity.distanceKm = route.distanceKm;
        entity.durationSeconds = route.durationSeconds;
        entity.routeCoordinatesJson = UserRouteEntity.coordinatesToJson(route.routeCoordinates);
        entity.createdAt = route.createdAt;
        return entity;
    }

    private List<UserRouteWaypointEntity> toWaypointEntities(UserRoute route) {
        List<UserRouteWaypointEntity> list = new ArrayList<>();
        if (route.waypoints != null) {
            for (int i = 0; i < route.waypoints.size(); i++) {
                double[] wp = route.waypoints.get(i);
                list.add(new UserRouteWaypointEntity(route.id, i, wp[0], wp[1]));
            }
        }
        return list;
    }

    @Override
    public LiveData<Result<Long>> saveRoute(UserRoute route) {
        MediatorLiveData<Result<Long>> result = new MediatorLiveData<>();
        result.setValue(new Result.Loading<>());

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("name", route.name);
        body.put("routeMode", route.routeMode);
        body.put("isCycle", route.isCycle);
        body.put("distanceKm", route.distanceKm);
        body.put("durationSeconds", route.durationSeconds);
        body.put("routeCoordinatesJson", UserRouteEntity.coordinatesToJson(route.routeCoordinates));
        body.put("waypointsJson", UserRouteEntity.coordinatesToJson(route.waypoints));

        LiveData<Result<NetworkUserRoute>> saveCall = networkDataSource.saveRoute(body);
        result.addSource(saveCall, networkResult -> {
            if (networkResult instanceof Result.Success) {
                NetworkUserRoute netRoute = ((Result.Success<NetworkUserRoute>) networkResult).data;
                if (netRoute != null) {
                    executorService.execute(() -> {
                        try {
                            UserRoute domainRoute = toDomainModel(netRoute);
                            userRouteDao.insertRoute(toEntity(domainRoute));
                            userRouteDao.insertWaypoints(toWaypointEntities(domainRoute));

                            mainHandler.post(() -> {
                                result.removeSource(saveCall);
                                result.setValue(new Result.Success<>(netRoute.getId()));
                            });
                        } catch (Exception e) {
                            mainHandler.post(() -> {
                                result.removeSource(saveCall);
                                result.setValue(new Result.Error<>(e, e.getMessage()));
                            });
                        }
                    });
                } else {
                    result.removeSource(saveCall);
                    result.setValue(new Result.Error<>(new Exception("Returned route is null"), "Returned route is null"));
                }
            } else if (networkResult instanceof Result.Error) {
                result.removeSource(saveCall);
                result.setValue(new Result.Error<>(
                        ((Result.Error<NetworkUserRoute>) networkResult).exception,
                        ((Result.Error<NetworkUserRoute>) networkResult).message
                ));
            }
        });

        return result;
    }

    @Override
    public LiveData<List<UserRoute>> getAllRoutes() {
        mainHandler.post(() -> {
            try {
                networkDataSource.getRoutes().observeForever(new androidx.lifecycle.Observer<Result<List<NetworkUserRoute>>>() {
                    private boolean removed = false;
                    @Override
                    public void onChanged(Result<List<NetworkUserRoute>> listResult) {
                        if (removed) return;
                        if (listResult instanceof Result.Success) {
                            removed = true;
                            List<NetworkUserRoute> list = ((Result.Success<List<NetworkUserRoute>>) listResult).data;
                            if (list != null) {
                                executorService.execute(() -> {
                                    try {
                                        for (NetworkUserRoute netRoute : list) {
                                            UserRoute domainRoute = toDomainModel(netRoute);
                                            userRouteDao.deleteWaypointsForRoute(domainRoute.id);
                                            userRouteDao.insertRoute(toEntity(domainRoute));
                                            userRouteDao.insertWaypoints(toWaypointEntities(domainRoute));
                                        }
                                    } catch (Exception ignored) {}
                                });
                            }
                        } else if (listResult instanceof Result.Error) {
                            removed = true;
                        }
                    }
                });
            } catch (Exception ignored) {}
        });

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
        result.setValue(new Result.Loading<>());

        LiveData<Result<Void>> deleteCall = networkDataSource.deleteRoute(id);

        deleteCall.observeForever(new androidx.lifecycle.Observer<Result<Void>>() {
            @Override
            public void onChanged(Result<Void> networkResult) {
                if (networkResult instanceof Result.Success) {
                    deleteCall.removeObserver(this);
                    executorService.execute(() -> {
                        try {
                            userRouteDao.deleteWaypointsForRoute(id);
                            userRouteDao.deleteRoute(id);
                            mainHandler.post(() -> result.setValue(new Result.Success<>(null)));
                        } catch (Exception e) {
                            mainHandler.post(() -> result.setValue(new Result.Error<>(e, e.getMessage())));
                        }
                    });
                } else if (networkResult instanceof Result.Error) {
                    deleteCall.removeObserver(this);
                    result.setValue(new Result.Error<>(
                            ((Result.Error<Void>) networkResult).exception,
                            ((Result.Error<Void>) networkResult).message
                    ));
                }
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<Void>> updateRoute(UserRoute route) {
        MutableLiveData<Result<Void>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("name", route.name);
        body.put("routeMode", route.routeMode);
        body.put("isCycle", route.isCycle);
        body.put("distanceKm", route.distanceKm);
        body.put("durationSeconds", route.durationSeconds);
        body.put("routeCoordinatesJson", UserRouteEntity.coordinatesToJson(route.routeCoordinates));
        body.put("waypointsJson", UserRouteEntity.coordinatesToJson(route.waypoints));

        LiveData<Result<Void>> updateCall = networkDataSource.updateRoute(route.id, body);
        updateCall.observeForever(new androidx.lifecycle.Observer<Result<Void>>() {
            @Override
            public void onChanged(Result<Void> networkResult) {
                if (networkResult instanceof Result.Success) {
                    updateCall.removeObserver(this);
                    executorService.execute(() -> {
                        try {
                            userRouteDao.deleteWaypointsForRoute(route.id);
                            userRouteDao.insertRoute(toEntity(route));
                            userRouteDao.insertWaypoints(toWaypointEntities(route));
                            mainHandler.post(() -> result.setValue(new Result.Success<>(null)));
                        } catch (Exception e) {
                            mainHandler.post(() -> result.setValue(new Result.Error<>(e, e.getMessage())));
                        }
                    });
                } else if (networkResult instanceof Result.Error) {
                    updateCall.removeObserver(this);
                    result.setValue(new Result.Error<>(
                            ((Result.Error<Void>) networkResult).exception,
                            ((Result.Error<Void>) networkResult).message
                    ));
                }
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
