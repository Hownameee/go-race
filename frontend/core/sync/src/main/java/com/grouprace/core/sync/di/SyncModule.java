package com.grouprace.core.sync.di;

import com.grouprace.core.data.NetworkMonitor;
import com.grouprace.core.data.SyncManager;
import com.grouprace.core.sync.NetworkMonitorImpl;
import com.grouprace.core.sync.SyncManagerImpl;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public abstract class SyncModule {

    @Binds
    @Singleton
    public abstract SyncManager bindSyncManager(SyncManagerImpl impl);

    @Binds
    @Singleton
    public abstract NetworkMonitor bindNetworkMonitor(NetworkMonitorImpl impl);
}
