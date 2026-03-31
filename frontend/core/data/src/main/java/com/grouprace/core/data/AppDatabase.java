package com.grouprace.core.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.ActivityDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.model.PostEntity;

@Database(entities = {RoutePoint.class, PostEntity.class, Activity.class, RecordEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();
    public abstract PostDao postDao();
    public abstract RecordDao recordDao();
    public abstract ActivityDao activityDao();
}
