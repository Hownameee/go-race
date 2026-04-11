package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.ClubApiService;
import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.utils.ApiResponse;


import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubNetworkDataSource {

    private static final String TAG = "ClubNetworkDataSource";
    private final ClubApiService apiService;

    @Inject
    public ClubNetworkDataSource(ClubApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<ClubListPayload>> getClubs(int offset, int limit) {
        MutableLiveData<Result<ClubListPayload>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getClubs(offset, limit).enqueue(new Callback<ApiResponse<ClubListPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClubListPayload>> call, Response<ApiResponse<ClubListPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ClubListPayload> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "getClubs: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "getClubs: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClubListPayload>> call, Throwable t) {
                Log.e(TAG, "getClubs: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }
}
