package com.grouprace.core.network.source;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.api.AuthApiService;
import com.grouprace.core.network.model.auth.GoogleAuthPayload;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.LoginResponse;
import com.grouprace.core.network.model.auth.RefreshTokenPayload;
import com.grouprace.core.network.model.auth.RequestPasswordResetOtpPayload;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.model.auth.VerifyPasswordResetOtpPayload;
import com.grouprace.core.network.model.notification.RegisterDeviceTokenRequest;
import com.grouprace.core.network.model.user.ResetPasswordWithOtpPayload;
import com.grouprace.core.network.utils.ApiResponse;

import org.json.JSONObject;

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
        return executeCall(
                apiService.register(payload),
                "Registration failed.",
                "Register",
                ignored -> null
        );
    }

    public LiveData<Result<LoginResponse>> login(LoginPayload payload) {
        return executeCall(
                apiService.login(payload),
                "Login failed.",
                "Login",
                response -> response
        );
    }

    public LiveData<Result<LoginResponse>> refreshToken(String refreshToken) {
        return executeCall(
                apiService.refreshToken(new RefreshTokenPayload(refreshToken)),
                "Refresh token failed.",
                "Refresh token",
                response -> response
        );
    }

    public LiveData<Result<Void>> requestPasswordResetOtp(String email) {
        return executeCall(
                apiService.requestPasswordResetOtp(new RequestPasswordResetOtpPayload(email)),
                "Request password reset OTP failed.",
                "Request password reset OTP",
                ignored -> null
        );
    }

    public LiveData<Result<Void>> verifyPasswordResetOtp(String email, String otpCode) {
        return executeCall(
                apiService.verifyPasswordResetOtp(new VerifyPasswordResetOtpPayload(email, otpCode)),
                "Verify password reset OTP failed.",
                "Verify password reset OTP",
                ignored -> null
        );
    }

    public LiveData<Result<Void>> resetPasswordWithOtp(
            String email,
            String otpCode,
            String newPassword,
            String confirmNewPassword
    ) {
        return executeCall(
                apiService.resetPasswordWithOtp(
                        new ResetPasswordWithOtpPayload(email, otpCode, newPassword, confirmNewPassword)
                ),
                "Reset password failed.",
                "Reset password",
                ignored -> null
        );
    }

    public  LiveData<Result<GoogleAuthResponse>> googleAuth(GoogleAuthPayload payload) {
        return executeCall(
            apiService.googleAuth(payload),
            "Google authentication failed.",
            "Google auth",
            response -> response
        );
    }

    private <T, R> LiveData<Result<R>> executeCall(
            Call<ApiResponse<T>> call,
            String fallbackMessage,
            String actionName,
            DataMapper<T, R> mapper
    ) {
        MutableLiveData<Result<R>> liveData = new MutableLiveData<>();
        liveData.postValue(new Result.Loading<>());

        call.enqueue(new Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<T> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        R mappedData = mapper.map(apiResponse.getData());
                        if (mappedData != null || apiResponse.getData() == null) {
                            Log.d("AuthDataSource", actionName + " success.");
                            liveData.postValue(new Result.Success<>(mappedData));
                        } else {
                            Log.e("AuthDataSource", actionName + " mapping failed: " + fallbackMessage);
                            liveData.postValue(new Result.Error<>(new Exception(fallbackMessage), fallbackMessage));
                        }
                    } else {
                        String msg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : fallbackMessage;
                        Log.e("AuthDataSource", actionName + " API failed: " + msg);
                        liveData.postValue(new Result.Error<>(new Exception(msg), msg));
                    }
                } else {
                    String errorMessage = extractErrorMessage(response, fallbackMessage);
                    Log.e("AuthDataSource", actionName + " HTTP failed: " + errorMessage);
                    liveData.postValue(new Result.Error<>(new Exception(errorMessage), errorMessage));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                Log.e("AuthDataSource", actionName + " network failure: " + t.getMessage(), t);
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                liveData.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return liveData;
    }

    private String extractErrorMessage(Response<?> response, String fallbackMessage) {
        if (response == null) {
            return fallbackMessage;
        }

        try {
            if (response.errorBody() != null) {
                String rawError = response.errorBody().string();
                if (rawError != null && !rawError.trim().isEmpty()) {
                    JSONObject jsonObject = new JSONObject(rawError);
                    String message = jsonObject.optString("message", null);
                    if (message != null && !message.trim().isEmpty()) {
                        return message;
                    }
                }
            }
        } catch (Exception ignored) {
        }

        String responseMessage = response.message();
        if (responseMessage != null && !responseMessage.trim().isEmpty()) {
            return responseMessage;
        }

        return fallbackMessage;
    }

    private interface DataMapper<T, R> {
        R map(T data);
    }

    public LiveData<Result<Boolean>> registerDeviceToken(String token) {
        MutableLiveData<Result<Boolean>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        RegisterDeviceTokenRequest body = new RegisterDeviceTokenRequest(token, "android");

        apiService.registerDeviceToken(body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("DeviceToken", "register success=true");
                    result.postValue(new Result.Success<>(true));
                } else {
                    String message = extractErrorMessage(response, "Register device token failed.");
                    Log.e("DeviceToken", "register HTTP " + response.code() + " " + message);
                    result.postValue(new Result.Error<>(new Exception(message), message));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e("DeviceToken", "register failure: " + t.getMessage());
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                result.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<Result<Boolean>> unregisterDeviceToken(String token) {
        MutableLiveData<Result<Boolean>> result = new MutableLiveData<>();
        result.setValue(new Result.Loading<>());

        RegisterDeviceTokenRequest body = new RegisterDeviceTokenRequest(token, "android");

        apiService.unregisterDeviceToken(body).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d("DeviceToken", "unregister success=true");
                    result.postValue(new Result.Success<>(true));
                } else {
                    String message = extractErrorMessage(response, "Unregister device token failed.");
                    Log.e("DeviceToken", "unregister HTTP " + response.code() + " " + message);
                    result.postValue(new Result.Error<>(new Exception(message), message));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e("DeviceToken", "unregister failure: " + t.getMessage());
                Exception exception = (t instanceof Exception) ? (Exception) t : new Exception(t);
                result.postValue(new Result.Error<>(exception, "Network Failure: " + t.getMessage()));
            }
        });

        return result;
    }
}
