package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.auth.GoogleAuthPayload;
import com.grouprace.core.network.model.auth.GoogleAuthResponse;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.source.AuthDataSource;
import com.grouprace.core.network.utils.SessionManager;

import javax.inject.Inject;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthDataSource authNetworkDataSource;
    private final SessionManager sessionManager;
    private final MutableLiveData<Boolean> _isLoggedIn = new MutableLiveData<>();

    @Inject
    public AuthRepositoryImpl(AuthDataSource authNetworkDataSource, SessionManager sessionManager) {
        this.authNetworkDataSource = authNetworkDataSource;
        this.sessionManager = sessionManager;
        this._isLoggedIn.setValue(sessionManager.isLoggedIn());
    }

    @Override
    public LiveData<Result<Void>> register(RegisterPayload payload) {
        return authNetworkDataSource.register(payload);
    }

    @Override
    public LiveData<Result<Void>> login(LoginPayload payload) {
        LiveData<Result<String>> networkResult = authNetworkDataSource.login(payload);

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();

            } else if (result instanceof Result.Success) {
                String token = ((Result.Success<String>) result).data;
                sessionManager.saveAuthToken(token);
                _isLoggedIn.setValue(true);

                return new Result.Success<>(null);

            } else {
                Result.Error<String> error = (Result.Error<String>) result;
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
        return _isLoggedIn;
    }

    @Override
    public void logout() {
        sessionManager.clearSession();
        _isLoggedIn.setValue(false);
    }

    @Override
    public LiveData<Result<GoogleAuthResponse>> googleAuth(GoogleAuthPayload payload) {
        LiveData<Result<GoogleAuthResponse>> networkResult = authNetworkDataSource.googleAuth(payload);

        return Transformations.map(networkResult, result -> {
            if (result instanceof  Result.Loading) {
                return new Result.Loading<>();
            } else if (result instanceof Result.Success) {
                GoogleAuthResponse response = ((Result.Success<GoogleAuthResponse>) result).data;

                if (response != null && response.getToken() != null && !response.getToken().isEmpty()) {
                  sessionManager.saveAuthToken(response.getToken());
                  _isLoggedIn.setValue(true);
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
}
