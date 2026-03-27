package com.grouprace.core.data.repository;

import com.grouprace.core.data.dao.RoutePointDao;
import com.grouprace.core.data.model.RoutePoint;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;

public class TrackingRepositoryImpl implements TrackingRepository {

    private final RoutePointDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    public TrackingRepositoryImpl(RoutePointDao dao) {
        this.dao = dao;
    }

    @Override
    public void savePoint(RoutePoint point) {
        executor.execute(() -> dao.insert(point));
    }
}
