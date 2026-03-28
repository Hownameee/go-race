package com.grouprace.core.network.api;

import com.grouprace.core.network.model.RecordPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecordApiService {
    @GET("api/users/{userId}/records/sync")
    Call<ApiResponse<RecordPayload>> getRecords(@Path("userId") int userId, @Query("recordId") int currentId);
}
