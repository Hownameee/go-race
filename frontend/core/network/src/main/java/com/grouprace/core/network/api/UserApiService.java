package com.grouprace.core.network.api;

import com.grouprace.core.network.model.user.AvatarUploadResponse;
import com.grouprace.core.network.model.user.ChangePasswordPayload;
import com.grouprace.core.network.model.user.ConfirmEmailChangePayload;
import com.grouprace.core.network.model.user.MyProfileInfoPayload;
import com.grouprace.core.network.model.user.ProfileOverviewResponse;
import com.grouprace.core.network.model.user.ResetPasswordWithOtpPayload;
import com.grouprace.core.network.model.user.VerifyEmailOtpPayload;
import com.grouprace.core.network.model.user.VerifyCurrentPasswordPayload;
import com.grouprace.core.network.utils.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.DELETE;
import okhttp3.MultipartBody;

public interface UserApiService {
    @GET("/api/users/me")
    Call<ApiResponse<MyProfileInfoPayload>> getMyInfo();

    @POST("/api/users/me/email/request-otp")
    Call<ApiResponse<Void>> requestEmailChangeOtp();

    @POST("/api/users/me/email/verify-otp")
    Call<ApiResponse<Void>> verifyEmailChangeOtp(@Body VerifyEmailOtpPayload payload);

    @PATCH("/api/users/me/email")
    Call<ApiResponse<Void>> confirmEmailChange(@Body ConfirmEmailChangePayload payload);

    @PATCH("/api/users/me/password")
    Call<ApiResponse<Void>> changePassword(@Body ChangePasswordPayload payload);

    @POST("/api/users/me/password/verify-current")
    Call<ApiResponse<Void>> verifyCurrentPassword(@Body VerifyCurrentPasswordPayload payload);

    @POST("/api/users/me/password/request-otp")
    Call<ApiResponse<Void>> requestPasswordResetOtp();

    @PATCH("/api/users/me/password/reset")
    Call<ApiResponse<Void>> resetPasswordWithOtp(@Body ResetPasswordWithOtpPayload payload);

    @DELETE("/api/users/me")
    Call<ApiResponse<Void>> deleteMyAccount();

    @Multipart
    @POST("/api/users/me/avatar")
    Call<ApiResponse<AvatarUploadResponse>> uploadMyAvatar(@Part MultipartBody.Part avatar);

    @PATCH("/api/users/me")
    Call<ApiResponse<Void>> updateMyInfo(@Body MyProfileInfoPayload payload);

    @GET("/api/users/me/overview")
    Call<ApiResponse<ProfileOverviewResponse>> getMyOverview();
}
