package com.grouprace.core.data.repository;
import androidx.lifecycle.LiveData;

import com.google.gson.JsonObject;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

public interface AuthRepository {
    LiveData<ApiResponse<JsonObject>> register(RegisterPayload payload);
    LiveData<ApiResponse<JsonObject>> login(LoginPayload payload);
}
