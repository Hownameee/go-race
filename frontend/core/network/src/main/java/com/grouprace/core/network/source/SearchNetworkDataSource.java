package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.SearchApiService;
import com.grouprace.core.network.model.search.ClubActionResultResponse;
import com.grouprace.core.network.model.search.NetworkUserSearch;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

import java.util.List;

public class SearchNetworkDataSource {

    private final SearchApiService apiService;
    private static final String TAG = "SearchNetworkDataSource";

    @Inject
    public SearchNetworkDataSource(SearchApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<List<NetworkUserSearch>>> searchUsers(String query) {
        MutableLiveData<Result<List<NetworkUserSearch>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.searchUsers(query).enqueue(new ListCallback(liveData, "searchUsers query: " + query));
        return liveData;
    }

    public LiveData<Result<List<NetworkUserSearch>>> getSuggestedUsers() {
        MutableLiveData<Result<List<NetworkUserSearch>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getSuggestedUsers().enqueue(new ListCallback(liveData, "getSuggestedUsers"));
        return liveData;
    }

    // --- CLUB METHODS (Thêm mới) ---

    /**
     * Tìm kiếm Câu lạc bộ theo tên.
     */
    public LiveData<Result<List<NetworkUserSearch>>> searchClubs(String query) {
        MutableLiveData<Result<List<NetworkUserSearch>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.searchClubs(query).enqueue(new ListCallback(liveData, "searchClubs query: " + query));
        return liveData;
    }

    /**
     * Lấy danh sách Câu lạc bộ gợi ý.
     */
    public LiveData<Result<List<NetworkUserSearch>>> getSuggestedClubs() {
        MutableLiveData<Result<List<NetworkUserSearch>>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.getSuggestedClubs().enqueue(new ListCallback(liveData, "getSuggestedClubs"));
        return liveData;
    }

    public LiveData<Result<Boolean>> followUser(int targetUserId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.followUser(targetUserId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), "Network Failure: " + t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<Boolean>> unfollowUser(int targetUserId) {
        MutableLiveData<Result<Boolean>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.unfollowUser(targetUserId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Result.Success<>(true));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), "Network Failure: " + t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<String>> joinClub(int clubId) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.joinClub(clubId).enqueue(new Callback<ApiResponse<ClubActionResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClubActionResultResponse>> call, Response<ApiResponse<ClubActionResultResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String resultText = response.body().getData() != null ? response.body().getData().getResult() : response.body().getMessage();
                    liveData.postValue(new Result.Success<>(resultText));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClubActionResultResponse>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), "Network Failure: " + t.getMessage()));
            }
        });
        return liveData;
    }

    public LiveData<Result<String>> leaveClub(int clubId) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        apiService.leaveClub(clubId).enqueue(new Callback<ApiResponse<ClubActionResultResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ClubActionResultResponse>> call, Response<ApiResponse<ClubActionResultResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String resultText = response.body().getData() != null ? response.body().getData().getResult() : response.body().getMessage();
                    liveData.postValue(new Result.Success<>(resultText));
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "HTTP " + response.code();
                    liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ClubActionResultResponse>> call, Throwable t) {
                liveData.postValue(new Result.Error<>(new Exception(t), "Network Failure: " + t.getMessage()));
            }
        });
        return liveData;
    }

    /**
     * Helper class để tái sử dụng logic xử lý Callback cho danh sách kết quả (User/Club).
     */
    private static class ListCallback implements Callback<ApiResponse<List<NetworkUserSearch>>> {
        private final MutableLiveData<Result<List<NetworkUserSearch>>> liveData;
        private final String methodName;

        public ListCallback(MutableLiveData<Result<List<NetworkUserSearch>>> liveData, String methodName) {
            this.liveData = liveData;
            this.methodName = methodName;
        }

        @Override
        public void onResponse(Call<ApiResponse<List<NetworkUserSearch>>> call, Response<ApiResponse<List<NetworkUserSearch>>> response) {
            if (response.isSuccessful() && response.body() != null) {
                ApiResponse<List<NetworkUserSearch>> apiResponse = response.body();
                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    Log.d(TAG, methodName + " Success: " + apiResponse.getData().size() + " items");
                    liveData.postValue(new Result.Success<>(apiResponse.getData()));
                } else {
                    Log.e(TAG, methodName + " API Error: " + apiResponse.getMessage());
                    liveData.postValue(new Result.Error<>(new Exception(apiResponse.getMessage()), apiResponse.getMessage()));
                }
            } else {
                String error = "HTTP Error: " + response.code();
                Log.e(TAG, methodName + " " + error);
                liveData.postValue(new Result.Error<>(new Exception(error), error));
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<List<NetworkUserSearch>>> call, Throwable t) {
            Log.e(TAG, methodName + " Network Failure: " + t.getMessage());
            liveData.postValue(new Result.Error<>(new Exception(t), "Network Failure: " + t.getMessage()));
        }
    }
}