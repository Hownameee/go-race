package com.grouprace.core.network.api;

import com.grouprace.core.network.model.directions.DirectionsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DirectionsApiService {

    /**
     * Get walking route between coordinates.
     * https://api.mapbox.com/directions/v5/mapbox/walking/{coordinates}
     *
     * @param coordinates semicolon-separated "lng,lat" pairs e.g. "lng1,lat1;lng2,lat2;lng3,lat3"
     */
    @GET("directions/v5/mapbox/walking/{coordinates}")
    Call<DirectionsResponse> getRoute(
            @Path(value = "coordinates", encoded = true) String coordinates,
            @Query("geometries") String geometries,    // "geojson"
            @Query("access_token") String accessToken
    );
}
