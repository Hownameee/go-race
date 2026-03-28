package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.AuthApiService;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.LoginResponse;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthDataSource {
    private final AuthApiService apiService;

    @Inject
    public AuthDataSource(AuthApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<Result<Void>> register(RegisterPayload payload) {
        MutableLiveData<Result<Void>> liveData = new MutableLiveData<>();

        // Bắn trạng thái Loading
        liveData.postValue(new Result.Loading<>());

        // Chuyển toàn bộ callback sang ApiResponse<Void>
        apiService.register(payload).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();

                    // Chỉ cần check isSuccess(), không cần check data nữa
                    if (apiResponse.isSuccess()) {
                        Log.d("AuthDataSource", "Register Success.");
                        // Trả về null đại diện cho Void
                        liveData.postValue(new Result.Success<>(null));
                    } else {
                        String msg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Registration failed.";
                        Log.e("AuthDataSource", "Register API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("AuthDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e("AuthDataSource", "Register Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    public LiveData<Result<String>> login(LoginPayload payload) {
        MutableLiveData<Result<String>> liveData = new MutableLiveData<>();

        // Bắn trạng thái Loading
        liveData.postValue(new Result.Loading<>());

        apiService.login(payload).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Log.d("AuthDataSource", "Login Success. Token: " + apiResponse.getData().getToken());
                        // Trả về thẳng Token chuỗi String khi Success
                        liveData.postValue(new Result.Success<>(apiResponse.getData().getToken()));
                    } else {
                        String msg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Login failed.";
                        Log.e("AuthDataSource", "Login API Failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = "HTTP Error: " + response.code() + " " + response.message();
                    Log.e("AuthDataSource", errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e("AuthDataSource", "Login Network Failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }
}