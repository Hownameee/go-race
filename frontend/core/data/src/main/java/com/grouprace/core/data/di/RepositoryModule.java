package com.grouprace.core.data.di;

import android.content.Context;

import com.grouprace.core.data.AppDatabase;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.data.repository.PostRepositoryImpl;
import com.grouprace.core.data.repository.TrackingRepository;
import com.grouprace.core.data.repository.TrackingRepositoryImpl;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Binds
    public abstract TrackingRepository bindTrackingRepository(TrackingRepositoryImpl impl);

    @Binds
    public abstract PostRepository bindPostRepository(PostRepositoryImpl impl);
}
