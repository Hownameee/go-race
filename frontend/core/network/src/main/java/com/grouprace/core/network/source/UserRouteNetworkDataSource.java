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

public class UserRouteNetworkDataSource {

    private static final String TAG = "UserRouteNetworkDataSource";
    private final DirectionsApiService directionsApiService;

    @Inject
    public UserRouteNetworkDataSource(DirectionsApiService directionsApiService) {
        this.directionsApiService = directionsApiService;
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
