package com.grouprace.core.network.api;

import com.grouprace.core.network.model.CreateRecordRequest;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.RecordPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecordApiService {
    @GET("api/records/sync")
    Call<ApiResponse<RecordPayload>> getRecords(@Query("recordId") int currentId);

    @POST("api/records")
    Call<ApiResponse<NetworkRecord>> createRecord(@Body CreateRecordRequest request);

    @PATCH("api/records/{recordId}")
    Call<ApiResponse<Void>> updateRecord(@Path("recordId") long recordId, @Body java.util.Map<String, Object> updateData);
}
