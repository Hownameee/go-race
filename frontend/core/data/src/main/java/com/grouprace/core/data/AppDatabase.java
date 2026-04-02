package com.grouprace.core.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.dao.PostDao;
import com.grouprace.core.data.model.RoutePoint;
import com.grouprace.core.data.model.PostEntity;

@Database(entities = {RoutePoint.class, PostEntity.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoutePointDao routePointDao();
    public abstract PostDao postDao();
}
