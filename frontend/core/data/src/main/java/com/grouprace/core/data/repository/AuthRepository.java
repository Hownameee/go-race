package com.grouprace.core.data.repository;
import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.model.auth.LoginPayload;
import com.grouprace.core.network.model.auth.LoginResponse;
import com.grouprace.core.network.model.auth.RegisterPayload;
import com.grouprace.core.network.utils.ApiResponse;

public interface AuthRepository {
    LiveData<Result<Void>> register(RegisterPayload payload);
    LiveData<Result<Void>> login(LoginPayload payload);
}
