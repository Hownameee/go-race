package com.grouprace.core.network.source;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonObject;
import com.grouprace.core.network.api.AuthApiService;
import com.grouprace.core.network.model.auth.LoginPayload;
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

    public LiveData<ApiResponse<JsonObject>> register(RegisterPayload payload) {
        MutableLiveData<ApiResponse<JsonObject>> liveData = new MutableLiveData<>();

        apiService.register(payload).enqueue(new Callback<ApiResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ApiResponse<JsonObject>> call, Response<ApiResponse<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    ApiResponse<JsonObject> errorRes = new ApiResponse<>();
                    errorRes.setSuccess(false);
                    errorRes.setMessage("Registration failed. Please try again.");
                    liveData.setValue(errorRes);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JsonObject>> call, Throwable t) {
                ApiResponse<JsonObject> errorRes = new ApiResponse<>();
                errorRes.setSuccess(false);
                errorRes.setMessage("Network error: " + t.getMessage());
                liveData.setValue(errorRes);
            }
        });

        return liveData;
    }

    public LiveData<ApiResponse<JsonObject>> login(LoginPayload payload) {
        MutableLiveData<ApiResponse<JsonObject>> liveData = new MutableLiveData<>();

        apiService.login(payload).enqueue(new Callback<ApiResponse<JsonObject>>() {
            @Override
            public void onResponse(Call<ApiResponse<JsonObject>> call, Response<ApiResponse<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.setValue(response.body());
                } else {
                    ApiResponse<JsonObject> errorRes = new ApiResponse<>();
                    errorRes.setSuccess(false);
                    errorRes.setMessage("Login failed. Please check your credentials.");
                    liveData.setValue(errorRes);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<JsonObject>> call, Throwable t) {
                ApiResponse<JsonObject> errorRes = new ApiResponse<>();
                errorRes.setSuccess(false);
                errorRes.setMessage("Network error: " + t.getMessage());
                liveData.setValue(errorRes);
            }
        });

        return liveData;
    }
}
