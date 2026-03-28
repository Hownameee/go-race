package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.google.gson.JsonObject;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.source.AuthDataSource;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthDataSource authNetworkDataSource;

    @Inject
    public AuthRepositoryImpl(AuthDataSource authNetworkDataSource) {
        this.authNetworkDataSource = authNetworkDataSource;
    }

    @Override
    public LiveData<ApiResponse<JsonObject>> register(RegisterPayload payload) {
        return authNetworkDataSource.register(payload);
    }

    @Override
    public LiveData<ApiResponse<JsonObject>> login(LoginPayload payload) {
        return authNetworkDataSource.login(payload);
    }
}