package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.model.NearbyPlace;
import com.grouprace.core.model.PlannedRoute;

import java.util.List;

public interface NearbyRouteRepository {

    LiveData<Result<List<NearbyPlace>>> getNearbyPlaces(double lng, double lat, String accessToken);

    LiveData<Result<PlannedRoute>> generateRoute(double userLng, double userLat,
                                                  double placeLng, double placeLat,
                                                  String accessToken);

    LiveData<Result<List<NearbyPlace>>> searchByQuery(String query, double lng, double lat,
                                                       String accessToken);
}
