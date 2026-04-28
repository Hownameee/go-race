package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.RecordApiService;
import com.grouprace.core.network.model.CreateRecordRequest;
import com.grouprace.core.network.model.NetworkRecord;
import com.grouprace.core.network.model.RecordPayload;
import com.grouprace.core.network.utils.ApiResponse;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecordNetworkDataSource {

    private final RecordApiService apiService;

    @Inject
    public RecordNetworkDataSource(RecordApiService apiService) {
        this.apiService = apiService;
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
                        Log.d("RecordNetworkDataSource", "Successfully fetched " + apiResponse.getData().getRecords().size() + " records");
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getRecords()));
                    } else {
                        Log.e("RecordNetworkDataSource", "API returned success false or null data. Message: " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e("RecordNetworkDataSource", "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordPayload>> call, Throwable t) {
                Log.e("RecordNetworkDataSource", "Network Failure: " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
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
                        Log.d("RecordNetworkDataSource", "Successfully fetched " + apiResponse.getData().getRecords().size() + " records");
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getRecords()));
                    } else {
                        Log.e("RecordNetworkDataSource", "API returned success false or null data. Message: " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e("RecordNetworkDataSource", "HTTP Error: " + response.code() + " " + response.message());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RecordPayload>> call, Throwable t) {
                Log.e("RecordNetworkDataSource", "Network Failure: " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
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
            public void onFailure(Call<ApiResponse<RecordPayload>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
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
            public void onFailure(Call<ApiResponse<NetworkRecord>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
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
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }
}
