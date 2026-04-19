package com.grouprace.core.network.api;

import com.grouprace.core.network.model.CreateRecordRequest;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.RecordPayload;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecordApiService {
    @GET("api/records/{recordId}")
    Call<ApiResponse<RecordPayload>> getRecord(@Path("recordId") int recordId);

    @GET("api/records")
    Call<ApiResponse<RecordPayload>> getRecords(@Query("offset") int offset, @Query("limit") int limit);

    @GET("api/records/users/{userId}")
    Call<ApiResponse<RecordPayload>> getUserRecords(
            @Path("userId") int userId,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @POST("api/records")
    Call<ApiResponse<NetworkRecord>> createRecord(@Body CreateRecordRequest request);

    @PATCH("api/records/{recordId}")
    Call<ApiResponse<Void>> updateRecord(@Path("recordId") long recordId, @Body java.util.Map<String, Object> updateData);

    @GET("/api/records/me/weekly-summary")
    Call<ApiResponse<RecordWeeklySummaryResponse>> getMyWeeklySummary(
            @Query("activityType") String activityType,
            @Query("weeks") int weeks
    );

    @GET("/api/records/users/{userId}/weekly-summary")
    Call<ApiResponse<RecordWeeklySummaryResponse>> getUserWeeklySummary(
            @Path("userId") int userId,
            @Query("activityType") String activityType,
            @Query("weeks") int weeks
    );

    @GET("/api/records/me/profile-statistics")
    Call<ApiResponse<RecordProfileStatisticsResponse>> getMyProfileStatistics(
            @Query("activityType") String activityType
    );

    @GET("/api/records/users/{userId}/profile-statistics")
    Call<ApiResponse<RecordProfileStatisticsResponse>> getUserProfileStatistics(
            @Path("userId") int userId,
            @Query("activityType") String activityType
    );

    @GET("/api/records/me/streak")
    Call<ApiResponse<RecordStreakResponse>> getMyStreak();

    @GET("/api/records/users/{userId}/streak")
    Call<ApiResponse<RecordStreakResponse>> getUserStreak(
            @Path("userId") int userId
    );
}
