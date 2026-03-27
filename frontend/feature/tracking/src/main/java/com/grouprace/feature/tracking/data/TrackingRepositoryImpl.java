package com.grouprace.feature.tracking.data;

import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RoutePoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackingRepositoryImpl implements TrackingRepository {

    private final RoutePointDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TrackingRepositoryImpl(RoutePointDao dao) {
        this.dao = dao;
    }

    @Override
    public void savePoint(RoutePoint point) {
        executor.execute(() -> dao.insert(point));
    }
}
