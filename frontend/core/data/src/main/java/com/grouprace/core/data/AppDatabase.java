package com.grouprace.core.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.model.RoutePoint;

@Database(entities = {RoutePoint.class, RecordEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();

    public abstract RecordDao recordDao();
}
