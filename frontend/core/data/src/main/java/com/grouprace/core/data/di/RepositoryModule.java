package com.grouprace.core.data.di;

import com.grouprace.core.data.repository.AuthRepository;
import com.grouprace.core.data.repository.AuthRepositoryImpl;
import com.grouprace.core.data.repository.NearbyRouteRepository;
import com.grouprace.core.data.repository.NearbyRouteRepositoryImpl;
import com.grouprace.core.data.repository.ClubRepository;
import com.grouprace.core.data.repository.ClubRepositoryImpl;
import com.grouprace.core.data.repository.NotificationRepository;
import com.grouprace.core.data.repository.NotificationRepositoryImpl;
import com.grouprace.core.data.repository.PostRepository;
import com.grouprace.core.data.repository.PostRepositoryImpl;
import com.grouprace.core.data.repository.RecordRepository;
import com.grouprace.core.data.repository.RecordRepositoryImpl;
import com.grouprace.core.data.repository.SearchRepository;
import com.grouprace.core.data.repository.SearchRepositoryImpl;
import com.grouprace.core.data.repository.TrackingRepository;
import com.grouprace.core.data.repository.TrackingRepositoryImpl;
import com.grouprace.core.data.repository.UserRepository;
import com.grouprace.core.data.repository.UserRepositoryImpl;

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
    public abstract RecordRepository bindRecordRepository(RecordRepositoryImpl impl);

    @Binds
    public abstract AuthRepository bindAuthRepository(AuthRepositoryImpl impl);

    @Binds
    public abstract NotificationRepository bindNotificationRepository(NotificationRepositoryImpl impl);

    @Binds
    public abstract SearchRepository bindSearchRepository(SearchRepositoryImpl impl);

    @Binds
    public abstract UserRepository bindUserRepository(UserRepositoryImpl impl);

    @Binds
    public abstract NearbyRouteRepository bindNearbyRouteRepository(NearbyRouteRepositoryImpl impl);
    
    @Binds
    public abstract ClubRepository bindClubRepository(ClubRepositoryImpl impl);
}
