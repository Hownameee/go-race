package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;

public interface FollowRepository {
    LiveData<Result<Boolean>> followUser(int targetUserId);

    LiveData<Result<Boolean>> unfollowUser(int targetUserId);
}
