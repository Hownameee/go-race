package com.grouprace.core.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.grouprace.core.data.dao.ClubDao;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.dao.ClubAdminDao;
import com.grouprace.core.data.model.ClubAdminEntity;
import com.grouprace.core.data.model.ClubEntity;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.data.model.UserRouteEntity;
import com.grouprace.core.data.model.UserRouteWaypointEntity;

@Database(entities = { RoutePoint.class, PostEntity.class, RecordEntity.class,
        UserRouteEntity.class, UserRouteWaypointEntity.class, ClubEntity.class,
        ClubAdminEntity.class }, version = 11, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();

    public abstract PostDao postDao();

    public abstract RecordDao recordDao();

    public abstract UserRouteDao userRouteDao();

    public abstract ClubDao clubDao();

    public abstract ClubAdminDao clubAdminDao();
}
