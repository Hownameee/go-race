package com.grouprace.core.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.model.PostEntity;

@Database(entities = {RoutePoint.class, PostEntity.class, RecordEntity.class}, version = 9, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();
    public abstract PostDao postDao();
    public abstract RecordDao recordDao();
}
