package com.grouprace.core.data;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.grouprace.core.data.dao.ActivityDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.model.RoutePoint;

@Database(entities = {RoutePoint.class, Activity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();
    public abstract ActivityDao activityDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS activities ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "title TEXT, "
                    + "startTime INTEGER NOT NULL, "
                    + "endTime INTEGER NOT NULL, "
                    + "distanceKm REAL NOT NULL, "
                    + "elapsedTimeMs INTEGER NOT NULL, "
                    + "paceMinPerKm REAL NOT NULL, "
                    + "completed INTEGER NOT NULL DEFAULT 0)");
            db.execSQL("ALTER TABLE route_points ADD COLUMN activityId INTEGER NOT NULL DEFAULT 0");
        }
    };
}
