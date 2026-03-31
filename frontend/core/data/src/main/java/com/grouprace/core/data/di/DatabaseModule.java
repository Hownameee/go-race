package com.grouprace.core.data.di;

import android.content.Context;

import androidx.room.Room;

import com.grouprace.core.data.AppDatabase;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.ActivityDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.PostDao;

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
                "gorace.db"
        ).fallbackToDestructiveMigration().build();
    }

    @Provides
    public RoutePointDao provideRoutePointDao(AppDatabase appDatabase) {
        return appDatabase.routePointDao();
    }

    @Provides
    public RecordDao provideRecordDao(AppDatabase appDatabase) {
        return appDatabase.recordDao();
    }
    
    public ActivityDao provideActivityDao(AppDatabase appDatabase) {
        return appDatabase.activityDao();
    }

    @Provides
    public PostDao providePostDao(AppDatabase appDatabase) {
        return appDatabase.postDao();
    }
}
