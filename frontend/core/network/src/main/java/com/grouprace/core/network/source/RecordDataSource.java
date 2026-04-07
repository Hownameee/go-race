package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.RecordApiService;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordDataSource {
    private final RecordApiService apiService;

    @Inject
    public RecordDataSource(RecordApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<RecordWeeklySummaryResponse>> getMyWeeklySummary(String activityType, int weeks) {
        MutableLiveData<Result<RecordWeeklySummaryResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getMyWeeklySummary(activityType, weeks)
                .enqueue(new Callback<ApiResponse<RecordWeeklySummaryResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<RecordWeeklySummaryResponse>> call,
                                           Response<ApiResponse<RecordWeeklySummaryResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<RecordWeeklySummaryResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                liveData.postValue(new Result.Success<>(apiResponse.getData()));
                            } else {
                                String message = apiResponse.getMessage() != null
                                        ? apiResponse.getMessage()
                                        : "Load weekly summary failed.";
                                liveData.postValue(new Result.Error<>(new Exception(message), message));
                            }
                        } else {
                            String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                            Log.e("RecordDataSource", errorMessage);
                            liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<RecordWeeklySummaryResponse>> call, Throwable t) {
                        String errorMessage = "Network Failure: " + t.getMessage();
                        Log.e("RecordDataSource", errorMessage, t);
                        Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                        liveData.postValue(new Result.Error<>(exception, errorMessage));
                    }
                });

        return liveData;
    }
}
