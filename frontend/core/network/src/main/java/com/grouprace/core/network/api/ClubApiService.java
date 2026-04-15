package com.grouprace.core.network.api;

import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.ClubPayload;
import com.grouprace.core.network.model.club.JoinClubResponse;
import com.grouprace.core.network.utils.ApiResponse;

import com.grouprace.core.network.model.club.CreateClubRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ClubApiService {

    @GET("api/clubs")
    Call<ApiResponse<ClubListPayload>> getClubs(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @POST("api/clubs/{clubId}/join")
    Call<ApiResponse<JoinClubResponse>> joinClub(@Path("clubId") int clubId);

    @POST("api/clubs/{clubId}/leave")
    Call<ApiResponse<JoinClubResponse>> leaveClub(@Path("clubId") int clubId);

    @POST("api/clubs")
    Call<ApiResponse<Object>> createClub(@Body CreateClubRequest request);

    @GET("api/clubs/{clubId}")
    Call<ApiResponse<ClubPayload>> getClub(@Path("clubId") int clubId);
}
