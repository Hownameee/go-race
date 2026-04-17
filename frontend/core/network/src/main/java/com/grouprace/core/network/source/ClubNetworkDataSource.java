package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.ClubApiService;
import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.ClubPayload;
import com.grouprace.core.network.model.club.JoinClubResponse;
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

    public LiveData<Result<ClubPayload>> getClubById(int clubId) {
        MutableLiveData<Result<ClubPayload>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getClub(clubId).enqueue(new Callback<ApiResponse<ClubPayload>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClubPayload>> call, Response<ApiResponse<ClubPayload>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ClubPayload> apiResponse = response.body();
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
            public void onFailure(Call<ApiResponse<ClubPayload>> call, Throwable t) {
                Log.e(TAG, "getClubs: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }


    public LiveData<Result<JoinClubResponse>> joinClub(int clubId) {
        MutableLiveData<Result<JoinClubResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.joinClub(clubId).enqueue(new Callback<ApiResponse<JoinClubResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<JoinClubResponse>> call, Response<ApiResponse<JoinClubResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<JoinClubResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "joinClub: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "joinClub: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JoinClubResponse>> call, Throwable t) {
                Log.e(TAG, "joinClub: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<JoinClubResponse>> leaveClub(int clubId) {
        MutableLiveData<Result<JoinClubResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.leaveClub(clubId).enqueue(new Callback<ApiResponse<JoinClubResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<JoinClubResponse>> call, Response<ApiResponse<JoinClubResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<JoinClubResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "leaveClub: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "leaveClub: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JoinClubResponse>> call, Throwable t) {
                Log.e(TAG, "leaveClub: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<String>> createClub(com.grouprace.core.network.model.club.CreateClubRequest request) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.createClub(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>("Club created successfully"));
                    } else {
                        Log.e(TAG, "createClub: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "createClub: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "createClub: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>> getAdmins(int clubId) {
        MutableLiveData<Result<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getAdmins(clubId).enqueue(new Callback<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>> call, Response<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "getAdmins: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "getAdmins: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubAdmin>>> call, Throwable t) {
                Log.e(TAG, "getAdmins: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }
}
