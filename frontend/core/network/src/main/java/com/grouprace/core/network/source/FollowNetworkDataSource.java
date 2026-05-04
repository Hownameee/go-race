package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.FollowApiService;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowNetworkDataSource {
    private final FollowApiService apiService;

    @Inject
    public FollowNetworkDataSource(FollowApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<Boolean>> followUser(int targetUserId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.followUser(targetUserId).enqueue(new FollowActionCallback(liveData));
        return liveData;
    }

    public LiveData<Result<Boolean>> unfollowUser(int targetUserId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.unfollowUser(targetUserId).enqueue(new FollowActionCallback(liveData));
        return liveData;
    }

    private static class FollowActionCallback implements Callback<ApiResponse<Void>> {
        private final MutableLiveData<Result<Boolean>> liveData;

        private FollowActionCallback(MutableLiveData<Result<Boolean>> liveData) {
            this.liveData = liveData;
        }

        @Override
        public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                liveData.postValue(new Result.Success<>(true));
                return;
            }

            String message = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
            liveData.postValue(new Result.Error<>(new Exception(message), message));
        }

        @Override
        public void onFailure(Call<ApiResponse<Void>> call, Throwable throwable) {
            String message = "Network Failure: " + throwable.getMessage();
            liveData.postValue(new Result.Error<>(new Exception(throwable), message));
        }
    }
}
