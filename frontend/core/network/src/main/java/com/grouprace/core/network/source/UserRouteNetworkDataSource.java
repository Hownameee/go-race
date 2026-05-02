package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.PlannedRoute;
import com.grouprace.core.network.api.DirectionsApiService;
import com.grouprace.core.network.model.directions.DirectionsResponse;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.grouprace.core.network.api.UserRouteApiService;
import com.grouprace.core.network.model.route.NetworkUserRoute;
import com.grouprace.core.network.utils.ApiResponse;

public class UserRouteNetworkDataSource {

    private static final String TAG = "UserRouteNetworkDataSource";
    private final DirectionsApiService directionsApiService;
    private final UserRouteApiService userRouteApiService;

    @Inject
    public UserRouteNetworkDataSource(DirectionsApiService directionsApiService,
                                   UserRouteApiService userRouteApiService) {
        this.directionsApiService = directionsApiService;
        this.userRouteApiService = userRouteApiService;
    }

    public LiveData<Result<NetworkUserRoute>> saveRoute(java.util.Map<String, Object> body) {
        MutableLiveData<Result<NetworkUserRoute>> result = new MutableLiveData<>();
        result.postValue(new Result.Loading<>());
        userRouteApiService.createRoute(body).enqueue(new Callback<ApiResponse<NetworkUserRoute>>() {
            @Override
            public void onResponse(Call<ApiResponse<NetworkUserRoute>> call, Response<ApiResponse<NetworkUserRoute>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    result.postValue(new Result.Error<>(new Exception("Failed to save route on backend"), "Failed to save route on backend"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NetworkUserRoute>> call, Throwable t) {
                result.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<Result<List<NetworkUserRoute>>> getRoutes() {
        MutableLiveData<Result<List<NetworkUserRoute>>> result = new MutableLiveData<>();
        result.postValue(new Result.Loading<>());
        userRouteApiService.getRoutes().enqueue(new Callback<ApiResponse<List<NetworkUserRoute>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NetworkUserRoute>>> call, Response<ApiResponse<List<NetworkUserRoute>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    result.postValue(new Result.Success<>(response.body().getData()));
                } else {
                    result.postValue(new Result.Error<>(new Exception("Failed to fetch routes"), "Failed to fetch routes"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NetworkUserRoute>>> call, Throwable t) {
                result.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<Result<Void>> deleteRoute(long routeId) {
        MutableLiveData<Result<Void>> result = new MutableLiveData<>();
        result.postValue(new Result.Loading<>());
        userRouteApiService.deleteRoute(routeId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    result.postValue(new Result.Success<>(null));
                } else {
                    result.postValue(new Result.Error<>(new Exception("Failed to delete route"), "Failed to delete route"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<Result<Void>> updateRoute(long routeId, java.util.Map<String, Object> body) {
        MutableLiveData<Result<Void>> result = new MutableLiveData<>();
        result.postValue(new Result.Loading<>());
        userRouteApiService.updateRoute(routeId, body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    result.postValue(new Result.Success<>(null));
                } else {
                    result.postValue(new Result.Error<>(new Exception("Failed to update route"), "Failed to update route"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return result;
    }

    public LiveData<Result<PlannedRoute>> generateRouteFromWaypoints(
            List<double[]> waypoints, String mode, boolean isCycle, String accessToken) {

        MutableLiveData<Result<PlannedRoute>> result = new MutableLiveData<>();
        result.postValue(new Result.Loading<>());
        
        String coordsString = buildCoordsString(waypoints, isCycle);

        Log.d(TAG, "Generating directions for " + waypoints.size() + " waypoints");
        directionsApiService.getRoute(coordsString, "geojson", accessToken)
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {
                            DirectionsResponse.Route routeRes = response.body().routes.get(0);
                            List<double[]> routeCoords = new ArrayList<>();
                            for (List<Double> coord : routeRes.geometry.coordinates) {
                                routeCoords.add(new double[]{coord.get(0), coord.get(1)});
                            }
                            PlannedRoute route = new PlannedRoute(routeCoords, routeRes.distance / 1000.0,
                                    (int) routeRes.duration);
                            Log.d(TAG, "Directions Success: " + route.getFormattedDistance());
                            result.postValue(new Result.Success<>(route));
                        } else {
                            String error = "Directions API failed: " + response.code();
                            Log.e(TAG, error);
                            result.postValue(new Result.Error<>(new Exception(error), error));
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Directions Network Failure", t);
                        result.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
                    }
                });

        return result;
    }

    private String buildCoordsString(List<double[]> waypoints, boolean appendFirst) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < waypoints.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(waypoints.get(i)[0]).append(",").append(waypoints.get(i)[1]);
        }
        if (appendFirst && !waypoints.isEmpty()) {
            sb.append(";").append(waypoints.get(0)[0]).append(",").append(waypoints.get(0)[1]);
        }
        return sb.toString();
    }
}
