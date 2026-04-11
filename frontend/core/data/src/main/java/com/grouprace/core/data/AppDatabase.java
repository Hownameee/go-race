package com.grouprace.core.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.grouprace.core.data.dao.ClubDao;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.ClubEntity;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.model.RoutePoint;

@Database(entities = {RoutePoint.class, PostEntity.class, RecordEntity.class, ClubEntity.class}, version = 10, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();

    public abstract PostDao postDao();

    public abstract RecordDao recordDao();

    public abstract ClubDao clubDao();
}
