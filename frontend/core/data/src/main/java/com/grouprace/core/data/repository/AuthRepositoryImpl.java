package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import android.util.Log;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.auth.GoogleAuthPayload;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.LoginResponse;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.source.AuthNetworkDataSource;
import com.grouprace.core.network.utils.SessionManager;
import com.grouprace.core.data.SyncManager;

import javax.inject.Inject;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthNetworkDataSource authNetworkDataSource;
    private final SessionManager sessionManager;
    private final SyncManager syncManager;

    @Inject
    public AuthRepositoryImpl(
            AuthNetworkDataSource authNetworkDataSource,
            SessionManager sessionManager,
            SyncManager syncManager
    ) {
        this.authNetworkDataSource = authNetworkDataSource;
        this.sessionManager = sessionManager;
        this.syncManager = syncManager;
    }

    @Override
    public LiveData<Result<Void>> register(RegisterPayload payload) {
        return authNetworkDataSource.register(payload);
    }

    @Override
    public LiveData<Result<Void>> login(LoginPayload payload) {
        LiveData<Result<LoginResponse>> networkResult = authNetworkDataSource.login(payload);

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();

            } else if (result instanceof Result.Success) {
                LoginResponse response = ((Result.Success<LoginResponse>) result).data;
                if (response == null
                        || response.getAccessToken() == null
                        || response.getRefreshToken() == null) {
                    return new Result.Error<>(new IllegalStateException("Missing token response"), "Missing token response");
                }

                sessionManager.saveSession(response.getAccessToken(), response.getRefreshToken());
                syncManager.scheduleUserProfileSync();

                return new Result.Success<>(null);

            } else {
                Result.Error<LoginResponse> error = (Result.Error<LoginResponse>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
    }

    @Override
    public LiveData<Result<Void>> requestPasswordResetOtp(String email) {
        return authNetworkDataSource.requestPasswordResetOtp(email);
    }

    @Override
    public LiveData<Result<Void>> verifyPasswordResetOtp(String email, String otpCode) {
        return authNetworkDataSource.verifyPasswordResetOtp(email, otpCode);
    }

    @Override
    public LiveData<Result<Void>> resetPasswordWithOtp(String email, String otpCode, String newPassword, String confirmNewPassword) {
        return authNetworkDataSource.resetPasswordWithOtp(email, otpCode, newPassword, confirmNewPassword);
    }

    @Override
    public LiveData<Boolean> getIsLoggedIn() {
        return sessionManager.getLoginState();
    }

    @Override
    public void logout() {
        sessionManager.clearSession();
    }

    @Override
    public LiveData<Result<GoogleAuthResponse>> googleAuth(GoogleAuthPayload payload) {
        LiveData<Result<GoogleAuthResponse>> networkResult = authNetworkDataSource.googleAuth(payload);

        return Transformations.map(networkResult, result -> {
            if (result instanceof  Result.Loading) {
                return new Result.Loading<>();
            } else if (result instanceof Result.Success) {
                GoogleAuthResponse response = ((Result.Success<GoogleAuthResponse>) result).data;

                if (response == null) {
                    return new Result.Error<>(new IllegalStateException("Empty Google auth response"), "Empty Google auth response");
                }

                if (response != null
                        && response.getAccessToken() != null
                        && !response.getAccessToken().isEmpty()
                        && response.getRefreshToken() != null
                        && !response.getRefreshToken().isEmpty()) {
                  sessionManager.saveSession(response.getAccessToken(), response.getRefreshToken());
                  syncManager.scheduleUserProfileSync();
                  Log.d("AuthRepository", "Google session saved.");
                } else if (!response.isRequiresProfileCompletion()) {
                    return new Result.Error<>(
                            new IllegalStateException("Missing Google auth tokens"),
                            "Google login succeeded but tokens were missing."
                    );
                }

                return new Result.Success<>(response);
            } else {
                Result.Error<GoogleAuthResponse> error = (Result.Error<GoogleAuthResponse>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
    }

    @Override
    public LiveData<Result<Boolean>>  registerDeviceToken(String token) {
        return authNetworkDataSource.registerDeviceToken(token);
    }

    @Override
    public LiveData<Result<Boolean>>  unregisterDeviceToken(String token) {
        return authNetworkDataSource.unregisterDeviceToken(token);
    }
}
