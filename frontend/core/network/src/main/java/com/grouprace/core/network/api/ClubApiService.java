package com.grouprace.core.network.api;

import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ClubApiService {

    @GET("api/clubs")
    Call<ApiResponse<ClubListPayload>> getClubs(
            @Query("offset") int offset,
            @Query("limit") int limit
    );
}
