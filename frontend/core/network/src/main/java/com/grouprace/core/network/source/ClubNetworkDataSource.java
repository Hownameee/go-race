package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.ClubApiService;
import com.grouprace.core.network.model.club.ClubListPayload;
import com.grouprace.core.network.model.club.ClubPayload;
import com.grouprace.core.network.model.club.IsLeaderResponse;
import com.grouprace.core.network.model.club.JoinClubResponse;
import com.grouprace.core.network.model.club.UpdateClubRequest;
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

    public LiveData<Result<Boolean>> checkIsLeader(int clubId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.checkIsLeader(clubId).enqueue(new Callback<ApiResponse<IsLeaderResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<IsLeaderResponse>> call, Response<ApiResponse<IsLeaderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<IsLeaderResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().isLeader()));
                    } else {
                        liveData.postValue(new Result.Success<>(false));
                    }
                } else {
                    liveData.postValue(new Result.Success<>(false));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<IsLeaderResponse>> call, Throwable t) {
                Log.e(TAG, "checkIsLeader: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Success<>(false));
            }
        });

        return liveData;
    }

    public LiveData<Result<Boolean>> checkIsAdmin(int clubId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.checkIsAdmin(clubId).enqueue(new Callback<ApiResponse<com.grouprace.core.network.model.club.IsAdminResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.grouprace.core.network.model.club.IsAdminResponse>> call, Response<ApiResponse<com.grouprace.core.network.model.club.IsAdminResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<com.grouprace.core.network.model.club.IsAdminResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData().isAdmin()));
                    } else {
                        liveData.postValue(new Result.Success<>(false));
                    }
                } else {
                    liveData.postValue(new Result.Success<>(false));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.grouprace.core.network.model.club.IsAdminResponse>> call, Throwable t) {
                Log.e(TAG, "checkIsAdmin: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Success<>(false));
            }
        });

        return liveData;
    }

    public LiveData<Result<String>> updateClub(int clubId, UpdateClubRequest request) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.updateClub(clubId, request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>("Club updated"));
                    } else {
                        Log.e(TAG, "updateClub: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "updateClub: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "updateClub: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return liveData;
    }
    public LiveData<Result<com.grouprace.core.network.model.club.NetworkClubStats>> getClubStats(int clubId) {
        MutableLiveData<Result<com.grouprace.core.network.model.club.NetworkClubStats>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getClubStats(clubId).enqueue(new Callback<ApiResponse<com.grouprace.core.network.model.club.NetworkClubStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.grouprace.core.network.model.club.NetworkClubStats>> call, Response<ApiResponse<com.grouprace.core.network.model.club.NetworkClubStats>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<com.grouprace.core.network.model.club.NetworkClubStats> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "getClubStats: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "getClubStats: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.grouprace.core.network.model.club.NetworkClubStats>> call, Throwable t) {
                Log.e(TAG, "getClubStats: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<String>> createEvent(int clubId, com.grouprace.core.network.model.club.CreateClubEventRequest request) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.createEvent(clubId, request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>("Event created"));
                    } else {
                        Log.e(TAG, "createEvent: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "createEvent: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "createEvent: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>> getEvents(int clubId) {
        MutableLiveData<Result<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getEvents(clubId).enqueue(new Callback<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>> call, Response<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "getEvents: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "getEvents: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubEvent>>> call, Throwable t) {
                Log.e(TAG, "getEvents: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<String>> joinEvent(int clubId, int eventId) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.joinEvent(clubId, eventId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>("Joined event"));
                    } else {
                        Log.e(TAG, "joinEvent: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "joinEvent: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "joinEvent: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<com.grouprace.core.network.model.club.NetworkEventStats>> getEventStats(int clubId, int eventId) {
        MutableLiveData<Result<com.grouprace.core.network.model.club.NetworkEventStats>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getEventStats(clubId, eventId).enqueue(new Callback<ApiResponse<com.grouprace.core.network.model.club.NetworkEventStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.grouprace.core.network.model.club.NetworkEventStats>> call, Response<ApiResponse<com.grouprace.core.network.model.club.NetworkEventStats>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<com.grouprace.core.network.model.club.NetworkEventStats> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        liveData.postValue(new Result.Success<>(apiResponse.getData()));
                    } else {
                        Log.e(TAG, "getEventStats: API error - " + apiResponse.getMessage());
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    Log.e(TAG, "getEventStats: HTTP " + response.code());
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.grouprace.core.network.model.club.NetworkEventStats>> call, Throwable t) {
                Log.e(TAG, "getEventStats: network failure - " + t.getMessage(), t);
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>> getMembers(int clubId) {
        MutableLiveData<Result<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        apiService.getMembers(clubId).enqueue(new Callback<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>> call, Response<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>> apiResponse = response.body();
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
            public void onFailure(Call<ApiResponse<java.util.List<com.grouprace.core.network.model.club.NetworkClubMember>>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<String>> updateMemberStatus(int clubId, int userId, String status) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("status", status);

        apiService.updateMemberStatus(clubId, userId, body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>("Status updated"));
                    } else {
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<String>> updateMemberRole(int clubId, int userId, String role) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.setValue(new Result.Loading<>());

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("role", role);

        apiService.updateMemberRole(clubId, userId, body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        liveData.postValue(new Result.Success<>("Role updated"));
                    } else {
                        liveData.postValue(new Result.Error<>(null, apiResponse.getMessage()));
                    }
                } else {
                    liveData.postValue(new Result.Error<>(null, "HTTP Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), t.getMessage()));
            }
        });

        return liveData;
    }
}
