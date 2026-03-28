package com.grouprace.core.data.repository;

import com.grouprace.core.data.dao.ActivityDao;
import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.Activity;
import com.grouprace.core.data.model.RoutePoint;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;

public class TrackingRepositoryImpl implements TrackingRepository {

    private static final String TAG = "TrackingRepository";
    private final RoutePointDao routePointDao;
    private final ActivityDao activityDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public TrackingRepositoryImpl(RoutePointDao routePointDao, ActivityDao activityDao) {
        this.routePointDao = routePointDao;
        this.activityDao = activityDao;
    }

    @Override
    public void savePoint(RoutePoint point) {
        executor.execute(() -> routePointDao.insert(point));
    }

    @Override
    public long createActivity(Activity activity) {
        try {
            Future<Long> future = executor.submit(() -> activityDao.insert(activity));
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "Failed to create activity", e);
            return -1;
        }
    }

    @Override
    public void updateActivity(Activity activity) {
        executor.execute(() -> activityDao.update(activity));
    }

    @Override
    public Activity getActivityById(long id) {
        try {
            Future<Activity> future = executor.submit(() -> activityDao.getById(id));
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get activity by id: " + id, e);
            return null;
        }
    }

    @Override
    public List<RoutePoint> getPointsForActivity(long activityId) {
        try {
            Future<List<RoutePoint>> future = executor.submit(() -> routePointDao.getByActivityId(activityId));
            return future.get();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get points for activity: " + activityId, e);
            return Collections.emptyList();
        }
    }
}
