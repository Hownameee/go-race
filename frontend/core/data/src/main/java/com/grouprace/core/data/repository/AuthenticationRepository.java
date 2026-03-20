package com.grouprace.core.data.repository;
import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.grouprace.core.network.model.authentication.LoginPayload;
import com.grouprace.core.network.model.authentication.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

public interface AuthenticationRepository {
    LiveData<ApiResponse<JsonObject>> register(RegisterPayload payload);
    LiveData<ApiResponse<JsonObject>> login(LoginPayload payload);
}
