package com.grouprace.core.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.grouprace.core.data.dao.RecordDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.UserRouteDao;
import com.grouprace.core.data.model.RecordEntity;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.model.PostEntity;
import com.grouprace.core.data.model.UserRouteEntity;
import com.grouprace.core.data.model.UserRouteWaypointEntity;

@Database(entities = {RoutePoint.class, PostEntity.class, RecordEntity.class,
        UserRouteEntity.class, UserRouteWaypointEntity.class}, version = 12, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();
    public abstract PostDao postDao();
    public abstract RecordDao recordDao();
    public abstract UserRouteDao userRouteDao();

    public static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE record ADD COLUMN pendingSync INTEGER NOT NULL DEFAULT 0");
        }
    };
}
