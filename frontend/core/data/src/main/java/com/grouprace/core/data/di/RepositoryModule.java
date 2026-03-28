package com.grouprace.core.data.di;

import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.data.repository.AuthRepositoryImpl;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.data.repository.PostRepositoryImpl;
import com.grouprace.core.data.repository.TrackingRepository;
import com.grouprace.core.data.repository.TrackingRepositoryImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    public abstract TrackingRepository bindTrackingRepository(TrackingRepositoryImpl impl);

    @Binds
    public abstract PostRepository bindPostRepository(PostRepositoryImpl impl);

    @Binds
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl impl);
}
