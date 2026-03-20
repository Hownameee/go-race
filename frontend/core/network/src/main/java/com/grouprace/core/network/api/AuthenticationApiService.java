package com.grouprace.core.network.api;

import com.google.gson.JsonObject;
import com.grouprace.core.network.model.authentication.LoginPayload;
import com.grouprace.core.network.model.authentication.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationApiService {
    // return new id
    @POST("/auth/register")
    Call<ApiResponse<JsonObject>> register(@Body RegisterPayload registerPayload);

    // return token
    @POST("/auth/login")
    Call<ApiResponse<JsonObject>> login(@Body LoginPayload loginPayload);
}
