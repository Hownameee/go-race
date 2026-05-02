package com.grouprace.core.data.di;

import android.content.Context;

import androidx.room.Room;

import com.grouprace.core.data.AppDatabase;
import com.grouprace.core.data.dao.ClubDao;
import com.grouprace.core.data.dao.ProfileDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.dao.ClubAdminDao;
import com.grouprace.core.data.dao.EventDao;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                "gorace.db").fallbackToDestructiveMigration().build();
    }

    @Provides
    public RoutePointDao provideRoutePointDao(AppDatabase appDatabase) {
        return appDatabase.routePointDao();
    }

    @Provides
    public RecordDao provideRecordDao(AppDatabase appDatabase) {
        return appDatabase.recordDao();
    }

    @Provides
    public PostDao providePostDao(AppDatabase appDatabase) {
        return appDatabase.postDao();
    }

    @Provides
    public UserRouteDao provideUserRouteDao(AppDatabase appDatabase) {
        return appDatabase.userRouteDao();
    }

    @Provides
    public ClubDao provideClubDao(AppDatabase appDatabase) {
        return appDatabase.clubDao();
    }

    @Provides
    public ClubAdminDao provideClubAdminDao(AppDatabase appDatabase) {
        return appDatabase.clubAdminDao();
    }

    @Provides
    public EventDao provideEventDao(AppDatabase appDatabase) {
        return appDatabase.eventDao();
    }

    // ===== Profile Feature Section =====
    @Provides
    public ProfileDao provideProfileDao(AppDatabase appDatabase) {
        return appDatabase.profileDao();
    }
}
