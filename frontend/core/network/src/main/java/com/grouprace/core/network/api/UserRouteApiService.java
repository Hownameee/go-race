package com.grouprace.core.network.api;

import com.grouprace.core.network.model.route.NetworkUserRoute;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import retrofit2.http.PATCH;

public interface UserRouteApiService {
    @POST("api/routes")
    Call<ApiResponse<NetworkUserRoute>> createRoute(@Body Map<String, Object> body);

    @GET("api/routes")
    Call<ApiResponse<List<NetworkUserRoute>>> getRoutes();

    @DELETE("api/routes/{id}")
    Call<ApiResponse<Void>> deleteRoute(@Path("id") long id);

    @PATCH("api/routes/{id}")
    Call<ApiResponse<Void>> updateRoute(@Path("id") long id, @Body Map<String, Object> body);
}
