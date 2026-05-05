package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.AIApiService;
import com.grouprace.core.network.model.ai.AIChatRequest;
import com.grouprace.core.network.model.ai.AIChatResponse;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AINetworkDataSource {
    private final AIApiService apiService;

    @Inject
    public AINetworkDataSource(AIApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<AIChatResponse>> chat(AIChatRequest request) {
        MutableLiveData<Result<AIChatResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.chat(request).enqueue(new Callback<ApiResponse<AIChatResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AIChatResponse>> call, Response<ApiResponse<AIChatResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AIChatResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AIChatResponse>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }
}
