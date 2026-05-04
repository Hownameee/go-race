package com.grouprace.core.network.api;

import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.ClubPayload;
import com.grouprace.core.network.model.club.IsLeaderResponse;
import com.grouprace.core.network.model.club.JoinClubResponse;
import com.grouprace.core.network.model.club.UpdateClubRequest;
import com.grouprace.core.network.utils.ApiResponse;

import com.grouprace.core.network.model.club.CreateClubRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("api/clubs/{clubId}/admins")
    Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>> getAdmins(@Path("clubId") int clubId);

    @GET("api/clubs/{clubId}/is-leader")
    Call<ApiResponse<com.grouprace.core.network.model.club.IsLeaderResponse>> checkIsLeader(@Path("clubId") int clubId);

    @GET("api/clubs/{clubId}/is-admin")
    Call<ApiResponse<com.grouprace.core.network.model.club.IsAdminResponse>> checkIsAdmin(@Path("clubId") int clubId);

    @PUT("api/clubs/{clubId}")
    Call<ApiResponse<Object>> updateClub(@Path("clubId") int clubId, @Body UpdateClubRequest request);

    @GET("api/clubs/{clubId}/stats")
    Call<ApiResponse<com.grouprace.core.network.model.club.NetworkClubStats>> getClubStats(@Path("clubId") int clubId);

    @POST("api/clubs/{clubId}/events")
    Call<ApiResponse<Object>> createEvent(@Path("clubId") int clubId, @Body com.grouprace.core.network.model.club.CreateClubEventRequest request);

    @GET("api/clubs/{clubId}/events")
    Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>> getEvents(@Path("clubId") int clubId);

    @POST("api/clubs/{clubId}/events/{eventId}/join")
    Call<ApiResponse<Object>> joinEvent(@Path("clubId") int clubId, @Path("eventId") int eventId);

    @GET("api/clubs/{clubId}/events/{eventId}/stats")
    Call<ApiResponse<com.grouprace.core.network.model.club.NetworkEventStats>> getEventStats(@Path("clubId") int clubId, @Path("eventId") int eventId);

    @GET("api/clubs/{clubId}/members")
    Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>> getMembers(@Path("clubId") int clubId);

    @PUT("api/clubs/{clubId}/members/{userId}/status")
    Call<ApiResponse<Object>> updateMemberStatus(
            @Path("clubId") int clubId,
            @Path("userId") int userId,
            @Body java.util.Map<String, String> body
    );

    @PUT("api/clubs/{clubId}/members/{userId}/role")
    Call<ApiResponse<Object>> updateMemberRole(
            @Path("clubId") int clubId,
            @Path("userId") int userId,
            @Body java.util.Map<String, String> body
    );

    @POST("api/clubs/{clubId}/transfer-leadership")
    Call<ApiResponse<Object>> transferLeadership(
            @Path("clubId") int clubId,
            @Body java.util.Map<String, Integer> body
    );
}
