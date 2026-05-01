package com.grouprace.core.data.repository;

import androidx.lifecycle.LiveData;

import com.grouprace.core.common.result.Result;
import com.grouprace.core.network.source.FollowNetworkDataSource;

import javax.inject.Inject;

public class FollowRepositoryImpl implements FollowRepository {
    private final FollowNetworkDataSource followNetworkDataSource;

    @Inject
    public FollowRepositoryImpl(FollowNetworkDataSource followNetworkDataSource) {
        this.followNetworkDataSource = followNetworkDataSource;
    }

    @Override
    public LiveData<Result<Boolean>> followUser(int targetUserId) {
        return followNetworkDataSource.followUser(targetUserId);
    }

    @Override
    public LiveData<Result<Boolean>> unfollowUser(int targetUserId) {
        return followNetworkDataSource.unfollowUser(targetUserId);
    }
}
