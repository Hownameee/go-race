package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;
import com.google.gson.JsonObject;
import com.grouprace.core.network.model.authentication.LoginPayload;
import com.grouprace.core.network.model.authentication.RegisterPayload;
import com.grouprace.core.network.source.AuthenticationDataSource;
import com.grouprace.core.network.utils.ApiResponse;

import javax.inject.Inject;

public class AuthenticationRepositoryImpl implements AuthenticationRepository {

    private final AuthenticationDataSource authNetworkDataSource;

    @Inject
    public AuthenticationRepositoryImpl(AuthenticationDataSource authNetworkDataSource) {
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