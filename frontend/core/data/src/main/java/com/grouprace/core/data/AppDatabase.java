package com.grouprace.core.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.grouprace.core.data.dao.NotificationDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.model.NotificationEntity;
import com.grouprace.core.data.dao.ClubDao;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.dao.ClubAdminDao;
import com.grouprace.core.data.dao.EventDao;
import com.grouprace.core.data.model.ClubAdminEntity;
import com.grouprace.core.data.model.ClubEntity;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.model.UserRouteEntity;
import com.grouprace.core.data.model.UserRouteWaypointEntity;
import com.grouprace.core.data.model.EventEntity;

@Database(entities = { RoutePoint.class, PostEntity.class, RecordEntity.class,
        UserRouteEntity.class, UserRouteWaypointEntity.class, ClubEntity.class,
        ClubAdminEntity.class, EventEntity.class, NotificationEntity.class}, version = 14, exportSchema = false)
@androidx.room.TypeConverters(com.grouprace.core.data.utils.Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();

    public abstract PostDao postDao();

    public abstract RecordDao recordDao();

    public abstract UserRouteDao userRouteDao();
    
    public abstract NotificationDao notificationDao();

    public abstract ClubDao clubDao();

    public abstract ClubAdminDao clubAdminDao();

    public abstract EventDao eventDao();
}
