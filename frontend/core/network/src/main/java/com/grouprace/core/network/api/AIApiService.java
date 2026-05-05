package com.grouprace.core.network.api;

import com.grouprace.core.network.model.ai.AIChatRequest;
import com.grouprace.core.network.model.ai.AIChatResponse;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AIApiService {
    @POST("api/ai/routing")
    Call<ApiResponse<AIChatResponse>> chat(@Body AIChatRequest request);
}
