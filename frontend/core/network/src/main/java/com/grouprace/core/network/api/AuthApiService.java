package com.grouprace.core.network.api;

import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.LoginResponse;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    // return new id
    @POST("/auth/register")
    Call<ApiResponse<Void>> register(@Body RegisterPayload registerPayload);

    // return token
    @POST("/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginPayload loginPayload);
}
