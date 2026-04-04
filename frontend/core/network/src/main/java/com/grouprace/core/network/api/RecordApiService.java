package com.grouprace.core.network.api;

import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecordApiService {
    @GET("/api/records/me/weekly-summary")
    Call<ApiResponse<RecordWeeklySummaryResponse>> getMyWeeklySummary(
            @Query("activityType") String activityType,
            @Query("weeks") int weeks
    );
}
