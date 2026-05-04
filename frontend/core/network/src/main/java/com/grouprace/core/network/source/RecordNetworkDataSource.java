package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.RecordApiService;
import com.grouprace.core.network.model.CreateRecordRequest;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.RecordPayload;
import com.grouprace.core.network.model.record.RecordProfileStatisticsResponse;
import com.grouprace.core.network.model.record.RecordStreakResponse;
import com.grouprace.core.network.model.record.RecordWeeklySummaryResponse;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordNetworkDataSource {

    private static final String TAG = "RecordNetworkDataSource";

    private final RecordApiService apiService;

    @Inject
    public RecordNetworkDataSource(RecordApiService apiService) {
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
                        handleWeeklySummaryResponse(response, liveData, "Load weekly summary failed.");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<RecordWeeklySummaryResponse>> call, Throwable throwable) {
                        postNetworkFailure(liveData, throwable);
                    }
                });

        return liveData;
    }

    public LiveData<Result<RecordWeeklySummaryResponse>> getUserWeeklySummary(int userId, String activityType, int weeks) {
        MutableLiveData<Result<RecordWeeklySummaryResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserWeeklySummary(userId, activityType, weeks)
                .enqueue(new Callback<ApiResponse<RecordWeeklySummaryResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<RecordWeeklySummaryResponse>> call,
                                           Response<ApiResponse<RecordWeeklySummaryResponse>> response) {
                        handleWeeklySummaryResponse(response, liveData, "Load user weekly summary failed.");
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<RecordWeeklySummaryResponse>> call, Throwable throwable) {
                        postNetworkFailure(liveData, throwable);
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
                    public void onFailure(Call<ApiResponse<RecordProfileStatisticsResponse>> call, Throwable throwable) {
                        postNetworkFailure(liveData, throwable);
                    }
                });

        return liveData;
    }

    public LiveData<Result<RecordProfileStatisticsResponse>> getUserProfileStatistics(int userId, String activityType) {
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
                    public void onFailure(Call<ApiResponse<RecordProfileStatisticsResponse>> call, Throwable throwable) {
                        postNetworkFailure(liveData, throwable);
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
            public void onFailure(Call<ApiResponse<RecordStreakResponse>> call, Throwable throwable) {
                postNetworkFailure(liveData, throwable);
            }
        });

        return liveData;
    }

    public LiveData<Result<RecordStreakResponse>> getUserStreak(int userId) {
        MutableLiveData<Result<RecordStreakResponse>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getUserStreak(userId).enqueue(new Callback<ApiResponse<RecordStreakResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecordStreakResponse>> call,
                                   Response<ApiResponse<RecordStreakResponse>> response) {
                handleStreakResponse(response, liveData, "Load user streak failed.");
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordStreakResponse>> call, Throwable throwable) {
                postNetworkFailure(liveData, throwable);
            }
        });

        return liveData;
    }

    public LiveData<Result<List<NetworkRecord>>> getRecord(int recordId) {
        MutableLiveData<Result<List<NetworkRecord>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getRecord(recordId).enqueue(new Callback<ApiResponse<RecordPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecordPayload>> call, Response<ApiResponse<RecordPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RecordPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d(TAG, "Successfully fetched " + apiResponse.getData().getRecords().size() + " records");
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getRecords()));
                    } else {
                        Log.e(TAG, "API returned success false or null data. Message: " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordPayload>> call, Throwable throwable) {
                Log.e(TAG, "Network Failure: " + throwable.getMessage(), throwable);
                liveData.postValue(new Result.Error<>(new Exception(throwable), throwable.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<List<NetworkRecord>>> getRecords(int offset, int limit) {
        MutableLiveData<Result<List<NetworkRecord>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getRecords(offset, limit).enqueue(new Callback<ApiResponse<RecordPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecordPayload>> call, Response<ApiResponse<RecordPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RecordPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d(TAG, "Successfully fetched " + apiResponse.getData().getRecords().size() + " records");
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getRecords()));
                    } else {
                        Log.e(TAG, "API returned success false or null data. Message: " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordPayload>> call, Throwable throwable) {
                Log.e(TAG, "Network Failure: " + throwable.getMessage(), throwable);
                liveData.postValue(new Result.Error<>(new Exception(throwable), throwable.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<List<NetworkRecord>>> getUserRecords(int userId, int offset, int limit) {
        MutableLiveData<Result<List<NetworkRecord>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getUserRecords(userId, offset, limit).enqueue(new Callback<ApiResponse<RecordPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecordPayload>> call, Response<ApiResponse<RecordPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<RecordPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getRecords()));
                    } else {
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordPayload>> call, Throwable throwable) {
                liveData.postValue(new Result.Error<>(new Exception(throwable), throwable.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<NetworkRecord>> createRecord(CreateRecordRequest request) {
        MutableLiveData<Result<NetworkRecord>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.createRecord(request).enqueue(new Callback<ApiResponse<NetworkRecord>>() {
            @Override
            public void onResponse(Call<ApiResponse<NetworkRecord>> call, Response<ApiResponse<NetworkRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<NetworkRecord> apiResponse = response.body();
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
            public void onFailure(Call<ApiResponse<NetworkRecord>> call, Throwable throwable) {
                liveData.postValue(new Result.Error<>(new Exception(throwable), throwable.getMessage()));
            }
        });

        return liveData;
    }

    public Result<NetworkRecord> createRecordSync(CreateRecordRequest request) {
        try {
            Response<ApiResponse<NetworkRecord>> response = apiService.createRecord(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                ApiResponse<NetworkRecord> apiResponse = response.body();
                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    return new Result.Success<>(apiResponse.getData());
                } else {
                    return new Result.Error<>(null, apiResponse.getMessage());
                }
            } else {
                return new Result.Error<>(null, "HTTP Error: " + response.code());
            }
        } catch (java.io.IOException e) {
            return new Result.Error<>(e, e.getMessage());
        }
    }

    public LiveData<Result<Void>> updateRecord(long recordId, java.util.Map<String, Object> updateData) {
        MutableLiveData<Result<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.updateRecord(recordId, updateData).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>(null));
                    } else {
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable throwable) {
                liveData.postValue(new Result.Error<>(new Exception(throwable), throwable.getMessage()));
            }
        });

        return liveData;
    }

    public Result<Void> updateRecordSync(long recordId, java.util.Map<String, Object> updateData) {
        try {
            Response<ApiResponse<Void>> response = apiService.updateRecord(recordId, updateData).execute();
            if (response.isSuccessful() && response.body() != null) {
                ApiResponse<Void> apiResponse = response.body();
                if (apiResponse.isSuccess()) {
                    return new Result.Success<>(null);
                } else {
                    return new Result.Error<>(null, apiResponse.getMessage());
                }
            } else {
                return new Result.Error<>(null, "HTTP Error: " + response.code());
            }
        } catch (java.io.IOException e) {
            return new Result.Error<>(e, e.getMessage());
        }
    }

    private void handleWeeklySummaryResponse(
            Response<ApiResponse<RecordWeeklySummaryResponse>> response,
            MutableLiveData<Result<RecordWeeklySummaryResponse>> liveData,
            String fallbackMessage
    ) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<RecordWeeklySummaryResponse> apiResponse = response.body();
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                liveData.postValue(new Result.Success<>(apiResponse.getData()));
            } else {
                String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : fallbackMessage;
                liveData.postValue(new Result.Error<>(new Exception(message), message));
            }
        } else {
            String message = "HTTP Error: " + response.code() + " " + response.message();
            Log.e(TAG, message);
            liveData.postValue(new Result.Error<>(new Exception(message), message));
        }
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
                String message = apiResponse.getMessage() != null ? apiResponse.getMessage() : fallbackMessage;
                liveData.postValue(new Result.Error<>(new Exception(message), message));
            }
        } else {
            String message = "HTTP Error: " + response.code() + " " + response.message();
            Log.e(TAG, message);
            liveData.postValue(new Result.Error<>(new Exception(message), message));
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
            String message = "HTTP Error: " + response.code() + " " + response.message();
            Log.e(TAG, message);
            liveData.postValue(new Result.Error<>(new Exception(message), message));
        }
    }

    private <T> void postNetworkFailure(MutableLiveData<Result<T>> liveData, Throwable throwable) {
        String message = "Network Failure: " + throwable.getMessage();
        Log.e(TAG, message, throwable);
        Exception exception = (throwable instanceof Exception) ? (Exception) throwable : new Exception(throwable);
        liveData.postValue(new Result.Error<>(exception, message));
    }
}
