package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.source.AuthDataSource;
import com.grouprace.core.network.utils.SessionManager;

import javax.inject.Inject;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthDataSource authNetworkDataSource;
    private final SessionManager sessionManager; // 1. Khai báo SessionManager

    // 2. Inject SessionManager qua Hilt thay vì dùng 'new'
    @Inject
    public AuthRepositoryImpl(AuthDataSource authNetworkDataSource, SessionManager sessionManager) {
        this.authNetworkDataSource = authNetworkDataSource;
        this.sessionManager = sessionManager;
    }

    @Override
    public LiveData<Result<Void>> register(RegisterPayload payload) {
        return authNetworkDataSource.register(payload);
    }

    // 3. Đổi Result<void> thành Result<Void>
    @Override
    public LiveData<Result<Void>> login(LoginPayload payload) {
        LiveData<Result<String>> networkResult = authNetworkDataSource.login(payload);

        return Transformations.map(networkResult, result -> {
            if (result instanceof Result.Loading) {
                return new Result.Loading<>();

            } else if (result instanceof Result.Success) {
                String token = ((Result.Success<String>) result).data;
                sessionManager.saveAuthToken(token);

                return new Result.Success<>(null);

            } else {
                Result.Error<String> error = (Result.Error<String>) result;
                return new Result.Error<>(error.exception, error.message);
            }
        });
    }
}