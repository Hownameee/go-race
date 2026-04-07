package com.grouprace.core.network.api;

import com.grouprace.core.network.model.searchbox.SearchBoxResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SearchBoxApiService {

    /**
     * Find nearby places by category (park, trail, landmark, etc.)
     * https://api.mapbox.com/search/searchbox/v1/category/{category}
     */
    @GET("search/searchbox/v1/category/{category}")
    Call<SearchBoxResponse> getNearbyPlaces(
            @Path("category") String category,
            @Query("proximity") String proximity,   // "lng,lat"
            @Query("limit") int limit,
            @Query("access_token") String accessToken
    );
}
