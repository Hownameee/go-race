package com.grouprace.core.network.api;

import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FollowApiService {

    @POST("api/users/{followingId}/follow")
    Call<ApiResponse<Void>> followUser(@Path("followingId") int targetUserId);

    @DELETE("api/users/{followingId}/follow")
    Call<ApiResponse<Void>> unfollowUser(@Path("followingId") int targetUserId);
}
