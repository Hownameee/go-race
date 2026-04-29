package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.RecordApiService;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;
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

    public LiveData<Result<RecordWeeklySummaryResponse>> getUserWeeklySummary(int userId, String activityType, int weeks) {
        // ===== Profile Section ====
        MutableLiveData<Result<RecordWeeklySummaryResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserWeeklySummary(userId, activityType, weeks)
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
                                        : "Load user weekly summary failed.";
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

    public LiveData<Result<RecordProfileStatisticsResponse>> getMyProfileStatistics(String activityType) {
        MutableLiveData<Result<RecordProfileStatisticsResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getMyProfileStatistics(activityType)
                .enqueue(new Callback<ApiResponse<RecordProfileStatisticsResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<RecordProfileStatisticsResponse>> call,
                                           Response<ApiResponse<RecordProfileStatisticsResponse>> response) {
                        handleProfileStatisticsResponse(response, liveData, "Load profile statistics failed.");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<RecordProfileStatisticsResponse>> call, Throwable t) {
                        postNetworkFailure(liveData, "RecordDataSource", t);
                    }
                });

        return liveData;
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getUserProfileStatistics(int userId, String activityType) {
        // ===== Profile Section ====
        MutableLiveData<Result<RecordProfileStatisticsResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserProfileStatistics(userId, activityType)
                .enqueue(new Callback<ApiResponse<RecordProfileStatisticsResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<RecordProfileStatisticsResponse>> call,
                                           Response<ApiResponse<RecordProfileStatisticsResponse>> response) {
                        handleProfileStatisticsResponse(response, liveData, "Load user profile statistics failed.");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<RecordProfileStatisticsResponse>> call, Throwable t) {
                        postNetworkFailure(liveData, "RecordDataSource", t);
                    }
                });

        return liveData;
    }

    public LiveData<Result<RecordStreakResponse>> getMyStreak() {
        MutableLiveData<Result<RecordStreakResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getMyStreak().enqueue(new Callback<ApiResponse<RecordStreakResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecordStreakResponse>> call,
                                   Response<ApiResponse<RecordStreakResponse>> response) {
                handleStreakResponse(response, liveData, "Load streak failed.");
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordStreakResponse>> call, Throwable t) {
                postNetworkFailure(liveData, "RecordDataSource", t);
            }
        });

        return liveData;
    }

    public LiveData<Result<RecordStreakResponse>> getUserStreak(int userId) {
        // ===== Profile Section ====
        MutableLiveData<Result<RecordStreakResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserStreak(userId).enqueue(new Callback<ApiResponse<RecordStreakResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecordStreakResponse>> call,
                                   Response<ApiResponse<RecordStreakResponse>> response) {
                handleStreakResponse(response, liveData, "Load user streak failed.");
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordStreakResponse>> call, Throwable t) {
                postNetworkFailure(liveData, "RecordDataSource", t);
            }
        });

        return liveData;
    }

    private void handleProfileStatisticsResponse(
            Response<ApiResponse<RecordProfileStatisticsResponse>> response,
            MutableLiveData<Result<RecordProfileStatisticsResponse>> liveData,
            String fallbackMessage
    ) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<RecordProfileStatisticsResponse> apiResponse = response.body();
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                liveData.postValue(new Result.Success<>(apiResponse.getData()));
            } else {
                String message = apiResponse.getMessage() != null
                        ? apiResponse.getMessage()
                        : fallbackMessage;
                liveData.postValue(new Result.Error<>(new Exception(message), message));
            }
        } else {
            String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
            Log.e("RecordDataSource", errorMessage);
            liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
        }
    }

    private void handleStreakResponse(
            Response<ApiResponse<RecordStreakResponse>> response,
            MutableLiveData<Result<RecordStreakResponse>> liveData,
            String fallbackMessage
    ) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<RecordStreakResponse> apiResponse = response.body();
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                liveData.postValue(new Result.Success<>(apiResponse.getData()));
            } else {
                String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : fallbackMessage;
                liveData.postValue(new Result.Error<>(new Exception(message), message));
            }
        } else {
            String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
            Log.e("RecordDataSource", errorMessage);
            liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
        }
    }

    private <T> void postNetworkFailure(MutableLiveData<Result<T>> liveData, String tag, Throwable t) {
        String errorMessage = "Network Failure: " + t.getMessage();
        Log.e(tag, errorMessage, t);
        Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
        liveData.postValue(new Result.Error<>(exception, errorMessage));
    }
}
