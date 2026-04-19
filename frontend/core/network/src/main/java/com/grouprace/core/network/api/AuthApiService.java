package com.grouprace.core.network.api;

import com.grouprace.core.network.model.auth.GoogleAuthPayload;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.LoginResponse;
import com.grouprace.core.network.model.auth.RequestPasswordResetOtpPayload;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.model.auth.VerifyPasswordResetOtpPayload;
import com.grouprace.core.network.model.notification.RegisterDeviceTokenRequest;
import com.grouprace.core.network.model.user.ResetPasswordWithOtpPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

public interface AuthApiService {
    // return new id
    @POST("/api/auth/register")
    Call<ApiResponse<Void>> register(@Body RegisterPayload registerPayload);

    // return token
    @POST("/api/auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginPayload loginPayload);

    @POST("/api/auth/password/request-otp")
    Call<ApiResponse<Void>> requestPasswordResetOtp(@Body RequestPasswordResetOtpPayload payload);

    @POST("/api/auth/password/verify-otp")
    Call<ApiResponse<Void>> verifyPasswordResetOtp(@Body VerifyPasswordResetOtpPayload payload);

    @PATCH("/api/auth/password/reset")
    Call<ApiResponse<Void>> resetPasswordWithOtp(@Body ResetPasswordWithOtpPayload payload);

    @POST("api/device-tokens")
    Call<ApiResponse<Object>> registerDeviceToken(@Body RegisterDeviceTokenRequest body);

    @POST("/api/auth/google")
    Call<ApiResponse<GoogleAuthResponse>> googleAuth(@Body GoogleAuthPayload payload);
}
